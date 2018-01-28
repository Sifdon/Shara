package org.seemsGood.shara.view.drawer;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seemsGood.shara.R;
import org.seemsGood.shara.view.base.fragment.MapFragment;
import org.seemsGood.shara.model.TypeDataTEST;
import org.seemsGood.shara.model.firebase.ShopMain;
import org.seemsGood.shara.view.company.CompanyActivity;
import org.seemsGood.shara.util.RequestHandler;
import org.seemsGood.shara.util.Session;
import org.seemsGood.shara.view.custom.ShopCardView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.seemsGood.shara.util.Session.location;
import static org.seemsGood.shara.util.Session.types;
import static org.seemsGood.shara.util.Session.typesData;

public class CompaniesTESTFragment extends Fragment {

	private static final String TAG = "mine";
	private static final int SHOPS_LOAD_COUNT = 8;

	private DatabaseReference reference;

	private TypesAdapter adapter;
	private ArrayAdapter<String> searchAdapter;

	private CardView searchCard;
	private CardView searchListCard;
	private ListView searchList;
	private SearchView searchView;
	private ConstraintLayout sortLayout;
	private FloatingActionButton fabSort;
	private Spinner categoriesSpinner;

	private String openedType;

	private boolean loading;
	private boolean sorting;
	private boolean searching;

	private boolean[] settings = {false, false, false, false};

	private List<String> newKeys = new ArrayList<>();

	//filter
	private boolean hideClosed = false;
	private boolean showTimes = false;
	private int locationType = 0;

    private int sortType = 0;

	private List<String> nameKeys = new ArrayList<>();

	private String category = null;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
		if (navigationView != null) {
			navigationView.getMenu().findItem(R.id.nav_companies).setChecked(true);
		}
		setHasOptionsMenu(true);

		reference = FirebaseDatabase.getInstance().getReference();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_companies_test, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

//        ((DrawerActivity) getActivity()).setToolbar(toolbar, (DrawerActivity) getActivity());

        sortLayout = getActivity().findViewById(R.id.sort_card);
        searchCard = getActivity().findViewById(R.id.search_bar);
        searchView = searchCard.findViewById(R.id.search_view);
        fabSort = getActivity().findViewById(R.id.shops_fab_sort);
        categoriesSpinner = getActivity().findViewById(R.id.categories_spinner);
        searchListCard = getActivity().findViewById(R.id.search_list_card);
        searchList = getActivity().findViewById(R.id.search_list);

        searchAdapter = new ArrayAdapter<>(getContext(), R.layout.template_spinner_item);
        searchList.setAdapter(searchAdapter);

        searchList.setOnItemClickListener((adapterView, view, i, l) -> {

            if (nameKeys.size() == 0) return;

            Intent intent = new Intent(getContext(), CompanyActivity.class);
            Bundle b = new Bundle();
            b.putString("key", nameKeys.get(i));
            intent.putExtras(b);
            getContext().startActivity(intent);

        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchAdapter.clear();
                nameKeys.clear();

                if (newText.equals("")) {
                    searchAdapter.add("Results will be here");
                    return true;
                }

                List<String> keys = new ArrayList<>();
                List<String> values = new ArrayList<>();

                for (Map.Entry<String, String> name : typesData.get(openedType).names.entrySet()) {
                    if (name.getValue().toLowerCase().contains(newText.toLowerCase())) {
                        keys.add(name.getKey());
                        values.add(name.getValue());
                    }
                }

                if (keys.size() == 0) {
                    searchAdapter.add("No such results");
                    return true;
                }
                searchAdapter.addAll(values);
                nameKeys.addAll(keys);

                return true;
            }
        });

        RecyclerView recyclerView = getActivity().findViewById(R.id.recycler_view);
        final LinearLayoutManager recyclerManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerManager);

        adapter = new TypesAdapter();
        recyclerView.setAdapter(adapter);

        searchView.setOnCloseListener(() -> {
            searchAnimation();
            nameKeys.clear();
            return false;
        });

        fabSort.setOnClickListener(v -> {

            newSortAnimation();

//                sortAnimation();
//                if (sorting && searching) searchAnimation();
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fabSort.hide();
                } else if (dy < 0) {
                    fabSort.show();
                }
                int last = recyclerManager.findLastVisibleItemPosition();
                if (types.size() != 0) {

                    int size = typesData.get(openedType).shops.size();
                    List<String> keys = new ArrayList<>(typesData.get(openedType).shops.keySet());

                    Log.d(TAG, "onScrolledLAST: " + last);
                    Log.d(TAG, "onScrolledKEYS: " + keys.size());
                    Log.d(TAG, "onScrolledSIZE: " + size);

                    if (last > size - 3 &&
                            !loading &&
                            keys.size() > size) {

                        adapter.startLoading();
                        loading = true;
                        Log.d(TAG, "onScrolled: +++");
                        String keysString = getNextStringKeys();

                        getShops(keysString);
                    }
                }
            }
        });

        if (types.size() == 0) {
            reference.child("constants/types")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                Session.types = dataSnapshot.getValue(new GenericTypeIndicator<Map<String, String>>() {
                                });
                                configureTypes();
                            } else {
                                Log.d(TAG, "no types");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        } else {
            configureTypes();
        }

        configureSortCard();

    }

    private void newSortAnimation() {

		sorting = !sorting;

		final RelativeLayout items = getActivity().findViewById(R.id.sort_items);

		final int[] heights = new int[items.getChildCount()];
		for(int i = 0; i < items.getChildCount(); i++){
			final ViewGroup v = (ViewGroup) items.getChildAt(i);
			final int finalI = i;

			v.animate()
					.alpha(sorting ?1:0)
					.setStartDelay(sorting ? i*100 : 0)
					.setDuration(500)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							if(!sorting) v.setVisibility(View.GONE);
							heights[finalI] = items.getHeight() - (items.getChildCount()-1) * items.getChildAt(finalI).getHeight();
						}

						@Override
						public void onAnimationStart(Animator animation) {
							if(sorting) v.setVisibility(View.VISIBLE);
						}
					});

			v.getChildAt(1).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					settings[finalI] = !settings[finalI];

					v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

					ResizeAnimation animation = new ResizeAnimation(v, settings[finalI], heights[finalI]);
					animation.setDuration(500);
					v.startAnimation(animation);

					for(int i0 = 0; i0 < items.getChildCount(); i0++){
						if(i0!=finalI && settings[i0]) {
							settings[i0] = false;
							ResizeAnimation anim = new ResizeAnimation((ViewGroup) items.getChildAt(i0), false, heights[i0]);
							anim.setDuration(500);
							items.getChildAt(i0).startAnimation(anim);
						}
					}
				}
			});

		}

		getActivity().findViewById(R.id.background_frame).animate()
				.alpha(sorting ? 1 : 0)
				.setDuration(500)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationStart(Animator animation) {
						getActivity().findViewById(R.id.background_frame).setVisibility(View.VISIBLE);
					}
				});
	}

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_companies, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.search) {
			searchAnimation();
			if (sorting && searching) sortAnimation();
		}
		return false;
	}

	private void sortAnimation(){

		sorting = !sorting;

		DisplayMetrics d = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(d);
		int screenHeight = d.heightPixels;

		int start = sorting ?-screenHeight:0;
		int end = sorting ?0:-screenHeight;

		TranslateAnimation animation = new TranslateAnimation(0,0,start,end);
		animation.setDuration(300);

		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				if (sorting) sortLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (!sorting) sortLayout.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		sortLayout.startAnimation(animation);

	}

	private void searchAnimation() {

		searching = !searching;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

			int width = searchCard.getWidth();
			width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2;

			int cx = width, cy = searchCard.getHeight() / 2;

			float x = searching?0:width;
			float y = searching?width:0;

			Animator anim = ViewAnimationUtils.createCircularReveal(searchCard, cx, cy, x,y);
			anim.setDuration(300);

			anim.addListener(new AnimatorListenerAdapter() {

				@Override
				public void onAnimationStart(Animator animation) {
					if (searching) {
						searchView.setIconified(false);
						searchCard.setVisibility(View.VISIBLE);
						searchAdapter.clear();
						searchAdapter.add("Results will be here");
						searchListCard.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					if (!searching){
						searchCard.setVisibility(View.INVISIBLE);
						searchListCard.setVisibility(View.GONE);
					}
				}
			});

			anim.start();
		} else {

			final int start = searching?-searchCard.getHeight():0;
			int end = searching?0:-searchCard.getHeight();

			TranslateAnimation animation = new TranslateAnimation(0,0,start,end);
			animation.setDuration(300);
			animation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					if(searching){
						searchView.setIconified(false);
						searchCard.setVisibility(View.VISIBLE);
						searchAdapter.clear();
						searchAdapter.add("Results will be here");
						searchListCard.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					if(!searching) {
						searchCard.setVisibility(View.INVISIBLE);
						searchListCard.setVisibility(View.GONE);
					}

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}
			});

			searchCard.startAnimation(animation);
		}

	}

	private void configureSortCard() {

		sortLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		final Switch locationSwitch = getActivity().findViewById(R.id.enable_location_switch);
		Switch hideClosedSwitch = getActivity().findViewById(R.id.hide_closed_switch);
		final RadioGroup sortGroup = getActivity().findViewById(R.id.sorting_group);
		final TextView changeButton = getActivity().findViewById(R.id.sort_location_change_button);

		final FrameLayout locationFrame = getActivity().findViewById(R.id.location_card_frame);
		final CardView locationCard = getActivity().findViewById(R.id.location_card);
		final RadioGroup locationGroup = getActivity().findViewById(R.id.location_group);
		final LinearLayout locationAddressContainer = getActivity().findViewById(R.id.location_address_container);
		final FrameLayout locationMapContainer = getActivity().findViewById(R.id.location_map_container);
		ImageView locationCheck = getActivity().findViewById(R.id.location_check_button);

		sortGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
				switch (i) {
					case R.id.sort_alphabet:
						sortType = 0;
						break;
					case R.id.sort_rating:
						sortType = 1;
						break;
					case R.id.sort_popularity:
						sortType = 2;
						break;
					case R.id.sort_sales:
						sortType = 3;
						break;
					case R.id.sort_time_to:
						sortType = 4;
						break;
				}
				updateKeys();
			}
		});


		GoogleMapOptions options = new GoogleMapOptions();

		options.camera(new CameraPosition(new LatLng(53.9, 27.55), 10, 0, 0))
				.zoomControlsEnabled(true);

		int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());

		final MapFragment fragment = MapFragment.newInstance(options, w);
		getChildFragmentManager()
				.beginTransaction()
				.add(R.id.location_map_container, fragment)
				.commit();

		fragment.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(final GoogleMap googleMap) {

				fragment.setVisibility(View.VISIBLE);

				googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
					@Override
					public void onMapClick(LatLng latLng) {
						googleMap.clear();
						googleMap.addMarker(new MarkerOptions().position(latLng).title("Location"));
						location = latLng.latitude + "," + latLng.longitude;
						locationType = 3;
					}
				});

				if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					return;
				}

				googleMap.setMyLocationEnabled(true);

			}
		});


		locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					locationFrame.animate()
							.setDuration(300)
							.alpha(1)
							.setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationStart(Animator animation) {
									locationFrame.setVisibility(View.VISIBLE);
								}
							});
					fabSort.hide();

					locationGroup.findViewById(R.id.location_current);

					int id = 0;

					if (locationType == 1) {
						id = R.id.location_current;
						locationAddressContainer.setVisibility(View.GONE);
						locationMapContainer.setVisibility(View.GONE);
						locationType = 1;
					} else if (locationType == 2) {
						id = R.id.location_address;
						locationAddressContainer.setVisibility(View.VISIBLE);
						locationMapContainer.setVisibility(View.GONE);
					} else if (locationType == 3) {
						id = R.id.location_map;
						locationAddressContainer.setVisibility(View.GONE);
						locationMapContainer.setVisibility(View.VISIBLE);
					}

					locationGroup.clearCheck();
					if (id != 0) locationGroup.check(id);

				} else {
					location = null;
					showTimes = false;
					locationType = 0;

					sortGroup.findViewById(R.id.sort_time_to).setEnabled(false);
					locationGroup.clearCheck();

					locationAddressContainer.setVisibility(View.GONE);
					locationMapContainer.setVisibility(View.GONE);

					changeButton.setVisibility(View.GONE);

					updateKeys();
				}
			}
		});

		hideClosedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				hideClosed = b;
			}
		});

		changeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				locationFrame.animate()
						.setDuration(300)
						.alpha(1)
						.setListener(new AnimatorListenerAdapter() {
							@Override
							public void onAnimationStart(Animator animation) {
								locationFrame.setVisibility(View.VISIBLE);

							}
						});
				fabSort.hide();
			}
		});

		locationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
				if (i == R.id.location_current) {
					locationAddressContainer.setVisibility(View.GONE);
					locationMapContainer.setVisibility(View.GONE);
					locationType = 1;
				} else if (i == R.id.location_address) {
					locationAddressContainer.setVisibility(View.VISIBLE);
					locationMapContainer.setVisibility(View.GONE);
					//locationType = 2;
				} else if (i == R.id.location_map) {
					locationAddressContainer.setVisibility(View.GONE);
					locationMapContainer.setVisibility(View.VISIBLE);
				}
			}
		});

		locationCheck.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (locationType != 0) {
					showTimes = true;
					locationFrame.animate()
							.setDuration(300)
							.alpha(0)
							.setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(Animator animation) {
									locationFrame.setVisibility(View.GONE);
								}
							});
					fabSort.show();

					sortGroup.findViewById(R.id.sort_time_to).setEnabled(true);

					changeButton.setVisibility(View.VISIBLE);

					for (String type : types.keySet()) {
						if(typesData.containsKey(type)) {
							typesData.get(type).loaded = new HashMap<>();
						}
					}

					if (locationType == 1) {
						getCurrentLocation();
					} else if (locationType == 3) {
						updateKeys();
					}

				}
			}
		});

		locationFrame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				locationFrame.animate()
						.setDuration(300)
						.alpha(0)
						.setListener(new AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(Animator animation) {
								locationFrame.setVisibility(View.GONE);
							}
						});
				fabSort.show();
				if (!showTimes) {

					locationAddressContainer.setVisibility(View.GONE);
					locationMapContainer.setVisibility(View.GONE);

					locationGroup.clearCheck();
					locationType = 0;
					locationSwitch.setChecked(false);
				}
			}
		});

		locationCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

	}

	private void configureTypes() {

		Spinner spinner = getActivity().findViewById(R.id.types_spinner);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.template_spinner_item_head, new ArrayList<>(types.values()));
		spinnerAdapter.setDropDownViewResource(R.layout.template_spinner_item);
		spinner.setAdapter(spinnerAdapter);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int type, long id) {
				adapter.clear();
				openedType = (String) types.keySet().toArray()[type];
				if (!typesData.containsKey(openedType)) {
					typesData.put(openedType, new TypeDataTEST());
					getShopKeys();
				} else {
					adapter.setShops(typesData.get(openedType).shops.values());
				}

				if(!typesData.containsKey(openedType)){
					reference.child("constants/categories/"+openedType).addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							List<String> values = new ArrayList<>();
							values.add("Все категории");
							if(dataSnapshot != null) {
								typesData.get(openedType).categories.putAll(dataSnapshot.getValue(new GenericTypeIndicator<Map<String, String>>() {
								}));
								values.addAll(typesData.get(openedType).categories.values());
							}

							ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.template_spinner_item, values);
							adapter.setDropDownViewResource(R.layout.template_spinner_item);
							categoriesSpinner.setAdapter(adapter);
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {

						}
					});
				} else {
					List<String> values = new ArrayList<>();
					values.add("Все категории");
					values.addAll(typesData.get(openedType).categories.values());
					ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.template_spinner_item_head, values);
					adapter.setDropDownViewResource(R.layout.template_spinner_item);
					categoriesSpinner.setAdapter(adapter);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if(i==0){
					category = null;
				} else {
					category = typesData.get(openedType).categories.keySet().toArray()[i-1].toString();
				}
				updateKeys();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

	}

	private String getNextStringKeys(){

		newKeys.clear();
        int size = typesData.get(openedType).shops.size();//8
        List<String> keys = typesData.get(openedType).keys;//20

        int endI = Math.min(size + 8, keys.size());

        Log.d(TAG, "getNextStringKeys: "+size);
        Log.d(TAG, "getNextStringKeys: "+keys.size());
        Log.d(TAG, "getNextStringKeys: "+endI);

        String keysString = "[";
        for (int i = size; i < endI; i++) {
			newKeys.add(keys.get(i));
			Log.d(TAG, "getNextStringKeys: "+keys.get(i));
			if(!typesData.get(openedType).loaded.containsKey(keys.get(i)))
				keysString += "\"" + keys.get(i) + "\",";
		}
        keysString = keysString.substring(0, keysString.length() - 1) + "]";

		if(keysString.equals("]")) {
			keysString = "[]";

			adapter.stopLoading();

			for(String key : newKeys){
				ShopMain s = typesData.get(openedType).loaded.get(key);
				typesData.get(openedType).shops.put(key,s);
				adapter.addShop(s);
			}
		}

		Log.d(TAG, "getNextStringKeys: new: "+newKeys.toString());
		Log.d(TAG, "getNextStringKeys: load: "+keysString);

        return keysString;

    }

	private void getShopKeys() {

		String url = getString(R.string.firebase_functions) +
				"sortShops" +
				"?type=" + openedType +
				"&count=" + SHOPS_LOAD_COUNT +
                "&sort=" +sortType;

		if (location != null) url += "&origins=" + location;
		if (category != null) url += "&category=" + category;

		Log.d(TAG, "getShopKeys: "+url);

		final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {

						try {

							for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                typesData.get(openedType).names.put(obj.getString("key"), obj.getString("name"));
                                typesData.get(openedType).keys.add(obj.getString("key"));
							}

						} catch (JSONException ignored) {
						}


                        String keysString = getNextStringKeys();

                        getShops(keysString);

					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						error.printStackTrace();
						Log.d(TAG, "onErrorResponse: "+error.toString());
					}
				});

		RequestHandler.getInstance(getContext())
				.cancelAll()
				.addToRequestQueue(request);
	}

	private void getShops(String keys) {

		if(keys.equals("[]")) return;

		String url = getString(R.string.firebase_functions) +
				"getShops?" +
				"keys=" + keys;

		if (location != null) url += "&origins=" + location;

		Log.d(TAG, "getShops: "+url);

		final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						adapter.stopLoading();
						Gson gson = new Gson();

						try {
							Iterator<String> iterator = response.keys();
							while(iterator.hasNext()){
								String key = iterator.next();
								ShopMain shop = gson.fromJson(response.getString(key), ShopMain.class);
								shop.setKey(key);
								typesData.get(openedType).loaded.put(key, shop);
							}

							for(String key : newKeys){
								ShopMain s = typesData.get(openedType).loaded.get(key);
								typesData.get(openedType).shops.put(key,s);
								adapter.addShop(s);
							}

						} catch (JSONException ignored) {
						}
						loading = false;
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						error.printStackTrace();
					}
				});

		RequestHandler.getInstance(getContext())
				.cancelAll()
				.addToRequestQueue(request);
	}

	private void getCurrentLocation() {
        final FusedLocationProviderClient fusedLocation = LocationServices.getFusedLocationProviderClient(getActivity());

        LocationRequest request = new LocationRequest();
        request.setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getCurrentLocation: nope");
            return;
        }
		adapter.clear();
        Log.d(TAG, "getCurrentLocation: yeap");

        fusedLocation.requestLocationUpdates(request, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                Location loc = locationResult.getLocations().get(0);

                double lat = loc.getLatitude();
                double lng = loc.getLongitude();

                location = lat + "," + lng;

				updateKeys();

                if (locationResult.getLocations().size() != 0) {
                    fusedLocation.removeLocationUpdates(this);
                }

            }

        }, null);

    }

    private void updateKeys(){

		adapter.clear();

		for (String type : types.keySet()) {
			if(typesData.containsKey(type)) {
				typesData.get(type).shops = new HashMap<>();
				typesData.get(type).names = new HashMap<>();
				typesData.get(type).keys = new ArrayList<>();
			}
		}

		getShopKeys();

	}

	private class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagHolder> {

		List<String> tags;

		class TagHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
			TextView tag;
			TagHolder(View itemView) {
				super(itemView);
				tag = itemView.findViewById(R.id.tag);
				itemView.setOnClickListener(this);
			}

			@Override
			public void onClick(View view) {
				((CardView)view).setCardBackgroundColor(Color.GRAY);
			}
		}

		TagsAdapter(List<String> tags) {
			this.tags = tags;
		}

		@Override
		public TagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(getContext()).
					inflate(R.layout.template_tag,parent,false);
			return new TagHolder(v);
		}

		@Override
		public void onBindViewHolder(TagHolder holder, int position) {
			holder.tag.setText(tags.get(position));
		}

		@Override
		public int getItemCount() {
			return tags.size();
		}
	}

	private class TypesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

		List<ShopMain> shops;

		TypesAdapter() {
			shops = new ArrayList<>();
			clear();
		}

		class LoadingHolder extends RecyclerView.ViewHolder{
			ProgressBar loading;
			LoadingHolder(View itemView) {
				super(itemView);
				loading = itemView.findViewById(R.id.progress_bar);
			}
		}

		class ShopHolder extends RecyclerView.ViewHolder{
			ShopCardView card;
			ShopHolder(View itemView) {
				super(itemView);
				card = itemView.findViewById(R.id.company_card);
			}
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			RecyclerView.ViewHolder vh;
			View v;
			if(viewType == 0){
				v = LayoutInflater.from(getContext()).
						inflate(R.layout.template_loading, parent, false);
				vh = new LoadingHolder(v);
			} else {
				v = LayoutInflater.from(getContext()).
						inflate(R.layout.template_company, parent, false);
				vh = new ShopHolder(v);
			}
			return vh;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			if (holder instanceof ShopHolder) {
				ShopMain shop = shops.get(position);
				((ShopHolder) holder).card.setupCard()
						.setShop(shop)
						.setImage("shops/" + shop.getKey() + "/gallery/main.png");
			} else if(holder instanceof LoadingHolder){
				((LoadingHolder) holder).loading.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public int getItemViewType(int position) {
			return shops.get(position)==null ? 0 : 1;
		}

		@Override
		public int getItemCount() {
			return shops.size();
		}

		void setShops(Collection<ShopMain> c){
            Log.d(TAG, "setShops: "+c.size());
            shops = new ArrayList<>(c);
			notifyDataSetChanged();
		}

		void addShop(ShopMain c){
            Log.d(TAG, "addShop: "+c.getKey());
            shops.add(c);
			notifyItemInserted(shops.size());
		}

		void startLoading(){
            Log.d(TAG, "startLoading: ");
            shops.add(null);
			notifyItemInserted(shops.size());
		}

		void stopLoading(){
            Log.d(TAG, "stopLoading: ");
            shops.remove(shops.size()-1);
			notifyItemRemoved(shops.size());
		}

		void clear(){
            Log.d(TAG, "clear: ");
            shops.clear();
			shops.add(null);
			notifyDataSetChanged();
		}
	}

	class ResizeAnimation extends Animation {

		private View text;
		private View fab;
		private View card;

		private float deltaCardWidth;
		private float deltaCardHeight;

		private float startTextX;
		private float deltaTextX;

		private float startFabX;
		private float deltaFabX;

		private boolean expand;
		private boolean cardStart;

		private float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
		private float delta;

		private float max;

		ResizeAnimation(ViewGroup parent, boolean b, float height) {

			expand = b;

			this.text = parent.getChildAt(0);
			this.fab = parent.getChildAt(1);
			this.card = parent.getChildAt(2);

			delta = parent.getWidth() - 48 * dp;

			startTextX = text.getX();
			startFabX = fab.getX();

			deltaTextX = -text.getWidth() - startTextX - 8 * dp;
			deltaFabX = 4 * dp - startFabX;
			deltaCardWidth = parent.getWidth() - 52 * dp;
			deltaCardHeight = height - 40 * dp;
			max = deltaCardWidth - 4 * dp;

			if (!expand) {
				deltaTextX += delta;
				deltaFabX += delta;
			}

			cardStart = !expand;

		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {

			text.setAlpha((expand ? 1 - interpolatedTime : interpolatedTime));
			text.setX(startTextX + deltaTextX * interpolatedTime);
			fab.setX(startFabX + deltaFabX * interpolatedTime);

			if (expand) {
				if (!cardStart && card.getX() - fab.getX() > 48 * dp) {
					cardStart = true;
				}
			} else {
				if (cardStart && deltaCardWidth - fab.getX() < 40 * dp) {
					cardStart = false;
					card.getLayoutParams().width = (int) (40 * dp);
					card.getLayoutParams().height = (int) (40 * dp);
					card.requestLayout();
				}
			}
			if (cardStart) {
				card.getLayoutParams().width = (int) (deltaCardWidth - fab.getX());
				card.getLayoutParams().height = (int) (40*dp+(deltaCardWidth - fab.getX()-40*dp) / max * deltaCardHeight);
				card.requestLayout();
			}
		}

	}

}
