package com.egorov.bestnewscity.appService;

import com.egorov.bestnewscity.exception.NotFoundNewsException;
import com.egorov.bestnewscity.model.CategoryNews;
import com.egorov.bestnewscity.model.dto.*;
import com.egorov.bestnewscity.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;


@RequiredArgsConstructor
@Service
public class NewsService implements INewsService {

    private final NewsRepository newsRepository;
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange directExchange;

    @Override
    public Mono<NewsDto> createdNews(NewsCreateModel newsCreateModel) {
        newsCreateModel.setCreateTimeAtNews(LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
        newsCreateModel.setCreateDateAtNews(LocalDate.now());
        for (CategoryNews category : CategoryNews.values()) {
            if (newsCreateModel.getCategory().contains(category)) {
                rabbitTemplate.convertAndSend(directExchange.getName(),
                        category.toString(), newsCreateModel);
            }
        }
        return newsRepository.save(MapperNews.INSTANCE.toNews(newsCreateModel)).map(MapperNews.INSTANCE::toNewsDto);
    }

    @Override
    public Mono<NewsDto> findById(String id) {
        return newsRepository.findById(id).filter(Objects::nonNull).map(MapperNews.INSTANCE::toNewsDto)
                .switchIfEmpty(Mono.error(new NotFoundNewsException("Not found News!!!")));
    }

    @Override
    public Mono<NewsDto> updateNews(String id, NewsUpdateModel newsUpdateModel) {
        return newsRepository.findById(id)
                .filter(Objects::nonNull)
                .doOnNext( el -> MapperNews.INSTANCE.updateNews(el, newsUpdateModel))
                .flatMap(newsRepository::save)
                .map(MapperNews.INSTANCE::toNewsDto);
    }

    @Override
    public Mono<Void> deleteNews(String id) {
        return newsRepository.deleteById(id).doOnSuccess(ResponseEntity::ok);
    }

    @Override
    public Flux<NewsDto> findByTitle(String author, List<String> category, String create) {
        if (!author.isEmpty() || !category.isEmpty() || !create.isEmpty()) {
            LocalDate searchDate = create.isEmpty()?LocalDate.now(): LocalDate.parse(create);
            return newsRepository.findNewsByAuthorOrAndCategoryContainsIgnoreCaseOrAndCreateDateAtNews(
                    author, category, searchDate).map(MapperNews.INSTANCE::toNewsDto);
        }
        return newsRepository.findAll().map(MapperNews.INSTANCE::toNewsDto);
    }
}
