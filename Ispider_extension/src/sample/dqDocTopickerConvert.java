package sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class dqDocTopickerConvert {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputDir = "C:\\Users\\Dell\\Desktop\\dqdoc_test\\";
		String outputDir = "C:\\Users\\Dell\\Desktop\\dqdoc_test2\\TEST_";
		
		if(args.length > 0) {
			inputDir = args[0];
			outputDir = args[1];
		}
		
		String title = "";
		String contents = "";
		String keywords = "";
		String dq_id="";
		
		int tchk = 0;
		int cchk = 0;
		
		Pattern pt1 = Pattern.compile("");
		Matcher mc1 = pt1.matcher("");

		
	
		BufferedReader br = null;
		
		String input = inputDir;
		//System.out.println("input>>>>>>>>>"+input);

		File inFile = new File(input);
		//System.out.println("inFile>>>>>>>>>"+inFile);
//		File[] fileList = inFile.listFiles();
		File[] fileList=inFile.listFiles();
		//System.out.println("fileList>>>>>>>>"+fileList);
		BufferedWriter bw = null;
		
		//TopickerExtract keywordExtractor = new TopickerExtract();
	

		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		System.out.println("%$#@! start time [ "+ format.format(new Date()) +"]");
		
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		
		if(fileList != null){
			for (File file : fileList) {
				
				System.out.println("dqDocTopickerConvert: extractFile: inputFile = " + inputDir + file.getName() +" start time [ "+ format1.format(new Date()) +"]");
				try {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(inputDir + file.getName() )));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					System.out.println("================================================ ALERT ================================================");
					System.out.println("dqDocTopickerConvert: extractFile: inputFile = " + inputDir + file.getName());
					System.out.println("================================================ ALERT ================================================");
				}

				String output1 = outputDir + file.getName();
				//System.out.println("output1>>>>>>>>>"+output1);
				File outFile = new File(output1);
				//System.out.println("outFile>>>>>>>>>"+outFile);
				String txt = "";
				int rowCount = 0;
				try {
					
					bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
					//System.out.println("bw..............>>>>>>>>>>>"+bw);
					
					int state = 0;
					int lineCnt = 0;
					try {
						
						
						while ((txt = br.readLine()) != null) {
							//System.out.println("txt>>>>>>>>>"+txt);
							lineCnt++;
							
							if(state == 3) {
								tchk ++;
							}else if(state == 5) {
								cchk++;
							}
							
							//System.out.println("txt>>>>>>>>>>"+txt);
							
							//String temp=SubStringResult("<Data>", "</Data>", txt);
							//System.out.println("temp>>>>>>>>>>"+temp);
						
					
						
						
							
							
//							pt1=Pattern.compile("<petiNo>(.*?)<\\/petiNo>");
//							mc1=pt1.matcher(txt.trim());
//							
//							if (mc1.find()) {
//								// System.out.println("@@@@");
//								dq_id += mc1.group(1);
//							}
//							
//							System.out.println("dq_id>>>>>>>>>"+dq_id);
//							
							
							
							
							
							
							
							
							
							
							
							
							
							
							
							
							
							
							
							
							
							
							if (txt.trim().contains("<petiNo>")) {
							
								System.out.println("*******************************##########");
								state = 1;
								rowCount++;
								title = "";
								contents = "";
								keywords = "";
							}else if (txt.trim().contains("</petiNo>")) {
								System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
								state = 2;
							} 
							else if (txt.trim().equals("<title>")) {
								state = 3;
								tchk = 0;
								cchk = 0;
							}  else if (txt.trim().equals("</title>")) {
								state = 4;
								tchk = 0;
								cchk = 0;
							} else if (txt.trim().equals("<reason>")) {
								state = 5;
								tchk = 0;
								cchk = 0;	
							}else if (txt.trim().equals("</reason>")) {
								state = 6;
								tchk = 0;
								cchk = 0;	
							}


							if(state == 2) {
								String TYPE = "";
								if(file.getName().contains("BILL")) {
									TYPE = "BILL";
									//keywords =  keywordExtractor.exTractorBill(title, "");  
								}else if(file.getName().contains("NEWS")) {
									TYPE = "NEWS";
									//keywords = keywordExtractor.exTractorNews(title, contents);  
								}else if(file.getName().contains("MINUTES")) {
									TYPE = "MINUTES";
									//keywords = keywordExtractor.exTractorMinutes(title, "");  
								}

								
								bw.write("(DQ_DOC");
								bw.newLine();
								bw.write(" ");
								bw.newLine();
								bw.write(")DQ_DOC");
								bw.newLine();
								bw.write("(TITLE");
								bw.newLine();
								bw.write(" ");
								bw.newLine();
								bw.write(")TITLE");
								bw.newLine();
								bw.write("(CONTENT");
								bw.newLine();
								bw.write(" ");
								bw.newLine();
								bw.write(")CONTENT");
								bw.newLine();
								
							}
							
							//bw.write(txt);
							bw.newLine();
							
							//System.out.println("state"+state);
							System.out.println("txt>>>>"+txt);
							if(tchk > 0) {
								title += txt + "\n";
							}else if(cchk > 0) {
								contents += txt + "\n";
							}

						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
					System.out.println("================================================ ALERT ================================================");
					System.out.println("dqDocTopickerConvert: extractFile: inputFile = " + output1);
					System.out.println("================================================ ALERT ================================================");
				} 
				
				
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
				System.out.println("%$#@! end time [ "+ format2.format(new Date()) +"]");
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		SimpleDateFormat format3 = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		System.out.println("%$#@! file all end time [ "+ format3.format(new Date()) +"]");
	}
	
	public static String SubStringResult(String startTag, String endTag, String text) {
		
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

}
