package iot.technology.mqtt.storage.session.cache;

/**
 * @author mushuwei
 */
public interface CacheManager {

	Boolean delete(String key);


	Boolean put(String key, Object value, Integer expireTime);


	Object get(String key);


	Boolean existsKey(String key);


	Object getMap(String key, String mapKey);

	Boolean putMap(String key, String mapKey, Object mapValue);
}
