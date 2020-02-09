package com.vduty.AMQHelper.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.activemq.broker.jmx.ConnectionViewMBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vduty.AMQHelper.JMSHelper.JMSHelper;
import com.vduty.AMQHelper.JMXHelper.JMXHelper;
import com.vduty.AMQHelper.entity.WebStringBuild;

@RestController
@RequestMapping("/home")
public class Home {

	private static Log log = LogFactory.getLog(Home.class);
	@Autowired
	JMXHelper jmxHelper;

	@Autowired
	JMSHelper jmsHelper;
	
	@GetMapping("")
	public String index() {

		WebStringBuild sb = new WebStringBuild();
		sb.append("<a href='/home/gettopicconsumer/topicname' >gettopicconsumer</a>");
		sb.append("<a href='/home/getconnectorbyclientid/{clientid}' >getconnectorbyclientid</a>");
		sb.append("<a href='/home/stopconnector/{clientid}' >stopconnector</a>");
		return sb.toString();
	}

	@GetMapping("/gettopicconsumer/{topicName}")
	public String getTopicConsumer(@PathVariable(value = "topicName") String topicName) {
		List<ConnectionViewMBean> lConnectbean = new ArrayList<ConnectionViewMBean>();
		lConnectbean = jmxHelper.getTopicConnectors(topicName);
		WebStringBuild sb = new WebStringBuild("<br/>");
		for (ConnectionViewMBean conn : lConnectbean) {
			sb.append(" clientid:");
			sb.append(conn.getClientId());
			sb.append(" username:");
			sb.append(conn.getUserName());
			sb.append(" remoteaddress:");
			sb.append(conn.getRemoteAddress().replace("//", "").split("\\:")[1]);
			sb.append(" consumers count: ");
			sb.append(String.valueOf(conn.getConsumers().length));
			sb.append(" producter count:");
			sb.append(String.valueOf(conn.getProducers().length));
			sb.append("<br/>");
		}

		return sb.toString();

	}

	@GetMapping("/getconnectorbyclientid/{clientid}")
	public String getConnectorViewMBeanByClientid(@PathVariable(value = "clientid") String clientId) {

		ConnectionViewMBean cBean = jmxHelper.getConnectorViewMBean(clientId, JMXHelper.ConnectionViewType.clientId);
		String address = null;
		try {
			if (cBean != null)
			address = cBean.getRemoteAddress();
			else
				log.error(" cBean is null");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		WebStringBuild sb = new WebStringBuild(address);

		return sb.toString();

	}

	/**
	 * stop(disconnect) the connector of getbyclientid
	 * 可以断开，但是client或马上重新连接
	 * @param clientId
	 * @return
	 */
	@GetMapping("/stopconnector/{clientid}")
	public String stopConnector(@PathVariable(value = "clientid") String clientId) {
		boolean result = true;

		ConnectionViewMBean cBean = jmxHelper.getConnectorViewMBean(clientId, JMXHelper.ConnectionViewType.clientId);
		try {
			cBean.stop();
		} catch (Exception e) {

			result = false;
			log.error("stop fail");
		}
		WebStringBuild sb = new WebStringBuild(String.valueOf(result));
		return sb.toString();

	}

}
