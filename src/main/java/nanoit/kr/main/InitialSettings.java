package nanoit.kr.main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class InitialSettings {

    public void makeXml() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();
            Element root = doc.createElement("root");
        } catch (Exception e) {
            e.getMessage();
        }
    }

}
