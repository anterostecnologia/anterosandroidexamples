package br.com.anteros.vendas.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.AnexoConsultaAdapter;
import br.com.anteros.vendas.gui.adapter.ClienteConsultaAdapter;
import br.com.anteros.vendas.modelo.Anexo;
import br.com.anteros.vendas.modelo.Cliente;

/**
 * Created by eduardogreco on 5/10/16.
 */
public class AnexoConsultaActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private ListView lvAnexos;
    private AnexoConsultaAdapter adapter;
    private final int REQUISICAO = 1000;
    private SQLRepository<Anexo, Long> anexoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anexo_consulta);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        anexoRepository = AnterosVendasContext.getInstance().getSQLRepository(Anexo.class);

        lvAnexos = (ListView) findViewById(R.id.anexo_consulta_list_view);
        lvAnexos.setOnItemLongClickListener(this);

        new BuscarAnexos().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.anexo_consulta_action);
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

            case R.id.anexo_consulta_action_adicionar:
                startActivity(new Intent(this, AnexoCadastroActivity.class));
                break;
        }
        return true;
    }

    private class BuscarAnexos extends AsyncTask<Void, Void, List<Anexo>> {

        private ProgressDialog progress;

        @Override
        public void onPreExecute() {
            progress = ProgressDialog.show(AnexoConsultaActivity.this,
                    getResources().getString(R.string.app_name), "Aguarde...",
                    true);
        }

        @Override
        protected List<Anexo> doInBackground(Void... params) {
            return anexoRepository.find(
                    "SELECT A.* FROM ANEXO A WHERE A.ID_CLIENTE = :PID_CLIENTE",
                    new NamedParameter("PID_CLIENTE", ClienteConsultaActivity.cliente.getId()));
        }

        @Override
        public void onPostExecute(List<Anexo> anexos) {
            adapter = new AnexoConsultaAdapter(AnexoConsultaActivity.this, anexos);
            lvAnexos.setAdapter(adapter);
            progress.dismiss();
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == lvAnexos) {
//            Anexo anexo = adapter.getItem(position);
//            Intent intent = new Intent(this, AnexoActivity.class);
//            intent.putExtra("anexo", (Parcelable) anexo);
//
//            startActivityForResult(intent, REQUISICAO);

            //AQUI
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUISICAO) {
            if (resultCode == RESULT_OK) {
                new BuscarAnexos().execute();
            }
        }
    }
}
