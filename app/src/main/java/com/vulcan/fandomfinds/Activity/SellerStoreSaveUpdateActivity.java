package com.vulcan.fandomfinds.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Animations.DeletingDialog;
import com.vulcan.fandomfinds.Animations.LoadingDialog;
import com.vulcan.fandomfinds.Animations.SavingDataDialog;
import com.vulcan.fandomfinds.Domain.Follower;
import com.vulcan.fandomfinds.Domain.NotificationDomain;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Enum.NotifyType;
import com.vulcan.fandomfinds.Enum.ProductStatus;
import com.vulcan.fandomfinds.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
    private Button sellerStoreSaveUpdateButton,sellerStoreDeleteButton,sellerStoreStatusButton;
    private ProductsDomain product;
    private Uri imagePath;
    SellerDomain seller;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    SavingDataDialog savingDataDialog;
    LoadingDialog loadingDialog;
    DeletingDialog deletingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_store_save_update);

        initComponents();
        setListeners();
        loadUser();
        loadItemTypes();

        loadProductDetails();
    }

    private void loadProductDetails() {
        product = (ProductsDomain) getIntent().getSerializableExtra("item");
        if(product != null){
            sellerStoreDeleteButton.setVisibility(View.VISIBLE);
            sellerStoreSaveUpdateItemTitle.setText(product.getTitle());
            sellerStoreSaveUpdateItemDes.setText(product.getDescription());
            price.setText(String.valueOf(product.getPrice()));

//            itemDiscount = item.getDiscount();
            discount.setText(String.valueOf(product.getDiscount()));
            calculateNewPrice();

            String type = product.getType();
            if(type.equals("Apparel")){
                spinner.setSelection(0);
            }else if (type.equals("Collectibles")){
                spinner.setSelection(1);
            }else if (type.equals("Gaming Gear")){
                spinner.setSelection(2);
            }else if (type.equals("Other")){
                spinner.setSelection(3);
            }

            if(product.getStatus().equals(ProductStatus.AVAILABLE)){
                sellerStoreStatusButton.setText("UNAVAILABLE");
            }else if(product.getStatus().equals(ProductStatus.OUT_OF_STOCK)){
                sellerStoreStatusButton.setText(String.valueOf(ProductStatus.AVAILABLE));
            }

            if(product.getSizesList() != null){
                String[] sizes = product.getSizesList().toArray(new String[0]);
                if(sizes.length != 0){
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
            }
            loadProductImage(product.getPicUrl());
        }
    }

    private void loadProductImage(String picUrl) {
        firebaseStorage.getReference("product-images/"+picUrl)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .into(sellerStoreSaveUpdateItemImg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SellerStoreSaveUpdateActivity.this,"Couldn't load the Product image!",Toast.LENGTH_LONG).show();
                    }
                });
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
        sellerStoreDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SellerStoreSaveUpdateActivity.this);
                builder.setMessage("Are you sure you want to delete this Merchandise?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deletingDialog.show();
                                        deleteProduct();
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        sellerStoreStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                checkProductStatus();
            }
        });
    }

    private void checkProductStatus() {
        if(product != null){
            Map<String,Object> map = new HashMap<>();
            if(product.getStatus().equals(ProductStatus.AVAILABLE)){
                map.put("status",String.valueOf(ProductStatus.OUT_OF_STOCK));
                changeStatus(map,"AVAILABLE");
            }else if(product.getStatus().equals(ProductStatus.OUT_OF_STOCK)){
                map.put("status",String.valueOf(ProductStatus.AVAILABLE));
                changeStatus(map,"UNAVAILABLE");
            }
        }else{
            loadingDialog.cancel();
        }
    }

    private void changeStatus(Map<String,Object> map,String changedStatus){
        firestore.collection("Products").whereEqualTo("id",product.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                snapshot.getReference().update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        loadingDialog.cancel();
                                        sellerStoreStatusButton.setText(changedStatus);
                                        Toast.makeText(SellerStoreSaveUpdateActivity.this,"Status Successfully Updated!",Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingDialog.cancel();
                                        Toast.makeText(SellerStoreSaveUpdateActivity.this,"Failed to update status!",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
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
        sellerStoreDeleteButton = findViewById(R.id.sellerStoreDeleteButton);
        sellerStoreDeleteButton.setVisibility(View.GONE);
        sellerStoreStatusButton = findViewById(R.id.sellerStoreStatusButton);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        savingDataDialog = new SavingDataDialog(SellerStoreSaveUpdateActivity.this);
        loadingDialog = new LoadingDialog(SellerStoreSaveUpdateActivity.this);
        deletingDialog = new DeletingDialog(SellerStoreSaveUpdateActivity.this);
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
                            //product title process for searching process
                            List<String> titleInsensitiveList = new ArrayList<>();
                            String titleInsensitive = productTitle.toLowerCase();
                            titleInsensitiveList.add(titleInsensitive);

                            String titleInsensitiveWithoutSpace = titleInsensitive.replaceAll("\\s","");
                            titleInsensitiveList.add(titleInsensitiveWithoutSpace);

                            for (int i = 1; i < titleInsensitiveWithoutSpace.length();i++){
                                String firstNLetters = titleInsensitiveWithoutSpace.substring(0,i);
                                titleInsensitiveList.add(firstNLetters);
                            }
                            //product title process for searching process

                            ProductsDomain newProduct = new ProductsDomain(productId,productTitle,productDescription,imageId,0,0
                                    ,productPrice,productDiscount,seller.getSellerName(),selectedSizes, ProductStatus.AVAILABLE,productType,seller.getId(),titleInsensitiveList);
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
            if(isNotEmpty(productTitle) && isNotEmpty(productDescription)){
                if(productPrice != 0){
                    if(productType.equals("Apparel") && selectedSizes.size() == 0){
                        savingDataDialog.cancel();
                        Toast.makeText(SellerStoreSaveUpdateActivity.this,"Please select at least one product size!",Toast.LENGTH_LONG).show();
                    }else{
                        ProductsDomain newProduct;

                        //product title process for searching process
                        List<String> titleInsensitiveList = new ArrayList<>();
                        String titleInsensitive = productTitle.toLowerCase();
                        titleInsensitiveList.add(titleInsensitive);

                        String titleInsensitiveWithoutSpace = titleInsensitive.replaceAll("\\s","");
                        titleInsensitiveList.add(titleInsensitiveWithoutSpace);

                        for (int i = 1; i < titleInsensitiveWithoutSpace.length();i++){
                            String firstNLetters = titleInsensitiveWithoutSpace.substring(0,i);
                            titleInsensitiveList.add(firstNLetters);
                        }
                        //product title process for searching process

                        if(imagePath == null){
                            newProduct = new ProductsDomain(product.getId(),productTitle,productDescription,product.getPicUrl(),product.getReview(),product.getScore()
                                    ,productPrice,productDiscount,product.getSellerName(),selectedSizes, product.getStatus(),productType,product.getSellerId(),titleInsensitiveList);
                        }else{
                            imageId = UUID.randomUUID().toString();
                            newProduct = new ProductsDomain(product.getId(),productTitle,productDescription,imageId,product.getReview(),product.getScore()
                                    ,productPrice,productDiscount,product.getSellerName(),selectedSizes, product.getStatus(),productType,product.getSellerId(),titleInsensitiveList);
                        }
                        updateProduct(newProduct);
                    }
                }else{
                    savingDataDialog.cancel();
                    Toast.makeText(SellerStoreSaveUpdateActivity.this,"Please set a price to the product!",Toast.LENGTH_LONG).show();
                }
            }else{
                savingDataDialog.cancel();
                Toast.makeText(SellerStoreSaveUpdateActivity.this,"Please fill the fields!",Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isNotEmpty(String value){
        if(value != null && !value.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    private void updateProduct(ProductsDomain product){
        Map<String,Object> map = new HashMap<>();
        map.put("title",product.getTitle());
        map.put("description",product.getDescription());
        map.put("price",product.getPrice());
        map.put("discount",product.getDiscount());
        map.put("status",product.getStatus());
        map.put("type",product.getType());
        String titleInsensitive = product.getTitle().toLowerCase();

        //product title process for searching process
        List<String> titleInsensitiveList = new ArrayList<>();
        titleInsensitiveList.add(titleInsensitive);

        String titleInsensitiveWithoutSpace = titleInsensitive.replaceAll("\\s","");
        titleInsensitiveList.add(titleInsensitiveWithoutSpace);

        for (int i = 1; i < titleInsensitiveWithoutSpace.length();i++){
            String firstNLetters = titleInsensitiveWithoutSpace.substring(0,i);
            titleInsensitiveList.add(firstNLetters);
        }
        //product title process for searching process

        map.put("titleInsensitive",titleInsensitiveList);
        if(product.getType().equals("Apparel")){
            map.put("sizesList",product.getSizesList());
        }else{
            map.put("sizesList",null);
            sizeExraSmall.setChecked(false);
            sizeSmall.setChecked(false);
            sizeMedium.setChecked(false);
            sizeLarge.setChecked(false);
            sizeExtraLarge.setChecked(false);
            sizeExtraExtraLarge.setChecked(false);
        }
        if(imagePath != null){
            map.put("picUrl",product.getPicUrl());
        }
        firestore.collection("Products").whereEqualTo("id",product.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                snapshot.getReference().update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if(imagePath != null){
                                            uploadImage(product.getPicUrl());
                                        }else{
                                            savingDataDialog.cancel();
                                            Toast.makeText(SellerStoreSaveUpdateActivity.this,"Product Successfully Updated!",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SellerStoreSaveUpdateActivity.this,"Failed to update product!",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void addNewProduct(ProductsDomain newProduct,String imageId) {
        firestore.collection("Products").add(newProduct).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                if(imagePath != null){
                    uploadImage(imageId);
                    notifyFollowers(newProduct);
                    clearFields();
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

    private void notifyFollowers(ProductsDomain newProduct){
        loadingDialog.show();
        firestore.collection("Sellers").whereEqualTo("id",seller.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                        snapshot.getReference().collection("Followers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                        Follower follower = snapshot1.toObject(Follower.class);
                                        findFollower(follower.getEmail(),newProduct);
                                    }
                                }
                            }
                        });
                    }
                    loadingDialog.cancel();
                    Toast.makeText(SellerStoreSaveUpdateActivity.this,"Followers Notified!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void findFollower(String email,ProductsDomain product) {
        firestore.collection("Customers").whereEqualTo("email",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                        saveNotification(email,product,snapshot);
                    }
                }
            }
        });
        firestore.collection("Sellers").whereEqualTo("email",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                        saveNotification(email,product,snapshot);
                    }
                }
            }
        });
    }

    private void saveNotification(String email, ProductsDomain product, QueryDocumentSnapshot snapshot){
        String notifyId = "NOTI_"+String.format("%06d",new Random().nextInt(999999));
        String sellerName = null;
        if(seller.getSellerName() != null){
            sellerName = seller.getSellerName();
        }else{
            sellerName = seller.getEmail();
        }

        String datetime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            datetime = dtf.format(LocalDateTime.now());
        }
        String title = "Hey! "+sellerName+" has release a new Merchandise!";
        String message = "Exciting News! Your favorite seller "+sellerName+" just dropped new merchandise: "+product.getTitle()+" !Explore the latest arrivals and grab exclusive items before they're gone! ";
        NotificationDomain notification = new NotificationDomain(notifyId,NotifyType.PRODUCT_RELEASE,title,message,product.getPicUrl(),datetime);
        snapshot.getReference().collection("Notifications").add(notification);
    }

    private void deleteProduct() {
        if(product != null){
            firestore.collection("Products").whereEqualTo("id",product.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot snapshot : task.getResult()){
                            snapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    deletingDialog.cancel();
                                    Toast.makeText(SellerStoreSaveUpdateActivity.this,"Product deleted successfully!",Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    deletingDialog.cancel();
                                    Toast.makeText(SellerStoreSaveUpdateActivity.this,"Deletion process Failed! Try again later.",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    deletingDialog.cancel();
                    Toast.makeText(SellerStoreSaveUpdateActivity.this,"Merch doesn't exists! Try again later.",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}

