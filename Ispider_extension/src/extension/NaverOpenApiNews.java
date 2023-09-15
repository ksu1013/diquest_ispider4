package extension;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class NaverOpenApiNews {
	
	
    public  String index(String keywords, String category, int start, String sort) {
    	
    	String clientId = "Dm0p7jMUZ1zqNzQjNBiY"; //애플리케이션 클라이언트 아이디값"
    	String clientSecret = "FL5MBi1vDl"; //애플리케이션 클라이언트 시크릿값"
    	String text = keywords;
    	try {
    		text = URLEncoder.encode(text, "UTF-8");
    	} catch (UnsupportedEncodingException e) { 
    		throw new RuntimeException("[검색어 인코딩 실패] ",e);
    	}
    	
    	int getList = 10;
    	//int i = 1 ;
    	int i = 1 + (start-1)*10;
    	
    	String apiURL = "https://openapi.naver.com/v1/search/" + category +"?query=" + text +"&display=" + getList + "&start="+ i + "&sort="+ sort;    // json 결과
    	//System.out.println("[apiURL]      :    " +apiURL);
    	
    	Map<String, String> requestHeaders = new HashMap<>();
    	requestHeaders.put("X-Naver-Client-Id", clientId);
    	requestHeaders.put("X-Naver-Client-Secret", clientSecret);
    	String responseBody = get(apiURL,requestHeaders);
    	
    	//System.out.println("[DQ-JSON] >> " + responseBody);
//    	i += 100;
    	return responseBody; 
    }

    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }
            
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private static String readBody(InputStream body){
    
    	
       // InputStreamReader streamReader = new InputStreamReader(body);
        int i = 0 ;
        try (BufferedReader lineReader = new BufferedReader(new InputStreamReader(body,"UTF-8"))) {
            StringBuilder responseBody = new StringBuilder();
            
            String line;
            while ((line = lineReader.readLine()) != null) {
            	++i;
                responseBody.append(line);
           //     System.out.println(i +": "+line);
            }
            
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}