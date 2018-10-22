package util;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisConfiguration {

	private String host = "localhost";
	private int port = 6379;
	private String password;
	private int timeout = 3000000;
	private int database = 0;
	private int maxActive = 10;
	private int maxWait = 3000;
	private int maxIdle = 10;
	private int minIdle = 0;



	public JedisPoolConfig getRedisConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxWaitMillis(maxWait);
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		config.setMaxTotal(maxActive);
		return config;
	}

	/**
	 * 
	 * 得到JedisPool连接
	 * 
	 * @return
	 */
	public JedisPool getJedisPool() {
		JedisPoolConfig config = getRedisConfig();
		JedisPool pool = new JedisPool(config, host, port, timeout, password, database);

//		System.out.println("jedis pool initial");
//		System.out.println("host:" + host);
//		System.out.println("port:" + port);
//		System.out.println("password:" + password);
//		System.out.println("timeout:" + timeout);
//		System.out.println("database:" + database);

		return pool;
	}

	public String gethost() {
		return this.host;
	}

	public void sethost(String host) {
		this.host = host;
	}

	public int getport() {
		return this.port;
	}

	public void setport(int port) {
		this.port = port;
	}

	public String getpassword() {
		return this.password;
	}

	public void setpawssword(String pwd) {
		this.password = pwd;
	}

	public int gettimeout() {
		return this.timeout;
	}

	public void settimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getdatabase() {
		return this.database;
	}

	public void setdatabase(int database) {
		this.database = database;
	}
}