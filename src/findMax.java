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
		//mapreduce���ÿ��һ�����ݾ͵���һ�θ÷���
		@Override
		protected void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {
			//����һ�е�����ת����string����
			String line = value.toString();
			//����һ�е��ı����ض��ָ����з�
			String year = line.substring(0,4);
			int temperature = Integer.parseInt(line.substring(8));
			context.write(new Text(year), new IntWritable(temperature));
		}
	}
	public static class WCReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		//�����map�������֮�󣬽�����kv�Ի������������з��飬Ȼ�󴫵�һ����<key,valus{}>������һ��reduce����
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
		//��������job���õ���Щ�����ĸ�jar��
		wcjob.setJarByClass(findMax.class);
		//��jobʹ�õ�mapper��reducer����
		wcjob.setMapperClass(WCMapper.class);
		wcjob.setReducerClass(WCReducer.class);
		//ָ��reduce���������kv����
		wcjob.setOutputKeyClass(Text.class);
		wcjob.setOutputValueClass(IntWritable.class);
		//ָ��mapper���������kv����
		wcjob.setMapOutputKeyClass(Text.class);
		wcjob.setMapOutputValueClass(IntWritable.class);
		//ָ��Ҫ������������ݴ��·��
		FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
		//ָ����������������ݴ��·��
		FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
		//��job�ύ����Ⱥ���� 
		wcjob.waitForCompletion(true);
	}
}