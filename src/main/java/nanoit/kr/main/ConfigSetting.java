package nanoit.kr.main;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.PropertyDto;
import org.apache.ibatis.io.Resources;

import java.io.File;
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

    public void setting() {
        try {
            File[] configureFiles = new File(System.getProperty("user.dir") + "/config").listFiles();

            if (configureFiles != null) {
                for (File file : configureFiles) {
                    // 만약 properties 파일안에 값이 없는 행이 있는 경우 예외 처리
                    if (file.getName().contains("properties")) {
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
            }

        } catch (Exception e) {
            log.error("[CONFIG-SETTING] ERROR DETECTED : {}", e.getMessage());
        }
    }

    public List<PropertyDto> getList() {
        return list;
    }
}
