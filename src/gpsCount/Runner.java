package gpsCount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 用来描述一个特定的作业
 * 比如，该作业使用哪个类作为逻辑处理中的map，哪个作为reduce
 * ....
 */
public class Runner {
	public static void main(String[] args) throws Exception {
		if(args.length < 2){
			throw new Exception("usg: <inputPath> <outputPath>");
		}
		Configuration conf = new Configuration();
		Job wcjob = Job.getInstance(conf);
		//设置整个job所用的那些类在哪个jar包
		wcjob.setJarByClass(Runner.class);
		//本job使用的mapper和reducer的类
		wcjob.setMapperClass(MappFunc.class);
		wcjob.setReducerClass(ReducerFunc.class);
		//指定reduce的输出数据kv类型
		wcjob.setOutputKeyClass(Text.class);
		wcjob.setOutputValueClass(Text.class);
		//指定mapper的输出数据kv类型
		wcjob.setMapOutputKeyClass(Text.class);
		wcjob.setMapOutputValueClass(Text.class);
		//设置reduce的个数
		wcjob.setNumReduceTasks(10);  
		//指定要处理的输入数据存放路径
		FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
		//指定处理结果的输出数据存放路径
		FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
		//将job提交给集群运行 
		wcjob.waitForCompletion(true);
	}
}
