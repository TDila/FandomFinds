package com.vulcan.fandomfinds.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Animations.LoadingDialog;
import com.vulcan.fandomfinds.Animations.SavingDataDialog;
import com.vulcan.fandomfinds.Domain.BaseDomain;
import com.vulcan.fandomfinds.Domain.CustomerDomain;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Domain.SocialMedia;
import com.vulcan.fandomfinds.Enum.SellerProfileStatus;
import com.vulcan.fandomfinds.R;

import org.checkerframework.checker.units.qual.C;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import io.grpc.internal.JsonParser;

public class ProfileInformationActivity extends AppCompatActivity {
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firestore;
    CustomerDomain customer;
    SellerDomain seller;
    private ImageView profileInfoBackButton;
    LoadingDialog loadingDialog;
    private ImageButton profileImage;
    private LinearLayout publicNameLayout,userBioLayout,socialMediaLayout,profileInCompleteErrorLayout,changePasswordLayout;
    private EditText sellerPublicName,profileInfoFirstname,profileInfoLastname,profileInfoUserEmail,youTube,twitter,facebook,instagram,twitch
            ,newPassword,oldPassword;
    private TextInputEditText profileInfoSellerBio;
    private Button passwordChangeButton,saveChangesButton;
    private final int PICK_IMAGE_CODE = 12;
    private ProgressDialog progressDialog;
    private Uri imagePath;
    private String user_id;
    SavingDataDialog savingDataDialog;
    boolean isTextModified = false;
    private SocialMedia socialMedia;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Uri croppedImgPath;
    String userString;
    String socialMediaString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_information);

        initComponents();
        loadingDialog.show();
        setListeners();

        getUserDetails();
        checkSellerCompleteness();

        for (UserInfo userInfo : firebaseUser.getProviderData()) {
            String providerId = userInfo.getProviderId();
            if(providerId.equals("password")){
                changePasswordLayout.setVisibility(View.VISIBLE);
            }else if (providerId.equals("google.com")){
                changePasswordLayout.setVisibility(View.GONE);
            }
        }
    }

    private void loadCustomerDetails() {
        user_id = customer.getId();
        String fname = customer.getFname();
        String lname = customer.getLname();
        String email = customer.getEmail();
        String imgUrl = customer.getProfilePicUrl();

        profileInfoUserEmail.setText(email);

        if(fname != null){
            profileInfoFirstname.setText(fname);
        }
        if(lname != null){
            profileInfoLastname.setText(lname);
        }
        if(imgUrl != null){
            loadProfileImg(imgUrl);
        }else{
            int drawableResourceId = getResources().getIdentifier("account_default_profile_img","drawable",getPackageName());
            Glide.with(ProfileInformationActivity.this)
                    .load(drawableResourceId)
                    .into(profileImage);
        }
        loadingDialog.cancel();

    }

    private void loadProfileImg(String imgUrl) {
        Intent intent = getIntent();
        String croppedImgPathString = intent.getStringExtra("croppedImgPath");
        if(croppedImgPathString != null){
            croppedImgPath = Uri.parse(croppedImgPathString);
            Picasso.get()
                    .load(croppedImgPath)
                    .into(profileImage);
            imagePath = croppedImgPath;
        }else{
            firebaseStorage.getReference("profile-images/"+imgUrl)
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get()
                                    .load(uri)
                                    .into(profileImage);
                        }
                    });
        }

    }

    private void setListeners() {
        profileInfoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileInformationActivity.this,AccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                checkPermission();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                activityResultLauncher.launch(Intent.createChooser(intent,"Select Image"));
            }
        });
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingDataDialog.show();
                saveChanges();
            }
        });
        passwordChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                changePassword();
            }
        });
    }

    private void saveChanges() {
        if(customer != null){
            saveCustomer();
        }else if(seller != null){
            saveSeller();
        }
    }

    private void saveSeller() {
        String firstName = profileInfoFirstname.getText().toString();
        String lastName = profileInfoLastname.getText().toString();
        String sellerName = sellerPublicName.getText().toString();
        String bio = profileInfoSellerBio.getText().toString();
        String youtubeLink = youTube.getText().toString();
        String twitterLink = twitter.getText().toString();
        String facebookLink = facebook.getText().toString();
        String instagramLink = instagram.getText().toString();
        String twitchLink = twitch.getText().toString();

        seller.setFname(firstName);
        seller.setLname(lastName);
        seller.setSellerName(sellerName);
        seller.setBio(bio);
        Map<String,Object> map = new HashMap<>();
        map.put("fname",seller.getFname());
        map.put("lname",seller.getLname());
        map.put("sellerName",seller.getSellerName());
        map.put("bio",seller.getBio());

        //seller name process for the searching process
        String sellerNameInsensitive = seller.getSellerName().toLowerCase();
        List<String> sellerNameInsensitiveList = new ArrayList<>();
        sellerNameInsensitiveList.add(sellerNameInsensitive);

        String sellerNameInsensitiveWithoutSpaces = sellerNameInsensitive.replaceAll("\\s","");
        sellerNameInsensitiveList.add(sellerNameInsensitiveWithoutSpaces);

        for(int i = 1; i < sellerNameInsensitiveWithoutSpaces.length(); i++){
            String firstNLetters = sellerNameInsensitiveWithoutSpaces.substring(0,i);
            sellerNameInsensitiveList.add(firstNLetters);
        }
        //seller name process for the searching process

        map.put("sellerNameInsensitive",sellerNameInsensitiveList);

        socialMedia.setYoutube(youtubeLink);
        socialMedia.setTwitter(twitterLink);
        socialMedia.setFacebook(facebookLink);
        socialMedia.setInstagram(instagramLink);
        socialMedia.setTwitch(twitchLink);

        String imageId = null;
        if(imagePath != null){
            imageId = UUID.randomUUID().toString();
            seller.setProfilePicUrl(imageId);
            map.put("profilePicUrl",seller.getProfilePicUrl());
        }

        String finalImageId = imageId;
        firestore.collection("Sellers").whereEqualTo("id",user_id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot snapshot : task.getResult()){
                                snapshot.getReference().update(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                saveSocialMediaLinks(snapshot, finalImageId);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                savingDataDialog.cancel();
                                                Toast.makeText(ProfileInformationActivity.this,"Failed to save data. Please try again.!",Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        savingDataDialog.cancel();
                        Toast.makeText(ProfileInformationActivity.this,"Failed to save data. Please try again.!",Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void saveSocialMediaLinks(QueryDocumentSnapshot snapshot,String imageId) {
        Map<String,Object> map = new HashMap<>();
        map.put("youtube",socialMedia.getYoutube());
        map.put("twitter",socialMedia.getTwitter());
        map.put("facebook",socialMedia.getFacebook());
        map.put("instagram",socialMedia.getInstagram());
        map.put("twitch",socialMedia.getTwitch());

        snapshot.getReference().collection("Social-Media").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                snapshot1.getReference().update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if(imageId != null){
                                            uploadImage(imageId);
                                        }else{
                                            savingDataDialog.cancel();
                                            Toast.makeText(ProfileInformationActivity.this,"Profile Successfully Updated!",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        savingDataDialog.cancel();
                                        Toast.makeText(ProfileInformationActivity.this,"Failed to save data. Please try again.!",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void saveCustomer() {
        String firstName = profileInfoFirstname.getText().toString();
        String lastName = profileInfoLastname.getText().toString();

        customer.setFname(firstName);
        customer.setLname(lastName);
        Map<String,Object> map = new HashMap<>();
        map.put("fname",customer.getFname());
        map.put("lname",customer.getLname());

        String imageId = null;
        if(imagePath != null){
            imageId = UUID.randomUUID().toString();
            customer.setProfilePicUrl(imageId);
            map.put("profilePicUrl",customer.getProfilePicUrl());
        }

        String finalImageId = imageId;
        firestore.collection("Customers").whereEqualTo("id",user_id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                DocumentReference reference = snapshot.getReference();
                                reference.update(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                if(finalImageId != null){
                                                    uploadImage(finalImageId);
                                                }else{
                                                    savingDataDialog.cancel();
                                                    Toast.makeText(ProfileInformationActivity.this,"Profile Successfully Updated!",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                savingDataDialog.cancel();
                                                Toast.makeText(ProfileInformationActivity.this,"Failed to save data. Please try again.!",Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        savingDataDialog.cancel();
                        Toast.makeText(ProfileInformationActivity.this,"Failed to save data. Please try again.!",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void initComponents() {
        //firebase
        firebaseStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        profileInfoBackButton = findViewById(R.id.profileInfoBackButton);
        loadingDialog = new LoadingDialog(ProfileInformationActivity.this);
        profileImage = findViewById(R.id.profileImage);
        sellerPublicName = findViewById(R.id.profileInfoSellerPublicName);
        profileInfoFirstname = findViewById(R.id.profileInfoFirstname);
        profileInfoLastname = findViewById(R.id.profileInfoLastname);
        profileInfoUserEmail = findViewById(R.id.profileInfoUserEmail);
        profileInfoSellerBio = findViewById(R.id.profileInfoSellerBio);

        //layouts
        socialMediaLayout = findViewById(R.id.socialMediaLayout);
        publicNameLayout = findViewById(R.id.publicNameLayout);
        userBioLayout = findViewById(R.id.userBioLayout);
        profileInCompleteErrorLayout = findViewById(R.id.profileInCompleteErrorLayout);
        profileInCompleteErrorLayout.setVisibility(View.GONE);
        changePasswordLayout = findViewById(R.id.changePasswordLayout);

        //social media
        youTube = findViewById(R.id.profileInfoSellerYouTube);
        twitter = findViewById(R.id.profileInfoSellerTwitter);
        facebook = findViewById(R.id.profileInfoSellerFacebook);
        instagram = findViewById(R.id.profileInfoSellerInstagram);
        twitch = findViewById(R.id.profileInfoSellerTwitch);

        newPassword = findViewById(R.id.profileInfoNewPassword);
        oldPassword = findViewById(R.id.profileInfoOldPassword);
        passwordChangeButton = findViewById(R.id.profileInfoPasswordChangeButton);
        saveChangesButton = findViewById(R.id.profileInfoSaveChanges);
//        saveChangesButton.setEnabled(false);

        //progress dialog
        progressDialog = new ProgressDialog(this);

        //saving data
        savingDataDialog = new SavingDataDialog(ProfileInformationActivity.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void getUserDetails() {
        userString = getIntent().getStringExtra("user");
        BaseDomain user = new Gson().fromJson(userString,BaseDomain.class);
        String userId = user.getId();
        String identifier = userId.substring(0,Math.min(userId.length(),3));
        if(identifier.equals("CUS")){
            customer = new Gson().fromJson(userString,CustomerDomain.class);
            socialMediaLayout.setVisibility(View.GONE);
            publicNameLayout.setVisibility(View.GONE);
            userBioLayout.setVisibility(View.GONE);
            loadCustomerDetails();
        }else if(identifier.equals("SEL")){
            seller = new Gson().fromJson(userString, SellerDomain.class);
            socialMediaLayout.setVisibility(View.VISIBLE);
            publicNameLayout.setVisibility(View.VISIBLE);
            userBioLayout.setVisibility(View.VISIBLE);

            socialMediaString = getIntent().getStringExtra("socialMedia");
            socialMedia = new Gson().fromJson(socialMediaString,SocialMedia.class);

            loadSellerDetails();
        }
    }

    private void loadSellerDetails() {
        user_id = seller.getId();
        String fname = seller.getFname();
        String lname = seller.getLname();
        String email = seller.getEmail();
        String imgUrl = seller.getProfilePicUrl();
        String sellerName = seller.getSellerName();
        String bio = seller.getBio();

        profileInfoUserEmail.setText(email);

        profileInfoFirstname.setText(isNotEmpty(fname) ? fname : "");
        profileInfoLastname.setText(isNotEmpty(lname) ? lname : "");
        sellerPublicName.setText(isNotEmpty(sellerName) ? sellerName : "");
        profileInfoSellerBio.setText(isNotEmpty(bio) ? bio : "");

        if (isNotEmpty(seller.getSellerName()) && isNotEmpty(seller.getBio()) && isNotEmpty(seller.getProfilePicUrl())) {
            profileInCompleteErrorLayout.setVisibility(View.GONE);
        } else {
            profileInCompleteErrorLayout.setVisibility(View.VISIBLE);
        }

        if(socialMedia != null){
            String youtubeLink = socialMedia.getYoutube();
            String twitterLink = socialMedia.getTwitter();
            String facebookLink = socialMedia.getFacebook();
            String instagramLink = socialMedia.getInstagram();
            String twitchLink = socialMedia.getTwitch();

            youTube.setText(isNotEmpty(youtubeLink) ? youtubeLink : "");
            twitter.setText(isNotEmpty(twitterLink) ? twitterLink : "");
            facebook.setText(isNotEmpty(facebookLink) ? facebookLink : "");
            instagram.setText(isNotEmpty(instagramLink) ? instagramLink : "");
            twitch.setText(isNotEmpty(twitchLink) ? twitchLink : "");
        }
        if(imgUrl != null){
            loadProfileImg(imgUrl);
        }else{
            int drawableResourceId = getResources().getIdentifier("account_default_profile_img","drawable",getPackageName());
            Glide.with(ProfileInformationActivity.this)
                    .load(drawableResourceId)
                    .into(profileImage);
        }
        loadingDialog.cancel();
    }

    private boolean isNotEmpty(String value){
        if(value != null && !value.isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    private void checkPermission(){
        Dexter.withContext(ProfileInformationActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");

                        activityResultLauncher.launch(Intent.createChooser(intent,"Select Image"));
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }


    private void uploadImage(String imageId) {
        if(imagePath != null){
            StorageReference reference = firebaseStorage.getReference("profile-images").child(imageId);
            reference.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    savingDataDialog.cancel();
                    Toast.makeText(ProfileInformationActivity.this,"Saved Successfully!",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    savingDataDialog.cancel();
                    Toast.makeText(ProfileInformationActivity.this,"Image Upload Failed!",Toast.LENGTH_LONG).show();
                }
            });
        }else{
            savingDataDialog.cancel();
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        imagePath = result.getData().getData();

                        Intent intent = new Intent(ProfileInformationActivity.this,ImageCropper.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("user",userString);
                        intent.putExtra("socialMedia",socialMediaString);
                        intent.putExtra("imagePath",String.valueOf(imagePath));
                        startActivity(intent);
                        finish();
                    }
                }
            });

    private void checkSellerCompleteness(){
        if(seller != null) {
            firestore.collection("Sellers").whereEqualTo("email", firebaseUser.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (DocumentChange change : value.getDocumentChanges()) {
                        SellerDomain seller = change.getDocument().toObject(SellerDomain.class);
                        if (change.getType() == DocumentChange.Type.MODIFIED) {
                            Map<String, Object> map = new HashMap<>();
                            if (isNotEmpty(seller.getSellerName()) && isNotEmpty(seller.getBio()) && isNotEmpty(seller.getProfilePicUrl())) {
                                map.put("profileStatus", SellerProfileStatus.COMPLETE);
                                profileInCompleteErrorLayout.setVisibility(View.GONE);
                            } else {
                                map.put("profileStatus", SellerProfileStatus.INCOMPLETE);
                                profileInCompleteErrorLayout.setVisibility(View.VISIBLE);
                            }
                            firestore.collection("Sellers").whereEqualTo("id", seller.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                            snapshot.getReference().update(map);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void changePassword() {
        String oldPasswordString = oldPassword.getText().toString();
        String newPasswordString = newPassword.getText().toString();
        if(newPasswordString != null && !newPasswordString.isEmpty() && oldPasswordString != null && !oldPasswordString.isEmpty()){
            if(newPasswordString.length() >= 8){
                AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),oldPasswordString);

                firebaseUser.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseUser.updatePassword(newPasswordString).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                loadingDialog.cancel();
                                Toast.makeText(ProfileInformationActivity.this,"Password successfully changed!",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.cancel();
                                Toast.makeText(ProfileInformationActivity.this,"Something went wrong! Try again Later.",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.cancel();
                        Toast.makeText(ProfileInformationActivity.this,"Authentication Failed!",Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                loadingDialog.cancel();
                Toast.makeText(ProfileInformationActivity.this,"New password must be at least 8 characters!",Toast.LENGTH_LONG).show();
            }
        }else{
            loadingDialog.cancel();
            Toast.makeText(ProfileInformationActivity.this,"Please fill the password fields!",Toast.LENGTH_LONG).show();
        }
//        firebaseAuth.sendPasswordResetEmail(firebaseUser.getEmail())
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(ProfileInformationActivity.this,"Email sent",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
    }

}