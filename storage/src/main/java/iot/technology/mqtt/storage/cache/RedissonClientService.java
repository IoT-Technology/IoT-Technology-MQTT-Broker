package iot.technology.mqtt.storage.cache;

import org.redisson.api.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author mushuwei
 */
@Service("redissonClientService")
public class RedissonClientService implements CacheManager {

	@Resource
	private RedissonClient redissonClient;

	@Override
	public Boolean deleteStringCache(String key) {
		RKeys keys = redissonClient.getKeys();
		keys.delete(key);
		return Boolean.TRUE;
	}


	@Override
	public Boolean putStringCache(String key, Object value, Integer expireTime) {
		RBucket<Object> bucket = redissonClient.getBucket(key);
		int cacheTime = (Objects.nonNull(expireTime) && expireTime > 0) ? expireTime : 300;
		bucket.set(value, cacheTime, TimeUnit.SECONDS);
		return Boolean.TRUE;
	}

	@Override
	public Object getStringCache(String key) {
		RBucket<Object> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}

	@Override
	public Boolean existsStringCache(String key) {
		RKeys keys = redissonClient.getKeys();
		Boolean result = keys.countExists(key) > 0 ? Boolean.TRUE : Boolean.FALSE;
		return result;
	}


	@Override
	public Object getHashCache(String key, String mapKey) {
		RMap<String, Object> cache = redissonClient.getMap(key);
		return cache.get(mapKey);
	}

	@Override
	public Boolean putHashCache(String key, String mapKey, Object mapValue) {
		RMap<String, Object> cache = redissonClient.getMap(key);
		cache.putIfAbsent(mapKey, mapValue);
		return Boolean.TRUE;
	}

	@Override
	public Boolean putAllHashCache(String key, Map<String, Object> maps) {
		RMap<String, Object> cache = redissonClient.getMap(key);
		cache.putAll(maps);
		return Boolean.TRUE;
	}

	@Override
	public Map<String, Object> getAllHashCache(String key) {
		RMap<String, Object> cache = redissonClient.getMap(key);
		Map<String, Object> hashMap = cache.readAllMap();
		return hashMap;
	}

	@Override
	public Boolean containHashKey(String key, String mapKey) {
		RMap<String, Object> cache = redissonClient.getMap(key);
		return cache.containsKey(mapKey);
	}

	@Override
	public Boolean removeHashKey(String key, String mapKey) {
		RMap<String, Object> cache = redissonClient.getMap(key);
		cache.remove(mapKey);
		return Boolean.TRUE;
	}

	@Override
	public Boolean addSetCache(String key, Object value) {
		RSet<Object> cache = redissonClient.getSet(key);
		cache.add(value);
		return Boolean.TRUE;
	}

	@Override
	public Boolean addAllSetCache(String key, List<Object> values) {
		RSet<Object> cache = redissonClient.getSet(key);
		return cache.addAll(values);
	}

	@Override
	public Boolean existsSetCache(String key, Object value) {
		RSet<Object> cache = redissonClient.getSet(key);
		return cache.contains(value);
	}

	@Override
	public Boolean removeSetCache(String key, Object value) {
		RSet<Object> cache = redissonClient.getSet(key);
		return cache.remove(value);
	}

	@Override
	public Set<Object> getAllSetCache(String key) {
		RSet<Object> cache = redissonClient.getSet(key);
		return cache.readAll();
	}
}
