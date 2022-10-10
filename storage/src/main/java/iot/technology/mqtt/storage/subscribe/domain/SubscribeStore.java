package iot.technology.mqtt.storage.subscribe.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author mushuwei
 */
@Getter
@Setter
@Accessors(chain = true)
public class SubscribeStore implements Serializable {

	private static final long serialVersionUID = 1276156087085594264L;

	private String clientId;

	private String topicName;

	private int mqttQoS;
}
