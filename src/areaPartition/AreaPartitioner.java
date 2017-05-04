package areaPartition;
import java.util.HashMap;
import org.apache.hadoop.mapreduce.Partitioner;
public class AreaPartitioner<KEY, VALUE> extends Partitioner<KEY, VALUE>{
	private static HashMap<String,Integer> areaMap = new HashMap<>();
	static{
		areaMap.put("135", 0);
		areaMap.put("136", 1);
		areaMap.put("137", 2);
		areaMap.put("138", 3);
		areaMap.put("139", 4);
	}
	@Override
	public int getPartition(KEY key, VALUE value, int numPartitions) {
		//��key���õ��ֻ��ţ���ѯ�ֻ��������ֵ䣬��ͬ��ʡ�ݷ��ز�ͬ�����
		int areaCoder  = areaMap.get(key.toString().substring(0, 3))==null?5:areaMap.get(key.toString().substring(0, 3));
		return areaCoder;
	}
}