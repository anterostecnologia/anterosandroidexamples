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
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import java.math.BigDecimal;
import java.util.Set;

import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.InfoAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.core.utils.DateUtil;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.validation.api.ConstraintViolation;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.PedidoCadastroPageViewAdapter;
import br.com.anteros.vendas.modelo.CondicaoPagamento;
import br.com.anteros.vendas.modelo.FormaPagamento;
import br.com.anteros.vendas.modelo.ItemPedido;
import br.com.anteros.vendas.modelo.PedidoVenda;
import br.com.anteros.vendas.modelo.ValidacaoPadrao;

/**
 * Activity responsável pelo cadastro do pedido.
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 12/05/16.
 */
public class PedidoCadastroActivity extends AppCompatActivity {

    /**
     * Página para controle das abas do pedido.
     */
    private ViewPager viewPager;

    /**
     * Abas do pedido
     */
    private PedidoCadastroDadosFragment pedidoCadastroDadosFragment;
    private PedidoCadastroItensFragment pedidoCadastroItensFragment;
    /**
     * Aba ativa
     */
    private int tabAtiva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        /**
         * Busca a view das abas(páginas)
         */
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            configuraViewPager(viewPager);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_pedido_tabs);
        tabLayout.setupWithViewPager(viewPager);

        /**
         * Atribui o evento para tratar quando as abas mudarem
         */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabAtiva = position;
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * Habilita o menu de acordo com a aba ativa.
     * @param menu menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (tabAtiva == 1) {
            menu.findItem(R.id.menu_pedido_adicionarProduto).setVisible(true);
        } else {
            menu.findItem(R.id.menu_pedido_adicionarProduto).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Cria as opções do menu.
     * @param menu Menu
     * @return True se criado.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pedido, menu);
        return true;
    }

    /**
     * Evento tecla pressionada
     * @param keyCode Código da tecla
     * @param event Evento
     * @return True para propagar evento.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            cancelarPedido();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Evento opção do menu selecionada.
     * @param item Item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelarPedido();
                break;

            case R.id.menu_pedido_salvar:
                /**
                 * Salvar pedido
                 */
                new QuestionAlert(this, this.getResources().getString(R.string.app_name), "Deseja salvar o pedido?",
                        new QuestionAlert.QuestionListener() {

                            @Override
                            public void onPositiveClick() {
                                new SalvarPedido().execute();
                            }

                            @Override
                            public void onNegativeClick() {

                            }

                        }).show();
                break;

            case R.id.menu_pedido_adicionarProduto:
                /**
                 * Adiciona um novo produto no pedido. Abre dialog de consulta
                 * de produtos e permite adicionar os produtos.
                 */
                ProdutoConsultaDialog produtoConsultaDialog = new ProdutoConsultaDialog();
                produtoConsultaDialog.setOnDismissListener(new ProdutoConsultaDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        /**
                         * Notifica o adapter que ocorreu modificação na lista.
                         */
                        PedidoCadastroItensFragment.adapter.notifyDataSetChanged();
                        /**
                         * Recalcula total do pedido.
                         */
                        calcularTotalPedido();
                    }
                });
                produtoConsultaDialog.show(getSupportFragmentManager(), "produtoConsultaDialog");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Calcula o total do pedido
     */
    public static void calcularTotalPedido() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : PedidoConsultaActivity.pedido.getItens()) {
            total = total.add(item.getVlTotal());
        }
        PedidoConsultaActivity.pedido.setVlTotalPedido(total);
        PedidoCadastroDadosFragment.atualizarValorTotal();
    }

    /**
     * Configura as páginas(abas) do pedido
     * @param viewPager ViewPager
     */
    private void configuraViewPager(final ViewPager viewPager) {
        /**
         * Cria um adapter
         */
        PedidoCadastroPageViewAdapter adapter = new PedidoCadastroPageViewAdapter(getSupportFragmentManager());

        /**
         * Adiciona os fragmentos das abas na página
         */
        pedidoCadastroDadosFragment = new PedidoCadastroDadosFragment();
        adapter.addFragment(pedidoCadastroDadosFragment, "Dados");

        pedidoCadastroItensFragment = new PedidoCadastroItensFragment();
        adapter.addFragment(pedidoCadastroItensFragment, "Itens");

        /**
         * Atribui o adapter
         */
        viewPager.setAdapter(adapter);
    }

    /**
     * AsyncTask para salvar o pedido
     */
    public class SalvarPedido extends AsyncTask<Integer, Void, String> {

        /**
         * Repositório do pedido
         */
        SQLRepository<PedidoVenda, Long> pedidoRepository = AnterosVendasContext.getInstance().getSQLRepository(PedidoVenda.class);

        private ProgressDialog dialog;

        Set<ConstraintViolation<PedidoVenda>> violacoes;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(PedidoCadastroActivity.this);
            dialog.setTitle(getResources().getString(R.string.app_name));
            dialog.setMessage("Salvando pedido...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                salvarDadosPedido();

                /**
                 * Valida o objeto do pedido
                 */
                violacoes = AnterosVendasContext.getInstance().getValidadorPadrao().validate(PedidoConsultaActivity.pedido, ValidacaoPadrao.class);
                if (violacoes.size() > 0) {
                    return "ERRO_VALIDACAO";
                }
                /**
                 * Inicia a transação e salvo pedido e itens
                 */
                pedidoRepository.getTransaction().begin();
                pedidoRepository.save(PedidoConsultaActivity.pedido);
                pedidoRepository.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    pedidoRepository.getTransaction().rollback();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                return e.getMessage() + "";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing())
                dialog.dismiss();

            if (result == null) {

                new InfoAlert(PedidoCadastroActivity.this, getString(R.string.app_name),
                        "Pedido salvo com sucesso!", new InfoAlert.InfoListener() {
                    @Override
                    public void onOkClick() {
                        setResult(RESULT_OK);
                        finish();
                    }
                }).show();

            } else if (result.equals("ERRO_VALIDACAO")) {
                new MensagemErrorDialog<PedidoVenda>(PedidoCadastroActivity.this,
                        "Atenção!", violacoes).show();
            } else {
                new ErrorAlert(PedidoCadastroActivity.this, getString(R.string.app_name),
                        "Salvando pedido: " + result).show();
            }
        }

    }

    /**
     * Salva os dados do pedido no objeto
     */
    private void salvarDadosPedido() {
        PedidoConsultaActivity.pedido.setCondicaoPagamento((CondicaoPagamento) PedidoCadastroDadosFragment.spCondicaoPagamento.getSelectedItem());
        PedidoConsultaActivity.pedido.setFormaPagamento((FormaPagamento) PedidoCadastroDadosFragment.spFormaPagamento.getSelectedItem());
        PedidoConsultaActivity.pedido.setDtPedido(DateUtil.stringToDate(PedidoCadastroDadosFragment.edData.getText().toString(), DateUtil.DATE));
    }

    /**
     * Cancelar a edição do pedido
     */
    private void cancelarPedido() {
        new QuestionAlert(this, this.getResources().getString(
                R.string.app_name), "Deseja cancelar o pedido?",
                new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        setResult(RESULT_CANCELED);
                        finish();
                    }

                    public void onNegativeClick() {

                    }

                }).show();
    }
}
