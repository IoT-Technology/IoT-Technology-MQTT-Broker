package iot.technology.mqtt.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author mushuwei
 */
@Slf4j
@ChannelHandler.Sharable
public class MqttTransportHandler extends SimpleChannelInboundHandler<MqttMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) throws Exception {

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof IOException) {
			// 远程主机强迫关闭了一个现有的连接的异常
			ctx.close();
		} else {
			super.exceptionCaught(ctx, cause);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}
}
