package iot.technology.mqtt.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.util.ResourceLeakDetector;
import iot.technology.mqtt.server.protocol.MqttIdleStateHandler;
import iot.technology.mqtt.server.protocol.ProtocolProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * @author mushuwei
 */
@Component
@ConfigurationProperties(prefix = "mqtt")
@Slf4j
public class MqttServer {
	@Value("${mqtt.bind_address}")
	private String host;
	@Value("${mqtt.bind_port}")
	private Integer port;

	@Value("${mqtt.netty.leak_detector_level}")
	private String leakDetectorLevel;
	@Value("${mqtt.netty.boss_group_thread_count}")
	private Integer bossGroupThreadCount;
	@Value("${mqtt.netty.worker_group_thread_count}")
	private Integer workerGroupThreadCount;
	@Value("${mqtt.netty.max_payload_size}")
	private Integer maxPayloadSize;

	@Resource
	private ProtocolProcess protocolProcess;

	private Channel serverChannel;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	@PostConstruct
	public void init() throws Exception {

		log.info("Setting resource leak detector level to {}", leakDetectorLevel);
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.valueOf(leakDetectorLevel.toUpperCase()));
		log.info("Starting MQTT transport...");

		log.info("Starting MQTT transport server");
		bossGroup = new NioEventLoopGroup(bossGroupThreadCount);
		workerGroup = new NioEventLoopGroup(workerGroupThreadCount);
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel socketChannel) throws Exception {
						ChannelPipeline pipeline = socketChannel.pipeline();
						pipeline.addLast("idle", new MqttIdleStateHandler());
						pipeline.addLast("decoder", new MqttDecoder(maxPayloadSize));
						pipeline.addLast("encoder", MqttEncoder.INSTANCE);
						MqttTransportHandler handler = new MqttTransportHandler(protocolProcess);
						pipeline.addLast(handler);
					}
				});

		serverChannel = b.bind(host, port).sync().channel();
		log.info("Mqtt transport started!");
	}

	@PreDestroy
	public void shutdown() throws InterruptedException {
		log.info("Stopping MQTT transport!");
		try {
			serverChannel.close().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
		log.info("MQTT transport stopped!");
	}
}
