package com.egorov.bestnewscity.controller;


import com.egorov.bestnewscity.appService.NewsService;
import com.egorov.bestnewscity.model.CategoryNews;
import com.egorov.bestnewscity.model.dto.NewsCreateModel;
import com.egorov.bestnewscity.model.dto.NewsDto;
import com.egorov.bestnewscity.model.dto.NewsUpdateModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public Flux<NewsDto> allNews(@RequestParam (required = false, defaultValue = "") String author,
                                 @RequestParam (required = false, defaultValue = "") List<String> category,
                                 @RequestParam (required = false, defaultValue = "") String create) {
        return newsService.findByTitle(author, category, create);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<NewsDto>> getOneNews(@PathVariable String id) {
        return newsService.findById(id).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PostMapping("/create")
    public Mono<ResponseEntity<NewsDto>> creatNews(@RequestBody @Valid NewsCreateModel newsCreateModel) {
        return newsService.createdNews(newsCreateModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<NewsDto>> updateNews(@PathVariable String id, @RequestBody @Valid NewsUpdateModel newsUpdateModel) {
        return newsService.updateNews(id, newsUpdateModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Void>> deleteNews(@PathVariable String id) {
        return newsService.findById(id)
                .flatMap(n -> newsService.deleteNews(n.getId()).then(
                        Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT))));
    }

}
