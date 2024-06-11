package com.cex.vulcano.communication;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    PATCH,
    TRACE;

    @Override
    public String toString() {
        return name();
    }
}
