package com.cex.vulcano.model;

public record TradePair (String pairAddress, String name, double priceUsd, double usd_24h_change) {
}
