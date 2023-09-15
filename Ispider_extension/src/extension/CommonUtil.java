package extension;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class CommonUtil {
	public String getSubStringResult(String startTag, String endTag, String text) {
		
		String result = "";
 
		if(!text.contains(startTag) || !text.contains(endTag)) {
			return result;
		}
		String subStringText = null;
		try {
			int start = text.indexOf(startTag) + startTag.length();
			subStringText = text.substring(start);
			int end = subStringText.indexOf(endTag);
			result = subStringText.substring(0, end);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			result = "";
		}
		 
		return result;
	}
	
	public String getProductDetailPage(String url, String encoding)
	  {
		BufferedReader in = null;
		Boolean check;
	    StringBuffer detail = new StringBuffer();
	    URL targetURL=null;
	    URLConnection urlConn=null;
	    HttpURLConnection hurlc=null;
	    String line=null;
	    try {
	    	targetURL = new URL(url);
	      urlConn = targetURL.openConnection();
	      hurlc = (HttpURLConnection)urlConn;
	      hurlc.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
	      hurlc.setRequestProperty("Referer", url);
	      hurlc.setRequestMethod("GET"); 
	      hurlc.setDoOutput(true);
	      hurlc.setDoInput(true);
	      hurlc.setUseCaches(false);
	      hurlc.setDefaultUseCaches(false);
	      hurlc.setReadTimeout(20000);
	      hurlc.setConnectTimeout(20000);
	     
	      in = new BufferedReader(new InputStreamReader(hurlc.getInputStream(), encoding));
	      
	      for (line = null; (line = in.readLine()) != null; ) {
	        detail.append(line + "\n");
	       // System.out.println("line>>>>>"+line);
	      }
	      in.close();
	    } catch (MalformedURLException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    
	    return detail.toString();
	  }
	
	public static String getProductDetailPage2(String url) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
			conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
			conn.setRequestProperty("Referer", url);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setDefaultUseCaches(false);
			conn.setReadTimeout(20000);
			conn.setConnectTimeout(20000);
			InputStream is = conn.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (NoSuchAlgorithmException | IOException | KeyManagementException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	
	public String getFileName(String filePath, String fileName) {
		try {
			URL obj = new URL(filePath);
			URLConnection conn = obj.openConnection();
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(20000);
			String header = conn.getHeaderField("Content-Disposition");
			if(header != null) {
				header = header.split("=")[1];
				header = header.replaceAll("\"", "");
				header = header.replaceAll(";", "");
				header = URLDecoder.decode(header,"8859_1");
				String s = new String(header.getBytes("8859_1"), "UTF-8");
				if(s.contains("�")) {
					String s2 = new String(header.getBytes("8859_1"), "EUC-KR");
					return s2;
				}else {
					return s;
				}
			}
			return fileName;
		}catch(Exception e) {
			System.out.println("첨부파일명 가져오기 실패 : " + filePath);
		}
		return fileName;
	}
	
	/**
	 * 
	 * @param value 날짜
	 * @param parsePattern 날짜 패턴
	 * @param locale 언어
	 * @return
	 */
	public String CommonPattern(String value, String parsePattern, Locale locale){
		String newDate = null;
		value = value.replaceAll("월", "").replaceAll("일", "").replaceAll("년", "").replaceAll(" ", ".");
		
		SimpleDateFormat parser = null;
		SimpleDateFormat odf = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
		
		ParsePosition pos = new ParsePosition(0);

		if(!locale.equals("")) {
			parser = new SimpleDateFormat(parsePattern, locale);
		}else {
			parser.applyPattern(parsePattern);
		}
		
		pos.setIndex(0);
		Date date = parser.parse(value, pos);
		System.out.println("date: " + date);
		if (date != null) {
			System.out.println("값: " + value);
			System.out.println("패턴:" + parsePattern);
			
			if(!odf.format(date).startsWith("00")){
				newDate = odf.format(date);
				
				System.out.println("return: "+newDate);
			}else {
				System.out.println("======================================");
				Date time = new Date();
				SimpleDateFormat format1 = new SimpleDateFormat ("yyyy-MM-dd");
				newDate = format1.format(time); 
			}				
		}

		return newDate;
	}
	public static boolean isCheckDate(String inputDate, String pubdateStr) {
		boolean isCheck = false;
		try {
			SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdformat2 = new SimpleDateFormat("yyyy-MM-dd");
			Date lastDate = sdformat.parse(inputDate);
			Date pubDate = new Date();

			pubDate = sdformat2.parse(pubdateStr);
			int result = lastDate.compareTo(pubDate);
			
			if(result <=0) {
				isCheck = true;
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return isCheck;
	}
}

