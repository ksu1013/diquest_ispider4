package extension;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.diquest.ispider.common.save.structure.Row;
import com.diquest.ispider.core.collect.DqPageInfo;
import com.diquest.ispider.core.runnable.Extension;

import extension.ConnectionUtil;

public class Incheon implements Extension {
	private ConnectionUtil connectionUtil;
	private CommonUtil commonUtil;
	private List<HashMap<String, String>> attaches_info;
	private String file_name;
	private StringBuffer tagList = new StringBuffer();
	private String url = "";

	@Override
	public Map<String, String> addRequestHeader(DqPageInfo arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String changeHtml(String htmlSrc, DqPageInfo dqPageInfo) {
		commonUtil = new CommonUtil();
		tagList.delete(0, tagList.length()); // 초기화
		if (dqPageInfo.getParentUrl() != null) { // 1depth
			/* 변수선언 start */

			Document doc = Jsoup.parse(htmlSrc);
			String newHtmlSrc = "<CONTENT-PAGE>\n";

			Elements titleArea = doc.select(".view-head-tit");
			String title = titleArea.html();
			newHtmlSrc += "<TITLE>" + title + "</TITLE>\n";
			/* 내용(content) 수집 */
			Elements contentElement = doc.select(".view-body");
			String content = contentElement.html();
			newHtmlSrc += "<CONTENT>" + content + "</CONTENT>\n";
			/* 생성일(created_date) 수집 */
			Element dateElement = doc.select(".info-desc").first();
			// 내용 가져오기
			String dateContent = dateElement.text();
			newHtmlSrc += "<CREATED_DATE>" + dateContent + "</CREATED_DATE>\n";
			// 파일
			Elements fileLinks = doc.select(".file-list").select(".file-list-item");
			String fncDownValue=null;
			String NewUrl="";
			String NewUrlName="";
			String[] k=null;
			int cnt=0;
			System.out.println("fileLinks.size()>>>>"+fileLinks.size());
			for (Element link : fileLinks) {
	            String onclickValue = link.select("a").attr("onclick");
	            String nameValue=link.select("a").text();
	            if (onclickValue != null && !onclickValue.isEmpty()) {
	                int startIndex = onclickValue.indexOf("'") + 1;
	                int endIndex = onclickValue.lastIndexOf("'");
	                fncDownValue = onclickValue.substring(startIndex, endIndex).replace("'", "");
	                k=fncDownValue.split(",");
	                NewUrl +="https://www.airport.kr/co/cmm/bbs/cmmBbsDown.do?NTT_ID="+k[0]+"&ATCH_SN="+k[1];
	                NewUrlName +=nameValue;
	                
	                if(fileLinks.size()-1 != cnt) {
	                	 NewUrl += "@@";
	                	 NewUrlName += "@@";
	                }
	               
	                System.out.println("fncDownValue>>>>"+NewUrl);
	                System.out.println("NewUrlName>>>>"+NewUrlName);
	            }
	            cnt++;
	        }
			
			newHtmlSrc += "<FILEURL>" + NewUrl + "</FILEURL>\n";
			newHtmlSrc += "<FILENAME>" + NewUrlName + "</FILENAME>\n";
			 
			
			newHtmlSrc += "</CONTENT-PAGE>";

			//System.out.println(newHtmlSrc);
			return newHtmlSrc;
		}

		return htmlSrc;
		// TODO Auto-generated method stub
	}

	@Override
	public String changeRequestURL(String arg0, DqPageInfo arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changeRowValue(Row arg0, DqPageInfo arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endExtension(DqPageInfo arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> makeNewUrls(String arg0, DqPageInfo arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Row[] splitRow(Row arg0, DqPageInfo arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startExtension(DqPageInfo arg0, String arg1) {
		System.out.println("=== DefaultExtension Start ===");
		attaches_info = new ArrayList<>();
		connectionUtil = new ConnectionUtil();

		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean validData(Row arg0, DqPageInfo arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}
