package com.vulcan.fandomfinds.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vulcan.fandomfinds.Domain.CustomerDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.util.UUID;

public class signupFragment extends Fragment {
    TextInputLayout emailLayout,passwordLayout;
    private TextInputEditText emailEditText,passwordEditText;
    private Button sign_up_button;
    private RadioButton sign_up_as_customer,sign_up_as_seller;
    CoordinatorLayout coordinatorLayout;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        //initializing the components
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        sign_up_button = view.findViewById(R.id.sign_up_button);
        sign_up_as_customer = view.findViewById(R.id.sign_up_as_customer);
        sign_up_as_seller = view.findViewById(R.id.sign_up_as_seller);
        emailLayout = view.findViewById(R.id.emailLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        coordinatorLayout = view.findViewById(R.id.coordinatorSignUp);

        firestore = FirebaseFirestore.getInstance();

        //password character length error listener
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() < 8){
                    passwordLayout.setError("Password length should be at least 8 characters!");
                }else{
                    passwordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    emailLayout.setError("Please enter your email!");
                }else{
                    emailLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                boolean customer = sign_up_as_customer.isChecked();
                boolean seller = sign_up_as_seller.isChecked();

                if(!email.isEmpty()){
                    if(!password.isEmpty()){
                        if (password.length() >= 8) {
                            firebaseAuth.createUserWithEmailAndPassword(email,password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                                verfication(user);
                                                saveUserData(user,email);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            String errorMessage = "Registration Failed!";
                                            if(e instanceof FirebaseAuthUserCollisionException){
                                                errorMessage = "This email has already registered!";
                                            }
                                            clearFields();
                                            Toast.makeText(getContext(),errorMessage,Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }else{
                            passwordLayout.setError("Password length should be at least 8 characters!");
                        }
                    }else{
                        passwordLayout.setError("Please enter your password!");
                    }
                }else{
                    emailLayout.setError("Please enter your email!");
                }
            }
        });
    }

    private void loadLoginFragment() {
        for(Fragment fragment : getActivity().getSupportFragmentManager().getFragments()){
            getActivity().getSupportFragmentManager().getFragments().remove(fragment);
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView,loginFragment.class,null)
                .commit();
    }

    private void clearFields() {
        emailEditText.setText("");
        passwordEditText.setText("");
        emailLayout.setError(null);
        passwordLayout.setError(null);
        emailEditText.requestFocus();
    }

    public void verfication(FirebaseUser user){
        if(user != null){
            user.sendEmailVerification();
            @SuppressLint("ShowToast")
            Snackbar snackbar = Snackbar.make(coordinatorLayout,"Verify Your Email",Snackbar.LENGTH_LONG);
            snackbar.setAction("Open Email", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                        startActivity(Intent.createChooser(intent, "Choose an email app"));
                    }
                }
            });
            snackbar.show();
        }
    }

    public void saveUserData(FirebaseUser user,String email){
        boolean customer = sign_up_as_customer.isChecked();
        boolean seller = sign_up_as_seller.isChecked();

        // have to check that the email is verified
        if(user != null){
            if(customer){
                Toast.makeText(getContext(),String.valueOf(customer),Toast.LENGTH_LONG).show();
                String userId = "CUS_"+ UUID.randomUUID();
                CustomerDomain customerDetails = new CustomerDomain(userId,email);
                firestore.collection("Customers").add(customerDetails)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(),"Signed Up Successfully!",Toast.LENGTH_LONG).show();
                                clearFields();
                                loadLoginFragment();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Registration Failed!",Toast.LENGTH_LONG).show();
                            }
                        });
            }else if(seller){
                String userId = "SEL_"+UUID.randomUUID();
                SellerDomain sellerDetails = new SellerDomain(userId,email);
                firestore.collection("Sellers").add(sellerDetails)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(),"Signed Up Successfully!",Toast.LENGTH_LONG).show();
                                clearFields();
                                loadLoginFragment();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Registration Failed!",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
    }

}