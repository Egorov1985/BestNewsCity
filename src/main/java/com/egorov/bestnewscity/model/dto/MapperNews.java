package com.egorov.bestnewscity.model.dto;

import com.egorov.bestnewscity.model.News;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MapperNews {

    MapperNews INSTANCE = Mappers.getMapper(MapperNews.class);

    NewsDto toNewsDto(News news);

    News toNews (NewsDto newsDto);

}
