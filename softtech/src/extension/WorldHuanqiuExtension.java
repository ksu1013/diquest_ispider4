package extension;

import com.diquest.ispider.common.conf.Configuration;
import com.diquest.ispider.common.conf.Reposit;
import com.diquest.ispider.common.save.structure.Row;
import com.diquest.ispider.core.collect.DqPageInfo;
import com.diquest.ispider.core.runnable.Extension;


import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @title 소프테크 비공식 수집개발
 * @author 김승욱 수집 사이트 : https://world.huanqiu.com/  (환구시보)
 */
public class WorldHuanqiuExtension implements Extension {

	private ConnectionUtil connectionUtil;
	private String cl_cd;
	private String origin_cd;
	private int doc_id;
	private String now_time;
	private List<HashMap<String, String>> attaches_info;
	private String file_name;
	private boolean error_exist;
	private StringBuffer tagList = new StringBuffer();
	//private CommonUtil commonUtil;
	
	private String url2 = "";
	private String Jsonurl="";

	@Override
	public void startExtension(DqPageInfo dqPageInfo, String homePath) {
		System.out.println("=== DefaultExtension Start ===");
		//Reposit reposit = Configuration.getInstance().getBbsReposit(dqPageInfo.getBbsId());
		doc_id = 0;
		attaches_info = new ArrayList<>();
		connectionUtil = new ConnectionUtil();
		error_exist = false;

		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
		now_time = sdf.format(now);
	}

	@Override
	public String changeRequestURL(String url, DqPageInfo dqPageInfo) {
		
		if(dqPageInfo.getParentUrl() == null) {
			Jsonurl="https://world.huanqiu.com/api/list?node=%22/e3pmh22ph/e3pmh2398%22,%22/e3pmh22ph/e3pmh26vv%22,%22/e3pmh22ph/e3pn6efsl%22,%22/e3pmh22ph/efp8fqe21%22&offset=0&limit=24";
		}else {
			Jsonurl=url;
		}
		this.url2 = Jsonurl;
		return url2;
	}

	@Override
	public Map<String, String> addRequestHeader(DqPageInfo dqPageInfo) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}

	@Override
	public String changeHtml(String htmlSrc, DqPageInfo dqPageInfo) {
//		ClingendaelInstituteExtensionTest test = new ClingendaelInstituteExtensionTest();
		//commonUtil = new CommonUtil();
		tagList.delete(0, tagList.length()); // 초기화
		String hostnm="https://world.huanqiu.com/article/";
		String aidValue=null;
		
		

		if (dqPageInfo.getParentUrl() == null) {
			String urlList = htmlSrc;
			 // JSON 파싱
			try {
	            JSONParser parser = new JSONParser();
	            JSONObject jsonObject = (JSONObject) parser.parse(urlList);
	            JSONArray listArray = (JSONArray) jsonObject.get("list");

	            // "list" 배열의 각 객체에서 "aid" 값을 가져오기
	            tagList.append("<!--List Start-->");
	            for (Object item : listArray) {
	                JSONObject itemObject = (JSONObject) item;
	                aidValue = (String) itemObject.get("aid");
	                if(aidValue !=null) {
	                	 if (tagList.indexOf(String.valueOf(aidValue)) == -1) {
	     		        	tagList.append("\n<a href =\""+hostnm+aidValue+"\n"+"\"></a>");
	     		        }
	                }
	            }
	            tagList.append("\n<!--List End-->");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			htmlSrc = tagList.toString();
			System.out.println(htmlSrc);

		} else if (url2.contains("article")) { // 1depth
			/* 변수선언 start */

			Document doc = Jsoup.parse(htmlSrc);
			String newHtmlSrc = "<CONTENT-PAGE>\n";

			Elements titleArea = doc.getElementsByClass("article-title");
			String title = titleArea.html();
			newHtmlSrc += "<TITLE>" + title + "</TITLE>\n";
			/* 내용(content) 수집 */
			Elements contentElement = doc.getElementsByClass("article-content");
			String content = contentElement.html();
			String result = removeHtmlTags(content.replace("&lt;", "<").replace("&gt;", ">"));

			newHtmlSrc += "<CONTENT>" + result + "</CONTENT>\n";
			/* 생성일(created_date) 수집 */
			String dateElement = doc.getElementsByClass("article-time").text();
			Long dateText = Long.parseLong(dateElement);
			//System.out.println(dateText);
			
	        Instant instant = Instant.ofEpochMilli(dateText);
	        ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
	        String dateTimeStr=dateTime.toString();
	        
			newHtmlSrc += "<CREATED_DATE>" + data(dateTimeStr) + "</CREATED_DATE>\n";

			return newHtmlSrc;
		}

		return htmlSrc;
	}

	 private String removeHtmlTags(String content) {
		 Pattern pattern = Pattern.compile("<[^>]+>");
	     Matcher matcher = pattern.matcher(content);

	     // HTML 태그 제거
	     String result = matcher.replaceAll("");

	     return result;
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
	public void changeRowValue(Row row, DqPageInfo dqPageInfo) {
		doc_id++;
		for (int i = 0; i < row.size(); i++) {
			String nodeId = row.getNodeByIdx(i).getId();
			String nodeName = row.getNodeByIdx(i).getName();
			String nodeValue = row.getNodeByIdx(i).getValue();
			if (nodeName.equals("source_class")) {
				cl_cd = nodeValue;
			} else if (nodeName.equals("source_ID")) {
				origin_cd = nodeValue;
			} else if (nodeName.equals("document_id")) {
				row.getNodeByIdx(i).setValue(String.format("%06d", doc_id));
			}
		}
	}

	@Override
	public boolean validData(Row row, DqPageInfo dqPageInfo) {
		boolean isCheck = true;
		String title = "";
		String documentId = String.format("%06d", doc_id);

		try {
			for (int i = 0; i < row.size(); i++) {
				String nodeName = row.getNodeByIdx(i).getName();
				String nodeValue = row.getNodeByIdx(i).getValue();
				if (nodeName.equals("title")) {
					title = nodeValue;
					break;
				}
			}

			connectionUtil.checkContentImage(row, dqPageInfo, attaches_info, file_name, documentId, cl_cd, origin_cd, now_time);
			if (title.equals("")) {
				isCheck = false;
				error_exist = true; // 에러 파일수 판단용
			}
		} catch (Exception e) {
			isCheck = false;
			error_exist = true; // 에러 파일수 판단용
			e.printStackTrace();
		}

		return isCheck;
	}

	@Override
	public void endExtension(DqPageInfo dqPageInfo) {
		try {
			file_name = connectionUtil.getNewFileName(cl_cd, origin_cd, now_time, dqPageInfo);
			String origin_file_name = connectionUtil.getOriginFileName(dqPageInfo);
			/* 수집로그 저장 */
			if (!connectionUtil.isLocal()) {
				connectionUtil.makeCollectLog(dqPageInfo.getBbsId(), cl_cd, origin_cd, origin_file_name, error_exist);
			}
			connectionUtil.moveAndSaveFile(dqPageInfo.getBbsId(), origin_file_name, file_name);
			System.out.println("첨부파일 목록 : " + attaches_info.toString());
			/* 첨부파일 저장 */
			connectionUtil.moveAndSaveAttachFile(dqPageInfo.getBbsId(), file_name, attaches_info);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("=== DefaultExtension end ===");
		}
	}
	
	public  String data(String dateTimeStr) {
        // 주어진 문자열로 OffsetDateTime 객체를 파싱합니다.
	   //DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        
        // 주어진 문자열을 파싱하여 ZonedDateTime 객체를 생성합니다.
        String input = dateTimeStr;
        int endIndex = input.indexOf("[");
        String parsedInput = input.substring(0, endIndex);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(parsedInput);
        
        // 새로운 형식으로 날짜를 변환하기 위한 DateTimeFormatter 객체를 생성합니다.
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        // 지정된 형식으로 날짜를 문자열로 변환합니다.
        String formattedDate = zonedDateTime.format(outputFormatter);
        
        // 결과 출력
        return formattedDate;
 }
	
	

}
