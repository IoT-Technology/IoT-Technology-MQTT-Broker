package iot.technology.mqtt.storage.session.service;

import iot.technology.mqtt.storage.session.domain.SessionStore;

/**
 * 会话存储接口类
 *
 * @author mushuwei
 */
public interface SessionStoreService {

	/**
	 * 存储会话
	 *
	 * @param clientId     设备编号
	 * @param sessionStore 会话实体
	 */
	void put(String clientId, SessionStore sessionStore);

	/**
	 * 获取会话
	 *
	 * @param clientId 设备编号
	 * @return 会话实体
	 */
	SessionStore get(String clientId);

	/**
	 * clientId对应的会话是否存在
	 *
	 * @param clientId 设备编号
	 * @return true/false
	 */
	boolean containsKey(String clientId);

	/**
	 * 删除会话
	 *
	 * @param clientId 设备编号
	 */
	void remove(String clientId);
}
