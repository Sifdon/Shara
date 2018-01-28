package org.seemsGood.shara.view.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;

public class MapFragment extends SupportMapFragment {

    private OnMapTouchListener listener;
    TouchLayout touchLayout;

    static int wi;

    public static MapFragment newInstance(GoogleMapOptions options, int width) {

        MapFragment fr = new MapFragment();
        Bundle b = new Bundle();
        b.putParcelable("MapOptions", options);
        fr.setArguments(b);
        wi = width;
        return fr;

    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {

        View layout = super.onCreateView(layoutInflater, viewGroup, bundle);

        touchLayout = new TouchLayout(getContext());
        touchLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        ((ViewGroup)layout).addView(touchLayout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        int w = wi;
        int h = 2*w/3;

        Log.d("mine", "onCreateView: "+w);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w,h);
        params.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());
        params.bottomMargin =(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());

        layout.setLayoutParams(params);

        layout.setVisibility(View.GONE);

        return layout;
    }

    public void setOnMapTouchListener(OnMapTouchListener l){
        listener = l;
    }

    public void setVisibility(int v){
        getView().setVisibility(v);
        touchLayout.setVisibility(v);
    }

    public interface OnMapTouchListener {
        void onTouch(boolean touch);
    }

    class TouchLayout extends FrameLayout {

        public TouchLayout(@NonNull Context context) {
            super(context);
        }

        public TouchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if(listener!=null) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        listener.onTouch(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        listener.onTouch(false);
                }
            }

            return super.dispatchTouchEvent(ev);
        }
    }
}
