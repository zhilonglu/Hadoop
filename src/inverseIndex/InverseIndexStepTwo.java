package inverseIndex;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Reducer;
public class InverseIndexStepTwo {	
public static class StepTwoMapper extends Mapper<LongWritable, Text, Text, Text>{			
	    //k: ����ʼƫ����    v:  {hello-->a.txt   3} 
		@Override
		protected void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {			
			String line = value.toString();			
			String[] fields = StringUtils.split(line, "\t");
			String[] wordAndfileName = StringUtils.split(fields[0], "-->");			
			String word = wordAndfileName[0];
			String fileName = wordAndfileName[1];
			long count = Long.parseLong(fields[1]);						
			context.write(new Text(word), new Text(fileName+"-->"+count));		
			//map����Ľ���������ʽ   �� <hello,a.txt-->3>			
		}
}
	public static class StepTwoReducer extends Reducer<Text, Text,Text, Text>{
		@Override
		protected void reduce(Text key, Iterable<Text> values,Context context)
				throws IOException, InterruptedException {
			//�õ�������  <hello,{a.txt-->3,b.txt-->2,c.txt-->1}>
			String result = "";
			for(Text value:values){
				result += value + " ";
			}
			context.write(key, new Text(result));
			//����Ľ������  k: hello   v: a.txt-->3  b.txt-->2  c.txt-->1  
		}
	}
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();	
		//�ȹ���job_one
//		Job job_one = Job.getInstance(conf);
//		
//		job_one.setJarByClass(InverseIndexStepTwo.class);
//		job_one.setMapperClass(StepOneMapper.class);
//		job_one.setReducerClass(StepOneReducer.class);
		//......
		//����job_two
		Job job_tow = Job.getInstance(conf);
		job_tow.setJarByClass(InverseIndexStepTwo.class);
		job_tow.setMapperClass(StepTwoMapper.class);
		job_tow.setReducerClass(StepTwoReducer.class);
		job_tow.setOutputKeyClass(Text.class);
		job_tow.setOutputValueClass(Text.class);
		FileInputFormat.setInputPaths(job_tow, new Path(args[0]));
		//���һ�²�����ָ�������·���Ƿ���ڣ�����Ѵ��ڣ���ɾ��
		Path output = new Path(args[1]);
		FileSystem fs = FileSystem.get(conf);
		if(fs.exists(output)){
			fs.delete(output, true);
		}
		FileOutputFormat.setOutputPath(job_tow, output);
		//���ύjob_oneִ��
//		boolean one_result = job_one.waitForCompletion(true);
//		if(one_result){
		System.exit(job_tow.waitForCompletion(true)?0:1);
//		}
	}
}