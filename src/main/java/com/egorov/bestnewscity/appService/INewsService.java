package com.egorov.bestnewscity.appService;


import com.egorov.bestnewscity.model.dto.NewsDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


public interface INewsService {

    Mono<NewsDto> createdNews(NewsDto newsDto);

    Mono<NewsDto> findById(String id);

    Mono<NewsDto> updateNews(String id, NewsDto newsDto);

    Mono<Void> deleteNews(String id);


    Flux<NewsDto> findByTitle(String title, List<String> category, String create);



}