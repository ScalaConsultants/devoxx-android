package io.scalac.degree.android.fragment;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.widget.Spinner;

import org.androidannotations.annotations.EFragment;

import io.scalac.degree.android.activity.MainActivity;

@EFragment
public class BaseFragment extends Fragment {

    public static final int UNKNOWN_TITLE_RES = -1;

    MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    Spinner getToolbarSpinner() {
        return getMainActivity().getToolbarSpinner();
    }

    public boolean needsToolbarSpinner() {
        return true;
    }

    public boolean needsFilterToolbarIcon() {
        return false;
    }

    @StringRes public int getTitle() {
        return UNKNOWN_TITLE_RES;
    }

    @Nullable public String getTitleAsString() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMainActivity().invalidateToolbarTitle();
    }
}
