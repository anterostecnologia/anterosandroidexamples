package br.com.anteros.vendas.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.sql.SQLException;

import br.com.anteros.android.persistence.sql.jdbc.SQLiteConnection;
import br.com.anteros.android.persistence.sql.jdbc.SQLiteResultSet;
import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.InfoAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.BackupService;
import br.com.anteros.vendas.ExportDatabaseTask;
import br.com.anteros.vendas.ImportDatabaseTask;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.RecriarBancoDeDados;

public class ManutencaoTabelasActivity extends AppCompatActivity {

    public static final int RECRIOU_TABELAS = 1;

    private ListView lvTabelas;
    private CursorAdapter adapter;
    private SQLiteConnection connection;
    private Cursor cursor = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manutencao_tabelas);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        lvTabelas = (ListView) findViewById(R.id.manutencao_tabelas_lvTabelas);

        try {
            connection = (SQLiteConnection) AnterosVendasContext.getInstance().getSession().getConnection();
        } catch (Exception e) {
        }
        cursor = connection
                .getDatabase()
                .rawQuery(
                        "SELECT name as _id FROM sqlite_master WHERE type='table' ORDER BY name;",
                        null);

        adapter = new CursorAdapter(this, cursor) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.manutencao_tabelas_item,
                        parent, false);
                bindView(view, context, cursor);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView lbTabela = (TextView) view
                        .findViewById(R.id.manutencao_tabela_item_nome_tabela);
                try {
                    lbTabela.setText(getObjectValue(cursor, 0) + "");
                } catch (SQLException e) {
                    e.printStackTrace();
                    lbTabela.setText("");
                }
            }

        };

        lvTabelas.setAdapter(adapter);
        lvTabelas.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> adapterView,
                                           View view, int position, long id) {
                String tabela = "";

                try {
                    tabela = getObjectValue((Cursor) adapter.getItem(position), 0) + "";
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                RegistrosTabelasActivity.setTabela(tabela);

                startActivity(new Intent(ManutencaoTabelasActivity.this,
                        RegistrosTabelasActivity.class));

                return false;
            }
        });
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
                dropAndCreateTables();
                break;

        }
        return true;
    }

    private void dropAndCreateTables() {
        new QuestionAlert(
                this,
                "Atenção!",
                "Ao recriar o banco de dados todas as informações serão perdidas permanentemente, "
                        + "este processo não poderá ser revertido. Deseja continuar?\n",
                new QuestionAlert.QuestionListener() {
                    @Override
                    public void onPositiveClick() {
                        try {
                            new RecriarBancoDeDados(
                                    ManutencaoTabelasActivity.this) {

                                @Override
                                public void onSuccess() {
                                    new InfoAlert(ManutencaoTabelasActivity.this, "Mensagem",
                                            "O banco de dados foi apagado e recriado com sucesso, "
                                                    + "a aplicação será encerrada.", new InfoAlert.InfoListener() {
                                        @Override
                                        public void onOkClick() {
                                            ManutencaoTabelasActivity.this
                                                    .setResult(ManutencaoTabelasActivity.RECRIOU_TABELAS);
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
                "Importar Banco de Dados ?", new QuestionAlert.QuestionListener() {

            public void onPositiveClick() {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    chooseFileToRestore();
                } else {
                    Toast.makeText(
                            ManutencaoTabelasActivity.this,
                            "Cartão externo não foi encontrado. Não será possível importar os dados.",
                            Toast.LENGTH_LONG).show();
                }
            }

            public void onNegativeClick() {
            }
        }).show();
    }

    private void exportarBancoDeDados() {
        new QuestionAlert(this, "Manutenção Tabelas",
                "Exportar Banco de Dados ?", new QuestionAlert.QuestionListener() {

            public void onPositiveClick() {
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

            public void onNegativeClick() {
            }
        }).show();
    }

    private void chooseFileToRestore() {

        File importFolder = new File(Environment.getExternalStorageDirectory()
                + "/backup/");

        final File importFiles[] = importFolder.listFiles();

        if (importFiles == null || importFiles.length == 0) {
            Toast.makeText(this, "Pasta de bakcup vazia", Toast.LENGTH_SHORT)
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
                                importDatabaseFile, AnterosVendasContext.getInstance().getAbsolutPathDb()).execute();
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        try {
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onDestroy();
    }

    private Object getObjectValue(Cursor cursor, int columnIndex) throws SQLException {
        switch (SQLiteResultSet.getDataType((SQLiteCursor) cursor, columnIndex)) {
            case SQLiteResultSet.FIELD_TYPE_INTEGER:
                long val = cursor.getLong(columnIndex);
                if (val > Integer.MAX_VALUE || val < Integer.MIN_VALUE) {
                    return new Long(val);
                } else {
                    return new Integer((int) val);
                }
            case SQLiteResultSet.FIELD_TYPE_FLOAT:
                return new Double(cursor.getDouble(columnIndex));
            case SQLiteResultSet.FIELD_TYPE_BLOB:
                return cursor.getBlob(columnIndex);
            case SQLiteResultSet.FIELD_TYPE_NULL:
                return null;
            case SQLiteResultSet.FIELD_TYPE_STRING:
            default:
                return cursor.getString(columnIndex);
        }
    }
}
