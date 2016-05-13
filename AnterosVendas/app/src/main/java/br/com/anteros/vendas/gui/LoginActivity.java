package br.com.anteros.vendas.gui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.anteros.social.core.AnterosSocialNetwork;
import br.com.anteros.social.core.OnLoginListener;
import br.com.anteros.social.core.OnLogoutListener;
import br.com.anteros.social.core.OnProfileListener;
import br.com.anteros.social.core.SocialProfile;
import br.com.anteros.social.core.component.AnterosSocialFloatingActionButton;
import br.com.anteros.social.facebook.AnterosFacebook;
import br.com.anteros.social.facebook.AnterosFacebookConfiguration;
import br.com.anteros.social.google.AnterosGoogle;
import br.com.anteros.social.google.AnterosGoogleConfiguration;
import br.com.anteros.social.instagram.AnterosInstagram;
import br.com.anteros.social.instagram.AnterosInstagramConfiguration;
import br.com.anteros.vendas.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, View.OnClickListener, OnLoginListener, OnLogoutListener {

    public static final String CLIENT_ID = "93a34b2080a34ccbadf58fe98595849b";
    public static final String CLIENT_SECRET = "8cd89592ce8c45d7869fc81164856726";
    public static final String REDIRECT_URL = "http://www.anteros.com.br";

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private FloatingActionButton mBtnFacebook;
    private FloatingActionButton mBtnGoogle;
    private FloatingActionButton mBtnInstagram;
    public static AnterosSocialNetwork anterosGoogle;
    public static AnterosSocialNetwork anterosFacebook;
    public static AnterosSocialNetwork anterosInstagram;
    private static final int LOGIN_FACEBOOK = 0;
    private static final int LOGIN_GOOGLE = 1;
    private static final int LOGIN_INSTAGRAM = 2;

    private int loginType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        FloatingActionButton mEmailSignInButton = (FloatingActionButton) findViewById(R.id.main_search_floatLogin);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        mBtnFacebook = (FloatingActionButton) findViewById(R.id.activity_login_floatFacebook);
        mBtnGoogle = (FloatingActionButton) findViewById(R.id.activity_login_floatGoogle);
        mBtnInstagram = (FloatingActionButton) findViewById(R.id.activity_login_floatInstagram);

        mBtnFacebook.setOnClickListener(this);
        mBtnGoogle.setOnClickListener(this);
        mBtnInstagram.setOnClickListener(this);

        anterosFacebook = AnterosFacebook.create(new AnterosFacebookConfiguration.Builder()
                .onLoginListener(this)
                .onLogoutListener(this)
                .activity(this).build());

        anterosGoogle = AnterosGoogle.create(new AnterosGoogleConfiguration.Builder().activity(this)
                .onLoginListener(this)
                .onLogoutListener(this).build());

        anterosInstagram = AnterosInstagram.create(new AnterosInstagramConfiguration.Builder()
                .onLoginListener(this)
                .onLogoutListener(this)
                .clientId(CLIENT_ID).clientSecret(CLIENT_SECRET).redirectURL(REDIRECT_URL).scope(null)
                .activity(this).build());

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_login_floatFacebook:
                loginType = LOGIN_FACEBOOK;
                anterosFacebook.login();
                break;
            case R.id.activity_login_floatGoogle:
                loginType = LOGIN_GOOGLE;
                anterosGoogle.login();
                break;
            case R.id.activity_login_floatInstagram:
                loginType = LOGIN_INSTAGRAM;
                anterosInstagram.login();
                break;
        }
    }

    @Override
    public void onLogin() {
        switch (loginType){
            case LOGIN_FACEBOOK:
                anterosFacebook.getProfile(new OnProfileListener() {

                    @Override
                    public void onThinking() {
                    }

                    public void onException(Throwable throwable) {
                        Log.d(LoginActivity.class.getName(), "Ocorreu um erro buscando perfil do Facebook do usuário. " + throwable.getMessage());
                        anterosFacebook.logout();
                    }

                    @Override
                    public void onFail(Throwable throwable) {
                    }

                    @Override
                    public void onComplete(SocialProfile profile) {
                        MenuActivity.perfilUsuario = profile;
                        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                        finish();
                    }
                });
                break;
            case LOGIN_GOOGLE:
                anterosGoogle.getProfile(new OnProfileListener() {

                    @Override
                    public void onThinking() {
                    }

                    public void onException(Throwable throwable) {
                        Log.d(LoginActivity.class.getName(), "Ocorreu um erro buscando perfil do Google do usuário. " + throwable.getMessage());
                        anterosGoogle.logout();
                    }

                    @Override
                    public void onFail(Throwable throwable) {
                    }

                    @Override
                    public void onComplete(SocialProfile profile) {
                        MenuActivity.perfilUsuario = profile;
                        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                        finish();
                    }
                });
                break;
            case LOGIN_INSTAGRAM:
                anterosInstagram.getProfile(new OnProfileListener() {

                    @Override
                    public void onThinking() {
                    }

                    public void onException(Throwable throwable) {
                        Log.d(LoginActivity.class.getName(), "Ocorreu um erro buscando perfil do Instagram do usuário. " + throwable.getMessage());
                        anterosInstagram.logout();
                    }

                    @Override
                    public void onFail(Throwable throwable) {
                    }

                    @Override
                    public void onComplete(SocialProfile profile) {
                        MenuActivity.perfilUsuario = profile;
                        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                        finish();
                    }
                });
                break;
        }



    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onFail(Throwable throwable) {

    }

    @Override
    public void onLogout() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        anterosFacebook.onActivityResult(requestCode, resultCode, data);
        anterosGoogle.onActivityResult(requestCode, resultCode, data);
        anterosInstagram.onActivityResult(requestCode, resultCode, data);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    return pieces[1].equals(mPassword);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}



//AIzaSyBYNhvlP8I93E1GhYKH3cVtU3UToZquWok