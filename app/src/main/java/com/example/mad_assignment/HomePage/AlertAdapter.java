package com.example.mad_assignment.HomePage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_assignment.R;

import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {

    private List<AlertFragment.WeatherEntry> alertList;

    public void setAlertList(List<AlertFragment.WeatherEntry> alertList) {
        this.alertList = alertList;
    }
    public AlertAdapter(List<AlertFragment.WeatherEntry> alertList) {
        this.alertList = alertList;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_list_item, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        AlertFragment.WeatherEntry alertItem = alertList.get(position);

        Context context = holder.itemView.getContext();

        holder.iconImageView.setImageDrawable(alertItem.getIconDrawable(context));
        holder.idTextView.setText("Alert: " + alertItem.getType());
        holder.timeTextView.setText(alertItem.getTime());
        holder.durationTextView.setText("Duration: ~"+alertItem.duration+" hours");
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    static class AlertViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView idTextView;
        TextView timeTextView;
        TextView durationTextView;

        public AlertViewHolder(final View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            idTextView = itemView.findViewById(R.id.typeTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
        }
    }
}
