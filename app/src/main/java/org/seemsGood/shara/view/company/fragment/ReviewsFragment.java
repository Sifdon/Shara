package org.seemsGood.shara.view.company.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.seemsGood.shara.R;
import org.seemsGood.shara.model.firebase.Review;
import org.seemsGood.shara.model.firebase.Shop;
import org.seemsGood.shara.util.Session;
import org.seemsGood.shara.util.RequestHandler;
import org.seemsGood.shara.view.custom.ReviewCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewsFragment extends Fragment {

    private static final String TAG = "mine";
    private static final int REVIEWS_LOAD_COUNT = 6;

    private Shop shop;

    private DatabaseReference databaseReference;

    private TextView ratingView;
    private TextView ratingCountView;
    private LinearLayout starsContainer;
    private LinearLayout barsContainer;

    private ReviewsAdapter adapter;

    private boolean loading;
    private int loadCount;
    private Map<String,Review> loadedReviews;

    public ReviewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_shop_reviews, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        shop = Session.shop;

        databaseReference = FirebaseDatabase.getInstance().getReference("companies/reviews/" + shop.getKey());
        configureMainInfo();
        if(shop.getReviews()==null) {
            shop.setupReviews();
            getKeys();
        } else {
            showAllReviews();
        }

    }

    private void configureMainInfo(){

        ratingView = getActivity().findViewById(R.id.company_rating);
        ratingCountView = getActivity().findViewById(R.id.shop_ratings_count);
        starsContainer = getActivity().findViewById(R.id.shop_stars_container);
        barsContainer = getActivity().findViewById(R.id.shop_bars_container);

        configureStars();

        RecyclerView reviewsRecycler = getActivity().findViewById(R.id.reviews_recycler);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter = new ReviewsAdapter();

        reviewsRecycler.setLayoutManager(manager);
        reviewsRecycler.setAdapter(adapter);

        reviewsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    ((FloatingActionButton) getActivity().findViewById(R.id.fab_sort)).hide();
                } else if (dy < 0) {
                    ((FloatingActionButton) getActivity().findViewById(R.id.fab_sort)).show();
                }

                int last = manager.findLastVisibleItemPosition();
                if (shop.getReviews().size() != 0) {

                    if (last > shop.getReviews().size() - 2 && !loading) {
                        loadReviews();
                    }
                }
            }
        });


        ratingView.post(new Runnable() {
            @Override
            public void run() {
                int height = ratingView.getHeight();
                String sh = String.valueOf(height);
                int li = Integer.valueOf(sh.substring(sh.length() - 1));

                if (li > 5) height += (10 - li);
                else height += (5 - li);

                ratingView.setHeight(height);
                configureBars();
            }
        });
    }

    private void configureStars(){

        float rating = shop.getRating();

        ratingView.setText(String.valueOf(rating));
        int r = 0;
        for(Integer i : shop.getPoints()) r+=i;
        ratingCountView.setText(String.valueOf(r));

        for(int i = 0; i < 5; i++){
            int id;
            if(rating>i+0.5) id = R.drawable.ic_star_full;
            else if(rating>i) id = R.drawable.ic_star_half;
            else id = R.drawable.ic_star_empty;

            ((ImageView)starsContainer.getChildAt(i)).setImageDrawable(ContextCompat.getDrawable(getContext(),id));
        }
    }

    private void configureBars() {
        List<Integer> points = shop.getPoints();
        int h = ratingView.getHeight();
        int w = barsContainer.getWidth();
        int maxBar = 0;
        for (int i = 0; i < 5; i++) if (points.get(i) > maxBar) maxBar = points.get(i);

        for (int i = 0; i < 5; i++) {
            int barLength = (int) (w * (float) points.get(i) / maxBar);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(barLength, h / 5);
            barsContainer.getChildAt(i).setLayoutParams(params);
        }
    }

    private void getKeys(){
        String url = getString(R.string.firebase_functions) +
                "sortReviews?" +
                "key=" + shop.getKey();

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                try {
                    for (int i = 0; i < response.length(); i++) {
                        shop.getReviewKeys().add(response.getString(i));
                    }
                } catch (JSONException ignored) {
                }

                loadReviews();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error.toString());
            }
        });

        RequestHandler.getInstance(getContext()).addToRequestQueue(request);

    }

    private ValueEventListener reviewValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Review r = dataSnapshot.getValue(new GenericTypeIndicator<Review>(){});
            loadedReviews.put(dataSnapshot.getKey(),r);
            shop.getReviews().put(dataSnapshot.getKey(),r);
            if(loadedReviews.size() == loadCount) {
                loading = false;
                adapter.stopLoading();
                showLoadedReviews();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void loadReviews() {
        loadedReviews = new HashMap<>();
        loading = true;
        loadCount = 0;
        int mapSize = shop.getReviews().size();
        int size = Math.min(shop.getReviewKeys().size(),mapSize + REVIEWS_LOAD_COUNT);
        loadCount = size-mapSize;
        if(loadCount>0) {
            adapter.startLoading();
            for (int i = mapSize; i < size; i++) {
                databaseReference.child(shop.getReviewKeys().get(i)).addListenerForSingleValueEvent(reviewValueListener);
            }
        }
    }

    private void showLoadedReviews() {
        adapter.addReviews(new ArrayList<>(loadedReviews.values()));
    }

    private void showAllReviews() {
        adapter.setReviews(new ArrayList<>(shop.getReviews().values()));
    }

    private class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<Review> reviews;

        ReviewsAdapter() {
            reviews = new ArrayList<>();
        }

        class LoadingHolder extends RecyclerView.ViewHolder {
            ProgressBar loading;
            LoadingHolder(View itemView) {
                super(itemView);
                loading = itemView.findViewById(R.id.progress_bar);
            }
        }

        class ReviewHolder extends RecyclerView.ViewHolder {
            ReviewCardView review;
            ReviewHolder(View itemView) {
                super(itemView);
                review = itemView.findViewById(R.id.review_card);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh;
            View v;
            if (viewType == 0) {
                v = LayoutInflater.from(getContext()).
                        inflate(R.layout.template_loading, parent, false);
                vh = new LoadingHolder(v);
            } else {
                v = LayoutInflater.from(getContext()).
                        inflate(R.layout.template_review, parent, false);
                vh = new ReviewHolder(v);
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ReviewHolder) {
                Review review = reviews.get(position);
                ((ReviewHolder) holder).review
                        .setupCard()
                        .setReview(review, shop);
            } else if (holder instanceof LoadingHolder) {
                ((LoadingHolder) holder).loading.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return reviews.get(position) == null ? 0 : 1;
        }

        @Override
        public int getItemCount() {
            return reviews.size();
        }

        void setReviews(List<Review> c) {
            reviews = new ArrayList<>(c);
            notifyDataSetChanged();
        }

        void addReviews(List<Review> c){
            reviews.addAll(c);
            notifyItemRangeInserted(reviews.size()-c.size(),c.size());
        }

        void addReview(Review c) {
            reviews.add(c);
            notifyItemInserted(reviews.size());
        }

        void startLoading() {
            reviews.add(null);
            notifyItemInserted(reviews.size());
        }

        void stopLoading() {
            reviews.remove(reviews.size() - 1);
            notifyItemRemoved(reviews.size());
        }

        void clear() {
            reviews.clear();
            reviews.add(null);
            notifyDataSetChanged();
        }
    }

}
