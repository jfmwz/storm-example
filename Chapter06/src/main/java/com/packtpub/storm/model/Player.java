package com.packtpub.storm.model;

public class Player {
    public static String next(String current) {
        if (current.equals("X")) return "O";
        else return "X";
    }
}
