package iot.technology.mqtt.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author mushuwei
 */
@SpringBootApplication(scanBasePackages = {"iot.technology.mqtt"})
public class MQTTServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MQTTServerApplication.class, args);
	}
}
