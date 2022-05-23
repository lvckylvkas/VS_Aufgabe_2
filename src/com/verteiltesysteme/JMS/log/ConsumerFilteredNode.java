package com.verteiltesysteme.JMS.log;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ConsumerFilteredNode implements MessageListener {
	private Connection connection;
	private Session session;
	private MessageConsumer consumer;

	public ConsumerFilteredNode() throws NamingException, JMSException {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination queue = (Destination) ctx.lookup(Conf.QUEUE);
		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(queue, "Priority='high'");
		consumer.setMessageListener(this);
		connection.start();
	}

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				String messageText = textMessage.getText();
				String priority = textMessage.getStringProperty("Priority");
				System.out.println(messageText + " [Priority=" + priority + "]");
			}
		} catch (JMSException e) {
			System.err.println(e);
		}
	}

	public static void main(String[] args) {
		long wait = Long.parseLong(args[0]);
		ConsumerFilteredNode node = null;
		try {
			node = new ConsumerFilteredNode();
			Thread.sleep(wait);
		} catch (InterruptedException | NamingException | JMSException e) {
			System.err.println(e);
		} finally {
			try {
				if (node != null && node.consumer != null)
					node.consumer.close();
				if (node != null && node.session != null)
					node.session.close();
				if (node != null && node.connection != null)
					node.connection.close();
			} catch (JMSException e) {
				System.err.println(e);
			}
		}
	}

}
