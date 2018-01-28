package org.seemsGood.shara.view.custom;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.seemsGood.shara.R;
import org.seemsGood.shara.model.firebase.Good;
import org.seemsGood.shara.model.realm.RealmGood;

import java.util.Map;

import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;

public class GoodCardView extends CardView {

    private static final String TAG = "mine";

    private ImageView image;
    private TextView name;
    private TextView price;
    private ImageView favorite;
    private ImageView plus;
    private ImageView minus;
    private TextView count;

    private int increment = 0;
    private Handler handler = new Handler();

    private RealmGood data;

//    TextView mainDescription;
//    TextView description;

    public GoodCardView(Context context) {
        super(context);
    }

    public GoodCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GoodCardView setupCard(){

        image = findViewById(R.id.good_image);
        name = findViewById(R.id.good_name);
        price = findViewById(R.id.good_price);
        favorite = findViewById(R.id.good_favorite_button);
        plus = findViewById(R.id.good_count_plus);
        minus = findViewById(R.id.good_count_minus);
        count = findViewById(R.id.good_count);

        plus.setOnTouchListener(touchListener);
        minus.setOnTouchListener(touchListener);

        return this;
    }

    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int id;
            boolean f;
            if(data.isFavorite()){
                id = R.drawable.ic_favorite_color_no;
                f = false;
            } else {
                id = R.drawable.ic_favorite_color;
                f = true;
            }
            favorite.setImageDrawable(getResources().getDrawable(id));

            Realm.getDefaultInstance().beginTransaction();
            data.setFavorite(f);
            Realm.getDefaultInstance().commitTransaction();
        }
    };

    OnTouchListener touchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handler.post(changeCount);

                if (view.getId() == R.id.good_count_minus) {
                    increment = -1;
                } else if (view.getId() == R.id.good_count_plus) {
                    increment = 1;
                }

            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                increment = 0;
                handler.removeCallbacks(changeCount);
            }

            return true;
        }
    };

    Runnable changeCount = new Runnable() {
        @Override
        public void run() {
            final int c = data.getCount() + increment;

            if (c > -1) {
                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        data.setCount(c);
                    }
                });
            }

            handler.postDelayed(this, 200);
        }
    };

    public GoodCardView configureCard(Good g){

        name.setText(g.getName());
        price.setText(String.valueOf(g.getPrice()));
        image.setImageDrawable(null);
        //ImageLoader.loadImage(image,g.getImageRef());

        data = Realm.getDefaultInstance()
                .where(RealmGood.class)
                .equalTo("key",g.data.getKey())
                .findFirst();

        count.setText(String.valueOf(data.getCount()));

        data.addChangeListener(listener);

        favorite.setOnClickListener(clickListener);

        int id;
        if(data.isFavorite()){
            id = R.drawable.ic_favorite_color;
        } else {
            id = R.drawable.ic_favorite_color_no;
        }

        favorite.setImageDrawable(ContextCompat.getDrawable(getContext(),id));

        return this;
    }

    private RealmObjectChangeListener<RealmModel> listener = new RealmObjectChangeListener<RealmModel>() {
        @Override
        public void onChange(RealmModel realmModel, ObjectChangeSet changeSet) {
            count.setText(String.valueOf(data.getCount()));
            Log.d(TAG, "onChangeDATAGOOD: "+System.identityHashCode(data));
        }
    };

    public GoodCardView addKind(Map<String,String> kinds){
        TextView kind = findViewById(R.id.good_kind);

        kind.setText(kinds.get(data.getKindKey()));

        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow:"+System.identityHashCode(data));
        data.removeAllChangeListeners();
    }
}
