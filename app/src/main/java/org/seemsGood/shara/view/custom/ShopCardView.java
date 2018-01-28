package org.seemsGood.shara.view.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.seemsGood.shara.R;
import org.seemsGood.shara.model.firebase.ShopAddress;
import org.seemsGood.shara.model.firebase.ShopMain;
import org.seemsGood.shara.model.firebase.WorkTime;
import org.seemsGood.shara.view.company.CompanyActivity;
import org.seemsGood.shara.util.ImageLoader;

import java.util.Calendar;

public class ShopCardView extends CardView implements View.OnClickListener{

    private static final String TAG = "mine";

    private ImageView image;
    private TextView name;
    private TextView rating;

    private ShopAddressLayout shopAddressLayout;

    private ShopMain shop;

    public ShopCardView(Context context) {
        super(context);
    }

    public ShopCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShopCardView setupCard(){

        image = findViewById(R.id.company_image);
        name = findViewById(R.id.company_name);
        rating = findViewById(R.id.company_rating);

        shopAddressLayout = findViewById(R.id.address_layout);

        shopAddressLayout.setupLayout();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW,R.id.company_name);
        shopAddressLayout.setLayoutParams(params);
        setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setForeground(getContext().getDrawable(R.drawable.ripple_background));
        }

        return this;
    }

    public ShopCardView setImage(final String ref){
        image.setImageDrawable(null);
        ImageLoader.loadImage(image,ref);
        return this;
    }

    public ShopCardView setShop(ShopMain cmp) {
        shop = cmp;
        name.setText(shop.getName());
        rating.setText(String.valueOf(shop.getRating()));

        if (shop.getAddress() != null) {
            shopAddressLayout.setVisibility(VISIBLE);
            setShopAddressLayout(shop.getAddress());
        } else {
            shopAddressLayout.setVisibility(GONE);
        }

        return this;
    }

    public void setShopAddressLayout(ShopAddress address){

        Log.d(TAG, "setShopAddressLayout: "+shop.getName()+"  "+address.getTimeTo());

        shopAddressLayout.setAddress(address);
        WorkTime todayWorkTime = address.getTodayWorkTime();

        int drawId = R.drawable.shop_time_open;
        if(!todayWorkTime.getWorkTime().equals("24 hours")){
            Calendar now = Calendar.getInstance();
            int m = now.get(Calendar.MINUTE) + now.get(Calendar.HOUR_OF_DAY) * 60;
            int pM = Integer.parseInt(todayWorkTime.getFrom().substring(3, 5)) + Integer.parseInt(todayWorkTime.getFrom().substring(0, 2)) * 60;
            int fM = Integer.parseInt(todayWorkTime.getTo().substring(3, 5)) + Integer.parseInt(todayWorkTime.getTo().substring(0, 2)) * 60;

            if (m < pM || m > fM) {
                drawId = R.drawable.shop_time_close;
            }

        }

        name.setCompoundDrawablesWithIntrinsicBounds(drawId,0,0,0);

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClickShop: "+shop.getKey());
        Intent intent = new Intent(getContext(),CompanyActivity.class);
        Bundle b = new Bundle();
        b.putString("key", shop.getKey());
        intent.putExtras(b);
        getContext().startActivity(intent);
    }
}
