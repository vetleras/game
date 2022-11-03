package mqtt;

import java.nio.charset.StandardCharsets;
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

	public static final String
		CONNECTIONERROR = "MQTTError",  // Added to scheduler if client can't connect
		GENERALERROR = "MQTTGeneralError", // Added to scheduler if error happens while sending message or subscribing to topic
		MESSAGEARRIVED = "MQTTMessageArrived"; // Added to scheduler when MqttClient receives a message

    public static enum Topic {
        JudgeToPi,
        PiToJudge,
        ParticipantToJudge,
        JudgeToParticipant
    }
    public static final HashMap<Topic, String> TOPICS = new HashMap<Topic, String>(MQTTclient.getNewTopicMap());

	public MQTTclient(String broker, String myAddress, boolean conf, SchedulerData s) {
		scheduler = s;
		MemoryPersistence pers = new MemoryPersistence();
		try {
			client = new MqttClient(broker, myAddress, pers);
			MqttConnectOptions opts = new MqttConnectOptions();
			opts.setCleanSession(true);
			client.connect(opts);
			client.setCallback(this);
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
		// System.out.println("Delivery completed")
	}

	public void messageArrived(String topic, MqttMessage mess) {
		String message = new String(mess.getPayload(), StandardCharsets.UTF_8);
		scheduler.addToQueueLast(MESSAGEARRIVED, message);
	}

	public void sendMessage(Topic topic, String content) {
		if (!this.client.isConnected()) {
			System.out.println("Client not connected");
			return;
		}
		MqttMessage message = new MqttMessage(content.getBytes());
		message.setQos(2);
		try {
			this.client.publish(TOPICS.get(topic), message);
			System.out.println("Message published");
		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
			scheduler.addToQueueLast(GENERALERROR);
		}
	}

	public void subscribeToTopic(Topic topic) {
		try {
			this.client.subscribe(TOPICS.get(topic));
		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
			scheduler.addToQueueLast(GENERALERROR);
		}
	}

	/**
	Populates a hashmap with topics and their string-equivalents.
	@return HashMap<Topic, String> populated with topics
	 */
    private static HashMap<Topic, String> getNewTopicMap() {
        HashMap<Topic, String> tmpTopics = new HashMap<Topic, String>();

        tmpTopics.put(MQTTclient.Topic.JudgeToParticipant, "JudgeToParticipant");
        tmpTopics.put(MQTTclient.Topic.ParticipantToJudge, "ParticipantToJudge");
        tmpTopics.put(MQTTclient.Topic.JudgeToPi, "JudgeToPi");
        tmpTopics.put(MQTTclient.Topic.PiToJudge, "PiToJudge");

        return tmpTopics;
    }
}