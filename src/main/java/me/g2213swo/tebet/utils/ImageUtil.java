package me.g2213swo.tebet.utils;

import me.g2213swo.tebet.integration.OCRAsyncTask;
import me.g2213swo.tebet.integration.OCRImpl;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageUtil {

    private static String ocrResult = null;
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

    public static CompletableFuture<String> processImageMessage(List<Image> images) {
        String apiKey = "K86585908788957";
        boolean isOverlayRequired = false;
        String imageUrl = Image.queryUrl(images.get(0));
        String language = "chs";
        OCRImpl callBack = new OCRImpl();

        OCRAsyncTask ocrAsyncTask = new OCRAsyncTask(apiKey, isOverlayRequired, imageUrl, language, callBack);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                return ocrAsyncTask.call();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }, executorService);

        return future.thenApply(result -> {
            if (result != null) {
                callBack.getOCRCallBackResult(result);
                ocrResult = callBack.getOCRResult();
                if (ocrResult == null) {
                    ocrResult = "图片识别失败";
                }
            }
            executorService.shutdown();
            return ocrResult;
        }).exceptionally(ex -> {
            ex.printStackTrace();
            executorService.shutdown();
            return null;
        });
    }

}
