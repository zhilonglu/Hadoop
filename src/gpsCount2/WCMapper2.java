package gpsCount2;
import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

//4�������У�ǰ������ָ��mapper�������ݵ����ͣ�KEYIN�������key�����ͣ�VALUEIN�������value������
//map �� reduce ������������������� key-value�Ե���ʽ��װ��
//Ĭ������£���ܴ��ݸ����ǵ�mapper�����������У�key��Ҫ������ı���һ�е���ʼƫ��������һ�е�������Ϊvalue
public class WCMapper2 extends Mapper<LongWritable, Text, Text, LongWritable>{
	//mapreduce���ÿ��һ�����ݾ͵���һ�θ÷���
	//����ʱ��ӳ���
	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		String line = value.toString();			
		String[] fields = StringUtils.split(line, "\t");
		long count = Long.parseLong(fields[1]);	
		String keyData;
		String[] wordAndfileName = StringUtils.split(fields[0], "#");
		if(count>50)//�˴�������������������50����Ϊ��������
		{
			keyData = wordAndfileName[0]+"#"+wordAndfileName[1];
			context.write(new Text(keyData), new LongWritable(1));
		}
		
	}
}