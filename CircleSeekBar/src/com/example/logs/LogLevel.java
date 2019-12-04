package com.example.logs;

public enum LogLevel {

    V(1),D(2),I(3),W(4),E(5);

    private final int value;
    LogLevel(int value)
    {
        this.value=value;
    }

    public int getValue()
    {
        return this.value;
    }


}
