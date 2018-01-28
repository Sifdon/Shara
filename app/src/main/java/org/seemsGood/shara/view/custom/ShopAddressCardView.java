package org.seemsGood.shara.view.custom;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.seemsGood.shara.R;
import org.seemsGood.shara.model.firebase.ShopAddress;
import org.seemsGood.shara.util.ImageLoader;

public class ShopAddressCardView extends CardView implements View.OnClickListener{

    private static final String TAG = "mine";

    private LinearLayout expandedLayout;
    private LinearLayout imagesContainer;
    private LinearLayout workTimesContainer;
    private LinearLayout addressTags;

    private ShopAddressLayout addressLayout;

    public ShopAddressCardView(Context context) {
        super(context);
    }

    public ShopAddressCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShopAddressCardView setupCard(){

        expandedLayout = findViewById(R.id.expanded_address);
        imagesContainer = findViewById(R.id.gallery_address_container);
        workTimesContainer = findViewById(R.id.work_times_container);
        addressTags = findViewById(R.id.address_tags_container);
        addressLayout = findViewById(R.id.address_layout);

        addressLayout.setupLayout();
        setOnClickListener(this);

        return this;
    }

    public ShopAddressCardView setAddress(ShopAddress address, String addressKey, String key){

        int r = 0;
        for(Integer i : address.getPoints()) r+=i;

        addressLayout.setAddress(address)
                .setRating(address.getRating(),r);

        for(int i = 0; i < 7; i++){
            TextView workTime = (TextView) workTimesContainer.getChildAt(i);
            workTime.setText(address.getWorkTime(i).getWorkTime());
        }

        if(address.getPicturesNames()!=null) {
            for (int i = 0; i < address.getPicturesNames().size(); i++) {
                ImageView image = new ImageView(getContext());
                image.setAdjustViewBounds(true);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
                if (i != 0)
                    params.leftMargin = width;
                if (i != address.getPicturesNames().size() - 1)
                    params.rightMargin = width;
                else
                    params.rightMargin = width * 2;
                imagesContainer.addView(image, params);
                String ref = "companies/" + key + "/gallery/addresses/" + addressKey + "/" + address.getPicturesNames().get(i) + "/cropped.png";

                ImageLoader.loadImage(image, ref);
            }
        }
        return this;
    }

    @Override
    public void onClick(View v) {
        if(expandedLayout.getVisibility()==VISIBLE){
            expandedLayout.setVisibility(GONE);
        } else {
            expandedLayout.setVisibility(VISIBLE);
        }
    }

    public void simplifyAddress(){
        setOnClickListener(null);
        addressLayout.hideTags()
                .moveTimeTo()
                .hideRating();
    }
}
