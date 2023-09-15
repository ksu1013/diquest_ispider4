package urlconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class urlconnectiontest {
	
	public static void main(String[] args) {
		String encodeResult = "";
		String keyword = "정치";
		String url="";
		String kkk="";
		try {
			encodeResult = URLEncoder.encode(keyword, "UTF-8").replaceAll("%", "%25");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url1 = "https://www.chosun.com/pf/api/v3/content/fetch/search-param-api?query=%7B\"page\"%3A"+ 1 + "%2C\"query\"%3A\"" + encodeResult + "\"%2C\"writer\"%3A\"\"%7D&&_website=chosun";
		String url1_1 = "http://kid.chosun.com/";
		String url1_2 = "http://realty.chosun.com/";
		String url1_3 = "https://realty.chosun.com/";
		String url1_4 = "http://san.chosun.com/";
		String url1_5 = "http://weekly.chosun.com/";
		String url1_6 = "http://newsteacher.chosun.com/";
		String url1_7 = "http://economychosun.com/";
		String url1_8 = "https://health.chosun.com/";
		String url1_9 = "http://news.chosun.com/";
		String url1_10 = "http://weeklybiz.chosun.com/";
		String url1_11 = "https://misaeng.chosun.com/";
		String url1_12 = "http://edu.chosun.com/";
		String url1_13 = "http://boutique.chosun.com/";
		String url2="http://www.kmib.co.kr/search/searchResult.asp?searchWord="+encodeResult+"&pageNo="+1+"&period=";
		String url3 = "https://search.hankookilbo.com/Search?Page="+1+"&tab=NEWS&sort=relation&searchText="+encodeResult+"&searchTypeSet=TITLE,CONTENTS&selectedPeriod=%EC%A0%84%EC%B2%B4&filter=head";
		String url4 = "http://mhsearch.munhwa.com/search.php?startCount="+1+"&query="+encodeResult+"&mode=basic&sort=DESC&collection=news&range=A&startDate=&endDate=&searchField=ALL&reQuery=&reporter=&reQuery=&reporter=&searchField=ALL&div_code=ALL&range=A&sort=DESC";
		String url5 = "https://search.hani.co.kr/Search?command=query&pageseq="+1+"&keyword="+encodeResult+"&media=news&submedia=&sort=d&period=all";				
		String url6 = "https://search.khan.co.kr/search.html?stb=khan&pg="+1+"&q="+encodeResult+"&sort=1";
		String url7 = "https://www.donga.com/news/search?check_news=1&more=1&sorting=1&search_date=1&v1=&v2=&range=1&query="+encodeResult+"p="+1;
		String url8 = "https://www.joongang.co.kr/_CP/496?keyword="+encodeResult+"&page="+1;
		String url9 = "http://www.daejonilbo.com/news/articleList.html?page="+1+"&sc_word="+encodeResult+"&total=&box_idxno=&sc_area=A&view_type=sm";
		String url10 = "https://prt.cctoday.co.kr/engine_yonhap/search.php?page="+1+"&searchword="+encodeResult+"&picktab=article&searchcont=article&period=all&sort=date";
		String url11 = "http://prt.ggilbo.com/engine_yonhap/search.php?page="+1+"&searchword="+encodeResult+"&picktab=article&searchcont=article&period=all&sort=date";
		String url12 = "https://www.chungnamilbo.co.kr/news/articleList.html?page="+1+"&sc_word="+encodeResult+"&sc_area=A&view_type=sm";
		String url13 = "https://www.dailycc.net/news/articleList.html?page="+1+"&sc_word="+encodeResult+"&sc_area=A&view_type=sm";
		String url14 = "https://ars.yna.co.kr/api/v2/search.asis?callback=Search.SearchPreCallback&page_no="+1+"&query="+encodeResult+"&ctype=A&page_size=10&channel=basic_kr";
		String url15 = "https://newsis.com/search/?page="+1+"&val="+encodeResult+"&sort=acc&jo=all_jogun&bun=all_bun&term=allday&s_yn=Y&catg=1&t=1";
		String url16 = "https://www.news1.kr/search_front/search.php?startCount="+1+"&realQuery="+encodeResult+"&query="+encodeResult+"&mode=basic&sort=DATE&collection=front_news&cal_range=A&searchField=ALL&reQuery=2&listType=listType1&reChk=on&searchField=ALL&categoryGroupCheckBox=ALL	";
		String url17 = "https://search.nocutnews.co.kr/list?pageIndex="+1+"&sv="+encodeResult+"&sk=2&sp=0&ot=2&sc=0&a=Center";
		String url18 = "http://www.goodmorningcc.com/news/articleList.html?page="+1+"&sc_word="+encodeResult+"&sc_area=A&view_type=sm";
		String url19 = "http://www.dtnews24.com/news/articleList.html?page="+1+"&sc_word="+encodeResult+"&sc_area=A&view_type=sm";
		String url20 = "https://reco.kbs.co.kr/v2/search?target=newstotal&keyword=" + encodeResult + "&page="+ 1 + "&page_size=10&sort_option=score&searchfield=all";		
		String url21 = "https://searchapi.imnews.imbc.com/search?page="+1+"&query="+encodeResult+"&pagesize=5&sorttype=date";
		String url22 = "https://www.segye.com/api/goSearch";
		String url23 = "https://search.seoul.co.kr/index.php?keyword="+encodeResult+"&cpCode=seoul&pageNum="+1;
		String url24 = "https://www.naeil.com/search/?gubun=body&tpage="+1+"&tsearch="+encodeResult;
		
		
		Boolean chosun=getProductDetailPage(url1,"UTF-8");
		Boolean chosun_1=getProductDetailPage(url1_1,"UTF-8");
		Boolean chosun_2=getProductDetailPage(url1_2,"UTF-8");
		Boolean chosun_3=getProductDetailPage(url1_3,"UTF-8");
		Boolean chosun_4=getProductDetailPage(url1_4,"UTF-8");
		Boolean chosun_5=getProductDetailPage(url1_5,"UTF-8");
		Boolean chosun_6=getProductDetailPage(url1_6,"UTF-8");
		Boolean chosun_7=getProductDetailPage(url1_7,"UTF-8");
		Boolean chosun_8=getProductDetailPage(url1_8,"UTF-8");
		Boolean chosun_9=getProductDetailPage(url1_9,"UTF-8");
		Boolean chosun_10=getProductDetailPage(url1_10,"UTF-8");
		Boolean chosun_11=getProductDetailPage(url1_11,"UTF-8");
		Boolean chosun_12=getProductDetailPage(url1_12,"UTF-8");
		Boolean chosun_13=getProductDetailPage(url1_13,"UTF-8");
		
		Boolean gugmin=getProductDetailPage(url2,"UTF-8");
		Boolean hankookilbo=getProductDetailPage(url3,"UTF-8");
		Boolean munhwa=getProductDetailPage(url4,"UTF-8");
		Boolean hani=getProductDetailPage(url5,"UTF-8");
		Boolean khan=getProductDetailPage(url6,"UTF-8");
		Boolean donga=getProductDetailPage(url7,"UTF-8");
		Boolean joongang=getProductDetailPage(url8,"UTF-8");
		Boolean daejonilbo=getProductDetailPage(url9,"UTF-8");
		Boolean cctoday=getProductDetailPage(url10,"UTF-8");
		Boolean ggilbo=getProductDetailPage(url11,"UTF-8");
		Boolean chungnamilbo=getProductDetailPage(url12,"UTF-8");
		Boolean dailycc=getProductDetailPage(url13,"UTF-8");
		Boolean yna=getProductDetailPage(url14,"UTF-8");
		Boolean newsis=getProductDetailPage(url15,"UTF-8");
		Boolean news1=getProductDetailPage(url16,"UTF-8");
		Boolean nocutnews=getProductDetailPage(url17,"UTF-8");
		Boolean goodmorningcc=getProductDetailPage(url18,"UTF-8");
		Boolean dtnews24=getProductDetailPage(url19,"UTF-8");
		Boolean kbs=getProductDetailPage(url20,"UTF-8");
		Boolean imbc=getProductDetailPage(url21,"UTF-8");
		Boolean segye=getProductDetailPage(url22,"UTF-8");
		Boolean seoul=getProductDetailPage(url23,"UTF-8");
		Boolean naeil=getProductDetailPage(url24,"UTF-8");
		
		
		
		System.out.println("chosun>>>>>>"+chosun);
		System.out.println("chosun_1>>>>>>"+chosun_1);
		System.out.println("chosun_2>>>>>>"+chosun_2);
		System.out.println("chosun_3>>>>>>"+chosun_3);
		System.out.println("chosun_4>>>>>>"+chosun_4);
		System.out.println("chosun_5>>>>>>"+chosun_5);
		System.out.println("chosun_6>>>>>>"+chosun_6);
		System.out.println("chosun_7>>>>>>"+chosun_7);
		System.out.println("chosun_8>>>>>>"+chosun_8);
		System.out.println("chosun_9>>>>>>"+chosun_9);
		System.out.println("chosun_10>>>>>>"+chosun_10);
		System.out.println("chosun_11>>>>>>"+chosun_11);
		System.out.println("chosun_12>>>>>>"+chosun_12);
		System.out.println("chosun_13>>>>>>"+chosun_13);
		
		System.out.println("gugmin>>>>>>"+gugmin);
		System.out.println("hankookilbo>>>>>>"+hankookilbo);
		System.out.println("munhwa>>>>>>"+munhwa);
		System.out.println("hani>>>>>>"+hani);
		System.out.println("khan>>>>>>"+khan);
		System.out.println("donga>>>>>>"+donga);
		System.out.println("joongang>>>>>>"+joongang);
		System.out.println("daejonilbo>>>>>>"+daejonilbo);
		System.out.println("cctoday>>>>>>"+cctoday);
		System.out.println("ggilbo>>>>>>"+ggilbo);
		System.out.println("chungnamilbo>>>>>>"+chungnamilbo);
		System.out.println("dailycc>>>>>>"+dailycc);
		System.out.println("yna>>>>>>"+yna);
		System.out.println("newsis>>>>>>"+newsis);
		System.out.println("news1>>>>>>"+news1);
		System.out.println("nocutnews>>>>>>"+nocutnews);
		System.out.println("goodmorningcc>>>>>>"+goodmorningcc);
		System.out.println("dtnews24>>>>>>"+dtnews24);
		System.out.println("kbs>>>>>>"+kbs);
		System.out.println("imbc>>>>>>"+imbc);
		System.out.println("segye>>>>>>"+segye);
		System.out.println("seoul>>>>>>"+seoul);
		System.out.println("naeil>>>>>>"+naeil);
		
		
		
	}


	public static Boolean getProductDetailPage(String url, String encoding) {
		Boolean check;
		HttpURLConnection hurlc = null;
		BufferedReader in = null;
		URLConnection urlConn = null;
		URL targetURL=null;
	    StringBuffer detail = new StringBuffer();
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
	      for (String line = null; (line = in.readLine()) != null; ) {
	        detail.append(line + "\n");
	      }
	      in.close();
	    } catch (MalformedURLException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    
	    if(in != null) {
	    	check=true;
	    }else {
	    	check=false;
	    }
	    
	    
	  return check;
	}

}
