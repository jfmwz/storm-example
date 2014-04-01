package com.packtpub.storm.model;

import java.io.Serializable;

public class FixMessageDto implements Serializable {
    private static final long serialVersionUID = 1L;
    public String symbol;
    public String uid;
    public String msgType;
    public Double price;

    public String toString() {
        return "[" + symbol + "] @ [" + price + "] for [" + msgType + "]";
    }
}
