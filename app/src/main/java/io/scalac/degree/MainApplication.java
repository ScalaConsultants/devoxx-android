package io.scalac.degree;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

public class MainApplication extends Application {

	@Override
	public void onCreate() {
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().penaltyDeath()..build());
		super.onCreate();
//		Crashlytics.start(this);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
			try {
				ViewConfiguration config = ViewConfiguration.get(this);
				Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
				if (menuKeyField != null) {
					menuKeyField.setAccessible(true);
					menuKeyField.setBoolean(config, false);
				}
			} catch (Exception ex) {
				// Ignore
			}

		// Universal image loader
		initImageLoader(getApplicationContext());
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		ImageLoader.getInstance().clearMemoryCache();
	}

	public static void initImageLoader(Context context) {
		com.nostra13.universalimageloader.utils.L.writeLogs(false);

		int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);

		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2)
				.threadPoolSize(5)
				.memoryCache(new LruMemoryCache(memoryCacheSize))
				.diskCache(new UnlimitedDiscCache(StorageUtils.getCacheDirectory(context)))
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}
