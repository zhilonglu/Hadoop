package gpsCount;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ReducerFunc extends Reducer<Text, Text, Text, Text>{
	//框架在map处理完成之后，将所有kv对缓存起来，进行分组，然后传递一个组<key,valus{}>，调用一次reduce方法
	//<hello,{1,1,1,1,1,1.....}>
	protected void reduce(Text key, Iterable<Text> values,Context context)
			throws IOException, InterruptedException {
		//		long cont = 0;
		//		for(LongWritable value:values){
		//			cont += value.get() ;
		//		}
		//优化后的代码
		String timeLine = "";
		StringBuilder sb_value = new StringBuilder("");
		//记录时间下的值
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
			//其中50表示一个小时内GPS轨迹点的个数
			if(Integer.parseInt(string.getValue().toString())>50)
			{
				sb_value.append(string.getKey()).append("#").append(string.getValue()).append(",");
			}
		}
		context.write(key, new Text(sb_value.toString()));
	}
}