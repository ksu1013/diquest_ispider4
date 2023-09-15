package test;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import extension.CommonUtil;
import extension.ConnectionUtil;

/**
 * @title 소프테크 비공식 수집개발
 * @author 강대범 수집 사이트 : https://www.clingendael.org/publications 테스트
 */
public class OpinionHuanqiuExtensionTest {

	private StringBuffer tagList = new StringBuffer();
	private ConnectionUtil connectionUtil = new ConnectionUtil();
	private CommonUtil commonUtil;


	private final String url0 = "https://opinion.huanqiu.com/api/list?node=%22/e3pmub6h5/e3pmub75a%22,%22/e3pmub6h5/e3pn00if8%22,%22/e3pmub6h5/e3pn03vit%22,%22/e3pmub6h5/e3pn4bi4t%22,%22/e3pmub6h5/e3pr9baf6%22,%22/e3pmub6h5/e3prafm0g%22,%22/e3pmub6h5/e3prcgifj%22,%22/e3pmub6h5/e81curi71%22,%22/e3pmub6h5/e81cv14rf%22,%22/e3pmub6h5/e81cv14rf/e81cv52ha%22,%22/e3pmub6h5/e81cv14rf/e81cv7hp0%22,%22/e3pmub6h5/e81cv14rf/e81cvaa3q%22,%22/e3pmub6h5/e81cv14rf/e81cvcd7e%22&offset=0&limit=24";
	private final String url1 = "https://opinion.huanqiu.com/article/4DeMKO7zcRc";

	public static void main(String[] args) throws IOException {
		CommonUtil commonUtil = new CommonUtil();
		OpinionHuanqiuExtensionTest test = new OpinionHuanqiuExtensionTest();
		String depth = "0";
//		String depth = "1";
		String fileNm = "UsNews";

		if (depth.equals("0")) {
			// 0depth
//			File input = new File("F:\\html\\" + fileNm + "0.txt");
			Document document = null;
			document = Jsoup.connect(test.url0).get();
			
			test.changeHtml(document.toString(), test.url0);
		} else {
			// 1depth
			Document document = null;
			document = Jsoup.connect(test.url1).get();
			test.changeHtml(document.toString(), test.url1);
		}
	}

	public String changeHtml(String htmlSrc, String url2)  {
		OpinionHuanqiuExtensionTest test = new OpinionHuanqiuExtensionTest();
		commonUtil = new CommonUtil();
		tagList.delete(0, tagList.length()); // 초기화
		String hostnm="https://opinion.huanqiu.com/article/";
		String aidValue=null;

		if (url2.equals(test.url0)) { // 0depth
			String urlList = commonUtil.getSubStringResult("<body>", "</body>", htmlSrc);
			System.out.println("urlList>"+urlList);
			 // JSON 파싱
			try {
	            JSONParser parser = new JSONParser();
	            JSONObject jsonObject = (JSONObject) parser.parse(urlList);
	            System.out.println("jsonObject>"+jsonObject);
	            JSONArray listArray = (JSONArray) jsonObject.get("list");

	            // "list" 배열의 각 객체에서 "aid" 값을 가져오기
	            tagList.append("<!--List Start-->");
	            for (Object item : listArray) {
	                JSONObject itemObject = (JSONObject) item;
	                aidValue = (String) itemObject.get("aid");
	                if(aidValue !=null) {
	                	 if (tagList.indexOf(String.valueOf(aidValue)) == -1) {
	     		        	tagList.append("\n<a href =\""+hostnm+aidValue+"\n"+"\"></a>");
	     		        }
	                }
	            }
	            tagList.append("\n<!--List End-->");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			htmlSrc = tagList.toString();
			//System.out.println(htmlSrc);

		} else if (url2.equals(test.url1)) { // 1depth
			/* 변수선언 start */
			Document doc = Jsoup.parse(htmlSrc);
			String newHtmlSrc = "<CONTENT-PAGE>\n";

			Elements titleArea = doc.getElementsByClass("article-title");
			String title = titleArea.html();
			newHtmlSrc += "<TITLE>" + title + "</TITLE>\n";
			/* 내용(content) 수집 */
			Elements contentElement = doc.getElementsByClass("article-content");
			String content = contentElement.html();
			String result = removeHtmlTags(content.replace("&lt;", "<").replace("&gt;", ">"));
			newHtmlSrc += "<CONTENT>" + result + "</CONTENT>\n";
			/* 생성일(created_date) 수집 */
			String dateElement = doc.getElementsByClass("article-time").text();
			Long dateText = Long.parseLong(dateElement);
	        Instant instant = Instant.ofEpochMilli(dateText);
	        ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
	        String kkkk=dateTime.toString();
				newHtmlSrc += "<CREATED_DATE>" + data2(kkkk) + "</CREATED_DATE>\n";

			newHtmlSrc += "</CONTENT-PAGE>";

			System.out.println(newHtmlSrc);
			return newHtmlSrc;
		}
		return null;
	}
	
	
	 private String removeHtmlTags(String content) {
		 Pattern pattern = Pattern.compile("<[^>]+>");
	     Matcher matcher = pattern.matcher(content);

	        // HTML 태그 제거
	        String result = matcher.replaceAll("");

	        return result;
	}

	public static String data2(String kkkk) {
	        // 주어진 문자열로 OffsetDateTime 객체를 파싱합니다.
		   //DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	        
	        // 주어진 문자열을 파싱하여 ZonedDateTime 객체를 생성합니다.
	        String input = kkkk;
	        int endIndex = input.indexOf("[");
	        String parsedInput = input.substring(0, endIndex);
	        ZonedDateTime zonedDateTime = ZonedDateTime.parse(parsedInput);
	        
	        // 새로운 형식으로 날짜를 변환하기 위한 DateTimeFormatter 객체를 생성합니다.
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	        
	        // 지정된 형식으로 날짜를 문자열로 변환합니다.
	        String formattedDate = zonedDateTime.format(outputFormatter);
	        
	        // 결과 출력
	        return formattedDate;
	 }


}