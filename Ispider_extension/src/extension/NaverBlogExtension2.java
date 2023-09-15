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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NaverBlogExtension2  implements Extension
{
	  private CommonUtil commonUtil;	 
	  static NaverOpenApiNews naverApiNews; 
	  private String query ="";			 // 검색 키워드 디코팅 후
	  private String de_query ="";		 // 검색 키워드 디코딩 전
	  private int    num = 0;			 // 페이지
	  private String currentUrl;   		 // 현재 URL
	  private String URL;				
	  private String checkdate;			 //val 체크
	  private String REG_DATE;			 //블로그 등록 날짜
	  private String sort = "date";		 //api 시 필요-최신수
	  private String category = "blog";	 //api 시 필요-카테고리-블로그
	 

  public void startExtension(DqPageInfo dqPageInfo, String homePath)
  {
	  
	  Reposit reposit = Configuration.getInstance().getBbsReposit(dqPageInfo.getBbsId());
		
		/* 공통 선언 start */
		naverApiNews = new NaverOpenApiNews();
		commonUtil = new CommonUtil();
		
		URL = reposit.getNodeByColumnName("CONTS_URL").getId();
		REG_DATE = reposit.getNodeByColumnName("REG_DT").getId();
		
		String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/naverblog/file.txt";
		//String filePath = "C:\\Users\\DIQUEST\\Desktop\\프로젝트\\충남도청 올담 고도화\\충남도청_트윗터_블로그\\file.txt";
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
		String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/naverblog/file.txt";
		//String filePath = "C:\\Users\\DIQUEST\\Desktop\\프로젝트\\충남도청 올담 고도화\\충남도청_트윗터_블로그\\file.txt";
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
		url = "https://www.naver.com/";
		//System.out.println("url11111111111>>>>>>"+url);
		//url = "http://section.blog.naver.com/";	
		//System.out.println("url>>>>>>"+url);
	}
	this.currentUrl = url;
	return url;
  }

  public void changeRowValue(Row row, DqPageInfo dqPageInfo)
  {
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
	  
	  if(dqPageInfo.getParentUrl() == null) {			
			String linkList ="<!-- 리스트 시작 -->\n";
			if(!query.equals("")) {
				String keyword ="";	
				for(int i=0; i < 1; i++ ) {
					keyword=query;
					String jsonList = naverApiNews.index(keyword, category , num, sort);
					JSONParser parser = new JSONParser();
					Object obj1 = null;
					
					try {
						obj1 = parser.parse(jsonList);
						JSONObject jsonObj = (JSONObject) obj1;
						JSONArray bodyArray;
						
						bodyArray = (JSONArray) jsonObj.get("items");
						Thread.sleep(500);
						for(int j=0; j < bodyArray.size(); j++) {
							JSONObject returnSubject = (JSONObject) bodyArray.get(j);
							String returnLink= String.valueOf(returnSubject.get("link"));
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
								
								try { 
									URL url = new URL(returnLink);
									HttpURLConnection conn;
									conn=(HttpURLConnection) url.openConnection();
									conn.setRequestMethod("GET");
									conn.setRequestProperty("charset", "utf-8");
//									int responseCode=conn.getResponseCode();
//									if( responseCode == 200 )
//							        {										
//										if(returnDescription.contains(keyword) && returnTitle.contains(keyword)) {
											linkList += "<a href=\"" + returnLink + "\">" + keyword + "</a>\n";		
//										}
//							        }
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						
					} catch (ParseException e) {
						e.printStackTrace();
						System.out.println(e);
					} catch (InterruptedException e) {
						System.err.format("IOException: %s%n", e);
					}
					 
				}
				
			}
			
			linkList += "<!-- 리스트 끝 -->";
			htmlSrc = linkList;
		}else { 
			String FORUM_TITLE = "";		//주제
			String FORUM_CONTENTS = "";		//내용
			String FORUM_DATE = "";			//날짜
			String FORUM_KEYWORD="";		//키워드
			String FORUM_NAME="";			//작성자
			String regex = ""; 				//정규식
			String preStr = "";				//내용 첫문장
			String postStr = "";			//내용 끝문장
			String today ="";				//오늘 날짜
			String regex2="";				//정규식2번째-날짜사용
			
			htmlSrc = commonUtil.getProductDetailPage(dqPageInfo.getUrl(), "UTF-8");
			
			//제목
			regex = "<meta property=\"og:title\".*?content=\"(.*?)\"";
			
			Pattern pt1 = Pattern.compile(regex);
			Matcher mc1 = pt1.matcher(htmlSrc);
			if(mc1.find()) {
				FORUM_TITLE= mc1.group(1).replace("&#8230;", "…");
			}else {
				FORUM_TITLE =  commonUtil.getSubStringResult("\"og:title\" content=\"", "\"/>", htmlSrc);
			}
			//제목-이모지제거
			pt1 = Pattern.compile("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+");
			mc1 = pt1.matcher(FORUM_TITLE);
			FORUM_TITLE = mc1.replaceAll("");
			
			
			//내용 
			preStr = "_postViewArea" + dqPageInfo.getUrl().substring(dqPageInfo.getUrl().indexOf("logNo=")+6) +"\">";
			postStr = "<div id=\"post_footer_contents\" class=\"post_footer_contents\">";
			FORUM_CONTENTS =  commonUtil.getSubStringResult(preStr, postStr, htmlSrc);
			//내용-이모지 제거
			pt1=Pattern.compile("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+");
			mc1=pt1.matcher(FORUM_CONTENTS);
			FORUM_CONTENTS = mc1.replaceAll("");
			
			//작성자
			FORUM_NAME=commonUtil.getSubStringResult("<meta property=\"naverblog:nickname\" content=\"","\" />",htmlSrc);
			
			//날짜 
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date= new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			today = formatter.format(cal.getTime());
			
			String htmlDate=commonUtil.getSubStringResult("<span class=\"se_publishDate pcol2\">", "</span>", htmlSrc);
			
			if(htmlSrc.contains("se_publishDate pcol2")) {
				if(htmlDate.contains("분 전")) {
					int htmlDate2=Integer.parseInt(htmlDate.replace("분 전", ""));
					cal.setTime(date);
					cal.add(Calendar.MINUTE, -htmlDate2);
					today = formatter.format(cal.getTime());  
					FORUM_DATE=today;
				}else if(htmlDate.contains("시간 전")) {
					int htmlDate2=Integer.parseInt(htmlDate.replace("시간 전", ""));
					cal.setTime(date);
					cal.add(Calendar.HOUR, -htmlDate2);
					today = formatter.format(cal.getTime());  
					FORUM_DATE=today;
				}else {
					regex2 = "<span class=\"se_publishDate pcol2\">([0-9]{4}\\. [0-9]{0,2}\\. [0-9]{0,2})\\. ([0-9]{0,2}\\:[0-9]{0,2})";
				}
				
			}else if(htmlSrc.contains("date fil5 pcol2 _postAddDate")) {
				regex2 = "<p class=\"date fil5 pcol2 _postAddDate\">([0-9]{4}\\. [0-9]{0,2}\\. [0-9]{0,2})\\. ([0-9]{0,2}\\:[0-9]{0,2})";
			}else {
				regex2 = "meta property=\"og:image\".*?pstatic.net\\/(.*?)_";
			}
			
			if(!regex2.equals("")) {
				pt1 = Pattern.compile(regex2); 
				mc1 = pt1.matcher(htmlSrc);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(mc1.find()) {
					
					String k2=mc1.group(1).replaceAll(" ", "").replace(".", "-")+" "+mc1.group(2)+":00";
					Date date2=new Date();
					try {
						date2=dateFormat.parse(k2);
						FORUM_DATE=dateFormat.format(date2);
					} catch (java.text.ParseException e) {
						e.printStackTrace();
					}
				}
				else { 
					Date now = new Date();
					FORUM_DATE = dateFormat.format(now);
				}
			}
			
			htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
			htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>"; 
			htmlSrc += "<FORUM_DATE>" + FORUM_DATE + "</FORUM_DATE>"; 
			htmlSrc += "<FORUM_KEYWORD>" + FORUM_KEYWORD+ "</FORUM_KEYWORD>"; 
			htmlSrc += "<FORUM_NAME>" + FORUM_NAME+ "</FORUM_NAME>"; 
			
		}
		
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