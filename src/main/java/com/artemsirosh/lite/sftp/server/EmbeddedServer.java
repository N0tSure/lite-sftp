package com.artemsirosh.lite.sftp.server;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.UserAuthPublicKeyFactory;
import org.apache.sshd.server.config.keys.DefaultAuthorizedKeysAuthenticator;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddedServer implements SmartLifecycle {

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final EmbeddedServerProperties properties;

    private SshServer sshServer;

    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            this.sshServer = SshServer.setUpDefaultServer();
            this.sshServer.setPort(properties.getPort());
            log.info("SFTP server will listen port {}", properties.getPort());

            this.sshServer.setUserAuthFactories(List.of(UserAuthPublicKeyFactory.INSTANCE));
            this.sshServer.setSubsystemFactories(List.of(new SftpSubsystemFactory()));
            this.sshServer.setKeyPairProvider(new FileKeyPairProvider(Path.of(properties.getServerKey())));

            final var userAuthenticator = new DefaultAuthorizedKeysAuthenticator(
                    properties.getUsername(), Path.of(properties.getUserKey()), false
            );
            log.info("SFTP server will configured for '{}' user", properties.getUsername());

            this.sshServer.setPublickeyAuthenticator(userAuthenticator);
            this.sshServer.setFileSystemFactory(new VirtualFileSystemFactory(Path.of(properties.getCatalog())));
            log.info("Server's working catalog: {}", properties.getCatalog());

            try {
                log.info("Launching SFTP server...");
                this.sshServer.start();
                log.info("SFTP server was launched");
            } catch (IOException exc) {
                log.error("Launching of the SFTP server was failed", exc);
                throw new UncheckedIOException(exc);
            }
        }
    }

    @Override
    public void stop() {
        if (this.isRunning.compareAndSet(true, false)) {
            try {
                log.info("Stopping SFTP server...");
                this.sshServer.stop();
                log.info("SFTP server was stopped");
            } catch (IOException exc) {
                log.error("Stopping of the SFTP server was failed");
                throw new UncheckedIOException(exc);
            }
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @PostConstruct
    void init() {
        Assert.notNull(properties.getServerKey(), "Server key file not provided");
        Assert.notNull(properties.getUsername(), "Username file not provided");
        Assert.notNull(properties.getUserKey(), "User key file not provided");
        Assert.notNull(properties.getCatalog(), "Working directory not provided");
        Assert.state(Files.exists(Path.of(properties.getServerKey())), "Server key file not found");
        Assert.state(Files.exists(Path.of(properties.getUserKey())), "User key file not found");
        Assert.state(Files.exists(Path.of(properties.getCatalog())), "Working directory not found");
    }

    @PreDestroy
    void destroy() {
        this.stop();
    }
}
