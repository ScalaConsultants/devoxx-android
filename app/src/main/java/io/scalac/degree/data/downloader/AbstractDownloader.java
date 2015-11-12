package io.scalac.degree.data.downloader;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import io.scalac.degree.connection.Connection;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 27/10/2015
 */
@EBean
public abstract class AbstractDownloader<T> {
    @Bean
    Connection connection;
}
