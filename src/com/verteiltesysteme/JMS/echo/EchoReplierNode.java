package com.verteiltesysteme.JMS.echo;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EchoReplierNode implements MessageListener {
	private Connection connection;
	private Session session;
	private MessageConsumer consumer;

	public EchoReplierNode() throws NamingException, JMSException {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Queue queue = (Queue) ctx.lookup(Conf.QUEUE);
		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(queue);
		consumer.setMessageListener(this);
		connection.start();
	}

	@Override
	public void onMessage(Message request) {
		try {
			if (request instanceof TextMessage) {
				TextMessage requestText = (TextMessage) request;
				System.out.println("empfangen: " + requestText.getText());
				MessageProducer replyProducer = session.createProducer(request.getJMSReplyTo());
				TextMessage reply = session.createTextMessage();
				reply.setText("echo: " + requestText.getText());
				Thread.sleep(5000);
				replyProducer.send(reply);
				replyProducer.close();
			}
		} catch (JMSException | InterruptedException e) {
			System.err.println(e);
		}

	}

	public static void main(String[] args) {
		EchoReplierNode node = null;
		try {
			node = new EchoReplierNode();
			while (true) {
				Thread.sleep(1000);
			}
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
