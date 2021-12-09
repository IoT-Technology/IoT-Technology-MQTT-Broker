package iot.technology.mqtt.server.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import iot.technology.mqtt.server.MqttSystemContext;
import iot.technology.mqtt.storage.session.domain.SessionStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author mushuwei
 */
@Slf4j
@Service("connectProcessor")
public class ConnectProcessor implements AbstractProtocolProcessor {

	@Resource
	private MqttSystemContext systemContext;

	@Override
	public void processMqttProtocol(Channel channel, MqttMessage msg) {
		if (msg.decoderResult().isFailure()) {
			Throwable cause = msg.decoderResult().cause();
			if (cause instanceof MqttUnacceptableProtocolVersionException) {
				// 不支持的协议版本
				MqttConnAckMessage connAckMessage =
						createMqttConnAckMessage(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION);
				channel.writeAndFlush(connAckMessage);
				channel.close();
				return;
			} else if (cause instanceof MqttIdentifierRejectedException) {
				// 不合格的clientId
				MqttConnAckMessage connAckMessage =
						createMqttConnAckMessage(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED);
				channel.writeAndFlush(connAckMessage);
				channel.close();
				return;
			}
			channel.close();
			return;
		}
		MqttConnectPayload mqttConnectPayload = (MqttConnectPayload) msg.payload();
		MqttConnectVariableHeader connectVariableHeader = (MqttConnectVariableHeader) msg.variableHeader();
		String clientId = mqttConnectPayload.clientIdentifier();
		//将clientId 存储到channel的map中
		channel.attr(AttributeKey.valueOf("clientId")).set(clientId);

		// 如果会话中已存储这个新连接的clientId, 就关闭之前该clientId的连接
		if (systemContext.getSessionStoreService().containsKey(clientId)) {
			SessionStore sessionStore = systemContext.getSessionStoreService().get(clientId);
			Channel previous = sessionStore.getChannel();
			previous.close();
		}

		SessionStore sessionStore = new SessionStore().setClientId(clientId).setChannel(channel);
		systemContext.getSessionStoreService().put(clientId, sessionStore);

		MqttConnAckMessage okResp =
				createMqttConnAckMessage(MqttConnectReturnCode.CONNECTION_ACCEPTED);
		channel.writeAndFlush(okResp);
		log.info("connect clientId: {}, channelId:{}", clientId, channel.id());
	}

	private MqttConnAckMessage createMqttConnAckMessage(MqttConnectReturnCode returnCode) {
		MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
				new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
				new MqttConnAckVariableHeader(returnCode, false), null);
		return connAckMessage;
	}
}
