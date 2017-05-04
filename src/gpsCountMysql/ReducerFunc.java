package gpsCountMysql;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ReducerFunc extends Reducer<Text, Text, Text, Text>{
	//�����map�������֮�󣬽�����kv�Ի������������з��飬Ȼ�󴫵�һ����<key,valus{}>������һ��reduce����
	//<hello,{1,1,1,1,1,1.....}>
	private static int rowkey = 0;
	protected void reduce(Text key, Iterable<Text> values,Context context)
			throws IOException, InterruptedException {
		
		HashMap<String,Integer> timeValue = new HashMap<String,Integer>();
		String timeLine = "";
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
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String databaseName = "gpsCount";// �Ѿ���MySQL���ݿ��д����õ����ݿ⡣
			String userName = "hhdata";// MySQLĬ�ϵ�root�˻���
			String password = "123456";// Ĭ�ϵ�root�˻�����Ϊ��
			Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.1.35:3306/" + databaseName, userName, password);
			Statement stmt = conn.createStatement();
//			String sql = "create table gpsCount_SZ(rowkey int(4) primary key,grid varchar(10),vehNo varchar(10),sTime varchar(15),carnum int(4))";
//			// �������ݿ��еı�
//			int result = stmt.executeUpdate(sql);
			String sql = "";
			int result = 0;
			String[] keyData = key.toString().split("#");
			Iterator ite=timeValue.entrySet().iterator();
				while(ite.hasNext()){
					rowkey++;
					Entry string=(Entry)ite.next();
					//����50��ʾһ��Сʱ��GPS�켣��ĸ���
//					if(Integer.parseInt(string.getValue().toString())>50)
//					{
						String mysqlValue = "VALUES("+rowkey+",\'"+keyData[0]+"\','"
								+keyData[1]+"\','"+string.getKey().toString()+"\',"+Integer.valueOf(string.getValue().toString())+")";
						sql = "INSERT INTO gpsCount_SZ(rowkey,grid,vehNo,sTime,carnum) "+mysqlValue;
						result = stmt.executeUpdate(sql);
//					}
				}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		context.write(key, new Text(timeLine+"##"+timeValue.size()));
	}
}