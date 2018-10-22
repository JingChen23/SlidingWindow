package util;

import java.util.ArrayList;
import java.util.List;

//这个类主要是用来计算列表内的时间差均值方差和金额差均值方差,用这个类新建一个对象,然后调用compute函数进行计算.结果会存到类的四个成员变量里面.
public class FeatureComputeHelper {
	private double timeAvg;
	private double timeVar;
	private double amtAvg;    //金额差平均值
	private double amtVar;
	private double averageAmt; //金额平均值
	public void compute (List<String> list, int windowSize){
		if(list.size() == 1){
			setAmtAvg(1000);
			setAmtVar(1000);
			setTimeAvg(windowSize);
			setTimeVar(windowSize);
			setAverageAmt(Double.parseDouble(list.get(0).split(",")[1]));
		}
		else{
			TimeTransHelper th = new TimeTransHelper();
			List<Double> timeGaps = new ArrayList<> ();
			List<Double> moneyGaps = new ArrayList<> ();
			for (int i = 1; i < list.size(); i++){
				double money = Double.parseDouble(list.get(i).split(",")[1]);
				double timeGap = Math.abs(th.toTime(list.get(i).split(",")[0]) - th.toTime(list.get(i-1).split(",")[0]));
				double moneyGap = Math.abs(money - Double.parseDouble(list.get(i-1).split(",")[1]));
				timeAvg += timeGap;
				amtAvg += moneyGap;
				timeGaps.add(timeGap/1000);
				moneyGaps.add(moneyGap);
				averageAmt += money;
			}
			int size = list.size();
			timeAvg /= (size-1);
			timeAvg /= 1000;
			amtAvg /= (size-1);
			averageAmt /= size;
			for (int i = 0; i < timeGaps.size(); i++){
				timeVar += Math.sqrt((timeGaps.get(i)-timeAvg) * (timeGaps.get(i)-timeAvg));
				amtVar +=  Math.sqrt((moneyGaps.get(i)-amtAvg) * (moneyGaps.get(i)-amtAvg));
			}
			if (timeGaps.size() == 1) timeVar = 0;
			else timeVar /= (timeGaps.size()-1);
			if (moneyGaps.size() == 1) amtVar = 0;
			else amtVar /= (moneyGaps.size()-1);
		}
	}
	
	public FeatureComputeHelper() {
		super();
		this.timeAvg = 0;
		this.timeVar = 0;
		this.amtAvg = 0;
		this.amtVar = 0;
	}

	public double getTimeAvg() {
		return timeAvg;
	}

	public void setTimeAvg(double timeAvg) {
		this.timeAvg = timeAvg;
	}

	public double getTimeVar() {
		return timeVar;
	}

	public double getAverageAmt() {
		return averageAmt;
	}

	public void setAverageAmt(double averageAmt) {
		this.averageAmt = averageAmt;
	}

	public void setTimeVar(double timeVar) {
		this.timeVar = timeVar;
	}

	public double getAmtAvg() {
		return amtAvg;
	}

	public void setAmtAvg(double amtAvg) {
		this.amtAvg = amtAvg;
	}

	public double getAmtVar() {
		return amtVar;
	}

	public void setAmtVar(double amtVar) {
		this.amtVar = amtVar;
	}

}
