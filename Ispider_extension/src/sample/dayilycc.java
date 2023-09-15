package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extension.CommonUtil;

public class dayilycc {
	static int cnt = 0;
	static int page = 0;
	CommonUtil common = new CommonUtil();
	//static NaverOpenApiNews naverApiNews = new NaverOpenApiNews();
	
	public static void main(String[] args) {
	
	
		dayilycc test = new dayilycc();
		
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
		url = "http://www.dailycc.net/news/articleView.html?idxno=710574&page=2&total=10153";

		test.changeHtml(getProductDetailPage(url), url);		
	}
	
	public String changeHtml(String htmlSrc, String url2) {
		
		dayilycc commonUtil = new dayilycc();
		
		
		
		// 0 depth
		String searchList =  "금융상품'|금융서비스'|소비'|금리'|예금'|적금'|펀드체크카드"; 
		String[] searchArr = searchList.split("'\\|");
		String encodeResult = "";
		
		
		if(url2.contains("search")) {
		String linkList ="<!-- 리스트 시작 -->\n";
//		System.out.println("Start: " + Start);
		//페이지가 15 단위로 이어짐
		for(int page = 1; page < 10; page=page+1) {
			for(int i=0; i < searchArr.length; i++ ) {
				
				String keyword = searchArr[i];
				//https://search.hankookilbo.com/Search?Page=2&tab=NEWS&sort=relation&searchText=%EA%B8%88%EC%9C%B5&searchTypeSet=TITLE,CONTENTS&selectedPeriod=%EC%A0%84%EC%B2%B4&filter=head
				String url = "https://www.dailycc.net/news/articleList.html?page="+page+"&sc_word="+keyword+"&sc_area=A&view_type=sm";
				
				String htmlList=common.getProductDetailPage(url, "UTF-8").replaceAll(" ", "").replaceAll("\n", "");
				
		
			
				Pattern pt1=Pattern.compile("");
				Matcher mc1=pt1.matcher("");
				
				pt1=Pattern.compile("<h4class=\"titles\"><ahref=\"(.*?)\"");
				
				mc1=pt1.matcher(htmlList);
			
				
				while(mc1.find()) {
					linkList += "<a href =\"" + mc1.group(1) + "\">"+keyword+"</a>\n";
				}
				htmlSrc += "<KEYWORD>" + keyword + "</KEYWORD>";// 키워드 추가
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
			
			                                                              
			
			
			//제목
			FORUM_TITLE = commonUtil.getSubStringResult("<h3 class=\"heading\">", "</h3>", htmlSrc);
			
			//컨텐츠
			FORUM_CONTENTS = commonUtil.getSubStringResult("<!-- contents //-->", "<article class=\"social-group for-bottom\">", htmlSrc);
			
			
			//작성일
			String FORUM_DATE_temp=commonUtil.getSubStringResult("<li>입력 :", "</li>", htmlSrc);
			FORUM_DATE=PickUpDate(FORUM_DATE_temp);
			
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
			SimpleDateFormat DateFormat = new SimpleDateFormat ("yyyy-MM-dd");
			Date ParsingDateData = DateFormat.parse(value);
			ParsingData = DateFormat.format(ParsingDateData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//ParsingData = ParsingData.replace("-", "");
		//System.out.println("ParsingData>>>>>>>>>"+ParsingData);
		return ParsingData;
	}

}
