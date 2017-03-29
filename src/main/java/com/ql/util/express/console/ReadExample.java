package com.ql.util.express.console;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ReadExample {
   public static void main(String[] args) throws Exception {
	   String fileName ="E:\\taobaocode\\QLExpress\\trunk\\example\\simple.ql";
	   InputStream in = new FileInputStream(fileName);
	   readExampleDefine(in);
}
   public static ExampleDefine readExampleDefine(String fileName) throws Exception {
	   InputStream in = new FileInputStream(fileName);
	   return readExampleDefine(in);
   }
   public static ExampleDefine readExampleDefine(InputStream in) throws Exception {
	   DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
	   DocumentBuilder dbd = dbf.newDocumentBuilder();  
	   Document doc = dbd.parse(in);  
	   XPathFactory f = XPathFactory.newInstance();  
	   XPath path = f.newXPath();  	   
	   Node scriptNode= (Node)path.evaluate("example/script", doc,XPathConstants.NODE);
	   String script = scriptNode.getTextContent().trim();
	   Node contextNode= (Node)path.evaluate("example/context", doc,XPathConstants.NODE);
	   String context =  contextNode.getTextContent().trim();
	   return new ExampleDefine(script,context);
}
}
