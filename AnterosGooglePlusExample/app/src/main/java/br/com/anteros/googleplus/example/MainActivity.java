package br.com.anteros.googleplus.example;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.anteros.social.core.AnterosSocialNetwork;
import br.com.anteros.social.core.OnLoginListener;
import br.com.anteros.social.core.OnLogoutListener;
import br.com.anteros.social.core.OnProfileListener;
import br.com.anteros.social.core.SocialProfile;
import br.com.anteros.social.google.AnterosGoogle;
import br.com.anteros.social.google.AnterosGoogleConfiguration;

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class MainActivity extends ActionBarActivity implements
        View.OnClickListener, OnLoginListener, OnLogoutListener {

    private TextView status;
    public static AnterosSocialNetwork anterosGoogle;
    private ImageView userPhoto;
    private TextView detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        status = (TextView) findViewById(R.id.status);
        userPhoto = (ImageView) findViewById(R.id.user_photo);
        detail = (TextView) findViewById(R.id.detail);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        anterosGoogle = AnterosGoogle.create(new AnterosGoogleConfiguration.Builder().activity(this)
                .onLoginListener(this)
                .onLogoutListener(this).build());
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        anterosGoogle.onActivityResult(requestCode, resultCode, data);
    }


    private void updateUI(boolean signedIn)  {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            status.setText("Connected");

            anterosGoogle.getProfile(new OnProfileListener(){

                @Override
                public void onThinking() {
                }

                @Override
                public void onFail(Throwable throwable) {
                    desconectado();
                }

                @Override
                public void onComplete(SocialProfile response) {
                    detail.setText(response.toString());
                    userPhoto.setImageBitmap(response.getImageBitmap());
                }
            });
        } else {
            desconectado();
        }
    }

    private void desconectado() {
        status.setText("Desconnected");

        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        detail.setText("");
        userPhoto.setImageBitmap(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                anterosGoogle.login();
                break;
            case R.id.sign_out_button:
                anterosGoogle.logout();
                break;

        }
    }

    @Override
    public void onLogin() {
        updateUI(true);
    }

    @Override
    public void onCancel() {
        updateUI(false);
    }

    @Override
    public void onFail(Throwable throwable) {
        updateUI(false);
    }

    @Override
    public void onLogout() {
        updateUI(false);
    }

}