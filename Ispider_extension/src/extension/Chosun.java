package extension;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.diquest.ispider.common.conf.Configuration;
import com.diquest.ispider.common.conf.Reposit;
import com.diquest.ispider.common.save.structure.Row;
import com.diquest.ispider.core.collect.DqPageInfo;
import com.diquest.ispider.core.runnable.Extension;

public class Chosun implements Extension {
	private CommonUtil common;

	private String query = ""; // 검색 키워드
	private int p;// 페이징
	private String URL;	
	private String REG_DATE;	
	private String checkdate;	
	private String MENU;	
	private String KEYWORD;	
	private String aTagUrl ="https://biz.chosun.com/stock/finance/2022/05/31/NKZCBCD5IBDGZEBZNUGAHAI6ZQ/";

	
	@Override
	public void startExtension(DqPageInfo dqPageInfo, String homePath) {
		Reposit reposit = Configuration.getInstance().getBbsReposit(dqPageInfo.getBbsId());
		
		/* 공통 선언 start */
		common = new CommonUtil();
		URL = reposit.getNodeByColumnName("CONTS_URL").getId();
		//REG_DT = reposit.getNodeByColumnName("REG_DT").getId();
		REG_DATE = reposit.getNodeByColumnName("DATE").getId();
		MENU = reposit.getNodeByColumnName("MENU").getId();
		KEYWORD = reposit.getNodeByColumnName("KEYWORD").getId();
		/* 공통 선언 end */
//		String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/chosun/file.txt";
		String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/chosun/file.txt";
        File file = new File(filePath); // File객체 생성
	        if(file.exists()){ // 파일이 존재하면
	            BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	            String line = null;
	            try {
					while ((line = reader.readLine()) != null){
						checkdate=line;
					}
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	        }
}

	@Override
	public void endExtension(DqPageInfo dqPageInfo) {
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter2= new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String StartDate = formatter2.format(cal.getTime());
		//String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/chosun/file.txt";
		String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/chosun/file.txt";
        File file = new File(filePath); // File객체 생성
        if(!file.exists()){ // 파일이 존재하지 않으면
            try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // 신규생성
        }else {
        	file.delete();
        	try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
        
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(StartDate);
	        writer.newLine();
	        writer.flush(); // 	        
	        writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String changeRequestURL(String url, DqPageInfo dqPageInfo) {
		if (url.contains("query")) {
			query = common.getSubStringResult("query=", "&p", url);
			p = Integer.parseInt(url.substring(url.indexOf("p=") + 2));
			url = "https://www.chosun.com/";
		}
		return url;

	}

	@Override
	public void changeRowValue(Row row, DqPageInfo dqPageInfo) {
		row.getNode(MENU).setValue(row.getNode(KEYWORD).getValue().replace("^DQ", " "));
	}

	@Override
	public String changeHtml(String htmlSrc, DqPageInfo dqPageInfo) {
		CommonUtil commonUtil = new CommonUtil();
		if (dqPageInfo.getParentUrl() == null) {
			String linkList = "<!-- 리스트 시작 -->\n";

			if (!query.equals("")) {
				String[] searchArr = query.split("'\\|");
				String encodeResult = "";
				String keyword = "";

				for (int i = 0; i < searchArr.length; i++) {
					keyword = searchArr[i];
					try {
						encodeResult = URLEncoder.encode(keyword, "UTF-8").replaceAll("%", "%25");

						String url = "https://www.chosun.com/pf/api/v3/content/fetch/search-param-api?query=%7B\"page\"%3A"+ p + "%2C\"query\"%3A\"" + encodeResult + "\"%2C\"writer\"%3A\"\"%7D&&_website=chosun";

						JSONParser parser = new JSONParser();

						Object obj1 = null;

						String jsonList = common.getProductDetailPage(url, "UTF-8");

						obj1 = parser.parse(jsonList);

						JSONObject jsonObj = (JSONObject) obj1;
						JSONArray bodyArray;

						bodyArray = (JSONArray) jsonObj.get("content_elements");


						for (int j = 0; j < bodyArray.size(); j++) {
							JSONObject returnSubject = (JSONObject) bodyArray.get(j);
							String returnLink = String.valueOf(returnSubject.get("site_url"));


							linkList += "<a href=\"" + returnLink + "\">" + keyword + "</a>\n";
						}
						htmlSrc = linkList;

						htmlSrc += "<KEYWORD>" + keyword + "</KEYWORD>";// 키워드 추가
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			linkList += "<!-- 리스트 끝 -->";
			htmlSrc = linkList;

		} else {
			String FORUM_CONTENTS = ""; // 내용
			String FORUM_TITLE = ""; // 제목
			String FORUM_DATE = ""; // 날짜
			String FORUM_MENU = ""; // 메뉴
			String FORUM_TYPE = "중앙지"; // 타입
			String FORUM_DATE_temp="";
			
			String contentTemp="";

			Pattern pt1 = Pattern.compile("");
			Matcher mc1 = pt1.matcher("");
			
			if(dqPageInfo.getUrl().contains("http://kid.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!-- 내용 -->", "<!-- // 내용 -->", htmlSrc);
				FORUM_DATE_temp = commonUtil.getSubStringResult("<span class=\"date\">\r\n" + 						"    입력 :", "</span>", htmlSrc);
				FORUM_DATE=PickUpDate(FORUM_DATE_temp);
				FORUM_CONTENTS=contentTemp;
			}else if((dqPageInfo.getUrl().contains("http://realty.chosun.com/"))||dqPageInfo.getUrl().contains("https://realty.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<div class=\"par\">", "<!-- #include \"common/article_tags.common\" -->", htmlSrc);
				String FORUM_DATE_temp2 = commonUtil.getSubStringResult("<p id=\"date_text\">", "<h3 class=\"news_subtitle\">", htmlSrc);
				pt1 = Pattern.compile("(입력 : [0-9]+\\.[0-9]+\\.[0-9]+\\ [0-9]+\\:[0-9]+)");

				mc1 = pt1.matcher(FORUM_DATE_temp2);
				
				if (mc1.find()) {
					FORUM_DATE_temp += mc1.group(1);
				}
				FORUM_DATE=PickUpDate(FORUM_DATE_temp);

				FORUM_CONTENTS=contentTemp;
				
			}else if (dqPageInfo.getUrl().contains("http://san.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!-- 기사 본문 start -->", "<!-- 기사 본문 end -->", htmlSrc);
				String FORUM_DATE_temp2 = commonUtil.getSubStringResult("<div class=\"news_date\">", "</div>", htmlSrc);
				pt1 = Pattern.compile("(입력 [0-9]+\\.[0-9]+\\.[0-9]+\\ [0-9]+\\:[0-9]+)");

				mc1 = pt1.matcher(FORUM_DATE_temp2);
				
				if (mc1.find()) {
					FORUM_DATE_temp += mc1.group(1);
				}
				FORUM_DATE=PickUpDate(FORUM_DATE_temp);
				
				FORUM_CONTENTS=contentTemp;
				
			}else if(dqPageInfo.getUrl().contains("http://weekly.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<article  id=\"article-view-content-div\" class=\"article-veiw-body view-page\" itemprop=\"articleBody\">", "</article>", htmlSrc);
				FORUM_DATE_temp = commonUtil.getSubStringResult("<li>입력", "</li>", htmlSrc);
				FORUM_DATE=PickUpDate(FORUM_DATE_temp);
				FORUM_CONTENTS=contentTemp;
				//FORUM_DATE = PickUpDate(FORUM_DATE_temp);
				
			}else if(dqPageInfo.getUrl().contains("http://newsteacher.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!-- article -->", "<!-- article -->", htmlSrc);
				FORUM_DATE_temp = commonUtil.getSubStringResult("<span class=\"date_text\">", "</span>", htmlSrc);
				FORUM_DATE=PickUpDate(FORUM_DATE_temp);
				FORUM_CONTENTS=contentTemp;
				//FORUM_DATE = PickUpDate(FORUM_DATE_temp);
				
			}else if(dqPageInfo.getUrl().contains("http://economychosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!-- 기사본문 : s -->", "<div class=\"date_text\">", htmlSrc);
				String FORUM_DATE_temp2 = commonUtil.getSubStringResult("<p class=\"ho\">", "</p>", htmlSrc).replace("&nbsp;", "");
				pt1 = Pattern.compile("([0-9]+호) ([0-9]+년[0-9]+월[0-9]+일)");

				mc1 = pt1.matcher(FORUM_DATE_temp2);
				if (mc1.find()) {
					FORUM_DATE_temp += mc1.group(2);
				}
				FORUM_DATE=PickUpDate(FORUM_DATE_temp);
				FORUM_CONTENTS=contentTemp;
				
			}else if(dqPageInfo.getUrl().contains("https://health.chosun.com/")||dqPageInfo.getUrl().contains("http://health.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!-- 기사 본문 start -->", "<!--여기까지만-->", htmlSrc);
				String FORUM_DATE_temp2 = commonUtil.getSubStringResult("<p id=\"date_text\">", "</p>", htmlSrc);
				pt1 = Pattern.compile("(입력 [0-9]+\\.[0-9]+\\.[0-9]+\\ [0-9]+\\:[0-9]+)");

				mc1 = pt1.matcher(FORUM_DATE_temp2);
				if (mc1.find()) {
					FORUM_DATE_temp += mc1.group(1);
				}
				FORUM_DATE=PickUpDate(FORUM_DATE_temp);
				FORUM_CONTENTS=contentTemp;
				
			}else if(dqPageInfo.getUrl().contains("http://news.chosun.com/")&&!dqPageInfo.getUrl().contains("http://news.chosun.com/misaeng/")) {
				contentTemp = commonUtil.getSubStringResult("<div class=\"par\">", "<!-- 기사 본문 end -->", htmlSrc);
				FORUM_DATE_temp = commonUtil.getSubStringResult("<div id=\"date_text\" class=\"news_date\">입력", "</div>", htmlSrc);
				FORUM_DATE=PickUpDate(FORUM_DATE_temp);
				FORUM_CONTENTS=contentTemp;
				
				
			}else if(dqPageInfo.getUrl().contains("http://weeklybiz.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<div class=\"par\">", "<!-- 기사 본문 end -->", htmlSrc);
				FORUM_DATE_temp = commonUtil.getSubStringResult("<div id=\"date_text\" class=\"news_date\"><span>입력", "</span>", htmlSrc);
				FORUM_DATE=PickUpDate(FORUM_DATE_temp);
				FORUM_CONTENTS=contentTemp;
				
				
			}else if(dqPageInfo.getUrl().contains("https://misaeng.chosun.com/")||dqPageInfo.getUrl().contains("http://news.chosun.com/misaeng/")) {
				contentTemp = commonUtil.getSubStringResult("<div class=\"par\">", "<div class=\"news_imgbox\">", htmlSrc);
				String FORUM_DATE_temp2 = commonUtil.getSubStringResult("<p id=\"date_text\">", "<h3 class=\"news_subtitle\">", htmlSrc);
				pt1 = Pattern.compile("(입력 : [0-9]+\\.[0-9]+\\.[0-9]+\\ [0-9]+\\:[0-9]+)");

				mc1 = pt1.matcher(FORUM_DATE_temp2);
				
				if (mc1.find()) {
					FORUM_DATE_temp += mc1.group(1);
				}
				FORUM_DATE=PickUpDate(FORUM_DATE_temp);
				FORUM_CONTENTS=contentTemp;
				
				
				
			}else if(dqPageInfo.getUrl().contains("http://edu.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!--기사영역S-->", "<!--기사영역E-->", htmlSrc);
				FORUM_DATE_temp = commonUtil.getSubStringResult("<p class=\"days\">", "</p>", htmlSrc);
				FORUM_DATE=PickUpDate(FORUM_DATE_temp);
				FORUM_CONTENTS=contentTemp;
				
			}else if(dqPageInfo.getUrl().contains("http://boutique.chosun.com/")) {
				contentTemp = commonUtil.getSubStringResult("<!-- 기사 본문 start -->", "<!-- 기사 본문 end -->", htmlSrc);
				FORUM_DATE_temp = commonUtil.getSubStringResult("<div id=\"date_text\" class=\"news_date\">입력", "</div>", htmlSrc);
				FORUM_DATE=PickUpDate(FORUM_DATE_temp);
				FORUM_CONTENTS=contentTemp;
				
		}else {
				
				contentTemp = commonUtil.getSubStringResult("<body>", "</body>", htmlSrc).replaceAll("\\{\"_id","\n\\{\"_id");
				pt1 = Pattern.compile("\"content\":\"(.*?)\",");
				mc1 = pt1.matcher(contentTemp);

				
				
				while (mc1.find()) {
						FORUM_CONTENTS += mc1.group(1);
				}
				

				pt1 = Pattern.compile("display_date\":\"([0-9]{4}[- /. /년 ]*[0-9]{2}[- /. /월]*[0-9]{2}T[0-9]{2}:[0-9]{2})");
				mc1 = pt1.matcher(htmlSrc);

				if (mc1.find()) {
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
			
			if(dqPageInfo.getUrl().contains("http://economychosun.com/")) {
				FORUM_MENU= commonUtil.getSubStringResult("<!-- 현재위치 -->", "<!-- 본문 -->", htmlSrc);
				
			}else if(dqPageInfo.getUrl().contains("http://weeklybiz.chosun.com/")||dqPageInfo.getUrl().contains("http://weekly.chosun.com/")||dqPageInfo.getUrl().contains("http://edu.chosun.com/")) {
				FORUM_MENU= commonUtil.getSubStringResult("<meta property=\"article:section\" content=\"", "\">", htmlSrc);
				
			}
	
			htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
			htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";
			htmlSrc += "<FORUM_DATE>" + FORUM_DATE.replaceAll("년", "-").replaceAll("월", "-").replaceAll("일", "") + "</FORUM_DATE>";
			//htmlSrc += "<FORUM_MENU>" + FORUM_MENU + "</FORUM_MENU>";
			htmlSrc +="<FORUM_TYPE>"+FORUM_TYPE+"</FORUM_TYPE>";

		}

		return htmlSrc;

	}

	@Override
	public List<String> makeNewUrls(String naviUrl, DqPageInfo dqPageInfo) {
		List<String> urls = new ArrayList<String>();
		urls.add(naviUrl);
		return urls;
	}

	@Override
	public Row[] splitRow(Row row, DqPageInfo dqPageInfo) {
		return null;
	}

	@Override
	public Map<String, String> addRequestHeader(DqPageInfo dqPageInfo) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}

	@Override
	public boolean validData(Row row, DqPageInfo dqPageInfo) {
		Date date = new Date();
		Date date2 = new Date();
		SimpleDateFormat formatter2= new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if(row.getNode(URL).getValue().equals(aTagUrl)) {
			return false;
		}
		
		if(dqPageInfo.getCollectType() == 0) {
			if(row.getNode(REG_DATE).getValue() != null && !row.getNode(REG_DATE).getValue().equals("")) {
				try {
					date=formatter2.parse(row.getNode(REG_DATE).getValue());
					date2=formatter2.parse(checkdate);
					int compare=date.compareTo(date2);
					if(compare >0) {
						return true;
					}else if(compare <0) {
						return false;
						
					}else {
						return true;
						
					}
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
			}
		}	
		return true;
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
		}else{
			value = "1900-01-01";
		}
		value = value.replace(".", "-").replace("년", "-").replace("월", "-").replace("/", "-").replace("일", " ").replace("시", ":").replace("분", "");
		try {
			SimpleDateFormat DateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
			Date ParsingDateData = DateFormat.parse(value);
			ParsingData = DateFormat.format(ParsingDateData);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
