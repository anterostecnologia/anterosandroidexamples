package br.com.anteros.facebook.example;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;

import br.com.anteros.social.core.AnterosSocialNetwork;
import br.com.anteros.social.core.OnLoginListener;
import br.com.anteros.social.core.OnLogoutListener;
import br.com.anteros.social.core.OnProfileListener;
import br.com.anteros.social.core.SocialProfile;
import br.com.anteros.social.facebook.AnterosFacebook;
import br.com.anteros.social.facebook.AnterosFacebookConfiguration;
import br.com.anteros.social.facebook.utils.FacebookUtils;

public class MainActivity extends ActionBarActivity implements
        View.OnClickListener, OnLoginListener, OnLogoutListener {

    private TextView status;
    public static AnterosSocialNetwork anterosFacebook;
    private ImageView userPhoto;
    private TextView detail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        AnterosFacebook.initialize(this);
        FacebookUtils.printHashKey(this.getApplicationContext());

        anterosFacebook = AnterosFacebook.create(new AnterosFacebookConfiguration.Builder()
                .onLoginListener(this)
                .onLogoutListener(this)
                .activity(this).build());

        // Views
        status = (TextView) findViewById(R.id.status);
        userPhoto = (ImageView) findViewById(R.id.user_photo);
        detail = (TextView) findViewById(R.id.detail);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        anterosFacebook.onActivityResult(requestCode, resultCode, data);
    }


    private void updateUI(boolean signedIn)  {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            status.setText("Connected");

            anterosFacebook.getProfile(new OnProfileListener() {

                @Override
                public void onThinking() {
                }

                public void onException(Throwable throwable) {
                    Log.d(MainActivity.class.getName(), "Ocorreu um erro buscando perfil do Facebook do usuário. " + throwable.getMessage());
                    anterosFacebook.logout();
                }

                @Override
                public void onFail(Throwable throwable) {
                }

                @Override
                public void onComplete(SocialProfile profile) {
                    detail.setText(profile.toString());
                    userPhoto.setImageBitmap(profile.getImageBitmap());
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
                anterosFacebook.login();
                break;
            case R.id.sign_out_button:
                anterosFacebook.logout();
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