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
import android.widget.ListView;

import java.util.List;

import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.ProdutoConsultaAdapter;
import br.com.anteros.vendas.modelo.Produto;

/**
 * Activity responsável pela consulta de produtos.
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 11/05/16.
 */
public class ProdutoConsultaActivity extends AppCompatActivity {

    private ListView lvProdutos;
    private ProdutoConsultaAdapter adapter;
    private final int EDITAR_PRODUTO = 1000;
    /**
     * Repositório do produto
     */
    private SQLRepository<Produto, Long> produtoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_consulta);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        /**
         * Obtém o repositório do produto
         */
        produtoRepository = AnterosVendasContext.getInstance().getSQLRepository(Produto.class);
        lvProdutos = (ListView) findViewById(R.id.lv_produto);

        /**
         * Busca a lista de produtos.
         */
        new BuscarProdutos().execute();
    }

    /**
     * Evento opção do menu selecionada.
     * @param item Item do menu
     * @return True para propagar evento.
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /**
                 * Sair da consulta de produtos.
                 */
                onBackPressed();
                break;
        }
        return true;
    }

    /**
     * AsyncTask para buscar produtos.
     */
    private class BuscarProdutos extends AsyncTask<Void, Void, List<Produto>> {

        private ProgressDialog progress;

        @Override
        public void onPreExecute() {
            progress = ProgressDialog.show(ProdutoConsultaActivity.this,
                    getResources().getString(R.string.app_name), "Aguarde...",
                    true);
        }

        @Override
        protected List<Produto> doInBackground(Void... params) {
            /**
             * Retorna a lista de todos os produtos.
             */
            return produtoRepository.find("SELECT PRO.* FROM OPCAO_PRODUTO PRO");
        }

        @Override
        public void onPostExecute(List<Produto> produtos) {
            adapter = new ProdutoConsultaAdapter(ProdutoConsultaActivity.this, produtos);
            lvProdutos.setAdapter(adapter);
            progress.dismiss();
        }
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
        if (requestCode == EDITAR_PRODUTO) {
            if (resultCode == RESULT_OK) {
                new BuscarProdutos().execute();
            }
        }
    }

}
