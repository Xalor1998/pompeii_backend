package com.cex.vulcano.communication;

import com.cex.vulcano.parse.JsonProcessor;
import com.cex.vulcano.utils.IOUtils;
import com.cex.vulcano.utils.URLUtils;
import okhttp3.*;
import okio.Buffer;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static okhttp3.internal.Util.EMPTY_REQUEST;

public class RestClient {

    protected final static String HEADER_AUTHORIZATION = "Authorization";
    protected final static String HEADER_ACCEPT = "Accept";


    private static ThreadLocal<Long> lastApiCallTime = new ThreadLocal<>();

    private OkHttpClient client;

    private Request.Builder builder = new Request.Builder();
    private CookieJar cookies;
    private String url;
    private String baseUrl;
    private HttpMethod method = HttpMethod.GET;
    private String mimeType = "text/plain";
    private Charset charset = StandardCharsets.UTF_8;
    private Object content;
    private List<Multipart> multiparts;
    private boolean noCertificateValidation = false;
    private long timeoutMs = -1L;
    private boolean authorizationChanged;
    private Consumer<byte[]> bodyConsumer;


    private final JsonProcessor json;

    public RestClient() {
        this(new JsonProcessor());
    }

    public RestClient(JsonProcessor json) {
        accept("application/json");
        mimeType("application/json");
        this.json = json;
    }

    public RestClient withoutCertificateValidation() {
        closeClient(false);
        noCertificateValidation = true;
        return this;
    }

    public RestClient timeout(long timeoutMs) {
        closeClient(false);
        this.timeoutMs = timeoutMs;
        return this;
    }



    public RestClient url(String url) {
        this.url = url;
        return this;
    }


    public RestClient url(URL url) {
        this.url = url.toString();
        return this;
    }


    public RestClient urlBase(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }


    public RestClient method(HttpMethod method) {
        this.method = method;
        return this;
    }


    public RestClient get() {
        this.method(HttpMethod.GET);
        return this;
    }


    public RestClient post() {
        this.method(HttpMethod.POST);
        return this;
    }


    public RestClient put() {
        this.method(HttpMethod.PUT);
        return this;
    }


    public RestClient delete() {
        this.method(HttpMethod.DELETE);
        return this;
    }


    public RestClient patch() {
        this.method(HttpMethod.PATCH);
        return this;
    }

    public RestClient mimeType(String mimeType) {
        this.mimeType = mimeType;
        this.multiparts = null;
        return this;
    }


    public RestClient charset(Charset charset) {
        this.charset = charset;
        return this;
    }


    public RestClient charset(String charsetName) {
        return charset(charsetName == null ? null : Charset.forName(charsetName));
    }

    public RestClient bodyConsumer(Consumer<byte[]> consumer) {
        this.bodyConsumer = consumer;
        return this;
    }


    public RestClient body(Object object) {
        if (object == null) {
            // do nothing
        } else if (object instanceof String) {
            // do nothing
        } else if (isSpecialBody(object)) {
            // do nothing
        } else {
            object = json.toJson(object);
        }
        this.multiparts = null;
        if (object == null) {
            this.content = null;
        } else if (isSpecialBody(object)) {
            this.content = object;
        } else {
            this.content = object.toString();
        }
        return this;
    }


    public <T> RestClient multipart(String mimeType, String name, T body) {
        this.content = null;
        this.mimeType = null;
        if (this.multiparts == null) {
            this.multiparts = new ArrayList<>();
        }
        this.multiparts.add(new Multipart(mimeType, name, body));
        return this;
    }


    public <T> RestClient multipart(String name, T body) {
        return multipart(null, name, body);
    }


    public RestClient header(String name, String value) {
        builder = builder.header(name, value);
        return this;
    }


    public RestClient accept(String mimeType) {
        return header(HEADER_ACCEPT, mimeType);
    }


    public RestClient authorization(String authorization) {
        authorizationChanged = true;
        return header(HEADER_AUTHORIZATION, authorization);
    }


    public RestClient basicAuthorization(String username, String password) {
        String authString = username + ":" + password;
        byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
        String authStringEnc = new String(authEncBytes);
        authorization("Basic " + authStringEnc);
        return this;
    }


    public RestClient bearerAuthorization(String token) {
        return authorization("Bearer " + token);
    }

    public HttpResponse<String> sendAndReceive() throws IOException, IllegalArgumentException {
        HttpResponse<String> result = new HttpResponse<>();
        String responseContent = null;
        if (baseUrl != null) {
            url = URLUtils.mergeURL(url, baseUrl);
        }
        try (Response response = sendAndReceiveResponse()) {
            result.setCode(response.code());
            result.setHeader(response.headers().toMultimap());
            responseContent = response.body().string();
        }
        if (responseContent != null) {
            result.setObject(responseContent);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> HttpResponse<T> sendAndReceive(Class<T> responseType) throws IOException, IllegalArgumentException {
        if (responseType.equals(InputStream.class)) {
            return (HttpResponse<T>) sendAndReceiveStream();
        } else if (responseType.equals(byte[].class)) {
            HttpResponse<InputStream> response = sendAndReceiveStream();
            byte[] bytes = null;
            if (response.getObject() != null) {
                bytes = IOUtils.toByteArray(response.getObject());
            }
            HttpResponse<T> result = new HttpResponse<>();
            result.setCode(response.getCode());
            result.setObject((T)bytes);
            result.setHeader(response.getHeader());
            return result;
        } else {
            HttpResponse<String> stringResponse = sendAndReceive();
            if (responseType.equals(String.class)) {
                return (HttpResponse<T>)stringResponse;
            } else {
                HttpResponse<T> result = new HttpResponse<>();
                result.setCode(stringResponse.getCode());
                T responseObject = parse(stringResponse.getObject(), responseType);
                result.setObject(responseObject);
                return result;
            }
        }
    }


    public <T> HttpResponse<List<T>> sendAndReceiveList(Class<T> itemType) throws IOException, IllegalArgumentException {
        HttpResponse<String> stringResponse = sendAndReceive();
        HttpResponse<List<T>> result = new HttpResponse<>();
        result.setCode(stringResponse.getCode());
        List<T> responseObject = parseList(stringResponse.getObject(), itemType);
        result.setObject(responseObject);
        return result;
    }

    public HttpResponse<InputStream> sendAndReceiveStream() throws IOException, IllegalArgumentException {
        HttpResponse<InputStream> result = new HttpResponse<>();
        InputStream responseContent = null;
        try (Response response = sendAndReceiveResponse()) {
            result.setCode(response.code());
            result.setHeader(response.headers().toMultimap());
//            logResponseProperties(result);
            byte[] bytes = response.body().bytes();
            responseContent = new ByteArrayInputStream(bytes);
        }
        result.setObject(responseContent);
        return result;
    }

    private Response sendAndReceiveResponse() throws IOException {
        RequestBody body;
        if (multiparts != null) {
            body = multipartBody(multiparts);
        } else if (content == null) {
            if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
                body = EMPTY_REQUEST;
            } else {
                body = null;
            }
        } else {
            body = requestBody(getContentType(), content);
        }
        body = consumeBody(body);
        Request request = builder
                .url(url)
                .method(method.name(), body)
                .build();
        if (authorizationChanged) {
            clearCookies(request);
            authorizationChanged = false;
        }
        OkHttpClient client = null;
        try {
            client = client();
            return client.newCall(request).execute();
        } finally {
            lastApiCallTime.set(System.currentTimeMillis());
            body(null);
        }
    }

    private RequestBody requestBody(String mimeType, Object content) {
        MediaType mediaType = mimeType == null ? null : MediaType.get(mimeType);
        if (content instanceof String) {
            return RequestBody.create(mediaType, (String)content);
        } else if (content instanceof InputStream) {
            return new StreamRequestBody(mediaType, (InputStream)content);
        } else if (content instanceof byte[]) {
            return RequestBody.create(mediaType, (byte[])content);
        } else if (content instanceof File) {
            return RequestBody.create(mediaType, (File)content);
        } else {
            throw new IllegalArgumentException(String.format("Unknown content: %s.", content.toString()));
        }
    }

    private RequestBody multipartBody(List<Multipart> parts) {
        MultipartBody.Builder builder = createMultipartBuilder();
        for (Multipart part : parts) {
            RequestBody body = requestBody(part.getMimeType(), part.getBody());
            builder.addFormDataPart(part.getName(), part.getFileName(), body);
        }
        return builder.build();
    }

    private RequestBody consumeBody(RequestBody body) throws IOException {
        if (bodyConsumer != null) {
            if (body == null) {
                bodyConsumer.accept(null);
            } else if (body instanceof MultipartBody) {
                MultipartBody multipart = (MultipartBody)body;
                MultipartBody.Builder builder = createMultipartBuilder();
                for (MultipartBody.Part part : multipart.parts()) {
                    RequestBody partBody = part.body();
                    partBody = consumeBody(partBody);
                    builder.addPart(part.headers(), partBody);
                }
                body = builder.build();
            } else {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                buffer.flush();
                byte[] bytes = buffer.readByteArray();
                bodyConsumer.accept(bytes);
                if (body instanceof StreamRequestBody) {
                    // we need to recreate the body because we used the stream
                    body = new StreamRequestBody(body.contentType(), new ByteArrayInputStream(bytes));
                }
            }
        }
        return body;
    }private String contentToString(Object content) {
        if (content == null) {
            return null;
        } else if (content instanceof File) {
            return ((File)content).getAbsolutePath();
        } else if (content instanceof byte[]) {
            byte[] bytes = (byte[])content;
            return String.format("[%d]", bytes.length);
        } else if (content instanceof String) {
            return content.toString();
        } else {
            return null;
        }
    }

    private OkHttpClient client() {
        if (client == null) {
            OkHttpClient.Builder bldr = new OkHttpClient.Builder();
            if (noCertificateValidation) {
                final TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };
                SSLSocketFactory sslSocketFactory;
                try {
                    SSLContext sslContext = SSLContext.getInstance("SSL");
                    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                    sslSocketFactory = sslContext.getSocketFactory();
                } catch (Exception ex) {
                    throw new IllegalStateException("Cannot turn off SSL certificate validation", ex);
                }
                bldr.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
                bldr.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }
            if (timeoutMs > 0) {
                bldr
                        .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                        .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                        .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS);
            }
            bldr.cookieJar(getCookies());
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            bldr.dispatcher(new Dispatcher(executorService));
            client = bldr.build();
        }
        return client;
    }

    private CookieJar getCookies() {
        if (cookies == null) {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            cookies = CookieJar.NO_COOKIES; //TODO: check if no cookie is reasonable
        }
        return cookies;
    }

    private void clearCookies(Request request) {
        if (cookies != null) {
            cookies.saveFromResponse(request.url(), Collections.emptyList());
        }
    }

    private String getContentType() {
        if (charset == null) {
            return mimeType;
        } else {
            return String.format("%s; charset=%s", mimeType, charset.name());
        }
    }

    public JsonProcessor getJson() {
        return json;
    }

    private boolean isSpecialBody(Object body) {
        if (body != null) {
            if (body instanceof File) {
                return true;
            } else if (body instanceof byte[]) {
                return true;
            } else if (body instanceof InputStream) {
                return true;
            }
        }
        return false;
    }

    private MultipartBody.Builder createMultipartBuilder() {
        return new MultipartBody.Builder().setType(MultipartBody.FORM);
    }

    private void closeClient(boolean log) {
        if (client != null) {
            client.dispatcher().cancelAll();
            List<Runnable> running = client.dispatcher().executorService().shutdownNow();
            client.connectionPool().evictAll();
            client = null;
        }
    }

    private <T> T parse(String value, Class<T> type) {
        try {
            return json.fromJson(value, type);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Response is not valid JSON", ex);
        }
    }

    private <T> List<T> parseList(String value, Class<T> type) {
        try {
            return json.fromJsonToList(value, type);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Response is not valid JSON", ex);
        }
    }
}
