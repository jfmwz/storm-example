package com.packtpub.storm.trident.operator;

import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

public class Filter extends BaseFilter {
    private static final long serialVersionUID = 1L;
    private String fieldName = null;
    private String value = null;

    public Filter(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public boolean isKeep(TridentTuple tuple) {
        String tupleValue = tuple.getStringByField(fieldName);
        if (tupleValue.equals(this.value)) {
            return true;
        }
        return false;
    }
}