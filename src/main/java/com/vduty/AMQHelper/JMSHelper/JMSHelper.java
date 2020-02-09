package com.vduty.AMQHelper.JMSHelper;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vduty.AMQHelper.config.AMQConnectConfig;
import com.vduty.AMQHelper.web.Home;

/**
 * 
 * @author vduty yeluxing
 * 
 */

@Component
public class JMSHelper {
	private static Log log = LogFactory.getLog(JMSHelper.class);
	private AMQConnectConfig amqConnectConfig;
	final Session session;

	@Autowired
	public JMSHelper(AMQConnectConfig amqConnectConfig) throws JMSException {
		this.amqConnectConfig = amqConnectConfig;
		String jmsconnectStr = String.format("tcp://%s:%s", amqConnectConfig.getIp(), amqConnectConfig.getJmsport());
		ConnectionFactory factory = new ActiveMQConnectionFactory(jmsconnectStr);

		javax.jms.Connection connection;

		connection = factory.createConnection();

		connection.start();
		log.info("JMSHelper connect id:  " + connection.getClientID());

		session = connection.createSession(false/* 支持事务 */, Session.AUTO_ACKNOWLEDGE);

		
		/**
		 * 监听连接事件
		 */
		Destination connectTopic = AdvisorySupport.getConnectionAdvisoryTopic();

		MessageConsumer conn_consumer = session.createConsumer(connectTopic);

		conn_consumer.setMessageListener(new MessageListener() {

			public void onMessage(Message message) {

				System.out.println("*********** connectTopic advisory message : " + message);

			}

		});

	}

	/**
	 * 监听topic被订阅的情况
	 * @param topicname
	 * @throws JMSException
	 */
	public void getConsumerAdvisoryTopic(String topicname) throws JMSException {

		Destination topic = AdvisorySupport.getConsumerAdvisoryTopic(session.createTopic(topicname));

		MessageConsumer consumer = session.createConsumer(topic);

		consumer.setMessageListener(new MessageListener() {

			public void onMessage(Message message) {

				System.out.println("------- advisory message : " + message);

			}

		});
	}

}
