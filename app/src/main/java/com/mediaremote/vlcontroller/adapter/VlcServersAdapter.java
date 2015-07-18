package com.mediaremote.vlcontroller.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mediaremote.vlcontroller.R;
import com.mediaremote.vlcontroller.model.VlcServer;

import java.util.List;

/**
 * Created by nikita on 28/06/15.
 */
public class VlcServersAdapter extends RecyclerView.Adapter<VlcServersAdapter.ViewHolder> {

    private List<VlcServer> vlcServerList;
    OnItemClickListener itemClickListener;

    public VlcServersAdapter(List<VlcServer> vlcServerList) {
        this.vlcServerList = vlcServerList;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }

    public void add(VlcServer item) {
        vlcServerList.add(item);
        notifyItemInserted(vlcServerList.size());
    }

    public void remove(VlcServer item) {
        int position = vlcServerList.indexOf(item);
        vlcServerList.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        vlcServerList.clear();
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
    }

    @Override
    public int getItemCount() {
        return vlcServerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView firstLine;
        public TextView secondLine;

        public ViewHolder(View itemView) {
            super(itemView);
            firstLine = (TextView) itemView.findViewById(R.id.firstLine);
            secondLine = (TextView) itemView.findViewById(R.id.secondLine);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onLongItemClick(v);
            }
            return true;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
        void onLongItemClick(View view);
    }


}
