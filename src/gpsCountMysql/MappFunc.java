package gpsCountMysql;
import java.io.IOException;
import java.util.HashMap;

import net.sf.json.JSONObject;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//4�������У�ǰ������ָ��mapper�������ݵ����ͣ�KEYIN�������key�����ͣ�VALUEIN�������value������
//map �� reduce ������������������� key-value�Ե���ʽ��װ��
//Ĭ������£���ܴ��ݸ����ǵ�mapper�����������У�key��Ҫ������ı���һ�е���ʼƫ��������һ�е�������Ϊvalue
public class MappFunc extends Mapper<LongWritable, Text, Text, Text>{
	//mapreduce���ÿ��һ�����ݾ͵���һ�θ÷���
	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		if(line.contains("{")&&line.contains("}")){//���ǵ�json�ļ��Ĳ�������
			JSONObject js = JSONObject.fromObject(line);
			StringBuilder sb_key = new StringBuilder(js.getString("gridNo"))
			.append("#")
			.append(js.getString("vehNo"));
			String timeLine = js.getString("gpsTime").substring(0,13).replace(" ", "-");
			context.write(new Text(sb_key.toString()),new Text(timeLine));
		}
	}
}