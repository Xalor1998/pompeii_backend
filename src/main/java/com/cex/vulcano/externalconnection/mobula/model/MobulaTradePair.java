package com.cex.vulcano.externalconnection.mobula.model;

import lombok.Data;

@Data
public class MobulaTradePair {
    private String symbol;
    private double price;
    private double price_change_24h;
}
