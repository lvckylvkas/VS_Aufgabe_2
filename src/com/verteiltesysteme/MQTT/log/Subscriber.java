package com.verteiltesysteme.MQTT.log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

class Subscriber {
	public static void main(String[] args) {
		MqttClient client = null;
		String clientId = MqttClient.generateClientId();
		try {
			client = new MqttClient(Conf.BROKER, clientId);
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
			client.subscribe(Conf.TOPIC);
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
