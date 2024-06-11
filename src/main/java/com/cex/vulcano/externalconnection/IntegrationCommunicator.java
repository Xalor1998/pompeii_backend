package com.cex.vulcano.externalconnection;


import com.cex.vulcano.communication.HttpResponse;
import com.cex.vulcano.exception.AuthorizationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public abstract class IntegrationCommunicator {

    protected void checkResponse(URL url, HttpResponse<?> response) throws FileNotFoundException, IOException, AuthorizationException {
        checkResponse(url == null ? null : url.toString(), response);
    }

    protected void checkResponse(String url, HttpResponse<?> response) throws FileNotFoundException, IOException, AuthorizationException {
        if (response.getCode() >= 400) {
            switch (response.getCode()) {
                case 401:
                case 402:
                case 407:
                    throw new AuthorizationException();
                case 403:
                case 404:
                case 410:
                case 415:
                case 423:
                    if (url != null) {
                        throw new FileNotFoundException(url);
                    } else {
                        throw new FileNotFoundException();
                    }
                default:
                    throw new IOException("Response code: " + response.getCode());
            }
        }
    }

}
