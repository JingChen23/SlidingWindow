package dao;

public class InfoDao {
	private String overPaid;
	private String amt_suspisious;
	private String amt;
	private String ip;
	private String times_window;
	private String avg_amt_window;
	private String var_amt_window;
	private String avg_time_window;
	private String var_time_window;
	private String amt_avg;
	public String getAmt_avg() {
		return amt_avg;
	}
	public void setAmt_avg(String amt_avg) {
		this.amt_avg = amt_avg;
	}
	private String sign;
	public String getOverPaid() {
		return overPaid;
	}
	public void setOverPaid(String overPaid) {
		this.overPaid = overPaid;
	}
	public String getAmt_suspisious() {
		return amt_suspisious;
	}
	public void setAmt_suspisious(String amt_suspisious) {
		this.amt_suspisious = amt_suspisious;
	}
	public String getAmt() {
		return amt;
	}
	public void setAmt(String amt) {
		this.amt = amt;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getTimes_window() {
		return times_window;
	}
	public void setTimes_window(String times_window) {
		this.times_window = times_window;
	}
	public String getAvg_amt_window() {
		return avg_amt_window;
	}
	public void setAvg_amt_window(String avg_amt_window) {
		this.avg_amt_window = avg_amt_window;
	}
	public String getVar_amt_window() {
		return var_amt_window;
	}
	public void setVar_amt_window(String var_amt_window) {
		this.var_amt_window = var_amt_window;
	}
	public String getAvg_time_window() {
		return avg_time_window;
	}
	public void setAvg_time_window(String avg_time_window) {
		this.avg_time_window = avg_time_window;
	}
	public String getVar_time_window() {
		return var_time_window;
	}
	public void setVar_time_window(String var_time_window) {
		this.var_time_window = var_time_window;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
		
}
