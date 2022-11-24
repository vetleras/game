package mqtt;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import runtime.SchedulerData;

public class MQTTclient implements MqttCallback {
	private SchedulerData scheduler;
	private MqttClient client;

	public static final String BrokerURI = "tcp://broker.hivemq.com:1883";

	public static final String CONNECTIONERROR = "MQTTError", // Added to scheduler if client can't connect
			GENERALERROR = "MQTTGeneralError", // Added to scheduler if error happens while sending message or
												// subscribing to topic
			MESSAGEARRIVED = "MQTTMessageArrived"; // Added to scheduler when MqttClient receives a message

	public static final String TOPIC = "lsurgvfkerhgvfreghfv";

	public static enum Topic {
		JudgeToPi,
		PiToJudge,
		ParticipantToJudge,
		JudgeToParticipant,
		// -------------------------- KEEPALIVE --------------------------
		Keepalive
		// -------------------------- END KEEPALIVE --------------------------
	}

	public MQTTclient(String broker, String myAddress, boolean conf, SchedulerData s) {
		scheduler = s;
		MemoryPersistence pers = new MemoryPersistence();
		try {
			client = new MqttClient(broker, myAddress, pers);
			MqttConnectOptions opts = new MqttConnectOptions();
			opts.setCleanSession(true);
			client.connect(opts);
			client.setCallback(this);
			client.subscribe(TOPIC);

		} catch (MqttException e) {
			System.err.println("MQTT Exception: " + e);
			scheduler.addToQueueLast(CONNECTIONERROR);
		}
	}

	public void connectionLost(Throwable e) {
		System.err.println("MQTT Connection lost: " + e);
		scheduler.addToQueueLast(CONNECTIONERROR);
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
	}

	public void messageArrived(String topic, MqttMessage mess) {
		String message = new String(mess.getPayload());
		System.out.println("MQTT message arrived: " + message);
		String[] args = message.split(" ", 2);
		scheduler.addToQueueLast(args[0], args[1]);
	}

	public void send(String eventId, String payload) {
		try {
			MqttMessage message = new MqttMessage((eventId + ' ' + payload).getBytes());
			message.setQos(2);
			client.publish(TOPIC, message);
		} catch (MqttException e) {
			System.out.println(e);

		}
	}
}
