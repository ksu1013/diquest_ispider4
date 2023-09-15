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

public class tjb_sample {
	static int cnt = 0;
	static int page = 0;
	CommonUtil common = new CommonUtil();
	//static NaverOpenApiNews naverApiNews = new NaverOpenApiNews();
	
	public static void main(String[] args) {
	
	
		tjb_sample test = new tjb_sample();
		
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
		url = "http://www.tjb.co.kr/news05/bodo/view/id/57531/version/1";

		test.changeHtml(getProductDetailPage(url), url);		
	}
	
	public String changeHtml(String htmlSrc, String url2) {
		
		tjb_sample commonUtil = new tjb_sample();
		String FORUM_CONTENTS = "";		//내용
		String FORUM_TITLE = "";		//제목
		String FORUM_DATE = "";		//날짜
		String FORUM_TYPE = "중앙지"; // 타입
		String FORUM_MENU = ""; // 메뉴
		String FORUM_DATE_temp2="";
		
		
		// 0 depth
		String searchList =  "eNortjKzUnqzYOqbnTOUrAFcJ2kF-w..'|eNortjKzUnq1ae-bBXOUrAEobwYe'|eNortjKzUnrTtObtrB4la1wwJoYFyg..'|eNortjKzUnqzbc7bnglK1lwwXCdmBeQ."; 
		String[] searchArr = searchList.split("'\\|");
		
			
		
		if(url2.contains("search")) {
			String linkList ="<!-- 리스트 시작 -->\n";
			for(int page = 1; page < 2; page=page+1) {
				for(int i=0; i < searchArr.length; i++ ) {
					
					String keyword = searchArr[i];
						
					String url = "http://www.tjb.co.kr/site-search/site/search/keyword/"+keyword+"/type/eNortjK0UjJSsgZcMAkbAco./page/"+page;
					
					
					
					String htmlList=common.getProductDetailPage(url, "UTF-8").replaceAll(" ", "").replaceAll("\n", "");
					
					String htmlList2=commonUtil.getSubStringResult("<h2>오늘의주요뉴스</h2>", "<divclass=\"paging\">", htmlList);
					
			
				
					Pattern pt1=Pattern.compile("");
					Matcher mc1=pt1.matcher("");
					
					pt1=Pattern.compile("<ahref=\"(.*?)\"");
					
					mc1=pt1.matcher(htmlList2);
					
				
					
					
					
					while(mc1.find()) {
						linkList += "<a href =\"" + mc1.group(1) + "\">"+keyword.replace("eNortjKzUnqzYOqbnTOUrAFcJ2kF-w..", "정치").replace("eNortjKzUnq1ae-bBXOUrAEobwYe", "경제").replace("eNortjKzUnrTtObtrB4la1wwJoYFyg..", "사회")
								.replace("eNortjKzUnq9ZsfbmVOUrAEoXDAGBg..", "문화").replace("eNortjKzUnrTsuPV5hYla1wwXCcZBeY.", "세계").replace("eNortjKzUnq1de2bBXOUrAEoJAYR", "국제").replace("eNortjKzUnqzvOHN9LVK1lwwJtMF4A..", "지역")
								.replace("eNortrK0UnrTteRt_5o3OxYoWQNcMEDOCCY.", "스포츠").replace("eNortjKyUvIMUbIGXDAL5wI2", "IT").replace("eNortjKzUnq1ec_bqTOVrAEoQAYR", "과학").replace("eNortjI0slJ6M2PJ2yl7Xnd3vJm2Q8kaXDBkkwpn", "오피니언")
								.replace("eNortjKzUno7rfPNgqlK1lwwJnYFyg..", "행정").replace("eNortjKzUnozc8errROUrAFcJ9gGCQ..", "외교").replace("eNortjKzUnrTsuDVxg1K1lwwXCcFBfg.", "선거").replace("eNortjKzUnozb8Kb2ROUrAEmrQXO", "자원")
								.replace("eNortrK0Unq9reF1_8w3TRuUrAFADQfy", "부동산").replace("eNortjKzUnq1o-PNnK1K1lwwXCeWBgQ.", "금융").replace("eNortrK0Unozb83b1p63jWuUrAFAJQfu", "재태크").replace("eNortrK0Unozb8Lr_plvNqxQsgZcMEBYCBE.", "자동차")
								.replace("eNortrK0Unq9Ycbr_pY3m7YoWQNcMED3CCM.", "반도체").replace("eNortjKzUnrTtOHN9FYla1wwJngFww..", "산업").replace("eNortjKzUnq1Y8Ob6a1K1lwwXCfiBfc.", "기업").replace("eNortjKzUnq9Zsub6WuVrAEoJgYY", "무역")
								.replace("eNortjKzUnrTvuft1IlK1lwwXCcEBd8.", "쇼핑").replace("eNortjKzUnqzbO6rbT1K1lwwXCeJBfg.", "증권").replace("eNortjKzUnqzbO6b7jlK1lwwXCcXBd8.", "증시").replace("eNortjKzUnozc8fbmTOUrAFcJ48F-A..", "외환").replace("eNortjKzUnqzYe-b6a1K1lwwKAgF_g..", "창업")
								.replace("eNortjKzUnqzfcWb6a1K1lwwXCe7BfA.", "취업").replace("eNortjKzUnozZ8Hbtq1K1lwwXCcfBe0.", "유통").replace("eNortjI0slJ6tXXtmwVzXm3aCySVrAFnxAq_", "국제경제").replace("eNortjKzUnozd8brxT1K1lwwJuUF2A..", "의료").replace("eNortjKzUnq1ccurDVOVrAEoUwYb", "건강")
								.replace("eNortjKzUno7c8arTXuVrAFcJ5sGFA..", "환경").replace("eNortjKzUnrTtObVxi1K1lwwXCdLBgY.", "사건").replace("eNortjKzUnrTtObV5gVK1lwwXCcXBfQ.", "사고")
								.replace("eNortjKzUnozfc2blo1K1lwwXCcrBe0.", "여성").replace("eNortrK0Unozb-mbqQvezN2hZA1cMEFVCDE.", "장애인").replace("eNortjKzUnrdtODNlBVK1lwwJm0F0g..", "날씨").replace("eNortjKzUnrduuN1_0wla1wwJswF2A..", "노동").replace("eNortjKzUnq9eeub5Q1K1lwwKBYGAw..", "복지")
								.replace("eNortrK0Unq9fsfrKVPeTNuiZA1cMEIgCDs.", "미디어").replace("eNortjKzUnq1dcKbOQuVrAFcJ3UF9Q..", "교육").replace("eNortjKzUnrTPeft9BlK1lwwJn0FzA..", "시험").replace("eNortjKzUnozo_HtzClK1lwwJjIFvA..", "영화").replace("eNortjI0slJ6vWbH25lT3szd83rDDCVrXDBnBAqr", "문화일반")
								.replace("eNortjKzUnq9fsebjgVK1lwwXCfwBgM.", "미술").replace("eNortjKzUnq1ccubbVOVrAEodQYj", "건축").replace("eNortjKzUnozt-fN1FYla1wwJlUFuA..", "음악")
								.replace("eNortjKzUnq9YeWbtoVK1lwwXCeYBfQ.", "방송").replace("eNortjKzUno7deabjgVK1lwwJpAFzA..", "학술").replace("eNortjKzUnqzqPXV1glK1lwwJuUF3w..", "종교").replace("eNortjKzUnozfc3baZ1K1lwwXCdcMAXY", "여행").replace("eNortjKzUnqzoOVN9xwla1wwJlcFwA..", "전시")
								.replace("eNortjKzUnrTPPftzDlK1lwwJl8Fyw..", "생활").replace("eNortjKzUnqzbc7bnglK1lwwXCdmBeQ.", "출판").replace("eNortjKzUnqzbeXrphVK1lwwXCfKBf0.", "충남").replace("eNortjKzUnqzbeWbTWuVrAEongYz", "충청").replace("eNortjI0slJ6s23lm01rXzeteN3fomQNXDBmswpz", "충청남도")
								.replace("eNortrK0Unozd8fblg2vW7YrWQNcMEF7CCg.", "인터넷").replace("eNortrK0Unqzd8bb1glvdixQsgZcMEFyCFwn", "콘텐츠").replace("eNortrK0Unq9asXrDVPezN2jZA1cMEJWCFE.", "모바일")
								.replace("eNortjKzUnq9ecubqR1K1lwwXCfgBfg.", "보안")+"</a>\n";
						
					}
					
				}
			}
			linkList += "<!-- 리스트 끝 -->";
			
			htmlSrc=linkList;
			
			System.out.println(htmlSrc);
		
		
	}else {
			System.out.println("@@@@");
			
			//제목
			FORUM_TITLE = commonUtil.getSubStringResult("<th scope=\"row\"><label for=\"title\">제목</label></th>", "</td>", htmlSrc);
			
			//컨텐츠
			FORUM_CONTENTS = commonUtil.getSubStringResult("<th scope=\"row\"><label for=\"contents\">내용</label></th>", "<br /><br />TJB 대전방송", htmlSrc);
			
			
			//작성일
			String FORUM_DATE_tmep=commonUtil.getSubStringResult("<th scope=\"row\"><label for=\"title\">작성자</label></th>", "</td>", htmlSrc);
			
			Pattern pt1=Pattern.compile("");
			Matcher mc1=pt1.matcher("");
			
			pt1=Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2})");
			
			mc1=pt1.matcher(FORUM_DATE_tmep);
		
			
			while(mc1.find()) {
				FORUM_DATE_temp2 += mc1.group(1);
			}
			
			FORUM_DATE=PickUpDate(FORUM_DATE_temp2);
			
			
			
			htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
			htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";	
			htmlSrc +="<FORUM_DATE>"+FORUM_DATE+"</FORUM_DATE>";
			htmlSrc +="<FORUM_TYPE>"+FORUM_TYPE+"</FORUM_TYPE>";
			
			
		System.out.println("FORUM_TITLE: " + FORUM_TITLE); 
		System.out.println("FORUM_CONTENTS: " + FORUM_CONTENTS);
		System.out.println("FORUM_DATE: " + FORUM_DATE);
		System.out.println("FORUM_TYPE: " + FORUM_TYPE);

		
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
		Pattern DatePattern = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2})");
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
		System.out.println("ParsingData>>>>>>>>>"+ParsingData);
		return ParsingData;
	}

}
