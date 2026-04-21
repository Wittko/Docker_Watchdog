package com.justdoit.watchdog.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DockerConfig.class);

    @Bean
    public DockerClient dockerClient() {
        String host = "unix:///var/run/docker.sock";
        log.info("!!!!!!!! ERZWINGE DOCKER HOST: {} !!!!!!!!", host);

        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(host)
                .build();

        com.github.dockerjava.zerodep.ZerodepDockerHttpClient httpClient =
                new com.github.dockerjava.zerodep.ZerodepDockerHttpClient.Builder()
                        .dockerHost(java.net.URI.create(host))
                        .build();

        return DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();
    }
}