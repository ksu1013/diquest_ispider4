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
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
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

//import com.diquest.ispider.common.Keyword;


import extension.CommonUtil;


public class test_DONGA{
	
	static int cnt = 0;
	static int page = 0;
	CommonUtil common = new CommonUtil();
	//static NaverOpenApiNews naverApiNews = new NaverOpenApiNews();
	
	public static void main(String[] args) {
	
		//naverApiNews = new NaverOpenApiNews();
	
		test_DONGA test = new test_DONGA();
		
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
//		url = "http://economychosun.com/client/news/view.php?boardName=C08&page=1&t_num=13607570";
		url = "http://weekly.chosun.com/news/articleView.html?idxno=20973";



		test.changeHtml(getProductDetailPage(url), url);		
	}
	
	public String changeHtml(String htmlSrc, String url2) {
		test_DONGA commonUtil = new test_DONGA();
		
		
		
		// 0 depth
		String searchList =  "금융상품'|금융서비스'|소비'|금리'|예금'|적금'|펀드체크카드"; 
		String[] searchArr = searchList.split("'\\|");
		String encodeResult = "";
		
		
		if(url2.contains("search")) {
		String linkList ="<!-- 리스트 시작 -->\n";
//		System.out.println("Start: " + Start);
		//페이지가 15 단위로 이어짐
		for(int page = 1; page < 2; page=page+15) {
			for(int i=0; i < searchArr.length; i++ ) {
				
				String keyword = searchArr[i];
				String url = "https://www.donga.com/news/search?p="+page+"&query="+keyword+"&check_news=1&more=1&sorting=1&search_date=1&v1=&v2=&range=1";
//				System.out.println("keyword: " + keyword);

//				System.out.println("url: " + url);
//	
				
				String htmlList=common.getProductDetailPage(url, "UTF-8").replaceAll(" ", "").replaceAll("\n", "");
				
		
//				System.out.println("htmlList"+htmlList);
			
				Pattern pt1=Pattern.compile("");
				Matcher mc1=pt1.matcher("");
				
				pt1=Pattern.compile("<pclass=\"tit\"><ahref=\"(.*?)\"");
				
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
			
			String FORUM_CONTENTS = ""; // 내용
			String FORUM_TITLE = ""; // 제목
			String FORUM_DATE = ""; // 날짜
			String FORUM_MENU = ""; // 메뉴
			String FORUM_TYPE = "중앙지"; // 타입
			
			String contentTemp="";

			Pattern pt1 = Pattern.compile("");
			Matcher mc1 = pt1.matcher("");
			
			if(url2.contains("http://kid.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!-- 내용 -->", "<!-- // 내용 -->", htmlSrc);
				FORUM_DATE = commonUtil.getSubStringResult("<span class=\"date\">\r\n" + 						"    입력 :", "</span>", htmlSrc);
				
				FORUM_CONTENTS=contentTemp;
			}else if((url2.contains("http://realty.chosun.com/"))||url2.contains("https://realty.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<div class=\"par\">", "<!-- #include \"common/article_tags.common\" -->", htmlSrc);
				String FORUM_DATE_temp2 = commonUtil.getSubStringResult("<p id=\"date_text\">", "<h3 class=\"news_subtitle\">", htmlSrc);
				pt1 = Pattern.compile("(입력 : [0-9]+\\.[0-9]+\\.[0-9]+\\ [0-9]+\\:[0-9]+)");

				mc1 = pt1.matcher(FORUM_DATE_temp2);
				
				if (mc1.find()) {
					// System.out.println("@@@@");
					FORUM_DATE += mc1.group(1);
				}
				
				FORUM_CONTENTS=contentTemp;
				
			}else if (url2.contains("http://san.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!-- 기사 본문 start -->", "<!-- 기사 본문 end -->", htmlSrc);
				String FORUM_DATE_temp2 = commonUtil.getSubStringResult("<div class=\"news_date\">", "</div>", htmlSrc);
				pt1 = Pattern.compile("(입력 [0-9]+\\.[0-9]+\\.[0-9]+\\ [0-9]+\\:[0-9]+)");

				mc1 = pt1.matcher(FORUM_DATE_temp2);
				
				if (mc1.find()) {
					// System.out.println("@@@@");
					FORUM_DATE += mc1.group(1);
				}
				
				FORUM_CONTENTS=contentTemp;
				
			}else if(url2.contains("http://weekly.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<article  id=\"article-view-content-div\" class=\"article-veiw-body view-page\" itemprop=\"articleBody\">", "</article>", htmlSrc);
				FORUM_DATE = commonUtil.getSubStringResult("<li>입력", "</li>", htmlSrc);
				FORUM_CONTENTS=contentTemp;
				//FORUM_DATE = PickUpDate(FORUM_DATE_temp);
				
			}else if(url2.contains("http://newsteacher.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!-- article -->", "<!-- article -->", htmlSrc);
				FORUM_DATE = commonUtil.getSubStringResult("<span class=\"date_text\">", "</span>", htmlSrc);
				FORUM_CONTENTS=contentTemp;
				//FORUM_DATE = PickUpDate(FORUM_DATE_temp);
				
			}else if(url2.contains("http://economychosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!-- 기사본문 : s -->", "<div class=\"date_text\">", htmlSrc);
				String FORUM_DATE_temp = commonUtil.getSubStringResult("<p class=\"ho\">", "</p>", htmlSrc).replace("&nbsp;", "");
				pt1 = Pattern.compile("([0-9]+호) ([0-9]+년[0-9]+월[0-9]+일)");

				mc1 = pt1.matcher(FORUM_DATE_temp);
				//System.out.println("htmlSrc>>>>>>>>>"+htmlSrc);
				if (mc1.find()) {
					// System.out.println("@@@@");
					FORUM_DATE += mc1.group(2);
				}
				FORUM_CONTENTS=contentTemp;
				
			}else if(url2.contains("https://health.chosun.com/")||url2.contains("http://health.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!-- 기사 본문 start -->", "<!--여기까지만-->", htmlSrc);
				String FORUM_DATE_temp = commonUtil.getSubStringResult("<p id=\"date_text\">", "</p>", htmlSrc);
				pt1 = Pattern.compile("(입력 [0-9]+\\.[0-9]+\\.[0-9]+\\ [0-9]+\\:[0-9]+)");

				mc1 = pt1.matcher(FORUM_DATE_temp);
				if (mc1.find()) {
					FORUM_DATE += mc1.group(1);
				}
				FORUM_CONTENTS=contentTemp;
				
			}else if(url2.contains("http://news.chosun.com/")&&!url2.contains("http://news.chosun.com/misaeng/")) {
				contentTemp = commonUtil.getSubStringResult("<div class=\"par\">", "<!-- 기사 본문 end -->", htmlSrc);
				FORUM_DATE = commonUtil.getSubStringResult("<div id=\"date_text\" class=\"news_date\">입력", "</div>", htmlSrc);
				
				FORUM_CONTENTS=contentTemp;
				
				
			}else if(url2.contains("http://weeklybiz.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<div class=\"par\">", "<!-- 기사 본문 end -->", htmlSrc);
				FORUM_DATE = commonUtil.getSubStringResult("<div id=\"date_text\" class=\"news_date\"><span>입력", "</span>", htmlSrc);
				
				FORUM_CONTENTS=contentTemp;
				
				
			}else if(url2.contains("https://misaeng.chosun.com/")||url2.contains("http://news.chosun.com/misaeng/")) {
				contentTemp = commonUtil.getSubStringResult("<div class=\"par\">", "<div class=\"news_imgbox\">", htmlSrc);
				String FORUM_DATE_temp2 = commonUtil.getSubStringResult("<p id=\"date_text\">", "<h3 class=\"news_subtitle\">", htmlSrc);
				pt1 = Pattern.compile("(입력 : [0-9]+\\.[0-9]+\\.[0-9]+\\ [0-9]+\\:[0-9]+)");

				mc1 = pt1.matcher(FORUM_DATE_temp2);
				
				if (mc1.find()) {
					FORUM_DATE += mc1.group(1);
				}
				
				FORUM_CONTENTS=contentTemp;
				
				
				
			}else if(url2.contains("http://edu.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!--기사영역S-->", "<!--기사영역E-->", htmlSrc);
				FORUM_DATE = commonUtil.getSubStringResult("<p class=\"days\">", "</p>", htmlSrc);
				FORUM_CONTENTS=contentTemp;
				
			}else if(url2.contains("http://boutique.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!-- 기사 본문 start -->", "<!-- 기사 본문 end -->", htmlSrc);
				FORUM_DATE = commonUtil.getSubStringResult("<div id=\"date_text\" class=\"news_date\">입력", "</div>", htmlSrc);
				FORUM_CONTENTS=contentTemp;
				
		}else {
				String FORUM_DATE_temp="";
				contentTemp = commonUtil.getSubStringResult("<body>", "</body>", htmlSrc).replaceAll("\\{\"_id","\n\\{\"_id");
				pt1 = Pattern.compile("\"content\":\"(.*?)\",");
				mc1 = pt1.matcher(contentTemp);

				
				
				while (mc1.find()) {
						FORUM_CONTENTS += mc1.group(1);
				}
				

				pt1 = Pattern.compile("display_date\":\"([0-9]{4}[- /. /년 ]*[0-9]{2}[- /. /월]*[0-9]{2}T[0-9]{2}:[0-9]{2})");
				mc1 = pt1.matcher(htmlSrc);

				if (mc1.find()) {
					// System.out.println("@@@@");
					FORUM_DATE_temp += mc1.group(1);
				}
				 
				 
					try {
						FORUM_DATE=PickUpDate2(FORUM_DATE_temp);
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				 
			}
			
			FORUM_TITLE = commonUtil.getSubStringResult("<title>", "</title>", htmlSrc);
			FORUM_MENU= commonUtil.getSubStringResult("_sf_async_config.sections = \"", "\";", htmlSrc).replaceAll("조선","");
			
			if(url2.contains("http://economychosun.com/")) {
				FORUM_MENU= commonUtil.getSubStringResult("<!-- 현재위치 -->", "<!-- 본문 -->", htmlSrc);
				
			}else if(url2.contains("http://weeklybiz.chosun.com/")||url2.contains("http://weekly.chosun.com/")||url2.contains("http://edu.chosun.com/")) {
				FORUM_MENU= commonUtil.getSubStringResult("<meta property=\"article:section\" content=\"", "\">", htmlSrc);
				
			}
			

			
//			System.out.println("FORUM_TITLE: " + FORUM_TITLE);
//			System.out.println("FORUM_CONTENTS: " + FORUM_CONTENTS);
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
	
	public static String getFileName(String filePath, String fileName) {
		try {
			URL obj = new URL(filePath);
			URLConnection conn = obj.openConnection();
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(20000);
			String header = conn.getHeaderField("Content-Disposition");
			if(header != null) {
				header = header.split("=")[1];
				header = header.replaceAll("\"", "");
				header = header.replaceAll(";", "");
				header = URLDecoder.decode(header,"8859_1");
				String s = new String(header.getBytes("8859_1"), "UTF-8");
				System.out.println("ㅎㅎ: " + s);
				if(s.contains("�")) {
					String s2 = new String(header.getBytes("8859_1"), "UTF-8");
					return s2;
				}else {
					return s;
				}
			}
			return fileName;
		}catch(Exception e) {
			System.out.println("첨부파일명 가져오기 실패 : " + filePath);
		}
		return fileName;
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
		Pattern DatePattern = Pattern.compile("[0-9]{4}[- /. /년 ]*[0-9]{2}[- /. /월]*[0-9]{2} [0-9]{2}:[0-9]{2}");
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
	
	public static String PickUpDate2(String InputValue) throws java.text.ParseException{		
		
			SimpleDateFormat DateFormat = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm");
			SimpleDateFormat DateFormat2 = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
			
			Date ParsingDateData = DateFormat.parse(InputValue);
		
			String new_date=DateFormat2.format(ParsingDateData);
			Date kkk=DateFormat2.parse(new_date);
			
			Calendar cal = Calendar.getInstance();

		     cal.setTime(kkk);
		     cal.add(Calendar.HOUR, 9);

		      String today = DateFormat2.format(cal.getTime());  
			
			
			
			
		return today;
	}
	
}
