package com.packtpub.storm.trident.operator;

import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Distinct extends BaseFilter {
    private static final long serialVersionUID = 1L;
    private Set<String> distincter = Collections.synchronizedSet(new HashSet<String>());

    @Override
    public boolean isKeep(TridentTuple tuple) {
        String id = this.getId(tuple);
        return distincter.add(id);
    }

    public String getId(TridentTuple t) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < t.size(); i++) {
            sb.append(t.getString(i));
        }
        return sb.toString();
    }
}