package com.egorov.bestnewscity.model.dto;

import com.egorov.bestnewscity.model.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper
public interface MapperNews {

    MapperNews INSTANCE = Mappers.getMapper(MapperNews.class);

    NewsDto toNewsDto(News news);

    News toNews(NewsDto newsDto);

    News toNews(NewsCreateModel newsCreateModel);


}
