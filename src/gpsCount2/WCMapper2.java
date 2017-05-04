package gpsCount2;
import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

//4个泛型中，前两个是指定mapper输入数据的类型，KEYIN是输入的key的类型，VALUEIN是输入的value的类型
//map 和 reduce 的数据输入输出都是以 key-value对的形式封装的
//默认情况下，框架传递给我们的mapper的输入数据中，key是要处理的文本中一行的起始偏移量，这一行的内容作为value
public class WCMapper2 extends Mapper<LongWritable, Text, Text, LongWritable>{
	//mapreduce框架每读一行数据就调用一次该方法
	//根据时间加车牌
	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		String line = value.toString();			
		String[] fields = StringUtils.split(line, "\t");
		long count = Long.parseLong(fields[1]);	
		String keyData;
		String[] wordAndfileName = StringUtils.split(fields[0], "#");
		if(count>50)//此处设置样本点数量超过50，认为车辆出车
		{
			keyData = wordAndfileName[0]+"#"+wordAndfileName[1];
			context.write(new Text(keyData), new LongWritable(1));
		}
		
	}
}