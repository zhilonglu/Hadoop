package createData;
import java.io.BufferedReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class Readcsv {
	final static String EO = "\r\n";
	private String fileName = null;
	private BufferedReader br = null;
	private List<String> list = new ArrayList<String>();
	private static int count=2;
	private static long carnumber=0;
	private static long datenumber=0;
	public  Readcsv() {  }
	public  Readcsv(String fileName,List<Json> jsonList) throws IOException {
		this.fileName = fileName;
		FileInputStream fis=new FileInputStream(fileName);
		InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		String line="";
		String[] arrs=null;
		int sum=0;
		while ((line=br.readLine())!=null) {
			sum=sum+1;
			arrs=line.split(",");
			if(sum>1){
				Json json = new Json();
				json.setStrVin(arrs[1]);
				json.setUiLongitude(Double.parseDouble(arrs[3]));
				json.setUiLatitude(Double.parseDouble(arrs[0]));
				json.setUiSpeed(Double.parseDouble(arrs[2]));
				json.setUiDirection(Integer.parseInt(arrs[4]));
				String time=arrs[5];
				Long timestamp = Long.parseLong(time)*1000;
				String d = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date(timestamp));   
				json.setStrPositionTime(d);
				jsonList.add(json);}
		}
		br.close();
		isr.close();
		fis.close();
	}
	public void CsvClose()throws Exception{
		this.br.close();
	}
	public static void writeJson(List<Json> jsonList, String path) {  
		File file = new File(path);  
		FileOutputStream out = null;  
		OutputStreamWriter osw = null;  
		BufferedWriter bw = null;  
		try {  
			out = new FileOutputStream(file);  
			osw = new OutputStreamWriter(out, "UTF8");  
			bw = new BufferedWriter(osw);  
			Iterator itr = jsonList.iterator();  
			while(itr.hasNext()) {  
				Json res = (Json)itr.next();  
				ObjectMapper mapper = new ObjectMapper(); 
				String json = mapper.writeValueAsString(res); 
				json = json + EO;  
				try {  
					bw.write(json);  
				} catch (IOException e) {  
					e.printStackTrace();  
				}  
			}  
		} catch (FileNotFoundException e1) {  
			e1.printStackTrace();  
		} catch (UnsupportedEncodingException e) {  
			e.printStackTrace();  
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {  
			try {  
				bw.close();  
				osw.close();  
				out.close();  
			} catch (IOException e) {  
				e.printStackTrace();  
			}  
		}  
	}  
	public static void WriteoneFile(String onefile,String filename) throws Exception{
		List<Json> jsonList = new ArrayList<Json>();
		String strsub=null;
		if(filename.length()>=4){// 鍒ゆ柇鏄惁闀垮害澶т簬绛変簬4
			strsub=filename.substring(0,filename.length()- 4);//涓�釜鍙傛暟琛ㄧず鎴彇浼犻�鐨勫簭鍙蜂箣鍚庣殑閮ㄥ垎
		}
		String WRITEPATH;
		if(count<10&&count>0){
			WRITEPATH="E:/shenzhou/gps/201604/2016040"+count+"new/"+strsub+"_new.json";}
		else {
			WRITEPATH="E:/shenzhou/gps/201604/201604"+count+"new/"+strsub+"_new.json";
		}        
		System.out.println("WRITEPATH=" + WRITEPATH);                               
		File file = new File(WRITEPATH);
		if(!file.exists()){          
			file.createNewFile();              
		}
		writeJson(jsonList, WRITEPATH);  
	}
	public static  void readfile(String filepath) throws Exception {
		try {
			File file = new File(filepath);
			if (!file.isDirectory()) {
				System.out.println("鏂囦欢");
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "\\" + filelist[i]);
					WriteoneFile(filepath + "\\" + filelist[i], readfile.getName());
					if (!readfile.isDirectory()) {
						System.out.println("+++++++"+count+readfile.getName());
					} else if (readfile.isDirectory()) {
						readfile(filepath + "\\" + filelist[i]);
					}
				}
				count=count+1;
			}

		} catch (FileNotFoundException e) {
			System.out.println("readfile()   Exception:" + e.getMessage());
		}
	}
	public static void main(String[] args)throws Exception {
		try {
			System.out.println("1="+count);
			while(count<=10){
				if(count<10)
				{readfile("E:/shenzhou/gps/201604/2016040"+count);
				System.out.println("1="+count);
				System.out.println("start E:/shenzhou/gps/201604/2016040"+count);
				}
				else{
					readfile("E:/shenzhou/gps/201604/201604"+count);
					System.out.println("2="+count);
					System.out.println("start E:/shenzhou/gps/201604/201604"+count);
				}
			} 
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		} 
		System.out.println("ok");
	}
}


