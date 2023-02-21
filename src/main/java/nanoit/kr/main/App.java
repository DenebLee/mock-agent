package nanoit.kr.main;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.queue.InternalQueueImpl;
import nanoit.kr.module.Filter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.UUID;


@Slf4j
public class App {
    private static final String CONFIG_DIR_PATH = System.getProperty("user.dir") + "/config";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");

    public static void main(String[] args) {
        try {
            // config 파일에 properties 파일이 있는지 체크
            if (!isConfigDirectoryExist()) {
                log.error("[SYSTEM] Property file does not exist in config file");
                System.exit(-1);
            }


            InternalQueueImpl internalQueueImpl = new InternalQueueImpl();
            new Filter(getRandomUuid(), internalQueueImpl);
//            new Insert(getRandomUuid(), internalQueue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getRandomUuid() {
        return UUID.randomUUID().toString();
    }

    private static boolean isConfigDirectoryExist() throws IOException {
        Path configDirPath = Paths.get(CONFIG_DIR_PATH);
        boolean isDirectoryExist = Files.isDirectory(configDirPath);
        boolean isPropertiesExist = Files.list(configDirPath)
                .anyMatch(path -> path.toString().endsWith(".xml"));

        return isDirectoryExist && isPropertiesExist;
    }
}