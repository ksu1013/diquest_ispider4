package test;

import extension.CommonUtil;
import extension.ConnectionUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @title 소프테크 비공식 수집개발
 * @author 강대범 수집 사이트 : https://www.clingendael.org/publications 테스트
 */
public class OneKoreaNewsExtensionTest {

	private StringBuffer tagList = new StringBuffer();
	private ConnectionUtil connectionUtil = new ConnectionUtil();
	private CommonUtil commonUtil;

	private final String url0 = "http://news.onekoreanews.net/section.php?pg=1&thread=01";
	private final String url1 = "http://news.onekoreanews.net/detail.php?number=91520&thread=01r04";

	public static void main(String[] args) throws IOException {
		CommonUtil commonUtil = new CommonUtil();
		OneKoreaNewsExtensionTest test = new OneKoreaNewsExtensionTest();

		String depth = "0";
//		String depth = "1";
		String fileNm = "UsNews";

		if (depth.equals("0")) {
			// 0depth
//			File input = new File("F:\\html\\" + fileNm + "0.txt");
			Document document = null;
			document = Jsoup.connect(test.url0).get();
			//System.out.println("document>>>>>>>>"+document);
			test.changeHtml(document.toString(), test.url0);
		} else {
			// 1depth
			Document document = null;
			document = Jsoup.connect(test.url1).get();
			test.changeHtml(document.toString(), test.url1);
		}
	}

	public String changeHtml(String htmlSrc, String url2) {
		OneKoreaNewsExtensionTest test = new OneKoreaNewsExtensionTest();
		commonUtil = new CommonUtil();
		tagList.delete(0, tagList.length()); // 초기화

		if (url2.equals(test.url0)) { // 0depth
			String domain = commonUtil.getDomain(url2);
			String urlList=commonUtil.getSubStringResult("<td style=\"padding:0 4 0 4;\">", "</table><script>document.all.total_news_number.innerHTML = '15176';</script></td>", htmlSrc);
			Pattern pt1 = Pattern.compile("<a\s+href=(.*?)>");
			Matcher mt1 = pt1.matcher(urlList);
			tagList.append("<!--List Start-->");
			while (mt1.find())
				tagList.append("\n<a href ='" + domain+"/"+mt1.group(1)+ "'></a>");
			tagList.append("\n<!--List End-->");
			htmlSrc = tagList.toString();
			System.out.println(htmlSrc);

		}else if (url2.equals(test.url1)) { // 1depth
			/* 변수선언 start */
//		 	System.out.println("1");
			Document doc = Jsoup.parse(htmlSrc);
			String newHtmlSrc = "<CONTENT-PAGE>\n";

			Element titleArea = doc.getElementsByTag("title").first();
			String title = titleArea.html();
			newHtmlSrc += "<TITLE>" + title + "</TITLE>\n";
			/* 내용(content) 수집 */
			Elements contentElement = doc.select("#ct");
			String content = contentElement.html();
			newHtmlSrc += "<CONTENT>" + content + "</CONTENT>\n";
			/* 생성일(created_date) 수집 */
			Element dateElement = doc.select("td.fontSize08").first();
			Element tdTag = dateElement.selectFirst("td");
			// 내용 가져오기
			String dateContent = tdTag.text();
			// 내용에서 불필요한 부분 제거하고 원하는 형식으로 변환
			dateContent = dateContent.replaceAll("[年月]", "-").replace("日", "");
			newHtmlSrc += "<CREATED_DATE>" + dateContent + "</CREATED_DATE>\n";
			newHtmlSrc += "</CONTENT-PAGE>";

			System.out.println(newHtmlSrc);
			return newHtmlSrc;
		}
		return null;
	}
	
	
	 public static String data(String kkkk) {
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