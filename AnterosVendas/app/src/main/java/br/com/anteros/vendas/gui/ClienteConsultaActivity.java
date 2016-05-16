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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.ClienteConsultaAdapter;
import br.com.anteros.vendas.modelo.Cliente;

/**
 * Activity responsável pela consulta de clientes.
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 12/05/16.
 */

public class ClienteConsultaActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    public static Cliente cliente;
    private ListView lvClientes;
    private ClienteConsultaAdapter adapter;
    private final int EDITAR_CLIENTE = 1000;
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

        /**
         * Obtém o repositório de clientes usado para buscar os dados.
         */
        clienteRepository = AnterosVendasContext.getInstance().getSQLRepository(Cliente.class);

        /**
     * Atribui evento long click para lista para permitir selecionar o cliente para edição.
         */
        lvClientes = (ListView) findViewById(R.id.cliente_consulta_list_view);
        lvClientes.setOnItemLongClickListener(this);

        /**
         * Busca os objetos dos clientes.
         */
        new BuscarClientes().execute();

    }

    /**
     * Cria as opções no menu para a consulta de cliente
     * @param menu
     * @return
     */
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

    /**
     * Se selecionou um item do menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /**
                 * Pressionou a tecla voltar chama o método onBackPressed;
                 */
                onBackPressed();
                break;

            case R.id.cliente_consulta_action_adicionar:
                /**
                 * Cria um novo cliente e chama a Activity de cadastro do cliente
                 * passando o código da EDITAR_CLIENTE.
                 */
                cliente = new Cliente();
                Intent intent = new Intent(this, ClienteCadastroActivity.class);
                startActivityForResult(intent, EDITAR_CLIENTE);
                break;
        }
        return true;
    }

    /**
     * AsyncTask para buscar a lista de clientes
     */
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
            /**
             * Retorna a lista de clientes
             */
            return clienteRepository.find("SELECT CLI.* FROM CLIENTE CLI");
        }

        @Override
        public void onPostExecute(List<Cliente> clientes) {
            adapter = new ClienteConsultaAdapter(ClienteConsultaActivity.this, clientes);
            lvClientes.setAdapter(adapter);
            progress.dismiss();
        }
    }


    /**
     * Evento que ocorre quando um long click foi executado. Se o evento
     * ocorreu na lista de clientes abre a activity de edição.
     * @param parent Pai da view
     * @param view View
     * @param position Posição
     * @param id id da view
     * @return False para não propagar o evento pois já foi tratado.
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == lvClientes) {
            cliente = adapter.getItem(position);
            Intent intent = new Intent(this, ClienteCadastroActivity.class);
            startActivityForResult(intent, EDITAR_CLIENTE);
        }
        return false;
    }

    /**
     * Evento que ocorre quando retorna de outras activities.
     * @param requestCode Código da requisição
     * @param resultCode Código do resultado
     * @param data Dados
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * Se o resultCode for editar cliente atualiza a lista de clientes pois foi alterada.
         */
        if (requestCode == EDITAR_CLIENTE) {
            new BuscarClientes().execute();
        }
    }
}
