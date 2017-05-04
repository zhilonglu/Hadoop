package gpsCount2;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class WCReducer2 extends Reducer<Text, LongWritable, Text, LongWritable>{
	//框架在map处理完成之后，将所有kv对缓存起来，进行分组，然后传递一个组<key,valus{}>，调用一次reduce方法
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