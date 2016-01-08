package io.scalac.degree.utils;

import org.androidannotations.annotations.EBean;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

@EBean
public class Utils {

	public static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Logger.exc(e);
			return "versionName";
		}
	}

	public static int getVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			Logger.exc(e);
			return 0;
		}
	}
}
