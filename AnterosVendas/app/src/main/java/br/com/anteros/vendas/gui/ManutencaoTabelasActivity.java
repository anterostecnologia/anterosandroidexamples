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

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import java.io.File;

import br.com.anteros.android.persistence.backup.BackupException;
import br.com.anteros.android.persistence.backup.BackupService;
import br.com.anteros.android.persistence.backup.DatabaseMaintenanceFragment;
import br.com.anteros.android.persistence.backup.ExportDatabaseTask;
import br.com.anteros.android.persistence.backup.ImportDatabaseTask;
import br.com.anteros.android.persistence.backup.RecreateDatabaseTask;
import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.InfoAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;

public class ManutencaoTabelasActivity extends AppCompatActivity {

    private static int REQUEST_PERMISSION_IMPORT = 777;
    private static int REQUEST_PERMISSION_EXPORT = 778;
    private static int REQUEST_PERMISSION_SHARE = 779;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manutencao_tabelas);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);


        DatabaseMaintenanceFragment fragment = new ManutencaoTabelasFragment();
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.manutencao_tabelas_fragment, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.manutencao_action);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.manutencao_action_exportar:
                exportarBancoDeDados();
                break;
            case R.id.manutencao_action_importar:
                importarBancoDeDados();
                break;
            case R.id.manutencao_action_recriar:
                recriarBancoDados();
                break;
            case R.id.manutencao_action_share:
                compartilharBancoDados();
                break;

        }
        return true;
    }

    private void compartilharBancoDados() {
        new QuestionAlert(this, "Manutenção Tabelas",
                "Deseja compartilhar o banco de dados ?", new QuestionAlert.QuestionListener() {

            public void onPositiveClick() {
                if (ActivityCompat.checkSelfPermission(ManutencaoTabelasActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ManutencaoTabelasActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ManutencaoTabelasActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_SHARE);
                } else {
                    selecionarArquivoCompartilhar();
                }
            }

            public void onNegativeClick() {
            }
        }).show();
    }

    private void selecionarArquivoCompartilhar() {

        File importFolder = new File(Environment.getExternalStorageDirectory()
                + "/backup/");

        final File importFiles[] = importFolder.listFiles();

        if (importFiles == null || importFiles.length == 0) {
            Toast.makeText(this, "Pasta de backup vazia", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecionar arquivo:");
        builder.setSingleChoiceItems(importFolder.list(), 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        File importDatabaseFile = importFiles[whichButton];
                        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.setType("*/*");
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "File Name");
                        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(importDatabaseFile));
                        startActivity(Intent.createChooser(emailIntent, "Share File"));
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void recriarBancoDados() {
        new QuestionAlert(
                this,
                "Atenção!",
                "Ao recriar o banco de dados todas as informações serão perdidas permanentemente, "
                        + "este processo não poderá ser revertido. Deseja continuar?\n",
                new QuestionAlert.QuestionListener() {
                    @Override
                    public void onPositiveClick() {
                        try {
                            new RecreateDatabaseTask(
                                    ManutencaoTabelasActivity.this, AnterosVendasContext.getInstance().getSession()) {

                                @Override
                                public void onSuccess() {
                                    new InfoAlert(ManutencaoTabelasActivity.this, "Mensagem",
                                            "O banco de dados foi apagado e recriado com sucesso, "
                                                    + "a aplicação será encerrada.", new InfoAlert.InfoListener() {
                                        @Override
                                        public void onOkClick() {
                                            ManutencaoTabelasActivity.this
                                                    .setResult(ImportDatabaseTask.TABLES_RECREATED);
                                            ManutencaoTabelasActivity.this.finish();
                                        }
                                    }).show();
                                }

                                @Override
                                public void onError(String message) {
                                    new ErrorAlert(ManutencaoTabelasActivity.this,
                                            AnterosVendasContext.getInstance().getApplicationName(), message).show();
                                }

                            }.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                            new ErrorAlert(ManutencaoTabelasActivity.this,
                                    "Ocorreu um erro.",
                                    "Erro ao recriar o banco de dados: "
                                            + e.getMessage());
                        }
                    }

                    @Override
                    public void onNegativeClick() {
                    }
                }).show();
    }

    private void importarBancoDeDados() {
        new QuestionAlert(this, "Manutenção Tabelas",
                "Importar banco de dados ?", new QuestionAlert.QuestionListener() {

            public void onPositiveClick() {


                if (ActivityCompat.checkSelfPermission(ManutencaoTabelasActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ManutencaoTabelasActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ManutencaoTabelasActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_IMPORT);
                } else {
                    executarImportacaoBancoDados();
                }
            }

            public void onNegativeClick() {
            }
        }).show();
    }

    private void executarImportacaoBancoDados() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            selecionarArquivoImportarBancoDados();
        } else {
            Toast.makeText(
                    ManutencaoTabelasActivity.this,
                    "Cartão externo não foi encontrado. Não será possível importar os dados.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void exportarBancoDeDados() {
        new QuestionAlert(this, "Manutenção Tabelas",
                "Exportar banco de dados ?", new QuestionAlert.QuestionListener() {

            public void onPositiveClick() {

                if (ActivityCompat.checkSelfPermission(ManutencaoTabelasActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ManutencaoTabelasActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ManutencaoTabelasActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_EXPORT);
                } else {
                    executarExportacaoBancoDados();
                }
            }

            public void onNegativeClick() {
            }
        }).show();
    }

    private void executarExportacaoBancoDados() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            new ExportDatabaseTask(ManutencaoTabelasActivity.this, AnterosVendasContext.getInstance().getAbsolutPathDb(),
                    AnterosVendasContext.getInstance().getDatabaseName(), getSharedPreferences(
                    BackupService.PREFERENCES_NAME, MODE_PRIVATE)).execute();
        } else {
            Toast.makeText(
                    ManutencaoTabelasActivity.this,
                    "Cartão externo não foi encontrado. Não será possível exportar os dados.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void selecionarArquivoImportarBancoDados() {

        File importFolder = new File(Environment.getExternalStorageDirectory()
                + "/backup/");

        final File importFiles[] = importFolder.listFiles();

        if (importFiles == null || importFiles.length == 0) {
            Toast.makeText(this, "Pasta de backup vazia", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecionar arquivo:");
        builder.setSingleChoiceItems(importFolder.list(), 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        File importDatabaseFile = importFiles[whichButton];
                        new ImportDatabaseTask(ManutencaoTabelasActivity.this,
                                importDatabaseFile, AnterosVendasContext.getInstance().getAbsolutPathDb(), AnterosVendasContext.getInstance().getSession()).execute();
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_PERMISSION_EXPORT) {
            if (ActivityCompat.checkSelfPermission(ManutencaoTabelasActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ManutencaoTabelasActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                throw new BackupException("Não foi possível executar a exportação do banco de dados pois você não possuí permissão.");
            } else {
                executarExportacaoBancoDados();
            }
        } else if (requestCode==REQUEST_PERMISSION_IMPORT) {
            if (ActivityCompat.checkSelfPermission(ManutencaoTabelasActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ManutencaoTabelasActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                throw new BackupException("Não foi possível executar a importação do banco de dados pois você não possuí permissão.");
            } else {
                executarImportacaoBancoDados();
            }
    } else if (requestCode==REQUEST_PERMISSION_SHARE) {
        if (ActivityCompat.checkSelfPermission(ManutencaoTabelasActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ManutencaoTabelasActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            throw new BackupException("Não foi possível compartilhar o banco de dados pois você não possuí permissão.");
        } else {
            selecionarArquivoCompartilhar();
        }
    }
    }
}
