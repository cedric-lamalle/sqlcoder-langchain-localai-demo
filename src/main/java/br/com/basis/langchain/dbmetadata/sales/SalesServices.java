package br.com.basis.langchain.dbmetadata.sales;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SalesServices {
    private final LocalAiService localAiService;
    private final JdbcTemplate jdbcTemplate;

    public SalesServices(LocalAiService localAiService, JdbcTemplate jdbcTemplate) {
        this.localAiService = localAiService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> queryByQuestion(String question) {
        return jdbcTemplate.queryForList(localAiService.generateQuery(question));
    }
}
