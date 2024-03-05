package com.egorov.bestnewscity.appService;

import com.egorov.bestnewscity.exception.NotFoundNewsException;
import com.egorov.bestnewscity.model.CategoryNews;
import com.egorov.bestnewscity.model.News;
import com.egorov.bestnewscity.model.dto.NewsDto;
import com.egorov.bestnewscity.model.dto.NewsMapper;
import com.egorov.bestnewscity.repository.NewsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Executable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {
    @InjectMocks
    private NewsService newsService;
    @Mock
    private NewsRepository newsRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private DirectExchange directExchange;

    private News news;
    private NewsDto newsDto;
    private String id;

    @BeforeEach
    void setUp() {
        news = News.builder().id("4").title("Title #4")
                .message("Message #4")
                .author("Bob")
                .category(List.of(CategoryNews.POLITIC, CategoryNews.TRAVEL))
                .createDateAtNews(LocalDate.now())
                .createTimeAtNews(LocalTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();
        newsDto = NewsMapper.mapToNewsDto(news);
        id = "4";
    }

    @Test
    void createdNews() {
        Mockito.when(newsRepository.save(news)).thenReturn(Mono.fromSupplier(() -> news));
        newsService.createdNews(newsDto).subscribe(result -> assertEquals(newsDto, result));
        Mockito.verify(newsRepository, Mockito.times(1)).save(news);
    }

    @Test
    void findByIdNews_when_exist() {
        Mockito.when(newsRepository.findById(id)).thenReturn(Mono.fromSupplier(() -> news));
        newsService.findById(id).subscribe(result -> assertEquals(newsDto, result));
        Mockito.verify(newsRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void findByIdNews_when_not_exist() {
        Mockito.when(newsRepository.findById(id)).thenThrow(new NotFoundNewsException("Not Found News!!!"));
        Exception exception = assertThrows(NotFoundNewsException.class, () -> newsService.findById(id).subscribe());
        assertEquals("Not Found News!!!", exception.getMessage());
        assertThrows(NotFoundNewsException.class, ()-> newsService.findById(id).subscribe());
    }

    @Test
    void updateNews_when_exist() {
        Mockito.when(newsRepository.findById(id)).thenReturn(Mono.just(news));
        Mockito.when(newsRepository.save(news)).thenReturn(Mono.just(news));
        newsService.updateNews(id, newsDto).subscribe(res -> {
            assertNotNull(res.getUpdateDateAtNews());
            assertNotNull(res.getCreateTimeAtNews());
            assertNotNull(res.getCreateDateAtNews());
            assertEquals(id, res.getId());
        });
        Mockito.verify(newsRepository, Mockito.times(1)).save(news);
    }

    @Test
    void updateNews_when_not_exist() {
        Mockito.when(newsRepository.findById(id)).thenThrow(new NotFoundNewsException("Not Found News!!!"));
        Exception exception = assertThrows(NotFoundNewsException.class, () -> newsService.findById(id).subscribe());
        assertEquals("Not Found News!!!", exception.getMessage());
        assertThrows(NotFoundNewsException.class, ()-> newsService.findById(id).subscribe());
    }


    @Test
    void findByTitle() {
    }

    @Test
    void findAllNews() {
    }
}