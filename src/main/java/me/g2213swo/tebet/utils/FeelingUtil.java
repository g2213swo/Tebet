package me.g2213swo.tebet.utils;

import me.g2213swo.tebet.Feeling;
import me.g2213swo.tebet.Tebet;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FeelingUtil {

    public static final FeelingUtil INSTANCE = new FeelingUtil();

    private static final Path PATH = Tebet.instance.getConfigFolderPath();
    private final Contact contact = Tebet.instance.getTebetBot().getAsFriend();

    private FeelingUtil() {
    }

    private static Image uploadImage(File file) throws IOException {
        try (ExternalResource resource = ExternalResource.create(file)) { // 使用文件 file
            return FeelingUtil.INSTANCE.contact.uploadImage(resource); // 用来上传图片
        }
    }

    //判断是否有指定情感图片，没有就创建一个
    private static boolean checkFolder(Feeling feeling) {
        if (!new File(PATH + "/feeling/" + feeling.name().toLowerCase() + ".png").exists()) {
            //创建一个文件夹并且输出警告
            new File(PATH + "/feeling").mkdirs();
            Tebet.instance.getLogger().warning("未找到" + feeling.name().toLowerCase() + ".png文件，已创建文件夹");
            return false;
        }
        return true;
    }

    public static Image getFeelingImage(Feeling feeling) {
        File file;
        Image image;
        if (!checkFolder(feeling)) {
            return null;
        }

        file = new File(PATH + "/feeling/" + feeling.name().toLowerCase() + ".png");
        try {
            image = uploadImage(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return image;
    }
}
