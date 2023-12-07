package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vulcan.fandomfinds.R;

public class SplashActivity extends AppCompatActivity {

    ImageView fandomfindsRoundLogo;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setTheme(R.style.Theme_FandomFinds);
        setContentView(R.layout.activity_splash);

        setAnimation();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user != null){
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(SplashActivity.this, SignUpInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },5000);

    }

    private void setAnimation() {
        fandomfindsRoundLogo = findViewById(R.id.fandomfindsRoundLogo);

        AlphaAnimation blinkanimation= new AlphaAnimation(1, 0);
        blinkanimation.setDuration(2000);
        blinkanimation.setInterpolator(new LinearInterpolator());
        blinkanimation.setRepeatCount(Animation.INFINITE);
        blinkanimation.setRepeatMode(Animation.REVERSE);

        fandomfindsRoundLogo.setAnimation(blinkanimation);
    }
}