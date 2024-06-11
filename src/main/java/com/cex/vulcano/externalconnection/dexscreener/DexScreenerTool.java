package com.cex.vulcano.externalconnection.dexscreener;

import com.cex.vulcano.exception.AuthorizationException;
import com.cex.vulcano.exception.TransformerException;
import com.cex.vulcano.externalconnection.dexscreener.model.DSTradePairs;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
public class DexScreenerTool {

    public static final String SOLANA_CHAIN = "solana";
    private DexScreenerCommunicator communicator;

    public DexScreenerTool() {

    }

    public DSTradePairs getPrice(List<String> pairs) {
        String path = String.format("pairs/%s/%s", SOLANA_CHAIN, String.join(",", pairs));
        DSTradePairs response = null;
        try {
            response = communicator().getObject(path, DSTradePairs.class);
        } catch (IOException | AuthorizationException | TransformerException e) {
            throw new RuntimeException(e); //TODO: exception handling
        }
        return response;
    }

    private DexScreenerCommunicator communicator() {
        if (communicator == null) {
            communicator = new DexScreenerCommunicator();
        }
        return communicator;
    }
}
