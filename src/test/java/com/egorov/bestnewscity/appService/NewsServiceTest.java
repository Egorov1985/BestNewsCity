package com.egorov.bestnewscity.appService;

import com.egorov.bestnewscity.exception.NotFoundNewsException;
import com.egorov.bestnewscity.model.dto.MapperNews;
import com.egorov.bestnewscity.model.dto.NewsCreateModel;
import com.egorov.bestnewscity.model.dto.NewsDto;
import com.egorov.bestnewscity.model.dto.NewsUpdateModel;
import com.egorov.bestnewscity.model.entity.CategoryNews;
import com.egorov.bestnewscity.model.entity.News;
import com.egorov.bestnewscity.repository.NewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {
    @InjectMocks
    private NewsService newsService;
    @Mock
    private NewsRepository newsRepository;

    @Spy
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
                .updateDateAtNews("Not updated!")
                .build();
        newsDto = MapperNews.INSTANCE.toNewsDto(news);
        id = "4";
    }

    @Test
    void createdNews() {

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
        assertThrows(NotFoundNewsException.class, () -> newsService.findById(id).subscribe());
    }

    @Test
    void updateNews_when_exist() {
        NewsUpdateModel updateModel = new NewsUpdateModel("Message #4", "Bob", List.of(CategoryNews.POLITIC, CategoryNews.SCIENCE));
        Mockito.when(newsRepository.findById(id)).thenReturn(Mono.just(news));
        Mockito.when(newsRepository.save(news)).thenReturn(Mono.just(news));
        newsService.updateNews(id, updateModel).subscribe(res -> {
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
        assertThrows(NotFoundNewsException.class, () -> newsService.findById(id).subscribe());
    }


    @Test
    void findByTitle() {
    }

    @Test
    void findAllNews() {
    }
}