package org.seemsGood.shara.view.company;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.seemsGood.shara.R;
import org.seemsGood.shara.view.base.DrawerActivity;
import org.seemsGood.shara.view.base.MainActivity;
import org.seemsGood.shara.model.firebase.Review;
import org.seemsGood.shara.util.CalendarUtil;
import org.seemsGood.shara.util.Session;

public class ReviewActivity extends DrawerActivity {

	int points;
	String key;
	String address;

	Toolbar toolbar;

	DatabaseReference databaseReference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		key = Session.shop.getKey();
		address = getIntent().getStringExtra("address");

		databaseReference = FirebaseDatabase.getInstance().getReference();

		FrameLayout fl = (FrameLayout) findViewById(R.id.drawer_container);
		getLayoutInflater().inflate(R.layout.activity_review,fl,true);
		toolbar = (Toolbar) findViewById(R.id.toolbar_review);

//		setToolbar(toolbar,this);

		toolbar.setTitle("Review");

        findViewById(R.id.progress_bar).setVisibility(View.GONE);

		final LinearLayout starsContainer = (LinearLayout) findViewById(R.id.overall_stars_rating);

		for(int i = 0; i < 5; i++){
			ImageView star = (ImageView) starsContainer.getChildAt(i);
			final int finalI = i;
			star.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					for(int j = 0; j < finalI+1; j++){
						((ImageView) starsContainer.getChildAt(j)).setImageDrawable(getResources().getDrawable(R.drawable.ic_star_full));
					}
					for(int j = finalI+1;j<5;j++){
						((ImageView) starsContainer.getChildAt(j)).setImageDrawable(getResources().getDrawable(R.drawable.ic_star_empty));
					}
					points = finalI+1;
					Log.d("mine","Points: "+points);
				}
			});
		}

		Button sendButton = (Button) findViewById(R.id.send_review);

		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {


				String date = CalendarUtil.getDate();
				String head;
				String description;
				String pluses;
				String minuses;

				head = ((TextView)findViewById(R.id.head_value)).getText().toString();
				description = ((TextView)findViewById(R.id.description_value)).getText().toString();
				pluses = ((TextView)findViewById(R.id.pluses_value)).getText().toString();
				minuses = ((TextView)findViewById(R.id.minuses_value)).getText().toString();

                String s;
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    s = "EXAMPLE_TEST";
                } else {
                    s = FirebaseAuth.getInstance().getCurrentUser().getUid();
                }
				Review review = new Review(head,points,pluses,minuses,description,date,address, s);

				final DatabaseReference ref = databaseReference.child("companies/reviews/"+key).push();
				ref.setValue(review);
				Log.d("mine","suck");

				Intent intent = new Intent(ReviewActivity.this,MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(this,MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

}
