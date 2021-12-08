package iot.technology.mqtt.storage.session.service.impl;

import iot.technology.mqtt.storage.session.domain.SessionStore;
import iot.technology.mqtt.storage.session.service.SessionStoreService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话存储接口实现类
 *
 * @author mushuwei
 */
@Service("sessionStoreService")
public class SessionStoreServiceImpl implements SessionStoreService {

	private Map<String, SessionStore> sessionStoreMap = new ConcurrentHashMap<>();

	@Override
	public void put(String clientId, SessionStore sessionStore) {
		sessionStoreMap.put(clientId, sessionStore);
	}

	@Override
	public SessionStore get(String clientId) {
		return sessionStoreMap.get(clientId);
	}

	@Override
	public boolean containsKey(String clientId) {
		return sessionStoreMap.containsKey(clientId);
	}

	@Override
	public void remove(String clientId) {
		sessionStoreMap.remove(clientId);
	}
}
