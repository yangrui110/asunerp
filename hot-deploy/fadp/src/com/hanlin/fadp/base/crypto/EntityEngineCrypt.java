package com.hanlin.fadp.base.crypto;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.hanlin.fadp.base.start.Start;
import com.hanlin.fadp.base.util.FileUtil;
import com.hanlin.fadp.base.util.UtilXml;

public class EntityEngineCrypt {
	private static String entityengineXml = "component://entity/config/entityengine.xml";
	public static boolean reencodeEntityEngine(String newKey) throws MalformedURLException, SAXException, ParserConfigurationException, IOException{
		File engineXml = FileUtil.getFile(entityengineXml);
		Document engine = UtilXml.readXmlDocument(engineXml.toURI().toURL());
		Element rootElement = engine.getDocumentElement();
		List<? extends Element> datasourceElementList = UtilXml.childElementList(rootElement, "datasource");
		for (Element datasourceElement : datasourceElementList) {
			//FIXME:需要加密的话就放开这里的操作,同时将Start内换为与该类同包下的Start.java
//			Element jdbcElement = UtilXml.firstChildElement(datasourceElement, "inline-jdbc");
//			String pass=SimpleEncode.decode(jdbcElement.getAttribute("jdbc-password"), Start.getInstance().pass);
//			jdbcElement.setAttribute("jdbc-password",SimpleEncode.encode(pass, newKey.getBytes()));
		}
		UtilXml.writeXmlDocument(engineXml.getCanonicalPath(), engine);
		return true;
	}
}
