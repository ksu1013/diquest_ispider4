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
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import extension.CommonUtil;

public class nocutnews {
	static int cnt = 0;
	static int page = 0;
	CommonUtil common = new CommonUtil();
	//static NaverOpenApiNews naverApiNews = new NaverOpenApiNews();
	
	public static void main(String[] args) {
	
	
		nocutnews test = new nocutnews();
		
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
		url = "https://www.nocutnews.co.kr/news/4392641";

		test.changeHtml(getProductDetailPage(url), url);		
	}
	
	public String changeHtml(String htmlSrc, String url2) {
		
		nocutnews commonUtil = new nocutnews();
		
		
		
		// 0 depth
		String searchList =  "금융상품'|금융서비스'|소비'|금리'|예금'|적금'|펀드체크카드"; 
		String[] searchArr = searchList.split("'\\|");
		String encodeResult = "";
		
		
		if(url2.contains("search")) {
		String linkList ="<!-- 리스트 시작 -->\n";
//		System.out.println("Start: " + Start);
		//페이지가 15 단위로 이어짐
		for(int page = 0; page < 2; page=page+1) {
			for(int i=0; i < searchArr.length; i++ ) {
				
				String keyword = searchArr[i];

				
				String url = "https://search.nocutnews.co.kr/list?pageIndex="+page+"&sv="+keyword+"&sk=2&sp=0&ot=2&sc=0&a=Center";
				
				
				JSONParser parser = new JSONParser();

				Object obj1 = null;

				
				//System.out.println("jsonList2>>>>>>>>>>>"+jsonList2);
				
					try {
							String jsonList = common.getProductDetailPage(url, "UTF-8");
						try {
							String jsonList2=commonUtil.getSubStringResult("_Page.list.news.data =", ";", jsonList);
							obj1 = parser.parse(jsonList2);
							
							JSONObject j1=(JSONObject) obj1;
							JSONObject j2=(JSONObject) j1.get("result");
							JSONArray j3=(JSONArray) j2.get("rows");
							for (int j = 0; j < j3.size(); j++) {
								
								JSONObject j4=(JSONObject) j3.get(j);
								JSONObject j5=(JSONObject) j4.get("fields");
								Object j6=j5.get("NUM");
								linkList += "<a href=https://www.nocutnews.co.kr/news/"+j6+">" + keyword + "</a>\n";
								htmlSrc = linkList;
							}
							
							
						} catch (Exception e) {
							// TODO: handle exception
						}	
							
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				
			}		
		}
		linkList += "<!-- 리스트 끝 -->";
		htmlSrc=linkList;
		
		System.out.println(htmlSrc);
		
		
		}
		else {
			System.out.println("@@@@");
			String FORUM_CONTENTS = "";		//내용
			String FORUM_TITLE = "";		//제목
			String FORUM_DATE = "";		//날짜
			
			                                                              
			
			
			FORUM_TITLE = commonUtil.getSubStringResult("<title>", "</title>", htmlSrc);
			
			FORUM_CONTENTS = commonUtil.getSubStringResult("<div id=\"pnlContent\" itemprop=\"articleBody\">", "<div class=\"v_like\">", htmlSrc);
			//컨텐츠
			if(htmlSrc.contains("<div class=\"con_b\">")) {
				FORUM_CONTENTS = commonUtil.getSubStringResult("<div class=\"con_b\">", "<div id=\"pnlJebo\" class=\"cb v_infotxt\">", htmlSrc);
			}
			
				
			

			
			String FORUM_DATE_temp=commonUtil.getSubStringResult("<meta name=\"article:published_time\" content=\"", "\" />", htmlSrc);
			try {
				FORUM_DATE=formatStrUTCToDateStr(FORUM_DATE_temp);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
			htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";	
			htmlSrc +="<FORUM_DATE>"+FORUM_DATE+"</FORUM_DATE>";
			
			
//		System.out.println("FORUM_TITLE: " + FORUM_TITLE); 
		System.out.println("FORUM_CONTENTS: " + FORUM_CONTENTS);
//		System.out.println("FORUM_DATE: " + FORUM_DATE);

		}
		
		return htmlSrc;

	}
	private String formatStrUTCToDateStr(String fORUM_DATE) throws ParseException {
		System.out.println("fORUM_DATE>>>>>>>>"+fORUM_DATE);
		String dateTime=fORUM_DATE.replace("T", " ").substring(0, 19);
		
		
	    return dateTime;
		
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
	
	
	
	

}
