package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import extension.CommonUtil;

public class kbs {
	static int cnt = 0;
	static int page = 0;
	CommonUtil common = new CommonUtil();
	// static NaverOpenApiNews naverApiNews = new NaverOpenApiNews();

	public static void main(String[] args) {

		kbs test = new kbs();

		List<String> urlList = new ArrayList<String>();
		// navi
		for (int i = 0; i < 1; i++) {
			urlList.add("https://www.donga.com/news/search?&query=금융상품'|금융서비스'|소비'|금리'|예금'|적금'|펀드체크카드");
		}

		// 0depth
		for (int i = 0; i < urlList.size(); i++) {
			test.changeHtml(getProductDetailPage(urlList.get(i)), urlList.get(i));
		}

		// 1depth
		String url = "";
//		url = "https://www.donga.com/news/article/all/20220412/112837367/1";
		url = "https://news.kbs.co.kr/news/view.do?ncd=5469927";

		test.changeHtml(getProductDetailPage(url), url);
	}

	public String changeHtml(String htmlSrc, String url2) {

		kbs commonUtil = new kbs();

		// 0 depth
		String searchList = "금융상품'|";
		String[] searchArr = searchList.split("'\\|");
		String encodeResult = "";

		if (url2.contains("search")) {
			String linkList = "<!-- 리스트 시작 -->\n";
//		System.out.println("Start: " + Start);
			// 페이지가 15 단위로 이어짐
			for (int page = 1; page < 2; page = page + 1) {
				for (int i = 0; i < searchArr.length; i++) {

					String keyword = searchArr[i];
					String urlKeyword = "";
					try {
						urlKeyword = URLEncoder.encode(keyword, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					String url = "https://reco.kbs.co.kr/v2/search?target=newstotal&keyword=" + urlKeyword + "&page="
							+ page + "&page_size=10&sort_option=score&searchfield=all";
					System.out.println("url>>>>>>>>>>>" + url);
					String htmlList = common.getProductDetailPage(url, "UTF-8").replaceAll(" ", "").replaceAll("\n",
							"");

					JSONParser parser = new JSONParser();

					Object obj1 = null;

					try {
						obj1 = parser.parse(htmlList);
						JSONObject j1 = (JSONObject) obj1;
						JSONArray j2 = (JSONArray) j1.get("data");
						for (int j = 0; j < j2.size(); j++) {
							JSONObject j3 = (JSONObject) j2.get(j);
							Object j4 = j3.get("target_url");

							linkList += "<a href=" + j4 + ">" + keyword + "</a>\n";
							htmlSrc = linkList;

						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			linkList += "<!-- 리스트 끝 -->";
			htmlSrc = linkList;

			System.out.println(htmlSrc);

		} else {
			System.out.println("@@@@");
			String FORUM_CONTENTS = ""; // 내용
			String FORUM_TITLE = ""; // 제목
			String FORUM_DATE = ""; // 날짜

			 FORUM_TITLE = commonUtil.getSubStringResult("<title>", "</title>", htmlSrc);

			// 컨텐츠
			 FORUM_CONTENTS = commonUtil.getSubStringResult("<div class=\"view_con_text\">", "<!-- //프린트 영역 -->", htmlSrc);

			// 작성일
			 FORUM_DATE=commonUtil.getSubStringResult("<li><span>입력</span>", "</li>", htmlSrc);
			

			htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
			htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";
			htmlSrc += "<FORUM_DATE>" + FORUM_DATE + "</FORUM_DATE>";

			System.out.println("FORUM_TITLE: " + FORUM_TITLE);
			System.out.println("FORUM_CONTENTS: " + FORUM_CONTENTS);
			System.out.println("FORUM_DATE: " + FORUM_DATE);

		}

		return htmlSrc;

	}

	private static String getProductDetailPage(String url) {

		StringBuffer detail = new StringBuffer();
		try {
			URL targetURL = new URL(url);
			URLConnection urlConn = targetURL.openConnection();
			// responseCode 확인
			HttpURLConnection hurlc = (HttpURLConnection) urlConn;
//			int responseCode = hurlc.getResponseCode();
//			System.out.println("responseCode: "+responseCode);
			//

			hurlc.setRequestProperty("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
			hurlc.setRequestProperty("Referer", url);
			hurlc.setRequestMethod("GET");
			hurlc.setDoOutput(true);
			hurlc.setDoInput(true);
			hurlc.setUseCaches(false);
			hurlc.setDefaultUseCaches(false);
			hurlc.setReadTimeout(20000);
			hurlc.setConnectTimeout(20000);
			BufferedReader in = new BufferedReader(new InputStreamReader(hurlc.getInputStream(), "UTF-8"));
			for (String line = null; (line = in.readLine()) != null;) {
				detail.append(line + "\n");
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return detail.toString();
	}

	public String getSubStringResult(String startTag, String endTag, String text) {

		String result = "";
		if (!text.contains(startTag) || !text.contains(endTag)) {
			return result;
		}
		String subStringText = null;
		try {
			int start = text.indexOf(startTag) + startTag.length();
			subStringText = text.substring(start);
			int end = subStringText.indexOf(endTag);
			result = subStringText.substring(0, end);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			result = "";
		}

		return result;
	}

	public static String PickUpDate(String InputValue) {
		/**
		 * 문장을 삽입하면 날짜를 추출해주는 메서드 참고) 사용가능 형식 1900-01-01 , 1900-1-1 , 1900/01/01 ,
		 * 1900/1/1 , 1900년01월01일, 1900년1월1일
		 * 
		 * @since 2019-05-09
		 * @author 이누리
		 */

		String value = "";
		String ParsingData = "";
		Pattern DatePattern = Pattern.compile("[0-9]{4}-[0-9]+-[0-9]+ [0-9]+:[0-9]+");
		Matcher DateMatcher = DatePattern.matcher(InputValue);
		if (DateMatcher.find()) {
			value = DateMatcher.group();
			System.out.println("value>>>>>>>>>" + value);
		} else {
			value = "1900-01-01";
		}
		value = value.replace(".", "-").replace("년", "-").replace("월", "-").replace("/", "-").replace("일", " ")
				.replace("시", ":").replace("분", "");
		System.out.println("value22222222>>>>>>>>>" + value);
		try {
			SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date ParsingDateData = DateFormat.parse(value);
			ParsingData = DateFormat.format(ParsingDateData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ParsingData = ParsingData.replace("-", "");
		System.out.println("ParsingData>>>>>>>>>" + ParsingData);
		return ParsingData;
	}

}
