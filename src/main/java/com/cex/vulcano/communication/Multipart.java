package com.cex.vulcano.communication;

import java.io.File;

public class Multipart {
    private String mimeType;

    private String name;

    private Object body;

    public Multipart(String mimeType, String name, Object body) {
        super();
        this.mimeType = mimeType;
        this.name = name;
        this.body = body;
    }

    public Multipart(String name, Object body) {
        this(null, name, body);
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getName() {
        return name;
    }

    public Object getBody() {
        return body;
    }

    public String getFileName() {
        if (body instanceof File) {
            return ((File)body).getName();
        }
        return null;
    }
}
