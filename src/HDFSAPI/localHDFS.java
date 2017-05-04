package HDFSAPI;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
public class localHDFS {
    //�������ļ�
    public static void createFile(String dst , byte[] contents) throws IOException{
        Configuration conf = new Configuration();
        conf.addResource("/etc/hadoop/2.4.2.0-258/0/core-site.xml");
        conf.addResource("/etc/hadoop/2.4.2.0-258/0/hdfs-site.xml");
        FileSystem fs = FileSystem.get(conf);
        Path dstPath = new Path(dst); //Ŀ��·��
        //��һ�������
        FSDataOutputStream outputStream = fs.create(dstPath);
        outputStream.write(contents);
        outputStream.close();
        fs.close();
        System.out.println("�ļ������ɹ���");
    }
    //�ϴ������ļ�
    public static void uploadFile(String src,String dst) throws IOException{
        Configuration conf = new Configuration();
        conf.addResource("/etc/hadoop/2.4.2.0-258/0/core-site.xml");
        conf.addResource("/etc/hadoop/2.4.2.0-258/0/hdfs-site.xml");
        Path srcPath = new Path(src); //ԭ·��
        Path dstPath = new Path(dst); //Ŀ��·��
        FileSystem fs = FileSystem.get(URI.create(dst),conf);
        //�����ļ�ϵͳ���ļ����ƺ���,ǰ�������ָ�Ƿ�ɾ��ԭ�ļ���trueΪɾ����Ĭ��Ϊfalse
        fs.copyFromLocalFile(false,srcPath, dstPath);
        //��ӡ�ļ�·��
        System.out.println("Upload to "+conf.get("fs.default.name"));
        System.out.println("------------list files------------"+"\n");
        FileStatus [] fileStatus = fs.listStatus(dstPath);
        for (FileStatus file : fileStatus) 
        {
            System.out.println(file.getPath());
        }
        fs.close();
    }
    //�ļ�������
    public static void rename(String oldName,String newName) throws IOException{
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path oldPath = new Path(oldName);
        Path newPath = new Path(newName);
        boolean isok = fs.rename(oldPath, newPath);
        if(isok){
            System.out.println("rename ok!");
        }else{
            System.out.println("rename failure");
        }
        fs.close();
    }
    //ɾ���ļ�
    public static void delete(String filePath) throws IOException{
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path(filePath);
        boolean isok = fs.deleteOnExit(path);
        if(isok){
            System.out.println("delete ok!");
        }else{
            System.out.println("delete failure");
        }
        fs.close();
    }
    //����Ŀ¼
    public static void mkdir(String path) throws IOException{
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path srcPath = new Path(path);
        boolean isok = fs.mkdirs(srcPath);
        if(isok){
            System.out.println("create dir ok!");
        }else{
            System.out.println("create dir failure");
        }
        fs.close();
    }
    //��ȡ�ļ�������
    public static void readFile(String filePath) throws IOException{
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path srcPath = new Path(filePath);
        InputStream in = null;
        try {
            in = fs.open(srcPath);
            IOUtils.copyBytes(in, System.out, 4096, false); //���Ƶ���׼�����
        } finally {
            IOUtils.closeStream(in);
        }
    }
    public static void main(String[] args) throws IOException {
        //�����ϴ��ļ�
//        uploadFile("C:\\Users\\NLSDE\\Desktop\\emp.txt","hdfs://redis1.hhdata.com:8020/user/lzl/");
        //���Դ����ļ�
       byte[] contents =  "hello world �������\n".getBytes();
        createFile("hdfs://redis1.hhdata.com:8020/user/lzl/data.txt",contents);
        //����������
        //rename("/user/hadoop/test/d.txt", "/user/hadoop/test/dd.txt");
        //����ɾ���ļ�
        //delete("test/dd.txt"); //ʹ�����·��
        //delete("test1");    //ɾ��Ŀ¼
        //�����½�Ŀ¼
        //mkdir("test1");
        //���Զ�ȡ�ļ�
    	//hdfs���ļ�·��
//    	"hdfs://wyc-c3.test.com:8020/storm/"
//        readFile("test1/d.txt");
    }

}