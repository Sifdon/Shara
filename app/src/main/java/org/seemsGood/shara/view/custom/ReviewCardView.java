package org.seemsGood.shara.view.custom;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.seemsGood.shara.R;
import org.seemsGood.shara.model.firebase.Review;
import org.seemsGood.shara.model.firebase.Shop;
import org.seemsGood.shara.util.ImageLoader;

public class ReviewCardView extends CardView {

    private TextView title;
    private TextView description;
    private TextView pluses;
    private TextView minuses;

    private LinearLayout starsContainer;

    private ImageView reviewerImage;
    private TextView reviewerName;
    private TextView reviewDate;
    private TextView reviewAddress;

    private ImageView likeButton;
    private ImageView replyExpandButton;

    private TextView replyHead;
    private TextView replyText;

    private boolean replyExpanded;
    private boolean liked;

    public ReviewCardView(Context context) {
        super(context);
    }

    public ReviewCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReviewCardView setupCard() {

        title = findViewById(R.id.review_title);
        description = findViewById(R.id.review_description);
        pluses = findViewById(R.id.review_pluses);
        minuses = findViewById(R.id.review_minuses);

        starsContainer = findViewById(R.id.review_stars_container);
        likeButton = findViewById(R.id.review_like_button);
        replyExpandButton = findViewById(R.id.reply_expand_button);

        reviewerImage = findViewById(R.id.reviewer_image);
        reviewerName = findViewById(R.id.reviewer_name);
        reviewDate = findViewById(R.id.review_date);
        reviewAddress = findViewById(R.id.review_address);

        replyHead = findViewById(R.id.review_reply_head);
        replyText = findViewById(R.id.review_reply_text);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View view) {

                int id;
                if(replyExpanded){
                    id = R.drawable.ic_reply_more;
                    replyText.setVisibility(GONE);
                } else {
                    id = R.drawable.ic_reply_less;
                    replyText.setVisibility(VISIBLE);
                }
                replyExpanded = !replyExpanded;
                replyExpandButton.setImageDrawable(ContextCompat.getDrawable(getContext(),id));

            }
        };

        replyExpandButton.setOnClickListener(listener);
        replyHead.setOnClickListener(listener);

        likeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int id;
                if(liked){
                    id = R.drawable.ic_like_no;
                } else {
                    id = R.drawable.ic_like;
                }
                liked = !liked;
                likeButton.setImageDrawable(ContextCompat.getDrawable(getContext(),id));
            }
        });

        return this;
    }

    public ReviewCardView setReview(Review review, Shop shop){



        title.setText(review.getTitle());
        description.setText(review.getDescription());
        pluses.setText(review.getPluses());
        minuses.setText(review.getMinuses());

        reviewAddress.setText(shop.getAddress(review.getAddress()).getPlace());
        reviewDate.setText(review.getDate());
        reviewerName.setText(review.getUserName());

        reviewerName.setText("Oleg Yukhnevich");

        ImageLoader.loadImage(reviewerImage,review.getUserImageRef());

        for(int i = 4; i > -1; i--){
            int id;

            if(review.getPoints()>i) id = R.drawable.ic_star_full;
            else id = R.drawable.ic_star_empty;

            ((ImageView)starsContainer.getChildAt(i)).setImageDrawable(ContextCompat.getDrawable(getContext(),id));

        }

        if(review.getAnswer()!=null){
            replyHead.setVisibility(VISIBLE);
            replyExpandButton.setVisibility(VISIBLE);

            replyHead.setText("Reply form "+shop.getName()+" on "+review.getAnswer().get("date"));
            replyText.setText(review.getAnswer().get("description"));
        }

        return this;
    }

}
