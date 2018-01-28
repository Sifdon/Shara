package org.seemsGood.shara.view.custom;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.seemsGood.shara.R;
import org.seemsGood.shara.adapter.TypesAdapter;
import org.seemsGood.shara.model.Company;
import org.seemsGood.shara.util.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompanyCardView extends CardView implements View.OnClickListener {

    @BindView(R.id.company_image)
    ImageView image;
    @BindView(R.id.company_name)
    TextView name;
    @BindView(R.id.company_rating)
    TextView rating;

    private Company company;

    public CompanyCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setForeground(getContext().getDrawable(R.drawable.ripple_background));
        }
    }

    public void init(View vh){
        ButterKnife.bind(vh, this);
    }

    public void configure(Company company){
        image.setImageDrawable(null);
        ImageLoader.loadImage(image,"companies/"+company.getKey()+"/main.png");
        this.company = company;
        name.setText(company.getName());
        rating.setText(String.valueOf(company.getRating()));
    }

    @Override
    public void onClick(View v) {

    }
}
