package com.cex.vulcano.externalconnection.mobula;

import com.cex.vulcano.exception.AuthorizationException;
import com.cex.vulcano.exception.TransformerException;
import com.cex.vulcano.externalconnection.mobula.model.MobulaTradePairs;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


@Service
public class MobulaTool {

    public static final String SOLANA_CHAIN = "solana";
    public static final String SOLANA_SYMBOL = "SOL";
    private MobulaCommunicator communicator;

    public MobulaTradePairs getPrice(List<String> pairs) {
        String path = String.format("market/multi-data?blockchains=%s&assets=%s", SOLANA_CHAIN, String.join(",", pairs));
        MobulaTradePairs response = null;
        try {
            response = communicator().getObject(path, MobulaTradePairs.class);
        } catch (IOException | AuthorizationException | TransformerException e) {
            throw new RuntimeException(e); //TODO: exception handling
        }
        return response;
    }

    private MobulaCommunicator communicator() {
        if (communicator == null) {
            communicator = new MobulaCommunicator();
        }
        return communicator;
    }
}
