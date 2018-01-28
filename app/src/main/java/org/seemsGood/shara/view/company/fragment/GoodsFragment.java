package org.seemsGood.shara.view.company.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

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
import org.json.JSONObject;
import org.seemsGood.shara.R;
import org.seemsGood.shara.model.firebase.Good;
import org.seemsGood.shara.model.firebase.Shop;
import org.seemsGood.shara.util.Session;
import org.seemsGood.shara.util.RequestHandler;
import org.seemsGood.shara.view.custom.GoodCardView;

import java.util.ArrayList;
import java.util.List;

public class GoodsFragment extends Fragment {

    private static final String TAG = "mine";
    private static final int GOODS_LOAD_COUNT = 16;

    private Shop shop;

    private GoodsAdapter adapter;

    private DatabaseReference databaseReference;

    private boolean loading;
    private int loadCount;
    private List<Good> loadedGoods;

    private int openedKind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_goods, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        shop = Session.shop;

        databaseReference = FirebaseDatabase.getInstance().getReference("companies/goods/"+shop.getKey());

        if(shop.getKinds()==null) return;

        if(shop.getGoods()==null) {
            shop.generateKinds();
        }

        RecyclerView goodsRecycler = getActivity().findViewById(R.id.goods_recycler);
        final StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adapter = new GoodsAdapter();

        goodsRecycler.setLayoutManager(manager);
        goodsRecycler.setAdapter(adapter);

        goodsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    ((FloatingActionButton) getActivity().findViewById(R.id.fab_sort)).hide();
                } else if (dy < 0) {
                    ((FloatingActionButton) getActivity().findViewById(R.id.fab_sort)).show();
                }

                int[] last = manager.findLastVisibleItemPositions(new int[2]);
                if (shop.getGoodsMap(openedKind).size() != 0) {

                    if (last[1] > shop.getGoodsMap(openedKind).size() - 6 && !loading) {
                        loadGoods(openedKind);
                    }
                }
            }
        });

        loadKinds();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadKinds(){

        Spinner spinner = getActivity().findViewById(R.id.spinner_kinds);
        spinner.setAdapter(new ArrayAdapter<>(getContext(),R.layout.template_spinner_item,shop.getKindsValues()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.clear();
                openedKind = i;

                if(shop.getGoodKeyList(i).size()==0){
                    getKeys(i);
                } else {
                    showAllGoods(i);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getKeys(final int kind) {
        String url = getString(R.string.firebase_functions) +
                "sortGoods" +
                "?kind=" + shop.getKindKey(kind) +
                "&key=" + shop.getKey() +
                "&sort=0";

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        shop.getNames().get(String.valueOf(kind)).put(obj.getString("key"),obj.getString("name"));
                        shop.addGoodKey(kind,obj.getString("key"));
                        Log.d(TAG, "onResponse: "+shop.getNames());
                    }
                } catch (JSONException ignored) {
                }

                //Session.getShop().setGoodsKeys(goodsKeys);

                loadGoods(kind);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
            }
        });

        RequestHandler.getInstance(getContext()).addToRequestQueue(request);
    }

    private void loadGoods(int kind){

        loadedGoods = new ArrayList<>();
        loading = true;
        loadCount = 0;
        int goodsSize = shop.getGoodsMap(openedKind).size();
        int size = Math.min(shop.getGoodKeyList(kind).size(),goodsSize + GOODS_LOAD_COUNT);
        loadCount = size-goodsSize;
        if(loadCount>0) {
            adapter.startLoading();
            for (int i = goodsSize; i < size; i++) {
                databaseReference.child(shop.getKindKey(kind)+"/"+ shop.getGoodKey(kind,i)).addListenerForSingleValueEvent(goodValueListener);
            }
        }

    }

    private void showLoadedGoods(){
        adapter.addGoods(loadedGoods);

    }

    private void showAllGoods(int kind){
        adapter.setGoods(new ArrayList<>(shop.getGoods().get(kind).values()));
    }

    private ValueEventListener goodValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Good r = dataSnapshot.getValue(new GenericTypeIndicator<Good>(){});
            r.setKeys(dataSnapshot.getKey(),shop.getKindKey(openedKind),shop.getKey());
            loadedGoods.add(r);
            shop.getGoodsMap(openedKind).put(dataSnapshot.getKey(),r);
            if(loadedGoods.size() == loadCount) {
                loading = false;
                adapter.stopLoading();
                showLoadedGoods();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private class GoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<Good> goods;

        GoodsAdapter() {
            goods = new ArrayList<>();
        }

        class LoadingHolder extends RecyclerView.ViewHolder {
            ProgressBar loading;
            LoadingHolder(View itemView) {
                super(itemView);
                loading = itemView.findViewById(R.id.progress_bar);
            }
        }

        class GoodHolder extends RecyclerView.ViewHolder {
            GoodCardView good;
            GoodHolder(View itemView) {
                super(itemView);
                good = itemView.findViewById(R.id.good_card);
                good.setupCard();
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
                        inflate(R.layout.template_good, parent, false);
                vh = new GoodHolder(v);
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof GoodHolder) {
                Good good = goods.get(position);
                ((GoodHolder) holder).good.configureCard(good);
            } else if (holder instanceof LoadingHolder) {
                ((LoadingHolder) holder).loading.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return goods.get(position) == null ? 0 : 1;
        }

        @Override
        public int getItemCount() {
            return goods.size();
        }

        void setGoods(List<Good> c) {
            goods = new ArrayList<>(c);
            notifyDataSetChanged();
        }

        void addGoods(List<Good> c) {
            goods.addAll(c);
            notifyItemRangeInserted(goods.size()-c.size(),c.size());
        }

        void startLoading() {
            goods.add(null);
            notifyItemInserted(goods.size());
        }

        void stopLoading() {
            goods.remove(goods.size() - 1);
            notifyItemRemoved(goods.size());
        }

        void clear() {
            goods.clear();
            notifyDataSetChanged();
        }
    }
}