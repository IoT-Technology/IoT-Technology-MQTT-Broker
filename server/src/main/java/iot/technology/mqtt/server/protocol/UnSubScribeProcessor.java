package iot.technology.mqtt.server.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import iot.technology.mqtt.server.MqttSystemContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mushuwei
 * @description 取消订阅
 * 客户端发送UNSUBSCRIBE报文给服务端，用于取消订阅主题
 */
@Slf4j
@Service("unsubscribeProcessor")
public class UnSubScribeProcessor implements AbstractProtocolProcessor {

	@Resource(name = "mqttSystemContext")
	private MqttSystemContext mqttSystemContext;

	@Override
	public void processMqttProtocol(Channel channel, MqttMessage msg) {
		MqttUnsubscribeMessage mqttUnsubMessage = (MqttUnsubscribeMessage) msg;
		String clinetId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
		List<String> unSubTopics = mqttUnsubMessage.payload().topics();
		if (!unSubTopics.isEmpty()) {
			unSubTopics.forEach(topic -> {
				mqttSystemContext.getSubscribeStoreService().remove(topic, clinetId);
			});
		}
		MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
				new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 2),
				MqttMessageIdVariableHeader.from(mqttUnsubMessage.variableHeader().messageId()),
				null);
		channel.writeAndFlush(unsubAckMessage);
		log.info("UNSUBSCRIBE - clientId: {}, unSubTopics: {}", clinetId, unSubTopics);
	}
}
