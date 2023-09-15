package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import extension.CommonUtil;

public class ynaNews {
	static int cnt = 0;
	static int page = 0;
	static CommonUtil common = new CommonUtil();
	//static NaverOpenApiNews naverApiNews = new NaverOpenApiNews();
	
	public static void main(String[] args) {
	
	
		ynaNews test = new ynaNews();

		List<String> urlList = new ArrayList<String>();
		//navi
		for (int i = 0; i < 1; i++) {
			urlList.add("https://www.donga.com/news/search?&query=금융상품'|금융서비스'|소비'|금리'|예금'|적금'|펀드체크카드");
		}
		
		//0depth
		for (int i = 0; i < urlList.size(); i++) {
			test.changeHtml(getProductDetailPage(urlList.get(i)), urlList.get(i));
		}	
		
		//1depth
		String url = ""; 
//		url = "https://www.donga.com/news/article/all/20220412/112837367/1";
		url = "https://www.yna.co.kr/view/AKR20220524137100002?section=search";

		test.changeHtml(getProductDetailPage(url), url);		
	}
	
	public String changeHtml(String htmlSrc, String url2) {
		
		ynaNews commonUtil = new ynaNews();
		
		
		
		// 0 depth
		String searchList =  "금융상품'|"; 
		String[] searchArr = searchList.split("'\\|");
		
		
		if(url2.contains("search3")) {
		String linkList ="<!-- 리스트 시작 -->\n";
		for(int page = 1; page < 2; page=page+1) {
			for(int i=0; i < searchArr.length; i++ ) {
				
				String keyword = searchArr[i];
				keyword = searchArr[i];
				String urlKeyword = "";
				try {
					urlKeyword = URLEncoder.encode(keyword, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String url = "https://ars.yna.co.kr/api/v2/search.asis?callback=Search.SearchPreCallback&page_no="+page+"&query="+urlKeyword+"&ctype=A&page_size=10&channel=basic_kr";
				String htmlList=common.getProductDetailPage(url, "UTF-8").replaceAll(" ", "").replaceAll("\n", "").replace("Search.SearchPreCallback(", "").replace(")", "");
				System.out.println(htmlList);
				
				JSONParser parser =new JSONParser();
				
				try {
					JSONObject j1= (JSONObject) parser.parse(htmlList);
					JSONObject j2=(JSONObject) j1.get("KR_ARTICLE");
					JSONArray j3=(JSONArray) j2.get("result");
					for (int j = 0; j < j3.size(); j++) {
						JSONObject j4=(JSONObject) j3.get(j);
						Object j5=j4.get("CONTENTS_ID");
						
						
						linkList += "<a href=https://www.yna.co.kr/view/"+j5+"?section=search>" + keyword + "</a>\n";
						htmlSrc = linkList;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
			}
		}
		linkList += "<!-- 리스트 끝 -->";
		htmlSrc=linkList;
		
		System.out.println(htmlSrc);
		
		
		}else {
			System.out.println("@@@@");
			String FORUM_CONTENTS = "";		//내용
			String FORUM_TITLE = "";		//제목
			String FORUM_DATE = "";		//날짜
			
			                                                              
			
			
			//제목
			FORUM_TITLE = commonUtil.getSubStringResult("<title>", "</title>", htmlSrc);
			
			//컨텐츠
			FORUM_CONTENTS = commonUtil.getSubStringResult("<p class=\"txt-link\"><a href=\"\" target=\"_blank\" title=\"새창\"></a></p>", "<p class=\"txt-copyright adrs\">", htmlSrc);
			
			
			//작성일
			FORUM_DATE=commonUtil.getSubStringResult("<span class=\"txt\">송고시간</span>", "</p>", htmlSrc);
			
			//FORUM_DATE=PickUpDate(FORUM_DATE_tmep);
			
			
			
			htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
			htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";	
			htmlSrc +="<FORUM_DATE>"+FORUM_DATE+"</FORUM_DATE>";
			
			
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
			
			hurlc.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
			hurlc.setRequestProperty("Referer", url);
			hurlc.setRequestMethod("GET");
			hurlc.setDoOutput(true);
			hurlc.setDoInput(true);
			hurlc.setUseCaches(false);
			hurlc.setDefaultUseCaches(false);
			hurlc.setReadTimeout(20000);
			hurlc.setConnectTimeout(20000);
			BufferedReader in = new BufferedReader(new InputStreamReader(hurlc.getInputStream(), "UTF-8"));
			for (String line = null; (line = in.readLine()) != null;){
				detail.append(line+"\n");
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
	
	
	
	public static String PickUpDate(String InputValue){		
		/**
		 * 문장을 삽입하면 날짜를 추출해주는 메서드
		 * 참고)
		 * 사용가능 형식
		 * 1900-01-01 , 1900-1-1 , 1900/01/01 , 1900/1/1 , 1900년01월01일, 1900년1월1일
		 * @since 2019-05-09
		 * @author 이누리
		 */
		
		String value = "";
		String ParsingData = "";
		Pattern DatePattern = Pattern.compile("[0-9]{4}-[0-9]+-[0-9]+ [0-9]+:[0-9]+");
		Matcher DateMatcher = DatePattern.matcher(InputValue);
		if(DateMatcher.find()){
			value = DateMatcher.group();
			System.out.println("value>>>>>>>>>"+value);
		}else{
			value = "1900-01-01";
		}
		value = value.replace(".", "-").replace("년", "-").replace("월", "-").replace("/", "-").replace("일", " ").replace("시", ":").replace("분", "");
		System.out.println("value22222222>>>>>>>>>"+value);
		try {
			SimpleDateFormat DateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
			Date ParsingDateData = DateFormat.parse(value);
			ParsingData = DateFormat.format(ParsingDateData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ParsingData = ParsingData.replace("-", "");
		System.out.println("ParsingData>>>>>>>>>"+ParsingData);
		return ParsingData;
	}

}
