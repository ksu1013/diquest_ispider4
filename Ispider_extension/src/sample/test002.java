package sample;

import extension.CommonUtil;

public class test002 {
	public static void main(String[] args) {
		
		
		String url = "http://economychosun.com/client/news/view.php?boardName=C08&page=1&t_num=13607492";
		
		CommonUtil com = new CommonUtil();
		
		
		
		System.out.println(com.getProductDetailPage(url, "UTF-8"));
		
		
		
		
	}
}
