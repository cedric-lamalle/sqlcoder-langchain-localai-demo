package br.com.basis.langchain.dbmetadata.config;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Collections;

public class CacheCustomizer implements CacheManagerCustomizer<ConcurrentMapCacheManager> {
    // ConcurrentMapCacheManager is enough for a demo! The goal is to not hit the db for metadata for every question.
    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        cacheManager.setCacheNames(Collections.singletonList("salesDdl"));
    }
}
