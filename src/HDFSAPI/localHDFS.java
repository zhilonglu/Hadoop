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
    //创建新文件
    public static void createFile(String dst , byte[] contents) throws IOException{
        Configuration conf = new Configuration();
        conf.addResource("/etc/hadoop/2.4.2.0-258/0/core-site.xml");
        conf.addResource("/etc/hadoop/2.4.2.0-258/0/hdfs-site.xml");
        FileSystem fs = FileSystem.get(conf);
        Path dstPath = new Path(dst); //目标路径
        //打开一个输出流
        FSDataOutputStream outputStream = fs.create(dstPath);
        outputStream.write(contents);
        outputStream.close();
        fs.close();
        System.out.println("文件创建成功！");
    }
    //上传本地文件
    public static void uploadFile(String src,String dst) throws IOException{
        Configuration conf = new Configuration();
        conf.addResource("/etc/hadoop/2.4.2.0-258/0/core-site.xml");
        conf.addResource("/etc/hadoop/2.4.2.0-258/0/hdfs-site.xml");
        Path srcPath = new Path(src); //原路径
        Path dstPath = new Path(dst); //目标路径
        FileSystem fs = FileSystem.get(URI.create(dst),conf);
        //调用文件系统的文件复制函数,前面参数是指是否删除原文件，true为删除，默认为false
        fs.copyFromLocalFile(false,srcPath, dstPath);
        //打印文件路径
        System.out.println("Upload to "+conf.get("fs.default.name"));
        System.out.println("------------list files------------"+"\n");
        FileStatus [] fileStatus = fs.listStatus(dstPath);
        for (FileStatus file : fileStatus) 
        {
            System.out.println(file.getPath());
        }
        fs.close();
    }
    //文件重命名
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
    //删除文件
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
    //创建目录
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
    //读取文件的内容
    public static void readFile(String filePath) throws IOException{
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path srcPath = new Path(filePath);
        InputStream in = null;
        try {
            in = fs.open(srcPath);
            IOUtils.copyBytes(in, System.out, 4096, false); //复制到标准输出流
        } finally {
            IOUtils.closeStream(in);
        }
    }
    public static void main(String[] args) throws IOException {
        //测试上传文件
//        uploadFile("C:\\Users\\NLSDE\\Desktop\\emp.txt","hdfs://redis1.hhdata.com:8020/user/lzl/");
        //测试创建文件
       byte[] contents =  "hello world 世界你好\n".getBytes();
        createFile("hdfs://redis1.hhdata.com:8020/user/lzl/data.txt",contents);
        //测试重命名
        //rename("/user/hadoop/test/d.txt", "/user/hadoop/test/dd.txt");
        //测试删除文件
        //delete("test/dd.txt"); //使用相对路径
        //delete("test1");    //删除目录
        //测试新建目录
        //mkdir("test1");
        //测试读取文件
    	//hdfs的文件路径
//    	"hdfs://wyc-c3.test.com:8020/storm/"
//        readFile("test1/d.txt");
    }

}