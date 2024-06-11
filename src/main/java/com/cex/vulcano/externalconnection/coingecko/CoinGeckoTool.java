package com.cex.vulcano.externalconnection.coingecko;

import com.cex.vulcano.exception.AuthorizationException;
import com.cex.vulcano.exception.TransformerException;
import com.cex.vulcano.externalconnection.coingecko.model.CGCurrencyInfo;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoinGeckoTool {
    public static final String SOLANA_CHAIN = "solana";
    public static final String USD_CURRENCY = "usd";
    private CoinGeckoCommunicator communicator;

    public Map<String, CGCurrencyInfo> getPrice(List<String> pairs) {
        Map<String, CGCurrencyInfo> response = new HashMap<>();
        for (String pair : pairs) {
            try {
                String path = String.format("simple/token_price/%s?contract_addresses=%s&vs_currencies=%s&include_24hr_change=true"
                        , SOLANA_CHAIN, pair, USD_CURRENCY);
                Type type = new TypeToken<Map<String, CGCurrencyInfo>>() {}.getType();
                response.putAll(communicator().getObject(path, type));
            } catch (IOException | AuthorizationException | TransformerException e) {
                throw new RuntimeException(e); //TODO: exception handling
            }
        }
        return response;
    }

    private CoinGeckoCommunicator communicator() {
        if (communicator == null) {
            communicator = new CoinGeckoCommunicator();
        }
        return communicator;
    }
}
