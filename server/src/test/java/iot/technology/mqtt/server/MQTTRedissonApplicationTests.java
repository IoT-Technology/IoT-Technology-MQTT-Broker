package iot.technology.mqtt.server;

import iot.technology.mqtt.server.domain.Book;
import iot.technology.mqtt.storage.session.cache.RedissonClientService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author mushuwei
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MQTTRedissonApplicationTests {

	@Resource
	private RedissonClientService clientService;

	@Test
	public void redisStringOperations() {
		Book book = new Book().setName("Harry Potter").setAuthor("J.K.Rowling").setPrice(120.00);
		clientService.put("harry-potter", book, null);
		Book bookCache = (Book) clientService.get("harry-potter");
		log.info("book:{}", bookCache);

	}

}
