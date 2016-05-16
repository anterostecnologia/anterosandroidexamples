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

import java.util.Date;
import java.util.List;

import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.PedidoConsultaAdapter;
import br.com.anteros.vendas.modelo.PedidoVenda;

/**
 * Activity responsável pela consulta do pedido.
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 10/05/16.
 */

public class PedidoConsultaActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    public static PedidoVenda pedido;
    private ListView lvPedidos;
    private PedidoConsultaAdapter adapter;
    private final int EDITAR_PEDIDO = 1000;
    private SQLRepository<PedidoVenda, Long> pedidoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedido_consulta);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        pedidoRepository = AnterosVendasContext.getInstance().getSQLRepository(PedidoVenda.class);

        /**
         * Atribui o long click para a view do pedido. Quando o usuário pressionar
         * o long click abre a edição do pedido.
         */
        lvPedidos = (ListView) findViewById(R.id.pedido_consulta_list_view);
        lvPedidos.setOnItemLongClickListener(this);

        /**
         * Busca os dados dos pedidos.
         */
        new BuscarPedidosConsulta().execute();
    }

    /**
     * Cria as opções do menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.pedido_consulta_action);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });

        return true;
    }

    /**
     * Evento se uma opção do menu foi selecionada.
     * @param item Item selecionado
     * @return True se propaga evento.
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /**
                 * Sair da consulta de pedidos
                 */
                onBackPressed();
                break;

            case R.id.pedido_consulta_action_adicionar:
                /**
                 * Adiciona um novo pedido de venda
                 */
                pedido = new PedidoVenda();
                pedido.setNrPedido(Long.valueOf(new Date().getTime()));
                pedido.setDtPedido(new Date());
                /**
                 * Inicia a activity de edição do pedido
                 */
                Intent intent = new Intent(this, PedidoCadastroActivity.class);
                startActivityForResult(intent, EDITAR_PEDIDO);
                break;
        }
        return true;
    }

    /**
     * AsyncTask para buscar os pedidos de venda.
     */
    private class BuscarPedidosConsulta extends AsyncTask<Void, Void, List<PedidoVenda>> {

        private ProgressDialog progress;

        @Override
        public void onPreExecute() {
            progress = ProgressDialog.show(PedidoConsultaActivity.this,
                    getResources().getString(R.string.app_name), "Aguarde...",
                    true);
        }

        @Override
        protected List<PedidoVenda> doInBackground(Void... params) {
            /**
             * Busca os objetos dos pedidos contendo dados parciais
             * apenas para apresentação na lista.
             */
            return pedidoRepository.find("SELECT  P.ID_PEDIDOVENDA,                " +
                    " P.NR_PEDIDO,                     " +
                    " P.DT_PEDIDO,                     " +
                    " P.TP_CONDICAO_PGTO,              " +
                    " P.FORMA_PAGTO,                   " +
                    " P.VL_TOTAL_PEDIDO,               " +
                    " C.ID_CLIENTE,                    " +
                    " C.RAZAO_SOCIAL                   " +
                    " FROM PEDIDOVENDA P, OPCAO_CLIENTE C           " +
                    " WHERE C.ID_CLIENTE = P.ID_CLIENTE        ");
        }

        @Override
        public void onPostExecute(List<PedidoVenda> pedidos) {
            /**
             * Cria um adapter com os dados e atribui na lista
             */
            adapter = new PedidoConsultaAdapter(PedidoConsultaActivity.this, pedidos);
            lvPedidos.setAdapter(adapter);
            progress.dismiss();
        }
    }


    /**
     * Evento pressionou long click na view do pedido
     * @param parent Pai da View
     * @param view View
     * @param position Posição da view
     * @param id id da View
     * @return False para não propagar o evento
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == lvPedidos) {
            /**
             * Pega o objeto pedido parcial usado apenas para apresentação na lista
             */
            PedidoVenda p = adapter.getItem(position);
            /**
             * Busca objeto completo usado para edição
             */
            pedido = pedidoRepository.findOne(
                    "SELECT P.* FROM PEDIDOVENDA P WHERE P.ID_PEDIDOVENDA = :PID_PEDIDOVENDA",
                    new NamedParameter("PID_PEDIDOVENDA", p.getId()));
            /**
             * Inicia activity para edição do pedido
             */
            Intent intent = new Intent(this, PedidoCadastroActivity.class);
            startActivityForResult(intent, EDITAR_PEDIDO);
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
        if (requestCode == EDITAR_PEDIDO) {
            /**
             * Atualiza lista de pedidos pois um pedido foi alterado.
             */
            new BuscarPedidosConsulta().execute();
        }
    }
}
