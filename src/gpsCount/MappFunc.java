package gpsCount;
import java.io.IOException;
import java.util.HashMap;

import net.sf.json.JSONObject;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

//4个泛型中，前两个是指定mapper输入数据的类型，KEYIN是输入的key的类型，VALUEIN是输入的value的类型
//map 和 reduce 的数据输入输出都是以 key-value对的形式封装的
//默认情况下，框架传递给我们的mapper的输入数据中，key是要处理的文本中一行的起始偏移量，这一行的内容作为value
public class MappFunc extends Mapper<LongWritable, Text, Text, Text>{
	//mapreduce框架每读一行数据就调用一次该方法
	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		if(line.contains("{")&&line.contains("}")){//考虑到json文件的不完整性
			JSONObject js = JSONObject.fromObject(line);
			StringBuilder sb_key = new StringBuilder(js.getString("gridNo"))
			.append("#")
			.append(js.getString("vehNo"));
			String timeLine = js.getString("gpsTime").substring(0,13).replace(" ", "-");
			context.write(new Text(sb_key.toString()),new Text(timeLine));
			//			StringBuilder sb_key = new StringBuilder(js.getString("gridNo"))
			//										.append("#")
			//										.append(js.getString("gpsTime").substring(0,13).replace(" ", "-"));
			//			context.write(new Text(sb_key.toString()),new Text(js.getString("vehNo")));
		}
		//shuffle修改后，将key改为时间+网格
		//		if(line.contains("{")&&line.contains("}")){//考虑到json文件的不完整性
		//			ObjectMapper mapper = new ObjectMapper();
		//			JsonNode rootNode = mapper.readTree(line);
		//			StringBuilder sb_key = new StringBuilder(rootNode.path("gridNo").asText())
		//										.append("#")
		//										.append(rootNode.path("gpsTime").asText().substring(0,13).replace(" ", "-"));
		//			context.write(new Text(sb_key.toString()),new Text(rootNode.path("vehNo").asText()));
		//		}
	}
}