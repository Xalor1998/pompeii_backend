package com.cex.vulcano.externalconnection.coingecko;

import com.cex.vulcano.communication.HttpResponse;
import com.cex.vulcano.communication.RestClient;
import com.cex.vulcano.exception.AuthorizationException;
import com.cex.vulcano.exception.TransformerException;
import com.cex.vulcano.externalconnection.IntegrationCommunicator;
import com.cex.vulcano.parse.JsonProcessor;

import java.io.IOException;
import java.lang.reflect.Type;

public class CoinGeckoCommunicator extends IntegrationCommunicator {
    private static final String BASE_URL = "https://api.coingecko.com/api/v3/";
    private RestClient client;

    public <T> T getObject(String path, Class<T> type) throws IOException, AuthorizationException, TransformerException {
        String url = fullUrl(path);
        HttpResponse<String> response;
        response = client()
                .get()
                .url(url)
                .sendAndReceive();
        checkResponse(url, response);
        return parseResponse(response, type);
    }

    public <T> T getObject(String path, Type type) throws IOException, AuthorizationException, TransformerException {
        String url = fullUrl(path);
        HttpResponse<String> response;
        response = client()
                .get()
                .url(url)
                .sendAndReceive();
        System.out.println(response.getCode() + ": " + response.getObject());
        checkResponse(url, response);
        return parseResponse(response, type);
    }

    private <T> T parseResponse(HttpResponse<String> response, Type type) throws TransformerException {
        T value = parseJson(response.getObject(), type);
        return value;
    }

    private <T> T parseJson(String json, Type type) throws TransformerException {
        return json().fromJson(json, type);
    }

    private JsonProcessor json() {
        return client().getJson();
    }


    private String fullUrl(String path) {
        if (path.contains("://")) {
            return path;
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return BASE_URL + path;
    }

    private RestClient client() {
        if (client == null) {
            client = new RestClient();
        }
        return client;
    }
}
