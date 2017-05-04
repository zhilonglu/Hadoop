import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
public class gpsCountByDay2{
	//4个泛型中，前两个是指定mapper输入数据的类型，KEYIN是输入的key的类型，VALUEIN是输入的value的类型
	//map 和 reduce 的数据输入输出都是以 key-value对的形式封装的
	//默认情况下，框架传递给我们的mapper的输入数据中，key是要处理的文本中一行的起始偏移量，这一行的内容作为value
	public static class WCMapper extends Mapper<LongWritable, Text, Text, LongWritable>{
		//mapreduce框架每读一行数据就调用一次该方法
		@Override
		protected void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {
			String line = value.toString();			
			String[] fields = StringUtils.split(line, "\t");
			long count = Long.parseLong(fields[1]);	
			String keyData;
			String[] wordAndfileName = StringUtils.split(fields[0], "#");
			if(count>50)//此处设置样本点数量超过50，认为车辆出车
			{
				keyData = wordAndfileName[0]+"#"+wordAndfileName[1];
				context.write(new Text(keyData), new LongWritable(1));
			}
		}
	}
	public static class WCReducer extends Reducer<Text, LongWritable, Text, LongWritable>{
		//框架在map处理完成之后，将所有kv对缓存起来，进行分组，然后传递一个组<key,valus{}>，调用一次reduce方法
		protected void reduce(Text key, Iterable<LongWritable> values,Context context)
				throws IOException, InterruptedException {
			long cont = 0;
			for(LongWritable value:values){
				cont += value.get() ;
			}
			context.write(key, new LongWritable(cont));
		}
	}
	public static void main(String[] args) throws Exception {
		if(args.length < 2){
			throw new Exception("usg: <inputPath> <outputPath>");
		}
		Configuration conf = new Configuration();
		Job wcjob = Job.getInstance(conf);
		//设置整个job所用的那些类在哪个jar包
		wcjob.setJarByClass(gpsCountByDay2.class);
		//本job使用的mapper和reducer的类
		wcjob.setMapperClass(WCMapper.class);
		wcjob.setReducerClass(WCReducer.class);
		//指定reduce的输出数据kv类型
		wcjob.setOutputKeyClass(Text.class);
		wcjob.setOutputValueClass(LongWritable.class);
		//指定mapper的输出数据kv类型
		wcjob.setMapOutputKeyClass(Text.class);
		wcjob.setMapOutputValueClass(LongWritable.class);
		//指定要处理的输入数据存放路径
		FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
		//指定处理结果的输出数据存放路径
		FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
		//将job提交给集群运行 
		wcjob.waitForCompletion(true);
	}
}