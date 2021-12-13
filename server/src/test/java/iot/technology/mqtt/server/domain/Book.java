package iot.technology.mqtt.server.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author mushuwei
 */
@Data
@Accessors(chain = true)
public class Book implements Serializable {
	/**
	 * 书名
	 */
	private String name;

	/**
	 * 作者
	 */
	private String author;

	/**
	 * 售价
	 */
	private Double price;
}
