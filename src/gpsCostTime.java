import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
public class gpsCostTime{
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
				String mapvalue = gpsTime;//ʱ��
				context.write(new Text(keyValue), new Text(mapvalue));
			}
		}
	}
	public static class WCReducer extends Reducer<Text, Text, Text, LongWritable>{
		//�����map�������֮�󣬽�����kv�Ի������������з��飬Ȼ�󴫵�һ����<key,valus{}>������һ��reduce����
		protected void reduce(Text key, Iterable<Text> values,Context context)
				throws IOException, InterruptedException {
			ArrayList timeList = new ArrayList();
			for (Text value : values) {
				timeList.add(value.toString());
			}
			Collections.sort(timeList);
			String beginTime = timeList.get(0).toString();
			String endTime = timeList.get(timeList.size()-1).toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt1 = null,dt2 = null;
			try {
				dt1 = sdf.parse(beginTime);
				dt2 = sdf.parse(endTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//gps�Ļ���ʱ��
			long costTime = (dt2.getTime()-dt1.getTime())/1000;
			context.write(key, new LongWritable(costTime));
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
		wcjob.setJarByClass(gpsCostTime.class);
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