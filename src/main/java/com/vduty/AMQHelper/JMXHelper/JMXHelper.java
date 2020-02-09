package com.vduty.AMQHelper.JMXHelper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import com.vduty.AMQHelper.config.AMQConnectConfig;

import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.ConnectionViewMBean;
import org.apache.activemq.broker.jmx.SubscriptionViewMBean;
import org.apache.activemq.broker.jmx.TopicViewMBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Component
@Data
public class JMXHelper {

	public enum ConnectionViewType {
		clientId, remoteAddress
	}

	private AMQConnectConfig amqConnectConfig;

	private static Log log = LogFactory.getLog(JMXHelper.class);

	private BrokerViewMBean brokerViewMBean;
	private MBeanServerConnection connection = null;
	private String pixObjectName;

	public String toString() {
		if (this.brokerViewMBean != null)
			return this.brokerViewMBean.toString();
		else {
			return " null ";
		}

	}

	@Autowired //解决自动注入顺序问提
	public JMXHelper(AMQConnectConfig amqConnectConfig) {
		log.info(" *************** init JMXHelper ******************");
		this.amqConnectConfig = amqConnectConfig;
		pixObjectName = "org.apache.activemq:type=Broker,brokerName=" + amqConnectConfig.getBrokername() + ",";

		try {
			String serviceURL = "service:jmx:rmi:///jndi/rmi://" + amqConnectConfig.getIp() + ":"
					+ amqConnectConfig.getPort() + amqConnectConfig.getPath();
			log.info(" serviceURL is  " + serviceURL);

			JMXServiceURL url = new JMXServiceURL(serviceURL);
			JMXConnector connector = JMXConnectorFactory.connect(url);

			this.connection = connector.getMBeanServerConnection();
			connector.addConnectionNotificationListener(new NotificationListener() {

				@Override
				public void handleNotification(Notification notification, Object handback) {
					log.info(" notification message " + notification.getMessage());

				}
			}, null, null);

			ObjectName name = new ObjectName(amqConnectConfig.getJmxdomain() + ":brokerName="
					+ amqConnectConfig.getBrokername() + ",type=Broker");

			this.brokerViewMBean = MBeanServerInvocationHandler.newProxyInstance(connection, name,
					BrokerViewMBean.class, true);
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error("JMXHelper init error", e);

		}

	}

	/*
	 * objectName example: objectName 可以从jconsole测mbean里获取 mqtt clientConnectors
	 * clientid的方式: org.apache.activemq:type=Broker,brokerName=localhost,connector=
	 * clientConnectors,connectorName=mqtt,connectionViewType=clientId,
	 * connectionName=mqttjs_aeb5ac43
	 * org.apache.activemq:type=Broker,brokerName=localhost,connector=
	 * clientConnectors,connectorName=mqtt,connectionViewType=clientId,
	 * connectionName=subclientIdSKDL5 remoteAddress 的方式：
	 * org.apache.activemq:type=Broker,brokerName=localhost,connector=
	 * clientConnectors,connectorName=mqtt,connectionViewType=remoteAddress,
	 * connectionName=tcp_//127.0.0.1_57006
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * get ConnectorViewMBean By Clientid of connection
	 * 
	 * @param clientId
	 * @return
	 */
	public ConnectionViewMBean getConnectorViewMBean(String clientId, ConnectionViewType connectionViewType) {
		String strObjName = String.format(
				pixObjectName + "connector=clientConnectors,connectorName=mqtt,connectionViewType=%s,connectionName=%s",
				connectionViewType, clientId);
		log.info("ConnectionViewMBean ObjectName  " + strObjName);
		ObjectName connectionObjectName = null;
		try {
			connectionObjectName = new ObjectName(strObjName);
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			log.error(e.getMessage());

		}
		ConnectionViewMBean connectVBean = MBeanServerInvocationHandler.newProxyInstance(this.connection,
				connectionObjectName, ConnectionViewMBean.class, true);

		return connectVBean;

	}

	public List<ConnectionViewMBean> getTopicConnectors(String topicName) {

		List<ConnectionViewMBean> result = new ArrayList<ConnectionViewMBean>();
		TopicViewMBean topicViewMBean = null;
		// org.apache.activemq:brokerName=local,type=Broker
		// org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Topic,destinationName=ActiveMQ.Advisory.Topic
		try {
			ObjectName name = new ObjectName(pixObjectName + "destinationType=Topic,destinationName=" + topicName);
			topicViewMBean = MBeanServerInvocationHandler.newProxyInstance(this.connection, name, TopicViewMBean.class,
					true);
		} catch (MalformedObjectNameException e) {
			log.error(" get tpvpicViewBean error " + e.getMessage());

		}

		try {

			log.info(" topicViewMBean.getSubscriptions() " + topicViewMBean.getSubscriptions().length);
			for (ObjectName mbeanNameConsumerObjectName : topicViewMBean.getSubscriptions()) {

				SubscriptionViewMBean subscriptionViewMBean = MBeanServerInvocationHandler.newProxyInstance(
						this.connection, mbeanNameConsumerObjectName, SubscriptionViewMBean.class, true);
				log.info(" topic subscriptionViewMBean " + subscriptionViewMBean.toString());
				ObjectName connectionObjectName = new ObjectName(
						subscriptionViewMBean.getConnection().getCanonicalName());
				log.info("gettopconnnector sbuscriptionViewBean "
						+ subscriptionViewMBean.getConnection().getCanonicalName());
				ConnectionViewMBean connectionViewMBean = MBeanServerInvocationHandler.newProxyInstance(this.connection,
						connectionObjectName, ConnectionViewMBean.class, true);
				if (connectionViewMBean != null) {
					result.add(connectionViewMBean);

				}
			}
		} catch (MalformedObjectNameException | IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());

		} finally {

		}
		return result;
	}

}
