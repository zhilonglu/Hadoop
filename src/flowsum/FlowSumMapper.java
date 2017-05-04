package flowsum;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


/**
 * FlowBean �������Զ����һ���������ͣ�Ҫ��hadoop�ĸ����ڵ�֮�䴫�䣬Ӧ����ѭhadoop�����л�����
 * �ͱ���ʵ��hadoop��Ӧ�����л��ӿ�
 *
 */
public class FlowSumMapper extends Mapper<LongWritable, Text, Text, FlowBean>{
	//�õ���־�е�һ�����ݣ��зָ����ֶΣ���ȡ��������Ҫ���ֶΣ��ֻ��ţ���������������������Ȼ���װ��kv���ͳ�ȥ
	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		//��һ������
		String line = value.toString();
		//�зֳɸ����ֶ�
		String[] fields = StringUtils.split(line, "\t");
		//�õ�������Ҫ���ֶ�
		String phoneNB = fields[1];
		long u_flow = Long.parseLong(fields[7]);
		long d_flow = Long.parseLong(fields[8]);
		//��װ����Ϊkv�����
		context.write(new Text(phoneNB), new FlowBean(phoneNB,u_flow,d_flow));
	}
}
