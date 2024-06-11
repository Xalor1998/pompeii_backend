package com.cex.vulcano.controller;

import com.cex.vulcano.externalconnection.coingecko.CoinGeckoTool;
import com.cex.vulcano.externalconnection.coingecko.model.CGCurrencyInfo;
import com.cex.vulcano.externalconnection.dexscreener.DexScreenerTool;
import com.cex.vulcano.externalconnection.mobula.MobulaTool;
import com.cex.vulcano.externalconnection.mobula.model.MobulaTradePair;
import com.cex.vulcano.externalconnection.mobula.model.MobulaTradePairs;
import com.cex.vulcano.model.TradePair;
import com.cex.vulcano.model.TradePairs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trade")
public class TradeController {

    @Autowired
    private MobulaTool tool;

    @GetMapping("/pairs")
    public ResponseEntity<TradePairs> getPairPrices(@RequestParam String ... pairIds) {
        MobulaTradePairs tradePairs = tool.getPrice(Arrays.asList(pairIds));
        return ResponseEntity.ok(transform(tradePairs));
    }

    private TradePairs transform(MobulaTradePairs tradePairs) {
        List<TradePair> resultPairs = new ArrayList<>();
        for (Map.Entry<String, MobulaTradePair> currency : tradePairs.getData().entrySet()) {
            MobulaTradePair pair = currency.getValue();
            TradePair resultPair = new TradePair(currency.getKey(), pair.getName(), pair.getPrice(), pair.getPrice_change_24h());
            resultPairs.add(resultPair);
        }
        return new TradePairs(resultPairs);
    }
}