package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Helper.ManagementCart;
import com.vulcan.fandomfinds.R;

public class SingleProductViewActivity extends AppCompatActivity {
    private Button single_product_buynow;
    private TextView single_product_title,single_product_price
            ,single_product_star_num,single_product_feedback_count,single_product_description,single_product_new_price
            ,single_product_discount;
    private ImageView single_product_backArrow,single_product_img;
    private ProductsDomain newArrival;
    private int numberOrder = 1;
    private ManagementCart managementCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product_view);
        managementCart = new ManagementCart(this);

        initView();
        getBundle();
    }
    private void getBundle(){
        newArrival = (ProductsDomain) getIntent().getSerializableExtra("newArrival");
        int drawableResourceId = this.getResources().getIdentifier(newArrival.getPicUrl(),"drawable",this.getPackageName());

        Glide.with(this)
                .load(drawableResourceId)
                .into(single_product_img);

        single_product_title.setText(newArrival.getTitle());
        double price = newArrival.getPrice();
        single_product_price.setText("$"+String.valueOf(price));
        single_product_description.setText(newArrival.getDescription());
        single_product_feedback_count.setText(String.valueOf(newArrival.getReview()));
        single_product_star_num.setText(String.valueOf(newArrival.getScore()));
        double discount = newArrival.getDiscount();
        if(discount != 0){
            double newPrice = price - (price * discount / 100);
            single_product_new_price.setVisibility(View.VISIBLE);
            single_product_discount.setVisibility(View.VISIBLE);
            single_product_price.setPaintFlags(single_product_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            single_product_new_price.setText(String.valueOf(newPrice));
            single_product_discount.setText(String.valueOf(discount)+"% OFF");
        }else{
            single_product_new_price.setVisibility(View.GONE);
            single_product_discount.setVisibility(View.GONE);
        }

        single_product_buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newArrival.setNumberInCart(numberOrder);
                managementCart.insertFood(newArrival);
            }
        });
        single_product_backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initView() {
        single_product_buynow = findViewById(R.id.single_product_buynow);
        single_product_title = findViewById(R.id.single_product_title);
        single_product_price = findViewById(R.id.single_product_price);
        single_product_star_num = findViewById(R.id.single_product_star_num);
        single_product_feedback_count = findViewById(R.id.single_product_feedback_count);
        single_product_description = findViewById(R.id.single_product_description);
        single_product_backArrow = findViewById(R.id.single_product_backArrow);
        single_product_img = findViewById(R.id.single_product_img);
        single_product_new_price = findViewById(R.id.single_product_new_price);
        single_product_discount = findViewById(R.id.single_product_discount);
    }
}