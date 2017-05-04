package gpsCount2;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class WCReducer2 extends Reducer<Text, LongWritable, Text, LongWritable>{
	//�����map�������֮�󣬽�����kv�Ի������������з��飬Ȼ�󴫵�һ����<key,valus{}>������һ��reduce����
	//<hello,{1,1,1,1,1,1.....}>
	protected void reduce(Text key, Iterable<LongWritable> values,Context context)
			throws IOException, InterruptedException {
		long cont = 0;
		for(LongWritable value:values){
			cont += value.get() ;
		}
		context.write(key, new LongWritable(cont));
	}
}