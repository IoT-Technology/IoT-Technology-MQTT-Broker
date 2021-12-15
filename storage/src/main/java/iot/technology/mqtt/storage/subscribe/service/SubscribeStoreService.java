package iot.technology.mqtt.storage.subscribe.service;

import iot.technology.mqtt.storage.subscribe.domain.SubscribeStore;

import java.util.Map;

/**
 * @author mushuwei
 * @description 订阅存储服务接口
 */
public interface SubscribeStoreService {

	/**
	 * 存储订阅
	 *
	 * @param topicName      主题名
	 * @param subscribeStore 订阅元数据
	 */
	void put(String topicName, SubscribeStore subscribeStore);
	

	/**
	 * 删除某主题下对应的客户端订阅
	 *
	 * @param topicName 主题名
	 * @param clientId  客户端编号
	 */
	void remove(String topicName, String clientId);

	/**
	 * 删除某客户端编号对应的所有topic订阅
	 *
	 * @param clientId
	 */
	void removeTopicByClientId(String clientId);

	/**
	 * 获取某topic下的订阅集合
	 *
	 * @param topicName 主题名
	 * @return 订阅集合
	 */
	Map<String, SubscribeStore> search(String topicName);
}
