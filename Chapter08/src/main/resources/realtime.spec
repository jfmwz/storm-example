[{
    "schema": {
        "dataSource": "nlp",
        "aggregators": [
            { "type": "count", "name": "wordcount" },
            { "type": "max", "fieldName": "baseline", "name" : "maxbaseline" }
        ],
        "indexGranularity": "minute",
        "shardSpec": {"type": "none"}
    },

    "config": {
        "maxRowsInMemory": 50000,
        "intermediatePersistPeriod": "PT30s"
    },

    "firehose": {
        "type": "storm",
        "sleepUsec": 100000,
        "maxGeneratedRows": 5000000,
        "seed": 0,
        "nTokens": 255,
        "nPerSleep": 3
    },

    "plumber": {
        "type": "realtime",
        "windowPeriod": "PT10s",
        "segmentGranularity": "minute",
        "basePersistDirectory": "/tmp/nlp/basePersist"
    }
}]
