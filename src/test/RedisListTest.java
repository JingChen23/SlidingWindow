package test;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import util.JedisConfiguration;
import util.FeatureComputeHelper;

public class RedisListTest {
	public static void main(String[] args) {
		JedisConfiguration jConfig = new JedisConfiguration();
		JedisPool jedisPool = jConfig.getJedisPool();
		Jedis jedis = jedisPool.getResource();
		jedis.flushDB();
		jedis.lpush("123", "20170101000000,15");
		jedis.lpush("123", "20170101000001,12");
		jedis.lpush("123", "20170101000002,14");
		jedis.lpush("123", "20170101000005,18");	
//		jedis.zadd("1234", 1, "time");
		List<String> list = jedis.lrange("123", 0, 10);
		FeatureComputeHelper helper = new FeatureComputeHelper();
		helper.compute(list, 3600);
		System.out.println(helper.getTimeAvg());
		System.out.println(helper.getTimeVar());
		System.out.println(helper.getAmtAvg());
		System.out.println(helper.getAmtVar());
//		jedis.ltrim("123", 0, 1);
//		System.out.println(list.get(0));
//		String str = jedis.lpop("123");
//		System.out.println(str);
	}
}
