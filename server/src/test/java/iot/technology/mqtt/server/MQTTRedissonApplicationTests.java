package iot.technology.mqtt.server;

import iot.technology.mqtt.server.domain.Book;
import iot.technology.mqtt.storage.cache.CacheManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author mushuwei
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MQTTRedissonApplicationTests {

	@Resource
	private CacheManager cacheManager;

	@Test
	public void redisStringOperations() {
		Book book = new Book().setName("Harry Potter").setAuthor("J.K.Rowling").setPrice(120.00);
		cacheManager.putStringCache("harry-potter", book, null);
		Book bookCache = (Book) cacheManager.getStringCache("harry-potter");
		log.info("book:{}", bookCache);
	}

	@Test
	public void redisHashOperations() {
		cacheManager.putHashCache("books", "java", "think in java");
		Map<String, Object> maps = new HashMap<>();
		maps.put("golang", "concurrency in go");
		maps.put("python", "python cookbook");
		cacheManager.putAllHashCache("books", maps);
		String javaBookName = (String) cacheManager.getHashCache("books", "java");
		Map<String, Object> cacheMap = cacheManager.getAllHashCache("books");
		log.info("java bookName:{}", javaBookName);
		log.info("books cacheMap:{}", cacheMap);
	}

	@Test
	public void redisSetOperations() {
		cacheManager.addSetCache("book", "python");
		cacheManager.addAllSetCache("book", Arrays.asList("java", "golang"));
		Set<Object> setCache = cacheManager.getAllSetCache("book");
		Boolean javaExist = cacheManager.existsSetCache("book", "java");
		Boolean rustExist = cacheManager.existsSetCache("book", "rust");
		log.info("book:{}", setCache);
		log.info("java is exist: {}", javaExist);
		log.info("rust is exist: {}", rustExist);
	}

}
