package iot.technology.mqtt.server.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author mushuwei
 */
@Slf4j
public class MqttIdleStateHandler extends IdleStateHandler {

	public MqttIdleStateHandler(int idle_time) {
		super(idle_time, 0, 0, TimeUnit.SECONDS);
	}

	@Override
	protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
		String idle_time = (String) ctx.channel().attr(AttributeKey.valueOf("idle_time")).get();
		log.info(idle_time + "秒内未读到数据，关闭连接");
		ctx.channel().close();
	}
}
