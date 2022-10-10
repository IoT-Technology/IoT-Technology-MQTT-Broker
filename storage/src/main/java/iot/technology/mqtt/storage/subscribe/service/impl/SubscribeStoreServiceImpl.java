package iot.technology.mqtt.storage.subscribe.service.impl;

import iot.technology.mqtt.storage.manager.SubscribeCacheManager;
import iot.technology.mqtt.storage.subscribe.domain.SubscribeStore;
import iot.technology.mqtt.storage.subscribe.service.SubscribeStoreService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author mushuwei
 */
@Service("subscribeStoreService")
public class SubscribeStoreServiceImpl implements SubscribeStoreService {

	@Resource
	private SubscribeCacheManager cacheManager;

	@Override
	public void put(String topicName, SubscribeStore subscribeStore) {
		cacheManager.put(topicName, subscribeStore.getClientId(), subscribeStore);
	}

	@Override
	public void remove(String topicName, String clientId) {
		cacheManager.removeClientOfTopic(topicName, clientId);
	}

	@Override
	public void removeTopicByClientId(String clientId) {
		cacheManager.removeClient(clientId);
	}

	@Override
	public Map<String, SubscribeStore> search(String topicName) {
		return cacheManager.getSubscribeStoreMapByTopic(topicName);
	}
}
