package com.mediaremote.vlcontroller.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mediaremote.vlcontroller.R;
import com.mediaremote.vlcontroller.model.VlcServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by nikita on 28/06/15.
 */
public class VlcServersAdapter extends RecyclerView.Adapter<VlcServersAdapter.ViewHolder> {

    private List<VlcServer> vlcServerList;
    private SparseBooleanArray selectedItems;
    OnItemClickListener itemClickListener;

    public VlcServersAdapter(List<VlcServer> vlcServerList) {
        this.vlcServerList = vlcServerList;
        this.selectedItems = new SparseBooleanArray();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); ++i) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public List<VlcServer> getSelectedServers() {
        List<VlcServer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); ++i) {
            items.add(vlcServerList.get(selectedItems.keyAt(i)));
        }
        return items;
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public void clearSelection() {
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();
        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }

    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }

    public void add(VlcServer item) {
        vlcServerList.add(item);
        notifyItemInserted(vlcServerList.size() - 1);
    }


    public void removeItem(int position) {
        vlcServerList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            vlcServerList.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    public void clear() {
        removeRange(0, vlcServerList.size());
    }

    public VlcServer get(int index) {
        return vlcServerList.get(index);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_vlc_server_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        VlcServer vlcServer = vlcServerList.get(i);
        viewHolder.firstLine.setText(vlcServer.getServerName());
        viewHolder.secondLine.setText(vlcServer.getIpAndPort());

        viewHolder.selectedOverlay.setVisibility(isSelected(i) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return vlcServerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView firstLine;
        public TextView secondLine;
        public View selectedOverlay;

        public ViewHolder(View itemView) {
            super(itemView);
            firstLine = (TextView) itemView.findViewById(R.id.firstLine);
            secondLine = (TextView) itemView.findViewById(R.id.secondLine);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
                Log.i("ViewHolder", ">>> onClick()");
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onLongItemClick(v, getPosition());
                Log.i("ViewHolder", ">>> onLongItemClick()");
            }
            return true;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
        boolean onLongItemClick(View view, int position);
    }


}
