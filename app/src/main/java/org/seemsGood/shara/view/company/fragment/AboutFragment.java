package org.seemsGood.shara.view.company.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.seemsGood.shara.R;
import org.seemsGood.shara.view.base.fragment.MapFragment;
import org.seemsGood.shara.model.firebase.Shop;
import org.seemsGood.shara.model.firebase.ShopAddress;
import org.seemsGood.shara.util.Session;
import org.seemsGood.shara.view.custom.ShopAddressCardView;

import java.util.List;
import java.util.Map;

public class AboutFragment extends Fragment {

    private static final String TAG = "mine";
    private Shop shop;
    static NestedScrollView scrollView;

    boolean infoExtended;



    public AboutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        scrollView = (NestedScrollView) inflater.inflate(R.layout.fragment_shop_about, container, false);
        return scrollView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        shop = Session.shop;
        configurePage();

    }

    private void configurePage() {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        getActivity().findViewById(R.id.info).setVisibility(View.VISIBLE);

        configureInfo(inflater);
        inflatePlaces(inflater);
        inflateSales(inflater);

    }

    private void configureInfo(final LayoutInflater inflater) {

        final TextView description = getActivity().findViewById(R.id.main_description);
        description.setText(shop.getDescription().length() < 83 ? shop.getDescription() : (shop.getDescription().substring(0, 80) + "..."));

        TextView rating = getActivity().findViewById(R.id.main_rating);
        rating.setText(String.valueOf(shop.getRating()));


        TextView ratingCount = getActivity().findViewById(R.id.main_rating_count);
        int r = 0;
        for(Integer i : shop.getPoints()) r+=i;
        ratingCount.setText("(" + r + ")");

        TextView maxPoints = getActivity().findViewById(R.id.max_points_percentage);
        maxPoints.setText(shop.getMaxPointsPercentage() + "%");

        inflateContacts(inflater);

        getActivity().findViewById(R.id.info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View c = getActivity().findViewById(R.id.contacts);

                if (!infoExtended) {
                    if (shop.getPhones() != null || shop.getSites() != null || shop.getEmails() != null){
                        c.setVisibility(View.VISIBLE);
                    }
                    description.setText(shop.getDescription());
                } else {
                    description.setText(shop.getDescription().length() < 83 ? shop.getDescription() : (shop.getDescription().substring(0, 80) + "..."));
                    c.setVisibility(View.GONE);
                }
                infoExtended = !infoExtended;
            }
        });


    }

    private void inflateContacts(LayoutInflater inflater){

        if (shop.getPhones() != null) {
            LinearLayout phonesContainer = getActivity().findViewById(R.id.phones_container);

            LinearLayout contact;
            TextView c0, c1 = null;
            for (Map.Entry<String, List<String>> operatorPhones : shop.getPhones().entrySet()) {
                int i = 0;
                for (String phoneNumber : operatorPhones.getValue()) {
                    if(i==0){
                        contact = (LinearLayout) inflater.inflate(R.layout.template_contact,phonesContainer,false);
                        c0 = (TextView) contact.getChildAt(0);
                        c1 =  (TextView) contact.getChildAt(1);
                        c0.setAutoLinkMask(Linkify.PHONE_NUMBERS);
                        c1.setAutoLinkMask(Linkify.PHONE_NUMBERS);
                        c0.setText(phoneNumber);
                        phonesContainer.addView(contact);
                    } else if(i==1){
                        c1.setText(phoneNumber);
                    }

                    i++;
                    if(i==2) i=0;
                }
            }
            getActivity().findViewById(R.id.contacts_phones).setVisibility(View.VISIBLE);
        }

        if (shop.getSites() != null) {

            LinearLayout websitesContainer = getActivity().findViewById(R.id.websites_container);

            LinearLayout contact;
            TextView c0, c1 = null;
            int i = 0;
            for (String website : shop.getSites()) {
                if (i == 0) {
                    contact = (LinearLayout) inflater.inflate(R.layout.template_contact, websitesContainer, false);
                    c0 = (TextView) contact.getChildAt(0);
                    c1 =  (TextView) contact.getChildAt(1);
                    c0.setAutoLinkMask(Linkify.WEB_URLS);
                    c1.setAutoLinkMask(Linkify.WEB_URLS);
                    c0.setText(website);
                    websitesContainer.addView(contact);
                } else if (i == 1) {
                    c1.setText(website);
                }

                i++;
                if (i == 2) i = 0;
            }
            getActivity().findViewById(R.id.contacts_websites).setVisibility(View.VISIBLE);

        }

        if (shop.getEmails() != null) {
            LinearLayout emailsContainer = getActivity().findViewById(R.id.emails_container);

            LinearLayout contact;
            TextView c0, c1 = null;
            int i = 0;
            for (String email : shop.getEmails()) {
                if (i == 0) {
                    contact = (LinearLayout) inflater.inflate(R.layout.template_contact, emailsContainer, false);
                    c0 = (TextView) contact.getChildAt(0);
                    c1 =  (TextView) contact.getChildAt(1);
                    c0.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
                    c1.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
                    c0.setText(email);
                    emailsContainer.addView(contact);
                } else if (i == 1) {
                    c1.setText(email);
                }

                i++;
                if (i == 2) i = 0;
            }
            getActivity().findViewById(R.id.contacts_emails).setVisibility(View.VISIBLE);
        }

    }

    private void inflatePlaces(LayoutInflater inflater){

        CardView places = getActivity().findViewById(R.id.places);
        if (shop.getAddresses() != null) {
            LinearLayout placesContainer = places.findViewById(R.id.places_container);
            for (Map.Entry<String, ShopAddress> a : shop.getAddresses().entrySet()) {
                ShopAddressCardView addressCard = (ShopAddressCardView) inflater.inflate(R.layout.template_address_card, placesContainer, false);
                addressCard.setupCard()
                        .setAddress(a.getValue(),a.getKey() , shop.getKey());
                placesContainer.addView(addressCard);
            }

            configureMap();

            places.setVisibility(View.VISIBLE);

        }

    }

    private void inflateSales(LayoutInflater inflater) {

        RecyclerView recyclerSales = getActivity().findViewById(R.id.shop_sales_recycler);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

    }

    private void configureMap(){

        GoogleMapOptions o = new GoogleMapOptions();

        o.camera(new CameraPosition(new LatLng(53.9,27.55),10,0,0))
            .zoomControlsEnabled(true);

        int w = getResources().getDisplayMetrics().widthPixels - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());

        final MapFragment fragment = MapFragment.newInstance(o,w);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.map_container,fragment)
                .commit();

        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                for(ShopAddress address : shop.getAddresses().values()) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(address.getCoordinates().getLatitude(), address.getCoordinates().getLongitude()))
                            .title(address.getPlace()));
                }

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
            }
        });

        fragment.setOnMapTouchListener(new MapFragment.OnMapTouchListener() {
            @Override
            public void onTouch(boolean touch) {
                scrollView.requestDisallowInterceptTouchEvent(touch);
            }
        });

        Button mapButton = getActivity().findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button button = (Button) v;
                if(button.getText().toString().equals("Map")){
                    fragment.setVisibility(View.VISIBLE);
                    button.setText("Hide");
                } else {
                    fragment.setVisibility(View.GONE);
                    button.setText("Map");
                }
            }
        });

    }

}
