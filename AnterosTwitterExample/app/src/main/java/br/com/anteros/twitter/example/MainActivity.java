package br.com.anteros.twitter.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.anteros.social.core.AnterosSocialNetwork;
import br.com.anteros.social.core.OnLoginListener;
import br.com.anteros.social.core.OnLogoutListener;
import br.com.anteros.social.core.OnProfileListener;
import br.com.anteros.social.core.SocialProfile;
import br.com.anteros.social.twitter.AnterosTwitter;
import br.com.anteros.social.twitter.AnterosTwitterConfiguration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnLoginListener, OnLogoutListener {

    private static final String TWITTER_KEY = "KBa81M9tS4z2lYVqkrIT3xltW";
    private static final String TWITTER_SECRET = "XFhlicmGTLHr9Y6wTG3VJqczTi4k8DMxJ3DGye7Vb5fzA4rqDD";
    private AnterosSocialNetwork anterosTwitter;
    private TextView status;
    private ImageView userPhoto;
    private TextView detail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        anterosTwitter = AnterosTwitter.create(new AnterosTwitterConfiguration.Builder().activity(this)
                .twitterkey(TWITTER_KEY).twitterSecret(TWITTER_SECRET)
                .onLoginListener(this)
                .onLogoutListener(this).build());


        setContentView(R.layout.activity_main);
        status = (TextView) findViewById(R.id.status);
        userPhoto = (ImageView) findViewById(R.id.user_photo);
        detail = (TextView) findViewById(R.id.detail);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        anterosTwitter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                anterosTwitter.login();
                break;
            case R.id.sign_out_button:
                anterosTwitter.logout();
                break;
            case R.id.disconnect_button:
                anterosTwitter.revoke();
                break;
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            status.setText("Connected");

            anterosTwitter.getProfile(new OnProfileListener() {
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

    private void desconectado() {
        status.setText("Desconnected");

        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        detail.setText("");
        userPhoto.setImageBitmap(null);
    }
}