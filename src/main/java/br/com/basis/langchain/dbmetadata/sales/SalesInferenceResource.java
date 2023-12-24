package br.com.basis.langchain.dbmetadata.sales;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SalesInferenceResource {

    private final SalesServices salesServices;

    public SalesInferenceResource(SalesServices salesServices) {
        this.salesServices = salesServices;
    }

    @PostMapping("/sql-generation")
    public List<Map<String, Object>> getSqlInference(@RequestBody Completion completion) {
        return salesServices.queryByQuestion(completion.question());
    }
}
