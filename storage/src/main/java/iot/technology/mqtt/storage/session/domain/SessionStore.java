package iot.technology.mqtt.storage.session.domain;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 会话存储
 *
 * @author mushuwei
 */
@Data
@AllArgsConstructor
public class SessionStore implements Serializable {
	private static final long serialVersionUID = 5209539791996944490L;

	private String clientId;

	private Channel channel;

	private boolean cleanSession;
}
