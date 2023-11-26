package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.R;

public class SellerStoreSaveUpdateActivity extends AppCompatActivity {
    private TextView newPrice,sellerStoreSaveUpdateItemTitle;
    private ImageButton sellerStoreSaveUpdateItemImg;
    private TextInputEditText sellerStoreSaveUpdateItemDes;
    private EditText price,discount;
    private Spinner spinner;
    private RadioButton sizeExraSmall,sizeSmall,sizeMedium,sizeLarge,sizeExtraLarge,sizeExtraExtraLarge;
    private LinearLayout sizesLayout;
    private double itemDiscount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_store_save_update);

        initComponents();
        loadItemTypes();

        ProductsDomain item = (ProductsDomain) getIntent().getSerializableExtra("item");
        if(item != null){
            sellerStoreSaveUpdateItemTitle.setText(item.getTitle());
            sellerStoreSaveUpdateItemDes.setText(item.getDescription());
            price.setText(String.valueOf(item.getPrice()));

//            itemDiscount = item.getDiscount();
            discount.setText(String.valueOf(item.getDiscount()));
            calculateNewPrice();

            String type = item.getType();
            if(type.equals("Apparel")){
                spinner.setSelection(0);
            }else if (type.equals("Collectibles")){
                spinner.setSelection(1);
            }else if (type.equals("Gaming Gear")){
                spinner.setSelection(2);
            }else if (type.equals("Other")){
                spinner.setSelection(3);
            }

            String[] sizes = item.getSizes();
            if(sizes != null){
                for(int i = 0; i < sizes.length;i++){
                    if(sizes[i].equals("XS")){
                        sizeExraSmall.setChecked(true);
                    }
                    if(sizes[i].equals("S")){
                        sizeSmall.setChecked(true);
                    }
                    if(sizes[i].equals("M")){
                        sizeMedium.setChecked(true);
                    }
                    if(sizes[i].equals("L")){
                        sizeLarge.setChecked(true);
                    }
                    if(sizes[i].equals("XL")){
                        sizeExtraLarge.setChecked(true);
                    }
                    if(sizes[i].equals("XXL")){
                        sizeExtraLarge.setChecked(true);
                    }
                }
            }
        }else{
            Toast.makeText(SellerStoreSaveUpdateActivity.this,"null",Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.sellerStoreSaveUpdateBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateNewPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateNewPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadItemTypes() {
        String[] types = {"Apparel","Collectibles","Gaming Gear","Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,types);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = parent.getSelectedItem().toString();
                if(type.equals("Apparel")) {
                    sizesLayout.setVisibility(View.VISIBLE);
                }else{
                    sizesLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initComponents() {
        newPrice = findViewById(R.id.sellerStoreSaveUpdateItemNewPrice);
        discount = findViewById(R.id.sellerStoreSaveUpdateItemDiscount);
        price= findViewById(R.id.sellerStoreSaveUpdateItemPrice);
        spinner = findViewById(R.id.typeSpinner);
        sizesLayout = findViewById(R.id.sizesLayout);
        sellerStoreSaveUpdateItemTitle = findViewById(R.id.sellerStoreSaveUpdateItemTitle);
        sellerStoreSaveUpdateItemDes = findViewById(R.id.sellerStoreSaveUpdateItemDes);
        sizeExraSmall = findViewById(R.id.sizeExraSmall);
        sizeSmall = findViewById(R.id.sizeSmall);
        sizeMedium = findViewById(R.id.sizeMedium);
        sizeLarge = findViewById(R.id.sizeLarge);
        sizeExtraLarge = findViewById(R.id.sizeExtraLarge);
        sizeExtraExtraLarge = findViewById(R.id.sizeExtraExtraLarge);
        sellerStoreSaveUpdateItemImg = findViewById(R.id.sellerStoreSaveUpdateItemImg);
    }

    public void calculateNewPrice(){
        String priceString = price.getText().toString();
        if(priceString != null && !priceString.equals("")) {
            double itemPrice = Double.parseDouble(price.getText().toString());

            String discountString = discount.getText().toString();

            if (discountString != null && !discountString.equals("")) {
                itemDiscount = Double.parseDouble(discountString);
//                    Toast.makeText(SellerStoreSaveUpdateActivity.this,discountString,Toast.LENGTH_SHORT).show();
                double itemNewPrice = itemPrice - (itemPrice * itemDiscount / 100);
                newPrice.setText("$" + String.valueOf(itemNewPrice));
            } else {
                itemDiscount = 0;
                newPrice.setText("$" + String.valueOf(itemPrice));
            }
        }else{
            newPrice.setText("$0.00");
        }
    }
}