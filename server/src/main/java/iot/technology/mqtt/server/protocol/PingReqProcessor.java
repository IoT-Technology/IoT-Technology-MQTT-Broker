package iot.technology.mqtt.server.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author mushuwei
 * @description https://iot.mushuwei.cn/#/mqtt3/mqtt-pingreq-0312
 */
@Slf4j
@Service("pingreqProcessor")
public class PingReqProcessor implements AbstractProtocolProcessor {

	@Override
	public void processMqttProtocol(Channel channel, MqttMessage msg) {
		MqttMessage pingRespMessage = MqttMessageFactory.newMessage(
				new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0),
				null,
				null);
		log.info("pingreq  clientId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get());
		channel.writeAndFlush(pingRespMessage);
	}
}


