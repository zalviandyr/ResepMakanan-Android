package com.zukron.resepmakanan.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewTreeObserver;

import com.android.volley.VolleyError;
import com.google.android.material.appbar.MaterialToolbar;
import com.zukron.resepmakanan.R;
import com.zukron.resepmakanan.adapter.MealAdapter;
import com.zukron.resepmakanan.model.Meal;
import com.zukron.resepmakanan.networking.APIMeal;
import com.zukron.resepmakanan.util.DatabaseFavorite;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity implements MealAdapter.OnSelected {
    private MaterialToolbar mb;
    private NestedScrollView nestedScrollView;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        mb = findViewById(R.id.mb);
        nestedScrollView = findViewById(R.id.nsv);
        rv = findViewById(R.id.rv);

        setBottomExtraSpace();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setSupportActionBar(mb);
        setRecyclerView();
    }

    /***
     * add extra space in bottom while scrolling near bottom of nested scroll view
     */
    private void setBottomExtraSpace() {
        // is equal 55dp
        final float padBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, getResources().getDisplayMetrics());
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // because i'am have super brain and genius, i'am create this formula
                int showIn = nestedScrollView.getChildAt(0).getHeight() - 100;
                int scrollVal = nestedScrollView.getScrollY() + nestedScrollView.getHeight();

                if (scrollVal > showIn) {
                    nestedScrollView.setPadding(0, 0, 0, (int) padBottom);
                    nestedScrollView.setClipToPadding(true);
                } else {
                    nestedScrollView.setPadding(0, 0, 0, 0);
                    nestedScrollView.setClipToPadding(false);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

    private void setRecyclerView() {
        DatabaseFavorite databaseFavorite = new DatabaseFavorite(this);
        ArrayList<Meal> meals = databaseFavorite.getAll();

        if (meals != null) {
            MealAdapter mealAdapter = new MealAdapter(this);
            mealAdapter.setMeals(meals);
            mealAdapter.setOnSelected(this);
            rv.setAdapter(mealAdapter);
        }
    }

    @Override
    public void onItemSelected(Meal meal) {
        if (meal != null) {
            String id = meal.getId();

            APIMeal apiMeal = new APIMeal(this);
            apiMeal.getMeal(id);
            apiMeal.setOnMealResponse(new APIMeal.OnMealResponse() {
                @Override
                public void onMeal(@Nullable Meal meal, @Nullable VolleyError error) {
                    Intent intent = new Intent(FavoriteActivity.this, MealDetailActivity.class);
                    intent.putExtra(MealDetailActivity.EXTRA_MEAL, meal);
                    startActivity(intent);
                }
            });
        }
    }
}