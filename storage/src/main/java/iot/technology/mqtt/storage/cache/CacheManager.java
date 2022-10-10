package iot.technology.mqtt.storage.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author mushuwei
 */
public interface CacheManager {

	//******string字符串操作 start

	/**
	 * 删除string字符串
	 *
	 * @param key 键
	 * @return true/false
	 */
	Boolean deleteStringCache(String key);

	/**
	 * 增加string/object类型 键值对
	 *
	 * @param key        键
	 * @param value      值
	 * @param expireTime 失效时间
	 * @return
	 */
	Boolean putStringCache(String key, Object value, Integer expireTime);

	/**
	 * 通过键获取对象值
	 *
	 * @param key 键
	 * @return 对象值
	 */
	Object getStringCache(String key);

	/**
	 * 检查是否存在某个键的键值对
	 *
	 * @param key 键
	 * @return true/false
	 */
	Boolean existsStringCache(String key);

	//******string字符串操作 end


	//******hash(字典) 操作 start

	/**
	 * 获取字典值
	 *
	 * @param key    键
	 * @param mapKey 字典键
	 * @return
	 */
	Object getHashCache(String key, String mapKey);

	/**
	 * 增加字典属性
	 *
	 * @param key      键
	 * @param mapKey   字典键
	 * @param mapValue 字典值
	 * @return
	 */
	Boolean putHashCache(String key, String mapKey, Object mapValue);

	/**
	 * 批量增加字典
	 *
	 * @param key  键
	 * @param maps 字典键值集合
	 * @return true/false
	 */
	Boolean putAllHashCache(String key, Map<String, Object> maps);

	/**
	 * 通过键批量获取字典键值集合
	 *
	 * @param key 键
	 * @return 字典键值集合
	 */
	Map<String, Object> getAllHashCache(String key);

	/**
	 * 检查某字典是否包含该字典键
	 *
	 * @param key    键
	 * @param mapKey 字典键
	 * @return
	 */
	Boolean containHashKey(String key, String mapKey);


	/**
	 * 删除某字典某字典键
	 *
	 * @param key    键
	 * @param mapKey 字典键
	 * @return
	 */
	Boolean removeHashKey(String key, String mapKey);
	//******hash(字典) 操作 end


	//******list(列表) 操作 start

	/**
	 * 增加set缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	Boolean addSetCache(String key, Object value);

	/**
	 * 批量增加set缓存
	 *
	 * @param key    键
	 * @param values set列表
	 * @return true/false
	 */
	Boolean addAllSetCache(String key, List<Object> values);

	/**
	 * 检查set缓存是否存在
	 *
	 * @param key   键
	 * @param value 值
	 * @return true/false
	 */
	Boolean existsSetCache(String key, Object value);


	Boolean removeSetCache(String key, Object value);

	/**
	 * 批量获取set列表
	 *
	 * @param key 键
	 * @return 值集合
	 */
	Set<Object> getAllSetCache(String key);
	//******list(列表) 操作 end


}
