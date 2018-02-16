package com.blp.outilcartographique.wifistate;

/**
 * Created by Alexis on 19/05/2017.
 */


        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.app.Activity;
        import android.os.Bundle;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;

        import com.dropbox.core.android.Auth;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button SignInButton = (Button) findViewById(R.id.sign_in_button);
        SignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.startOAuth2Authentication(getApplicationContext(), getString(R.string.APP_KEY));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAccessToken();
    }

    public void getAccessToken() {
        String accessToken = Auth.getOAuth2Token(); //generate Access Token
        if (accessToken != null) {
            //Store accessToken in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("com.polytech.OutilCarthographique", Context.MODE_PRIVATE);
            prefs.edit().putString("access-token", accessToken).apply();

            //Proceed to MainActivityDropbox
            Intent intent = new Intent(LoginActivity.this, MainActivityDropbox.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed (){
        Intent backAccueil = new Intent(LoginActivity.this, Accueil.class);
        startActivity(backAccueil);
    }
}