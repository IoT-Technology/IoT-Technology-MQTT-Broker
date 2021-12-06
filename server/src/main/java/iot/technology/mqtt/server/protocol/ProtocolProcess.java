package iot.technology.mqtt.server.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author mushuwei
 */
@Slf4j
@Component
public class ProtocolProcess {

	@Resource
	private ApplicationContext applicationContext;

	public void process(ChannelHandlerContext ctx, MqttMessage msg) {
		AbstractProtocolProcessor protocolProcessor =
				(AbstractProtocolProcessor) applicationContext.getBean(msg.fixedHeader().messageType().name().toLowerCase() + "Processor");
		protocolProcessor.processMqttProtocol(ctx.channel(), msg);
	}

}
