package br.com.basis.langchain.dbmetadata;

import br.com.basis.langchain.dbmetadata.config.ApplicationProperties;
import br.com.basis.langchain.dbmetadata.config.LocalAiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties({LocalAiProperties.class, ApplicationProperties.class})
@EnableCaching
public class DbmetadataApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbmetadataApplication.class, args);
    }

}
