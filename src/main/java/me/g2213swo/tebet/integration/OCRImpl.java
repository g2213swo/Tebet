package me.g2213swo.tebet.integration;

import com.google.gson.Gson;
import me.g2213swo.tebet.Tebet;
import net.mamoe.mirai.utils.MiraiLogger;

public class OCRImpl implements IOCRCallBack {
    private final Gson gson = new Gson();
    private OCRResult ocrResult;

    private final MiraiLogger logger = Tebet.INSTANCE.getLogger();

    @Override
    public void getOCRCallBackResult(String response) {
        ocrResult = gson.fromJson(response, OCRResult.class);
    }

    public String getOCRResult() {
        if (ocrResult == null) {
            logger.warning("OCRResult is null");
            return null;
        }
        if (ocrResult.OCRExitCode != 1) {
            logger.warning("OCRResult.OCRExitCode is not 1");
            return null;
        }
        if (ocrResult.ParsedResults.size() == 0) {
            logger.warning("OCRResult.ParsedResults.size() is 0");
            return null;
        }
        return ocrResult.ParsedResults.get(0).ParsedText;
    }
}
