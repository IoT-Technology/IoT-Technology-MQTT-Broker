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

	private static final int IDLE_TIME = 15;

	public MqttIdleStateHandler() {
		super(IDLE_TIME, 0, 0, TimeUnit.SECONDS);
	}

	@Override
	protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
		String idle_time = (String) ctx.channel().attr(AttributeKey.valueOf("idle_time")).get();
		log.info(IDLE_TIME + "秒内未读到数据，关闭连接");
		ctx.channel().close();
	}
}
