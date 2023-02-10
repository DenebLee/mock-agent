package nanoit.kr.main;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.PropertyDto;
import org.apache.ibatis.io.Resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ConfigSetting {

    private File[] configureFiles;
    private Properties properties;
    private final List<PropertyDto> list;

    public ConfigSetting() {
        configureFiles = new File(System.getProperty("user.dir") + "/config").listFiles();
        System.out.println(Arrays.toString(configureFiles));
        this.list = new ArrayList<>();
    }

    public List<PropertyDto> setting() throws IOException {
        try {
            configureFiles = new File(System.getProperty("user.dir") + "/config").listFiles();
            System.out.println(Arrays.toString(configureFiles));

            for (File file : configureFiles) {
                if (file.getName().contains("properties")) {
                    System.out.println(file.getName());
                    InputStream propertiesStream = Resources.getResourceAsStream(file.getName());
                    properties.load(propertiesStream);
                    PropertyDto dto = new PropertyDto();

                    dto
                            .setDbDriver(properties.getProperty("driver"))
                            .setUrl(properties.getProperty("url"))
                            .setDbUsername(properties.getProperty("username"))
                            .setDbPwd(properties.getProperty("password"))
                            .setMapper(properties.getProperty("mapper"))
                            .setTcpUrl(properties.getProperty("tcp.url"))
                            .setPort(Integer.parseInt(properties.getProperty("tcp.port")))
                            .setUserAgent(Integer.parseInt(properties.getProperty("user.agent")))
                            .setUserId(properties.getProperty("user.name"))
                            .setUserPwd(properties.getProperty("user.password"))
                            .setUserEmail(properties.getProperty("user.email"))
                            .setDatabase(properties.getProperty("user.database"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            log.error("[CONFIG-SETTING] ERROR DETECTED : {}", e.getMessage());
            System.exit(-1);
        }
        return list;
    }
}
