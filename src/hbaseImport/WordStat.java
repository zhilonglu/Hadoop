package hbaseImport;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
public class WordStat {

	/**
	 * TableMapper<Text,IntWritable>  Text:�����key���ͣ�IntWritable�������value����
	 */
	public static class MyMapper extends TableMapper<Text,IntWritable>{
		private static IntWritable one = new IntWritable(1);
		private static Text word = new Text();
		@Override
		protected void map(ImmutableBytesWritable key, Result value,
				Context context)
						throws IOException, InterruptedException {
			//������ֻ��һ�����壬�����Ҿ�ֱ�ӻ�ȡÿһ�е�ֵ
			String words = Bytes.toString(value.list().get(0).getValue());
			StringTokenizer st = new StringTokenizer(words); 
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				word.set(s);
				context.write(word, one);
			}
		}
	}

	/**
	 * TableReducer<Text,IntWritable>  Text:�����key���ͣ�IntWritable�������value���ͣ�ImmutableBytesWritable���������
	 */
	public static class MyReducer extends TableReducer<Text,IntWritable,ImmutableBytesWritable>{
		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Context context)
						throws IOException, InterruptedException {
			int sum = 0;
			for(IntWritable val:values) {
				sum+=val.get();
			}
			//���һ�м�¼��ÿһ��������Ϊ�м�
			Put put = new Put(Bytes.toBytes(key.toString()));
			//������result�����һ����ʶ��num,��ֵΪÿ�����ʳ��ֵĴ���
			//String.valueOf(sum)�Ƚ�����ת��Ϊ�ַ���������浽���ݿ�����\x00\x00\x00\x������ʽ
			//Ȼ����ת�����ƴ浽hbase��
			put.add(Bytes.toBytes("result"), Bytes.toBytes("num"), Bytes.toBytes(String.valueOf(sum)));
			context.write(new ImmutableBytesWritable(Bytes.toBytes(key.toString())),put);
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.rootdir", "hdfs://wyc-c3.test.com:8020/apps/hbase/data");//ʹ��eclipseʱ�����������������޷���λ
		conf.set("hbase.zookeeper.quorum", "wyc-a1.test.com,wyc-a2.test.com,wyc-b1.test.com");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("zookeeper.znode.parent","/hbase-unsecure");
		Job job = new Job(conf,"wordstat");
		job.setJarByClass(WordStat.class);
		Scan scan = new Scan();
		//ָ��Ҫ��ѯ������
		scan.addColumn(Bytes.toBytes("content"),null);
		//ָ��Mapper��ȡ�ı�Ϊword
		TableMapReduceUtil.initTableMapperJob("word", scan, MyMapper.class, Text.class, IntWritable.class, job);
		//ָ��Reducerд��ı�Ϊstat
		TableMapReduceUtil.initTableReducerJob("stat", MyReducer.class, job);
		System.exit(job.waitForCompletion(true)?0:1);
	}
}
