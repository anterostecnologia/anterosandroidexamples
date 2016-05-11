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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.ClienteConsultaAdapter;
import br.com.anteros.vendas.modelo.Cliente;

/**
 * Created by eduardogreco on 5/10/16.
 */
public class ClienteConsultaActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private ListView lvClientes;
    private ClienteConsultaAdapter adapter;
    private final int REQUISICAO = 1000;
    private SQLRepository<Cliente, Long> clienteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_consulta);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        clienteRepository = AnterosVendasContext.getInstance().getSQLRepository(Cliente.class);

        lvClientes = (ListView) findViewById(R.id.cliente_consulta_list_view);
        lvClientes.setOnItemLongClickListener(this);

        new BuscarClientes().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.cliente_consulta_action);
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

            case R.id.cliente_consulta_action_adicionar:
                Intent intent = new Intent(this, ClienteCadastroActivity.class);
                startActivityForResult(intent, REQUISICAO);
                break;
        }
        return true;
    }

    private class BuscarClientes extends AsyncTask<Void, Void, List<Cliente>> {

        private ProgressDialog progress;

        @Override
        public void onPreExecute() {
            progress = ProgressDialog.show(ClienteConsultaActivity.this,
                    getResources().getString(R.string.app_name), "Aguarde...",
                    true);
        }

        @Override
        protected List<Cliente> doInBackground(Void... params) {
            return clienteRepository.find("SELECT CLI.* FROM CLIENTE CLI");
        }

        @Override
        public void onPostExecute(List<Cliente> clientes) {
            adapter = new ClienteConsultaAdapter(ClienteConsultaActivity.this, clientes);
            lvClientes.setAdapter(adapter);
            progress.dismiss();
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == lvClientes) {
            Cliente cliente = adapter.getItem(position);
            Intent intent = new Intent(this, ClienteCadastroActivity.class);
            intent.putExtra("cliente", (Parcelable) cliente);

            startActivityForResult(intent, REQUISICAO);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUISICAO) {
            if (resultCode == RESULT_OK) {
                new BuscarClientes().execute();
            }
        }
    }
}
