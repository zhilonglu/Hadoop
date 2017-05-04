package gpsCount;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ReducerFunc extends Reducer<Text, Text, Text, Text>{
	//�����map�������֮�󣬽�����kv�Ի������������з��飬Ȼ�󴫵�һ����<key,valus{}>������һ��reduce����
	//<hello,{1,1,1,1,1,1.....}>
	protected void reduce(Text key, Iterable<Text> values,Context context)
			throws IOException, InterruptedException {
		//		long cont = 0;
		//		for(LongWritable value:values){
		//			cont += value.get() ;
		//		}
		//�Ż���Ĵ���
		String timeLine = "";
		StringBuilder sb_value = new StringBuilder("");
		//��¼ʱ���µ�ֵ
		HashMap<String,Integer> timeValue = new HashMap<String,Integer>();
		for(Text value:values){
			timeLine = value.toString();
			if(timeValue.containsKey(timeLine))
			{
				int temp = timeValue.get(timeLine);
				timeValue.put(timeLine, temp+1);
			}
			else{
				timeValue.put(timeLine,1);
			}
		}
		Iterator ite=timeValue.entrySet().iterator();
		while(ite.hasNext()){
			Entry string=(Entry)ite.next();
			//����50��ʾһ��Сʱ��GPS�켣��ĸ���
			if(Integer.parseInt(string.getValue().toString())>50)
			{
				sb_value.append(string.getKey()).append("#").append(string.getValue()).append(",");
			}
		}
		context.write(key, new Text(sb_value.toString()));
	}
}