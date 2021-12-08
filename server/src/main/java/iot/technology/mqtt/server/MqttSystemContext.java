package iot.technology.mqtt.server;

import iot.technology.mqtt.storage.session.service.SessionStoreService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author mushuwei
 */
@Slf4j
@Component
public class MqttSystemContext {

	@Getter
	@Resource
	private SessionStoreService sessionStoreService;
}
