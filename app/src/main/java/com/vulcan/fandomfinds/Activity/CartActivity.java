package com.vulcan.fandomfinds.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.vulcan.fandomfinds.Adapter.CartAdapter;
import com.vulcan.fandomfinds.Animations.LoadingDialog;
import com.vulcan.fandomfinds.Domain.BaseDomain;
import com.vulcan.fandomfinds.Domain.BillingShippingDomain;
import com.vulcan.fandomfinds.Domain.CustomerDomain;
import com.vulcan.fandomfinds.Domain.NotificationDomain;
import com.vulcan.fandomfinds.Domain.OrderDomain;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Domain.SocialMedia;
import com.vulcan.fandomfinds.Enum.NotifyType;
import com.vulcan.fandomfinds.Enum.OrderStatus;
import com.vulcan.fandomfinds.Fragments.bottomNavigation;
import com.vulcan.fandomfinds.Helper.ChangeNumberitemsListener;
import com.vulcan.fandomfinds.Helper.ManagementCart;
import com.vulcan.fandomfinds.Helper.TinyDB;
import com.vulcan.fandomfinds.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class CartActivity extends AppCompatActivity{
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private ManagementCart managementCart;
    private TextView cart_subtotal,cart_delivery,cart_total_tax,cart_total,cart_empty_txt,cart_delivery_address_value,cart_payment_method_value;
    private double tax;
    private ScrollView scrollView;
    private ImageView cart_back_btn,empty_cart_img,billingShipping_address,billingShipping_paymentMethod;
    private LinearLayout cartErrorMessageLayout;
    private Button cartOrderNowButton;
    FirebaseFirestore firestore;
    CustomerDomain customer;
    FirebaseAuth firebaseAuth;
    SellerDomain seller;
    FirebaseUser user;
    BillingShippingDomain billingShipping;
    LoadingDialog loadingDialog;
    TinyDB tinyDB;
    double delivery = 10;
    private String channelId = "Order Notifications";
    NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        managementCart = new ManagementCart(this);

        initView();
        setListeners();
        loadingDialog.show();
        setVariable();
        calculateCart();
        initList();
        loadBottomNavigationBar();
        searchUserProcess();
        registerNotificationChannel();
    }

    private void setListeners() {
        billingShipping_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBillingShipping();
            }
        });

        billingShipping_paymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBillingShipping();
            }
        });
        cartOrderNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                orderNow();
            }
        });
    }

    private void orderNow() {
        String datetime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            datetime = formatter.format(LocalDateTime.now());
        }

        ArrayList<ProductsDomain> productList = tinyDB.getListObject("CartList");
        for (int position = 0; position < productList.size();position++){
            ProductsDomain product = productList.get(position);
            double price  = product.getPrice();
            int count = product.getNumberInCart();
            double discount = product.getDiscount();
            double total = (price - price*discount/100)*count + delivery;
            String orderId = "OD_"+ String.format("%06d",new Random().nextInt(999999));
            String selectedSize = null;
            if(product.getSelectedSize() != null){
                selectedSize = product.getSelectedSize();
            }
            OrderDomain order = new OrderDomain(orderId,datetime,total,count,billingShipping.getPostalCode(),user.getEmail(),product.getSellerId(),selectedSize);
            firestore.collection("Orders").add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference1) {
                    documentReference1.collection("Product").add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            if(customer != null){
                                saveBuyer(documentReference1, customer,product.getSellerId());
                            }else if (seller != null){
                                saveBuyer(documentReference1,seller,product.getSellerId());
                            }
                            findBuyer(product);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CartActivity.this,"Order Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                }
            });
        }

        String key = "CartList";
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(CartActivity.this).edit();
        editor.remove(key);
        editor.apply();
        loadingDialog.cancel();
        Toast.makeText(CartActivity.this,"Successfully Made the Order!",Toast.LENGTH_LONG).show();
    }

    private void findBuyer(ProductsDomain product) {
        firestore.collection("Customers").whereEqualTo("email",user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                        saveNotification(product,snapshot);
                    }
                }
            }
        });
        firestore.collection("Sellers").whereEqualTo("email",user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                        saveNotification(product,snapshot);
                    }
                }
            }
        });
    }

    private void saveNotification(ProductsDomain product, QueryDocumentSnapshot snapshot){
        String datetime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            datetime = dtf.format(LocalDateTime.now());
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        }
        String notifyId = "NOTI_"+String.format("%06d",new Random().nextInt(999999));
        String title = "Your order has been placed!";
        String message = "\uD83C\uDF89 Your order is confirmed. \uD83D\uDECD️ Get ready for the arrival of "+product.getTitle()+". \uD83D\uDE9A Thank you for choosing FandomFinds! \uD83C\uDF1F";
        NotificationDomain notification = new NotificationDomain(notifyId,NotifyType.NEW_ORDER,title,message,product.getPicUrl(),datetime);
        snapshot.getReference().collection("Notifications").add(notification);
        sendOrderNotification(notification);
    }

    private void sendOrderNotification(NotificationDomain notify){
        Intent intent = new Intent(CartActivity.this,NotificationsActivity.class);
        if(seller != null){
            String sellerString = (new Gson()).toJson(seller);
            intent.putExtra("seller",sellerString);
        }
        if(customer != null){
            String customerString = (new Gson()).toJson(customer);
            intent.putExtra("customer",customerString);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(CartActivity.this,0,intent,PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(CartActivity.this,channelId)
                .setSmallIcon(R.drawable.fandom_finds_main_logo_new)
                .setContentTitle(notify.getTitle())
                .setContentText(notify.getMessage())
                .setContentIntent(pendingIntent)
                .build();

        String id = String.format("%06d",new Random().nextInt(999999));
        notificationManager.notify(Integer.parseInt(id),notification);
    }

    private void goToPurchaseHistory() {
        Intent intent = new Intent(CartActivity.this,PurchaseHistoryActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveBuyer(DocumentReference orderRef,BaseDomain user,String sellerId) {
        orderRef.collection("Customer").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                documentReference.collection("Delivery-Payment").add(billingShipping)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                saveSeller(orderRef,sellerId);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CartActivity.this,"Order Process Failed! Try again later.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveSeller(DocumentReference orderRef,String sellerId) {
        firestore.collection("Sellers").whereEqualTo("id",sellerId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                SellerDomain seller = snapshot.toObject(SellerDomain.class);
                                orderRef.collection("Seller").add(seller).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void loadBillingShipping(){
        Intent intent = new Intent(CartActivity.this,BillingShippingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if(customer != null){
            intent.putExtra("customerId",customer.getId());
        }else if(seller != null){
            intent.putExtra("sellerId",seller.getId());
        }
        startActivity(intent);
    }

    private void initList() {
        if(managementCart.getListCart().isEmpty()){
            cart_empty_txt.setVisibility(View.VISIBLE);
            empty_cart_img.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }else{
            cart_empty_txt.setVisibility(View.GONE);
            empty_cart_img.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new CartAdapter(managementCart.getListCart(), this, new ChangeNumberitemsListener() {
            @Override
            public void change() {
                calculateCart();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void calculateCart() {
        double percentTax = 0.02;
        double deliveryTotal = 0;
        tax = Math.round(managementCart.getTotal()*percentTax*100.0)/100.0;

        ArrayList<ProductsDomain> productList = tinyDB.getListObject("CartList");
        for (int position = 0; position < productList.size();position++){
            deliveryTotal = deliveryTotal + delivery;
        }

        double total = Math.round((managementCart.getTotal()+tax+deliveryTotal)*100)/100;
        double itemTotal = Math.round(managementCart.getTotal()*100)/100;

        cart_subtotal.setText("$"+itemTotal);
        cart_total_tax.setText("$"+tax);
        cart_delivery.setText("$"+deliveryTotal);
        cart_total.setText("$"+total);
    }

    private void setVariable() {
        cart_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        cart_subtotal = findViewById(R.id.cart_subtotal_value);
        cart_delivery = findViewById(R.id.cart_delivery_value);
        cart_total_tax = findViewById(R.id.cart_total_tax_value);
        cart_total = findViewById(R.id.cart_total_value);
        recyclerView = findViewById(R.id.cart_product_list);
        scrollView = findViewById(R.id.cart_scrollView);
        cart_back_btn = findViewById(R.id.cart_back_btn);
        cart_empty_txt = findViewById(R.id.cart_empty_txt);
        empty_cart_img = findViewById(R.id.empty_cart_img);
        billingShipping_address = findViewById(R.id.cart_to_billingShipping_address);
        billingShipping_paymentMethod = findViewById(R.id.cart_to_billingShipping_paymentMethod);
        cartErrorMessageLayout = findViewById(R.id.cartErrorMessageLayout);
        cartErrorMessageLayout.setVisibility(View.GONE);
        loadingDialog = new LoadingDialog(CartActivity.this);
        cart_delivery_address_value = findViewById(R.id.cart_delivery_address_value);
        cart_payment_method_value = findViewById(R.id.cart_payment_method_value);
        cartOrderNowButton = findViewById(R.id.cart_order_now_btn);
        tinyDB = new TinyDB(CartActivity.this);
    }
    private void loadBottomNavigationBar(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView2, bottomNavigation.class,null)
                .commit();
    }

    private void searchUserProcess() {
        firestore.collection("Customers").whereEqualTo("email",user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                customer = snapshot.toObject(CustomerDomain.class);
                                getBillingShippingDetails(snapshot);
                            }
                        }
                    }
                });
        firestore.collection("Sellers").whereEqualTo("email",user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                seller = snapshot.toObject(SellerDomain.class);
                                getBillingShippingDetails(snapshot);
                            }
                        }
                    }
                });
    }

    public void getBillingShippingDetails(QueryDocumentSnapshot snapshot){
        snapshot.getReference().collection("Billing-Shipping").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                billingShipping = snapshot1.toObject(BillingShippingDomain.class);
                                initErrorMessage();
                                loadBillingDetails();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CartActivity.this,"Details Loading Failed! Try Again Later.",Toast.LENGTH_LONG).show();
                        loadingDialog.cancel();
                    }
                });
    }
    public void initErrorMessage(){
        if(billingShipping != null){
            if(billingShipping.getShippingAddress() == null || billingShipping.getPostalCode() == 0 || billingShipping.getPaymentMethod() == null){
                cartErrorMessageLayout.setVisibility(View.VISIBLE);
                cartOrderNowButton.setEnabled(false);
            }else{
                cartErrorMessageLayout.setVisibility(View.GONE);
                cartOrderNowButton.setEnabled(true);
            }
        }else{
            cartErrorMessageLayout.setVisibility(View.VISIBLE);
            cartOrderNowButton.setEnabled(false);
        }
    }

    private void loadBillingDetails() {
        if(billingShipping != null){
            if(billingShipping.getShippingAddress() != null){
                cart_delivery_address_value.setText(billingShipping.getShippingAddress());
            }
            if(billingShipping.getPaymentMethod() != null){
                cart_payment_method_value.setText(billingShipping.getPaymentMethod());
            }
        }
        loadingDialog.cancel();
    }

    private void registerNotificationChannel(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,"Promotional",NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            channel.setDescription("Promotional channel ");
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setVibrationPattern(new long[]{0,1000});
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
    }
}