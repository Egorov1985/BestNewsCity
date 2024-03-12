package com.egorov.bestnewscity.controller;

import com.egorov.bestnewscity.appService.NewsService;
import com.egorov.bestnewscity.exception.NotFoundNewsException;
import com.egorov.bestnewscity.model.dto.NewsDto;
import com.egorov.bestnewscity.model.dto.NewsUpdateModel;
import com.egorov.bestnewscity.model.entity.CategoryNews;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = NewsController.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NewsControllerTest {

    @MockBean
    NewsService newsService;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void allNews_whenReturnListNews() {
        NewsDto newsDto1 = NewsDto.builder().id("id_news_#1").title("New title #1.").message("New message #1.")
                .author("Tom").category(List.of(CategoryNews.ECONOMIC))
                .createDateAtNews(LocalDate.of(2012, 12, 3))
                .createTimeAtNews(LocalTime.of(12, 22))
                .updateDateAtNews("Not updated!").build();

        NewsDto newsDto2 = NewsDto.builder().id("id_news #2").title("New title #2.").message("New message #2.")
                .author("Anna").category(List.of(CategoryNews.SCIENCE))
                .createDateAtNews(LocalDate.of(2020, 11, 9))
                .createTimeAtNews(LocalTime.of(11, 59))
                .updateDateAtNews("Not updated!").build();

        NewsDto newsDto3 = NewsDto.builder().id("id_news #3").title("New title #3.").message("New message #3.")
                .author("Bill").category(List.of(CategoryNews.SCIENCE))
                .createDateAtNews(LocalDate.of(2023, 5, 5))
                .createTimeAtNews(LocalTime.of(22, 34))
                .updateDateAtNews("Not updated!").build();
        Flux<NewsDto> newsDtoFlux = Flux.just(newsDto1, newsDto2, newsDto3);

        Mockito.when(newsService.findNewsByAuthorOrAndCategoryOrAndCreate
                ("", List.of(), "")).thenReturn(newsDtoFlux);
        webTestClient.get().uri("/api/news")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(NewsDto.class)
                .consumeWith(System.out::println);

        Mockito.when(newsService.findNewsByAuthorOrAndCategoryOrAndCreate
                ("Tom", List.of(), "")).thenReturn(Flux.fromIterable(List.of(newsDto3)));

        webTestClient.get().uri("/api/news?author=Tom")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(NewsDto.class)
                .contains(newsDto3)
                .consumeWith(System.out::println);

        Mockito.when(newsService.findNewsByAuthorOrAndCategoryOrAndCreate
                ("", List.of(), "2020-11-9")).thenReturn(Flux.fromIterable(List.of(newsDto2)));

        webTestClient.get().uri("/api/news?create=2020-11-9")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(NewsDto.class)
                .contains(newsDto2)
                .consumeWith(System.out::println);

        Mockito.when(newsService.findNewsByAuthorOrAndCategoryOrAndCreate
                ("", List.of(CategoryNews.ECONOMIC.toString()), "")).thenReturn(Flux.fromIterable(List.of(newsDto1)));

        webTestClient.get().uri("/api/news?category=ECONOMIC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(NewsDto.class)
                .contains(newsDto1)
                .consumeWith(System.out::println);
    }

    @Test
    void getOneNews_where_exist() {
        NewsDto newsDto = NewsDto.builder().id("id_news").title("New title.").message("New message")
                .author("Tom").category(List.of(CategoryNews.ECONOMIC))
                .createDateAtNews(LocalDate.now())
                .createTimeAtNews(LocalTime.now().truncatedTo(ChronoUnit.MINUTES))
                .updateDateAtNews("Not updated!").build();
        Mockito.when(newsService.findById(Mockito.any())).thenReturn(Mono.just(newsDto));

        Flux<NewsDto> responseBody = webTestClient.get().uri("/api/news/{id}",
                        Collections.singletonMap("id", newsDto.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(NewsDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(newsDto)
                .verifyComplete();
    }

    @Test
    void getOneNews_where_is_not_exist() {

        Mockito.when(newsService.findById(Mockito.any())).thenThrow(new NotFoundNewsException("Not found News!!!"));
        webTestClient.get()
                .uri("/api/news/{id}", Collections.singletonMap("id", "news_id_not"))
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("text/plain;charset=UTF-8")
                .expectBody(String.class)
                .consumeWith(System.out::println);
    }

    @Test
    void creatNews_whenCreateNews_success() {
        NewsDto newsDto = NewsDto.builder().id("id_news").title("New title.").message("New message")
                .author("Tom").category(List.of(CategoryNews.ECONOMIC))
                .createDateAtNews(LocalDate.now())
                .createTimeAtNews(LocalTime.now().truncatedTo(ChronoUnit.MINUTES))
                .updateDateAtNews("Not updated!").build();


        Mockito.when(newsService.createdNews(newsDto)).thenReturn(Mono.fromSupplier(() -> newsDto));

        webTestClient.post()
                .uri("/api/news/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newsDto), NewsDto.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.title").isEqualTo(newsDto.getTitle())
                .jsonPath("$.message").isEqualTo(newsDto.getMessage())
                .jsonPath("$.author").isEqualTo(newsDto.getAuthor())
                .jsonPath("$.category").isNotEmpty();

    }

    @Test
    void updateNews() {
        NewsDto newsDto = NewsDto.builder().id("id_news").title("New title.").message("New message")
                .author("Tom").category(List.of(CategoryNews.ECONOMIC))
                .createDateAtNews(LocalDate.now())
                .createTimeAtNews(LocalTime.now().truncatedTo(ChronoUnit.MINUTES))
                .updateDateAtNews("Not updated!").build();

        Mockito.when(newsService.updateNews(Mockito.any(String.class), Mockito.any(NewsUpdateModel.class))).thenReturn(Mono.just(newsDto));

        webTestClient.put()
                .uri("/api/news/update/{id}", Collections.singletonMap("id", newsDto.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newsDto), NewsDto.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.title").isEqualTo(newsDto.getTitle())
                .jsonPath("$.message").isEqualTo(newsDto.getMessage())
                .jsonPath("$.category").isNotEmpty();

    }

    @Test
    void deleteNews() {
        Mono<Void> voidReturn = Mono.empty();
        NewsDto newsDto = NewsDto.builder().id("id_news").title("New title.").message("New message")
                .author("Tom").category(List.of(CategoryNews.ECONOMIC))
                .createDateAtNews(LocalDate.now())
                .createTimeAtNews(LocalTime.now().truncatedTo(ChronoUnit.MINUTES))
                .updateDateAtNews("Not updated!").build();

        Mockito.when(newsService.deleteNews(newsDto.getId())).thenReturn(voidReturn);

        webTestClient.delete()
                .uri("/api/news/delete/{id}", Collections.singletonMap("id", newsDto.getId()))
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}