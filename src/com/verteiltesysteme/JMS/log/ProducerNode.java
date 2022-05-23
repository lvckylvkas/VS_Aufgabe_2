package com.verteiltesysteme.JMS.log;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ProducerNode {
	private Connection connection;
	private Session session;
	private MessageProducer producer;

	public ProducerNode() throws NamingException, JMSException {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination queue = (Destination) ctx.lookup(Conf.QUEUE);
		producer = session.createProducer(queue);
	}

	public void sendMessage(String text, String priority) throws JMSException {
		TextMessage message = session.createTextMessage();
		message.setText(text);
		message.setStringProperty("Priority", priority);
		producer.send(message);
	}

	public static void main(String[] args) {
		String text = args[0];
		String priority = args[1];
		ProducerNode node = null;
		try {
			node = new ProducerNode();
			node.sendMessage(text, priority);
			System.out.println("Message: " + text);
		} catch (NamingException | JMSException e) {
			System.err.println(e);
		} finally {
			try {
				if (node != null && node.producer != null) {
					node.producer.close();
				}
				if (node != null && node.session != null) {
					node.session.close();
				}
				if (node != null && node.connection != null) {
					node.connection.close();
				}
			} catch (JMSException e) {
				System.err.println(e);
			}
		}
	}
}
