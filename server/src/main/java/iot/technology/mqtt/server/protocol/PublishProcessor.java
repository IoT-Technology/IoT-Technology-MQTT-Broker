package iot.technology.mqtt.server.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
		byte[] messageBytes = new byte[mqttPubMessage.payload().readableBytes()];
		mqttPubMessage.payload().getBytes(mqttPubMessage.payload().readerIndex(), messageBytes);
		String messageStr = new String(messageBytes);
		MqttQoS qosLevel = msg.fixedHeader().qosLevel();
		log.info("clientId:{}, qos:{}, topicName:{}, message:{}", clientId, qosLevel, mqttPubVariableHeader.topicName(), messageStr);

		if (qosLevel == MqttQoS.AT_MOST_ONCE) {
		} else if (qosLevel == MqttQoS.AT_LEAST_ONCE) {
			this.sendPubAckMessage(channel, mqttPubVariableHeader.packetId());
		} else if (qosLevel == MqttQoS.EXACTLY_ONCE) {
			this.sendPubRecMessage(channel, mqttPubVariableHeader.packetId());
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
