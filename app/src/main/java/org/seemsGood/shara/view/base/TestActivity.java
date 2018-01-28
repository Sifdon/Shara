package org.seemsGood.shara.view.base;

import android.os.Bundle;

import org.seemsGood.shara.R;

public class TestActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate(R.layout.fragment_companies);
    }
}
