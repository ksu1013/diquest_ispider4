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

public class cctoday {
	static int cnt = 0;
	static int page = 0;
	CommonUtil common = new CommonUtil();
	//static NaverOpenApiNews naverApiNews = new NaverOpenApiNews();
	
	public static void main(String[] args) {
	
	
		cctoday test = new cctoday();
		
		List<String> urlList = new ArrayList<String>();
		//navi
		for (int i = 0; i < 1; i++) {
//			urlList.add("https://www.donga.com/news/search?&query=금융상품'|금융서비스'|소비'|금리'|예금'|적금'|펀드체크카드");
		}
		
		//0depth
		for (int i = 0; i < urlList.size(); i++) {
			test.changeHtml(getProductDetailPage(urlList.get(i)), urlList.get(i));
		}	
		
		//1depth
		String url = ""; 
//		url = "https://www.donga.com/news/article/all/20220412/112837367/1";
		url = "https://www.cctoday.co.kr/news/articleView.html?idxno=2165080&_ga=2.169994336.1383611248.1658106198-822596936.1655878190";

		test.changeHtml(getProductDetailPage(url), url);		
	}
	
	public String changeHtml(String htmlSrc, String url2) {
		
		cctoday commonUtil = new cctoday();
		String FORUM_CONTENTS = "";		//내용
		String FORUM_TITLE = "";		//제목
		String FORUM_DATE = "";		//날짜
		String FORUM_TYPE = "중앙지"; // 타입
		String FORUM_MENU = ""; // 메뉴
		
		
		// 0 depth
		String searchList =  "국제'|스포츠'|지역'|IT"; 
		String[] searchArr = searchList.split("'\\|");
		
			
		
	//	if(url2.contains("search")) {
		if(url2.contains("idxno")) {
		String linkList ="<!-- 리스트 시작 -->\n";
//		System.out.println("Start: " + Start);
		//페이지가 15 단위로 이어짐
		for(int page = 1; page < 2; page=page+1) {
			for(int i=0; i < searchArr.length; i++ ) {
				
				String keyword = searchArr[i];
					
				//https://search.hankookilbo.com/Search?Page=2&tab=NEWS&sort=relation&searchText=%EA%B8%88%EC%9C%B5&searchTypeSet=TITLE,CONTENTS&selectedPeriod=%EC%A0%84%EC%B2%B4&filter=head
				String url = "https://prt.cctoday.co.kr/engine_yonhap/search.php?page="+page+"&searchword="+keyword+"&total=&picktab=article&searchcont=article&others_cont_type=&div_code=&cust_div_code=&sfield=&article_type=&period=all&from_date=&to_date=&sort=date&orgsearchword=";
				System.out.println("url>>>>>>>"+url);
				linkList +="<FORUM_MENU>"+keyword+"</FORUM_MENU>\n";
				
				System.out.println("FORUM_MENU: " + keyword);
				
				String htmlList=common.getProductDetailPage(url, "UTF-8").replaceAll(" ", "").replaceAll("\n", "");
				
		
			
				Pattern pt1=Pattern.compile("");
				Matcher mc1=pt1.matcher("");
				
				pt1=Pattern.compile("<divclass=\"title\"><ahref=\"(.*?)\"");
				
				mc1=pt1.matcher(htmlList);
			
				
				while(mc1.find()) {
					linkList += "<a href =\"" + mc1.group(1) + "\">"+keyword+"</a>\n";
				}
				
				
			}
		}
		linkList += "<!-- 리스트 끝 -->";
		
		htmlSrc=linkList;
		
		System.out.println(htmlSrc);
		
		
	}else {
			System.out.println("@@@@");
			
			//제목
			FORUM_TITLE = commonUtil.getSubStringResult("<h3 class=\"heading\">", "</h3>", htmlSrc);
			
			//컨텐츠
			FORUM_CONTENTS = commonUtil.getSubStringResult("<div class=\"article-body\">", "<article class=\"account\">", htmlSrc);
			
			
			//작성일
			String FORUM_DATE_tmep=commonUtil.getSubStringResult("-->승인", "<!--</a>--></li>", htmlSrc);
			FORUM_DATE=PickUpDate(FORUM_DATE_tmep);
			
			
			
			htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
			htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";	
			htmlSrc +="<FORUM_DATE>"+FORUM_DATE+"</FORUM_DATE>";
			htmlSrc +="<FORUM_TYPE>"+FORUM_TYPE+"</FORUM_TYPE>";
			//htmlSrc +="<FORUM_MENU>"+FORUM_MENU+"</FORUM_MENU>";
			
			
		System.out.println("FORUM_TITLE: " + FORUM_TITLE); 
		System.out.println("FORUM_CONTENTS: " + FORUM_CONTENTS);
		System.out.println("FORUM_DATE: " + FORUM_DATE);
		System.out.println("FORUM_TYPE: " + FORUM_TYPE);
		//System.out.println("FORUM_MENU: " + FORUM_MENU);

		
		//20220623
		//2022-06-23
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
		Pattern DatePattern = Pattern.compile("[0-9]{4}[- /. /년 ]*(0[1-9]|1[0-2]|[1-9])[- /. /월]*(0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[- /. /일]*([0-9]+)[- /. /시]*([0-9]+)[- /. /분]");
		Matcher DateMatcher = DatePattern.matcher(InputValue);
		if(DateMatcher.find()){
			value = DateMatcher.group();
			//System.out.println("value>>>>>>>>>"+value);
		}else{
			value = "1900-01-01";
		}
		value = value.replace(".", "-").replace("년", "-").replace("월", "-").replace("/", "-").replace("일", " ").replace("시", ":").replace("분", "");
		//System.out.println("value22222222>>>>>>>>>"+value);
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
