package br.com.basis.langchain.dbmetadata.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application")
public class ApplicationProperties {
    private String promptTemplate;

    public String getPromptTemplate() {
        return promptTemplate;
    }

    public void setPromptTemplate(String promptTemplate) {
        this.promptTemplate = promptTemplate;
    }
}
