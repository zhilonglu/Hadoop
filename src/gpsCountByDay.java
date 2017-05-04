import java.io.IOException;
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
public class gpsCountByDay{
	//4�������У�ǰ������ָ��mapper�������ݵ����ͣ�KEYIN�������key�����ͣ�VALUEIN�������value������
	//map �� reduce ������������������� key-value�Ե���ʽ��װ��
	//Ĭ������£���ܴ��ݸ����ǵ�mapper�����������У�key��Ҫ������ı���һ�е���ʼƫ��������һ�е�������Ϊvalue
	public static class WCMapper extends Mapper<LongWritable, Text, Text, LongWritable>{
		//mapreduce���ÿ��һ�����ݾ͵���һ�θ÷���
		@Override
		protected void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			if(line.contains("{")&&line.contains("}")){//�����쳣��json�ļ���������
				JSONObject js = JSONObject.fromObject(line);
				String gpsTime = js.getString("gpsTime");
				String timeLine = gpsTime.substring(0,10).replace("-", "");//�������
				String gridNo = js.getString("gridNo");
				String carPlate = js.getString("vehNo");
				String keyData = timeLine+"#"+gridNo+"#"+carPlate;
				context.write(new Text(keyData), new LongWritable(1));
			}
		}
	}
	public static class WCReducer extends Reducer<Text, LongWritable, Text, LongWritable>{
		//�����map�������֮�󣬽�����kv�Ի������������з��飬Ȼ�󴫵�һ����<key,valus{}>������һ��reduce����
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
		//��������job���õ���Щ�����ĸ�jar��
		wcjob.setJarByClass(gpsCountByDay.class);
		//��jobʹ�õ�mapper��reducer����
		wcjob.setMapperClass(WCMapper.class);
		wcjob.setReducerClass(WCReducer.class);
		//ָ��reduce���������kv����
		wcjob.setOutputKeyClass(Text.class);
		wcjob.setOutputValueClass(LongWritable.class);
		//ָ��mapper���������kv����
		wcjob.setMapOutputKeyClass(Text.class);
		wcjob.setMapOutputValueClass(LongWritable.class);
		//ָ��Ҫ������������ݴ��·��
		FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
		//ָ����������������ݴ��·��
		FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
		//��job�ύ����Ⱥ���� 
		wcjob.waitForCompletion(true);
	}
}