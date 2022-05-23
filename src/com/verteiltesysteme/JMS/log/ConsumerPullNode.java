package com.verteiltesysteme.JMS.log;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ConsumerPullNode {
	private Connection connection;
	private Session session;
	private MessageConsumer consumer;

	public ConsumerPullNode() throws NamingException, JMSException {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination queue = (Destination) ctx.lookup(Conf.QUEUE);
		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(queue);
		connection.start();
	}

	public void receiveAndPrintMessages(long timeout) throws JMSException {
		Message message;
		while ((message = consumer.receive(timeout)) != null) {
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				String messageText = textMessage.getText();
				System.out.println(messageText);
			}
		}
	}

	public static void main(String[] args) {
		long timeout = Long.parseLong(args[0]);
		ConsumerPullNode node = null;
		try {
			node = new ConsumerPullNode();
			node.receiveAndPrintMessages(timeout);
		} catch (NamingException | JMSException e) {
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