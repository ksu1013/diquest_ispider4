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

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.parser.ParseException;

import extension.CommonUtil;


public class test_Seoul{
	
	static int cnt = 0;
	static int page = 0;
	CommonUtil common = new CommonUtil();
	//static NaverOpenApiNews naverApiNews = new NaverOpenApiNews();
	
	public static void main(String[] args) {
	
		//naverApiNews = new NaverOpenApiNews();
	
		test_Seoul test = new test_Seoul();
		
		List<String> urlList = new ArrayList<String>();
		//navi
		for (int i = 0; i < 1; i++) {
			urlList.add("https://www.search.co.kr?&query=금융상품'|금융서비스'|소비'|금리'|예금'|적금'|펀드체크카드");
		}
		
		//0depth
		for (int i = 0; i < urlList.size(); i++) {
//			test.changeHtml(getProductDetailPage(urlList.get(i)), urlList.get(i));
		}	
		//1depth
		String url = ""; 
//		url = "https://www.donga.com/news/article/all/20220412/112837367/1";
//		url = "https://www.seoul.co.kr/news/newsView.php?id=20220421003006";
		url = "https://go.seoul.co.kr/news/newsView.php?id=20150518011010";

		test.changeHtml(getProductDetailPage(url), url);		
	}
	
	public String changeHtml(String htmlSrc, String url2) {
		test_Seoul commonUtil = new test_Seoul();
		
		
		
		// 0 depth
		String searchList =  "금융상품'|금융서비스'|소비'|금리'|예금'|적금'|펀드체크카드"; 
		String[] searchArr = searchList.split("'\\|");
		String encodeResult = "";
		
		
		if(url2.contains("search")) {
		String linkList ="<!-- 리스트 시작 -->\n";
//		System.out.println("Start: " + Start);
		//페이지가 15 단위로 이어짐
		for(int page = 1; page < 2; page=page+1) {
			for(int i=0; i < searchArr.length; i++ ) {
				++ cnt;
				String keyword = searchArr[i];
				String url = "https://search.seoul.co.kr/index.php?keyword="+keyword+"&pageNum="+page;
//				System.out.println("keyword: " + keyword);

//				System.out.println("url: " + url);
//	
				
				String htmlList=common.getProductDetailPage(url, "UTF-8").replaceAll(" ", "").replaceAll("\n", "").replaceAll("		", "");
		
//				System.out.println("htmlList"+htmlList);
			
				Pattern pt1=Pattern.compile("");
				Matcher mc1=pt1.matcher("");
				
				pt1=Pattern.compile("<ddclass=\"thumb\"><ahref=\"(.*?)\"");
				
				mc1=pt1.matcher(htmlList);
			
				
				while(mc1.find()) {
					String realUrl="";
					String findnhtml ="";
					try {
						URL urlTest = new URL(mc1.group(1).replace("http","https"));
						HttpURLConnection conn;
						conn=(HttpURLConnection) urlTest.openConnection();
						conn.setRequestMethod("GET");
						conn.setRequestProperty("charset", "utf-8");
						int responseCode=conn.getResponseCode();
//						System.out.println("리스폰 코드: "+responseCode);
						if( responseCode == HttpsURLConnection.HTTP_OK )
				        {
							String test001 = mc1.group(1);
							if(!test001.contains("https")) {
								test001 = test001.replace("http", "https");
							}
							findnhtml = common.getProductDetailPage(test001, "UTF-8");
				        }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Pattern pt2=Pattern.compile("");
					Matcher mc2=pt2.matcher("");
					
					pt2=Pattern.compile("<link rel=\"canonical\" href=\"(.*?)\"");
					
					mc2=pt2.matcher(findnhtml);
					while(mc2.find()) {
						realUrl+=mc2.group(1);
//						System.out.println(">>>"+realUrl);
					}
					
					//정규식으로 짜고 섭트링
					if(realUrl.contains("seoul")) {
						
						linkList += " <a href =\"" + mc1.group(1) + "\">"+keyword+"</a>\n";
					}
				}

			}
		}
		linkList += "<!-- 리스트 끝 -->";
		htmlSrc=linkList;
		
//		System.out.println(htmlSrc);
		
		}
		else {
			
			if(url2.contains("go.seoul.co.kr/")){ //서울 pn 사이트 일경우
				
//				System.out.println("나온다 나와!!!!!!!!!!!!!!!!!!!!!");
				String FORUM_CONTENTS = "";		//내용
				String FORUM_TITLE = "";		//제목
				String FORUM_DATE = "";		//날짜
				
				Pattern pt1=Pattern.compile("");
				Matcher mc1=pt1.matcher("");                                                                
				
				
				//제목
				FORUM_TITLE = commonUtil.getSubStringResult("<title>", "</title>", htmlSrc);
				
				//컨텐츠
				FORUM_CONTENTS = commonUtil.getSubStringResult("<!-- ���� �� -->", "<!-- //기사 본문 :: end -->", htmlSrc);
				
				
				
				
				String FORUM_DATE_temp = commonUtil.getSubStringResult("<span style=\"margin-top:5px;float:left;color:#888888\" itemprop=\"datePublished\" content=\"", "\">", htmlSrc);
				try {
					FORUM_DATE=formatStrUTCToDateStr(FORUM_DATE_temp);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				
				htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
				htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";	
				htmlSrc +="<FORUM_DATE>"+FORUM_DATE+"</FORUM_DATE>";

				
				System.out.println("FORUM_TITLE: " + FORUM_TITLE);
				System.out.println("FORUM_CONTENTS: " + FORUM_CONTENTS);
				System.out.println("FORUM_DATE: " + FORUM_DATE);

			}
			
			else if (url2.contains("en.seoul.co.kr/")){//서울 en일 경우
				
				String FORUM_CONTENTS = "";		//내용
				String FORUM_TITLE = "";		//제목
				String FORUM_DATE = "";		//날짜
				
				Pattern pt1=Pattern.compile("");
				Matcher mc1=pt1.matcher("");                                                                 
				
				
				//제목
				FORUM_TITLE = commonUtil.getSubStringResult("<title>", "</title>", htmlSrc);
				//System.out.println("제목:"+FORUM_TITLE);
				//컨텐츠
				FORUM_CONTENTS = commonUtil.getSubStringResult("<!-- ���� �� -->", "</div>", htmlSrc);
				
				
//				//작성일
				String FORUM_DATE_temp = commonUtil.getSubStringResult("<span style=\"margin-left:10px;margin-top:7px;float:left;color:#888888;font-size:11px;\"  itemprop=\"datePublished\" content=\"", "\">", htmlSrc);
				try {
					FORUM_DATE=formatStrUTCToDateStr(FORUM_DATE_temp);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
				htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";	
				htmlSrc +="<FORUM_DATE>"+FORUM_DATE+"</FORUM_DATE>";

				
				System.out.println("FORUM_TITLE: " + FORUM_TITLE);
				System.out.println("FORUM_CONTENTS: " + FORUM_CONTENTS);
				System.out.println("FORUM_DATE: " + FORUM_DATE);
				
			}
			
			else if (url2.contains("culture.seoul.co.kr")){//서울 컬쳐 일 경우
				
				String FORUM_CONTENTS = "";		//내용
				String FORUM_TITLE = "";		//제목
				String FORUM_DATE = "";		//날짜
				
				Pattern pt1=Pattern.compile("");
				Matcher mc1=pt1.matcher("");                                                                
				
				
				//제목
				FORUM_TITLE = commonUtil.getSubStringResult("<title>", "</title>", htmlSrc);
				
				//컨텐츠
				FORUM_CONTENTS = commonUtil.getSubStringResult("<div id=\"\" class=\"articleDiv\">", "<script>", htmlSrc);
				
				
				//작성일
				String FORUM_DATE_temp = commonUtil.getSubStringResult("<div class=\"articleDnF\"><span class=\"articleDay\">입력: ‘", " /  수정:", htmlSrc);
				SimpleDateFormat sdf= new SimpleDateFormat("yy-MM-dd HH:mm");
				SimpleDateFormat sdf2= new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Date date=new Date();
				
				try {
					date=sdf.parse(FORUM_DATE_temp);
					FORUM_DATE=sdf2.format(date);
					
					System.out.println("FORUM_DATE>>>>>>>>"+FORUM_DATE);
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}

				
				htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
				htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";	
				htmlSrc +="<FORUM_DATE>"+FORUM_DATE+"</FORUM_DATE>";

				
				System.out.println("FORUM_TITLE: " + FORUM_TITLE);
				System.out.println("FORUM_CONTENTS: " + FORUM_CONTENTS);
				System.out.println("FORUM_DATE: " + FORUM_DATE);
			}
			
			else { //일반 서울 신문일 경우 
			String FORUM_CONTENTS = "";		//내용
			String FORUM_TITLE = "";		//제목
			String FORUM_DATE = "";		//날짜
			
			Pattern pt1=Pattern.compile("");
			Matcher mc1=pt1.matcher("");                                                                
			
			
			//제목
			FORUM_TITLE = commonUtil.getSubStringResult("<title>", "</title>", htmlSrc);
			
			//컨텐츠
			FORUM_CONTENTS = commonUtil.getSubStringResult("<div class=\"S20_v_article\" id=\"atic_txt1\" itemprop=\"articleBody\">", "<!--//기사 본문 :::end-->", htmlSrc);
			
			
			//작성일
//			FORUM_DATE = commonUtil.getSubStringResult("<div class=\"S20_VCdate\">", "</div>", htmlSrc);

			Pattern pt2=Pattern.compile("");
			Matcher mc2=pt2.matcher("");
			
//			System.out.println(htmlSrc);
//			
			pt2=Pattern.compile("<span itemprop=\"datePublished\" content=\".*?\">([0-9]{4}.[0-9]{0,2}.[0-9]{0,2})");//매일경제 날짜 정규식
			
			mc2=pt2.matcher(htmlSrc);
			
			while(mc2.find()) {
				FORUM_DATE +=  mc2.group(1);
			}
			
			System.out.println("변경전3" + FORUM_DATE);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
			LocalDate date = LocalDate.parse(FORUM_DATE, formatter);
			FORUM_DATE=date.toString();
//			System.out.println(date);
//			System.out.println("변경후" + FORUM_DATE);

			
			htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
			htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";	
			htmlSrc +="<FORUM_DATE>"+FORUM_DATE+"</FORUM_DATE>";
			

			
			System.out.println("FORUM_TITLE: " + FORUM_TITLE);
			System.out.println("FORUM_CONTENTS: " + FORUM_CONTENTS);
			System.out.println("FORUM_DATE: " + FORUM_DATE);
			}

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
	private String formatStrUTCToDateStr(String fORUM_DATE) throws ParseException {
		System.out.println("fORUM_DATE>>>>>>>>"+fORUM_DATE);
		String dateTime=fORUM_DATE.replace("T", " ").substring(0, 19);
		
		
	    return dateTime;
		
	}
	
	
}
