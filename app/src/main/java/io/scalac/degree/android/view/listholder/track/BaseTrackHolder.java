package io.scalac.degree.android.view.listholder.track;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import io.scalac.degree.connection.model.SlotApiModel;

public abstract class BaseTrackHolder extends RecyclerView.ViewHolder {

    public BaseTrackHolder(View itemView) {
        super(itemView);
    }

    public abstract void setupView(SlotApiModel slotApiModel);
}
