package ssi.backend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "host")
@Getter
@Setter
public class HostProperties{
    private String url;
}