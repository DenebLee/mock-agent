package nanoit.kr.main;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.PropertyDto;
import org.apache.ibatis.io.Resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ConfigSetting {

    private final Properties properties;
    private final List<PropertyDto> list;

    public ConfigSetting() {
        this.list = new ArrayList<>();
        this.properties = new Properties();
    }

    private void loadProperties() {
        try {
            File[] configureFiles = new File(System.getProperty("user.dir") + "/config").listFiles();

            if (configureFiles != null) {
                for (File file : configureFiles) {
                    if (file.getName().contains("properties")) {
                        Properties properties = new Properties();
                        try (InputStream propertiesStream = Resources.getResourceAsStream(file.getName())) {
                            if (propertiesStream == null) {
                                log.error("[CONFIG-SETTING] Failed to load properties file {}", file.getName());
                                continue;
                            }
                            properties.load(propertiesStream);
                        } catch (IOException e) {
                            log.error("[CONFIG-SETTING] Failed to load properties file {}", file.getName(), e);
                            continue;
                        }
                        if (validateProperties(properties)) {
                            PropertyDto dto = new PropertyDto();
                            dto.setDbDriver(properties.getProperty("driver"))
                                    .setUrl(properties.getProperty("url"))
                                    .setDbUsername(properties.getProperty("username"))
                                    .setDbPwd(properties.getProperty("password"))
                                    .setTcpUrl(properties.getProperty("tcp.url"))
                                    .setPort(Integer.parseInt(properties.getProperty("tcp.port")))
                                    .setUserAgent(Integer.parseInt(properties.getProperty("user.agent")))
                                    .setUserId(properties.getProperty("user.name"))
                                    .setUserPwd(properties.getProperty("user.password"))
                                    .setUserEmail(properties.getProperty("user.email"))
                                    .setDatabase(properties.getProperty("user.database"));

                            if (!list.contains(dto)) {
                                list.add(dto);
                            }
                        } else {
                            log.error("[CONFIG-SETTING] The File contain Null data");
                        }
                    }
                }
            } else {
                log.error("[CONFIG-SETTING] File does not exist");
            }
        } catch (Exception e) {
            log.error("[CONFIG-SETTING] ERROR DETECTED : {}", e.getMessage());
        }
    }

    public List<PropertyDto> getList() {
        loadProperties();
        return list;
    }

    private boolean validateProperties(Properties prop) {
        if (properties.getProperty("driver") == null || properties.getProperty("driver").contains(" ")) {
            return false;
        }
        if (properties.getProperty("url") == null || properties.getProperty("url").contains(" ")) {
            return false;
        }
        if (properties.getProperty("username") == null || properties.getProperty("username").contains(" ")) {
            return false;
        }
        if (properties.getProperty("password") == null || properties.getProperty("password").contains(" ")) {
            return false;
        }
        if (properties.getProperty("tcp.url") == null || properties.getProperty("tcp.url").contains(" ")) {
            return false;
        }
        if (properties.getProperty("tcp.port") == null || properties.getProperty("tcp.port").contains(" ")) {
            return false;
        }
        if (properties.getProperty("user.agent") == null || properties.getProperty("user.agent").contains(" ")) {
            return false;
        }
        if (properties.getProperty("user.name") == null || properties.getProperty("user.name").contains(" ")) {
            return false;
        }
        if (properties.getProperty("user.password") == null || properties.getProperty("user.user.password").contains(" ")) {
            return false;
        }
        return properties.getProperty("user.database") != null || !properties.getProperty("user.database").contains(" ");
    }
}
