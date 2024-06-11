package com.cex.vulcano.controller;

import com.cex.vulcano.externalconnection.dexscreener.DexScreenerTool;
import com.cex.vulcano.externalconnection.dexscreener.model.DSTradePair;
import com.cex.vulcano.externalconnection.dexscreener.model.DSTradePairs;
import com.cex.vulcano.model.TradePair;
import com.cex.vulcano.model.TradePairs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/trade")
public class TradeController {
    @Autowired
    private DexScreenerTool tool;

    public TradeController() {
        this.tool = new DexScreenerTool();
    }

    @GetMapping("/pairs")
    public ResponseEntity<TradePairs> getPairPrices(@RequestParam String ... pairIds) {
        DSTradePairs tradePairs = tool.getPrice(Arrays.asList(pairIds));
        TradePairs response = transform(tradePairs);
        return ResponseEntity.ok(response);
    }

    private TradePairs transform(DSTradePairs tradePairs) {
        List<TradePair> resultPairs = new ArrayList<>();
        for (DSTradePair pair : tradePairs.getPairs()) {
            TradePair resultPair = new TradePair(pair.getPairAddress(), pair.getPriceUsd());
            resultPairs.add(resultPair);
        }
        return new TradePairs(resultPairs);
    }
}