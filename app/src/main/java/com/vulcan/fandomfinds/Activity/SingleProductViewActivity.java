package com.vulcan.fandomfinds.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Helper.ManagementCart;
import com.vulcan.fandomfinds.R;

import java.lang.reflect.Array;
import java.util.List;

public class SingleProductViewActivity extends AppCompatActivity {
    private Button single_product_buynow;
    private TextView single_product_title,single_product_price
            ,single_product_star_num,single_product_feedback_count,single_product_description,single_product_new_price
            ,single_product_discount;
    private Spinner spinner;
    private ImageView single_product_backArrow,single_product_img;
    private ProductsDomain newArrival;
    private LinearLayout sizeLayout;
    private int numberOrder = 1;
    private ManagementCart managementCart;
    FirebaseStorage firebaseStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product_view);
        managementCart = new ManagementCart(this);

        firebaseStorage = FirebaseStorage.getInstance();

        initView();
        getBundle();
    }
    private void getBundle(){
        newArrival = (ProductsDomain) getIntent().getSerializableExtra("newArrival");

        if(newArrival.getPicUrl() != null){
            firebaseStorage.getReference("product-images/"+newArrival.getPicUrl())
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get()
                                    .load(uri)
                                    .into(single_product_img);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int drawableResourceId = getResources().getIdentifier("product_default", "drawable",getPackageName());
                            Glide.with(SingleProductViewActivity.this)
                                    .load(drawableResourceId)
                                    .into(single_product_img);
                        }
                    });
        }else{
            int drawableResourceId = getResources().getIdentifier("product_default", "drawable",getPackageName());
            Glide.with(SingleProductViewActivity.this)
                    .load(drawableResourceId)
                    .into(single_product_img);
        }

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

        if(newArrival.getSizesList() != null){
            String[] sizes = newArrival.getSizesList().toArray(new String[0]);
            if(sizes.length != 0){
//            spinner.setVisibility(View.VISIBLE);
                sizeLayout.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sizes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        newArrival.setSelectedSize(String.valueOf(parent.getItemAtPosition(position)));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }else{
//            spinner.setVisibility(View.GONE);
                sizeLayout.setVisibility(View.GONE);
            }
        }

        single_product_buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newArrival.setNumberInCart(numberOrder);
                if(newArrival.getType().equals("Apparel")){
                    String selectedSize = spinner.getSelectedItem().toString();
                    newArrival.setSelectedSize(selectedSize);
                }
                managementCart.insertProduct(newArrival);
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
        spinner = findViewById(R.id.single_product_sizes);
        sizeLayout = findViewById(R.id.single_product_sizeLayout);
    }
}