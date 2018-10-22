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
//	ԭʼ����תΪѵ����
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
//              �������ַ����а���            
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
//	    		������������
	            if(sign<7 && sign>1) continue;	            	  
//	                                    ����д��data object(InfoDao)
	            if(sign>1) info.setSign("1");
	            else info.setSign("0");
//	            ���򼯺ϴ����û������޶�����ۼƽ�����һ�ʵĽ���ʱ��
//	            ����Ǹ��û��ĵ�һ�����ݵĻ�
	            
	            double transAmt = Double.parseDouble(transMoney);
	            
	            if (jedis.zscore(user+"z", "time") == null){
	            	jedis.zadd(user+"z", Double.parseDouble(transTime) , "time");          //ʱ�����
		        	jedis.zadd(user+"z", transAmt, "accumulatedAmt");  //��Ϊ�ǵ�һ�Σ������ۼƽ�������ʽ��        	
		        	if(dayLimit.equals("0") || dayLimit.isEmpty() || dayLimit == null || singleLimit.equals("0") || singleLimit == null || singleLimit.isEmpty()) info.setOverPaid("0");
		        	else{
		        		if (Double.parseDouble(singleLimit) < transAmt) info.setOverPaid("1");
		        		else info.setOverPaid("0");
		        	}
		        	jedis.zadd(user+"z", Double.parseDouble(transTime), "time");
//		        	���¸��û�����ʷ�����б�
		        	jedis.lpush(user+"l",transTime+","+transMoney);
//		        	д�����ݶ���Ϊ��������������׼��
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
//	            ������ǵ�һ���Ļ�
	            else{
//	            	�������㵱���ۼƽ��
	            	DecimalFormat df1 = new DecimalFormat("0.0000000000000");	            	
	            	String lastDay = df1.format((jedis.zscore(user+"z", "time"))).replace(".", "").substring(6,8);
	            	String thisDay = transTime.substring(6,8);       	
//	            	ͬһ��Ľ����ۼƽ��
	            	if (thisDay.equals(lastDay)) { 
	            		double money = jedis.zscore(user+"z","accumulatedAmt");
	            		money += transAmt;
	            		jedis.zadd(user+"z", money, "accumulatedAmt");
	            	}
//	            	����ͬһ��Ľ��ۼƽ�������Ƚ��
	            	else{
	            		jedis.zadd(user+"z", transAmt, "accumulatedAmt");
	            	}
//		        	�Ƿ񳬶��ֶ��趨
	            	if(dayLimit.equals("0") || dayLimit.isEmpty() || dayLimit == null || singleLimit.equals("0") || singleLimit == null || singleLimit.isEmpty()) info.setOverPaid("0");
		        	else{
		        		if (Double.parseDouble(singleLimit) < transAmt) info.setOverPaid("1");
		        		else if (Double.parseDouble(dayLimit) < jedis.zscore(user+"z","accumulatedAmt")) info.setOverPaid("1");
		        		else info.setOverPaid("0");
		        	}
	            	jedis.zadd(user+"z", Double.parseDouble(transTime), "time");
//		        	�Ȱ�����������
	            	info.setAmt(df.format(transAmt));
		        	if (Math.abs(transAmt - suspiciousAmt1) < 0.1 || Math.abs(transAmt - suspiciousAmt2) < 0.1) 
		        		info.setAmt_suspisious("1");
		        	else info.setAmt_suspisious("0");	
		        	info.setIp(ip);
		        	info.setTimes_window("1");
//	            	�鷳���б���º�ͳ���������㻷��
//		        	���ȸ����б�
		        	jedis.lpush(user+"l",transTime+","+transMoney);
//		        	����б��ȴ���һ,˵��������һ����ʷ��¼,���Ǿʹ��б��ƨ�ɿ�ʼɾ����,��Ϊƨ�ɶ�����õ�.
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
//		        	�����б�֮��,�������µľ�ֵ�ͷ���
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
