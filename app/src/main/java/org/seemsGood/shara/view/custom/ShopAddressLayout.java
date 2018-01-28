package org.seemsGood.shara.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.seemsGood.shara.R;
import org.seemsGood.shara.model.firebase.ShopAddress;

public class ShopAddressLayout extends RelativeLayout {

    private TextView placeView;
    private TextView workTimeView;
    private TextView timeToView;

    private TextView ratingView;
    private TextView ratingCountView;

    private LinearLayout tagsLayout;

    public ShopAddressLayout(Context context) {
        super(context);
    }

    public ShopAddressLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShopAddressLayout setupLayout() {

        placeView = findViewById(R.id.address_place);
        workTimeView = findViewById(R.id.address_work_time);
        timeToView = findViewById(R.id.address_time_to);
        tagsLayout = findViewById(R.id.address_tags_container);

        ratingView = findViewById(R.id.address_rating_points);
        ratingCountView = findViewById(R.id.address_rating_count);

        return this;
    }

    public ShopAddressLayout setAddress(ShopAddress address) {
        String todayWorkTime = address.getTodayWorkTime().getWorkTime();
        workTimeView.setText(todayWorkTime);
        placeView.setText(address.getPlace());

        if(address.getTimeTo() != -1) {
            int min = address.getTimeTo();
            String time = (min > 60 ? "60+" : min) + "min";
            timeToView.setText(time);
            timeToView.setVisibility(VISIBLE);
        } else {
            timeToView.setVisibility(GONE);
        }

        return this;
    }

    public ShopAddressLayout setRating(double r, int rC){
        tagsLayout.setVisibility(GONE);
        ratingView.setText(String.valueOf(r));
        ratingCountView.setText("("+rC+")");
        findViewById(R.id.address_rating_container).setVisibility(VISIBLE);

        return this;
    }

    public ShopAddressLayout hideTags(){
        tagsLayout.setVisibility(GONE);
        return this;
    }

    public ShopAddressLayout moveTimeTo(){
        LayoutParams params = (LayoutParams) timeToView.getLayoutParams();
        params.addRule(BELOW,R.id.address_place);
        timeToView.setLayoutParams(params);

        LayoutParams p = (LayoutParams) placeView.getLayoutParams();
        p.removeRule(LEFT_OF);
        placeView.setLayoutParams(p);

        return this;
    }

    public ShopAddressLayout hideRating(){
        ratingView.setVisibility(GONE);
        ratingCountView.setVisibility(GONE);
        return this;
    }

}
