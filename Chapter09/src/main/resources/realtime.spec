[{
    "schema": {
        "dataSource": "stockinfo",
        "aggregators": [
            { "type": "count", "name": "orders"},
        	{ "type": "doubleSum", "fieldName": "price", "name": "totalPrice" }
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
        "windowPeriod": "PT30s",
        "segmentGranularity": "minute",
        "basePersistDirectory": "/tmp/example/rand_realtime/basePersist"
    }
}]
