package org.seemsGood.shara.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.seemsGood.shara.R;
import org.seemsGood.shara.model.Company;
import org.seemsGood.shara.view.custom.CompanyCardView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Company> companies;

    public TypesAdapter() {
        companies = new ArrayList<>();
    }

    class LoadingHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        LoadingHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }

    class CompanyHolder extends RecyclerView.ViewHolder{

        CompanyCardView card;

        CompanyHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.company_card);
            card.init(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == 0) {
            view = inflater.inflate(R.layout.template_loading, parent, false);
            viewHolder = new LoadingHolder(view);
        } else {
            view = inflater.inflate(R.layout.template_company, parent, false);
            viewHolder = new CompanyHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CompanyHolder){
            ((CompanyHolder)holder).card.configure(companies.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("TypesAdapter", "getItemViewType: "+position);
        return companies.get(position) == null ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    public void clear(){
        Log.d("TypesAdapter", "clear: ");
        companies.clear();
        notifyDataSetChanged();
    }

    public void add(Collection<Company> companies){
        Log.d("TypesAdapter", "add: ");
        this.companies.addAll(companies);
        notifyDataSetChanged();
    }

    public void beginLoading(){
        Log.d("TypesAdapter", "beginLoading: ");
        companies.add(null);
        notifyItemInserted(companies.size());
    }

    public void endLoading(){
        Log.d("TypesAdapter", "endLoading: ");
        companies.remove(companies.size()-1);
        notifyItemRemoved(companies.size());
    }

}
