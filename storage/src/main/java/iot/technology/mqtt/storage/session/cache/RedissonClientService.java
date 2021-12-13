package iot.technology.mqtt.storage.session.cache;

import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author mushuwei
 */
@Service("redissonClientService")
public class RedissonClientService implements CacheManager {

	@Resource
	private RedissonClient redissonClient;

	@Override
	public Boolean delete(String key) {
		RKeys keys = redissonClient.getKeys();
		keys.delete(key);
		return Boolean.TRUE;
	}


	@Override
	public Boolean put(String key, Object value, Integer expireTime) {
		RBucket<Object> bucket = redissonClient.getBucket(key);
		int cacheTime = (Objects.nonNull(expireTime) && expireTime > 0) ? expireTime : 300;
		bucket.set(value, cacheTime, TimeUnit.SECONDS);
		return Boolean.TRUE;
	}

	@Override
	public Object get(String key) {
		RBucket<Object> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}

	@Override
	public Boolean existsKey(String key) {
		RKeys keys = redissonClient.getKeys();
		Boolean result = keys.countExists(key) > 0 ? Boolean.TRUE : Boolean.FALSE;
		return result;
	}


	@Override
	public Object getMap(String key, String mapKey) {
		RMap<String, Object> map = redissonClient.getMap(key);
		return map.get(mapKey);
	}

	@Override
	public Boolean putMap(String key, String mapKey, Object mapValue) {
		RMap<String, Object> map = redissonClient.getMap(key);
		map.put(mapKey, mapValue);
		return Boolean.TRUE;
	}
}
