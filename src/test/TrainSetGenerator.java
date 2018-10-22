package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import dao.InfoDao;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import util.FeatureComputeHelper;
import util.JedisConfiguration;
import util.TimeTransHelper;

public class TrainSetGenerator {	
//	原始数据转为训练集
	private int windowSize = 2592000;
	private double suspiciousAmt1 = 1999.19995;
	private double suspiciousAmt2 = 999.59998;
	
	public void trainSetGenerator(BufferedReader br, BufferedWriter bw){
		try {			
			JedisConfiguration jConfig = new JedisConfiguration();
			JedisPool jedisPool = jConfig.getJedisPool();
			Jedis jedis = jedisPool.getResource();
			jedis.flushDB();
	        // read file content from file	       
	        String str = null;
	        InfoDao info = new InfoDao();
	        DecimalFormat df = new DecimalFormat("0.00");
	        while((str = br.readLine()) != null) {
	            String[] items = str.split(",");
//              变量从字符串中剥离            
		        String transTime = items[4].substring(1, items[4].length()-1);
		        String transMoney = items[5].substring(1, items[5].length()-1);
		        if (transMoney == null || transMoney.equals("") || transMoney.isEmpty()) continue;
		        String user = items[3].substring(1, items[3].length()-1);
//		        String seller = items[6].substring(1, items[6].length()-1);
		        System.out.println(transTime);
		        String dayLimit = items[1].substring(1, items[1].length()-1);
		        String ip = items[8].substring(1, items[8].length()-1);
		        String singleLimit = items[2].substring(1, items[2].length()-1);
		        int sign = Integer.parseInt(items[9].substring(1, items[9].length()-1));
//	    		除掉垃圾数据
	            if(sign<7 && sign>1) continue;	            	  
//	                                    变量写入data object(InfoDao)
	            if(sign>1) info.setSign("1");
	            else info.setSign("0");
//	            有序集合存入用户的日限额，当日累计金额，和上一笔的交易时间
//	            如果是该用户的第一条数据的话
	            
	            double transAmt = Double.parseDouble(transMoney);
	            
	            if (jedis.zscore(user+"z", "time") == null){
	            	jedis.zadd(user+"z", Double.parseDouble(transTime) , "time");          //时间更新
		        	jedis.zadd(user+"z", transAmt, "accumulatedAmt");  //因为是第一次，所以累计金额就是这笔金额        	
		        	if(dayLimit.equals("0") || dayLimit.isEmpty() || dayLimit == null || singleLimit.equals("0") || singleLimit == null || singleLimit.isEmpty()) info.setOverPaid("0");
		        	else{
		        		if (Double.parseDouble(singleLimit) < transAmt) info.setOverPaid("1");
		        		else info.setOverPaid("0");
		        	}
		        	jedis.zadd(user+"z", Double.parseDouble(transTime), "time");
//		        	更新该用户的历史交易列表
		        	jedis.lpush(user+"l",transTime+","+transMoney);
//		        	写入数据对象，为特征集的生成做准备
		        	info.setAmt(df.format(transAmt));
		        	if (Math.abs(transAmt - suspiciousAmt1) < 0.1 || Math.abs(transAmt - suspiciousAmt2) < 0.1) 
		        		info.setAmt_suspisious("1");
		        	else info.setAmt_suspisious("0");		        	
		        	info.setIp(ip);
		        	info.setTimes_window("1");
		        	info.setAvg_amt_window("1000");
		        	info.setVar_amt_window("1000");
		        	info.setAvg_time_window(String.valueOf(windowSize));
		        	info.setVar_time_window(String.valueOf(windowSize));
		        	info.setAmt_avg(info.getAmt());
	            }
//	            如果不是第一条的话
	            else{
//	            	起手先算当日累计金额
	            	DecimalFormat df1 = new DecimalFormat("0.0000000000000");	            	
	            	String lastDay = df1.format((jedis.zscore(user+"z", "time"))).replace(".", "").substring(6,8);
	            	String thisDay = transTime.substring(6,8);       	
//	            	同一天的交易累计金额
	            	if (thisDay.equals(lastDay)) { 
	            		double money = jedis.zscore(user+"z","accumulatedAmt");
	            		money += transAmt;
	            		jedis.zadd(user+"z", money, "accumulatedAmt");
	            	}
//	            	不是同一天的将累计金额设成这比金额
	            	else{
	            		jedis.zadd(user+"z", transAmt, "accumulatedAmt");
	            	}
//		        	是否超额字段设定
	            	if(dayLimit.equals("0") || dayLimit.isEmpty() || dayLimit == null || singleLimit.equals("0") || singleLimit == null || singleLimit.isEmpty()) info.setOverPaid("0");
		        	else{
		        		if (Double.parseDouble(singleLimit) < transAmt) info.setOverPaid("1");
		        		else if (Double.parseDouble(dayLimit) < jedis.zscore(user+"z","accumulatedAmt")) info.setOverPaid("1");
		        		else info.setOverPaid("0");
		        	}
	            	jedis.zadd(user+"z", Double.parseDouble(transTime), "time");
//		        	先把能做的做了
	            	info.setAmt(df.format(transAmt));
		        	if (Math.abs(transAmt - suspiciousAmt1) < 0.1 || Math.abs(transAmt - suspiciousAmt2) < 0.1) 
		        		info.setAmt_suspisious("1");
		        	else info.setAmt_suspisious("0");	
		        	info.setIp(ip);
		        	info.setTimes_window("1");
//	            	麻烦的列表更新和统计特征计算环节
//		        	首先更新列表
		        	jedis.lpush(user+"l",transTime+","+transMoney);
//		        	如果列表长度大于一,说明至少有一条历史记录,我们就从列表的屁股开始删东西,因为屁股都是最久的.
		        	TimeTransHelper timeTransHelper = new TimeTransHelper();
		        	while(jedis.llen(user+"l") > 1){
		        		String farestTime = jedis.rpop(user+"l");
		        		long timeGap = Math.abs((timeTransHelper.toTime(transTime) - timeTransHelper.toTime(farestTime))/1000); 
		        		if (timeGap > windowSize){
		        			continue;
		        		}
		        		else{
		        			jedis.rpush(user+"l", farestTime);
		        			break;
		        		}
		        	}
//		        	更新列表之后,计算最新的均值和方差
		        	List<String> list = jedis.lrange(user+"l", 0, jedis.llen(user+"l"));
		        	FeatureComputeHelper fch = new FeatureComputeHelper();
		        	fch.compute(list, windowSize);
		        	info.setTimes_window(String.valueOf(list.size()));
		        	info.setAvg_time_window(df.format(fch.getTimeAvg()));
		        	info.setVar_time_window(df.format(fch.getTimeVar()));
		        	info.setAvg_amt_window(df.format(fch.getAmtAvg()));
		        	info.setVar_amt_window(df.format(fch.getAmtVar()));
		        	info.setAmt_avg(df.format(fch.getAverageAmt()));
	            }	            
	            String str1 = info.getOverPaid() +","+ info.getAmt() +","+ info.getAmt_suspisious() +","+ info.getIp() +","+ info.getTimes_window()
	            +","+info.getAvg_time_window() +","+ info.getVar_time_window() +","+ info.getAvg_amt_window() +","+ info.getVar_amt_window()+","+info.getAmt_avg() +","+ info.getSign();
	            bw.write(str1);
	            bw.newLine();
	        }
	        System.out.println("done!");
	  }
	  catch(FileNotFoundException e) {
	              e.printStackTrace();
	        }
	        catch(IOException e) {
	              e.printStackTrace();
	        }
	  }


	public static void main(String[] args) {
		TrainSetGenerator dp = new TrainSetGenerator();
		try {
			FileReader reader = new FileReader("D://Jworkspace/4-5.csv");
	        BufferedReader br = new BufferedReader(reader);
	        FileWriter writer = new FileWriter("D://Jworkspace/4-5train.csv");
	        BufferedWriter bw = new BufferedWriter(writer);
			dp.trainSetGenerator(br,bw);
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

//    	RandomEngine randomGenerator = RandomEngine.makeDefault();
//    	Gamma gamma = new Gamma (3.0,20.0, randomGenerator);
//		System.out.println(gamma.nextDouble());
	}	
}
