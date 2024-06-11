package com.cex.vulcano.externalconnection.mobula.model;

import lombok.Data;

@Data
public class MobulaTradePair {
    private String name;
    private double price;
    private double price_change_24h;
}
