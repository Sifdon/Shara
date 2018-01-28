package org.seemsGood.shara.view.company;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.seemsGood.shara.R;
import org.seemsGood.shara.view.base.DrawerActivity;
import org.seemsGood.shara.model.firebase.Good;
import org.seemsGood.shara.model.firebase.Order;
import org.seemsGood.shara.model.firebase.Shop;
import org.seemsGood.shara.model.firebase.ShopAddress;
import org.seemsGood.shara.model.realm.RealmGood;
import org.seemsGood.shara.util.Session;
import org.seemsGood.shara.view.custom.GoodCardView;
import org.seemsGood.shara.view.custom.ShopAddressCardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

public class CartActivity extends DrawerActivity {

    private static final String TAG = "mine";

    private String key;
    private Shop shop;
    private Order order;

    private DatabaseReference goodsReference;

    private RealmResults<RealmGood> goodsData;
    private List<Good> goods = new ArrayList<>();

    private GoodsCartAdapter adapter;

    private TextView orderButton;
    private TextView cartPrice;
    private TextView itemsCount;
    private FrameLayout addGoods;
    private RelativeLayout orderContainer;

    private LinearLayout shopAddresses;
    private LinearLayout userAddresses;

    private TextView timeChooser;
    private TextView dateChooser;
    private SeekBar pointsSeekBar;
    private TextView points;
    private TextView pointsKind;
    private TextView correctedPrice;

    private FrameLayout payLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout fl = (FrameLayout) findViewById(R.id.drawer_container);
        getLayoutInflater().inflate(R.layout.activity_cart, fl, true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setToolbar(toolbar, this);

        shop = Session.shop;
        key = shop.getKey();
        goodsReference = FirebaseDatabase
                .getInstance()
                .getReference("companies/goods/" + key);

        order = new Order();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            order.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } else {
            order.setUserId("EXAMPLE_TEST");
        }

        toolbar.setTitle("Cart: " + shop.getName());

        payLayout = (FrameLayout) findViewById(R.id.pay_layout);
        cartPrice = (TextView) findViewById(R.id.cart_price);
        itemsCount = (TextView) findViewById(R.id.cart_items_count);
        addGoods = (FrameLayout) findViewById(R.id.add_goods_card);
        orderContainer = (RelativeLayout) findViewById(R.id.order_container);
        orderButton = (TextView) findViewById(R.id.cart_order_button);

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i;
                String s;
                if (orderContainer.getVisibility() == View.VISIBLE) {
                    i = View.GONE;
                    s = "Order";
                } else {
                    i = View.VISIBLE;
                    s = "Back";
                }
                invalidateOptionsMenu();

                orderContainer.setVisibility(i);
                orderButton.setText(s);
            }
        });

        configureOrderLayout();

        RecyclerView goodCartRecycler = (RecyclerView) findViewById(R.id.cart_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        goodCartRecycler.setLayoutManager(manager);

        adapter = new GoodsCartAdapter();
        goodCartRecycler.setAdapter(adapter);

        goodsData = Realm.getDefaultInstance()
                .where(RealmGood.class)
                .equalTo("shopKey", key)
                .greaterThan("count", 0)
                .findAll();

        itemsCount.setText("Items: " + goodsData.size());

        goodsData.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<RealmGood>>() {
            @Override
            public void onChange(RealmResults<RealmGood> realmGoods, OrderedCollectionChangeSet changeSet) {

                float p = calcPrice();
                if(pointsSeekBar.getProgress()!=0) {
                    float cp = p - (float) pointsSeekBar.getProgress() / 100;
                    correctedPrice.setText(cp + "p");
                    points.setText(String.valueOf(pointsSeekBar.getProgress()));
                } else {
                    correctedPrice.setText(p + "p");
                    points.setText(String.valueOf((int) (p)));
                }

                itemsCount.setText("Items: " + realmGoods.size());
                cartPrice.setText("Price: " + p);
                if (changeSet.getInsertions().length == 1) {
                    final RealmGood good = realmGoods.get(changeSet.getInsertions()[0]);

                    goodsReference
                            .child(good.getKindKey())
                            .child(good.getKey())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Good g = dataSnapshot.getValue(new GenericTypeIndicator<Good>() {
                                    });
                                    if (g == null) {
                                        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                good.deleteFromRealm();
                                                Log.d(TAG, "execute: " + goodsData.size());
                                            }
                                        });
                                        return;
                                    }
                                    g.data = good;
                                    goods.add(g);
                                    adapter.addGood(g);

                                    float p = calcPrice();
                                    if(pointsSeekBar.getProgress()!=0) {
                                        float cp = p - (float) pointsSeekBar.getProgress() / 100;
                                        correctedPrice.setText(cp + "p");
                                        points.setText(String.valueOf(pointsSeekBar.getProgress()));
                                    } else {
                                        correctedPrice.setText(p + "p");
                                        points.setText(String.valueOf((int) (p)));
                                    }

                                    cartPrice.setText("Price: " + p);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                }
            }
        });

        loadGoods();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);

        if (orderContainer.getVisibility() == View.VISIBLE) {
            menu.findItem(R.id.menu_pay).setVisible(true);
            menu.findItem(R.id.menu_add).setVisible(false);
        } else {
            menu.findItem(R.id.menu_pay).setVisible(false);
            menu.findItem(R.id.menu_add).setVisible(true);
        }

        int id;
        if(addGoods.getVisibility() == View.VISIBLE) {
            id = R.drawable.ic_close;
        } else {
            id = R.drawable.ic_add;
        }

        menu.findItem(R.id.menu_add).setIcon(ContextCompat.getDrawable(this, id));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            int id;
            if (addGoods.getVisibility() == View.GONE) {
                addGoods.setVisibility(View.VISIBLE);
                addGoods.animate()
                        .alpha(1)
                        .setDuration(250)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ((FloatingActionButton) findViewById(R.id.fab_sort)).show();
                            }
                        });
                id = R.drawable.ic_close;
            } else {
                ((FloatingActionButton) findViewById(R.id.fab_sort)).hide();
                addGoods.animate()
                        .alpha(0)
                        .setDuration(250)
                        .setStartDelay(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                addGoods.setVisibility(View.GONE);
                            }
                        });
                id = R.drawable.ic_add;
            }
            item.setIcon(ContextCompat.getDrawable(this, id));
        } else if (item.getItemId() == R.id.menu_pay){

            if(order.getAddress()==null) return super.onOptionsItemSelected(item);

            float cp = calcPrice()-(float) pointsSeekBar.getProgress()/100;
            Log.d(TAG, "onOptionsItemSelected: "+cp);

            Realm.getDefaultInstance().beginTransaction();
            for(RealmGood g : goodsData){
                g.setCount(0);
            }
            Realm.getDefaultInstance().commitTransaction();
            order.setFullCost(cp);

            FirebaseDatabase
                    .getInstance()
                    .getReference("companies/orders/" + key)
                    .push()
                    .setValue(order);

            payLayout.setVisibility(View.VISIBLE);
            payLayout.animate()
                    .alpha(1)
                    .setDuration(500);

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {

                    payLayout.findViewById(R.id.paying_progress_bar).setVisibility(View.GONE);
                    payLayout.findViewById(R.id.paying_check).setVisibility(View.VISIBLE);
                    ((TextView) payLayout.findViewById(R.id.paying_text)).setText("Payment\nsuccessful");

                    payLayout.animate()
                            .alpha(0)
                            .setStartDelay(2000)
                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    Intent intent = new Intent(CartActivity.this, ReviewActivity.class);
                                    intent.putExtra("addressvbncn",order.getAddress());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                }
                            });
                }
            },4000);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(addGoods.getVisibility() == View.VISIBLE){

            ((FloatingActionButton) findViewById(R.id.fab_sort)).hide();
            addGoods.animate()
                    .alpha(0)
                    .setDuration(250)
                    .setStartDelay(100)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            addGoods.setVisibility(View.GONE);
                            invalidateOptionsMenu();
                        }
                    });
        } else if(orderContainer.getVisibility() == View.VISIBLE){
            invalidateOptionsMenu();

            orderContainer.setVisibility(View.GONE);
            orderButton.setText("Order");
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        goodsData.removeAllChangeListeners();
    }

    private float calcPrice() {
        float price = 0;
        Good g = null;
        for (Good good : goods) {
            if (good.data.getCount() == 0) {
                g = good;
            } else {
                price += good.getPrice() * good.data.getCount();
            }
        }
        if (g != null) {
            goods.remove(g);
            adapter.removeGood(g);

        }
        return price;
    }

    private void loadGoods() {

        if (goodsData.size() == 0) {
            completeLoad();
            return;
        }

        for (final RealmGood good : goodsData) {
            Log.d(TAG, "loadGoods: " + good.getKey() + "  " + good.getKindKey());
            goodsReference
                    .child(good.getKindKey())
                    .child(good.getKey())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: "+dataSnapshot.getRef().toString());
                            Good g = dataSnapshot.getValue(new GenericTypeIndicator<Good>() {
                            });
                            if (g == null) {
                                Log.d(TAG, "onNULL: "+good.toString());
                                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        good.deleteFromRealm();
                                        Log.d(TAG, "execute: " + goodsData.size());
                                    }
                                });
                                return;
                            }
                            g.data = good;
                            goods.add(g);
                            if (goods.size() == goodsData.size()) {
                                adapter.addGoods(goods);
                                completeLoad();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void completeLoad() {

        float p = calcPrice();
        float cp = p-(float)pointsSeekBar.getProgress()/100;
        correctedPrice.setText(cp+"p");
        points.setText(String.valueOf((int)(p)));

        cartPrice.setText("Price: " + p);

        findViewById(R.id.cart_recycler).setVisibility(View.VISIBLE);
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        findViewById(R.id.order_card).setVisibility(View.VISIBLE);

    }

    private void configureOrderLayout() {

        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.SECOND,0);

        final int[] dates = new int[]{
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
        };



        dateChooser = (TextView) findViewById(R.id.date_chooser);
        timeChooser = (TextView) findViewById(R.id.time_chooser);
        points = (TextView) findViewById(R.id.order_points);
        pointsKind = (TextView) findViewById(R.id.order_points_kind);
        pointsSeekBar  = (SeekBar) findViewById(R.id.order_points_seek_bar);
        correctedPrice = (TextView) findViewById(R.id.order_corrected_price);

        pointsSeekBar.setMax(50);

        String date = configureDate(dates[0], dates[1] + 1, dates[2]);
        String time = configureTime(dates[3], dates[4]);

        dateChooser.setText(date);
        timeChooser.setText(time);

        order.setDate(date);
        order.setOrderDate(date);
        order.setOrderTime(time);

        dateChooser.setPaintFlags(dateChooser.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        timeChooser.setPaintFlags(timeChooser.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        dateChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                DatePickerDialog dialog = new DatePickerDialog(CartActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        int[] c = dates.clone();

                        c[0] = i2;
                        c[1] = i1;
                        c[2] = i;

                        if(getCalendarDate(c).getTimeInMillis()>calendar.getTimeInMillis()){
                            String date = configureDate(i2, i1 + 1, i);
                            dateChooser.setText(date);
                            dates[0] = i2;
                            dates[1] = i1;
                            dates[2] = i;

                            order.setOrderDate(date);

                        } else {
                            Snackbar.make(view,"Wrong date and time",Snackbar.LENGTH_SHORT).show();
                        }

                    }
                }, dates[2], dates[1], dates[0]);
                dialog.show();
            }
        });

        timeChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                TimePickerDialog dialog = new TimePickerDialog(CartActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        int[] c = dates.clone();

                        c[3] = i;
                        c[4] = i1;
                        if(getCalendarDate(c).getTimeInMillis()>calendar.getTimeInMillis()){
                            String time = configureTime(i,i1);
                            timeChooser.setText(time);
                            dates[3] = i;
                            dates[4] = i1;

                            order.setOrderTime(time);

                        } else {
                            Snackbar.make(view,"Wrong date and time",Snackbar.LENGTH_SHORT).show();
                        }

                    }
                }, dates[3], dates[4], true);
                dialog.show();
            }
        });

        shopAddresses = (LinearLayout) findViewById(R.id.chooser_shop);
        userAddresses = (LinearLayout) findViewById(R.id.chooser_user);

        LayoutInflater inflater = LayoutInflater.from(this);

        for (final Map.Entry<String, ShopAddress> address : shop.getAddresses().entrySet()) {
            final ShopAddressCardView cardView = (ShopAddressCardView) inflater.inflate(R.layout.template_address_card, shopAddresses, false);

            cardView.setupCard()
                    .setAddress(address.getValue(), address.getKey(), key)
                    .simplifyAddress();

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    order.setAddress(address.getKey());
                    order.setStatus("reserved");

                    for (int i = 0; i < shopAddresses.getChildCount(); i++) {
                        ((CardView) shopAddresses.getChildAt(i)).setCardBackgroundColor(Color.WHITE);
                    }

                    for (int i = 0; i < userAddresses.getChildCount(); i++) {
                        ((CardView) userAddresses.getChildAt(i)).setCardBackgroundColor(Color.WHITE);
                    }

                    cardView.setCardBackgroundColor(Color.argb(255, 185, 246, 202));
                }
            });

            shopAddresses.addView(cardView);
        }

        pointsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(i==0) {
                    pointsKind.setText("Receive");
                    int p = (int) (calcPrice());
                    points.setText(String.valueOf(p));
                    correctedPrice.setText(String.valueOf(calcPrice()));
                    order.setPointsReceived(p);
                    order.setPointsSpent(0);
                } else {
                    pointsKind.setText("Spend");
                    points.setText(String.valueOf(i));

                    float cp = calcPrice() - (float) i / 100;
                    correctedPrice.setText(String.valueOf(cp));

                    order.setPointsSpent(i);
                    order.setPointsReceived(0);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private Calendar getCalendarDate(int[] indexes) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND,1);
        c.set(Calendar.DAY_OF_MONTH,indexes[0]);
        c.set(Calendar.MONTH,indexes[1]);
        c.set(Calendar.YEAR,indexes[2]);
        c.set(Calendar.HOUR_OF_DAY,indexes[3]);
        c.set(Calendar.MINUTE,indexes[4]);
        return c;
    }

    private String configureDate(int d, int m, int y) {
        return (d > 9 ? "" : "0") + d + "." + (m > 9 ? "" : "0") + m + "." + y;
    }

    private String configureTime(int h, int m) {
        return (h > 9 ? "" : "0") + h + ":" + (m > 9 ? "" : "0") + m;
    }

    private class GoodsCartAdapter extends RecyclerView.Adapter<GoodsCartAdapter.GoodCartHolder> {

        List<Good> goods;

        GoodsCartAdapter() {
            goods = new ArrayList<>();
        }

        class GoodCartHolder extends RecyclerView.ViewHolder {
            GoodCardView good;

            GoodCartHolder(View itemView) {
                super(itemView);
                good = itemView.findViewById(R.id.good_cart_card);
                good.setupCard();
            }
        }

        @Override
        public GoodsCartAdapter.GoodCartHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getBaseContext()).
                    inflate(R.layout.template_good_cart, parent, false);
            return new GoodCartHolder(v);
        }

        @Override
        public void onBindViewHolder(GoodsCartAdapter.GoodCartHolder holder, int position) {
            Good good = goods.get(position);
            holder.good.configureCard(good)
                    .addKind(shop.getKinds());
        }

        @Override
        public int getItemCount() {
            return goods.size();
        }

        void removeGood(Good c) {
            int i = goods.indexOf(c);
            goods.remove(c);
            notifyItemRemoved(i);
        }

        void addGood(Good c) {
            goods.add(c);
            notifyItemInserted(goods.size());
        }

        void addGoods(List<Good> c) {
            goods.addAll(c);
            notifyDataSetChanged();
        }
    }

}