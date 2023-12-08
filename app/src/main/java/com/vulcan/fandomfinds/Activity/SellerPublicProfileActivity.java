package com.vulcan.fandomfinds.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Animations.LoadingDialog;
import com.vulcan.fandomfinds.Domain.Follower;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Fragments.SellerAboutFragment;
import com.vulcan.fandomfinds.Fragments.SellerStoreFragment;
import com.vulcan.fandomfinds.Helper.ManagementCart;
import com.vulcan.fandomfinds.R;

import java.util.HashMap;
import java.util.Map;

public class SellerPublicProfileActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    private SellerDomain seller;
    private ImageView profileSellerImg;
    private TextView store_text,about_text,sellerName,followerCount,followButton;
    private int currentFollowerCount;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_public_profile);

        initComponents();

        checkLoggedIn();

    }

    private void checkLoggedIn() {
        if(user == null){
            Toast.makeText(SellerPublicProfileActivity.this,"Log in to check seller store.",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SellerPublicProfileActivity.this,SignUpInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }else{
            Intent intent = getIntent();
            String sellerDetails = intent.getStringExtra("seller");
            seller = (new Gson()).fromJson(sellerDetails, SellerDomain.class);

            setListeners();

            loadingDialog.show();
            loadStreamerDetails();
            loadStore();


            firestore.collection("Sellers").whereEqualTo("id",seller.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(DocumentChange change : value.getDocumentChanges()){
                        SellerDomain sellerChange = change.getDocument().toObject(SellerDomain.class);
                        followerCount.setText(sellerChange.getFollowers()+" followers");
                        currentFollowerCount = sellerChange.getFollowers();
                    }
                }
            });
        }
    }

    private void setListeners() {
        about_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                for (Fragment fragment : getSupportFragmentManager().getFragments()){
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                loadAbout();
            }
        });

        store_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                for (Fragment fragment : getSupportFragmentManager().getFragments()){
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                loadStore();
            }
        });
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(followButton.getText().equals("follow")){
                    loadingDialog.show();
                    followProcess();
                }else if(followButton.getText().equals("following")){
                    loadingDialog.show();
                    unfollowProcess();
                }
            }
        });
    }

    private void unfollowProcess() {
        if(seller != null){
            int newFollowerCount = currentFollowerCount - 1;
            seller.setFollowers(newFollowerCount);
            Map<String,Object> map = new HashMap<>();
            map.put("followers",seller.getFollowers());

            firestore.collection("Sellers").whereEqualTo("id",seller.getId()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot snapshot : task.getResult()){
                                    snapshot.getReference().collection("Followers").whereEqualTo("email",user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                    snapshot.getReference().update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            snapshot1.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    customerUnfollowingProcess();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    loadingDialog.cancel();
                                                                    Toast.makeText(SellerPublicProfileActivity.this,"Unfollowing Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            loadingDialog.cancel();
                                                            Toast.makeText(SellerPublicProfileActivity.this,"Unfollowing Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loadingDialog.cancel();
                                            Toast.makeText(SellerPublicProfileActivity.this,"Unfollowing Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.cancel();
                            Toast.makeText(SellerPublicProfileActivity.this,"Unfollowing Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void customerUnfollowingProcess() {
        firestore.collection("Customers").whereEqualTo("email",user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                snapshot.getReference().collection("Following").whereEqualTo("id",seller.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){
                                            for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                snapshot1.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        loadingDialog.cancel();
                                                        followButton.setText("follow");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        loadingDialog.cancel();
                                                        Toast.makeText(SellerPublicProfileActivity.this,"Unfollowing Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingDialog.cancel();
                                        Toast.makeText(SellerPublicProfileActivity.this,"Unfollowing Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.cancel();
                        Toast.makeText(SellerPublicProfileActivity.this,"Unfollowing Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                    }
                });

        firestore.collection("Sellers").whereEqualTo("email",user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                snapshot.getReference().collection("Following").whereEqualTo("id",seller.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){
                                            for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                snapshot1.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        loadingDialog.cancel();
                                                        followButton.setText("follow");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        loadingDialog.cancel();
                                                        Toast.makeText(SellerPublicProfileActivity.this,"Unfollowing Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingDialog.cancel();
                                        Toast.makeText(SellerPublicProfileActivity.this,"Unfollowing Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.cancel();
                        Toast.makeText(SellerPublicProfileActivity.this,"Unfollowing Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void followProcess() {
        if(seller != null){
            int newFollowerCount = currentFollowerCount + 1;
            seller.setFollowers(newFollowerCount);
            Map<String,Object> map = new HashMap<>();
            map.put("followers",seller.getFollowers());

            firestore.collection("Sellers").whereEqualTo("id",seller.getId()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot snapshot : task.getResult()){
                                    Follower follower = new Follower(user.getEmail());
                                    snapshot.getReference().collection("Followers").add(follower).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if(task.isSuccessful()){
                                                snapshot.getReference().update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
//                                                        updateFollowerCount(map,snapshot);
                                                        customerFollowingProcess();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        loadingDialog.cancel();
                                                        Toast.makeText(SellerPublicProfileActivity.this,"Following Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.cancel();
                            Toast.makeText(SellerPublicProfileActivity.this,"Following Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

//    private void updateFollowerCount(Map<String, Object> map,QueryDocumentSnapshot snapshot) {
//        snapshot.getReference().update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                customerFollowingProcess();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                loadingDialog.cancel();
//                Toast.makeText(SellerPublicProfileActivity.this,"Following Process Failed! Try again later.",Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    private void customerFollowingProcess() {
        firestore.collection("Customers").whereEqualTo("email",user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                snapshot.getReference().collection("Following").add(seller).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        loadingDialog.cancel();
                                        followButton.setText("following");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingDialog.cancel();
                                        Toast.makeText(SellerPublicProfileActivity.this,"Following Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.cancel();
                        Toast.makeText(SellerPublicProfileActivity.this,"Following Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                    }
                });

        firestore.collection("Sellers").whereEqualTo("email",user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                snapshot.getReference().collection("Following").add(seller).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        loadingDialog.cancel();
                                        followButton.setText("following");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingDialog.cancel();
                                        Toast.makeText(SellerPublicProfileActivity.this,"Following Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.cancel();
                        Toast.makeText(SellerPublicProfileActivity.this,"Following Process Failed! Try again later.",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadStreamerDetails() {
        if(seller != null){
            sellerName.setText(seller.getSellerName() != null ? seller.getSellerName():"Seller Name");
            String imageUrl = seller.getProfilePicUrl();
            if(imageUrl != null && !imageUrl.isEmpty()){
                firebaseStorage.getReference("profile-images/"+imageUrl).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get()
                                        .load(uri)
                                        .into(profileSellerImg);
                            }
                        });
            }else{
                int drawableResourceId = getResources().getIdentifier("account_default_profile_img","drawable",getPackageName());
                Glide.with(SellerPublicProfileActivity.this)
                        .load(drawableResourceId)
                        .into(profileSellerImg);
            }
            firestore.collection("Customers").whereEqualTo("email",user.getEmail()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot snapshot : task.getResult()){
                                    snapshot.getReference().collection("Following").whereEqualTo("email",seller.getEmail()).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        if(task.getResult().size() != 0){
                                                            followButton.setText("following");
                                                        }
                                                    }
                                                }
                                            });
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
                                    snapshot.getReference().collection("Following").whereEqualTo("email",seller.getEmail()).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        if(task.getResult().size() != 0){
                                                            followButton.setText("following");
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

    public void loadStore(){
        String sellerString = (new Gson()).toJson(seller);

        Bundle bundle = new Bundle();
        bundle.putString("seller", sellerString);

        SellerStoreFragment sellerStoreFragment = new SellerStoreFragment();
        sellerStoreFragment.setArguments(bundle);

        for (Fragment fragment : getSupportFragmentManager().getFragments()){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView5,sellerStoreFragment,null)
                .commit();
        loadingDialog.cancel();
    }
    public void loadAbout(){
        String sellerString = (new Gson()).toJson(seller);

        Bundle bundle = new Bundle();
        bundle.putString("seller", sellerString);

        Fragment sellerAboutFragment = new SellerAboutFragment();
        sellerAboutFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView5,sellerAboutFragment,null)
                .commit();
        loadingDialog.cancel();
    }
    private void initComponents() {
        store_text = findViewById(R.id.seller_public_profile_store_text);
        about_text =findViewById(R.id.seller_public_profile_about_text);
        sellerName = findViewById(R.id.seller_public_profile_streamer_name);
        followerCount = findViewById(R.id.followerCount);
        followButton = findViewById(R.id.seller_public_profile_follow);
        profileSellerImg = findViewById(R.id.publicProfileSellerImg);

        //firebaseStorage
        firebaseStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        loadingDialog = new LoadingDialog(SellerPublicProfileActivity.this);
    }
}