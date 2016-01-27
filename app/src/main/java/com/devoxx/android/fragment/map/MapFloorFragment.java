package com.devoxx.android.fragment.map;

import android.widget.ImageView;

import com.devoxx.R;
import com.devoxx.android.fragment.common.BaseFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import uk.co.senab.photoview.PhotoViewAttacher;

@EFragment(R.layout.fragment_map_floor)
public class MapFloorFragment extends BaseFragment {

    @FragmentArg
    int floor;

    @ViewById(R.id.mapFloorImage)
    ImageView imageView;

    @AfterViews
    void afterViews() {
        imageView.setImageResource(R.drawable.campus_test);
        new PhotoViewAttacher(imageView);
    }
}
