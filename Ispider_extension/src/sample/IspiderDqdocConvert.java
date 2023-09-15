package sample;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class IspiderDqdocConvert {

    private static String OUTPUT_FILE_PATH  = "C:\\dq-ispider_test\\csv\\%s";
//	private static String COPY_FILE_PATH 	= "/bdpcloud/ispider/BAKCUP_DQDOC/%s";    
	private static String INPUT_FILE_PATH   = "C:\\dq-ispider_test\\dqdoc\\6\\6_Chosun_0.UTF-8";
	
	public static void convertFile(List<String> schemaArr) {
//		String INPUT_FILE_PATH   = "/home/airport/ispider/dqdoc/"+ bbsId +"/%s.UTF-8";

		try {
		//System.out.println("fileName >> " + fileName);
		
		SimpleDateFormat format_file 	= new SimpleDateFormat("yyyyMMddHHmmss");
//		SimpleDateFormat reg_date 	= new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat format 	= new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		Date date = new Date();
	
//		String[] outputFileName	= fileName.split("_");
//		String outputFileName = fileName.substring(fileName.indexOf("@") + 1 , fileName.lastIndexOf("_"));
		String outputFileName = "NEWS";
//		String inputFile 	= String.format(INPUT_FILE_PATH, bbsId, fileName);
		String inputFile 	= String.format(INPUT_FILE_PATH);
		String outputFile 	= String.format(OUTPUT_FILE_PATH, outputFileName);
		//String copyFile		= String.format(COPY_FILE_PATH, fileName + "_" + format_file.format(date));
		
		
//		System.out.println("inputFile>>>"+inputFile);
//		System.out.println("outputFile>>>"+outputFile);
//		System.out.println("copyFile>>>"+copyFile);
//		fileName >> xogns
//		inputFile>>>/home/airport/ispider/dqdoc/xogns.UTF-8
//		outputFile>>>/home/airport/ispider/finish_dqdoc/xogns
//		copyFile>>>/home/airport/ispider/BAKCUP_DQDOC/xogns_20201012111848

	
		System.out.println("schemaArr : " + schemaArr);
		
		BufferedReader br = null;
		
		File file = new File(inputFile);
		System.out.println("file >>> " + file);
	
//		String insertDate = format_file.format(startDate);
//		
//		System.out.println("insertDate >>> " + insertDate);
		
		/* 파일이 존재하지 않는 경우 fin 파일 생성 후 종료 */
		if(!file.exists()) {
//			createFile(outputFile+".csv");
//			createFile(outputFile+".fin");
			createFile(outputFile+"_"+format_file.format(date)+".csv");
			createFile(outputFile+"_"+format_file.format(date)+".fin");
			System.out.println("!!!!안녕..");
			return;
		}
		
		BufferedWriter bw = null;
	
		System.out.println("start time [ "+ format.format(date) +"]");
		
				
		System.out.println("ispiderDqdocConvert: extractFile: inputFile = " + inputFile +" start time [ "+ format.format(new Date()) +"]");
		try {
			br = new BufferedReader((new InputStreamReader(new FileInputStream(new File(inputFile)), "UTF-8")));
//			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("================================================ ALERT ================================================");
			System.out.println("ispiderDqdocConvert: extractFile: inputFile = " + inputFile);
			System.out.println("================================================ ALERT ================================================");
		}

		File outFile = new File(outputFile);
		String txt = "";

		System.out.println("outFile : " + outFile);
		/*StringBuilder categoryId 	= new StringBuilder();
		StringBuilder categoryName 	= new StringBuilder();
		StringBuilder bbsId 		= new StringBuilder();
		StringBuilder bbsName 		= new StringBuilder();

		StringBuilder URL 			= new StringBuilder();
		StringBuilder PREVIOUS_PEOPLE_COUNT = new StringBuilder();
		StringBuilder TITLE 		= new StringBuilder();
		StringBuilder PEOPLE_COUNT	= new StringBuilder();
		StringBuilder CONTENT		= new StringBuilder();
		StringBuilder PERCENTAGE_CHANGE = new StringBuilder();
		
		StringBuilder SMONTH 		= new StringBuilder();
		StringBuilder SYEAR 		= new StringBuilder();
		StringBuilder EMONTH 		= new StringBuilder();
		StringBuilder EYEAR 		= new StringBuilder();
		StringBuilder VPOINT 		= new StringBuilder();
		*/
		String[] dataArr= new String[schemaArr.size()];
//		long contsSeq = System.nanoTime();
		
		
		int rowCount = 0;
		int indexNum = -1;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile +"_"+ format_file.format(date)+".csv")));
			int state = 0;
			int lineCnt = 0;
			try {
				while ((txt = br.readLine()) != null) {
					lineCnt++;
					if(txt.trim().equals("")){
						continue;
					}
					try {
						if (txt.trim().equals("(DQ_DOC")) {
							state = 1;
							rowCount++;
							/* HEADER 정보 필요 시 아래의 주석 제거 */
//							if (rowCount == 1) {
//								for (int s = 0; s < schemaArr.size(); s++) {
//									bw.write(schemaArr.get(s) + "\t");
//								}
//								bw.newLine();
//							}
						} else if (txt.trim().equals(")DQ_DOC")) {
							indexNum = -1;
							state = 2;
							String line = "";
							System.out.println("dataArr.length : " + dataArr.length);
							for(int l=0; l<dataArr.length; l++ ) {
								System.out.println(schemaArr.get(l) + " : " + l + " : " + dataArr[l]);
//								if(l == 6) {
//									line += insertDate + "`|";
//								}else if(l == 4) {
////									System.out.println(dataArr[dataArr.length -1]);
////									int idx = dataArr[l].indexOf("@");
////									line += dataArr[l].substring(idx+1) + "`|";
//									line += dataArr[dataArr.length -1] + "`|";
////									System.out.println(l + ": 4 : " + dataArr[dataArr.length -1]);
//								}else if(l == 0){
////									line += contsSeq + bbsId + "`|";
//									line += System.nanoTime() + bbsId + "`|";
//								}else if(l == dataArr.length-1){
////									line += dataArr[l];
//								}else if(l == dataArr.length-2){
//									line += dataArr[l];
//								}else if(l == 5) {
////									System.out.println("??????????:" + dataArr[l]);
//									line += mAIN_URL + "`|";
//								}else {
////									System.out.println("!!!!!!!!!!!:"+ dataArr[l]);
//									line += dataArr[l] + "`|";
//								}
								if(l == dataArr.length-1){
									line += dataArr[l];
								}else {
									line += dataArr[l] + "`|";
								}
							}
//							System.out.println("bbsId : " + bbsId);
							System.out.println("line >>>>>>>>>>>>>>>>>>> " + line);
						
							
							
							bw.write(line.replace(" ",""));
							bw.newLine();

							dataArr = new String[schemaArr.size()];
							
						} else {
							String openStr = txt.substring(0, 1);
							System.out.println("!zzzzzz:" +txt.substring(1, txt.trim().length()).trim());
							if(openStr.equals("(")) {
								if (schemaArr.contains(txt.substring(1, txt.trim().length()).trim())) {
									indexNum = schemaArr.indexOf(txt.substring(1, txt.trim().length()).trim());
									//System.out.println("확인>>>>>>>"+indexNum);
									//System.out.println("openStr.equals(\"(\") :  "+txt + " indexNum : " + indexNum);
									state = 3;
								}else {
									System.out.println("확인2222222222222222222>>>>>>>"+indexNum);
									//indexNum = -1;
								}
							}else if(openStr.equals(")")) {
								if (schemaArr.contains(txt.substring(1, txt.trim().length()).trim())) {
									state = 4;
								}
							}else {
//								 if (txt.trim().equals(")BBS_ID")){
//									 System.out.println("@%@#%@#%@%@%@%");
//								 }
								 dataArr[indexNum] = txt.replaceAll("`|", " ");
//								 dataArr[dataArr.length-1] = txt.replaceAll("`|", " ");
								 System.out.println("txt==============" + txt);
//								if(indexNum > 0) {
//								}

								state = 5;
							}
						}
						
//						if (txt.trim().equals(")BBS_ID")){
//							 System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//						 }

					} catch (IOException e1) {
						e1.printStackTrace();
						System.out.println("================================================ ALERT ================================================");
						System.out.println("ispiderDqdocConvert: extractFile: inputFile = " + inputFile + " " + lineCnt + " line contains error");
						System.out.println("================================================ ALERT ================================================");
					}

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			System.out.println("================================================ ALERT ================================================");
			System.out.println("ispiderDqdocConvert: extractFile: inputFile = " + outputFile);
			System.out.println("================================================ ALERT ================================================");
		}
//		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★수집 건수★★★★★★★★★★★★★★★★★★★★★★★★★");
//		System.out.println(rowCount);	
//		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★수집 건수★★★★★★★★★★★★★★★★★★★★★★★★★");
		System.out.println("end time [ "+ format.format(new Date()) +"]");
		
		
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		if(file.exists()) {
//			copyFile(inputFile, copyFile);
//		}
		System.out.println(file.getAbsolutePath());
//		if(file.delete()) {
//			System.out.println("파일 delete ! ");
//		}else {
//			System.out.println("파일 없음 .. delete 실패 ");
//		}
	
		
		// DQ_DOC 파싱 후 완료 fin 파일 생성
		createFile(outputFile+"_"+format_file.format(date)+".fin");
		
		}catch (Exception e222) {
			// TODO: handle exception
			System.out.println("************************************");
			e222.printStackTrace();
			System.out.println("************************************");
		}
	}

	private static void copyFile(String inputFile, String copyFile) {
		InputStream inStream = null;
		OutputStream outStream = null;
		
		try {
			inStream = new FileInputStream(inputFile);
			outStream = new FileOutputStream(copyFile);
			
			byte[] buffer = new byte[1024];
			int length;
			while((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	private static void createFile(String fileName) {
		File outFile = new File(fileName);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void deleteFile(String fileName) {
		String deleteFileName = String.format(OUTPUT_FILE_PATH, fileName);
		File file = new File(deleteFileName);
		if(file.exists()) {
			file.delete();
		}
	}
	

}
