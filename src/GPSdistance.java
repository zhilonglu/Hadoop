import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
public class GPSdistance{
	public static class WCMapper extends Mapper<LongWritable, Text, Text, Text>{
		//mapreduce���ÿ��һ�����ݾ͵���һ�θ÷���
		@Override
		protected void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {
			//����һ�е�����ת����string����
			String line = value.toString();
			if(line.contains("{")&&line.contains("}")){//�����쳣�ַ����Ķ�������
				JSONObject js = JSONObject.fromObject(line);
				String company = js.getString("source");
				String gpsTime = js.getString("gpsTime");
				String timeLine = gpsTime.replace("-", "").replace(" ", "").replace(":", "");
				String gridNo = js.getString("gridNo");
				String lon = js.getString("lon");
				String lat = js.getString("lat");
				String carPlate = js.getString("vehNo");
				//key��ֵ������#��˾#����#Сʱ
				String keyValue = carPlate+"#"+company+"#"+gridNo+"#"+timeLine.substring(0,10);
				String mapvalue = gpsTime+"#"+lon+"-"+lat;//ʱ��#����+ά��
				context.write(new Text(keyValue), new Text(mapvalue));
			}
		}
	}
	public static double Distance(double long1, double lat1, double long2,double lat2) {  
		double a, b, R;  
		R = 6378137; // ����뾶  
		lat1 = lat1 * Math.PI / 180.0;  
		lat2 = lat2 * Math.PI / 180.0;  
		a = lat1 - lat2;  
		b = (long1 - long2) * Math.PI / 180.0;  
		double d;  
		double sa2, sb2;  
		sa2 = Math.sin(a / 2.0);  
		sb2 = Math.sin(b / 2.0);  
		d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)* Math.cos(lat2) * sb2 * sb2));  
		return d;  
	}
	public static class WCReducer extends Reducer<Text, Text, Text, LongWritable>{
		//�����map�������֮�󣬽�����kv�Ի������������з��飬Ȼ�󴫵�һ����<key,valus{}>������һ��reduce����
		protected void reduce(Text key, Iterable<Text> values,Context context)
				throws IOException, InterruptedException {
			HashMap<String,String> tempdata = new HashMap<String,String>();
			List<Map.Entry<String, String>> mHashMapEntryList;
			for (Text value : values) {
				String[] data = value.toString().split("#");
				tempdata.put(data[0], data[1]);
			}
			mHashMapEntryList=new ArrayList<Map.Entry<String, String>>(tempdata.entrySet());
			Collections.sort(mHashMapEntryList, new Comparator<Map.Entry<String, String>>() {   
				public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
					return (o1.getKey()).compareTo(o2.getKey());
				}
			});
			//��ʱ����õ�map��ֵ��ʽ�ǡ�ʱ��=����+γ�ȡ�
			int listsize = mHashMapEntryList.size();
			long alldistance = 0;
			//����һ���켣�ľ���
			for (int i = 0; i < listsize-1; i++) {
				String lonAndlat1 = mHashMapEntryList.get(i).toString().split("=")[1];
				double lon1 = Double.valueOf(lonAndlat1.split("-")[0]);
				double lat1 = Double.valueOf(lonAndlat1.split("-")[1]);
				String lonAndlat2 = mHashMapEntryList.get(i+1).toString().split("=")[1];
				double lon2 = Double.valueOf(lonAndlat2.split("-")[0]);
				double lat2 = Double.valueOf(lonAndlat2.split("-")[1]);
				alldistance += Distance(lon1,lat1,lon2,lat2);
			}
			context.write(key, new LongWritable(alldistance));
		}
	}
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		if (args.length < 2) {
			System.err.println("Usage:<inputPath> <outputPath>");
			System.exit(2);
		}
		Job wcjob = Job.getInstance(conf);
		//��������job���õ���Щ�����ĸ�jar��
		wcjob.setJarByClass(GPSdistance.class);
		//��jobʹ�õ�mapper��reducer����
		wcjob.setMapperClass(WCMapper.class);
		wcjob.setReducerClass(WCReducer.class);
		//ָ��mapper���������kv����
		wcjob.setMapOutputKeyClass(Text.class);
		wcjob.setMapOutputValueClass(Text.class);
		//ָ��reduce���������kv����
		wcjob.setOutputKeyClass(Text.class);
		wcjob.setOutputValueClass(LongWritable.class);
		//ָ��Ҫ������������ݴ��·��
		FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
		//ָ����������������ݴ��·��
		FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
		//��job�ύ����Ⱥ���� 
		wcjob.waitForCompletion(true);
	}
}