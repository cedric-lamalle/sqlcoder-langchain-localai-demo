package br.com.basis.langchain.dbmetadata.sales;

import br.com.basis.langchain.dbmetadata.config.ApplicationProperties;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LocalAiService {
    private static final Logger logger = LoggerFactory.getLogger(LocalAiService.class);
    private final ChatLanguageModel chatLanguageModel;
    private final DdlExtractor ddlExtractor;

    private final ApplicationProperties properties;

    public LocalAiService(ChatLanguageModel chatLanguageModel, DdlExtractor ddlExtractor, ApplicationProperties properties) {
        this.chatLanguageModel = chatLanguageModel;
        this.ddlExtractor = ddlExtractor;
        this.properties = properties;
    }

    public String generateQuery(String question) {
        String prompt = String.format(properties.getPromptTemplate(),
                question,
                ddlExtractor.getSalesDdl(),
                question);
        logger.debug("Prompt:\n{}", prompt);
        return cleanGeneratedSql(chatLanguageModel.generate(prompt));
    }

    protected String cleanGeneratedSql(String sql) {
        String cleanSql = sql.replace("```", "").trim();
        int lastPos = cleanSql.length() - 1;
        if(cleanSql.lastIndexOf(';') == lastPos) {
            cleanSql = cleanSql.substring(0, lastPos);
        }
        return cleanSql;
    }

}
