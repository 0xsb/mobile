package cmcc.mobile.admin.util;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLParseUtil {

	public static Document loadXML(String xml) {
		try {
			DocumentBuilderFactory fctr = DocumentBuilderFactory.newInstance();
			DocumentBuilder bldr = fctr.newDocumentBuilder();
			InputSource insrc = new InputSource(new StringReader(xml));
			return bldr.parse(insrc);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new RuntimeException("解析xml失败:", e);
		}
	}

	public static void main(String[] args) {
		Document document = loadXML("<response><status>status1</status><uid>uid2</uid><employeeNumber>employeeNumber3</employeeNumber><message>4errorcode</message></response>");
		Element response = document.getDocumentElement();
		String textContent = response.getChildNodes().item(0).getTextContent();
		response.getElementsByTagName("status").item(0).getTextContent();
		System.out.println(textContent);
		
	}
}
