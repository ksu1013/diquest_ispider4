package NaverBlog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.diquest.ispider.common.conf.Configuration;
import com.diquest.ispider.common.conf.Reposit;
import com.diquest.ispider.common.save.structure.Row;
import com.diquest.ispider.core.collect.DqPageInfo;
import com.diquest.ispider.core.runnable.Extension;

import extension.NaverOpenApiNews;



/**
 * @수집사이트 - https://www.naver.com
 * @since	- 2022.04.01
 * @author	- 최진영
 *
 */
public class NAVER_BLOG implements Extension {
	private CommonUtil commonUtil;	 
	static NaverOpenApiNews naverApiNews; 
	
	private String currentUrl;   // 현재 URL
	private String query ="";		 // 검색 키워드
	private int    num = 0;			 // 페이징
	
	private String category = "blog";
	private String URL;	
	private String REG_DT;
	private String REGDATE;
	
	// 전체[0]-최신순, 추가[1]-연관도순
	private String sort = "date";
	
	// 0Depth 기본 <a> 태그용 Url
	private String aTagUrl ="https://blog.naver.com/PostView.naver?blogId=buckspresso&logNo=222708424348";
	
	// 최종수집 날짜
	static String lastDate = "";
	
	@Override
	public void startExtension(DqPageInfo dqPageInfo, String homePath) {
		Reposit reposit = Configuration.getInstance().getBbsReposit(dqPageInfo.getBbsId());
		
		/* 공통 선언 start */
		naverApiNews = new NaverOpenApiNews();
		commonUtil = new CommonUtil();
		/* 공통 선언 end */
		
		if(dqPageInfo.getCollectType() == 1) {
			sort = "sim ";
		}

		URL = reposit.getNodeByColumnName("URL").getId();
		REG_DT = reposit.getNodeByColumnName("REG_DT").getId();
		REGDATE = reposit.getNodeByColumnName("REGDATE").getId();
		
		if(dqPageInfo.getCollectType() == 0) {
			lastDate =commonUtil.getFileRead(dqPageInfo.getBbsId());
		}
	}

	@Override
	public void endExtension(DqPageInfo dqPageInfo) {
		//최종 수집날짜 저장
		commonUtil.setFileWrite(dqPageInfo.getBbsId());
	}

	@Override
	public String changeRequestURL(String url, DqPageInfo dqPageInfo) {
		if(url.contains("query")) {
			query =  commonUtil.getSubStringResult("query=", "&num", url);
			num =  Integer.parseInt(url.substring(url.indexOf("num=")+4));
			url = "https://www.naver.com/";			
		}
		this.currentUrl = url;
		return url;
		
	}

	@Override 
	public void changeRowValue(Row row, DqPageInfo dqPageInfo) {
		if(row.getNode(REG_DT).getValue() == null || row.getNode(REG_DT).getValue().equals("")) {
			LocalDate now = LocalDate.now();
			// 포맷 정의
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			// 포맷 적용
			String formatedNow = now.format(formatter);
			// 결과 저장
			row.getNode(REG_DT).setValue(formatedNow);
		}
	}
	

	@Override
	public String changeHtml(String htmlSrc, DqPageInfo dqPageInfo) {
		
		if(dqPageInfo.getParentUrl() == null) {			
			String linkList ="<!-- 리스트 시작 -->\n";
			linkList += "<a href=\"" + aTagUrl + "\"></a>\n";
			if(!query.equals("")) {
				String[] searchArr = query.split("'\\|");
				String keyword ="";	

				for(int i=0; i < searchArr.length; i++ ) {
					keyword = searchArr[i];
					String jsonList = naverApiNews.index(keyword, category , num, sort);
					JSONParser parser = new JSONParser();
					
					Object obj1 = null;
					
					try {
						obj1 = parser.parse(jsonList);
						JSONObject jsonObj = (JSONObject) obj1;
						JSONArray bodyArray;
						
						bodyArray = (JSONArray) jsonObj.get("items");
						Thread.sleep(500);
//						TimeUnit.MILLISECONDS.sleep(500);
						for(int j=0; j < bodyArray.size(); j++) {
							JSONObject returnSubject = (JSONObject) bodyArray.get(j);
							String returnLink= String.valueOf(returnSubject.get("link"));
							String returnTitle = String.valueOf(returnSubject.get("title"));
							String returnDescription = String.valueOf(returnSubject.get("description"));
							
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
								
//								System.out.println("[DQ-LINK] >>>> " + returnLink);
								
								try { 
									URL url = new URL(returnLink);
									HttpURLConnection conn;
									conn=(HttpURLConnection) url.openConnection();
									conn.setRequestMethod("GET");
									conn.setRequestProperty("charset", "utf-8");
									int responseCode=conn.getResponseCode();
									if( responseCode == 200 )
							        {										
										if(returnDescription.contains(keyword) && returnTitle.contains(keyword)) {
											linkList += "<a href=\"" + returnLink + "\">" + keyword + "</a>\n";									
										}
							        }
								} catch (IOException e) {
									// TODO Auto-generated catch block
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
			String FORUM_DATE = "";			//주제

			String regex = ""; 
			
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
		
			//내용 
			String preStr = "";
			String postStr = "";
			
			
			preStr = "_postViewArea" + dqPageInfo.getUrl().substring(dqPageInfo.getUrl().indexOf("logNo=")+6) +"\">";
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
			
			if(mc1.find()) {
				FORUM_DATE=mc1.group(1).replaceAll(" ", "");
				if(!FORUM_DATE.contains(".")) {
					FORUM_DATE = commonUtil.CommonPattern(FORUM_DATE, "yyyyMMdd", Locale.US);					
				}
			}
			else { 
				Date now = new Date();
				FORUM_DATE = dateFormat.format(now);
			}

			htmlSrc += "<FORUM_TITLE>" + FORUM_TITLE + "</FORUM_TITLE>";
			htmlSrc += "<FORUM_CONTENTS>" + FORUM_CONTENTS + "</FORUM_CONTENTS>"; 
			htmlSrc += "<FORUM_DATE>" + FORUM_DATE.replaceAll("\\.", "-") + "</FORUM_DATE>"; 
			
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
		if(row.getNode(URL).getValue().equals(aTagUrl)) {
			return false;
		}
		
		if(dqPageInfo.getCollectType() == 0) {
			if(row.getNode(REGDATE).getValue() != null && !row.getNode(REGDATE).getValue().equals("")) {
				/* @return -1: true, 0: true, 1: false */
				boolean isCheck = commonUtil.isCheckDate(lastDate, row.getNode(REGDATE).getValue());
		
				if(!isCheck) {
					return false;
				}
			}else {
				return false;
			}
		}
		return true;
	}
	
	
	
}
