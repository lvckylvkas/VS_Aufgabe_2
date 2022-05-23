package com.verteiltesysteme.JMS.echo;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EchoRequesterNode {
	private Connection connection;
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;
	private Queue replyQueue;

	public EchoRequesterNode() throws NamingException, JMSException {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue = (Queue) ctx.lookup(Conf.QUEUE);
		producer = session.createProducer(queue);
		replyQueue = session.createTemporaryQueue();
		consumer = session.createConsumer(replyQueue);
		connection.start();
	}

	public void receiveAndPrintMessages() throws JMSException {
		Message request;
		while ((request = consumer.receive()) != null) {
			try {
				if (request instanceof TextMessage) {
					TextMessage requestText = (TextMessage) request;
					String messageText = requestText.getText();
					System.out.println("empfangen: " + messageText);
				}
			} catch (JMSException e) {
				System.err.println(e);
			}
		}
	}

	public void sendMessage(String text) throws JMSException {
		TextMessage message = session.createTextMessage();
		message.setText(text);
		message.setJMSReplyTo(replyQueue);
		producer.send(message);
	}

	public static void main(String[] args) {
		String text = args[0];
		EchoRequesterNode node = null;
		try {
			node = new EchoRequesterNode();
			node.sendMessage(text);
			node.receiveAndPrintMessages();
		} catch (NamingException | JMSException e) {
			System.err.println(e);
		} finally {
			try {
				if (node != null && node.producer != null) {
					node.producer.close();
				}
				if (node != null && node.consumer != null) {
					node.consumer.close();
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
