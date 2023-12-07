package com.vulcan.fandomfinds.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vulcan.fandomfinds.Activity.MainActivity;
import com.vulcan.fandomfinds.Animations.LoadingDialog;
import com.vulcan.fandomfinds.Domain.CustomerDomain;
import com.vulcan.fandomfinds.R;

import org.checkerframework.checker.units.qual.A;

import java.util.UUID;

public class loginFragment extends Fragment {
    private TextInputLayout loginEmailLayout,loginPasswordLayout;
    private TextInputEditText loginEmailText,loginPasswordText;
    private TextView forgotPassword;
    private Button login_button;
    private LinearLayout loginWithGoogle;
    private LoadingDialog loadingDialog;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    SignInClient signInClient;
    CoordinatorLayout loginCoordinator;
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
        forgotPassword = view.findViewById(R.id.forgotPassword);
        loginCoordinator = view.findViewById(R.id.loginCoordinator);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        signInClient = Identity.getSignInClient(getContext());
        loadingDialog = new LoadingDialog(getContext());

        setListeners();
    }

    private void setListeners() {
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
        loginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeginSignInRequest oneTapRequest = BeginSignInRequest.builder()
                        .setGoogleIdTokenRequestOptions(
                                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                        .setSupported(true)
                                        .setServerClientId(getString(R.string.web_client_id))
                                        .setFilterByAuthorizedAccounts(false)
                                        .build()
                        ).build();

                Task<BeginSignInResult> beginSignInResultTask = signInClient.beginSignIn(oneTapRequest);
                beginSignInResultTask.addOnSuccessListener(new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult beginSignInResult) {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(beginSignInResult.getPendingIntent()).build();
                        signInLauncher.launch(intentSenderRequest);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmailText.getText().toString();
                if(!email.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("This will send a password reset email to your email. Do you want to continue?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendPasswordResetEmail(email);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else{
                    loginEmailLayout.setError("Please enter your email");
                    Toast.makeText(getContext(),"Please enter your email!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendPasswordResetEmail(String email){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(),"Please enter your email!",Toast.LENGTH_LONG).show();
                Snackbar snackbar = Snackbar.make(loginCoordinator,"Email Sent Successfully!",Snackbar.LENGTH_LONG);
                snackbar.setAction("Open Email", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        startActivity(Intent.createChooser(intent,"Choose an app"));
                    }
                });
                snackbar.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Please enter your email!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher
            = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    loadingDialog.show();
                    handleSignInResult(o.getData());
                }
            });

    private void handleSignInResult(Intent intent){
        try {
            SignInCredential signInCredential = signInClient.getSignInCredentialFromIntent(intent);
            String idToken = signInCredential.getGoogleIdToken();
            firebaseAuthWithGoogle(idToken);
        }catch (ApiException e){
        }
    }

    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken,null);
        Task<AuthResult> authResultTask = firebaseAuth.signInWithCredential(authCredential);
        authResultTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Failure3",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUI(FirebaseUser user){
        firestore.collection("Sellers").whereEqualTo("email",user.getEmail()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    if (task.getResult().size() == 0){
                                        firestore.collection("Customers").whereEqualTo("email",user.getEmail()).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            if(task.getResult().size() == 0){
                                                                String userId = "CUS_"+ UUID.randomUUID();
                                                                CustomerDomain customerDetails = new CustomerDomain(userId,user.getEmail());
                                                                firestore.collection("Customers").add(customerDetails)
                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentReference documentReference) {
                                                                                loadingDialog.cancel();
                                                                                Toast.makeText(getContext(),"Login Successful!",Toast.LENGTH_LONG).show();
                                                                                loadHome();
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                loadingDialog.cancel();
                                                                                Toast.makeText(getContext(),"Registration Failed!",Toast.LENGTH_LONG).show();
                                                                            }
                                                                        });
                                                            }else{
                                                                loadingDialog.cancel();
                                                                Toast.makeText(getContext(),"Login Successful!",Toast.LENGTH_LONG).show();
                                                                loadHome();
                                                            }
                                                        }
                                                    }
                                                });
                                    }else{
                                        loadingDialog.cancel();
                                        Toast.makeText(getContext(),"Login Successful!",Toast.LENGTH_LONG).show();
                                        loadHome();
                                    }
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