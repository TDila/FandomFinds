package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vulcan.fandomfinds.Fragments.loginFragment;
import com.vulcan.fandomfinds.Fragments.signupFragment;
import com.vulcan.fandomfinds.R;

public class SignUpInActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    private TextView login,signUp,skipButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_in);

        checkLoggedIn();

        initComponents();

        FragmentContainerView fragmentContainerView = findViewById(R.id.fragmentContainerView);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView, loginFragment.class,null)
                .commit();

        setListeners();
    }

    private void setListeners() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFragmentContainer();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentContainerView,loginFragment.class,null)
                        .commit();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFragmentContainer();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentContainerView, signupFragment.class,null)
                        .commit();
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpInActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void checkLoggedIn() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(SignUpInActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void initComponents() {
        login = findViewById(R.id.sign_up_in_login_txt);
        signUp = findViewById(R.id.sign_up_in_signup_txt);
        skipButton = findViewById(R.id.skipButton);
    }

    private void clearFragmentContainer(){
        for (Fragment fragment : getSupportFragmentManager().getFragments()){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }
}