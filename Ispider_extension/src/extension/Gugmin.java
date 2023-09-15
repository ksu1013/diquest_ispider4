package extension;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.diquest.ispider.common.conf.Configuration;
import com.diquest.ispider.common.conf.Reposit;
import com.diquest.ispider.common.save.structure.Row;
import com.diquest.ispider.core.collect.DqPageInfo;
import com.diquest.ispider.core.runnable.Extension;

public class Gugmin implements Extension {
private CommonUtil common;	
	


private String query = ""; // 검색 키워드
private int p;// 페이징
private String URL;	
private String REG_DATE;	
private String checkdate;	
private String MENU;	
private String KEYWORD;	
private String aTagUrl ="https://news.kmib.co.kr/article/view.asp?arcid=0017204141";


@Override
public void startExtension(DqPageInfo dqPageInfo, String homePath) {
	Reposit reposit = Configuration.getInstance().getBbsReposit(dqPageInfo.getBbsId());
	
	/* 공통 선언 start */
	common = new CommonUtil();
	URL = reposit.getNodeByColumnName("CONTS_URL").getId();
	REG_DATE = reposit.getNodeByColumnName("DATE").getId();
	MENU = reposit.getNodeByColumnName("MENU").getId();
	KEYWORD = reposit.getNodeByColumnName("KEYWORD").getId();
	/* 공통 선언 end */
	String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/gugmin/file.txt";
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
	String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/gugmin/file.txt";
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
		if(url.contains("query")) {
			query =  common.getSubStringResult("query=", "&p", url);
			p =  Integer.parseInt(url.substring(url.indexOf("p=")+2));
			
			url = "http://www.kmib.co.kr/";
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

		if(dqPageInfo.getParentUrl() == null) {
			String linkList ="<!-- 리스트 시작 -->\n";
				if(!query.equals("")) {
				
				String[] searchArr = query.split("'\\|");
				String keyword = "";
				

				for(int i=0; i < searchArr.length; i++ ) {
				
					keyword = searchArr[i];
					String urlKeyword = "";
					urlKeyword = convertUnicode(keyword).replace("\\", "%");
					
					String url = "https://www.kmib.co.kr/search/searchResult.asp?searchWord="+urlKeyword+"&pageNo="+p+"&period=";


					String htmlList=common.getProductDetailPage(url, "UTF-8").replaceAll(" ", "").replaceAll("\n", "");
					

				
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
			
			
		}
	}
		else {
			
			String FORUM_CONTENTS = "";		//내용
			String FORUM_TITLE = "";		//제목
			String FORUM_DATE = "";		//날짜
			String FORUM_TYPE = "중앙지"; // 타입
			String FORUM_MENU = ""; // 타입
			
			
			//제목
			FORUM_TITLE = commonUtil.getSubStringResult("<title>", "</title>", htmlSrc);
			//컨텐츠
			FORUM_CONTENTS = commonUtil.getSubStringResult("<!-- 기사 내용 -->", "<!-- //기사 내용 -->", htmlSrc);
			
			String FORUM_DATE_tmep = commonUtil.getSubStringResult("<span class=\"t11\">", "</span>", htmlSrc);
			FORUM_DATE=PickUpDate(FORUM_DATE_tmep);
			FORUM_MENU = commonUtil.getSubStringResult("<p class=\"loca\">", "<a href=\"list.asp?sid1=all\">", htmlSrc).replace("&gt;", "");
			
			
			htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
			htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";	
			htmlSrc +="<FORUM_DATE>"+FORUM_DATE+"</FORUM_DATE>";
			htmlSrc +="<FORUM_TYPE>"+FORUM_TYPE+"</FORUM_TYPE>";
			//htmlSrc +="<FORUM_MENU>"+FORUM_MENU+"</FORUM_MENU>";
			
			

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

		
}
