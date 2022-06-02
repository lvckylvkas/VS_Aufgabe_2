package com.verteiltesysteme.Aufgabe;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

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

/**Entwickeln Sie einen Adapter, der Nachrichten von JMS und MQTT abonnieren kann. Es soll möglich sein, 
 * mit derselben Adapter-Instanz gleichzeitig sowohl mit einem JMS- als auch mit einem (anderen) MQTT-Broker verbunden zu sein. 
 * Die zu abonnierenden Topics (jeweils 1 Topic für jeden der beiden Server) und Server-Adressen sollen konfigurierbar sein. 

Wenn der Adapter eine Nachricht enthält, soll er prüfen, ob es sich um eine Nachricht handelt, in der ein Objekt vom Typ 
de.hs_mannheim.ffi.vs.syslog.model.SyslogMessage bzw. eines Untertyps enthalten ist. 
Dafür gibt es zwei Möglichkeiten: als textuelle Repräsentation (s. toString()-Methode) oder als serialisiertes Java-Objekt. 
Sie brauchen nur die Variante als Java-Objekt unterstützen.

Wird eine Nachricht vom Adapter empfangen, soll er das SyslogMessage-Objekt (oder das Objekt des entsprechenden Untertyps) 
an einen Websocket "forwarden", wenn das Feld sev mindestens WARNING entspricht (also im Fall EMERGENCY, ALERT, CRITICAL, ERROR und WARNING).
Die Adresse des Websockets soll konfigurierbar sein. **/

public class Adapter implements MessageListener{
    private Connection connection;
    private Session session;
	private MessageConsumer consumer;
    private MessageProducer producer;
    public static final String TOPIC = "com/verteiltesysteme/MQTT/4711/messages";
    public static final String BROKER = "tcp://localhost:1883";

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

        MqttClient client = null;
		String clientId = MqttClient.generateClientId();
		try {
			client = new MqttClient(BROKER, clientId);
			client.setCallback(new MqttCallback() {

				@Override
				public void connectionLost(Throwable arg0) {
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken arg0) {
				}

				@Override
				public void messageArrived(String topic, MqttMessage m) throws Exception {
					System.out.println("Topic: " + topic + ",  Message: " + m.toString());
				}
			});
			client.connect();
			client.subscribe(TOPIC);
			while (true) {
				Thread.sleep(1000);
			}
		} catch (MqttException | InterruptedException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				client.disconnect();
			} catch (MqttException e) {
				// unrecoverable
			}
		}
	}
}
