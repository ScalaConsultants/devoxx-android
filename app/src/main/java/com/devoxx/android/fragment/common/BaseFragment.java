package com.devoxx.android.fragment.common;

import com.devoxx.android.activity.MainActivity;

import org.androidannotations.annotations.EFragment;

import android.support.v4.app.Fragment;

@EFragment
public class BaseFragment extends Fragment {

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}
