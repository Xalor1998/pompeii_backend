package com.cex.vulcano.externalconnection.mobula.model;

import lombok.Data;

import java.util.Map;

@Data
public class MobulaTradePairs {
    private Map<String, MobulaTradePair> data;
}
