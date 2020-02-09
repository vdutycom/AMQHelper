package com.vduty.AMQHelper.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.vduty.AMQHelper.JMXHelper.JMXHelper;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "activemqconnect")
public class AMQConnectConfig {

	private static Log log = LogFactory.getLog(AMQConnectConfig.class);
	private int port;//
	private String path;// : /jmxrmi
	private String jmxdomain;// : org.apache.activemq
	private String ip;// : 127.0.0.1
	private String brokername;// : localhost
	private int jmsport;
	
	public AMQConnectConfig() {
		log.info(" AMQConnectConfig init ....");
	}

}
