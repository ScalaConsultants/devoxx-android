package io.scalac.degree.android.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import java.util.List;

import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree.utils.Utils;
import io.scalac.degree33.R;

@EFragment(R.layout.items_list_view)
public class BreaksFragment extends BaseFragment {

    @Bean
    SlotsDataManager slotsDataManager;

    @FragmentArg
    SlotApiModel timeslotID;

    private ItemAdapter listAdapter;
    private List<SlotApiModel> breakItemsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        breakItemsList = slotsDataManager.getBreaksListBySlot(timeslotID);
        listAdapter = new ItemAdapter();
    }

    @AfterViews void afterViews() {
        final ListView listViewBreaks = (ListView) getView();
        listViewBreaks.setFooterDividersEnabled(false);
        listViewBreaks.setAdapter(listAdapter);
        listViewBreaks.setOnItemClickListener(null);
        listViewBreaks.setItemsCanFocus(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    class ItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return breakItemsList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View viewItem;
            ViewHolder holder;

            if (convertView == null) {
                viewItem = getActivity().getLayoutInflater().inflate(R.layout.break_list_item, parent, false);
                holder = new ViewHolder();
                holder.textTitle = (TextView) viewItem.findViewById(R.id.textTitle);
                holder.textDesc = (TextView) viewItem.findViewById(R.id.textDesc);
                viewItem.setTag(holder);
            } else {
                viewItem = convertView;
                holder = (ViewHolder) viewItem.getTag();
            }

            final SlotApiModel breakItem = breakItemsList.get(position);
            holder.textTitle.setVisibility(View.VISIBLE);
            holder.textTitle.setText(breakItem.slotBreak.nameEN);
            holder.textDesc.setVisibility(View.GONE);

            return viewItem;
        }

        private class ViewHolder {
            public TextView textTitle;
            public TextView textDesc;
        }
    }
}
