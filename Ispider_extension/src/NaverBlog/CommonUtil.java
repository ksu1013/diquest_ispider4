package NaverBlog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
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
	

	private static String filePath = "";
	// 구분자
	private final static String FILE_SEPARATOR 	= System.getProperty("file.separator");
	
	
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
	    StringBuffer detail = new StringBuffer();
	    try {
	      URL targetURL = new URL(url);
	      URLConnection urlConn = targetURL.openConnection();
	      HttpURLConnection hurlc = (HttpURLConnection)urlConn;
	      hurlc.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36");
	      hurlc.setRequestProperty("Referer", url);
	      hurlc.setRequestMethod("GET"); 
	      hurlc.setDoOutput(true);
	      hurlc.setDoInput(true);
	      hurlc.setUseCaches(false);
	      hurlc.setDefaultUseCaches(false);
	      hurlc.setReadTimeout(20000);
	      hurlc.setConnectTimeout(20000);
//	      System.out.println("[DQ-Request] >>>> " + hurlc.getHeaderFields().toString());
//	      System.out.println("[DQ-Request.Encoding] >>>> " + hurlc.getContentEncoding());
	      BufferedReader in = new BufferedReader(new InputStreamReader(hurlc.getInputStream(), encoding));
	      for (String line = null; (line = in.readLine()) != null; ) {
	        detail.append(line + "\n");
	      }
	      in.close();
	    } catch (MalformedURLException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return detail.toString();
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
		if (date != null) {
			
			if(!odf.format(date).startsWith("00")){
				newDate = odf.format(date);
			}else {
				Date time = new Date();
				SimpleDateFormat format1 = new SimpleDateFormat ("yyyy-MM-dd");
				newDate = format1.format(time); 
			}				
		}

		return newDate.replaceAll("\\.", "-");
	}

	public static String getSSLIgnore(String url) { 
		String urlStr = url;

		StringBuffer sb = new StringBuffer();

		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			URL urls = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) urls.openConnection();

			InputStreamReader in = new InputStreamReader((InputStream) conn.getContent());
			BufferedReader br = new BufferedReader(in);

			for (String line = null; (line = br.readLine()) != null;) {
				sb.append(new String(line.getBytes("UTF-8")));
			}

			br.close();
			in.close();
			conn.disconnect();

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return sb.toString();
	}
	
	/**
	 * 파일 쓰기
	 * @param BBS_ID
	 */
	public static void setFileWrite(String BBS_ID) {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@ 파일쓰기");
		
		try {
			String fileName =  "";
			filePath = getDataDicPath();
			fileName = "BBS_"+ BBS_ID+"_LastDateFile.txt";
			
			// 1. 파일 객체 생성            
			File file = new File(filePath+fileName);
			System.out.println("@@@@@ 파일 경로 확인 @@@@@>>> " + filePath+fileName);
			// 2. 파일 존재여부 체크 및 생성
			if (!file.exists()) {
				file.createNewFile();
			}
			
			// 3. Buffer를 사용해서 File에 write할 수 있는 BufferedWriter 생성            
			FileWriter fw = new FileWriter(file);            
			BufferedWriter writer = new BufferedWriter(fw);             

			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date now = new Date();
			String nowDate = dateFormat.format(now);
			
			// 4. 파일에 쓰기           
			writer.write(nowDate);
			// 5. BufferedWriter close
			writer.close();
			
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@ 파일쓰기 끝 ");
		} catch (IOException e) {           
			e.printStackTrace();        
		}
		
	}
	
	/**
	 * 파일 읽기
	 * @param BBS_ID  
	 * @return outPut
	 */
	public static String getFileRead(String BBS_ID) {
		
		String outPut = "";
		System.out.println("filePath >>>>>>>>>> " + filePath);
		filePath = getDataDicPath();
		File rw = new File(filePath);
		System.out.println("에러 위치 확인 ");
		File []fileList = rw.listFiles();
		String fileName = "BBS_"+ BBS_ID+"_LastDateFile.txt";
		for(File file : fileList) {
			System.out.println("@@@@ 파일명 @@@@ >> " + file.getName());
			if(file.getName().equals(fileName)) {
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(new File(filePath+fileName)));
					String s;
					while (( s = br.readLine()) != null) {
						outPut = s;
					}
					
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				}
			}
		}
		
		System.out.println("@@@@@@@@@날짜 출력@@@@@@@@@@ >>>>>> "+ outPut);
		return outPut;
	}
	
	
	/**
	 * 
	 * @param inputDate 마지막 수집 날짜
	 * @param pubdateStr
	 * @return -1: true, 0: true, 1: false
	 */
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
	
	/**
	 * "isCheckDateDic" 파일 경로
	 * @return boolean
	 */
	private static String getDataDicPath() {		
		String CONF_FILE_PATH ="";
		String CONF_PATH = "";
		CONF_FILE_PATH = "isCheckDateDic" + FILE_SEPARATOR;
		
		/* 환경변수 $ISPIDER4_HOME 위치 확인  */
		String home = System.getenv("ISPIDER4_HOME");
		if (home == null || home.trim().equals("")) {
			home = "./";
		}
		System.out.println(home);
		if (home.endsWith(FILE_SEPARATOR)) {
			CONF_PATH = home + CONF_FILE_PATH;
		} else {
			CONF_PATH = home + FILE_SEPARATOR + CONF_FILE_PATH;
		}
		
		System.out.println("readFile>>>>>>>>>>["+ CONF_PATH +"]");
		
		return CONF_PATH;
	}
	
}