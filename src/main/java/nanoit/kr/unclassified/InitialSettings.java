package nanoit.kr.unclassified;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.PropertyDto;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class InitialSettings {


    public final List<PropertyDto> propertyDtoList;
    private final List<File> configurationFiles;

    public InitialSettings() {
        this.propertyDtoList = new ArrayList<>();
        this.configurationFiles = new ArrayList<>();
    }

    public List<PropertyDto> getPropertyDtoList() {
        return propertyDtoList;
    }


    // config 폴더가 존재하는지 여부와 안에 xml 파일이 있는지 확인 여부
    public boolean isConfigDirectoryExist() throws IOException {
        Path configDirPath = Paths.get(GlobalConstant.CONFIG_FILE_PATH);
        boolean isDirectoryExist = Files.isDirectory(configDirPath);
        boolean isPropertiesExist = Files.list(configDirPath)
                .anyMatch(path -> path.toString().endsWith(GlobalConstant.FILE_FORMAT));
        return isDirectoryExist && isPropertiesExist;
    }

    public boolean isDuplicateFile(File file) {
        String fileName = file.getName();
        return fileName.contains("SMS") && !configurationFiles.contains(file) && fileName.contains(GlobalConstant.FILE_FORMAT);
    }


    // 같아도 되는 값
    // driver ,database 이름, port,id, pwd, email
    // 달라야 할 값
    // dbId
    public boolean isPropertyDtoValid(PropertyDto dto) {
        for (PropertyDto data : propertyDtoList) {
            if (data.getUrl().equals(dto.getUrl()) || data.getDbId().equals(dto.getDbId()) || data.getUserAgent() == dto.getUserAgent()) {
                log.warn("[INITIAL-SETTINGS] Creation of dto failed because xml file with same value exists");
                return false;
            }
        }
        return true;
    }


    public boolean addListDtoAndFile() {
        File[] files = new File(GlobalConstant.CONFIG_FILE_PATH).listFiles();
        PropertyDto dto = new PropertyDto();
        if (files == null) {
            log.warn("[INITIAL-SETTINGS] No configuration files found");
            return false;
        }

        for (File file : files) {
            if (isDuplicateFile(file)) {
                configurationFiles.add(file);
            }
            dto = convert(file);
            if (dto != null && isPropertyDtoValid(dto)) {
                propertyDtoList.add(dto);
            } else {
                log.warn("[INITIAL-SETTINGS] Failed to Dto");
            }
        }
        if (!propertyDtoList.isEmpty() && Arrays.stream(files).count() == configurationFiles.size()) {
            if (!propertyDtoList.contains(dto)) {
                log.info("[INITIAL-SETTINGS] new Dto Detected)");
            }
            log.info("[INITIAL-SETTINGS] Success To add DTO count : {} and FIle count : {}", propertyDtoList.size(), configurationFiles.size());
            return true;
        }
        log.warn("[INITIAL-SETTINGS] Create PropertyDto Failed");
        return false;
    }

    public void makeConfigSettingXmlFile() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();
            Element rootElement = doc.createElement("root");
            doc.appendChild(rootElement);

            // Add entry elements
            String[] keys = {"DBMS", "DB_URL", "DB_NAME", "DB_ID", "DB_PWD", "TCP_URL", "TCP_PORT", "USER_AGENT", "USER_ID", "USER_PWD", "USER_EMAIL"};

            for (String key : keys) {
                Element entry = doc.createElement("entry");
                entry.setAttribute("key", key);
                entry.appendChild(doc.createTextNode("plz insert Data"));
                rootElement.appendChild(entry);
            }

            File file = new File(GlobalConstant.CONFIG_FILE_PATH + "SMS.xml");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    log.info("[INITIAL-SETTINGS] SMS.xml created successfully");
                } catch (IOException e) {
                    throw new Exception("[INITIAL-SETTINGS] Failed to create sms.xml: " + e.getMessage());
                }
            } else {
                log.info("[INITIAL-SETTINGS] SMS.xml created successfully.");
            }

            // Print the XML document
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // 자동 들여쓰기 사용
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); // 들여쓰기 간격 2칸씩 -> 그냥 정렬될 수 있도록 넣어둠
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            System.exit(-1);
        }
    }


    public PropertyDto convert(File file) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("entry");

            PropertyDto propertyDto = new PropertyDto();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String key = element.getAttribute("key");
                    String value = element.getTextContent();
                    if (value == null || value.trim().isEmpty()) {
                        return null;
                    }

                    switch (key) {
                        case "DBMS":
                            propertyDto.setDbms(value);
                            switch (value) {
                                case "POSTGRESQL":
                                    propertyDto.setDbDriver(GlobalConstant.POSTGRESQL_DRIVER);
                                    break;
                                case "MYSQL":
                                    propertyDto.setDbDriver(GlobalConstant.MYSQL_DRIVER);
                                    break;
                                case "ORACLE":
                                    propertyDto.setDbDriver(GlobalConstant.ORACLE_DRIVER);
                                    break;
                                case "MSSQL":
                                    propertyDto.setDbDriver(GlobalConstant.MSSQL_DRIVER);
                                    break;
                            }
                            break;
                        case "DB_URL":
                            propertyDto.setUrl(value);
                            break;

                        case "DB_NAME":
                            propertyDto.setDbName(value);
                            break;
                        case "DB_ID":
                            propertyDto.setDbId(value);
                            break;
                        case "DB_PWD":
                            propertyDto.setDbPwd(value);
                            break;
                        case "TCP_URL":
                            propertyDto.setTcpUrl(value);
                            break;
                        case "TCP_PORT":
                            propertyDto.setPort(Integer.parseInt(value));
                            break;
                        case "USER_AGENT":
                            propertyDto.setUserAgent(Integer.parseInt(value));
                            break;
                        case "USER_ID":
                            propertyDto.setUserId(value);
                            break;
                        case "USER_PWD":
                            propertyDto.setUserPwd(value);
                            break;
                        case "USER_EMAIL":
                            propertyDto.setUserEmail(value);
                            break;
                        default:
                            break;
                    }
                }
            }
            return propertyDto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
