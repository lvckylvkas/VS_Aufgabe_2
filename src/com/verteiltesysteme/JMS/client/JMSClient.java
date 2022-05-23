package com.verteiltesysteme.JMS.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSClient implements MessageListener {
	private Connection connection;
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;

	public JMSClient(String sendDest, String receiveDest) throws NamingException, JMSException {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destOut = (Destination) ctx.lookup(sendDest);
		Destination destIn = (Destination) ctx.lookup(receiveDest);
		producer = session.createProducer(destOut);
		consumer = session.createConsumer(destIn);
		consumer.setMessageListener(this);
		connection.start();
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			try {
				System.out.println(textMessage.getText());
			} catch (JMSException e) {
				System.err.println(e);
			}
		}
	}

	public static void main(String[] args) {
		JMSClient node = null;
		try {
			node = new JMSClient("com.verteiltesysteme.JMS.client.queue1", "com.verteiltesysteme.JMS.client.queue2");
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			String line;
			while (true) {
				line = input.readLine();
				node.producer.send(node.session.createTextMessage(line));
			}
		} catch (NamingException | JMSException | IOException e) {
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