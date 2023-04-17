package me.g2213swo.tebet.receiver;


import java.util.concurrent.TimeUnit;

public interface Receiver {

    void receive();

    TimeUnit getUnit();

    long getPeriod();

}
