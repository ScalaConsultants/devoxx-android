package io.scalac.degree.data.downloader;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import io.scalac.degree.connection.Connection;

@EBean
public abstract class AbstractDownloader<T> {
    @Bean
    Connection connection;
}
