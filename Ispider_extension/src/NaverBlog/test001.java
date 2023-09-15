package NaverBlog;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import extension.NaverOpenApiNews;

public class test001 {
	
	static CommonUtil common = new CommonUtil();
	static NaverOpenApiNews naw= new NaverOpenApiNews();
	
	public static void main(String[] args) throws ParseException {
//		String url = "ttps://search.naver.com/search.naver?where=news&sm=tab_opt&sort=1&photo=0&field=0&pd=0&ds&de&docid&related=0&mynews=0&office_type=0&office_section_code=0&news_office_checked&nso=so%3Add%2Cp%3Aall&is_sug_officeid=0&start=11?&query=충남";
//		String query =  url.substring(url.indexOf("&query=")+7);
//		System.out.println(query);
//		int num =  Integer.parseInt(common.getSubStringResult("&start=", "?", url));
//		System.out.println(num);
		
//		String kk=naw.index("충남", "news.json", 1, "date");
//		
//		System.out.println(kk);
		
		String k="Thu, 22 Dec 2022 12:26:00 +0900";
		
		Date date =new Date();
		
		SimpleDateFormat format2 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z",new Locale("en", "US"));
		
		date=format2.parse(k);
		
		
		
		System.out.println(date);
		
		
		
	}
}
