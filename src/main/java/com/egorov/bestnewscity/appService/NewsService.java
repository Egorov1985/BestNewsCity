package com.egorov.bestnewscity.appService;

import com.egorov.bestnewscity.exception.NotFoundNewsException;
import com.egorov.bestnewscity.model.dto.MapperNews;
import com.egorov.bestnewscity.model.dto.NewsCreateModel;
import com.egorov.bestnewscity.model.dto.NewsDto;
import com.egorov.bestnewscity.model.dto.NewsUpdateModel;
import com.egorov.bestnewscity.model.entity.CategoryNews;
import com.egorov.bestnewscity.model.entity.News;
import com.egorov.bestnewscity.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;


@RequiredArgsConstructor
@Service
@Slf4j
public class NewsService implements INewsService {

    private final NewsRepository newsRepository;
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange directExchange;

    @Override
    public Mono<NewsDto> createdNews(NewsDto newsDto) {
        newsDto.setCreateDateAtNews(LocalDate.now());
        newsDto.setCreateTimeAtNews(LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
        newsDto.setUpdateDateAtNews("Not updated!!!");

       return newsRepository.save(MapperNews.INSTANCE.toNews(newsDto)).doOnNext(this::sendNewsAlert)
               .doOnNext(news -> newsDto.setId(news.getId()))
               .thenReturn(newsDto);
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
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(news ->
                    update(news, newsUpdateModel).subscribe()
                )
                .flatMap(newsRepository::save)
                .map(MapperNews.INSTANCE::toNewsDto);
    }

    @Override
    public Mono<Void> deleteNews(String id) {
        return newsRepository.deleteById(id).doOnSuccess(ResponseEntity::ok);
    }

    @Override
    public Flux<NewsDto> findNewsByAuthorOrAndCategoryOrAndCreate(String author, List<String> category, String create) {
        if (!author.isEmpty() || !category.isEmpty() || !create.isEmpty()) {
            LocalDate searchDate = create.isEmpty() ? LocalDate.now() : LocalDate.parse(create);
            return newsRepository.findNewsByAuthorOrAndCategoryContainsIgnoreCaseOrAndCreateDateAtNews(
                    author, category, searchDate).map(MapperNews.INSTANCE::toNewsDto);
        }
        return newsRepository.findAll().map(MapperNews.INSTANCE::toNewsDto);
    }

    private Mono<Void> update(News news, NewsUpdateModel newsUpdateModel) {
        return Mono.fromSupplier(() -> {
            news.setTitle(news.getTitle());
            news.setMessage(newsUpdateModel.getMessage());
            news.setCategory(newsUpdateModel.getCategory());
            news.setUpdateDateAtNews("Last updated "  + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm")));
            return news;
        }).then();
    }
    private void sendNewsAlert(News news) {
        for (CategoryNews category : CategoryNews.values()) {
            if (news.getCategory().contains(category)) {
                rabbitTemplate.convertAndSend(directExchange.getName(),
                        category.toString(), "New news in the category " + category + "! " +
                                "To read it, follow the link " + "http://localhost:8080/api/news/" + news.getId());
            }
        }
    }


}
