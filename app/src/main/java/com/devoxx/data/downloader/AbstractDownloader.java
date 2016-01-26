package com.devoxx.data.downloader;

import com.devoxx.connection.Connection;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

@EBean
public abstract class AbstractDownloader<T> {
    @Bean
    Connection connection;
}
