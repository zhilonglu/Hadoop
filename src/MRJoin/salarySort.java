package MRJoin;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
public class salarySort extends Configured implements Tool {
    public static class MapClass extends Mapper<LongWritable, Text, IntWritable, Text> {
        public void map(LongWritable key, Text value, Context context) 
        		throws IOException,         InterruptedException {
            // ��Ա���ļ��ֶν��в��
            String[] kv = value.toString().split(",");
            // ���keyΪԱ�����й��ʺ�valueΪԱ������
            int empAllSalary = "".equals(kv[6]) ? Integer.parseInt(kv[5]) :                
            	Integer.parseInt(kv[5]) + Integer.parseInt(kv[6]);
            context.write(new IntWritable(empAllSalary), new Text(kv[1]));
        }
    }
    /**
     * �ݼ������㷨
     */
    public static class DecreaseComparator extends IntWritable.Comparator {
        public int compare(WritableComparable a, WritableComparable b) {
            return -super.compare(a, b);
        }
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            return -super.compare(b1, s1, l1, b2, s2, l2);
        }
    }
    @Override
    public int run(String[] args) throws Exception {
        // ʵ������ҵ����������ҵ����
        Job job = new Job(getConf(), "salarySort");
        job.setJobName("salarySort");
        // ����Mapper��Reduce��
        job.setJarByClass(salarySort.class);
        job.setMapperClass(MapClass.class);
        // ���������ʽ��
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setSortComparatorClass(DecreaseComparator.class);
        // ��1������ΪԱ������·���͵�2������Ϊ���·��
        String[] otherArgs = new GenericOptionsParser(job.getConfiguration(),args).getRemainingArgs();
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        job.waitForCompletion(true);
        return job.isSuccessful() ? 0 : 1;
    }

    /**
     * ��������ִ�����
     * @param args �������
     */
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new salarySort(), args);
        System.exit(res);
    }
}
