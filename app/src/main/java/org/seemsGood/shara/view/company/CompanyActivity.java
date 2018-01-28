package org.seemsGood.shara.view.company;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.seemsGood.shara.R;
import org.seemsGood.shara.view.base.DrawerActivity;
import org.seemsGood.shara.model.firebase.Shop;
import org.seemsGood.shara.model.realm.RealmGood;
import org.seemsGood.shara.model.realm.RealmShop;
import org.seemsGood.shara.view.company.fragment.AboutFragment;
import org.seemsGood.shara.view.company.fragment.GoodsFragment;
import org.seemsGood.shara.view.company.fragment.ReviewsFragment;
import org.seemsGood.shara.util.ImageLoader;
import org.seemsGood.shara.util.Session;
import org.seemsGood.shara.util.RequestHandler;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

public class CompanyActivity extends DrawerActivity {

	private static final String TAG = "mine";

	private static Shop shop;
	private static String key = "";
	private static String origins = "";

	private RealmResults<RealmGood> goods;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();

		shop = Session.shop;
		Log.d(TAG, "onCreate: o"+origins.equals(Session.location));
		if (shop != null && bundle.getString("key").equals(shop.getKey()) && origins.equals(Session.location)) {
			inflate();
			return;
		}

		key = bundle.getString("key");
		if (Session.location != null) {
			origins = Session.location;
		}


		String url = getString(R.string.firebase_functions) +
				"getShop?" +
				"key=" + key;

		if (!origins.equals("")) url += "&origins=" + origins;

		final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Gson gson = new Gson();

				shop = gson.fromJson(String.valueOf(response), Shop.class);

				Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
					@Override
					public void execute(Realm realm) {
						RealmShop s = realm
								.where(RealmShop.class)
								.equalTo("key", key)
								.findFirst();
						if (s == null) s = realm.createObject(RealmShop.class, key);
						shop.data = s;
					}
				});

				Session.shop = shop;
				Log.d(TAG, "onResponse: Inflate");
				inflate();

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d(TAG, "onErrorResponse: " + error.toString());
			}
		});

		RequestHandler.getInstance(this).addToRequestQueue(request);

	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu:");
		getMenuInflater().inflate(R.menu.menu_company, menu);

		MenuItem favItem = menu.findItem(R.id.menu_favorite);
		int favoriteId;

		if (shop.data.isFavorite()) favoriteId = R.drawable.ic_favorite;
		else favoriteId = R.drawable.ic_favorite_no;

		favItem.setIcon(ContextCompat.getDrawable(this, favoriteId));

		View cartActionView = menu.findItem(R.id.menu_cart).getActionView();

		final TextView cartCounter = cartActionView.findViewById(R.id.cart_layout_counter);

		goods = Realm.getDefaultInstance()
				.where(RealmGood.class)
				.equalTo("shopKey", key)
				.greaterThan("count", 0)
				.findAll();

		cartCounter.setText(String.valueOf(goods.size()));

		goods.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<RealmGood>>() {
			@Override
			public void onChange(RealmResults<RealmGood> realmGoods, OrderedCollectionChangeSet changeSet) {
				Log.d(TAG, "onChange: " + realmGoods.size());
				cartCounter.setText(String.valueOf(realmGoods.size()));
			}
		});

		cartActionView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(CompanyActivity.this, CartActivity.class);
				intent.putExtra("key", key);
				startActivity(intent);
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {

		int id = item.getItemId();

		if (id == R.id.menu_favorite) {

			Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					shop.data.changeFavorite();

					int favoriteId;

					if (shop.data.isFavorite()) favoriteId = R.drawable.ic_favorite;
					else favoriteId = R.drawable.ic_favorite_no;

					item.setIcon(ContextCompat.getDrawable(getBaseContext(), favoriteId));
				}
			});

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(goods!=null) {
			goods.removeAllChangeListeners();
			for (RealmGood g : goods) {
				g.removeAllChangeListeners();
			}
		}
	}

	private void inflate() {

		findViewById(R.id.progress_bar).setVisibility(View.GONE);

		FrameLayout fl = (FrameLayout) findViewById(R.id.drawer_container);
		getLayoutInflater().inflate(R.layout.activity_shop, fl, true);

		((FloatingActionButton) findViewById(R.id.fab_sort)).hide();

		Toolbar toolbar = (Toolbar) findViewById(R.id.shop_toolbar);
//		setToolbar(toolbar, this);
		toolbar.setTitle(shop.getName());

		final ViewPager viewPager = (ViewPager) findViewById(R.id.shop_view_pager);
		viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
		viewPager.setOffscreenPageLimit(3);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.shop_tabs);
		tabLayout.setupWithViewPager(viewPager);

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					((FloatingActionButton) findViewById(R.id.fab_sort)).hide();
				} else {
					((FloatingActionButton) findViewById(R.id.fab_sort)).show();
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_DRAGGING) {
					((FloatingActionButton) findViewById(R.id.fab_sort)).hide();
				} else if (state == ViewPager.SCROLL_STATE_IDLE) {
					if (viewPager.getCurrentItem() != 0) {
						((FloatingActionButton) findViewById(R.id.fab_sort)).show();
					}
				}
			}
		});

		String ref = "companies/" + key + "/gallery/main.png";

		ImageLoader.loadImage((ImageView) findViewById(R.id.company_image), ref);
	}

	private class TabsAdapter extends FragmentPagerAdapter {

		TabsAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			switch (position) {
				case 0:
					return new AboutFragment();
				case 1:
					return new GoodsFragment();
				case 2:
					return new ReviewsFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return "About";
				case 1:
					return "Goods";
				case 2:
					return "Reviews";
			}
			return null;
		}
	}

}