package io.scalac.degree.data.manager;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.List;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 28/10/2015
 */
@EBean
public abstract class AbstractDataManager<T> {

    @UiThread
    void notifyAboutSuccess(IDataManagerListener<T> listener, T result) {
        if (listener != null) {
            listener.onDataAvailable(result);
        }
    }

    @UiThread
    void notifyAboutSuccess(IDataManagerListener<T> listener, List<T> result) {
        if (listener != null) {
            listener.onDataAvailable(result);
        }
    }

    @UiThread
    void notifyAboutStart(IDataManagerListener<T> listener) {
        if (listener != null) {
            listener.onDataStartFetching();
        }
    }

    @UiThread
    void notifyAboutFailed(IDataManagerListener<T> listener) {
        if (listener != null) {
            listener.onDataError();
        }
    }

    public interface IDataManagerListener<T> {
        void onDataStartFetching();

        void onDataAvailable(List<T> items);

        void onDataAvailable(T item);

        void onDataError();
    }
}
