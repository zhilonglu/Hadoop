package statistics;
//出车率小时统计表
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
 * @lzl 
 * 2016-09-19 23:57 
 * version 1
 */
public class sta_vehicle_hour {
	public static class TblsWritable implements Writable, DBWritable 
	{  
		int id;//标识  
		String sta_date; //统计运距统计日期
		String date_type;//日期类型
		String sta_hour;//统计粒度半小时
		int belong_enterprise_id;//所属公司标识
		String belong_enterprise;//所属公司
		String regionalism_code;//行政区代码
		String reionalism_name;//行政区代码名称
		String belong_province;//所属省名称
		String belong_city;//所属市名称
		String belong_country;//所属区县名称
		double dispatch_vehicle_number;
		double register_vehicle_number;
		double bus_operating_rate;
		public TblsWritable() 
		{  
		}  
		public TblsWritable(int id,String sta_date,String date_type,String sta_hour
				,int belong_enterprise_id,String belong_enterprise,String regionalism_code,String reionalism_name,String belong_province,String belong_city,String belong_country,
				double dispatch_vehicle_number,double register_vehicle_number,double bus_operating_rate) 
		{  
			this.id = id;//标识  
			this.sta_date = sta_date; //统计运距统计日期
			this.date_type = date_type;//日期类型
			this.sta_hour = sta_hour;
			this.belong_enterprise_id = belong_enterprise_id;
			this.belong_enterprise = belong_enterprise;
			this.regionalism_code = regionalism_code;
			this.reionalism_name = reionalism_name;
			this.belong_province = belong_province;
			this.belong_city = belong_city;
			this.belong_country = belong_country;
			this.dispatch_vehicle_number = dispatch_vehicle_number;
			this.register_vehicle_number = register_vehicle_number;
			this.bus_operating_rate = bus_operating_rate;
		}  
		@Override  
		public void write(PreparedStatement statement) throws SQLException 
		{
			statement.setInt(1, this.id);
			statement.setString(2, this.sta_date);
			statement.setString(3, this.date_type);
			statement.setString(4,this.sta_hour);
			statement.setInt(5, this.belong_enterprise_id);  
			statement.setString(6, this.belong_enterprise);
			statement.setString(7, this.regionalism_code);
			statement.setString(8, this.reionalism_name);
			statement.setString(9, this.belong_province);
			statement.setString(10, this.belong_city);
			statement.setString(11, this.belong_country);
			statement.setDouble(12, this.dispatch_vehicle_number);
			statement.setDouble(13, this.register_vehicle_number);
			statement.setDouble(14, this.bus_operating_rate);
		}  
		@Override  
		public void readFields(ResultSet resultSet) throws SQLException 
		{  
			this.id = resultSet.getInt(1);//标识  
			this.sta_date = resultSet.getString(2); //统计运距统计日期
			this.date_type = resultSet.getString(3);//日期类型
			this.sta_hour = resultSet.getString(4);
			this.belong_enterprise_id = resultSet.getInt(5);
			this.belong_enterprise = resultSet.getString(6);
			this.regionalism_code = resultSet.getString(7);
			this.reionalism_name = resultSet.getString(8);
			this.belong_province = resultSet.getString(9);
			this.belong_city = resultSet.getString(10);
			this.belong_country = resultSet.getString(11);
			this.dispatch_vehicle_number = resultSet.getDouble(12);
			this.register_vehicle_number = resultSet.getDouble(13);
			this.bus_operating_rate = resultSet.getDouble(14);
		}  
		@Override  
		public void write(DataOutput out) throws IOException 
		{  
			out.writeInt(this.id);
			out.writeUTF(this.sta_date);
			out.writeUTF(this.date_type);
			out.writeUTF(this.sta_hour);
			out.writeInt(this.belong_enterprise_id);
			out.writeUTF(this.belong_enterprise);
			out.writeUTF(this.regionalism_code);
			out.writeUTF(this.reionalism_name);
			out.writeUTF(this.belong_province);
			out.writeUTF(this.belong_city);
			out.writeUTF(this.belong_country);
			out.writeDouble(this.dispatch_vehicle_number);
			out.writeDouble(this.register_vehicle_number);
			out.writeDouble(this.bus_operating_rate);
		}  
		@Override  
		public void readFields(DataInput in) throws IOException 
		{  
			this.id = in.readInt();//标识  
			this.sta_date = in.readUTF(); //统计运距统计日期
			this.date_type =in.readUTF();//日期类型
			this.sta_hour = in.readUTF();
			this.belong_enterprise_id = in.readInt();
			this.belong_enterprise = in.readUTF();
			this.regionalism_code = in.readUTF();
			this.reionalism_name = in.readUTF();
			this.belong_province = in.readUTF();
			this.belong_city = in.readUTF();
			this.belong_country = in.readUTF();
			this.dispatch_vehicle_number = in.readDouble();
			this.register_vehicle_number = in.readDouble();
			this.bus_operating_rate = in.readDouble();
		}  
		public String toString() 
		{  
			return new String(this.id +" "+ this.sta_date +" "+this.date_type+" "+this.sta_hour+" "+
					this.belong_enterprise_id +" "+this.belong_enterprise+" "+this.regionalism_code+" "+
					this.reionalism_name+" "+this.belong_province+" "+this.belong_city+" "+
					this.belong_country+" "+this.dispatch_vehicle_number+" "+
					this.register_vehicle_number +" "+this.bus_operating_rate);  
		}  
	} 
	public static class ConnMysqlMapper extends Mapper<LongWritable,Text,Text,Text>
	{  
		public void map(LongWritable key,Text value,Context context)throws IOException,InterruptedException 
		{  
			String line = value.toString();
			JSONObject js = JSONObject.fromObject(line);
			StringBuilder sb_key = new StringBuilder(js.getString("manageNo"))
			.append("#")
			.append(js.getString("companyID"))
			.append("#")
			.append(js.getString("gpsTime").substring(0,10));
			StringBuilder sb_value = new StringBuilder(js.getString("vehNo"));
			context.write(new Text(sb_key.toString()),new Text(sb_value.toString()));
		}  
	} 
	//通过行政区代码，获取行政区代码所对应名称以及对应的省市县名称
	public static String connMysql_region(int region_id){
		String output = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String databaseName = "nacp_baseinfo";// 已经在MySQL数据库中创建好的数据库。
			String userName = "root";// MySQL默认的root账户名
			String password = "111111";// 默认的root账户密码为空
			Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.1.217:3306/" + databaseName, userName, password);
			Statement stmt = conn.createStatement();
			String sql = "";
			sql = "SELECT * FROM regionalism_code_detail where regionalism_code="+region_id;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				output = rs.getString(5)+"#"+rs.getString(7)+"#"+rs.getString(9)+"#"+rs.getString(11);
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	//返回的值为查找到的注册车辆数以及对应的企业名称
	public static String connMysql_sum(int enterprise,int region_code){
		int sumNum = 0;
		String entername="";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String databaseName = "nacp_baseinfo";// 已经在MySQL数据库中创建好的数据库。
			String userName = "root";// MySQL默认的root账户名
			String password = "111111";// 默认的root账户密码为空
			Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.1.217:3306/" + databaseName, userName, password);
			Statement stmt = conn.createStatement();
			String sql = "";
			sql = "SELECT count(vehicle_id) FROM base_info_vehicle where enterprise_id="
					+enterprise+"and regionalism_code="+region_code;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				sumNum += Integer.valueOf(rs.getString(1));
				//System.out.println(rs.getString(1));
			}
			sql = "SELECT enterprise_name FROM base_info_vehicle where enterprise_id="+enterprise;
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				entername = rs.getString(1);
				break;//只要找到一个值就行
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sumNum+"#"+entername;
	}
	//判断某一天是周末还是工作日
	public static String dayForWeek(String pTime){  
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");  
		Calendar c = Calendar.getInstance();  
		try {
			c.setTime(format.parse(pTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		int dayForWeek = 0;  
		if(c.get(Calendar.DAY_OF_WEEK) == 1){  
			dayForWeek = 7;  
		}else{  
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;  
		}  
		if(dayForWeek==6 || dayForWeek==7)
			return "周末";
		else{
			return "工作日";
		}
	}
	static int id = 0;//全局变量用作车辆统计标识
	public static class ConnMysqlReducer extends Reducer<Text,Text,TblsWritable,TblsWritable> 
	{  
		//其中的key值为行政区编码#公司编码#时间(2016091923)
		public void reduce(Text key,Iterable<Text> values,Context context)throws IOException,
		InterruptedException 
		{  
			int sum = 0;
			HashMap<String,Integer> map= new HashMap<String,Integer>();
			for(Text val : values){
				if(!map.containsKey(val.toString())){
					map.put(val.toString(), 1);
				}
				else{
					map.put(val.toString(), map.get(val.toString())+1);
				}
			}
			for(String k : map.keySet()){
				if(map.get(k)>=10)
					sum++;
			}
			id++;
			String sta_date=key.toString().split("#")[2].substring(0,8);
			String date_type=dayForWeek(sta_date);//日期类型
			String sta_hour=key.toString().split("#")[2];//统计粒度半小时
			int belong_enterprise_id=Integer.valueOf(key.toString().split("#")[1]);//所属公司标识
			String regionalism_code = key.toString().split("#")[0];//行政区代码
			String returnValue1 = connMysql_sum(belong_enterprise_id,Integer.valueOf(regionalism_code));
			String returnValue2 = connMysql_region(Integer.valueOf(regionalism_code));
			String belong_enterprise=returnValue1.split("#")[1];
			String reionalism_name=returnValue2.split("#")[0];//行政区代码名称
			String belong_province=returnValue2.split("#")[1];//所属省名称
			String belong_city=returnValue2.split("#")[2];//所属市名称
			String belong_country=returnValue2.split("#")[3];//所属区县名称
			double dispatch_vehicle_number = sum;
			double register_vehicle_number = Double.valueOf(returnValue2.split("#")[0]);
			double bus_operating_rate = dispatch_vehicle_number / register_vehicle_number;
			context.write(new TblsWritable(id,sta_date,date_type,sta_hour,belong_enterprise_id,belong_enterprise,
					regionalism_code,reionalism_name,belong_province,belong_city,belong_country,dispatch_vehicle_number
					,register_vehicle_number,bus_operating_rate),null);
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
		DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver","jdbc:mysql://192.168.1.217:3306/nacp_statistics_publish","root", "111111");    
		Job job = new Job(conf,"sta_vehicle_hour");  
		job.setJarByClass(sta_vehicle_hour.class);  
		job.setMapperClass(ConnMysqlMapper.class);  
		job.setReducerClass(ConnMysqlReducer.class);  
		job.setOutputKeyClass(Text.class);  
		job.setOutputValueClass(Text.class); 
		job.setInputFormatClass(TextInputFormat.class);  
		job.setOutputFormatClass(DBOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		DBOutputFormat.setOutput(job, "sta_vehicle_hour","id","sta_date","date_type","sta_hour","belong_enterprise_id","belong_enterprise",
				"regionalism_code","reionalism_name","belong_province","belong_city","belong_country","dispatch_vehicle_number","register_vehicle_number","bus_operating_rate");
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
