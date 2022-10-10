package iot.technology.mqtt.server.protocol;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import iot.technology.mqtt.server.MqttSystemContext;
import iot.technology.mqtt.storage.subscribe.domain.SubscribeStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <pre>
 * **********************************************************************
 * +-----------+-------------------+
 * | QoS Level | Expected Response |
 * +-------------------------------+
 * |  QoS 0    |    None           |
 * +-------------------------------+
 * |  QoS 1    |   PUBACK Packet   |
 * +-------------------------------+
 * |  QoS 2    |   PUBREC Packet   |
 * +-----------+-------------------+
 * </pre>
 *
 * @author mushuwei
 * @description 发布消息处理逻辑
 */
@Slf4j
@Component("publishProcessor")
public class PublishProcessor implements AbstractProtocolProcessor {

	//剩余长度
	private static final Integer REPLY_REMAINING_LENGTH = 2;

	@Resource(name = "mqttSystemContext")
	private MqttSystemContext mqttSystemContext;

	@Override
	public void processMqttProtocol(Channel channel, MqttMessage msg) {
		String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
		MqttPublishMessage mqttPubMessage = (MqttPublishMessage) msg;
		/**
		 * mqtt发布消息格式
		 * mqttPubVariableHeader 发布消息变长头
		 * messageBytes 消息体
		 * qosLevel 消息级别
		 */
		MqttPublishVariableHeader mqttPubVariableHeader = mqttPubMessage.variableHeader();
		String topicName = mqttPubVariableHeader.topicName();
		byte[] messageBytes = new byte[mqttPubMessage.payload().readableBytes()];
		mqttPubMessage.payload().getBytes(mqttPubMessage.payload().readerIndex(), messageBytes);
		String messageStr = new String(messageBytes);
		MqttQoS qosLevel = msg.fixedHeader().qosLevel();
		log.info("clientId:{}, qos:{}, topicName:{}, message:{}", clientId, qosLevel, topicName, messageStr);

		if (qosLevel == MqttQoS.AT_MOST_ONCE) {
		} else if (qosLevel == MqttQoS.AT_LEAST_ONCE) {
			this.sendPubAckMessage(channel, mqttPubVariableHeader.packetId());
		} else if (qosLevel == MqttQoS.EXACTLY_ONCE) {
			this.sendPubRecMessage(channel, mqttPubVariableHeader.packetId());
		}
		this.deliveryMessage(topicName, qosLevel, messageBytes, false, false);
	}

	private void deliveryMessage(String topic, MqttQoS mqttQoS, byte[] messageBytes, boolean retain, boolean dup) {
		Map<String, SubscribeStore> subscribeStoreMap = mqttSystemContext.getSubscribeStoreService().search(topic);
		for (Map.Entry<String, SubscribeStore> entry : subscribeStoreMap.entrySet()) {
			SubscribeStore subscribeStore = entry.getValue();
			//看某个设备是否存活，如果存活进行投递
			if (mqttSystemContext.getSessionStoreService().containsKey(entry.getKey())) {
				MqttQoS respQoS = mqttQoS.value() > subscribeStore.getMqttQoS() ? MqttQoS.valueOf(subscribeStore.getMqttQoS()) : mqttQoS;
				MqttPublishMessage deliveryMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
						new MqttPublishVariableHeader(topic, 0),
						Unpooled.buffer().writeBytes(messageBytes));
				log.info("PUBLISH - clientId: {}, topic: {}, Qos: {}", subscribeStore.getClientId(), topic, respQoS.value());
				mqttSystemContext.getSessionStoreService().get(entry.getKey()).getChannel().writeAndFlush(deliveryMessage);
			}
		}
	}

	private void sendPubAckMessage(Channel channel, int messageId) {
		MqttPubAckMessage pubAckMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
				new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_LEAST_ONCE, false, REPLY_REMAINING_LENGTH),
				MqttMessageIdVariableHeader.from(messageId),
				null
		);
		channel.writeAndFlush(pubAckMessage);
	}

	private void sendPubRecMessage(Channel channel, int messageId) {
		MqttMessage pubRecMessage = MqttMessageFactory.newMessage(
				new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.EXACTLY_ONCE, false, REPLY_REMAINING_LENGTH),
				MqttMessageIdVariableHeader.from(messageId),
				null
		);
		channel.writeAndFlush(pubRecMessage);
	}
}
