package org.seemsGood.shara.view.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import org.seemsGood.shara.R;

public class AccountActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout fl = (FrameLayout) findViewById(R.id.drawer_container);
        getLayoutInflater().inflate(R.layout.activity_account, fl, true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setToolbar(toolbar, this);

    }

}
