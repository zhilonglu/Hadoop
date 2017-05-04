package gpsCount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * ��������һ���ض�����ҵ
 * ���磬����ҵʹ���ĸ�����Ϊ�߼������е�map���ĸ���Ϊreduce
 * ....
 */
public class Runner {
	public static void main(String[] args) throws Exception {
		if(args.length < 2){
			throw new Exception("usg: <inputPath> <outputPath>");
		}
		Configuration conf = new Configuration();
		Job wcjob = Job.getInstance(conf);
		//��������job���õ���Щ�����ĸ�jar��
		wcjob.setJarByClass(Runner.class);
		//��jobʹ�õ�mapper��reducer����
		wcjob.setMapperClass(MappFunc.class);
		wcjob.setReducerClass(ReducerFunc.class);
		//ָ��reduce���������kv����
		wcjob.setOutputKeyClass(Text.class);
		wcjob.setOutputValueClass(Text.class);
		//ָ��mapper���������kv����
		wcjob.setMapOutputKeyClass(Text.class);
		wcjob.setMapOutputValueClass(Text.class);
		//����reduce�ĸ���
		wcjob.setNumReduceTasks(10);  
		//ָ��Ҫ������������ݴ��·��
		FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
		//ָ����������������ݴ��·��
		FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
		//��job�ύ����Ⱥ���� 
		wcjob.waitForCompletion(true);
	}
}
