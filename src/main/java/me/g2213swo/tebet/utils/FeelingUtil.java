package me.g2213swo.tebet.utils;

import me.g2213swo.tebet.Feeling;
import me.g2213swo.tebet.Tebet;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import net.mamoe.mirai.utils.MiraiLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FeelingUtil {

    public static final FeelingUtil INSTANCE = new FeelingUtil();

    private static final Path PATH = Tebet.INSTANCE.getConfigFolderPath();
    private final Contact contact = Tebet.INSTANCE.getTebetBot().getAsFriend();

    private static final MiraiLogger logger = Tebet.INSTANCE.getLogger();

    private static final List<File> imageFiles = new ArrayList<>();

    private FeelingUtil() {
    }

    private static Image uploadImage(File file) {
        try (ExternalResource resource = ExternalResource.create(file)) { // 使用文件 file
            if (file.exists()) {
                return FeelingUtil.INSTANCE.contact.uploadImage(resource);
            } else {
                //创建文件夹
                file.getParentFile().mkdirs();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Image getFeelingImage(Feeling feeling) {
        File imageFileDir = new File(PATH + "/feeling/" + feeling.name().toLowerCase() + "/");
        traverseAndCheckFiles(imageFileDir);
        if (imageFiles.size() == 0) {
            logger.warning(feeling.name() + " 情绪图片文件夹为空");
            //创建文件夹
            imageFileDir.mkdirs();
            return null;
        }
        File imageFile = imageFiles.get((int) (Math.random() * imageFiles.size()));
        imageFiles.clear();
        return uploadImage(imageFile);
    }

    public static void traverseAndCheckFiles(File directory) {
        if (directory == null || !directory.isDirectory()) {
            logger.warning("指定的文件对象为空或不是目录");
            return;
        }
        File[] files = directory.listFiles(); // 获取目录下的所有文件和目录
        if (files == null || files.length == 0) {
            logger.warning("目录为空");
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                // 如果是目录，则递归遍历
                traverseAndCheckFiles(file);
            } else {
                // 如果是文件，则判断文件类型
                if (isPngOrGifFile(file)) {
                    //debug
                    //logger.info(file.getName() + " 是 PNG 或 GIF 文件");
                    imageFiles.add(file);
                } else {
                    logger.warning(file.getName() + " 不是 PNG 或 GIF 文件");
                }
            }
        }
    }

    public static boolean isPngOrGifFile(File file) {
        // 判断文件扩展名是否为 .png 或 .gif
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        return fileExtension.equalsIgnoreCase(".png") || fileExtension.equalsIgnoreCase(".gif");
    }
}
