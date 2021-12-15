package iot.technology.mqtt.storage.manager;

import iot.technology.mqtt.storage.cache.CacheManager;
import iot.technology.mqtt.storage.subscribe.domain.SubscribeStore;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static iot.technology.mqtt.storage.cache.CacheConst.CLIENT_PRE;
import static iot.technology.mqtt.storage.cache.CacheConst.SUBSRIBE_PRE;

/**
 * @author mushuwei
 */
@Service("subscribeManager")
public class SubscribeCacheManager {

	@Resource(name = "redissonClientService")
	private CacheManager cacheManager;

	public SubscribeStore put(String topic, String clientId, SubscribeStore subscribeStore) {
		cacheManager.putHashCache(SUBSRIBE_PRE + topic, clientId, subscribeStore);
		cacheManager.addSetCache(CLIENT_PRE + clientId, topic);
		return subscribeStore;
	}

	public SubscribeStore get(String topic, String clientId) {
		return (SubscribeStore) cacheManager.getHashCache(SUBSRIBE_PRE + topic, clientId);
	}

	public Boolean topicContainClient(String topic, String clientId) {
		return cacheManager.containHashKey(SUBSRIBE_PRE + topic, clientId);
	}

	public Boolean removeClientOfTopic(String topic, String clientId) {
		cacheManager.removeSetCache(CLIENT_PRE + clientId, topic);
		return cacheManager.removeHashKey(SUBSRIBE_PRE + topic, clientId);
	}

	public Boolean removeClient(String clientId) {
		Set<Object> topicLists = cacheManager.getAllSetCache(CLIENT_PRE + clientId);
		topicLists.forEach(topic -> {
			cacheManager.removeHashKey(SUBSRIBE_PRE + topic, CLIENT_PRE + clientId);
		});
		cacheManager.deleteStringCache(clientId);
		return Boolean.TRUE;
	}

	public Map<String, SubscribeStore> getSubscribeStoreMapByTopic(String topic) {
		Map<String, SubscribeStore> resultMap = new HashMap<>();
		Map<String, Object> cacheMap = cacheManager.getAllHashCache(topic);
		for (Map.Entry<String, Object> entry : cacheMap.entrySet()) {
			resultMap.put(entry.getKey(), (SubscribeStore) entry.getValue());
		}
		return resultMap;
	}

}
