package iot.technology.mqtt.server.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mushuwei
 */
@Slf4j
@Service("subscribeProcessor")
public class SubScribeProcessor implements AbstractProtocolProcessor {

	@Override
	public void processMqttProtocol(Channel channel, MqttMessage msg) {
		MqttSubscribeMessage mqttSubMessage = (MqttSubscribeMessage) msg;
		List<MqttTopicSubscription> topicSubscriptions = mqttSubMessage.payload().topicSubscriptions();
		if (this.validTopicFilter(topicSubscriptions)) {
			String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
			List<Integer> mqttQoSList = new ArrayList<Integer>();
			for (MqttTopicSubscription mqttTopicSubscription : topicSubscriptions) {
				String topicFilter = mqttTopicSubscription.topicName();
				MqttQoS mqttQoS = mqttTopicSubscription.qualityOfService();
				mqttQoSList.add(mqttQoS.value());

			}
			MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
					MqttMessageIdVariableHeader.from(mqttSubMessage.variableHeader().messageId()),
					new MqttSubAckPayload(mqttQoSList));
			channel.writeAndFlush(subAckMessage);
			log.info("SUBSCRIBE - clientId: {}, topicSubscriptions: {}", clientId, topicSubscriptions);
		}
	}

	private boolean validTopicFilter(List<MqttTopicSubscription> topicSubscriptions) {

		for (MqttTopicSubscription topic : topicSubscriptions) {
			String topicName = topic.topicName();
			//以#或+符号开头的、以/符号结尾的订阅按非法订阅处理, 这里没有参考标准协议
			if (StringUtils.startsWith(topicName, "+") || StringUtils.endsWith(topicName, "/")) {
				return false;
			}
			//如果出现多个#符号的订阅按非法订阅处理
			if (StringUtils.countMatches(topicName, "#") > 1) {
				return false;
			}
			//如果+符合和/+字符串出现的次数不等的情况按非法订阅处理
			if (StringUtils.countMatches(topicName, "+") != StringUtils.countMatches(topicName, "/+")) {
				return false;
			}
		}
		return true;
	}

}
