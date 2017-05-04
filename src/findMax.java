import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
public class findMax{
	public static class WCMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
		//mapreduce框架每读一行数据就调用一次该方法
		@Override
		protected void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {
			//将这一行的内容转换成string类型
			String line = value.toString();
			//对这一行的文本按特定分隔符切分
			String year = line.substring(0,4);
			int temperature = Integer.parseInt(line.substring(8));
			context.write(new Text(year), new IntWritable(temperature));
		}
	}
	public static class WCReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		//框架在map处理完成之后，将所有kv对缓存起来，进行分组，然后传递一个组<key,valus{}>，调用一次reduce方法
		//<hello,{1,1,1,1,1,1.....}>
		protected void reduce(Text key, Iterable<IntWritable> values,Context context)
				throws IOException, InterruptedException {
			int maxValue = Integer.MIN_VALUE;
			for (IntWritable value : values) {
                maxValue = Math.max(maxValue, value.get());
            }
			context.write(key, new IntWritable(maxValue));
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
		wcjob.setJarByClass(findMax.class);
		//本job使用的mapper和reducer的类
		wcjob.setMapperClass(WCMapper.class);
		wcjob.setReducerClass(WCReducer.class);
		//指定reduce的输出数据kv类型
		wcjob.setOutputKeyClass(Text.class);
		wcjob.setOutputValueClass(IntWritable.class);
		//指定mapper的输出数据kv类型
		wcjob.setMapOutputKeyClass(Text.class);
		wcjob.setMapOutputValueClass(IntWritable.class);
		//指定要处理的输入数据存放路径
		FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
		//指定处理结果的输出数据存放路径
		FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
		//将job提交给集群运行 
		wcjob.waitForCompletion(true);
	}
}