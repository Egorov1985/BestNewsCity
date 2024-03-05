package com.egorov.bestnewscity.model.dto;

import com.egorov.bestnewscity.model.News;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

@Mapper
public interface MapperNews {

    MapperNews INSTANCE = Mappers.getMapper(MapperNews.class);

    NewsDto toNewsDto(News news);

    News toNews(NewsDto newsDto);

    News toNews(NewsCreateModel newsCreateModel);

    default void updateNews(News news, NewsUpdateModel newsUpdateModel){
        news.setTitle(news.getTitle());
        news.setMessage(newsUpdateModel.getMessage());
        news.setCategory(newsUpdateModel.getCategory());
        news.setUpdateDateAtNews(LocalDate.now());
    };

}
