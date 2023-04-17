package me.g2213swo.tebet.receiver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ReceiverManager {
    private static final ScheduledExecutorService EXECUTOR =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());


    /**
     * 开始接收一个接收器
     * @param receiver 接收器
     */
    public static void startReceiver(Receiver receiver) {
        EXECUTOR.scheduleAtFixedRate(receiver::receive, 0, receiver.getPeriod(), receiver.getUnit());
    }
}
