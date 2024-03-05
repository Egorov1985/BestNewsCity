package com.egorov.bestnewscity.repository;

import com.egorov.bestnewscity.model.News;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

public interface NewsRepository extends ReactiveMongoRepository<News, String> {
    Flux<News> findNewsByAuthorOrAndCategoryContainsIgnoreCaseOrAndCreateDateAtNews(String author, List<String> category, LocalDate create);

}
