package com.cex.vulcano.externalconnection.dexscreener.model;

public class DSTradePair {

    private String pairAddress;
    private Token baseToken;
    private Token quoteToken;
    private String priceNative;
    private String priceUsd;

    public String getPairAddress() {
        return pairAddress;
    }

    public void setPairAddress(String pairAddress) {
        this.pairAddress = pairAddress;
    }

    public Token getBaseToken() {
        return baseToken;
    }

    public void setBaseToken(Token baseToken) {
        this.baseToken = baseToken;
    }

    public Token getQuoteToken() {
        return quoteToken;
    }

    public void setQuoteToken(Token quoteToken) {
        this.quoteToken = quoteToken;
    }

    public String getPriceNative() {
        return priceNative;
    }

    public void setPriceNative(String priceNative) {
        this.priceNative = priceNative;
    }

    public String getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(String priceUsd) {
        this.priceUsd = priceUsd;
    }
}
