package com.belsoft.sportstracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.belsoft.sportstracker.R;
import com.belsoft.sportstracker.data.RouteItem;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {
    private final ArrayList<RouteItem> routeItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class RouteViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView textView1;
        final TextView textView2;

        RouteViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public RouteAdapter(ArrayList<RouteItem> routeItems) {
        this.routeItems = routeItems;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_item, parent, false);
        return new RouteViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        RouteItem currentItem = routeItems.get(position);

        holder.imageView.setImageResource(currentItem.getIcon());
        holder.textView1.setText(currentItem.getDate());
        holder.textView2.setText(currentItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return routeItems.size();
    }
}
