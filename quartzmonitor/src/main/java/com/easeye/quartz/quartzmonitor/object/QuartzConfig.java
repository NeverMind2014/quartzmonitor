package com.easeye.quartz.quartzmonitor.object;

/**
 * Quartz连接的配置类
 */
public class QuartzConfig {
	private String configId;
	private String name;
	private String host;
	private int port;
	private String userName;
	private String password;
	private boolean isConnected;

	public QuartzConfig() {
	}

	public QuartzConfig(String configId, String host, int port, String userName, String password) {
		this.configId = configId;
		this.host = host;
		this.port = port;
		this.name = this.getName();
		this.userName = userName;
		this.password = password;
	}

	public String getName() {
		this.name = getHost() + ":" + getPort();
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

}
