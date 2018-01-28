package org.seemsGood.shara.view.base;

import android.os.Bundle;

public class MainActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadFragment(getIntent().getIntExtra("fragmentId",-1));
    }

}
