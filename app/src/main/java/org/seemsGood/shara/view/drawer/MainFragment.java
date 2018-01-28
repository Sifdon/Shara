package org.seemsGood.shara.view.drawer;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import org.seemsGood.shara.R;
import org.seemsGood.shara.view.base.DrawerActivity;

import butterknife.BindView;

public class MainFragment extends Fragment{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @CallSuper
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((DrawerActivity)getActivity()).configureToolbarDrawer(toolbar);
    }
}
