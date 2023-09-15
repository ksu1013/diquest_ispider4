package softtech;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diquest.ispider.common.save.structure.Row;
import com.diquest.ispider.core.collect.DqPageInfo;
import com.diquest.ispider.core.runnable.Extension;

public class huanqiu implements Extension {

	@Override
	public Map<String, String> addRequestHeader(DqPageInfo arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String changeHtml(String arg0, DqPageInfo arg1) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean validData(Row arg0, DqPageInfo arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static String PickUpDate(String InputValue){		
		/**
		 * 문장을 삽입하면 날짜를 추출해주는 메서드
		 * 참고)
		 * 사용가능 형식
		 * 1900-01-01 , 1900-1-1 , 1900/01/01 , 1900/1/1 , 1900년01월01일, 1900년1월1일
		 * @since 2019-05-09
		 * @author 이누리
		 */
		
		String value = "";
		String ParsingData = "";
		Pattern DatePattern = Pattern.compile("[0-9]{4}[- /. /년 ]*(0[1-9]|1[0-2]|[1-9])[- /. /월]*(0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[- /. /일]*([0-9]+)[- /. /시]*([0-9]+)[- /. /분]");
		Matcher DateMatcher = DatePattern.matcher(InputValue);
		if(DateMatcher.find()){
			value = DateMatcher.group();
		}else{
			value = "1900-01-01";
		}
		value = value.replace(".", "-").replace("년", "-").replace("월", "-").replace("/", "-").replace("일", " ").replace("시", ":").replace("분", "");
		try {
			SimpleDateFormat DateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
			Date ParsingDateData = DateFormat.parse(value);
			ParsingData = DateFormat.format(ParsingDateData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ParsingData;
	}

}
