package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extension.CommonUtil;

public class Gugmin {
	static int cnt = 0;
	static int page = 0;
	CommonUtil common = new CommonUtil();
	//static NaverOpenApiNews naverApiNews = new NaverOpenApiNews();
	
	public static void main(String[] args) {
	
		//naverApiNews = new NaverOpenApiNews();
	
		Gugmin test = new Gugmin();
		
		List<String> urlList = new ArrayList<String>();
		//navi
		for (int i = 0; i < 1; i++) {
			urlList.add("http://www.kmib.co.kr/search/searchResult.asp?query=정치'|경제'|사회'|문화&p=*");
		}
		
		//0depth
		for (int i = 0; i < urlList.size(); i++) {
			test.changeHtml(getProductDetailPage(urlList.get(i)), urlList.get(i));
		}	
		
		//1depth
		String url = ""; 
//		url = "https://www.donga.com/news/article/all/20220412/112837367/1";
//		url = "http://news.kmib.co.kr/article/viewDetail.asp?newsClusterNo=01100201.20070715100001840";
		url = "https://news.kmib.co.kr/article/view.asp?arcid=0017061457&code=61111111&sid1=pol";



//		test.changeHtml(getProductDetailPage(url), url);		
	}
	
	public String changeHtml(String htmlSrc, String url2) {
		Gugmin commonUtil = new Gugmin();
		
		
		
		// 0 depth
		String searchList =  "정치'|경제'|사회'|문화"; 
//		String[] searchArr = searchList.split("'\\|");
		String encodeResult = "";
		
		
		if(url2.contains("search")) {
			String linkList ="<!-- 리스트 시작 -->\n";
			if(!searchList.equals("")) {
			
			String[] searchArr = searchList.split("'\\|");
			String keyword = "";
			

			System.out.println("@@@@@@@@@@@@@@@@@@@@1");
			for(int i=0; i < searchArr.length; i++ ) {
				System.out.println("@@@@@@@@@@@@@@@@@@@@2");
				keyword = searchArr[i];
				String urlKeyword = "";
				urlKeyword = convertUnicode(keyword).replace("\\", "%");
				
//				String url = "http://www.kmib.co.kr/search/searchResult.asp?searchWord="+urlKeyword+"&pageNo=1";
				String url = "https://www.kmib.co.kr/search/searchResult.asp?searchWord="+urlKeyword;

				System.out.println(url);
				String htmlList=common.getProductDetailPage(url, "UTF-8").replaceAll(" ", "").replaceAll("\n", "");

				System.out.println(htmlList);
				Pattern pt1=Pattern.compile("");
				Matcher mc1=pt1.matcher("");
				
				pt1=Pattern.compile("<dtclass=\'tit\'><ahref=\'(.*?)\'");
				mc1=pt1.matcher(htmlList);
				
				
				
				
				
				while(mc1.find()) {
					linkList += "<a href =\"" + mc1.group(1) + "\">"+keyword+"</a>\n";
				}
			}
		
		linkList += "<!-- 리스트 끝 -->";
		htmlSrc=linkList;
		
		System.out.println(htmlSrc);
	}
}else {
			System.out.println("@@@@");
			String FORUM_CONTENTS = "";		//내용
			String FORUM_TITLE = "";		//제목
			String FORUM_DATE = "";		//날짜
			
			Pattern pt1=Pattern.compile("");
			Matcher mc1=pt1.matcher("");                                                                
			
			
			//제목
			FORUM_TITLE = commonUtil.getSubStringResult("<title>", "</title>", htmlSrc);
			
			//컨텐츠
			FORUM_CONTENTS = commonUtil.getSubStringResult("<!-- 기사 내용 -->", "<a href=\"\"http://www.kmib.co.kr\"\" target=\"\"_blank\"\">", htmlSrc);
			
			
			//작성일
			FORUM_DATE = commonUtil.getSubStringResult("<span class=\"t11\">", "</span>", htmlSrc);
//			System.out.println("변경전" + FORUM_DATE);
			//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US);
			//LocalDate date = LocalDate.parse(FORUM_DATE, formatter);
			//FORUM_DATE=date.toString();

			
			htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
			htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";	
			htmlSrc +="<FORUM_DATE>"+FORUM_DATE+"</FORUM_DATE>";
			

			
//			System.out.println("FORUM_TITLE: " + FORUM_TITLE); 
//			System.out.println("FORUM_CONTENTS: " + FORUM_CONTENTS);
//			System.out.println("FORUM_DATE: " + FORUM_DATE);

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

	private static String convertUnicode(String val) {
		// 변환할 문자를 저장할 버퍼 선언
		StringBuffer sb = new StringBuffer();
		// 글자를 하나하나 탐색한다.
		for (int i = 0; i < val.length(); i++) {
		// 글자 추츨 int값으로 가져온다.
			int code = val.codePointAt(i);
			// 128이하면 ascii코드로 변환하지 않는다.
				if (code < 128) {
				sb.append(String.format("%c", code));
				} else {
				// 16진수 유니코드로 변환한다.
				sb.append(String.format("\\u%04x", code));
				}
			}
			// 결과 리턴
			return sb.toString();
		}
}
