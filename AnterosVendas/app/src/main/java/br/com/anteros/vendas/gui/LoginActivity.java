/*
 * Copyright 2016 Anteros Tecnologia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.anteros.vendas.gui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

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
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.social.core.AnterosSocialNetwork;
import br.com.anteros.social.core.OnLoginListener;
import br.com.anteros.social.core.OnLogoutListener;
import br.com.anteros.social.core.OnProfileListener;
import br.com.anteros.social.core.SocialProfile;
import br.com.anteros.social.facebook.AnterosFacebook;
import br.com.anteros.social.facebook.AnterosFacebookConfiguration;
import br.com.anteros.social.google.AnterosGoogle;
import br.com.anteros.social.google.AnterosGoogleConfiguration;
import br.com.anteros.social.instagram.AnterosInstagram;
import br.com.anteros.social.instagram.AnterosInstagramConfiguration;
import br.com.anteros.vendas.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Activity responsável pelo login da aplicação;
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 12/05/16.
 */

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, View.OnClickListener, OnLoginListener, OnLogoutListener {

    /**
     * Dados da aplicação cadastrados no Instragram.
     */
    public static final String CLIENT_ID = "93a34b2080a34ccbadf58fe98595849b";
    public static final String CLIENT_SECRET = "8cd89592ce8c45d7869fc81164856726";
    public static final String REDIRECT_URL = "http://www.anteros.com.br";


    private static final int REQUEST_READ_CONTACTS = 0;


    public static final String SENHA = "SENHA";
    public static final String USUARIO = "USUARIO";

    private AutoCompleteTextView login;
    private EditText senha;
    private FloatingActionButton btnFacebook;
    private FloatingActionButton btnGoogle;
    private FloatingActionButton btnInstagram;
    public static AnterosSocialNetwork anterosGoogle;
    public static AnterosSocialNetwork anterosFacebook;
    public static AnterosSocialNetwork anterosInstagram;
    private static final int LOGIN_FACEBOOK = 0;
    private static final int LOGIN_GOOGLE = 1;
    private static final int LOGIN_INSTAGRAM = 2;

    private int loginType;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (AutoCompleteTextView) findViewById(R.id.email);
        carregarAutoComplete();

        senha = (EditText) findViewById(R.id.password);
        senha.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    executaLogin();
                    return true;
                }
                return false;
            }
        });

        FloatingActionButton mEmailSignInButton = (FloatingActionButton) findViewById(R.id.main_search_floatLogin);
        /**
         * Atribui evento responsável por executar o login.
         */
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                executaLogin();
            }
        });


        btnFacebook = (FloatingActionButton) findViewById(R.id.activity_login_floatFacebook);
        btnGoogle = (FloatingActionButton) findViewById(R.id.activity_login_floatGoogle);
        btnInstagram = (FloatingActionButton) findViewById(R.id.activity_login_floatInstagram);

        /**
         * Atribui o evento de onClick no botões de rede social
         */
        btnFacebook.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        btnInstagram.setOnClickListener(this);

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

        /**
         * Carrega usuário e senha salvo nas preferências.
         */
        carregarLoginSenha();

    }

    /**
     * Carrega o autocomplete
     */
    private void carregarAutoComplete() {
        if (!verificaSePodeUsarContatos()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Verifica se tem permissão para usar os contatos no autocomplete do e-mail
     * @return
     */
    private boolean verificaSePodeUsarContatos() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(login, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
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
     * Evento que recebe resultado da requisição de permissões
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        /**
         * Se tem permissão para ler os contatos carrega no autocomplete
         */
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                carregarAutoComplete();
            }
        }
    }


    /**
     * Executa o login
     */
    private void executaLogin() {
        login.setError(null);
        senha.setError(null);

        /**
         * Obtém os valores do login e senha
         */
        String login = this.login.getText().toString();
        String password = senha.getText().toString();

        boolean cancel = false;
        View focusView = null;

        /**
         * Valida campos
          */
        if (!TextUtils.isEmpty(password) && !isSenhaValida(password)) {
            senha.setError(getString(R.string.error_invalid_password));
            focusView = senha;
            cancel = true;
        }

        /**
         * Verifica se o login(e-mail) é valido
          */
        if (TextUtils.isEmpty(login)) {
            this.login.setError(getString(R.string.error_field_required));
            focusView = this.login;
            cancel = true;
        } else if (!isEmailValido(login)) {
            this.login.setError(getString(R.string.error_invalid_email));
            focusView = this.login;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            /**
             * Valida o usuário e senha
             */
            validarLoginSenha();
            /**
             * Salva dados nas preferências
             */
            salvarLoginSenha();
            /**
             * Inicia o MenuActivity.
             */
            startActivity(new Intent(LoginActivity.this, MenuActivity.class));
            /**
             * Finaliza o login.
             */
            finish();
        }
    }

    /**
     * Salva o login e senha nas preferências
     */
    private void salvarLoginSenha() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (StringUtils.isNotEmpty(login.getText()+"")) {
            editor.putString(USUARIO, login.getText() + "");
        }
        if (StringUtils.isNotEmpty(senha.getText()+"")) {
            editor.putString(SENHA, senha.getText() + "");
        }

        editor.commit();
    }

    /**
     * Carrega o login e senha salvo nas preferências
     */
    private void carregarLoginSenha() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        login.setText(sharedPref.getString(USUARIO, login.getText() + ""));
        senha.setText(sharedPref.getString(SENHA, senha.getText() + ""));
    }

    /**
     * Fazer aqui a validação do login e senha
     */
    private void validarLoginSenha() {

    }

    private boolean isEmailValido(String email) {
        return email.contains("@");
    }

    private boolean isSenhaValida(String password) {
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

        adicionaEmailsNoAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void adicionaEmailsNoAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        login.setAdapter(adapter);
    }

    /**
     * Evento para tratar onClick nos botões da rede social.
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_login_floatFacebook:
                /**
                 * Executa o login no Facebook
                 */
                loginType = LOGIN_FACEBOOK;
                anterosFacebook.login();
                break;
            case R.id.activity_login_floatGoogle:
                /**
                 * Executa o login no Google
                 */
                loginType = LOGIN_GOOGLE;
                anterosGoogle.login();
                break;
            case R.id.activity_login_floatInstagram:
                /**
                 * Executa o login no Instagram
                 */
                loginType = LOGIN_INSTAGRAM;
                anterosInstagram.login();
                break;
        }
    }

    /**
     * Evento ocorre quando executou o login em uma rede social.
     */
    @Override
    public void onLogin() {
        switch (loginType){
            case LOGIN_FACEBOOK:
                /**
                 * Busca o perfil do usuário no Facebook.
                 */
                anterosFacebook.getProfile(new OnProfileListener() {

                    @Override
                    public void onThinking() {
                        progressDialog = ProgressDialog.show(LoginActivity.this, getResources()
                                .getString(R.string.app_name), "Buscando perfil...");
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                    }

                    public void onException(Throwable throwable) {
                        progressDialog.dismiss();
                        Log.d(LoginActivity.class.getName(), "Ocorreu um erro buscando perfil do Facebook do usuário. " + throwable.getMessage());
                        anterosFacebook.logout();
                    }

                    @Override
                    public void onFail(Throwable throwable) {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onComplete(SocialProfile profile) {
                        progressDialog.dismiss();
                        MenuActivity.perfilUsuario = profile;
                        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                        finish();
                    }
                });
                break;
            case LOGIN_GOOGLE:
                /**
                 * Busca o perfil do usuário no Google
                 */
                anterosGoogle.getProfile(new OnProfileListener() {

                    @Override
                    public void onThinking() {
                        progressDialog = ProgressDialog.show(LoginActivity.this, getResources()
                                .getString(R.string.app_name), "Buscando perfil...");
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                    }

                    public void onException(Throwable throwable) {
                        progressDialog.dismiss();
                        Log.d(LoginActivity.class.getName(), "Ocorreu um erro buscando perfil do Google do usuário. " + throwable.getMessage());
                        anterosGoogle.logout();
                    }

                    @Override
                    public void onFail(Throwable throwable) {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onComplete(SocialProfile profile) {
                        progressDialog.dismiss();
                        MenuActivity.perfilUsuario = profile;
                        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                        finish();
                    }
                });
                break;
            case LOGIN_INSTAGRAM:
                /**
                 * Busca o perfil do usuário no Instagram.
                 */
                anterosInstagram.getProfile(new OnProfileListener() {

                    @Override
                    public void onThinking() {
                        progressDialog = ProgressDialog.show(LoginActivity.this, getResources()
                                .getString(R.string.app_name), "Buscando perfil...");
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                    }

                    public void onException(Throwable throwable) {
                        progressDialog.dismiss();
                        Log.d(LoginActivity.class.getName(), "Ocorreu um erro buscando perfil do Instagram do usuário. " + throwable.getMessage());
                        anterosInstagram.logout();
                    }

                    @Override
                    public void onFail(Throwable throwable) {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onComplete(SocialProfile profile) {
                        progressDialog.dismiss();
                        MenuActivity.perfilUsuario = profile;
                        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                        finish();
                    }
                });
                break;
        }



    }

    /**
     * Se o login foi cancelado.
     */
    @Override
    public void onCancel() {

    }

    /**
     * Se ocorreu um erro no login
     * @param throwable Erro
     */
    @Override
    public void onFail(Throwable throwable) {

    }

    /**
     * Se executou logout na rede social.
     */
    @Override
    public void onLogout() {

    }

    /**
     * Evento que ocorre quando retorna o resultado de outra activity.
     * @param requestCode Código da requisição
     * @param resultCode Código do resultado.
     * @param data Dados
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * Redireciona resultado para redes socias realizarem o tratamento.
         */
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


}
