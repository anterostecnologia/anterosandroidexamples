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

package br.com.anteros.vendas;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import br.com.anteros.android.ui.controls.QuestionAlert;
import dalvik.system.DexClassLoader;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        /*
         * Inicializa o contexto da aplicação de vendas.
         */
        AnterosVendasContext.setApplication(this.getApplication());
        final AnterosVendasContext vendasContext = AnterosVendasContext.getInstance();

        vendasContext.populateDatabase();


//        new QuestionAlert(this, "Manutenção Tabelas",
//                "Exportar Banco de Dados ?", new QuestionAlert.QuestionListener() {
//
//            public void onPositiveClick() {
//                if (Environment.getExternalStorageState().equals(
//                        Environment.MEDIA_MOUNTED)) {
//                    new ExportDatabaseTask(MenuActivity.this, vendasContext.getAbsolutPathDb(),
//                            vendasContext.getDatabaseName(), getSharedPreferences(
//                            BackupService.PREFERENCES_NAME, MODE_PRIVATE)).execute();
//                } else {
//                    Toast.makeText(
//                            MenuActivity.this,
//                            "Cartão externo não foi encontrado. Não será possível exportar os dados.",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//
//            public void onNegativeClick() {
//            }
//        }).show();



    }


}
