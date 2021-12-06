package iot.technology.mqtt.server.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author mushuwei
 */
@Slf4j
@Service("disconnectProcessor")
public class DisConnectProcessor implements AbstractProtocolProcessor {

	@Override
	public void processMqttProtocol(Channel channel, MqttMessage msg) {
		String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
		log.info("disconnect clientId: {}", clientId);
		channel.close();
	}
}
