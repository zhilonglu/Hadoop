package HDFSAPI;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
public class HDFSWriteHelloWorld {
	public static void main(String[] args) throws Exception {
		try {
			//uploadToHdfs();  
			//deleteFromHdfs();
			//getDirectoryFromHdfs();
			appendToHdfs();
//			readFromHdfs();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			System.out.println("SUCCESS");
		}
	}
	/**�ϴ��ļ���HDFS��ȥ*/
	private static void uploadToHdfs() throws FileNotFoundException,IOException {
		String localSrc = "d://qq.txt";
		String dst = "hdfs://192.168.0.113:9000/user/zhangzk/qq.txt";
		InputStream in = new BufferedInputStream(new FileInputStream(localSrc));
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(dst), conf);
		OutputStream out = fs.create(new Path(dst), new Progressable() {
			public void progress() {
				System.out.print(".");
			}
		});
		IOUtils.copyBytes(in, out, 4096, true);
	}
	/**��HDFS�϶�ȡ�ļ�*/
	private static void readFromHdfs() throws FileNotFoundException,IOException {
		String dst = "hdfs://192.168.0.113:9000/user/zhangzk/qq.txt"; 
		Configuration conf = new Configuration(); 
		FileSystem fs = FileSystem.get(URI.create(dst), conf);
		FSDataInputStream hdfsInStream = fs.open(new Path(dst));
		OutputStream out = new FileOutputStream("d:/qq-hdfs.txt");
		byte[] ioBuffer = new byte[1024];
		int readLen = hdfsInStream.read(ioBuffer);
		while(-1 != readLen){
			out.write(ioBuffer, 0, readLen); 
			readLen = hdfsInStream.read(ioBuffer);
		}
		out.close();
		hdfsInStream.close();
		fs.close();
	}
	/**��append��ʽ��������ӵ�HDFS���ļ���ĩβ;ע�⣺�ļ����£���Ҫ��hdfs-site.xml����<property><name>dfs.append.support</name><value>true</value></property>*/
	private static void appendToHdfs() throws FileNotFoundException,IOException {
		String dst = "hdfs://redis1.hhdata.com:8020/user/lzl/test.txt"; 
		Configuration conf = new Configuration(); 
		FileSystem fs = FileSystem.get(URI.create("hdfs://redis1.hhdata.com:8020/user/lzl/"), conf); 
		FSDataOutputStream out = fs.append(new Path(dst));
		int readLen = "zhangzk add by hdfs java api".getBytes().length;
		while(-1 != readLen){
			out.write("zhangzk add by hdfs java api".getBytes(), 0, readLen);
		}
		out.close();
		fs.close();
	}
	/**��HDFS��ɾ���ļ�*/
	private static void deleteFromHdfs() throws FileNotFoundException,IOException {
		String dst = "hdfs://192.168.0.113:9000/user/zhangzk/qq-bak.txt"; 
		Configuration conf = new Configuration(); 
		FileSystem fs = FileSystem.get(URI.create(dst), conf);
		fs.deleteOnExit(new Path(dst));
		fs.close();
	}
	/**����HDFS�ϵ��ļ���Ŀ¼*/
	private static void getDirectoryFromHdfs() throws FileNotFoundException,IOException {
		String dst = "hdfs://192.168.0.113:9000/user/zhangzk"; 
		Configuration conf = new Configuration(); 
		FileSystem fs = FileSystem.get(URI.create(dst), conf);
		FileStatus fileList[] = fs.listStatus(new Path(dst));
		int size = fileList.length;
		for(int i = 0; i < size; i++){
			System.out.println("name:" + fileList[i].getPath().getName() + "\t\tsize:" + fileList[i].getLen());
		}
		fs.close();
	}
}