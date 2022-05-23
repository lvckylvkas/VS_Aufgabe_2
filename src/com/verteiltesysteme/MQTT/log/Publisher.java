package com.verteiltesysteme.MQTT.log;

import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

class Publisher {
	public static void main(String[] args) {
		MqttClient client;
		String clientId = MqttClient.generateClientId();
		try {
			client = new MqttClient(Conf.BROKER, clientId);
			client.connect();
			MqttMessage message = new MqttMessage();
			for (int i = 0; i < 30; i++) {
				String m = "[" + clientId + "] message " + i + ": " + (new Date()).toString();
				message.setPayload(m.getBytes());
				client.publish(Conf.TOPIC, message);
			}
			client.disconnect();
		} catch (MqttException e) {
			System.err.println(e.getMessage());
		}
	}
}
