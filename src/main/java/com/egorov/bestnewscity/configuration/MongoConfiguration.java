package com.egorov.bestnewscity.configuration;

import com.egorov.bestnewscity.model.CategoryNews;
import com.egorov.bestnewscity.model.News;
import com.egorov.bestnewscity.repository.NewsRepository;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;


@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.egorov.bestnewscity.repository")
public class MongoConfiguration extends AbstractReactiveMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "news";
    }

    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create("mongodb://root:root1234@localhost:27017/");
    }


    @Bean
    CommandLineRunner commandLineRunner(NewsRepository newsRepository) {
        return args -> {
            var data = Flux.just(
                    new News("1", "Title for news 1", "Message for news #1", "Greg",
                            List.of(CategoryNews.POLITIC, CategoryNews.ECONOMIC), LocalDate.of(2012, 11, 5),
                                    LocalTime.of(15, 12).truncatedTo(ChronoUnit.MINUTES), null),
                    new News("2", "Title for news 2", "Message for news #2", "Bill",
                            List.of(CategoryNews.SPORT), LocalDate.of(2015, 1, 6),
                                    LocalTime.of(10, 00).truncatedTo(ChronoUnit.MINUTES), null),
                    new News("3", "Title for news 3", "Message for news #3", "Tomas",
                            List.of(CategoryNews.SCIENCE, CategoryNews.POLITIC), LocalDate.of(2024, 5, 9),
                                    LocalTime.of(00, 55).truncatedTo(ChronoUnit.MINUTES), null),
                    new News("4", "Title for news 4", "Message for news #4", "Anna",
                            List.of(CategoryNews.TECHNOLOGY, CategoryNews.ECONOMIC), LocalDate.of(2020, 4, 8),
                                    LocalTime.of(11, 5).truncatedTo(ChronoUnit.MINUTES), null),
                    new News("5", "Title for news 5", "Message for news #5", "Rita",
                            List.of(CategoryNews.POLITIC), LocalDate.of(2021, 7, 1),
                                    LocalTime.of(21, 35).truncatedTo(ChronoUnit.MINUTES), null));

            newsRepository.deleteAll()
                    .thenMany(newsRepository.saveAll(data)).subscribe(System.out::println);
        };
    }
}
