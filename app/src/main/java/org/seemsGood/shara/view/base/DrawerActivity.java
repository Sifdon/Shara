package org.seemsGood.shara.view.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.seemsGood.shara.R;
import org.seemsGood.shara.view.drawer.CompaniesFragment;
import org.seemsGood.shara.view.drawer.NotificationsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class DrawerActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	@BindView(R.id.drawer_layout)
	DrawerLayout drawer;
	@BindView(R.id.nav_view)
	NavigationView navigationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer);
		ButterKnife.bind(this);

		navigationView.setNavigationItemSelectedListener(this);
	}

	protected void inflate(int layoutId){
		getLayoutInflater().inflate(layoutId, findViewById(R.id.drawer_container), true);
		if (findViewById(R.id.toolbar) != null)
			configureToolbarDrawer(findViewById(R.id.toolbar));
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START))
			drawer.closeDrawer(GravityCompat.START);
		else
			super.onBackPressed();
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {

		if (item.isChecked()) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			loadFragment(item.getItemId());
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			if (this instanceof MainActivity) {
				drawer.openDrawer(GravityCompat.START);
			} else {
				onBackPressed();
			}
		}

		return super.onOptionsItemSelected(item);
	}

	public void configureToolbarDrawer(Toolbar toolbar) {
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getSupportActionBar().setHomeAsUpIndicator(this instanceof MainActivity ?
				R.drawable.ic_menu :
				R.drawable.ic_arrow_back);

	}

	protected void loadFragment(int id) {

		if (this instanceof MainActivity) {

			Fragment fragment;
			switch (id) {
				case R.id.nav_notifications:
					fragment = new NotificationsFragment();
					break;
				default:
					id = R.id.nav_companies;
					fragment = new CompaniesFragment();
			}

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.drawer_container, fragment)
					.commit();
			drawer.closeDrawer(GravityCompat.START);
			navigationView.setCheckedItem(id);
		} else {
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("fragmentId", id);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}
	}


}