package com.zukron.resepmakanan.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;
import com.zukron.resepmakanan.R;
import com.zukron.resepmakanan.activity.MealDetailActivity;
import com.zukron.resepmakanan.adapter.MealAdapter;
import com.zukron.resepmakanan.model.Meal;
import com.zukron.resepmakanan.networking.APIMeal;

import java.util.ArrayList;
import java.util.Objects;

public class SearchFragment extends Fragment implements MealAdapter.OnSelected {
    private TextInputLayout inputSearch;
    private RecyclerView rvSearch;
    private ProgressBar pbSearch;
    private MealAdapter mealAdapter;
    private NestedScrollView nestedScrollView;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputSearch = view.findViewById(R.id.input_search);
        pbSearch = view.findViewById(R.id.pb);
        rvSearch = view.findViewById(R.id.rv);
        nestedScrollView = view.findViewById(R.id.nsv);

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

    @Override
    public void onStart() {
        super.onStart();

        mealAdapter = new MealAdapter(getContext());
        mealAdapter.setOnSelected(this);

        Objects.requireNonNull(inputSearch.getEditText()).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    pbSearch.setVisibility(View.VISIBLE);

                    APIMeal apiMeal = new APIMeal(getContext());
                    apiMeal.getMealSearch(textView.getText().toString().trim());
                    apiMeal.setOnMealListResponse(new APIMeal.OnMealListResponse() {
                        @Override
                        public void onMealList(@Nullable ArrayList<Meal> meals, @Nullable VolleyError error) {
                            if (meals != null) {
                                pbSearch.setVisibility(View.GONE);

                                mealAdapter.setMeals(meals);
                                rvSearch.setAdapter(mealAdapter);
                            }
                        }
                    });

                    hideKeyboard();
                    inputSearch.getEditText().clearFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private void hideKeyboard() {
        if (getActivity() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

            View view = getActivity().getCurrentFocus();
            if (view == null) {
                view = new View(getActivity().getApplicationContext());
            }

            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onItemSelected(Meal meal) {
        if (meal != null) {
            Intent intent = new Intent(getContext(), MealDetailActivity.class);
            intent.putExtra(MealDetailActivity.EXTRA_MEAL, meal);
            startActivity(intent);
        }
    }
}