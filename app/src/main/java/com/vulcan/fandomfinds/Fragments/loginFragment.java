package com.vulcan.fandomfinds.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vulcan.fandomfinds.Activity.MainActivity;
import com.vulcan.fandomfinds.Animations.LoadingDialog;
import com.vulcan.fandomfinds.R;
public class loginFragment extends Fragment {
    private TextInputLayout loginEmailLayout,loginPasswordLayout;
    private TextInputEditText loginEmailText,loginPasswordText;
    private Button login_button;
    private LinearLayout loginWithGoogle;
    private LoadingDialog loadingDialog;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initializing component
        loginEmailLayout = view.findViewById(R.id.loginEmailLayout);
        loginPasswordLayout = view.findViewById(R.id.loginPasswordLayout);
        loginEmailText = view.findViewById(R.id.loginEmailText);
        loginPasswordText = view.findViewById(R.id.loginPasswordText);
        login_button = view.findViewById(R.id.login_button);
        loginWithGoogle = view.findViewById(R.id.loginWithGoogle);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(getContext());


        loginEmailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    loginEmailLayout.setError("Please enter your email");
                }else{
                    loginEmailLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        loginPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    loginPasswordLayout.setError("Please enter your password");
                }else{
                    loginPasswordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmailText.getText().toString();
                String password = loginPasswordText.getText().toString();

                if(!email.isEmpty()){
                    if(!password.isEmpty()){
                        loadingDialog.show();
                        firebaseAuth.signInWithEmailAndPassword(email,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        if(task.isSuccessful()){
                                            checkVerification(user);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingDialog.cancel();
                                        String errorMessage = "Login Failure! Try Again Later.";
                                        if(e instanceof FirebaseAuthInvalidCredentialsException){
                                            errorMessage = "Invalid Credentials!";
                                        }
                                        Toast.makeText(getContext(),errorMessage,Toast.LENGTH_LONG).show();
                                        clearFields();
                                    }
                                });
                    }else{
                        loginPasswordLayout.setError("Please enter your password");
                    }
                }else{
                    loginEmailLayout.setError("Please enter your email");
                }
            }
        });
    }

    private void clearFields() {
        loginEmailText.setText("");
        loginPasswordText.setText("");
        loginEmailText.requestFocus();
    }

    private void checkVerification(FirebaseUser user){
        if(user != null){
            if(user.isEmailVerified()){
                loadingDialog.cancel();
                Toast.makeText(getContext(),"Login Successful!",Toast.LENGTH_LONG).show();
                loadHome();
            }else{
                loadingDialog.cancel();
                Toast.makeText(getContext(),"Please Verify Your Email",Toast.LENGTH_LONG).show();
            }
        }else{
            loadingDialog.cancel();
            Toast.makeText(getContext(),"Login Failed! Try again Later.",Toast.LENGTH_LONG).show();
            clearFields();
            loginEmailLayout.setError(null);
            loginPasswordLayout.setError(null);
        }
    }

    private void loadHome(){
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        getActivity().finish();
    }
}