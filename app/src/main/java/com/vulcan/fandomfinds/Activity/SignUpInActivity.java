package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vulcan.fandomfinds.Fragments.loginFragment;
import com.vulcan.fandomfinds.Fragments.signupFragment;
import com.vulcan.fandomfinds.R;

public class SignUpInActivity extends AppCompatActivity {

    private TextView login,signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_in);

        login = findViewById(R.id.sign_up_in_login_txt);
        signUp = findViewById(R.id.sign_up_in_signup_txt);

        FragmentContainerView fragmentContainerView = findViewById(R.id.fragmentContainerView);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView, loginFragment.class,null)
                .commit();

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
    }

    private void clearFragmentContainer(){
        for (Fragment fragment : getSupportFragmentManager().getFragments()){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }
}