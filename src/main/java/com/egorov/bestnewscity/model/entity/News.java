package com.egorov.bestnewscity.model.entity;

import com.egorov.bestnewscity.model.entity.CategoryNews;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Document(collection = "news")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class News {

    @MongoId
    private String id;
    private String title;
    private String message;
    private String author;
    private List<CategoryNews> category;
    private LocalDate createDateAtNews;
    private LocalTime createTimeAtNews;
    private String updateDateAtNews;
}
