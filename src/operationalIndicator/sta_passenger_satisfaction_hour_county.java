package operationalIndicator;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import net.sf.json.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
/**
 * 通过mapreduce将数据写入mysql中
 * @author lzl
 * @version v1
 * 
 */
public class sta_passenger_satisfaction_hour_county {
	public static class TblsWritable implements Writable, DBWritable 
	{  
		String grid;  
		String vehNo; 
		String sTime;
		int carNum;
		public TblsWritable() 
		{  
		}  
		public TblsWritable(String grid,String tab_type,String sTime,int carNum) 
		{  
			this.grid = grid;
			this.vehNo = tab_type;
			this.sTime = sTime;
			this.carNum = carNum;
		}  
		@Override  
		public void write(PreparedStatement statement) throws SQLException 
		{
			statement.setString(1, this.grid);  
			statement.setString(2, this.vehNo);  
			statement.setString(3, this.sTime);  
			statement.setInt(4, this.carNum);  
		}  
		@Override  
		public void readFields(ResultSet resultSet) throws SQLException 
		{  
			this.grid = resultSet.getString(1);  
			this.vehNo = resultSet.getString(2);  
			this.sTime = resultSet.getString(3);  
			this.carNum = resultSet.getInt(4);  
		}  
		@Override  
		public void write(DataOutput out) throws IOException 
		{  
			out.writeUTF(this.grid);
			out.writeUTF(this.vehNo);
			out.writeUTF(this.sTime);
			out.writeInt(this.carNum);
		}  
		@Override  
		public void readFields(DataInput in) throws IOException 
		{  
			this.grid = in.readUTF();  
			this.vehNo = in.readUTF();  
			this.sTime = in.readUTF();  
			this.carNum = in.readInt();  
		}  
		public String toString() 
		{  
			return new String(this.grid + " " + this.vehNo+" "+this.sTime+" "+this.carNum);  
		}  
	} 
	public static class ConnMysqlMapper extends Mapper<LongWritable,Text,Text,Text>
	{  
		public void map(LongWritable key,Text value,Context context)throws IOException,InterruptedException 
		{  
			String line = value.toString();
			if(line.contains("{")&&line.contains("}")){//考虑到json文件的不完整性
				JSONObject js = JSONObject.fromObject(line);
				StringBuilder sb_key = new StringBuilder(js.getString("gridNo"))
				.append("#")
				.append(js.getString("vehNo"));
				String timeLine = js.getString("gpsTime").substring(0,13).replace(" ", "-");
				context.write(new Text(sb_key.toString()),new Text(timeLine));
			}
		}  
	}  
	public static class ConnMysqlReducer extends Reducer<Text,Text,TblsWritable,TblsWritable> 
	{  
		public void reduce(Text key,Iterable<Text> values,Context context)throws IOException,
		InterruptedException 
		{  
			//优化后的代码
			String timeLine = "";
			//记录时间下的值
			HashMap<String,Integer> timeValue = new HashMap<String,Integer>();
			for(Text value:values){
				timeLine = value.toString();
				if(timeValue.containsKey(timeLine))
				{
					int temp = timeValue.get(timeLine);
					timeValue.put(timeLine, temp+1);
				}
				else{
					timeValue.put(timeLine,1);
				}
			}
			Iterator ite=timeValue.entrySet().iterator();
			while(ite.hasNext()){
				Entry string=(Entry)ite.next();
				//其中50表示一个小时内GPS轨迹点的个数，此处的值修改可以生效，过滤一些值写入数据库
				if(Integer.parseInt(string.getValue().toString())>7)
				{
					String[] keyData = key.toString().split("#");
					context.write(new TblsWritable(keyData[0],keyData[1],string.getKey().toString(),Integer.valueOf(string.getValue().toString())), null);
				}
			}
		}  
	}  
	public static void main(String args[]) throws IOException, InterruptedException, ClassNotFoundException
	{
		if(args.length < 1){
			try {
				throw new Exception("usg: <inputPath>");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Configuration conf = new Configuration(); 
		DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver","jdbc:mysql://192.168.1.35:3306/gpsCount","hhdata", "123456");    
		Job job = new Job(conf,"sta_passenger_satisfaction_hour_county");  
		job.setJarByClass(sta_passenger_satisfaction_hour_county.class);  
		job.setMapperClass(ConnMysqlMapper.class);  
		job.setReducerClass(ConnMysqlReducer.class);  
		job.setOutputKeyClass(Text.class);  
		job.setOutputValueClass(Text.class); 
		job.setInputFormatClass(TextInputFormat.class);  
		job.setOutputFormatClass(DBOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		DBOutputFormat.setOutput(job, "gpsCount_SZ_0914", "grid","vehNo","sTime","carNum");
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
