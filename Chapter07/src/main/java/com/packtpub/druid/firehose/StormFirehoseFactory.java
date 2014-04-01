package com.packtpub.druid.firehose;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.metamx.druid.realtime.firehose.Firehose;
import com.metamx.druid.realtime.firehose.FirehoseFactory;

import java.io.IOException;

@JsonTypeName("storm")
public class StormFirehoseFactory implements FirehoseFactory {
    private static final StormFirehose FIREHOSE = new StormFirehose();

    @JsonCreator
    public StormFirehoseFactory() {
    }

    @Override
    public Firehose connect() throws IOException {
        return FIREHOSE;
    }

    public static StormFirehose getFirehose() {
        return FIREHOSE;
    }
}
