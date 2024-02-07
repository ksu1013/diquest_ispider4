package extension;


import com.diquest.ispider.common.conf.Configuration;
import com.diquest.ispider.common.conf.Reposit;
import com.diquest.ispider.common.conf.main.BbsMain;
import com.diquest.ispider.common.conf.setting.BbsSetting;
import com.diquest.ispider.common.save.structure.Row;
import com.diquest.ispider.common.status.CollectStatus;
import com.diquest.ispider.core.collect.DqPageInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.io.Resources;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class ConnectionUtil {

	private static String resource = "config/config.properties";
	private static Properties properties = new Properties();
	private static String REGEXP_NO_FILE_STRING = "[\\\\/:*?\"<>|]";
	private static Set<String> IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList("jpg", "jpeg", "gif", "png", "pdf", "svg"));
	private static Set<String> CATEGORY_ID_DEV_LIST = new HashSet<>(Arrays.asList("81", "82", "83"));	/* 테스트용 ispider4 카테고리(그룹) ID 값 목록 (전부 DEV용 그룹임) */
	String PROXY_IP;
	int PROXY_PORT;
	private String osVersion;
	private String ipAddress;
	private String ispider4Home;

	public ConnectionUtil() {
		/* 로컬 테스트와 실제 서버 구동의 구분을 위해서 OS 및 IP 값을 가져오도록 한다. */
		osVersion = System.getProperty("os.name");
		ispider4Home = System.getenv("ISPIDER4_HOME");
		InetAddress myIP = null;
		try {
			myIP = InetAddress.getLocalHost();
			ipAddress = myIP.getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public String getPageHtml(String url) {
		StringBuffer sb = new StringBuffer();

		try {
			getProperties();

			PROXY_IP = properties.getProperty("PROXY_IP");
			PROXY_PORT = Integer.parseInt(properties.getProperty("PROXY_PORT"));

			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_IP, PROXY_PORT));
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection(proxy);

			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");

			int responseCode = con.getResponseCode();

			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

			String str;

			while ((str = in.readLine()) != null) {
				sb.append(str).append(System.lineSeparator());
			}

			in.close();

		}catch(ProtocolException e) {

			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return sb.toString();

	}
	
	/**
	 * 기존 FIN파일 생성 
	 * @param dirPath
	 * @param fileName
	 */
	public void makeFinFile(String dirPath, String fileName) {
		try {
			// FIN 파일 생성
			File fin_file = new File(dirPath, fileName + ".FIN");
			fin_file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * prodYn true일 때만 FIN파일 생성
	 * @param dirPath
	 * @param fileName
	 * @param prodYn
	 */
	public void makeFinFile(String dirPath, String fileName, boolean isTest) {
		
		if (!isTest) {
			try {
				// FIN 파일 생성
				File fin_file = new File(dirPath, fileName + ".FIN");
				fin_file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 수집 로그 CloudSQL 저장
	 * @param logInfo
	 */
	public void insertLog(HashMap<String, Object> logInfo) {
		getProperties();

		final String DB_NAME = properties.getProperty("MARIADB_LOG_DBNAME");
//		final String DB_NAME = "stc_cld_mgr";
//		final String driver = "org.gjt.mm.mysql.Driver";
//		final String DB_IP = "10.156.192.3";
//		final String DB_PORT = "3306";
//		final String DB_URL = "jdbc:mysql://"+DB_IP+":"+DB_PORT+"/"+DB_NAME;

		/*개발*/
//		final String driver = "org.mariadb.jdbc.Driver";
//		final String DB_IP = "10.10.10.214";
//		final String DB_PORT = "3306";
//		final String DB_URL = "jdbc:mariadb://"+DB_IP+":"+DB_PORT+"/"+DB_NAME;
		getProperties();
		final String driver = properties.getProperty("MARIADB_DRIVER");
		final String DB_IP = properties.getProperty("MARIADB_IP");
		final String DB_PORT = properties.getProperty("MARIADB_PORT");
		final String DB_URL = "jdbc:mariadb://"+DB_IP+":"+DB_PORT+"/"+DB_NAME;
		final String DB_ID = properties.getProperty("MARIADB_ID");
		final String DB_PW = properties.getProperty("MARIADB_PW");

		
		Connection conn = null;
		PreparedStatement pstmt = null;
		int rs = 0;
		try {

			Class.forName(driver);
			conn = DriverManager.getConnection(DB_URL, DB_ID, DB_PW);

			if (conn != null) {
				System.out.println("DB 접속성공!!");
			}

			String sql = "insert into UNITY_LOGS (LOG_CL, CL_CD, ORIGIN_CD, CRT_CNT, SCS_CNT, FAILR_CNT, SCS_DOC_CNT, FAILR_DOC_CNT, CRT_DT, SYNCHRN_YN) "
					+ "VALUES('1', ?, ?, ?, ?, ?,?, ?, NOW(), 'N')";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, (String) logInfo.get("cl_cd"));
			pstmt.setString(2, (String) logInfo.get("origin_cd"));
			pstmt.setInt(3, (int) logInfo.get("save_cnt"));
			pstmt.setInt(4, (int) logInfo.get("scs_cnt"));
			pstmt.setInt(5, (int) logInfo.get("failr_cnt"));
			pstmt.setInt(6, (int) logInfo.get("scs_doc_cnt"));
			pstmt.setInt(7, (int) logInfo.get("failr_doc_cnt"));
			rs = pstmt.executeUpdate();

		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로드 실패!");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("DB 접속실패!");
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * DQDOC 파일 파일명 생성규칙에 맞게 이름 설정 후 COLLECT 경로로 저장 (완성된 dqdoc 이동하는 케이스)
	 * @param bbsId
	 * @param originFileName
	 * @param fileName
	 */
	public void moveAndSaveFile(String bbsId, String originFileName, String fileName) {
		moveAndSaveFile(bbsId, originFileName, fileName, false);
	}

	/**
	 * DQDOC 파일 파일명 생성규칙에 맞게 이름 설정 후 COLLECT 경로로 저장
	 * @param bbsId
	 * @param bbsId
	 * @param originFileName
	 * @param fileName
	 */
	public void moveAndSaveFile(String bbsId, String originFileName, String fileName, boolean isTest) {
		try {
			String originFilePath = ispider4Home + "/dqdoc/" + bbsId + "/" + originFileName + ".UTF-8";
			String collectDir = "/mnt/nfs/collect/web";
			if (isLocal() | isTest) {
				collectDir = ispider4Home + "/dqdoc_collect";
			}
			Path origin_file = Paths.get(originFilePath);
			File collect_dir = new File(collectDir);

			if (!collect_dir.exists()) {
				System.out.println("Create Directory !!! ");
				collect_dir.mkdir();
			}

			if (origin_file.toFile().exists()) {
				Path new_file = Paths.get(collect_dir.getAbsolutePath() + "/" + fileName);
				Path newFilePath = Files.move(origin_file, new_file);

				//FIN파일 생성
				makeFinFile(collect_dir.getAbsolutePath(), fileName, isTest);
				System.out.println(newFilePath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * images node 첨부파일명 파일명 규칙에 맞춘 이름으로 변경
	 * @param prefixFileName	//바꿀 파일명 prefix
	 * @param attach	//images node 원본 파일명
	 * @return
	 */
	public String setAttachNode(String prefixFileName, HashMap<String, String> attach) {
		String resultFiles = "";
		HashMap<String, String> attaches = attach;
		prefixFileName = prefixFileName.replaceAll(".dqdoc", "");
		String pre_fileName = prefixFileName + "_" + attaches.get("document_id");
		String fileName = "";
		String[] attachFiles = attaches.get("images").split("\n");
		int index = 0;	//이미지 파일명 인덱스

		for (int idx = 0; idx < attachFiles.length; idx++) {
			String[] arr = attachFiles[idx].split("_");
			String temp = "";
			if (arr.length > 1) {
				temp = arr[1];
			}
			if (temp.indexOf(".") != 0) {	// 첨부파일에 파일명이 정상적으로 있을 경우
				int ext_idx = attachFiles[idx].lastIndexOf(".");
				String ext = "";
				if (ext_idx > 0) {
					ext = attachFiles[idx].substring(ext_idx);	//확장자
					fileName = pre_fileName + String.format("%03d", index + 1) + ext;	//파일명 생성규칙에 따른 첨부파일명
				} else {
					fileName = pre_fileName + String.format("%03d", index + 1);
				}
				resultFiles += fileName + "\n";
				index++;
			}
		}
		return resultFiles;
	}

	/**수정중
	 * images node 첨부파일명 파일명 규칙에 맞춘 이름으로 변경
	 * @param prefixFileName	//바꿀 파일명 prefix
	 * @param attach	//images node 원본 파일명
	 * @return
	 */
	public String setAttachNode2(String prefixFileName, HashMap<String, String> attach) {
		String resultFiles = "";
		HashMap<String, String> attaches = attach;
		prefixFileName = prefixFileName.replaceAll(".dqdoc", "");
		String pre_fileName = prefixFileName+"_"+attaches.get("document_id");
		String fileName = "";
		String[] attachFiles = attaches.get("images").split("\n");
		int index = 0;	//이미지 파일명 인덱스

		for (int idx = 0; idx < attachFiles.length; idx++) {
			if (attachFiles[idx].split("_")[1].indexOf(".") != 0) {	//첨부파일에 파일명이 정상적으로 있을 경우
				int ext_idx = attachFiles[idx].lastIndexOf(".");
				String ext = "";
				if (ext_idx > 0) {
					ext = attachFiles[idx].substring(ext_idx);	//확장자
					fileName = pre_fileName+String.format("%03d", index+1)+ext;	//파일명 생성규칙에 따른 첨부파일명
				} else {
					fileName = pre_fileName+String.format("%03d", index+1);
				}
				resultFiles += fileName+"\n";
				index++;
			}
		}
		return resultFiles;
	}

	/**
	 * 첨부파일 저장
	 * @param bbsId
	 * @param prefixFileName	//바꿀 파일명 prefix
	 * @param attaches_info	//첨부파일 목록 (원본파일명)
	 */
	public void moveAndSaveAttachFile(String bbsId, String prefixFileName, List<HashMap<String, String>> attaches_info) {
		moveAndSaveAttachFile(bbsId, prefixFileName, attaches_info, false);
	}

	/**
	 * 첨부파일 저장
	 * @param bbsId
	 * @param prefixFileName	//바꿀 파일명 prefix
	 * @param attaches_info	//첨부파일 목록 (원본파일명)
	 * @param isTest 테스트 상태 여부 (파일 저장 위치가 테스트 여부에 따라 달라짐)
	 */
	public void moveAndSaveAttachFile(String bbsId, String prefixFileName, List<HashMap<String, String>> attaches_info, boolean isTest) {
		try {
			String collectDirPath = "/mnt/nfs/collect/web";
			String imgBackupDirPath = "/mnt/nfs/image_backup";
			if (isLocal() | isTest) {
				collectDirPath = ispider4Home + "/dqdoc_collect";
				imgBackupDirPath = ispider4Home + "/attach_backup";
			}
			File collect_dir = new File(collectDirPath);
			File img_backup_dir = new File(imgBackupDirPath);
			if (!img_backup_dir.isDirectory()) {
				img_backup_dir.mkdir();
			}

			for (int i = 0; i < attaches_info.size(); i++) {
				HashMap<String, String> attaches = attaches_info.get(i);
				prefixFileName = prefixFileName.replaceAll(".dqdoc", "");
				String pre_fileName = prefixFileName + "_" + attaches.get("document_id");
				String fileName = "";
				String[] attachFiles = attaches.get("images").split("\n");
				int index = 0;	//이미지 파일명 인덱스

				for (int idx = 0; idx < attachFiles.length; idx++) {
					String attachFile = attachFiles[idx];
					String[] atNmUdArr = attachFile.split("_");
					if (atNmUdArr.length > 1 && atNmUdArr[1].indexOf(".") != 0) {	/* 첨부파일에 파일명이 정상적으로 있을 경우 */
						int ext_idx = attachFile.lastIndexOf(".");
						String ext = "";
						if (ext_idx > 0) {
							ext = attachFile.substring(ext_idx);	// 확장자
							fileName = pre_fileName + String.format("%03d", index + 1) + ext;	/* 파일명 생성규칙에 따른 첨부파일명 */
						} else {
							// TODO 확장자가 없는 파일은 identify에서 파일 확장자 뽑아서 붙여주기
							fileName = pre_fileName + String.format("%03d", index + 1);
						}
						Path origin_file = Paths.get(ispider4Home + "/attach/" + bbsId + "/" + attachFile);
						Path new_file = Paths.get(collect_dir.getAbsolutePath() + "/" + fileName);
						Path backup_file = Paths.get(img_backup_dir.getAbsolutePath() + "/" + fileName);
						/* 파일 존재여부 검증 */
						File attach_file = new File(ispider4Home + "/attach/" + bbsId + "/" + attachFile);
						if (attach_file.exists()) {
							Files.copy(origin_file, backup_file, StandardCopyOption.REPLACE_EXISTING);
							Path newFilePath = Files.move(origin_file, new_file, StandardCopyOption.REPLACE_EXISTING);
							File check_newFile = new File(collect_dir.getAbsolutePath() + "/" + fileName);
							if (check_newFile.exists()) {
								makeFinFile(collect_dir.getAbsolutePath(), fileName, isTest);	/* FIN파일 생성 */
							}
						}
						index++;
					} else {
						System.out.println("이미지 체크 필요: " + attachFile);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 첨부파일 저장 (수정중)
	 * @param bbsId
	 * @param prefixFileName	//바꿀 파일명 prefix
	 * @param attaches_info	//첨부파일 목록 (원본파일명)
	 * @param attaches_new_filename		//바뀔 첨부파일명 담아놓은 목록 (doc_id, attachFilename)
	 */
	public void moveAndSaveAttachFile(String bbsId, String prefixFileName, List<HashMap<String, String>> attaches_info, HashMap<String, String> attaches_new_filename) {
		moveAndSaveAttachFile(bbsId, prefixFileName, attaches_info, attaches_new_filename, false);
	}

	/**
	 * 첨부파일 저장 (수정중)
	 * @param bbsId
	 * @param prefixFileName	//바꿀 파일명 prefix
	 * @param attaches_info	//첨부파일 목록 (원본파일명)
	 * @param attaches_new_filename		//바뀔 첨부파일명 담아놓은 목록 (doc_id, attachFilename)
	 * @param isTest 테스트 상태 여부 (파일 저장 위치가 테스트 여부에 따라 달라짐)
	 */
	public void moveAndSaveAttachFile(String bbsId, String prefixFileName, List<HashMap<String, String>> attaches_info, HashMap<String, String> attaches_new_filename, boolean isTest) {
		try {
			String collectDirPath = "/mnt/nfs/collect/web";
			String imgBackupDirPath = "/mnt/nfs/image_backup";
			if (isLocal() | isTest) {
				collectDirPath = ispider4Home + "/dqdoc_collect";
				imgBackupDirPath = ispider4Home + "/attach_backup";
			}
			File collect_dir = new File(collectDirPath);
			File img_backup_dir = new File(imgBackupDirPath);
			if (!img_backup_dir.isDirectory()) {
				img_backup_dir.mkdir();
			}

			for (int i = 0; i<attaches_info.size(); i++) {
				HashMap<String, String> attaches = attaches_info.get(i);
				prefixFileName = prefixFileName.replaceAll(".dqdoc", "");
				String fileName = "";
				String[] attachFiles = attaches.get("images").split("\n");
				String[] fileNames = attaches_new_filename.get(attaches.get("document_id")).split("\n");

				for (int idx = 0; idx < attachFiles.length; idx++) {
					if (attachFiles[idx].split("_")[1].indexOf(".") != 0) {	// 첨부파일에 파일명이 정상적으로 있을 경우
						fileName = fileNames[idx];

						Path origin_file = Paths.get(ispider4Home + "/attach/" + bbsId + "/" + attachFiles[idx]);
						Path new_file = Paths.get(collect_dir.getAbsolutePath() + "/" + fileName);
						Path backup_file = Paths.get(img_backup_dir.getAbsolutePath() + "/" + fileName);

						//파일 존재여부 검증
						File attach_file = new File(ispider4Home + "/attach/" + bbsId + "/" + attachFiles[idx]);
						if (attach_file.exists()) {
							Files.copy(origin_file, backup_file, StandardCopyOption.REPLACE_EXISTING);
							Path newFilePath = Files.move(origin_file, new_file, StandardCopyOption.REPLACE_EXISTING);
							File check_newFile = new File(collect_dir.getAbsolutePath() + "/" + fileName);
							if (check_newFile.exists()) {
								//FIN파일 생성
								makeFinFile(collect_dir.getAbsolutePath(), fileName, isTest);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//이미지 깨짐여부 확인
	public static boolean isImage(String filepath){
		boolean result = false;
		File f = new File(filepath);
		try {
			BufferedImage buf = ImageIO.read(f);
			if(buf == null){
				result = false;
			} else {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 수집로그 적재할 데이터 조회 및 로그 적재
	 * @param bbsId
	 * @param clCd
	 * @param originCd
	 */
	public void makeCollectLog(String bbsId, String clCd, String originCd, String fileName, boolean error_exist) {
		HashMap<String, Object> log_info = new HashMap<>();
		int scs_cnt = 0;
		int failr_cnt = 0;
		int save_cnt = 0;
		// 실패수 조회
		int error_cnt = CollectStatus.getCollectStatus(bbsId).getErrCnt();
		// 실패 문서 수 : CollectStatus.getInstance(bbsId).getFailedDocCount();
		int error_doc_cnt = CollectStatus.getInstance(bbsId).getFailedDocCount();
		int scs_doc_cnt = CollectStatus.getInstance(bbsId).getSaveFileCount();
		System.out.println("error_doc_cnt : "+error_doc_cnt);
		if (error_cnt > 0 || error_exist) {
			failr_cnt = 1;
		} else {
			failr_cnt = 0;
		}

		//fileName에 해당하는 파일이 존재하면 생성 수 = 1
		///home/diquest/ispider4/dqdoc/"+bbsId+"/"+originFileName+".UTF-8
		File file = new File(ispider4Home + "/dqdoc/" + bbsId + "/" + fileName + ".UTF-8");
		if (file.exists()) {
			save_cnt = 1;
			scs_cnt = 1;
		} else {
			System.out.println("file is not exist!!!!");
		}

		// 수집로그 저장
		log_info.put("cl_cd", clCd);
		log_info.put("origin_cd", originCd);
		log_info.put("scs_cnt", scs_cnt);
		log_info.put("scs_doc_cnt", scs_doc_cnt);
		log_info.put("failr_cnt", failr_cnt);
		log_info.put("failr_doc_cnt", error_doc_cnt);
		log_info.put("save_cnt", save_cnt);
		insertLog(log_info);
	}

	/**
	 * gdelt 수집 모니터링 로그
	 * @param bbsId
	 * @param clCd
	 * @param originCd
	 * @param fileName
	 * @param error_exist
	 */
	public void makeGdeltCollectLog(String bbsId, String clCd, String originCd, List<String> fileNames, boolean error_exist) {
		HashMap<String, Object> log_info = new HashMap<>();
		int scs_cnt = 0;
		int failr_cnt = 0;
		int save_cnt = 0;

		//실패수 조회
		int error_cnt = CollectStatus.getCollectStatus(bbsId).getErrCnt();
		System.out.println("error_cnt : "+error_cnt);
		if (error_cnt > 0 || error_exist) {
			failr_cnt = 1;
			scs_cnt = 0;
		} else {
			failr_cnt = 0;
			scs_cnt = 1;
		}

		//fileName에 해당하는 파일이 존재하면 생성 수 = 1
		///home/diquest/ispider4/dqdoc/"+bbsId+"/"+originFileName+".UTF-8
		for(int i=0; i<fileNames.size(); i++) {
			String fileName = fileNames.get(i);
			File file = new File("/mnt/nfs/collect/gdelt/" + fileName);
			if(file.exists()) {
				save_cnt += 1;
			}else {
				System.out.println(file.getName());
				System.out.println("file is not exist!!!!");
			}
		}

		//문제 없이 수집되었으면 생성파일수 = 성공파일수
		scs_cnt = save_cnt;

		// 수집로그 저장
		log_info.put("cl_cd", clCd);
		log_info.put("origin_cd", originCd);
		log_info.put("scs_cnt", scs_cnt);
		log_info.put("failr_cnt", failr_cnt);
		log_info.put("save_cnt", save_cnt);
		insertLog(log_info);

	}

	public void getProperties() {
		try {
			Reader reader = Resources.getResourceAsReader(resource);
			properties.load(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 새로운 파일명 생성
	 * @param cl_cd
	 * @param origin_cd
	 * @param now_time
	 * @param dqPageInfo
	 * @return
	 */
	public String getNewFileName(String cl_cd, String origin_cd, String now_time, DqPageInfo dqPageInfo) {
		String newFileName = "";
		String bbs_id = String.format("%04d", Integer.parseInt(dqPageInfo.getBbsId()));

		newFileName = "WEB_" + cl_cd + origin_cd + "_" + bbs_id + "_" + now_time + ".dqdoc";

		return newFileName;
	}

	/**
	 * 기존 파일명
	 * @param dqPageInfo
	 * @return
	 */
	public String getOriginFileName(DqPageInfo dqPageInfo) {
		BbsMain bbsMain = Configuration.getInstance().getBbsMain(dqPageInfo.getBbsId());
		String originFileName = dqPageInfo.getBbsId() + "_" + bbsMain.getBbsName() + "_0";

		return originFileName;
	}

	/**
	 * 첨부파일 이미지 본문 내 위치 변환 처리 함수
	 * @param contentHtml content html 내용
	 * @param compareImages 이미지 변환을 위한 이미지 파일명 목록
	 * @return String 변경된 content html
	 */
	public String getContainImageContent(String contentHtml, List<String> compareImages) {
		return getContainImageContent(contentHtml, compareImages, false);
	}

	/**
	 * 첨부파일 이미지 본문 내 위치 변환 처리 함수
	 * @param contentHtml content html 내용
	 * @param compareImages 이미지 변환을 위한 이미지 파일명 목록
	 * @return String 변경된 content html
	 */
	public String getContainImageContent(String contentHtml, List<String> compareImages, boolean isATagCheck) {

		Document contentDoc = Jsoup.parse(contentHtml);

		/* pdf 있는지 여부 체크, pdf가 아니면 해당 요소 밖으로 꺼내고 a태그는 삭제한다. */
		if (isATagCheck) {
			Elements aTags = contentDoc.select("a");
			for (int cnt = 0; cnt < aTags.size(); cnt++) {
				Element aTag = aTags.get(cnt);
				String href = aTag.attr("href");
				if (!href.contains(".pdf")) {
					Elements children = aTag.children();    /* a 태그 안의 자식 요소들을 추출 */
					aTag.parent().insertChildren(aTag.siblingIndex(), children);    /* a 태그의 부모 요소에 자식 요소들 추가 */
					aTag.remove();    /* a 태그 제거 */
				}
			}
		}

		Elements imageElements = contentDoc.select("img");
		Set<Integer> fileNameNumSet = new HashSet<>();	/* 2023-05-10 jhjeon: compareImages의 순서 체크, 한번 변환한 이미지 태그는 다시 바꾸지 않기 위한 조치이다. */
		for (int imageCnt = 0; imageCnt < imageElements.size(); imageCnt++) {
			for (int compareImageCnt = 0; compareImageCnt < compareImages.size(); compareImageCnt++) {
				if (fileNameNumSet.contains(compareImageCnt)) {
					continue;
				} else {
					Element imageElement = imageElements.get(imageCnt);
					String imageSrc = imageElement.attr("src");
					String contentImage = compareImages.get(compareImageCnt);
					if (StringUtils.contains(imageSrc, contentImage)) {
						Element newElement = new Element(Tag.valueOf("DQOBJECT"), "");
						newElement.text("<FileName>" + (compareImageCnt + 1) + "</FileName>");
						imageElement.replaceWith(newElement);
						fileNameNumSet.add(compareImageCnt);
						break;
					}
				}
			}
		}

		String changedContentHtml = contentDoc.body().html();
		changedContentHtml = prettyDqObjectTags(changedContentHtml);	/* DQOBJECT 태그 한줄 정리 처리용 함수 분리 후 해당 함수는 row 값 변경 전 맨 마지막에 진행한다. */

		return changedContentHtml;
	}

	public String prettyDqObjectTags(String contentHtml) {
		String changedContentHtml = contentHtml;
		changedContentHtml = changedContentHtml.replace("<dqobject>", "<DQOBJECT>");	/* 대문자로 변환 */
		changedContentHtml = changedContentHtml.replace("</dqobject>", "</DQOBJECT>");
		changedContentHtml = changedContentHtml.replaceAll("<DQOBJECT>\\s{2,}", "<DQOBJECT>");
		changedContentHtml = changedContentHtml.replaceAll("<DQOBJECT>\\n", "<DQOBJECT>");
		changedContentHtml = changedContentHtml.replaceAll("\\s{2,}</DQOBJECT>", "</DQOBJECT>");
		changedContentHtml = changedContentHtml.replaceAll("\\n</DQOBJECT>", "</DQOBJECT>");
		changedContentHtml = changedContentHtml.replace("&lt;filename&gt;", "<FileName>");
		changedContentHtml = changedContentHtml.replace("&lt;/filename&gt;", "</FileName>");
		changedContentHtml = changedContentHtml.replace("&lt;Filename&gt;", "<FileName>");
		changedContentHtml = changedContentHtml.replace("&lt;/Filename&gt;", "</FileName>");
		changedContentHtml = changedContentHtml.replace("&lt;fileName&gt;", "<FileName>");
		changedContentHtml = changedContentHtml.replace("&lt;/fileName&gt;", "</FileName>");
		changedContentHtml = changedContentHtml.replace("&lt;FileName&gt;", "<FileName>");
		changedContentHtml = changedContentHtml.replace("&lt;/FileName&gt;", "</FileName>");
		changedContentHtml = changedContentHtml.replace("<filename>", "<FileName>");
		changedContentHtml = changedContentHtml.replace("</filename>", "</FileName>");
		changedContentHtml = changedContentHtml.replace("<Filename>", "<FileName>");
		changedContentHtml = changedContentHtml.replace("</Filename>", "</FileName>");
		changedContentHtml = changedContentHtml.replace("<fileName>", "<FileName>");
		changedContentHtml = changedContentHtml.replace("</fileName>", "</FileName>");
		changedContentHtml = changedContentHtml.replaceAll("\\s{2,}<FileName>", "<FileName>");
		changedContentHtml = changedContentHtml.replaceAll("<FileName>\\s{2,}", "<FileName>");
		changedContentHtml = changedContentHtml.replaceAll("</FileName>\\s{2,}", "</FileName>");
		changedContentHtml = changedContentHtml.replaceAll("\\s{2,}</FileName>", "</FileName>");
		changedContentHtml = changedContentHtml.replaceAll("<FileName>\\n", "<FileName>");
		changedContentHtml = changedContentHtml.replaceAll("\\n</FileName>", "</FileName>");

		return changedContentHtml;
	}

	/**
	 * 첨부파일 이미지 본문 내 위치
	 *
	 * @param nodeValue
	 * @param content_images
	 * @return String
	 */
	public List<String> setAttachImageValue(List attaches_info, HashMap<String, String> attach, String images, int doc_id) {
		// 현재 문서 첨부파일
		List<String> content_images = new ArrayList<>();

		attach.put("document_id", String.format("%06d", doc_id));
		attach.put("images", images);

		attaches_info.add(attach);

		content_images = Arrays.asList(images.split("\n"));
		int image_idx = 0;
		for (int idx = 0; idx < content_images.size(); idx++) {
			String image = content_images.get(idx);
			if (!image.substring(image.indexOf("_") + 1).startsWith(".")) {
				content_images.set(image_idx, image.substring(image.indexOf("_") + 1));
				image_idx++;
			}
		}

		return content_images;
	}

	/**
	 * 수집문서 row의 content image 체크 (header null 처리, 이미지 다운로드 간 딜레이 부여 여부 false)
	 *
	 * @param  row
	 * @param  dqPageInfo
	 */
	public void checkContentImage(
			Row row,
			DqPageInfo dqPageInfo,
			List<HashMap<String, String>> attaches_info,
			String file_name,
			String documentId,
			String cl_cd,
			String origin_cd,
			String now_time
	) throws Exception {
		checkContentImage(row, dqPageInfo, attaches_info, file_name, documentId, cl_cd, origin_cd, now_time, null, null, null, false, 0, 0);
	}

	/**
	 * 수집문서 row의 content image 체크 (이미지 다운로드 간 딜레이 부여 여부 false)
	 *
	 * @param  row
	 * @param  dqPageInfo
	 */
	public void checkContentImage(
			Row row,
			DqPageInfo dqPageInfo,
			List<HashMap<String, String>> attaches_info,
			String file_name,
			String documentId,
			String cl_cd,
			String origin_cd,
			String now_time,
			Map<String, String> header
	) throws Exception {
		checkContentImage(row, dqPageInfo, attaches_info, file_name, documentId, cl_cd, origin_cd, now_time, header, null, null, false, 0, 0);
	}

	/**
	 * 수집문서 row의 content image 체크
	 * 수집 url 머
	 * 이미지 다운로드 간 딜레이 부여 여부 false
	 *
	 * @param  row
	 * @param  dqPageInfo
	 */
	public void checkContentImage(
			Row row,
			DqPageInfo dqPageInfo,
			List<HashMap<String, String>> attaches_info,
			String file_name,
			String documentId,
			String cl_cd,
			String origin_cd,
			String now_time,
			Set<String> checkUrlPattern
	) throws Exception {
		checkContentImage(row, dqPageInfo, attaches_info, file_name, documentId, cl_cd, origin_cd, now_time, null, checkUrlPattern, null, false, 0, 0);
	}

	/**
	 * 수집문서 row의 content image 체크 (이미지 다운로드 간 딜레이 부여 여부 false)
	 *
	 * @param  row
	 * @param  dqPageInfo
	 */
	public void checkContentImage(
			Row row,
			DqPageInfo dqPageInfo,
			List<HashMap<String, String>> attaches_info,
			String file_name,
			String documentId,
			String cl_cd,
			String origin_cd,
			String now_time,
			Map<String, String> header,
			Set<String> checkUrlPattern
	) throws Exception {
		checkContentImage(row, dqPageInfo, attaches_info, file_name, documentId, cl_cd, origin_cd, now_time, header, checkUrlPattern, null, false, 0, 0);
	}

	/**
	 * 수집문서 row의 content image 체크
	 *
	 * @param  row
	 * @param  dqPageInfo
	 */
	public void checkContentImage(
			Row row,
			DqPageInfo dqPageInfo,
			List<HashMap<String, String>> attaches_info,
			String file_name,
			String documentId,
			String cl_cd,
			String origin_cd,
			String now_time,
			Map<String, String> header,
			boolean isDelay,
			int maxDelay,
			int minDelay
	) throws Exception {
		checkContentImage(row, dqPageInfo, attaches_info, file_name, documentId, cl_cd, origin_cd, now_time, header, null, null, isDelay, maxDelay, minDelay);
	}

	/**
	 * 수집문서 row의 content image 체크
	 *
	 * @param  row
	 * @param  dqPageInfo
	 */
	public void checkContentImage(
			Row row,
			DqPageInfo dqPageInfo,
			List<HashMap<String, String>> attaches_info,
			String file_name,
			String documentId,
			String cl_cd,
			String origin_cd,
			String now_time,
			Map<String, String> header,
			Set<String> checkUrlPattern,
			Set<String> prohibitUrlPattern,
			boolean isDelay,
			int maxDelay,
			int minDelay
	) throws Exception {

		String url = "";
		int imagesKey = 0;
		String images = "";
		int imagePathKey = 0;
		String imagePath = "";
		int contentKey = 0;
		String content = "";
		List<String> contentImages = new ArrayList<>();     /* 현재 문서 첨부파일명을 저장할 리스트 */
		String dummyImages = "";
		String dummyImagePathList = "";

		int image_idx = 0;
		HashMap<String, String> attach = new HashMap<>();
		for (int i = 0; i < row.size(); i++) {
			String nodeName = row.getNodeByIdx(i).getName();
			String nodeValue = row.getNodeByIdx(i).getValue();
			if (nodeName.equals("images")) {
				imagesKey = i;
				images = nodeValue;
			}
			if (nodeName.equals("image_path")) {
				imagePathKey = i;
				imagePath = nodeValue;
			}
			if (nodeName.equals("content")) {
				contentKey = i;
				content = nodeValue;
			}
			if (nodeName.equals("url")) {
				url = nodeValue;
			}
		}

		if (!"".equals(images)) {
			images = images.replaceAll("\r\n", "\n");
			String[] valueArr = images.split("\n");
			contentImages = new ArrayList<>(Arrays.asList(valueArr));   /* 현재 문서 첨부파일명 리스트로 저장 */
			for (int idx = 0; idx < contentImages.size(); idx++) {  /* 본래 파일명 앞에 붙은 String 값 제거 */
				String image = contentImages.get(idx);
				if (!image.substring(image.indexOf("_") + 1).startsWith(".")) {
					contentImages.set(image_idx, image.substring(image.indexOf("_") + 1));
					image_idx++;
				}
			}
		}

		if (!"".equals(content)) {  // 현재 문서 첨부파일 매핑 위치 설정
			content = getContainImageContent(content, contentImages);
			/* <!-- 2023-04-28 jhjeon 추가: img 태그 남아있는 케이스에 대한 조치 추가 --> */
			Document contentDoc = Jsoup.parse(content); /* 남아있는 케이스 다시 체크 & 다운로드 처리 */
			Elements attachDumpElements = contentDoc.select("img, a");	/* img 및 a 태그 목록 */
			Elements attachElements = new Elements();
			for (Element element : attachDumpElements) {
				if (element.tagName().equals("img")) {
					attachElements.add(element);
				} else {
					String href = element.attr("href");
					if (href != null) {
						if (href.toLowerCase().contains(".pdf")) {
							attachElements.add(element);
						}
					}
				}
			}
			String attachFolderPath = ispider4Home + "/attach/" + dqPageInfo.getBbsId() + "/";
			File attachFolder = new File(attachFolderPath);
			if (!attachFolder.exists()) {
				boolean created = attachFolder.mkdirs();
				if (created) {
					System.out.println("폴더를 생성했습니다: " + attachFolder);
				}   /* 폴더 생성 이외의 메세지는 무의미해서 따로 조치하지 않음 */
			}
			if (attachElements.size() > 0) {
				for (Element attachElement : attachElements) {
					String src = "";
					String linkAttrKey = "src";
					if ("a".equalsIgnoreCase(attachElement.tagName())) {
						src = attachElement.attr("href");
					} else {
						src = attachElement.attr("src");
					}
					boolean attachUrlCheck = true; /* 수집 여부 확인, 기본적으로 true 설정하고 url 패턴 리스트에 따라 수집여부 변경 */
					if (src == null || "".equals(src)) {	/* src 값이 null이거나 빈값이면 첨부파일 수집 처리 false */
						attachUrlCheck = false;
					}
					if (attachUrlCheck && checkUrlPattern != null) {	/* checkUrlPattern 목록이 있을 경우 수집 여부 기본값을 false로 변경, 목록에 있을 경우에만 true 처리 */
						attachUrlCheck = false;
						for (String pattern : checkUrlPattern) {
							if (Pattern.matches(pattern, src)) {
								attachUrlCheck = true;
								break;
							}
						}
					}
					if (attachUrlCheck && prohibitUrlPattern != null) {	/* prohibitUrlPattern 목록이 있을 경우 수집 대상 src 값을 체크해서 prohibitUrlPattern에 해당되는 케이스는 false 처리 */
						for (String pattern : prohibitUrlPattern) {
							if (Pattern.matches(pattern, src)) {
								attachUrlCheck = false;
								break;
							}
						}
					}

					if (attachUrlCheck) {
						if (!src.startsWith("https://") && !src.startsWith("http://")) {	/* src 앞이 https:// 또는 http:// 으로 시작하지 않을 경우 처리 */
							if (src.startsWith("//")) {    /* 주소 맨 앞에 프로토콜이 작성되어 있지 않는 경우 */
								if (url.contains("http://")) {
									src = "http:" + src;
								} else if (url.contains("https://")) {
									src = "https:" + src;
								}
							} else if (src.startsWith("/")) {    /* 이미지 주소에 프로토콜 및 도메인이 없는 경우 */
								URL temp = new URL(url);
								String protocol = temp.getProtocol();
								String domain = temp.getHost();
								src = protocol + "://" + domain + src;
							} else if (src.startsWith("data:image/")) {
								if (attachElement.hasAttr("data-lazyload")) {        /* 2023-05-10 jhjeon: 이미지 주소가 src attribute에 있지 않고 별도 attribute에 있는 경우 조치 추가 */
									String dataLazyload = attachElement.attr("data-lazyload");
									attachElement.attr(linkAttrKey, dataLazyload);
									src = dataLazyload;
								} else if (attachElement.hasAttr("data-src")) {    /* src에 들어갈 데이터를 dataSrc 값으로 변경한다. */
									String dataSrc = attachElement.attr("data-src");
									attachElement.attr(linkAttrKey, dataSrc);
									src = dataSrc;
								}
							} else if (src.startsWith(".")) {    /* 2023-05-10 jhjeon: 이미지 주소 앞부분이 ./으로 시작할 경우의 조치 추가, 2023-06-21: 2차 수정 */
								src = getFullImageUrl(url, src);
							} else {	/* 2023-06-09 jhjeon: src 앞이 https:// 또는 http:// 으로 시작하지 않을 경우 위의 조건문(특이케이스) 외의 조치 추가 */
								String fileName = "";
								String[] srcPathArr = src.split("/");
								fileName = srcPathArr[srcPathArr.length - 1];
								URL urlObj = new URL(url);
								String path = urlObj.getPath();
								String pageName = path.substring(path.lastIndexOf("/") + 1);
								String prefixUrl = url.replace(pageName, "");
								String fullImageSrc = prefixUrl + fileName;
								attachElement.attr(linkAttrKey, fullImageSrc);
								src = fullImageSrc;
							}
						}
						String replaceSrc = src.toString();	/* 중복 파일명이 있어 파일명을 변경해야 할 경우, image element의 src 값도 변경하기 위한 용도로 별도 변수를 생성 (기존 src는 다운로드 시까지 해당 값을 가지고 있어야 하므로 별도로 가지고 있어야만 함) */

						URL pageUrlObj = null;
						boolean isCurrentUrl = true;
						try {    /* 2023-05-03 jhjeon: src 값이 정상적인 url 값인지 체크한다. */
							pageUrlObj = new URL(src);
						} catch (MalformedURLException e) {
							System.out.println(src + " 값은 정상적인 url이 아니므로 첨부파일 수집에서 제외합니다.");
							isCurrentUrl = false;
						}

						if (isCurrentUrl) {
							/* <!-- 2023-05-02 jhjeon: 이미지 url을 체크해서 http 또는 https 프로토콜이 없는 경우 full url을 만드는 로직이 추가되어야 함 --> */
							if (!src.contains("http")) {
								String imageProtocol = pageUrlObj.getProtocol();
								String imageHost = pageUrlObj.getHost();
								String pageSubPath = pageUrlObj.getPath();
								src = imageProtocol + "://" + imageHost + pageSubPath + "/" + src;
								System.out.println("change img url: " + src);
							}   /* <!-- 2023-05-02 jhjeon: 해당 부분은 여기까지 작업 --> */
							String realFileName = src.substring(src.lastIndexOf("/") + 1);
							String originalFileName = replaceSrc.substring(src.lastIndexOf("/") + 1);
							realFileName = realFileName.replaceAll(REGEXP_NO_FILE_STRING, "");	/* 2023-05-23 jhjeon: 파일명에 들어갈 수 없는 문자 삭제 로직 추가 */
							String fileName = documentId + "_" + realFileName;
							String filePath = attachFolderPath + fileName;
							File downloadFile = new File(filePath);
							/* 파일 다운로드 전 동일한 파일이 있는지 체크하기 위한 준비작업 */
							int baseFileCount = 0;
							String baseFileExtension = getFileExtension(realFileName);
							String baseFileName = "";
							String baseRealFileName = "";
							if (baseFileExtension == null) {
								baseFileName = fileName;
								baseRealFileName = realFileName;
							} else {
								int dotIndex = fileName.lastIndexOf('.');
								if (dotIndex != -1) {
									baseFileName = fileName.substring(0, dotIndex);
								}
								int rfDotIndex = realFileName.lastIndexOf('.');
								if (rfDotIndex != -1) {
									baseRealFileName = realFileName.substring(0, rfDotIndex);
								}
							}
							/* 파일 다운로드 전에 동일한 파일이 있는지를 체크한다. 이미 존재하는 파일이면 파일명을 변경한다. (DQOBJECT 변환을 위해서 실제 content 안의 이미지 태그 src도 변경한다.) */
							while (downloadFile.exists()) {
								baseFileCount++;
								if (baseFileExtension == null) {
									fileName = baseFileName + "_" + baseFileCount;
									realFileName = baseRealFileName + "_" + baseFileCount;
								} else {
									fileName = baseFileName + "_" + baseFileCount + "." + baseFileExtension;
									realFileName = baseRealFileName + "_" + baseFileCount + "." + baseFileExtension;
								}

								filePath = attachFolderPath + fileName;
								downloadFile = new File(filePath);
							}

							try {   /* 파일 다운로드 부분 */
								TrustManager[] trustAllCerts = new TrustManager[] {	/* SSL 인증서 검증 비활성화 */
									new X509TrustManager() {
										public java.security.cert.X509Certificate[] getAcceptedIssuers() {
											return null;
										}
										public void checkClientTrusted(
												java.security.cert.X509Certificate[] certs, String authType) {
										}
										public void checkServerTrusted(
												java.security.cert.X509Certificate[] certs, String authType) {
										}
									}
								};

								HttpURLConnection conn = (HttpURLConnection) new URL(src).openConnection();
								// SSL 인증서 검증 비활성화
								SSLContext sslContext = SSLContext.getInstance("SSL");
								sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
								HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
								// 프록시 설정 부분
								if (isProxy()) {
									String proxyHost = getProxyIp();
									int proxyPort = getProxyPortNumber();
									Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
									conn = (HttpURLConnection) new URL(src).openConnection(proxy);
								}
								// 헤더 설정 부분
								if (header != null) {
									for (String key : header.keySet()) {
										String headerAttrValue = header.get(key);
										conn.setRequestProperty(key, headerAttrValue);
									}
								}
								InputStream in = conn.getInputStream();
								OutputStream out = FileUtils.openOutputStream(downloadFile);
								IOUtils.copy(in, out);
								IOUtils.closeQuietly(in);
								IOUtils.closeQuietly(out);
								if (!checkImageExtension(filePath)) {
									String extension = ImageExtensionIdentifier.getImageExtension(filePath);
									if (extension == null) {
										for (String ext : IMAGE_EXTENSIONS) {
											if (realFileName.contains("." + ext + "?")) {
												extension = ext;
												break;
											}
											String[] arr = realFileName.split("\\.");
											String prevExt = arr[arr.length - 1];
											if (prevExt.contains(ext)) {
												extension = ext;
												break;
											}
										}
									}
									if (extension != null) {
										String[] arr = realFileName.split("\\.");
										String changeFileName = realFileName;
										if (arr.length > 1) {
											String prevExt = arr[arr.length - 1];
											changeFileName = changeFileName.replace(prevExt, extension);
										} else {
											changeFileName += "." + extension;
										}
										fileName = documentId + "_" + changeFileName;
										filePath = attachFolderPath + fileName;
										File renameFile = new File(filePath);
										downloadFile.renameTo(renameFile);
									}
								}
								System.out.println(fileName + " File downloaded successfully.");
								if (isDelay) {    /* 파일 다운로드 간 딜레이를 주어야 하는 경우에만 sleep으로 딜레이를 준다. */
									Random random = new Random();
									int delay = random.nextInt(maxDelay - minDelay + 1) + minDelay;
									try {
										Thread.sleep(delay); /* 랜덤 시간 동안 대기 */
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							} catch (IOException e) {
								System.out.println("Error while downloading the file " + fileName + ": " + e.getMessage());
								e.printStackTrace();
							}

							if ("".equals(dummyImagePathList)) {
								dummyImagePathList = filePath;
							} else {
								dummyImagePathList += "\n" + filePath;
							}
							contentImages.add(realFileName);
							if (!realFileName.equals(originalFileName)) {	/* 2023-05-30 jhjeon: 실제 img의 src 값 변경 (DQOBJECT 변경 처리에 필요) */
								replaceSrc = replaceSrc.replace(originalFileName, realFileName);
								attachElement.attr(linkAttrKey, replaceSrc);
							}
							if ("".equals(dummyImages)) {
								dummyImages = fileName;
							} else {
								dummyImages += "\n" + fileName;
							}
						}
					}
				}

				if ("".equals(imagePath)) {
					imagePath = dummyImagePathList;
				} else {
					imagePath += "\n" + dummyImagePathList;
				}
				if ("".equals(images)) {
					images = dummyImages;
				} else {
					images += "\n" + dummyImages;
				}
			}
			content = contentDoc.html();
			/* 마저 체크안된 이미지 정보들을 images 및 image_path에, 그리고 다시 변경된 content를 content 필드에 넣는다. */
			content = getContainImageContent(content, contentImages, true);
			row.getNodeByIdx(imagesKey).clearValue();
			row.getNodeByIdx(imagesKey).setValue(images);
			row.getNodeByIdx(imagePathKey).clearValue();
			row.getNodeByIdx(imagePathKey).setValue(imagePath);
			row.getNodeByIdx(contentKey).clearValue();
			row.getNodeByIdx(contentKey).setValue(content);
		}

		if (!"".equals(images)) {   /* 첨부파일명 변경 */
			attach.put("document_id", documentId);
			attach.put("images", images);
			attaches_info.add(attach);
			file_name = getNewFileName(cl_cd, origin_cd, now_time, dqPageInfo);
			String attachFiles = setAttachNode(file_name, attach);   /* 첨부파일명 저장 */
			row.getNodeByIdx(imagesKey).clearValue();
			row.getNodeByIdx(imagesKey).setValue(attachFiles);
		}
	}

	/**
	 * CREATED_DATE 값을 정해진 형식으로 변경
	 * @param dateFormat 변경해야 할 날짜 String Format 형식
	 * @param inputDate 포맷을 변경할 날짜값
	 * @return String 새로 포맷된 날짜 값
	 */
	public String formatCurrentCreatedDate(String dateFormat, String inputDate) {

		String format = "yyyy-MM-dd";
		if (dateFormat.contains("HH")) {
			format += " HH";
			if (dateFormat.contains(":mm")) {
				format += ":mm";
				if (dateFormat.contains(":ss")) {
					format += ":ss";
				}
			}
		}
		if ("yyyy-MM-dd".equals(format)) {
			format += " HH:mm:ss";
			dateFormat += " HH:mm:ss";
			inputDate += " 00:00:01";
		}
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(dateFormat);
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(format);
		LocalDateTime dateTime = LocalDateTime.parse(inputDate, inputFormatter);
		String outDate = dateTime.format(outputFormatter);

		return outDate;
	}

	/**
	 * 이미지 확장자 여부를 확인하는 함수
	 * @param filePath 파일 경로
	 * @return boolean 이미지 확장자 여부 (true면 이미지 확장자)
	 */
	public boolean checkImageExtension(String filePath) {
		String fileExtension = getFileExtension(filePath);
		if (fileExtension != null) {
			String lowercaseExtension = fileExtension.toLowerCase();
			return IMAGE_EXTENSIONS.contains(lowercaseExtension);
		}
		return false;
	}

	/**
	 * 파일 확장자를 가져온다
	 * @param filePath 파일 경로
	 * @return 확장자
	 */
	private String getFileExtension(String filePath) {
		int dotIndex = filePath.lastIndexOf('.');
		if (dotIndex > 0 && dotIndex < filePath.length() - 1) {
			return filePath.substring(dotIndex + 1);
		}
		return null;
	}

	/**
	 * PROXY IP 호출
	 * @return String PROXY IP 값
	 */
	public String getProxyIp() {
		getProperties();
		String proxyIp = properties.getProperty("PROXY_IP");
		return proxyIp;
	}

	/**
	 * PROXY PORT 호출
	 * @return String PROXY PORT 값
	 */
	public String getProxyPort() {
		getProperties();
		String proxyPort = properties.getProperty("PROXY_PORT");
		return proxyPort;
	}

	/**
	 * PROXY PORT 호출 (integer)
	 * @return int PROXY PORT 값
	 */
	public int getProxyPortNumber() {
		String proxyPort = getProxyPort();
		int proxyPortInt = Integer.parseInt(proxyPort);
		return proxyPortInt;
	}

	/**
	 * 로컬 테스트를 위해 현재 로컬에서 작업중인지 여부를 확인하는 함수
	 * @return boolean 로컬 여부
	 */
	public boolean isLocal() {
		boolean isLocal = false;
		if (osVersion.contains("Windows")
		  && (ipAddress.startsWith("192.168.") || ipAddress.startsWith("169.254."))) {
			isLocal = true;
		}

		return isLocal;
	}

	/**
	 * 프록시를 사용해야 하는 환경에서 프록시 적용 여부를 확인하는 함수
	 * @return boolean 프록시 사용 여부
	 */
	public boolean isProxy() {
		boolean isProxy = false;
		if (isLocal()) {	/* 로컬이지만 10층 전제현 개발 PC 로컬은 프록시를 적용한다. */
			String userName = System.getProperty("user.name");
			if ("qer34t".equalsIgnoreCase(userName)) {
				isProxy = true;
			}
		} else {	/* 로컬이 아닌 경우 프록시를 적용한다. */
			isProxy = true;
		}

		return isProxy;
	}

	/**
	 * 현재 ispider4 그룹이 테스트용 그룹인지 체크한다
	 * @param bbsSetting ISpider4 BbsSetting 변수
	 * @return boolean 테스트용 그룹 여부 확인
	 */
	public boolean isTest(Reposit bbsReposit) {
		String name = bbsReposit.getName();
		if (name.contains("DEV") || name.contains("TEST")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Full Url이 아닌 이미지 url을 Full Url로 생성한다.
	 * @param currentPageUrl 현재 페이지 주소
	 * @param relativeImagePath 이미지 상대 경로
	 * @return String full
	 */
	private static String getFullImageUrl(String currentPageUrl, String relativeImagePath) {
		try {
			URI currentUri = new URI(currentPageUrl);
			URL currentUrl = currentUri.toURL();

			// 현재 페이지의 URL과 이미지의 상대 경로를 결합하여 절대 경로로 만듭니다.
			URL imageUrl = new URL(currentUrl, relativeImagePath);

			return imageUrl.toString();
		} catch (URISyntaxException | java.net.MalformedURLException e) {
			e.printStackTrace();
		}

		return relativeImagePath;
	}

	public static void main(String[] args) {
//		Map<String, String> env = System.getenv();
//		String os = System.getProperty("os.name");
//		String userName = System.getProperty("user.name");
//		InetAddress myIP = null;
//		try {
//			myIP = InetAddress.getLocalHost();
//		} catch (UnknownHostException e) {
//			throw new RuntimeException(e);
//		}
//		System.out.println("Current OS is: " + os);
//		System.out.println("My IP Address is: " + myIP.getHostAddress());
//		System.out.println("User Name: " + userName);
//		String computerName = env.get("COMPUTERNAME");
//		System.out.println("컴퓨터 이름: " + computerName);
//		String currentPageUrl = "http://example.com/path1/path2/path3/path4/test.html";
//		String relativeImagePath = "../../../img/hello.png";
//		String fullImageUrl = getFullImageUrl(currentPageUrl, relativeImagePath);
//		System.out.println("Full Image URL: " + fullImageUrl);
	}
}