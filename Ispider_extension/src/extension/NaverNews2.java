package extension;


import com.diquest.ispider.common.conf.Configuration;
import com.diquest.ispider.common.conf.Reposit;
import com.diquest.ispider.common.save.structure.Row;
import com.diquest.ispider.core.collect.DqPageInfo;
import com.diquest.ispider.core.runnable.Extension;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NaverNews2  implements Extension
{
	  private CommonUtil commonUtil;	 
	  private NaverOpenApiNews naverApiNews; 
	  private String query ="";			 // 검색 키워드 디코팅 후
	  private String de_query ="";		 // 검색 키워드 디코딩 전
	  private int    num = 0;			 // 페이지
	  private String currentUrl;   		 // 현재 URL
	  private String URL;				
	  private String checkdate;			 //val 체크
	  private String REG_DATE;			 //블로그 등록 날짜
	  private String sort = "date";		 //api 시 필요-최신수
	  private String category = "news.json";	 //api 시 필요-카테고리-블로그
	  
	  private String TITLE;
	  private String CONTENT;
	  private String KEYWORD;
	  private String url2;
 

  public void startExtension(DqPageInfo dqPageInfo, String homePath)
  {
	  
	  Reposit reposit = Configuration.getInstance().getBbsReposit(dqPageInfo.getBbsId());
		
		/* 공통 선언 start */
		naverApiNews = new NaverOpenApiNews();
		commonUtil = new CommonUtil();
		
		URL = reposit.getNodeByColumnName("CONTS_URL").getId();
		REG_DATE = reposit.getNodeByColumnName("REG_DT").getId();
		TITLE = reposit.getNodeByColumnName("CONTS_TITLE").getId();
		KEYWORD = reposit.getNodeByColumnName("KEYWORD").getId();
		CONTENT = reposit.getNodeByColumnName("CONTS_CONTS").getId();
		
		//String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/navernews/file.txt";
		String filePath = "C:\\Users\\DIQUEST\\Desktop\\프로젝트\\충남도청 올담 고도화\\충남도청_트윗터_블로그\\file3.txt";
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

  public void endExtension(DqPageInfo dqPageInfo)
  {
	  Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String StartDate = formatter2.format(cal.getTime());
		//String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/navernews/file.txt";
		String filePath = "C:\\Users\\DIQUEST\\Desktop\\프로젝트\\충남도청 올담 고도화\\충남도청_트윗터_블로그\\file3.txt";
      File file = new File(filePath); // File객체 생성
      if(!file.exists()){ // 파일이 존재하지 않으면
          try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			} // 신규생성
      }else {
      	file.delete();
      	try {
				file.createNewFile();
			} catch (IOException e) {
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
			e.printStackTrace();
		}
  }
  public String changeRequestURL(String url, DqPageInfo dqPageInfo)
  {
	if(url.contains("keyword")) {
		de_query =  url.substring(url.indexOf("&keyword=")+9);
		try {
			query=URLDecoder.decode(de_query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		num =  Integer.parseInt(commonUtil.getSubStringResult("pageNo=", "&", url));
		//url = "https://www.naver.com/";
	}
	this.currentUrl = url;
	this.url2 = url;
	
	return url;
  }

  public void changeRowValue(Row row, DqPageInfo dqPageInfo)
  {
	  	String Temp_Title = row.getNode(TITLE).getValue();
		row.getNode(CONTENT).setValue(Temp_Title.substring(Temp_Title.indexOf("<내용>")+"<내용>".length(), Temp_Title.indexOf("</내용>")));
		
		row.getNode(KEYWORD).setValue(Temp_Title.substring(Temp_Title.indexOf("<키워드>")+"<키워드>".length(), Temp_Title.indexOf("</키워드>")));
		row.getNode(TITLE).setValue(Temp_Title.substring(Temp_Title.indexOf("<제목>")+"<제목>".length(),Temp_Title.indexOf("</제목>")));
		row.getNode(REG_DATE).setValue(Temp_Title.substring(Temp_Title.indexOf("<작성일>")+"<작성일>".length(), Temp_Title.indexOf("</작성일>")));
		
		
		if(row.getNode(REG_DATE).getValue() == null || row.getNode(REG_DATE).getValue().equals("")) {
			LocalDate now = LocalDate.now();
			// 포맷 정의
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			// 포맷 적용
			String formatedNow = now.format(formatter);
			// 결과 저장
			
			row.getNode(REG_DATE).setValue(formatedNow);
		}
  }
  public String changeHtml(String htmlSrc, DqPageInfo dqPageInfo)
  {
	    JSONParser parser = new JSONParser();
		Object obj1 = null;
			String linkList ="<!-- 리스트 시작 -->\n";
			if(!query.equals("")) {
				String keyword ="";	
					keyword=query;
					String jsonList = naverApiNews.index(keyword, category , num, sort);
						try {
							obj1 = parser.parse(jsonList);
							JSONObject jsonObj = (JSONObject) obj1;
							JSONArray bodyArray;
							
							bodyArray = (JSONArray) jsonObj.get("items");
							//Thread.sleep(500);
							if(url2.contains("section.blog.naver.com")) {
								for(int j=0; j < bodyArray.size(); j++) {
									JSONObject returnSubject = (JSONObject) bodyArray.get(j);
									
									String returnLink= String.valueOf(returnSubject.get("link"));
									String title= String.valueOf(returnSubject.get("title"));
									String description= String.valueOf(returnSubject.get("description"));
									String pubDate= String.valueOf(returnSubject.get("pubDate"));
									
									SimpleDateFormat format2 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z",new Locale("en", "US"));
									
									Date date = format2.parse(pubDate);
									
									SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									
									String strNewDtFormat = newDtFormat.format(date);
									
									linkList +="<tbody>\n";
									linkList += "<a href=\"" + returnLink + "\">" + keyword + "";	
									linkList += "<키워드>" + keyword + "</키워드>";	
									linkList += "<링크>" + returnLink + "</링크>";	
									linkList += "<제목>" + title + "</제목>";
									linkList += "<내용>" + description + "</내용>"; 
									linkList += "<작성일>" + strNewDtFormat + "</작성일>";
									linkList +="</tbody>\n</a>\n";
									
								}
							}
						} catch (ParseException e) {
							e.printStackTrace();
							System.out.println(e);
						} catch (Exception e) {
							System.err.format("IOException: %s%n", e);
						} 
				}
			
			linkList += "<!-- 리스트 끝 -->";
			
			htmlSrc = linkList;
	  
		return htmlSrc;
  }

  public List<String> makeNewUrls(String naviUrl, DqPageInfo dqPageInfo)
  {
	  List<String> urls = new ArrayList<String>();
		urls.add(naviUrl);
		return urls; 
  }

  public Row[] splitRow(Row row, DqPageInfo dqPageInfo)
  {
	  
	  
    return null;
  }

  public Map<String, String> addRequestHeader(DqPageInfo dqPageInfo)
  {
	  Map<String, String> map = new HashMap<String, String>();
		return map;
  }

  public boolean validData(Row row, DqPageInfo dqPageInfo)
  {
	  	Date date = new Date();
		Date date2 = new Date();
		SimpleDateFormat formatter2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
					e.printStackTrace();
				}
				
			
			}
		}	
		return true;
  }
}