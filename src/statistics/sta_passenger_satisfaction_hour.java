package statistics;
//乘客满意度小时统计表
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
* 通过mapreduce计算并将计算结果写入将数据写入mysql中
*/
public class sta_passenger_satisfaction_hour {
	public static class TblsWritable implements Writable, DBWritable 
	{  
		int sta_satisfaction_id;//标识  
		String sta_date; //统计运距统计日期
		String date_type;//日期类型
		String sta_hour;//统计粒度半小时
		double passenger_satisfaction;
		int belong_enterprise_id;//所属公司标识
		String belong_enterprise;//所属公司
		String regionalism_code;//行政区代码
		String reionalism_name;//行政区代码名称
		String belong_province;//所属省名称
		String belong_city;//所属市名称
		String belong_country;//所属区县名称
		public TblsWritable() 
		{  
		}  
		public TblsWritable(int sta_satisfaction_id,String sta_date,String date_type,String sta_hour,double passenger_satisfaction,int belong_enterprise_id,String belong_enterprise
				,String regionalism_code,String reionalism_name,String belong_province,String belong_city,String belong_country) 
		{  
			this.sta_satisfaction_id = sta_satisfaction_id;//标识  
			this.sta_date = sta_date; //统计运距统计日期
			this.date_type = date_type;//日期类型
			this.sta_hour = sta_hour;
			this.passenger_satisfaction = passenger_satisfaction;
			this.belong_enterprise_id = belong_enterprise_id;
			this.belong_enterprise = belong_enterprise;
			this.regionalism_code = regionalism_code;
			this.reionalism_name = reionalism_name;
			this.belong_province = belong_province;
			this.belong_city = belong_city;
			this.belong_country = belong_country;
		}  
		@Override  
		public void write(PreparedStatement statement) throws SQLException 
		{
			statement.setInt(1, this.sta_satisfaction_id);
			statement.setString(2, this.sta_date);
			statement.setString(3, this.date_type);
			statement.setString(4,this.sta_hour);
			statement.setDouble(5, this.passenger_satisfaction);
			statement.setInt(6, this.belong_enterprise_id);  
			statement.setString(7, this.belong_enterprise);
			statement.setString(8, this.regionalism_code);
			statement.setString(9, this.reionalism_name);
			statement.setString(10, this.belong_province);
			statement.setString(11, this.belong_city);
			statement.setString(12, this.belong_country);
		}  
		@Override  
		public void readFields(ResultSet resultSet) throws SQLException 
		{  
			this.sta_satisfaction_id = resultSet.getInt(1);//标识  
			this.sta_date = resultSet.getString(2); //统计运距统计日期
			this.date_type = resultSet.getString(3);//日期类型
			this.sta_hour = resultSet.getString(4);
			this.passenger_satisfaction = resultSet.getDouble(5);
			this.belong_enterprise_id = resultSet.getInt(6);
			this.belong_enterprise = resultSet.getString(7);
			this.regionalism_code = resultSet.getString(8);
			this.reionalism_name = resultSet.getString(9);
			this.belong_province = resultSet.getString(10);
			this.belong_city = resultSet.getString(11);
			this.belong_country = resultSet.getString(12);
		}  
		@Override  
		public void write(DataOutput out) throws IOException 
		{  
			out.writeInt(this.sta_satisfaction_id);
			out.writeUTF(this.sta_date);
			out.writeUTF(this.date_type);
			out.writeUTF(this.sta_hour);
			out.writeDouble(this.passenger_satisfaction);
			out.writeInt(this.belong_enterprise_id);
			out.writeUTF(this.belong_enterprise);
			out.writeUTF(this.regionalism_code);
			out.writeUTF(this.reionalism_name);
			out.writeUTF(this.belong_province);
			out.writeUTF(this.belong_city);
			out.writeUTF(this.belong_country);
		}  
		@Override  
		public void readFields(DataInput in) throws IOException 
		{  
			this.sta_satisfaction_id = in.readInt();//标识  
			this.sta_date = in.readUTF(); //统计运距统计日期
			this.date_type =in.readUTF();//日期类型
			this.sta_hour = in.readUTF();
			this.passenger_satisfaction = in.readDouble();
			this.belong_enterprise_id = in.readInt();
			this.belong_enterprise = in.readUTF();
			this.regionalism_code = in.readUTF();
			this.reionalism_name = in.readUTF();
			this.belong_province = in.readUTF();
			this.belong_city = in.readUTF();
			this.belong_country = in.readUTF();
		}  
		public String toString() 
		{  
			return new String(this.sta_satisfaction_id +" "+ this.sta_date +" "+this.date_type+" "+this.sta_hour+" "+this.passenger_satisfaction+" "+
			this.belong_enterprise_id +" "+this.belong_enterprise+" "+this.regionalism_code+" "+
			this.reionalism_name+" "+this.belong_province+" "+this.belong_city+" "+
			this.belong_country);  
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
					//context.write(new TblsWritable(keyData[0],keyData[1],string.getKey().toString(),Integer.valueOf(string.getValue().toString())), null);
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
		Job job = new Job(conf,"sta_passenger_satisfaction_hour");  
		job.setJarByClass(sta_passenger_satisfaction_hour.class);  
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
