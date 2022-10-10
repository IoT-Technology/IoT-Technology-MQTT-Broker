package iot.technology.mqtt.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * @author mushuwei
 */
@Component
@ConditionalOnExpression("'${mqtt.enabled}'=='true'")
@Slf4j
public class MqttServer {
	@Value("${mqtt.bind_address}")
	private String host;
	@Value("${mqtt.bind_port}")
	private Integer port;

	@Value("${mqtt.ssl.enabled}")
	private boolean sslEnabled;

	@Value("${mqtt.ssl.bind_address}")
	private String sslHost;

	@Value("${mqtt.ssl.bind_port}")
	private Integer sslPort;

	@Value("${mqtt.netty.leak_detector_level}")
	private String leakDetectorLevel;
	@Value("${mqtt.netty.boss_group_thread_count}")
	private Integer bossGroupThreadCount;
	@Value("${mqtt.netty.worker_group_thread_count}")
	private Integer workerGroupThreadCount;
	@Value("${mqtt.netty.max_payload_size}")
	private Integer maxPayloadSize;

	@Value("${mqtt.netty.so_keep_alive}")
	private boolean keepAlive;

	@Resource
	private MqttTransportContext context;

	private Channel serverChannel;

	private Channel sslServerChannel;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	@PostConstruct
	public void init() throws Exception {

		log.info("Setting resource leak detector level to {}", leakDetectorLevel);
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.valueOf(leakDetectorLevel.toUpperCase()));
		log.info("Starting MQTT transport...");

		bossGroup = new NioEventLoopGroup(bossGroupThreadCount);
		workerGroup = new NioEventLoopGroup(workerGroupThreadCount);
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new MqttTransportServerInitializer(context, false))
				.childOption(ChannelOption.SO_KEEPALIVE, keepAlive);

		serverChannel = b.bind(host, port).sync().channel();
		log.info("Mqtt started on {}", port);
		if (sslEnabled) {
			b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new MqttTransportServerInitializer(context, true))
					.childOption(ChannelOption.SO_KEEPALIVE, keepAlive);
			sslServerChannel = b.bind(sslHost, sslPort).sync().channel();
			log.info("Mqtt TLS/SSL started on {}", sslPort);
		}
		log.info("Mqtt transport started!");
	}

	@PreDestroy
	public void shutdown() throws InterruptedException {
		log.info("Stopping MQTT transport!");
		try {
			serverChannel.close().sync();
			if (sslEnabled) {
				sslServerChannel.close().sync();
			}
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
		log.info("MQTT transport stopped!");
	}
}
