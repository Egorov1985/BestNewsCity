package com.egorov.bestnewscity.model.dto;

import com.egorov.bestnewscity.model.News;

public class NewsMapper {

    public static News mapToNews (NewsDto newsDto) {
        News news = new News();
        news.setId(newsDto.getId());
        news.setTitle(newsDto.getTitle());
        news.setMessage(newsDto.getMessage());
        news.setAuthor(newsDto.getAuthor());
        news.setCategory(newsDto.getCategory());
        news.setCreateDateAtNews(newsDto.getCreateDateAtNews());
        news.setCreateTimeAtNews(newsDto.getCreateTimeAtNews());
        news.setUpdateDateAtNews(newsDto.getUpdateDateAtNews());
        return news;
    }

    public static NewsDto mapToNewsDto (News news) {
        NewsDto newsDto = new NewsDto();
        newsDto.setId(news.getId());
        newsDto.setTitle(news.getTitle());
        newsDto.setAuthor(news.getAuthor());
        newsDto.setMessage(news.getMessage());
        newsDto.setCategory(news.getCategory());
        newsDto.setCreateDateAtNews(news.getCreateDateAtNews());
        newsDto.setCreateTimeAtNews(news.getCreateTimeAtNews());
        newsDto.setUpdateDateAtNews(news.getUpdateDateAtNews());
        return newsDto;
    }
}
