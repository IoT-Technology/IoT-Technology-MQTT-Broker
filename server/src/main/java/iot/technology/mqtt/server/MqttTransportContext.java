package iot.technology.mqtt.server;

import io.netty.handler.ssl.SslHandler;
import iot.technology.mqtt.server.protocol.ProtocolProcess;
import iot.technology.mqtt.server.ssl.MqttSslHandlerProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionalOnExpression("'${mqtt.enabled}'=='true'")
@Component
public class MqttTransportContext {

    @Getter
    @Autowired(required = false)
    private MqttSslHandlerProvider sslHandlerProvider;

    @Getter
    @Value("${mqtt.netty.max_payload_size}")
    private Integer maxPayloadSize;

    @Getter
    @Setter
    private SslHandler sslHandler;

    @Getter
    @Value("${transport.mqtt.timeout:10000}")
    private long timeout;

    @Getter
    @Autowired(required = false)
    private ProtocolProcess protocolProcess;
}
