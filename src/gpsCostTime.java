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
		//mapreduce框架每读一行数据就调用一次该方法
		@Override
		protected void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {
			//将这一行的内容转换成string类型
			String line = value.toString();
			if(line.contains("{")&&line.contains("}")){//对于异常字符串的丢弃处理
				JSONObject js = JSONObject.fromObject(line);
				String company = js.getString("source");
				String gpsTime = js.getString("gpsTime");
				String timeLine = gpsTime.replace("-", "").replace(" ", "").replace(":", "");
				String gridNo = js.getString("gridNo");
				String lon = js.getString("lon");
				String lat = js.getString("lat");
				String carPlate = js.getString("vehNo");
				//key的值：车牌#公司#网格#小时
				String keyValue = carPlate+"#"+company+"#"+gridNo+"#"+timeLine.substring(0,10);
				String mapvalue = gpsTime;//时间
				context.write(new Text(keyValue), new Text(mapvalue));
			}
		}
	}
	public static class WCReducer extends Reducer<Text, Text, Text, LongWritable>{
		//框架在map处理完成之后，将所有kv对缓存起来，进行分组，然后传递一个组<key,valus{}>，调用一次reduce方法
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
			//gps的花费时间
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
		//设置整个job所用的那些类在哪个jar包
		wcjob.setJarByClass(gpsCostTime.class);
		//本job使用的mapper和reducer的类
		wcjob.setMapperClass(WCMapper.class);
		wcjob.setReducerClass(WCReducer.class);
		//指定mapper的输出数据kv类型
		wcjob.setMapOutputKeyClass(Text.class);
		wcjob.setMapOutputValueClass(Text.class);
		//指定reduce的输出数据kv类型
		wcjob.setOutputKeyClass(Text.class);
		wcjob.setOutputValueClass(LongWritable.class);
		//指定要处理的输入数据存放路径
		FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
		//指定处理结果的输出数据存放路径
		FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
		//将job提交给集群运行 
		wcjob.waitForCompletion(true);
	}
}