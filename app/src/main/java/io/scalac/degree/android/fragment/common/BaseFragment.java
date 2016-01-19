package io.scalac.degree.android.fragment.common;

import org.androidannotations.annotations.EFragment;

import android.support.v4.app.Fragment;

import io.scalac.degree.android.activity.MainActivity;

@EFragment
public class BaseFragment extends Fragment {

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}
