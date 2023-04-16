package me.g2213swo.tebet.receiver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ReceiverManager {
    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);


    /**
     * 开始接收一个接收器
     * @param receiver 接收器
     */
    public static void startReceiver(Receiver receiver) {
        EXECUTOR.schedule(receiver::receive, receiver.getDelay(), receiver.getUnit());
    }
}
