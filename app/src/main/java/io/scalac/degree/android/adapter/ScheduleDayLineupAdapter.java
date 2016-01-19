package io.scalac.degree.android.adapter;


import com.annimon.stream.Optional;

import org.androidannotations.annotations.EBean;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.scalac.degree.android.view.listholder.BaseScheduleDayLineupHolder;
import io.scalac.degree.connection.model.BreakApiModel;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.connection.model.TalkFullApiModel;

@EBean
public class ScheduleDayLineupAdapter extends RecyclerView.Adapter<BaseScheduleDayLineupHolder> {

    private final List<Item> data = new ArrayList<>();

    public void setData(List<Item> aData) {
        data.clear(); // TODO hmmm??? clear()?
        data.addAll(aData);
    }

    @Override
    public BaseScheduleDayLineupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseScheduleDayLineupHolder(new TextView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(BaseScheduleDayLineupHolder holder, int position) {
        final TextView textView = (TextView) holder.itemView;
        textView.setSingleLine();

        final Item item = data.get(position);

        final String label;
        switch (item.getType()) {
            case TALK:
                textView.setTextColor(Color.GRAY);
                label = item.getTalk().get().title;
                break;
            case BREAK:
                textView.setTextColor(Color.RED);
                label = item.getBreak().get().nameEN;
                break;
            case NONE:
            default:
                // TODO Handle somehow!
                textView.setTextColor(Color.GREEN);
                label = "none-empty";
        }
        textView.setText(label);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public enum ItemType {
        TALK, BREAK, NONE
    }

    public static Item createFromSlotModel(final SlotApiModel slot) {
        return new Item() {
            @Override
            ItemType getType() {
                return slot.isTalk() ? ItemType.TALK : slot.isBreak()
                        ? ItemType.BREAK : ItemType.NONE;
            }

            @Override
            Optional<TalkFullApiModel> getTalk() {
                return Optional.ofNullable(slot.talk);
            }

            @Override
            Optional<BreakApiModel> getBreak() {
                return Optional.ofNullable(slot.slotBreak);
            }
        };
    }

    public static abstract class Item {

        abstract ItemType getType();

        abstract Optional<TalkFullApiModel> getTalk();

        abstract Optional<BreakApiModel> getBreak();
    }
}
