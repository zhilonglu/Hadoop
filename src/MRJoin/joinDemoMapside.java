package MRJoin;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
//ͨ��dept��������Ա�����ڵĲ��Ű���Ϣ��ͳ�Ƹ������ŵ��ܹ���
public class joinDemoMapside extends Configured implements Tool {
	public static class MapClass extends Mapper<LongWritable, Text, Text, Text> {
		// ���ڻ��� dept�ļ��е�����
		private Map<String, String> deptMap = new HashMap<String, String>();
		private String[] kv;
		// �˷�������Map����ִ��֮ǰִ����ִ��һ��
		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			BufferedReader in = null;
			try {
				// �ӵ�ǰ��ҵ�л�ȡҪ������ļ�
				Path[] paths = DistributedCache.getLocalCacheFiles(context.getConfiguration());
				String deptIdName = null;
				for (Path path : paths) {
					// �Բ����ļ��ֶν��в�ֲ����浽deptMap��
					if (path.toString().contains("dept")) {
						in = new BufferedReader(new FileReader(path.toString()));
						while (null != (deptIdName = in.readLine())) {
							// �Բ����ļ��ֶν��в�ֲ����浽deptMap��
							// ����Map��keyΪ���ű�ţ�valueΪ���ڲ�������
							deptMap.put(deptIdName.split(",")[0], deptIdName.split(",")[1]);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			// ��Ա���ļ��ֶν��в��
			kv = value.toString().split(",");
			// map join: ��map�׶ι��˵�����Ҫ�����ݣ����keyΪ�������ƺ�valueΪԱ������
			if (deptMap.containsKey(kv[7])) {
				if (null != kv[5] && !"".equals(kv[5].toString())) {
					context.write(new Text(deptMap.get(kv[7].trim())), new Text(kv[5].trim()));
				}
			}
		}
	}
	public static class Reduce extends Reducer<Text, Text, Text, LongWritable> {
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			// ��ͬһ���ŵ�Ա�����ʽ������
			long sumSalary = 0;
//			int deptNumber = 0;//ͳ�Ʋ�������
			for (Text val : values) {
				sumSalary += Long.parseLong(val.toString());
//				deptNumber++;
			}
			// ���keyΪ�������ƺ�valueΪ�ò���Ա�������ܺ�
			context.write(key, new LongWritable(sumSalary));
			//���keyΪ�������ƺ�valueΪ�ò���Ա������ƽ��ֵ
//			 context.write(key, new Text("Dept Number:" + deptNumber + ", Ave Salary:" + sumSalary / deptNumber));
		}
	}
	@Override
	public int run(String[] args) throws Exception {
		// ʵ������ҵ����������ҵ���ơ�Mapper��Reduce��
		Job job = new Job(getConf(), "joinDemoMapside");
		job.setJobName("joinDemoMapside");
		job.setJarByClass(joinDemoMapside.class);
		job.setMapperClass(MapClass.class);
		job.setReducerClass(Reduce.class);
		// ���������ʽ��
		job.setInputFormatClass(TextInputFormat.class);
		// ���������ʽ
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		// ��1������Ϊ����Ĳ�������·������2������ΪԱ������·���͵�3������Ϊ���·��
		String[] otherArgs = new GenericOptionsParser(job.getConfiguration(), args).getRemainingArgs();
		DistributedCache.addCacheFile(new Path(otherArgs[0]).toUri(), job.getConfiguration());
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		job.waitForCompletion(true);
		return job.isSuccessful() ? 0 : 1;
	}
	/**
	 * ��������ִ�����
	 * @param args �������
	 */
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new joinDemoMapside(), args);
		System.exit(res);
	}
}
