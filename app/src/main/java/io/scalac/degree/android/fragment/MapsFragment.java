package io.scalac.degree.android.fragment;

import org.androidannotations.annotations.EFragment;

import io.scalac.degree33.R;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 16/12/2015
 */
@EFragment(R.layout.fragment_maps)
public class MapsFragment extends BaseFragment {
    // TODO Add view.
    
    @Override
    public boolean needsToolbarSpinner() {
        return false;
    }
}
