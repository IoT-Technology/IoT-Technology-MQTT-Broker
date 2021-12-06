package iot.technology.mqtt.server.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * @author mushuwei
 */
public interface AbstractProtocolProcessor {

	void processMqttProtocol(Channel channel, MqttMessage msg);
}
