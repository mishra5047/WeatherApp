package com.abhishek.weatherapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhishek.weatherapp.databinding.ItemReportBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/** The Adapter for Recycler View used in 7 days report**/
public class WeatherReportAdapter extends RecyclerView.Adapter<WeatherReportAdapter.ViewHolder> {

    // data members
    ItemReportBinding binding;
    ArrayList<ItemWeatherReport> list;
    Context context;
    // unit -> C or F
    String unit;

    public WeatherReportAdapter(ArrayList<ItemWeatherReport> list, Context context, String unit) {
        this.list = list;
        this.context = context;
        this.unit = unit;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // using binding to generate the view Holder object
        binding = ItemReportBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull WeatherReportAdapter.ViewHolder holder, int position) {
        // setting the layout according to the item of arraylist

        ItemWeatherReport item = list.get(position);
        binding.dayItem.setText(item.getDay());
        binding.textMin.setText(item.getMinTemp() + unit);
        binding.textMax.setText(item.getMaxTemp() + unit);
        binding.textWeather.setText(item.getImgText());

        // setting the Image using picasso
        Picasso.get().load(item.getImageUrl()).into(binding.imageWeatherIcon);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // The viewHolder for the adapter
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView day, tempMin, tempMax, textWeather;
        ImageView imageWeather;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            day = binding.dayItem;
            tempMin = binding.textMin;
            tempMax = binding.textMax;
            imageWeather = binding.imageWeatherIcon;
            textWeather = binding.textWeather;
        }
    }
}
