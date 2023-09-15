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
import com.diquest.ispider.common.save.structure.RNode;
import com.diquest.ispider.common.save.structure.Row;
import com.diquest.ispider.core.collect.DqPageInfo;
import com.diquest.ispider.core.runnable.Extension;

public class cctoday2 implements Extension {
	private CommonUtil common;	

	private String query = ""; // 검색 키워드
	private int p;// 페이징
	private String URL;	
	private String REG_DATE;	
	private String checkdate;	
	private String url2;
	private String realDomain = "";
	private String aTagUrl ="https://www.cctoday.co.kr/news/articleView.html?idxno=2162816";

	
	@Override
	public void startExtension(DqPageInfo dqPageInfo, String homePath) {
		Reposit reposit = Configuration.getInstance().getBbsReposit(dqPageInfo.getBbsId());
		
		/* 공통 선언 start */
		common = new CommonUtil();
		URL = reposit.getNodeByColumnName("CONTS_URL").getId();
		//REG_DT = reposit.getNodeByColumnName("REG_DT").getId();
		REG_DATE = reposit.getNodeByColumnName("DATE").getId();
		/* 공통 선언 end */
		String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file//cctoday/file.txt";
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
	        
	        Map<String, RNode> temp = Configuration.getInstance().getBbsMain(dqPageInfo.getBbsId()).getAssignNodes();
	        Reposit repo = Configuration.getInstance().getBbsReposit(dqPageInfo.getBbsId());
	        
	        for(String key : temp.keySet()) {
	            if(repo.getNode(key).getColumnName().equals("CONTS_URL")) {
	            	realDomain = temp.get(key).getValue();
	            	System.out.println("realDomain : " + realDomain);
	            }
	        }
	}


	@Override
	public void endExtension(DqPageInfo dqPageInfo) {
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter2= new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String StartDate = formatter2.format(cal.getTime());
		String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/cctoday/file.txt";
	    File file = new File(filePath); // File객체 생성
	    if(!file.exists()){ // 파일이 존재하지 않으면
	        try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // 신규생성/data/diquest/dq-ispider4-4.3.1.3/date_file/chosun
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
		
//		url = "https://biz.chosun.com/engine_yonhap/search.php?query=정치'|경제'|사회&p=*";
		String realurl = "";
		if(url.contains("query")) {
			query =  common.getSubStringResult("query=", "&p", url);
			p =  Integer.parseInt(url.substring(url.indexOf("p=")+2));
			
			url = realDomain;
		}
		
		this.url2 = url;
		
		return url;
	}

	@Override
	public void changeRowValue(Row row, DqPageInfo dqPageInfo) {
	}

	@Override
	public String changeHtml(String htmlSrc, DqPageInfo dqPageInfo) {	
		CommonUtil commonUtil = new CommonUtil();
		
		htmlSrc = common.getProductDetailPage(url2,"UTF-8");
		
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
					String url = "https://prt.cctoday.co.kr/engine_yonhap/search.php?page="+p+"&searchword="+urlKeyword+"&picktab=article&searchcont=article&period=all&sort=date";
										
					String htmlList=common.getProductDetailPage(url, "UTF-8").replaceAll(" ", "").replaceAll("\n", "");
					
			
				
					Pattern pt1=Pattern.compile("");
					Matcher mc1=pt1.matcher("");
					
					
					pt1=Pattern.compile("<divclass=\"title\"><ahref=\"(.*?)\"");
					
					mc1=pt1.matcher(htmlList);
				
					
					
					while(mc1.find()) {
						linkList += "<a href =\"" + mc1.group(1).replace("http:", "https:") + "\">"+keyword+"</a>\n";
					}
				}
			
			linkList += "<!-- 리스트 끝 -->";
			htmlSrc=linkList;
			
		}
	}else {
			
			String FORUM_CONTENTS = "";		//내용
			String FORUM_TITLE = "";		//제목
			String FORUM_DATE = "";		//날짜
			String FORUM_MENU = ""; // 메뉴
			String FORUM_TYPE = "지방지"; // 타입
			
			//타이틀
			//제목
			FORUM_TITLE = commonUtil.getSubStringResult("<h3 class=\"heading\">", "</h3>", htmlSrc);
			
			//컨텐츠
			FORUM_CONTENTS = commonUtil.getSubStringResult("<div class=\"article-body\">", "<article class=\"account\">", htmlSrc);
			
			//작성일
			String FORUM_DATE_tmep=commonUtil.getSubStringResult("-->승인", "<!--</a>--></li>", htmlSrc);
			FORUM_DATE=PickUpDate(FORUM_DATE_tmep);
			
			FORUM_MENU = commonUtil.getSubStringResult("<meta property=\"article:section\" content=\"", "\">", htmlSrc);
			
			htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
			htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>";	
			htmlSrc +="<FORUM_DATE>"+FORUM_DATE+"</FORUM_DATE>";
			htmlSrc +="<FORUM_TYPE>"+FORUM_TYPE+"</FORUM_TYPE>";
			htmlSrc +="<FORUM_MENU>"+FORUM_MENU+"</FORUM_MENU>";

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
		Pattern DatePattern = Pattern.compile("[0-9]{4}[- /. /년 ]*(0[1-9]|1[0-2]|[1-9])[- /. /월]*(0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[- /. /일]*([0-9]+)[- /. /시]*([0-9]+)[- /. /분]");
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
