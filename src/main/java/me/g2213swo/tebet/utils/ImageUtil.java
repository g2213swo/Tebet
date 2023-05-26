package me.g2213swo.tebet.utils;

import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;

public class ImageUtil {
    /**
     * 获取消息中的图片
     * @param messageChain 消息链
     * @return 图片列表
     */
    public static List<Image> getImages(MessageChain messageChain) {
        List<Image> images = new ArrayList<>();

        // 判断是否含有图片
        for (Message message1 : messageChain) {
            if (message1 instanceof Image) {
                Image image = (Image) message1;
                images.add(image);
            }
        }
        return images;

    }

}
