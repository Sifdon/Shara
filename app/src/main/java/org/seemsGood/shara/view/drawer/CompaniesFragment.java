package org.seemsGood.shara.view.drawer;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;

import org.seemsGood.shara.R;
import org.seemsGood.shara.adapter.SearchAdapter;
import org.seemsGood.shara.adapter.SpinnerAdapter;
import org.seemsGood.shara.adapter.TypesAdapter;
import org.seemsGood.shara.view.base.TestActivity;
import org.seemsGood.shara.viewModel.CompaniesViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

public class CompaniesFragment extends MainFragment{


    @BindView(R.id.types_spinner)
    Spinner spinner;
    @BindView(R.id.search_bar)
    CardView searchBar;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.search_list_card)
    CardView searchListCard;
    @BindView(R.id.search_list)
    ListView searchList;
    @BindView(R.id.click_frame)
    FrameLayout clickFrame;

    private SearchAdapter searchAdapter;
    private TypesAdapter typesAdapter;
    private SpinnerAdapter spinnerAdapter;

    private CompaniesViewModel companies;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_companies, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        typesAdapter = new TypesAdapter();
        spinnerAdapter = new SpinnerAdapter(getContext());
        searchAdapter = new SearchAdapter(getContext());

        typesAdapter.beginLoading();

        spinner.setAdapter(spinnerAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(typesAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //find
                Log.d("CompaniesFragment", "onQueryTextChange: "+newText);
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            searchBar.setVisibility(View.INVISIBLE);
            searchListCard.setVisibility(View.INVISIBLE);
            clickFrame.setVisibility(View.INVISIBLE);
            return false;
        });

        clickFrame.setOnClickListener(v -> {
            searchBar.setVisibility(View.INVISIBLE);
            searchListCard.setVisibility(View.INVISIBLE);
            clickFrame.setVisibility(View.INVISIBLE);
        });

        companies = ViewModelProviders.of(getActivity()).get(CompaniesViewModel.class);

        companies.getTypes().observe(this, types ->{
            spinnerAdapter.clear();
            if(types == null) {
                spinnerAdapter.add("Empty");
                return;
            }
            spinnerAdapter.addAll(types.keySet());
        });

        companies.getOpenedType().observe(this, type ->{
            if (type == null) {
                Log.d("CompaniesFragment", "onActivityCreated: TYPE Failed");
                return;
            }
            companies.loadNewCompanies();
            Log.d("CompaniesFragment", "onActivityCreated: "+type.getKey());
        });

        companies.getNewCompanies().observe(this, companies -> {
            typesAdapter.endLoading();
            typesAdapter.add(companies);
            Log.d("CompaniesFragment", "onActivityCreated: "+companies);
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_companies,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.search:
                if(searchBar.getVisibility()==View.INVISIBLE){
                    searchBar.setVisibility(View.VISIBLE);
                    searchListCard.setVisibility(View.VISIBLE);
                    searchView.setIconified(false);
                    clickFrame.setVisibility(View.VISIBLE);
                }
                break;
        }

        return true;
    }

    @OnItemClick(R.id.search_list)
    void onSearchItemClick(int position){

    }

    @OnItemSelected(R.id.types_spinner)
    void onSpinnerItemSelected(int position) {
        typesAdapter.clear();
        typesAdapter.beginLoading();

        String item = spinnerAdapter.getItem(position);
        companies.loadType(item);
        Log.d("CompaniesFragment", "onSpinnerItemSelected: " + position + " " + item);
    }

    @OnClick(R.id.fab_test)
    void test(){
        startActivity(new Intent(getContext(), TestActivity.class));
    }

}
