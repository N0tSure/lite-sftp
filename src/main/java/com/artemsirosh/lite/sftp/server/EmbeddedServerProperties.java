package com.artemsirosh.lite.sftp.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("service.embedded.sftp")
public class EmbeddedServerProperties {

    private static final int DEFAULT_PORT = 22;

    private int port = DEFAULT_PORT;
    private String serverKey;
    private String username;
    private String userKey;
    private String catalog;
}
