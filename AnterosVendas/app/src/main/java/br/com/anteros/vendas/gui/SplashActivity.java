package br.com.anteros.vendas.gui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;

/**
 * @author Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Data: 11/05/16.
 */
public class SplashActivity extends AppCompatActivity {
    private TextView tvCarregando;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        /**
         * Atribui o contexto da aplicação
         */
        AnterosVendasContext.setApplication(getApplication());
        AnterosVendasContext.getInstance();

        new AsyncTask<Void, String, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Thread.sleep(2000);
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();

                    return Boolean.TRUE;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Boolean.FALSE;
            }
        }.execute();
    }
}
