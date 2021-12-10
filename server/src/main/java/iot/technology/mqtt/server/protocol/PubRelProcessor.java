package iot.technology.mqtt.server.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author mushuwei
 * @description PUBREL-发布释放(QoS 2,第二步)
 * PUBCOMP-发布完成(QoS 2,第三步)
 */
@Slf4j
@Service("pubrelProcessor")
public class PubRelProcessor implements AbstractProtocolProcessor {

	@Override
	public void processMqttProtocol(Channel channel, MqttMessage msg) {
		String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
		MqttPubReplyMessageVariableHeader variableHeader = (MqttPubReplyMessageVariableHeader) msg.variableHeader();
		int messageId = variableHeader.messageId();
		MqttMessage pubCompMessage = MqttMessageFactory.newMessage(
				new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.EXACTLY_ONCE, false, 2),
				MqttMessageIdVariableHeader.from(messageId),
				null
		);
		log.info("PUBREL client:{}, messageId:{}", clientId, messageId);
		channel.writeAndFlush(pubCompMessage);
	}
}
