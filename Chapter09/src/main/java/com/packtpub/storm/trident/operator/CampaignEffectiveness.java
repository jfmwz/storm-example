package com.packtpub.storm.trident.operator;

import com.esotericsoftware.minlog.Log;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.List;

public class CampaignEffectiveness extends BaseFunction {
    private static final long serialVersionUID = 1L;

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        String campaign = (String) tuple.getValue(0);
        Long impressions_count = (Long) tuple.getValue(1);
        Long click_thru_count = (Long) tuple.getValue(2);
        if (click_thru_count == null)
            click_thru_count = new Long(0);
        double effectiveness = (double) click_thru_count / (double) impressions_count;
        Log.error("[" + campaign + "," + String.valueOf(click_thru_count) + "," + impressions_count + ", " + effectiveness + "]");
        List<Object> values = new ArrayList<Object>();
        values.add(campaign);
        collector.emit(values);
    }
}
