package com.easeye.quartz.quartzmonitor.object;

import javax.management.ObjectName;

import com.easeye.quartz.quartzmonitor.core.QuartzClient;

public class JMXInput {
	private QuartzClient quartzClient;
	private String[] signature;
	private String operation;
	private Object[] parameters;
	private ObjectName objectName;

	public JMXInput() {
	}

	/**
	 * 
	 * @param quartzInstance
	 * @param signature
	 * @param operation
	 * @param parameters
	 * @param objectName
	 */
	public JMXInput(QuartzClient quartzInstance, String[] signature, String operation, Object[] parameters, ObjectName objectName) {
		this.quartzClient = quartzInstance;
		this.signature = signature;
		this.operation = operation;
		this.parameters = parameters;
		this.objectName = objectName;
	}

	public QuartzClient getQuartzInstanceConnection() {
		return quartzClient;
	}

	public void setQuartzInstanceConnection(QuartzClient quartzClient) {
		this.quartzClient = quartzClient;
	}

	public String[] getSignature() {
		return signature;
	}

	public void setSignature(String[] signature) {
		this.signature = signature;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public ObjectName getObjectName() {
		return objectName;
	}

	public void setObjectName(ObjectName objectName) {
		this.objectName = objectName;
	}
}