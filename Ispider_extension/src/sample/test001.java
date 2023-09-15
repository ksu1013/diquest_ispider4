package sample;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class test001 {
	public static void main(String[] args) throws ParseException {
		String EndDate="20220403";
		Calendar cal = Calendar.getInstance();
		
		String StartDate=null;
		String StartDate2=null;
		SimpleDateFormat formatter2= new SimpleDateFormat("yyyyMMdd");
		cal.set(Calendar.YEAR, Integer.parseInt(EndDate.substring(0, 4)));
		cal.set(Calendar.MONTH, Integer.parseInt(EndDate.substring(4, 6))-1);
		cal.set(Calendar.DATE, Integer.parseInt(EndDate.substring(6, 8)));
		StartDate2 = formatter2.format(cal.getTime());
		
		for (int i = 0; i < 6; i++) {
			
			
			cal.add(Calendar.MONTH, -i);
			
			cal.set(Calendar.DAY_OF_MONTH,1); 
			StartDate = formatter2.format(cal.getTime());
			
			
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); //말일 구하기
			
			if(i==0) {
				
				EndDate=StartDate2;
			}else {
				
				EndDate=formatter2.format(cal.getTime());
			}
			cal.add(Calendar.MONTH,+i);
			
			System.out.println(StartDate);
			System.out.println("EndDate>>>>"+EndDate);
		
			
		}
	}
	
}



