package com.vulcan.fandomfinds.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;
import com.vulcan.fandomfinds.Animations.LoadingDialog;
import com.vulcan.fandomfinds.Animations.SavingDataDialog;
import com.vulcan.fandomfinds.BroadCastReceiver.MySmsReceiver;
import com.vulcan.fandomfinds.BroadCastReceiver.SmsReceivedCallback;
import com.vulcan.fandomfinds.Domain.BillingShippingDomain;
import com.vulcan.fandomfinds.R;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BillingShippingActivity extends AppCompatActivity implements SmsReceivedCallback {
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    private ImageView backButton;
    private EditText shippingAddressField,postalCodeField,paypalAddressField;
    private Button saveDetailsButton,savePaymentMethodButton,verifyButton;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private LinearLayout verificationLayout;
    private TextInputEditText verificationCodeField1,verificationCodeField2,verificationCodeField3
            ,verificationCodeField4,verificationCodeField5,verificationCodeField6;
    String userId;
    String userType;
    BillingShippingDomain billingShipping;
    static final String CUSTOMERS = "Customers";
    static final String SELLERS = "Sellers";
    SavingDataDialog savingDataDialog;
    LoadingDialog loadingDialog;
    private String verificationId;
    CountryCodePicker ccp;
    TextInputEditText editTextCarrierNumber;
    TextInputLayout phoneNumberLayout;
    QueryDocumentSnapshot billingShippingSnapshot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_shipping);

        initComponents();
        loadingDialog.show();
        setListeners();
        loadBillingShippingDetails();
        MySmsReceiver.setCallback(this);

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                System.out.println("onVerificationCompleted: "+phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                System.out.println("onVerificationFailed: "+e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                System.out.println("OnCodeSent: "+verificationId);
                Toast.makeText(BillingShippingActivity.this,"OTP Code has sent to your phone",Toast.LENGTH_LONG).show();

                BillingShippingActivity.this.verificationId = verificationId;
                resendingToken = forceResendingToken;
            }
        };
    }

    @Override
    public void onSmsReceived(String smsMessage) {
        for (int i = 1; i <= smsMessage.length() ; i++){
            switch (i){
                case 1:
                    verificationCodeField1.setText(smsMessage.substring(0,1));
                    break;
                case 2:
                    verificationCodeField2.setText(smsMessage.substring(1,2));
                    break;
                case 3:
                    verificationCodeField3.setText(smsMessage.substring(2,3));
                    break;
                case 4:
                    verificationCodeField4.setText(smsMessage.substring(3,4));
                    break;
                case 5:
                    verificationCodeField5.setText(smsMessage.substring(4,5));
                    break;
                case 6:
                    verificationCodeField6.setText(smsMessage.substring(5,6));
                    break;
            }
        }
        verifyAndSave(smsMessage);
    }
    private void initComponents() {
        backButton = findViewById(R.id.bs_back_button);
        shippingAddressField = findViewById(R.id.bs_shipping_address);
        postalCodeField = findViewById(R.id.bs_postal_code);
        saveDetailsButton = findViewById(R.id.bs_save_details);
        savingDataDialog = new SavingDataDialog(BillingShippingActivity.this);
        loadingDialog = new LoadingDialog(BillingShippingActivity.this);
        verifyButton = findViewById(R.id.verifyButton);

        savePaymentMethodButton = findViewById(R.id.savePaymentMethodButton);

        paypalAddressField = findViewById(R.id.bs_paypal_address);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        editTextCarrierNumber = findViewById(R.id.editText_carrierNumber);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        phoneNumberLayout= findViewById(R.id.phoneNumberLayout);

        verificationLayout = findViewById(R.id.verificationLayout);
        verificationLayout.setVisibility(View.GONE);

        verificationCodeField1 = findViewById(R.id.verificationCodeField1);
        verificationCodeField2 = findViewById(R.id.verificationCodeField2);
        verificationCodeField3 = findViewById(R.id.verificationCodeField3);
        verificationCodeField4 = findViewById(R.id.verificationCodeField4);
        verificationCodeField5 = findViewById(R.id.verificationCodeField5);
        verificationCodeField6 = findViewById(R.id.verificationCodeField6);
    }
    private void setListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BillingShippingActivity.this,AccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
        saveDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingDataDialog.show();
                saveDetails();
            }
        });
        savePaymentMethodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingDataDialog.show();
                savePaymentMethod();
            }
        });
        editTextCarrierNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
                        @Override
                        public void onValidityChanged(boolean isValidNumber) {
                            if(isValidNumber){
                                phoneNumberLayout.setError(null);
                            }else{
                                phoneNumberLayout.setError("Invalid Phone Number");
                            }
                        }
                    });
                }
            }
        });
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAndSave();
            }
        });
    }
    private void loadBillingShippingDetails() {
        Intent intent = getIntent();
        String customerId = intent.getStringExtra("customerId");
        String sellerId = intent.getStringExtra("sellerId");
        if(customerId != null){
            userId = customerId;
            userType = CUSTOMERS;
            searchUser(CUSTOMERS);
        }else if(sellerId != null){
            userId = sellerId;
            userType = SELLERS;
            searchUser(SELLERS);
        }
    }
    private void searchUser(String collectionName) {
        firestore.collection(collectionName).whereEqualTo("id",userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                snapshot.getReference().collection("Billing-Shipping").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    if (task.getResult().getDocuments().size() == 0){
                                                        createBillingShippingCollection(snapshot);
                                                    }else{
                                                        for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                            billingShipping = snapshot1.toObject(BillingShippingDomain.class);
                                                            loadUserDetails();
                                                        }
                                                    }
                                                }else{
                                                    System.out.println("Failed Billing shipping");
                                                    loadingDialog.cancel();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                loadingDialog.cancel();
                                                Toast.makeText(BillingShippingActivity.this,"Details Loading Failed! Try Again Later.",Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }else{
                            loadingDialog.cancel();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.cancel();
                        Toast.makeText(BillingShippingActivity.this,"Details Loading Failed! Try Again Later.",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void createBillingShippingCollection(QueryDocumentSnapshot snapshot) {
        BillingShippingDomain billingShippingDomain = new BillingShippingDomain();
        snapshot.getReference().collection("Billing-Shipping").add(billingShippingDomain)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        billingShipping = new BillingShippingDomain();
                        loadingDialog.cancel();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.cancel();
                        Toast.makeText(BillingShippingActivity.this,"Details Loading Failed! Try Again Later.",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadUserDetails(){
        if(billingShipping != null){
            shippingAddressField.setText(billingShipping.getShippingAddress() != null ? billingShipping.getShippingAddress() : "");
            postalCodeField.setText(billingShipping.getPostalCode() != 0 ? String.valueOf(billingShipping.getPostalCode()) : "");
//            mobileNumberField.setText(billingShipping.getMobileNumber() != null ? billingShipping.getMobileNumber() : "");
            if(billingShipping.getMobileNumber() != null && !billingShipping.getMobileNumber().isEmpty()){
                ccp.setFullNumber(billingShipping.getMobileNumber());
            }
            paypalAddressField.setText(billingShipping.getPaymentMethod() != null ? billingShipping.getPaymentMethod() : "");
        }
        loadingDialog.cancel();
    }
    private void saveDetails() {
        String shippingAddress= shippingAddressField.getText().toString();
        int postalCode = 0;
        String postalCodeString = postalCodeField.getText().toString();
        if(!postalCodeString.isEmpty()){
            postalCode = Integer.parseInt(postalCodeString);
        }
        String mobileNumber = ccp.getFullNumberWithPlus();

        billingShipping.setShippingAddress(shippingAddress);
        billingShipping.setPostalCode(postalCode);

        Map<String,Object> map = new HashMap<>();
        map.put("shippingAddress",billingShipping.getShippingAddress());
        map.put("postalCode",billingShipping.getPostalCode());

        String collectionName = null;
        if(userType.equals(CUSTOMERS)){
            collectionName = CUSTOMERS;
        }else if(userType.equals(SELLERS)){
            collectionName = SELLERS;
        }
        if(collectionName != null){
            firestore.collection(collectionName).whereEqualTo("id",userId).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot snapshot : task.getResult()){
                                    snapshot.getReference().collection("Billing-Shipping").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        System.out.println(task.getResult());
                                                        for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                            snapshot1.getReference().update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    if(billingShipping.getMobileNumber() == null || !billingShipping.getMobileNumber().equals(mobileNumber)) {
                                                                        billingShipping.setMobileNumber(mobileNumber);
                                                                        verificationLayout.setVisibility(View.VISIBLE);
                                                                        verifyAndStoreMobile(snapshot1);
                                                                    }else{
                                                                        savingDataDialog.cancel();
                                                                        Toast.makeText(BillingShippingActivity.this,"Saved Successfully!",Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    savingDataDialog.cancel();
                                                                    Toast.makeText(BillingShippingActivity.this,"Failed to save Data! Try again Later.",Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    savingDataDialog.cancel();
                                                    Toast.makeText(BillingShippingActivity.this,"Failed to save Data! Try again Later.",Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            savingDataDialog.cancel();
                            Toast.makeText(BillingShippingActivity.this,"Failed to save Data! Try again Later.",Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void verifyAndStoreMobile(QueryDocumentSnapshot snapshot1) {
        String mobileNumber = billingShipping.getMobileNumber();
        if(ccp.isValidFullNumber()){
            billingShippingSnapshot = snapshot1;
            PhoneAuthProvider.verifyPhoneNumber(
                    PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(mobileNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(callbacks)
                            .build());
            savingDataDialog.cancel();
        }else{
            savingDataDialog.cancel();
            billingShipping.setMobileNumber(null);
            Toast.makeText(BillingShippingActivity.this,"Please Enter Valid Phone Number",Toast.LENGTH_LONG).show();
        }
    }

    private void verifyAndSave(String smsMessage){
        loadingDialog.show();
        System.out.println(smsMessage);
        System.out.println(verificationId);
        if(verificationId.equals(smsMessage)){
            saveMobileNumber();
        }else{
            loadingDialog.cancel();
            Toast.makeText(BillingShippingActivity.this,"Enter correct verification code!",Toast.LENGTH_LONG).show();
        }
    }

    private void verifyAndSave(){
        loadingDialog.show();
        String verficationCode = verificationCodeField1.getText().toString()+verificationCodeField2.getText().toString()+verificationCodeField3.getText().toString()
                +verificationCodeField4.getText().toString()+verificationCodeField5.getText().toString()+verificationCodeField6.getText().toString();
        System.out.println(verficationCode);
        if(verficationCode != null & verficationCode.length() == 6){
            saveMobileNumber();
        }else{
            Toast.makeText(BillingShippingActivity.this,"Enter verification code!",Toast.LENGTH_LONG).show();
        }
    }

    private void saveMobileNumber() {
        if(billingShippingSnapshot != null){
            String mobileNumber = billingShipping.getMobileNumber();
            Map<String,Object> map = new HashMap<>();
            map.put("mobileNumber",mobileNumber);
            billingShippingSnapshot.getReference().update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    clear();
                    Toast.makeText(BillingShippingActivity.this,"Saved Successfully!",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    clear();
                    Toast.makeText(BillingShippingActivity.this,"Saving Process Failed! Try Again later.",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void clear(){
        billingShippingSnapshot = null;
        loadingDialog.cancel();
        verificationLayout.setVisibility(View.GONE);
    }

    public void savePaymentMethod(){
        String paypalAddress = paypalAddressField.getText().toString();

        billingShipping.setPaymentMethod(paypalAddress);

        Map<String,Object> map = new HashMap<>();
        map.put("paymentMethod",billingShipping.getPaymentMethod());

        String collectionName = null;
        if(userType.equals(CUSTOMERS)){
            collectionName = CUSTOMERS;
        }else if(userType.equals(SELLERS)){
            collectionName = SELLERS;
        }

        if(collectionName != null){
            firestore.collection(collectionName).whereEqualTo("id",userId).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot snapshot : task.getResult()){
                                    snapshot.getReference().collection("Billing-Shipping").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                            snapshot1.getReference().update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                        savingDataDialog.cancel();
                                                                        Toast.makeText(BillingShippingActivity.this,"Saved Successfully!",Toast.LENGTH_LONG).show();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    savingDataDialog.cancel();
                                                                    Toast.makeText(BillingShippingActivity.this,"Failed to save Payment method! Try again Later.",Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    savingDataDialog.cancel();
                                                    Toast.makeText(BillingShippingActivity.this,"Failed to save Payment method! Try again Later.",Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            savingDataDialog.cancel();
                            Toast.makeText(BillingShippingActivity.this,"Failed to save Payment method! Try again Later.",Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}