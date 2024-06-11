package com.cex.vulcano.communication;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.IOException;
import java.io.InputStream;

public class StreamRequestBody extends RequestBody {
    private final MediaType contentType;
    private final InputStream stream;

    public StreamRequestBody(MediaType contentType, InputStream stream) {
        super();
        this.contentType = contentType;
        this.stream = stream;
    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public long contentLength() {
        try {
            return stream.available();
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(stream);
            sink.writeAll(source);
        } finally {
            Util.closeQuietly(source);
        }
    }
}
