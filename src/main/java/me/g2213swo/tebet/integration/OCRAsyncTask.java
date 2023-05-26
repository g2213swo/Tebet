package me.g2213swo.tebet.integration;

import net.mamoe.mirai.internal.deps.okhttp3.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class OCRAsyncTask implements Callable<String> {

    private static final String TAG = OCRAsyncTask.class.getName();

    String url = "https://api.ocr.space/parse/image"; // OCR API Endpoints

    private final String mApiKey;
    private final boolean isOverlayRequired;
    private final String mImageUrl;
    private final String mLanguage;
    private final IOCRCallBack mIOCRCallBack;

    public OCRAsyncTask(String apiKey, boolean isOverlayRequired, String imageUrl, String language, IOCRCallBack iOCRCallBack) {
        this.mApiKey = apiKey;
        this.isOverlayRequired = isOverlayRequired;
        this.mImageUrl = imageUrl;
        this.mLanguage = language;
        this.mIOCRCallBack = iOCRCallBack;
    }

    @Override
    public String call() {
        try {
            return sendPost(mApiKey, isOverlayRequired, mImageUrl, mLanguage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String sendPost(String apiKey, boolean isOverlayRequired, String imageUrl, String language) throws Exception {

        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 10808)))
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String postData = "apikey=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8) +
                "&isOverlayRequired=" + URLEncoder.encode(String.valueOf(isOverlayRequired), StandardCharsets.UTF_8) +
                "&url=" + URLEncoder.encode(imageUrl, StandardCharsets.UTF_8) +
                "&language=" + URLEncoder.encode(language, StandardCharsets.UTF_8) +
                "&OCREngine=" + URLEncoder.encode("2", StandardCharsets.UTF_8);

        RequestBody body = RequestBody.create(mediaType, postData);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Accept-Language", "en-US,en;q=0.5")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
