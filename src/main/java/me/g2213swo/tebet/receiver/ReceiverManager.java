package me.g2213swo.tebet.receiver;

import java.util.Timer;
import java.util.TimerTask;

public class ReceiverManager {
    private static final Timer timer = new Timer();


    /**
     * 开始接收一个接收器
     * @param receiver 接收器
     */
    public static void startReceiver(Receiver receiver) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                receiver.receive();
            }
        }, 0, 1000 * 10);
    }
}
