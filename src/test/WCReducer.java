package test;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class WCReducer extends Reducer<Text, Text, Text, Text>{
	
	
	
	//�����map�������֮�󣬽�����kv�Ի������������з��飬Ȼ�󴫵�һ����<key,valus{}>������һ��reduce����
	//<hello,{1,1,1,1,1,1.....}>
	protected void reduce(Text key, Iterable<Text> values,Context context)
			throws IOException, InterruptedException {

		String output = "";
		//����value��list�������ۼ����
			for(Text value:values){
				output += value + "##";
			}
		//�����һ�����ʵ�ͳ�ƽ��
		context.write(key, new Text(output));
		
	}
	
	

}