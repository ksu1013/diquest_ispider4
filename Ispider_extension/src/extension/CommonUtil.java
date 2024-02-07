package extension;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

	/* 숫자 여부 체크  */
	public boolean isNumeric(String s) {
		return s.replaceAll("[+-]?\\d+", "").equals("") ? true : false;
	}

	public String getNumberByText(String text) {
		if(text == null || text.equals("")) {
			return null;
		}

		return text.replaceAll("[^0-9]","");
	}


	/**
	 * String to json
	 * */
	public JsonObject stirngToJson(String jsonText) {

		if(jsonText == null || jsonText.equals("")) {
			System.out.println("[NOT FOUND]jsonText is null..");
			return null;
		}

		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(jsonText).getAsJsonObject();

		return obj;
	}


	public String getSubStringResult(String startTag, String endTag, String text) {


		String result = "";

		if(!text.contains(startTag) || !text.contains(endTag)) {
			return result;
		}
		String subStringText = null;
		try {
			int start = text.indexOf(startTag) + startTag.length();
			subStringText = text.substring(start);
			int end = subStringText.indexOf(endTag);
			result = subStringText.substring(0, end);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			result = "";
		}

		return result;
	}


	// www.naver.com 여기까지만 가져오는것
	public String getDomain(String url) {
		String preText = "http://";
		if(url.contains("https://")) preText = "https://";
		String newUrl = url.substring(url.indexOf(preText) + preText.length());
		String newUrl2 = newUrl.substring(0,newUrl.indexOf("/"));

		String domain = preText + newUrl2;

		return domain;
	}

	// ? 앞까지.
	public String getFullDomain(String url) {
		String preText = "http://";
		if(url.contains("https://")) preText = "https://";
		String newUrl = url.substring(url.indexOf(preText) + preText.length());
		String newUrl2 = newUrl.substring(0,newUrl.indexOf("?"));

		String domain = preText + newUrl2;

		return domain;
	}


	/*
	 * 추가적인 HTML 정보를 받아 오기 위한 http Connection
	 * 
	 * */
	public String getProductDetailPage(String url, String encoding) {
		StringBuffer detail = new StringBuffer();
		try {
			URL targetURL = new URL(url);
			URLConnection urlConn = targetURL.openConnection();
			HttpURLConnection hurlc = (HttpURLConnection) urlConn;
			hurlc.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
			hurlc.setRequestProperty("Referer", url);
			hurlc.setRequestMethod("GET");
			hurlc.setDoOutput(true);
			hurlc.setDoInput(true);
			hurlc.setUseCaches(false);
			hurlc.setDefaultUseCaches(false);
			hurlc.setReadTimeout(20000);
			hurlc.setConnectTimeout(20000);

			BufferedReader in = new BufferedReader(new InputStreamReader(hurlc.getInputStream(), encoding));
			for (String line = null; (line = in.readLine()) != null;){
				detail.append(line+"\r\n");
			}
			in.close();
			if(hurlc != null) hurlc.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return detail.toString();
	}

	public String pickUpDate(String InputValue) {
		String value = "";
		String ParsingData = "";
		if (!(InputValue.equals("") && !(InputValue.equals(null)))) {
			Pattern DatePattern = Pattern
					.compile("[0-9]{4}[- /. /년 ]*(0[1-9]|1[0-2]|[1-9])[- /. /월]*(0[1-9]|[1-2][0-9]|3[0-1]|[1-9])");
			Matcher DateMatcher = DatePattern.matcher(InputValue.replace(" ", ""));
			if (DateMatcher.find()) {
				value = DateMatcher.group();
			} else {
				value = "1900-01-01";
			}
			value = value.replace(".", "-").replace("년", "-").replace("월", "-").replace("/", "-");
			try {
				SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date ParsingDateData = DateFormat.parse(value);
				ParsingData = DateFormat.format(ParsingDateData);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ParsingData = ParsingData.replace("-", "");
		}
		return ParsingData;
	}


	// 공백, 탭 , 개행 등 제거
	public String removeWhiteSpace(String input) {

		if (input == null || input.equals("")) {
			return input;
		}

		return input.replaceAll("(\r\n|\r|\n|\n\r|\\p{Z}|\\t)", "");
	}

	/*
	 * 추가적인 HTML 정보를 받아 오기 위한 http Connection
	 * 1페이지 접속은 되나 페이지 변경시 안될때 사용
	 * url , page파라미터(ex: pageNo=2), encoding Type
	 * 
	 * */
	public String getProductDetailPageParam(String url, String pageParam, String encoding) {
		StringBuffer detail = new StringBuffer();
		try {
			URL targetURL = new URL(url);
			URLConnection urlConn = targetURL.openConnection();
			HttpURLConnection hurlc = (HttpURLConnection) urlConn;
			hurlc.setRequestProperty("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
			hurlc.setRequestProperty("Referer", url);
			hurlc.setRequestProperty("Upgrade-Insecure-Requests", "1");
			hurlc.setRequestMethod("POST");
			hurlc.setRequestProperty("x-requested-with", "XMLHttpRequest");
			hurlc.setRequestProperty("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
			hurlc.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset="+encoding);
			hurlc.setDoOutput(true);
			hurlc.setDoInput(true);
			hurlc.setUseCaches(false);
			hurlc.setDefaultUseCaches(false);
			hurlc.setReadTimeout(2000000);
			hurlc.setConnectTimeout(2000000);
			OutputStreamWriter wr = new OutputStreamWriter(hurlc.getOutputStream());
			wr.write(pageParam);
			wr.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(hurlc.getInputStream(), encoding));
			for (String line = null; (line = in.readLine()) != null;)
				detail.append(line+"\n");

			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return detail.toString();
	}

	// create date 날짜 포맷 변경
	public String changeDateFormat(String datetimeHtml){
		String datetime = "";

		if(datetimeHtml != null && !datetimeHtml.equals("")) {
			LocalDateTime localtime = LocalDateTime.from(Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(datetimeHtml)).atZone(ZoneId.of("Asia/Seoul")));
			datetime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localtime);
		}

		return datetime;
	}

	// 0뎁스 LIST 만들기
	public String getUrlList(Elements urlList, String domain){
		StringBuffer tagList = new StringBuffer();
		urlList = urlList.select("a[href]");

		tagList.append("<!--List Start-->");
		for (Element link : urlList) {
			tagList.append("\n<a href =\"" + domain + link.attr("href") + "\"></a>");
		}
		tagList.append("\n<!--List End-->");
		return tagList.toString();
	}
	public String getUrlList2(StringBuilder sb, String domain) {
		StringBuffer tagList = new StringBuffer();
		tagList.append("<!--List Start-->");
		for (int i = 0; i < sb.length(); i++) {
			tagList.append("\n<a href =\"" + domain + sb + "\"></a>");
		}
		tagList.append("\n<!--List End-->");
		return tagList.toString();
		// TODO Auto-generated method stub
	}

	

	// 1뎁스 tag 만들기  title, create_date, content
	public String makeCollectContext(String title, String datetime, String content) {
		StringBuffer tagList = new StringBuffer();
		tagList.append("<TITLE>" + title + "</TITLE>\n");
			tagList.append("<DATETIME>" + datetime + "</DATETIME>\n");
		tagList.append("<CONTENT>" + content + "</CONTENT>\n");

		return tagList.toString();
	}

	
	

}
