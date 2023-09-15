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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diquest.ispider.common.conf.Configuration;
import com.diquest.ispider.common.conf.Reposit;
import com.diquest.ispider.common.save.structure.Row;
import com.diquest.ispider.core.collect.DqPageInfo;
import com.diquest.ispider.core.runnable.Extension;

public class ggilbo implements Extension {
	private CommonUtil common;	
	

	private String query = ""; // 검색 키워드
	private int p;// 페이징
	private String URL;	
	private String REG_DATE;	
	private String checkdate;	
	private String MENU;	
	private String KEYWORD;
	private String aTagUrl ="http://www.ggilbo.com/news/articleView.html?idxno=918759";

	
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
		String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/ggilbo/file.txt";
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
		String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/ggilbo/file.txt";
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
			
			url = "https://prt.ggilbo.com/";
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
					try {
						urlKeyword = URLEncoder.encode(keyword, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String url = "http://prt.ggilbo.com/engine_yonhap/search.php?page="+p+"&searchword="+urlKeyword+"&picktab=article&searchcont=article&period=all&sort=date";
					//String url = "https://prt.ggilbo.com/engine_yonhap/search.php?page="+p+"&searchword="+urlKeyword+"&picktab=article&searchcont=article&period=all&sort=date";
					
					String htmlList=common.getProductDetailPage(url, "UTF-8").replaceAll(" ", "").replaceAll("\n", "");
					
					Pattern pt1=Pattern.compile("");
					Matcher mc1=pt1.matcher("");
					
					
					pt1=Pattern.compile("<divclass=\"title\"><ahref=\"(.*?)\"");
					mc1=pt1.matcher(htmlList);
				
					
					
					while(mc1.find()) {
						linkList += "<a href =\"" + mc1.group(1) + "\">"+keyword+"</a>\n";
						
					}
					
				}
			
			linkList += "<!-- 리스트 끝 -->";
			htmlSrc=linkList;
			System.out.println("htmlSrc>>>>>>"+htmlSrc);
			
		}
	}else {
			
			String FORUM_CONTENTS = "";		//내용
			String FORUM_TITLE = "";		//제목
			String FORUM_DATE = "";		//날짜
			String FORUM_MENU = ""; // 메뉴
			String FORUM_TYPE = "지방지"; // 타입
			String FORUM_DATE_tmep = "지방지"; // 타입
			
			//타이틀
			FORUM_TITLE = commonUtil.getSubStringResult("<h3 class=\"heading\">", "</h3>", htmlSrc);
			
			//컨텐츠
			FORUM_CONTENTS = commonUtil.getSubStringResult("<!-- contents //-->", "<article id=\"anchorBtm\" class=\"article-copy\">", htmlSrc);
			
			//작성일
			FORUM_DATE_tmep=commonUtil.getSubStringResult("<i class=\"icon-clock-o\"></i> 입력", "</li>", htmlSrc);
			FORUM_DATE=PickUpDate(FORUM_DATE_tmep);
			
			FORUM_MENU=commonUtil.getSubStringResult("<meta property=\"article:section\" content=\"", "\">", htmlSrc);
			
			
			htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
			htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";	
			htmlSrc +="<FORUM_DATE>"+FORUM_DATE+"</FORUM_DATE>";
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
	
	public static void main(String[] args) throws Exception {
		
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
