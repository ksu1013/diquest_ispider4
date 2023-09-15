package test;

import extension.CommonUtil;
import extension.ConnectionUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @title 소프테크 비공식 수집개발
 * @author 강대범 수집 사이트 : https://www.clingendael.org/publications 테스트
 */
public class ClingendaelInstituteExtensionTest {

	private StringBuffer tagList = new StringBuffer();
	private ConnectionUtil connectionUtil = new ConnectionUtil();
	private CommonUtil commonUtil;

	private final String proxyIp = "10.10.10.213";
	private final int proxyPort = 3128;

	private final String url0 = "https://www.clingendael.org/publications";
	private final String url1 = "https://www.clingendael.org/publication/expert-insights-iran";

	public static void main(String[] args) throws IOException {
		CommonUtil commonUtil = new CommonUtil();
		ClingendaelInstituteExtensionTest test = new ClingendaelInstituteExtensionTest();

//		String depth = "0";
		String depth = "1";
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

	public String changeHtml(String htmlSrc, String url2) {
		ClingendaelInstituteExtensionTest test = new ClingendaelInstituteExtensionTest();
		commonUtil = new CommonUtil();
		tagList.delete(0, tagList.length()); // 초기화

		if (url2.equals(test.url0)) { // 0depth
			String domain = commonUtil.getDomain(url2);

			Document document = Jsoup.parse(htmlSrc);
			Elements urlList = document.getElementsByClass("title");
			htmlSrc = commonUtil.getUrlList(urlList, domain);
			System.out.println(htmlSrc);

		} else if (url2.equals(test.url1)) { // 1depth
			/* 변수선언 start */
//		 	System.out.println("1");
			Document doc = Jsoup.parse(htmlSrc);
			String newHtmlSrc = "<CONTENT-PAGE>\n";
			/* 제목(title) 수집 */
//			String titleArea = doc.select("title").first().ownText();
//			String title = titleArea;

			Elements titleArea = doc.getElementsByClass("block-title");
			String title = titleArea.html();
			newHtmlSrc += "<TITLE>" + title + "</TITLE>\n";
			/* 내용(content) 수집 */
			Elements contentElement = doc.getElementsByClass("publication-content");
			String content = contentElement.html();
			newHtmlSrc += "<CONTENT>" + content + "</CONTENT>\n";
			/* 생성일(created_date) 수집 */
			String dateElement = doc.getElementsByClass("block-date").text();
			String dateText = dateElement;

			String pattern = "(\\d{2})\\s(\\w{3})\\s(\\d{4})\\s-\\s(\\d{2}:\\d{2})";

			// 정규식 패턴을 컴파일
			Pattern regex = Pattern.compile(pattern);

			// 입력된 문자열과 정규식 패턴을 매칭
			Matcher matcher = regex.matcher(dateText);
			if (matcher.find()) {
				String day = matcher.group(1);
				String month = convertMonth(matcher.group(2));
				String year = matcher.group(3);
				String time = matcher.group(4);
				String reformCreatedDateStr = year + "-" + month + "-" + day + " " + time;
				
				newHtmlSrc += "<CREATED_DATE>" + reformCreatedDateStr + "</CREATED_DATE>\n";
			}

			newHtmlSrc += "</CONTENT-PAGE>";

			System.out.println(newHtmlSrc);
			return newHtmlSrc;
		}
		return null;
	}

	public static String convertMonth(String month) {
		// 영문으로 된 월을 숫자로 변환
		switch (month) {
		case "Jan":
			return "01";
		case "Feb":
			return "02";
		case "Mar":
			return "03";
		case "Apr":
			return "04";
		case "May":
			return "05";
		case "Jun":
			return "06";
		case "Jul":
			return "07";
		case "Aug":
			return "08";
		case "Sep":
			return "09";
		case "Oct":
			return "10";
		case "Nov":
			return "11";
		case "Dec":
			return "12";
		default:
			return month;
		}
	}

}