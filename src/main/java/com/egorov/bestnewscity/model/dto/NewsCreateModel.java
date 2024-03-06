package com.egorov.bestnewscity.model.dto;

import com.egorov.bestnewscity.model.entity.CategoryNews;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsCreateModel {

    @NotNull(message = "{news.validation.title}")
    private String title;
    @NotNull(message = "{news.validation.message.default}")
    private String message;
    @NotNull (message = "{news.validation.author}")
    private String author;
    @NotNull (message = "{news.validation.category}")
    private List<CategoryNews> category;
}
