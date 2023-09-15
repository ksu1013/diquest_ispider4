package sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.w3c.dom.Element;

public class dqDocTopickerConvert2 {
	
public static void main(String[] args) throws Exception{
	String inputDir = "C:\\Users\\Dell\\Desktop\\dqdoc_test\\";
	String input = inputDir;
	//System.out.println("input>>>>>>>>>"+input);

	File inFile = new File(input);
	
//	InputStream inputStream = new FileInputStream(inFile);
//	Reader reader = new InputStreamReader(inputStream,"UTF-8");
//	InputSource is = new InputSource(reader);
//	is.setEncoding("UTF-8");
	
	
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	
	DocumentBuilder db = factory.newDocumentBuilder();
	
	Document doc =db.parse(input);
	
	Element root = doc.getDocumentElement();				// 첫번재 태그 가져옴(sample)
	System.out.println("root  >> "+root.getNodeName());
	
	Node firstNode = root.getFirstChild();			
//	Node name = firstNode.getNextSibling();					// 다음 depth로 넘어감
	System.out.println("firstNode   >> " + firstNode);		// #text 로 뜨는거는 xml파일의 빈 공백을 노드로 인식하기 때문! (무시해도 됨)
	
	NodeList childList = root.getChildNodes();				// 첫번째 sample태그 아래에 있는 childs 태그들 
	
	for(int i=0; i<childList.getLength(); i++) {
		Node item = childList.item(i);
		if(item.getNodeType() == Node.ELEMENT_NODE) {   // 노드 타입이 공백이 이닌경우 (element인 경우)
			System.out.println("item node name   >> "+item.getNodeName());
			System.out.println("item node value   >> "+item.getTextContent());
		}else {
			System.out.println("공백");
		}
	}
	
	

	
	
	
	
	
}	


}
