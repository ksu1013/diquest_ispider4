package extension;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.diquest.diator.collect.DiatorExtension;
import com.diquest.diator.collect.api.API;
import com.diquest.diator.collect.api.ParsingResult;
import com.diquest.diator.collect.api.twitter.Tweet;


public class twitter implements DiatorExtension  {
	private String checkdate;	
	private Date uploadDate;	
	
	
	@Override
	public void changeResultValue(API api, ParsingResult result) {
		
		//api.
		Tweet tweet =(Tweet) result;
		uploadDate = tweet.getCreateAt();
	}
	

	@Override
	public void endExtension(String arg0, int arg1) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String StartDate = formatter2.format(cal.getTime());
		//String filePath = "C:\\Users\\DIQUEST\\Desktop\\프로젝트\\충남도청 올담 고도화\\충남도청_트윗터_블로그\\file2.txt";
		String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/twitter/file.txt";
	    File file = new File(filePath); // File객체 생성
	    if(!file.exists()){ // 파일이 존재하지 않으면
	        try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // 신규생성
	    }else {
	    	file.delete();
	    	try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(StartDate);
	        writer.newLine();
	        writer.flush(); // 	        
	        writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public ParsingResult[] splitResult(API arg0, ParsingResult arg1) {
		// TODO Auto-generated method stub
	
		return null;
	}

	@Override
	public void startExtension(String bbsid, String homepath) {
		//String filePath = "C:\\Users\\DIQUEST\\Desktop\\프로젝트\\충남도청 올담 고도화\\충남도청_트윗터_블로그\\file2.txt";
		String filePath = "/data/diquest/dq-ispider4-4.3.1.3/date_file/twitter/file.txt";
	    File file = new File(filePath); // File객체 생성
	        if(file.exists()){ // 파일이 존재하면
	            BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	            String line = null;
	            try {
					while ((line = reader.readLine()) != null){
						checkdate=line;
					}
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	        }
	}

	@Override
	public boolean validData(API arg0, ParsingResult arg1) {
		Date date = new Date();
		Date date2 = new Date();
		SimpleDateFormat formatter2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String kkk3=uploadDate.toString();
		
			if(kkk3!= null &&!kkk3.equals("")&&checkdate !=null&&!checkdate.equals("")) {
				try {
					date=uploadDate;
					date2=formatter2.parse(checkdate);
					int compare=date.compareTo(date2);
					if(compare >0) {
						return true;
					}else if(compare <0) {
						return false;
						
					}else {
						return true;
						
					}
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
				return true;
		}
	
	
}
