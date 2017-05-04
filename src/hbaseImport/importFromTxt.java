package hbaseImport;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.json.JSONObject;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
public class importFromTxt {
	static Configuration conf_hbase = HBaseConfiguration.create();
	static Connection conn = null;
	static{
		conf_hbase.set("hbase.rootdir", "hdfs://redis1.hhdata.com:8020/apps/hbase/data");//ʹ��eclipseʱ�����������������޷���λ
		conf_hbase.set("hbase.zookeeper.quorum", "redis1.hhdata.com,redis2.hhdata.com,sql1.hhdata.com");
		conf_hbase.set("hbase.zookeeper.property.clientPort", "2181");
		conf_hbase.set("zookeeper.znode.parent","/hbase-unsecure");
	}
	public static class WordCountHbaseMapper extends
	Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			conn = ConnectionFactory.createConnection(conf_hbase);
			Table table_new = conn.getTable(TableName.valueOf("test0908"));
			String line = value.toString();
			if(line.contains("{")&&line.contains("}")){//���ǵ�json�ļ��Ĳ�������
				JSONObject js = JSONObject.fromObject(line);
				String source = js.getString("source");
				String driverName = js.getString("dirverName");
				String driverIdCode = js.getString("dirverIdCode");
				String driverTcode = js.getString("driverTcode");
				String vehType = js.getString("vehType");
				String vehNo = js.getString("vehNo");
				String gpsTime = js.getString("gpsTime");
				String lon = js.getString("lon");
				String lat = js.getString("lat");
				String velocity = js.getString("velocity");
				String vehStatus = js.getString("vehStatus");
				String grid = js.getString("gridNo");
				StringBuilder sb_key = new StringBuilder(js.getString("gridNo"))
											.append("#")
											.append(js.getString("vehNo"))
											.append("#")
											.append(gpsTime);
				Put put = new Put(sb_key.toString().getBytes());
				put.addColumn("info".getBytes(), "source".getBytes(), source.getBytes());
				put.addColumn("info".getBytes(), "driverName".getBytes(), driverName.getBytes());
				put.addColumn("info".getBytes(), "driverIdCode".getBytes(), driverIdCode.getBytes());
				put.addColumn("info".getBytes(), "driverTcode".getBytes(), driverTcode.getBytes());
				put.addColumn("info".getBytes(), "vehType".getBytes(), vehType.getBytes());
				put.addColumn("info".getBytes(), "lon".getBytes(), lon.getBytes());
				put.addColumn("info".getBytes(), "lat".getBytes(), lat.getBytes());
				put.addColumn("info".getBytes(), "velocity".getBytes(), velocity.getBytes());
				put.addColumn("info".getBytes(), "vehStatus".getBytes(), vehStatus.getBytes());
				table_new.put(put);
				table_new.close();
			}
		}
	}
	public static class WordCountHbaseReducer extends
	TableReducer<Text, IntWritable, ImmutableBytesWritable> {
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
//			int sum = 0;
//			for (IntWritable val : values) {// �������
//				sum += val.get();
//			}
//			Put put = new Put(key.getBytes());//putʵ������ÿһ���ʴ�һ��
//			//����Ϊcontent,�����η�Ϊcount����ֵΪ��Ŀ
//			put.add(Bytes.toBytes("content"), Bytes.toBytes("count"), Bytes.toBytes(String.valueOf(sum)));
//			context.write(new ImmutableBytesWritable(key.getBytes()), put);// �����ͺ��<key,value>
		}
	}
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage: importFromTxt <in>");
			System.exit(2);
		}
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		job.setJarByClass(importFromTxt.class);
		//ʹ��WordCountHbaseMapper�����Map���̣�
		job.setMapperClass(WordCountHbaseMapper.class);
//		TableMapReduceUtil.initTableReducerJob(tablename, WordCountHbaseReducer.class, job);
		//�����������ݵ�����·����
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		//������Map���̺�Reduce���̵�������ͣ���������key���������ΪText��
//		job.setOutputKeyClass(Text.class);
		//������Map���̺�Reduce���̵�������ͣ���������value���������ΪIntWritable��
//		job.setOutputValueClass(IntWritable.class);
		//����job.waitForCompletion(true) ִ������ִ�гɹ����˳���
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
