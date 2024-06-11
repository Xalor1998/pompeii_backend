package com.cex.vulcano.communication;

import java.util.List;
import java.util.Map;

public class HttpResponse<T> {
    private T object;
    private int code;
    private Map<String, List<String>> header;

    public void setObject(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setHeader(Map<String, List<String>> header) {
        this.header = header;
    }

    public Map<String, List<String>> getHeader() {
        return header;
    }

    public String getHeader(String name) {
        if (header != null) {
            List<String> values = header.get(name);
            if (values == null) {
                for (Map.Entry<String, List<String>> entry : header.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase(name)) {
                        values = entry.getValue();
                        break;
                    }
                }
            }
            if (values != null && !values.isEmpty()) {
                return values.get(0);
            }
        }
        return null;
    }
}
