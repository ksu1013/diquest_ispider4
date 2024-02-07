package NaverBlog;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import extension.CommonUtil;
import extension.NaverOpenApiNews;


public class test_NAVER_BLOG{
	
	static int cnt = 0;
	static int page = 0;
	CommonUtil common = new CommonUtil();
	static NaverOpenApiNews naverApiNews = new NaverOpenApiNews();
	
	public static void main(String[] args) {
	
		naverApiNews = new NaverOpenApiNews();
	
		test_NAVER_BLOG test = new test_NAVER_BLOG();
		
		List<String> urlList = new ArrayList<String>();
		//navi
		for (int i = 0; i < 2; i++) {
//			urlList.add("https://openapi.naver.com/v1/search/blog?query=금융상품'|금융서비스'|소비'|금리'|예금'|적금'|펀드체크카드&display=10&start=1");
			urlList.add("https://openapi.naver.com/v1/search/blog?query=MMDA&display=10&start=1");
//			urlList.add("https://openapi.naver.com/v1/search/blog?query=우체국 금융'|우체국 예금'|우체국 보험'|우체국 펀드'|우체국 체크카드'|우체국 카드'|금융 신상품'|금융 특판'|금융 출시'|예금 신상품'|예금 특판'|예금 출시'|보험 신상품'|보험 특판'|보험 출시'|펀드 신상품'|펀드 특판'|펀드 출시'|체크카드 신상품'|체크카드 특판'|체크카드 출시'|카드 신상품'|카드 특판'|카드 출시'|저축예금'|정기예금'|자유적금'|퇴직적음'|MMDA'|사망보험'|상해보험'|어린이보험'|종신보험'|실손보험'|암보험'|연금보험'|보장성보험'|저축성보험'|체크카드'|지역화폐카드&display=10&start=1");
		}
		
		//0depth
		for (int i = 0; i < urlList.size(); i++) {
			test.changeHtml(getProductDetailPage(urlList.get(i)), urlList.get(i));
		}
		
		//1depth
		String url = ""; 

		String OUTPUT_FILE_PATH  = "C:\\post-offic\\finish_dqdoc\\%s";
		
		String outputFileName = "NAVER_NEWS";
		String outputFile 	= String.format(OUTPUT_FILE_PATH, outputFileName);
		
		System.out.println(outputFile);
		
		
//		url = "https://news.naver.com/main/read.naver?mode=LSD&mid=sec&sid1=101&oid=008&aid=0004725188";
//		url = "https://news.naver.com/main/read.naver?mode=LSD&mid=sec&sid1=101&oid=421&aid=0006008313";
//		url = "https://blog.naver.com/PostView.naver?blogId=incruit1&logNo=10155943861";
//		url = "https://blog.naver.com/PostView.naver?blogId=jha409&logNo=222456896414";
//		url = "https://biz.chosun.com/topics/topics_social/2022/04/06/F5CIAJALVZFOREDAHKSPANUO3M/";
//		url = "https://www.chosun.com/economy/economy_general/2022/03/28/6SKMV3OUQNDSRDR5VPVSPUEHXY/";
		url = "https://blog.naver.com/PostView.naver?blogId=katherineragsdale&logNo=222719414219";
		url = "https://blog.naver.com/PostView.naver?blogId=sadgoose501&logNo=222715982891";
		url = "https://blog.naver.com/PostView.naver?blogId=lazyrabbit507&logNo=222747341214";
		url = "https://blog.naver.com/PostView.naver?blogId=blackbutterfly809&logNo=222808858351";
		url = "https://blog.naver.com/PostView.naver?blogId=sadbird642&logNo=222807993395";
//		test.changeHtml(getProductDetailPage(url), url);		
	}
	
	public String changeHtml(String htmlSrc, String url2) {
		test_NAVER_BLOG commonUtil = new test_NAVER_BLOG();
		// 0 depth
		if(url2.contains("openapi")) {
			System.out.println(url2);
			String searchList =  commonUtil.getSubStringResult("query=", "&display", url2); 
			String[] searchArr = searchList.split("'\\|");
			
			System.out.println(searchList);
			System.out.println(searchArr.length);
			String Start = url2.substring(url2.indexOf("start=")+6);
			
			String linkList ="<!-- 리스트 시작 -->\n";
//			System.out.println("Start: " + Start);
			

			String encodeResult = "";
			
			int num = 1;
			for(int z=0; z < 1; z++) {
				
				for(int i=0; i < searchArr.length; i++ ) {
					
					String keyword = searchArr[i];
					try {
						encodeResult = URLEncoder.encode(keyword, "UTF-8");
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					

					
					System.out.println("keyword: " + keyword); 
//					System.out.println("encodeResult: " + encodeResult);
					
					String jsonList = naverApiNews.index(keyword,"blog", num, "date");
					
					JSONParser parser = new JSONParser();
					
					Object obj1 = null;
					
					try {
						obj1 = parser.parse(jsonList);
						JSONObject jsonObj = (JSONObject) obj1;
						JSONArray bodyArray;
						
						bodyArray = (JSONArray) jsonObj.get("items");
//						TimeUnit.SECONDS.sleep(1);
						TimeUnit.MILLISECONDS.sleep(200);
						System.out.println(bodyArray.size());
						
						for(int j=0; j < bodyArray.size(); j++) {
							JSONObject returnSubject = (JSONObject) bodyArray.get(j);
							String returnLink= String.valueOf(returnSubject.get("link"));
							String returnDescription = String.valueOf(returnSubject.get("description"));
							String returnTitle = String.valueOf(returnSubject.get("title"));
								
							System.out.println("[DQ-LINK] >>>> " + returnLink);
							if(returnLink.contains("blog.naver.com")) {
								String id = commonUtil.getSubStringResult("blog.naver.com/", "?", returnLink);
								String logNo = returnLink.substring(returnLink.indexOf("logNo=")+6);
								Pattern pt1 = Pattern.compile("blog.naver.com/(.*?)/(.*)");
								Matcher mc1 = pt1.matcher(returnLink);
								if(mc1.find()) {
									returnLink = "https://blog.naver.com/PostView.naver?blogId="+mc1.group(1)+"&logNo="+mc1.group(2);
								}else {
									returnLink = "https://blog.naver.com/PostView.naver?blogId="+id+"&logNo="+logNo ;									
								}
								
								if(returnDescription.contains(keyword) && returnTitle.contains(keyword)) {
									cnt ++;
									linkList += "cnt:" + cnt + " <a href=\"" + returnLink + "\">" +keyword +"</a>\n";									
								}
							}
						}
						
						
						htmlSrc = linkList;
						
					} catch (ParseException e) {
						e.printStackTrace();
						System.out.println(e);
					} catch (InterruptedException e) {
						System.err.format("IOException: %s%n", e);
					}
					
				}
				
				num += 1;
			}
			linkList += "<!-- 리스트 끝 -->";
			System.out.println(linkList);
		}else {
			
			String FORUM_TITLE = "";		//주제
			String FORUM_CONTENTS = "";		//내용
			String FORUM_DATE = "";			//주제

			String regex = ""; 
			
			htmlSrc = common.getProductDetailPage(url2, "UTF-8");
			
			//제목
			regex = "<meta property=\"og:title\".*?content=\"(.*?)\"";
			
			Pattern pt1 = Pattern.compile(regex);
			Matcher mc1 = pt1.matcher(htmlSrc);
			if(mc1.find()) {
				FORUM_TITLE= mc1.group(1).replace("&#8230;", "…");
			}else {
				FORUM_TITLE =  commonUtil.getSubStringResult("\"og:title\" content=\"", "\"/>", htmlSrc);
			}
		
			//내용 
			String preStr = "";
			String postStr = "";
			
			
			preStr = "_postViewArea" + url2.substring(url2.indexOf("logNo=")+6) +"\">";
			postStr = "<div id=\"post_footer_contents\" class=\"post_footer_contents\">";
			
			FORUM_CONTENTS =  commonUtil.getSubStringResult(preStr, postStr, htmlSrc);
			
			
			//날짜 
			if(htmlSrc.contains("se_publishDate pcol2")) {
				regex = "<span class=\"se_publishDate pcol2\">([0-9]{4}\\. [0-9]{0,2}\\. [0-9]{0,2})\\.";
			}else if(htmlSrc.contains("date fil5 pcol2 _postAddDate")) {
				regex = "<p class=\"date fil5 pcol2 _postAddDate\">([0-9]{4}\\. [0-9]{0,2}\\. [0-9]{0,2})\\.";
			}else {
				regex = "meta property=\"og:image\".*?pstatic.net\\/(.*?)_";
			}
			
			pt1 = Pattern.compile(regex); 
			mc1 = pt1.matcher(htmlSrc);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
//			if(mc1.find()) {
//				FORUM_DATE=mc1.group(1).replaceAll(" ", "");
//				if(!FORUM_DATE.contains(".")) {
//					FORUM_DATE = common.CommonPattern(FORUM_DATE, "yyyyMMdd", Locale.US);					
//				}
//			}
//			else { 
//				Date now = new Date();
//				FORUM_DATE = dateFormat.format(now);
//			}
	
			
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
	
}
