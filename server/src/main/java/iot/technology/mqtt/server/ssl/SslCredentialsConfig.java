package iot.technology.mqtt.server.ssl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class SslCredentialsConfig {

    private boolean enabled = true;

    private SslCredentialsType type;


}
