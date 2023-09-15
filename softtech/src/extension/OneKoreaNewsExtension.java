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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @title 소프테크 비공식 수집개발
 * @author 김승욱 수집 사이트 :  http://news.onekoreanews.net/  (ONEKOREANEWS)
 */
public class OneKoreaNewsExtension implements Extension {

	private ConnectionUtil connectionUtil;
	private String cl_cd;
	private String origin_cd;
	private int doc_id;
	private String now_time;
	private List<HashMap<String, String>> attaches_info;
	private String file_name;
	private boolean error_exist;
	private StringBuffer tagList = new StringBuffer();
	private CommonUtil commonUtil;
	
	private String url2 = "";

	@Override
	public void startExtension(DqPageInfo dqPageInfo, String homePath) {
		System.out.println("=== DefaultExtension Start ===");
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
		this.url2 = url;
		return url2;
	}

	@Override
	public Map<String, String> addRequestHeader(DqPageInfo dqPageInfo) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}

	@Override
	public String changeHtml(String htmlSrc, DqPageInfo dqPageInfo) {
		commonUtil = new CommonUtil();
		tagList.delete(0, tagList.length()); // 초기화
		if (dqPageInfo.getParentUrl() == null) {
			String domain = commonUtil.getDomain(url2);
			String urlList=commonUtil.getSubStringResult("<td style=\"padding:0 4 0 4;\">", "</table><script>document.all.total_news_number.innerHTML = '15176';</script></td>", htmlSrc);
			Pattern pt1 = Pattern.compile("<a\s+href=(.*?)>");
			Matcher mt1 = pt1.matcher(urlList);
			tagList.append("<!--List Start-->");
			while (mt1.find())
				tagList.append("\n<a href ='" + domain+"/"+mt1.group(1)+ "'></a>");
			tagList.append("\n<!--List End-->");
			htmlSrc = tagList.toString();
		} else if (url2.contains("detail")) { // 1depth
			/* 변수선언 start */

			Document doc = Jsoup.parse(htmlSrc);
			String newHtmlSrc = "<CONTENT-PAGE>\n";

			Element titleArea = doc.getElementsByTag("title").first();
			String title = titleArea.html();
			newHtmlSrc += "<TITLE>" + title + "</TITLE>\n";
			/* 내용(content) 수집 */
			Elements contentElement = doc.select("#ct");
			String content = contentElement.html();
			newHtmlSrc += "<CONTENT>" + content + "</CONTENT>\n";
			/* 생성일(created_date) 수집 */
			Element dateElement = doc.select("td.fontSize08").first();
			Element tdTag = dateElement.selectFirst("td");
			// 내용 가져오기
			String dateContent = tdTag.text();
			// 내용에서 불필요한 부분 제거하고 원하는 형식으로 변환
			dateContent = dateContent.replaceAll("[年月]", "-").replace("日", "");
			newHtmlSrc += "<CREATED_DATE>" + dateContent + "</CREATED_DATE>\n";
			newHtmlSrc += "</CONTENT-PAGE>";

			//System.out.println(newHtmlSrc);
			return newHtmlSrc;
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
	
	
	

}
