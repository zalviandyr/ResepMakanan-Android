package com.zukron.resepmakanan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zukron.resepmakanan.R;

import java.util.ArrayList;

/**
 * Project name is Resep Makanan
 * Created by Zukron Alviandy R on 8/26/2020
 * Contact me if any issues on zukronalviandy@gmail.com
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> ingredients;
    private ArrayList<String> measures;

    public IngredientAdapter(Context context, ArrayList<String> ingredients, ArrayList<String> measures) {
        this.context = context;

        // find not null or null
        ArrayList<String> meaDummy = new ArrayList<>();
        ArrayList<String> ingDummy = new ArrayList<>();

        for (int i = 0; i < ingredients.size(); i++) {
            if (!ingredients.get(i).isEmpty()) {
                ingDummy.add(ingredients.get(i));
                meaDummy.add(measures.get(i));
            }
        }

        this.ingredients = ingDummy;
        this.measures = meaDummy;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String number = (position + 1) + ".";
        String measure = "(" + measures.get(position) + ")";

        holder.tvNumber.setText(number);
        holder.tvIngredient.setText(ingredients.get(position));
        holder.tvMeasure.setText(measure);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvIngredient, tvMeasure;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNumber = itemView.findViewById(R.id.tv_number);
            tvIngredient = itemView.findViewById(R.id.tv_ingredient);
            tvMeasure = itemView.findViewById(R.id.tv_measure);
        }
    }
}
