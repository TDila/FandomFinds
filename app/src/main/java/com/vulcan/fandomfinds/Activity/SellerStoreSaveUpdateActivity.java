package com.vulcan.fandomfinds.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Animations.SavingDataDialog;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Enum.ProductStatus;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SellerStoreSaveUpdateActivity extends AppCompatActivity {
    private TextView newPrice,sellerStoreSaveUpdateItemTitle;
    private ImageButton sellerStoreSaveUpdateItemImg;
    private TextInputEditText sellerStoreSaveUpdateItemDes;
    private EditText price,discount;
    private Spinner spinner;
    private RadioButton sizeExraSmall,sizeSmall,sizeMedium,sizeLarge,sizeExtraLarge,sizeExtraExtraLarge;
    private LinearLayout sizesLayout;
    private double itemDiscount;
    private Button sellerStoreSaveUpdateButton;
    private ProductsDomain product;
    private Uri imagePath;
    SellerDomain seller;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    SavingDataDialog savingDataDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_store_save_update);

        initComponents();
        setListeners();
        loadUser();
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
    }

    private void loadUser() {
        Intent intent = getIntent();
        String userString = intent.getStringExtra("user");
        seller = (new Gson()).fromJson(userString, SellerDomain.class);
    }

    private void setListeners(){
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
        sellerStoreSaveUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingDataDialog.show();
                saveProductDetails();
            }
        });
        sellerStoreSaveUpdateItemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                checkPermission();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                activityResultLauncher.launch(Intent.createChooser(intent,"Select Image"));
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
        sellerStoreSaveUpdateButton = findViewById(R.id.sellerStoreSaveUpdateButton);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        savingDataDialog = new SavingDataDialog(SellerStoreSaveUpdateActivity.this);
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

    private void saveProductDetails() {
        String productId;
        String imageId = null;
        String productTitle = sellerStoreSaveUpdateItemTitle.getText().toString();
        String productDescription = sellerStoreSaveUpdateItemDes.getText().toString();
        String priceString = price.getText().toString();
        String discountString = discount.getText().toString();
        double productPrice = 0;
        if(isNotEmpty(priceString)){
            productPrice = Double.parseDouble(priceString);
        }
        double productDiscount = 0;
        if (isNotEmpty(discountString)){
           productDiscount = Double.parseDouble(discountString);
        }
        String productType = spinner.getSelectedItem().toString();

        List<String> selectedSizes = new ArrayList<>();
        if (sizeExraSmall.isChecked()) {
            selectedSizes.add("XS");
        }
        if (sizeSmall.isChecked()) {
            selectedSizes.add("S");
        }
        if (sizeMedium.isChecked()) {
            selectedSizes.add("M");
        }
        if (sizeLarge.isChecked()) {
            selectedSizes.add("L");
        }
        if (sizeExtraLarge.isChecked()) {
            selectedSizes.add("XL");
        }
        if (sizeExtraExtraLarge.isChecked()) {
            selectedSizes.add("XXL");
        }

        if(product == null){
            productId = "PD_"+ UUID.randomUUID();
            imageId = UUID.randomUUID().toString();
            if(imagePath != null){
                if(isNotEmpty(productTitle) && isNotEmpty(productDescription)){
                    if(productPrice != 0){
                        if(productType.equals("Apparel") && selectedSizes.size() == 0){
                            savingDataDialog.cancel();
                            Toast.makeText(SellerStoreSaveUpdateActivity.this,"Please select at least one product size!",Toast.LENGTH_LONG).show();
                        }else{
                            ProductsDomain newProduct = new ProductsDomain(productId,productTitle,productDescription,imageId,0,0
                                    ,productPrice,productDiscount,seller.getSellerName(),selectedSizes, ProductStatus.AVAILABLE,productType,seller.getId());
                            addNewProduct(newProduct,imageId);
                        }
                    }else{
                        savingDataDialog.cancel();
                        Toast.makeText(SellerStoreSaveUpdateActivity.this,"Please set a price to the product!",Toast.LENGTH_LONG).show();
                    }
                }else{
                    savingDataDialog.cancel();
                    Toast.makeText(SellerStoreSaveUpdateActivity.this,"Please fill the fields!",Toast.LENGTH_LONG).show();
                }
            }else{
                savingDataDialog.cancel();
                Toast.makeText(SellerStoreSaveUpdateActivity.this,"Please select an Image!",Toast.LENGTH_LONG).show();
            }
        }else{
            productId = product.getId();
        }
    }

    private boolean isNotEmpty(String value){
        if(value != null && !value.isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    private void addNewProduct(ProductsDomain newProduct,String imageId) {
        firestore.collection("Products").add(newProduct).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                if(imagePath != null){
                    uploadImage(imageId);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                savingDataDialog.cancel();
                Toast.makeText(SellerStoreSaveUpdateActivity.this,"Failed to save Product. Please try again.!",Toast.LENGTH_LONG).show();
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        imagePath = result.getData().getData();
                        Picasso.get()
                                .load(imagePath)
                                .into(sellerStoreSaveUpdateItemImg);
                    }
                }
            });

    private void uploadImage(String imageId) {
        if(imagePath != null){
            StorageReference reference = firebaseStorage.getReference("product-images").child(imageId);
            reference.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    clearFields();
                    savingDataDialog.cancel();
                    Toast.makeText(SellerStoreSaveUpdateActivity.this,"Product Save Successfully!",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    clearFields();
                    savingDataDialog.cancel();
                    Toast.makeText(SellerStoreSaveUpdateActivity.this,"Image Upload Failed!",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void clearFields(){
        discount.setText("");
        price.setText("");
        spinner.setSelection(0);
        sellerStoreSaveUpdateItemTitle.setText("");
        sellerStoreSaveUpdateItemDes.setText("");
        sizeExraSmall.setChecked(false);
        sizeSmall.setChecked(false);
        sizeMedium.setChecked(false);
        sizeLarge.setChecked(false);
        sizeExtraLarge.setChecked(false);
        sizeExtraExtraLarge.setChecked(false);
        int drawableResourceid = getResources().getIdentifier("product_default","drawable",getPackageName());
        Glide.with(SellerStoreSaveUpdateActivity.this)
                .load(drawableResourceid)
                .into(sellerStoreSaveUpdateItemImg);
        imagePath = null;
    }
}