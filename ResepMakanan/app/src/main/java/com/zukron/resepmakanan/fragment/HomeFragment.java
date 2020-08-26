package com.zukron.resepmakanan.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.zukron.resepmakanan.R;
import com.zukron.resepmakanan.activity.CategoryMealActivity;
import com.zukron.resepmakanan.activity.MealDetailActivity;
import com.zukron.resepmakanan.adapter.CategoryAdapter;
import com.zukron.resepmakanan.adapter.RandomMealAdapter;
import com.zukron.resepmakanan.model.Category;
import com.zukron.resepmakanan.model.Meal;
import com.zukron.resepmakanan.networking.APICategory;
import com.zukron.resepmakanan.networking.APIMeal;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements APICategory.OnCategoryResponse {
    private ViewPager2 viewPager2;
    private DotsIndicator dotsIndicator;
    private ProgressBar pbCategory;
    private RecyclerView rvCategory;
    private RandomMealAdapter randomMealAdapter;
    private CategoryAdapter categoryAdapter;
    private NestedScrollView nestedScrollView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager2 = view.findViewById(R.id.vp2);
        dotsIndicator = view.findViewById(R.id.di);
        pbCategory = view.findViewById(R.id.pb);
        rvCategory = view.findViewById(R.id.rv);
        nestedScrollView = view.findViewById(R.id.nsv);

        categoryAdapter = new CategoryAdapter(getContext());
        randomMealAdapter = new RandomMealAdapter(getContext());

        APICategory apiCategory = new APICategory(getContext(), this);
        apiCategory.getCategories();

        setViewpager();
        setBottomExtraSpace();
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

    private void setViewpager() {
        // set effect page while scrolling
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPager2.setAdapter(randomMealAdapter);
        viewPager2.setPageTransformer(compositePageTransformer);

        // set dot
        dotsIndicator.setViewPager2(viewPager2);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(final int position) {
                if (randomMealAdapter.getMeals(position) == null) {
                    final APIMeal apiMeal = new APIMeal(getContext());
                    apiMeal.getRandomMeal();
                    apiMeal.setOnMealResponse(new APIMeal.OnMealResponse() {
                        @Override
                        public void onMeal(@Nullable Meal meal, @Nullable VolleyError error) {
                            if (meal != null) {
                                randomMealAdapter.setMeal(meal, position);
                                randomMealAdapter.notifyItemChanged(position);
                            }
                        }
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        randomMealAdapter.setOnSelected(new RandomMealAdapter.OnSelected() {
            @Override
            public void onItemSelected(Meal meal) {
                Intent intent = new Intent(getContext(), MealDetailActivity.class);
                intent.putExtra(MealDetailActivity.EXTRA_MEAL, meal);
                startActivity(intent);
            }
        });

        categoryAdapter.setOnSelected(new CategoryAdapter.OnSelected() {
            @Override
            public void onItemSelected(Category category) {
                Intent intent = new Intent(getContext(), CategoryMealActivity.class);
                intent.putExtra(CategoryMealActivity.EXTRA_CATEGORY, category);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCategoriesResponse(@Nullable ArrayList<Category> categories, VolleyError error) {
        if (categories != null) {
            pbCategory.setVisibility(View.GONE);

            categoryAdapter.setCategories(categories);
            rvCategory.setAdapter(categoryAdapter);
        }
    }
}