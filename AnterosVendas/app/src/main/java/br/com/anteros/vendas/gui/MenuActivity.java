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

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import br.com.anteros.android.core.util.GUIUtils;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.social.core.SocialProfile;
import br.com.anteros.social.core.image.CircularImageView;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.MenuItem;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.MenuAdapter;

/**
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 10/05/16.
 */
public class MenuActivity extends AppCompatActivity implements OnItemClickListener {

    public static final int OPCAO_CLIENTE = 0;
    public static final int OPCAO_PEDIDO = 1;
    public static final int OPCAO_PRODUTO = 2;
    public static final String MENU_CLIENTE = "Cliente";
    public static final String MENU_PEDIDO = "Pedido";
    public static final String MENU_PRODUTO = "Produto";
    public static final String DESCRICAO_MENU_CLIENTES = "Cadastro e consulta de clientes";
    public static final String DESCRICAO_MENU_PEDIDOS = "Cadastro e consulta de pedidos";
    public static final String DESCRICAO_MENU_PRODUTOS = "Consulta de produtos";

    /**
     * Perfil do usuário
     */
    public static SocialProfile perfilUsuario;
    /**
     * Grid do menu
     */
    private GridView gridMenu;
    /**
     * Image view em forma circular para mostra a foto do perfil
     * do usuário na rede social
     */
    private CircularImageView imgUserSocial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setIcon(R.drawable.ic_anteros_logo_toolbar);

        gridMenu = (GridView) findViewById(R.id.activity_menu_gridMenu
        );
        gridMenu.setOnItemClickListener(this);

        adicionarItensMenu();
        calculaEConfiguraLayoutMenu();

        imgUserSocial = (CircularImageView) findViewById(R.id.activity_menu_gridMenu_imgusuario);

        /**
         * Atribui a imagem do perfil
         */
        if (perfilUsuario!=null){
            imgUserSocial.setVisibility(View.VISIBLE);
            imgUserSocial.setImageBitmap(perfilUsuario.getImageBitmap());
        } else {
            imgUserSocial.setVisibility(View.INVISIBLE);
        }

        /**
         * Adiciona produtos caso não exista
         */
        AnterosVendasContext.getInstance().adicionaProdutos();
    }

    /**
     * Calcula e configura o layout do menu. Verifica se é tablet ou outro tamanho
     * de dispositivo.
     */
    private void calculaEConfiguraLayoutMenu() {
        if (gridMenu != null) {
            if (GUIUtils.isTablet(MenuActivity.this)) {
                gridMenu.setVerticalSpacing(5);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    gridMenu.setNumColumns(3);
                else
                    gridMenu.setNumColumns(2);
            } else {
                gridMenu.setVerticalSpacing(3);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    gridMenu.setNumColumns(2);
                else
                    gridMenu.setNumColumns(1);
            }
        }
    }


    /**
     * Adiciona as opções no menu
     */
    private void adicionarItensMenu() {
        List<MenuItem> itens = new ArrayList<MenuItem>();

        itens.add(new MenuItem(OPCAO_CLIENTE, MENU_CLIENTE, DESCRICAO_MENU_CLIENTES, "", getResources().getDrawable(R.drawable.ic_menu_cliente), Color.TRANSPARENT));
        itens.add(new MenuItem(OPCAO_PEDIDO, MENU_PEDIDO, DESCRICAO_MENU_PEDIDOS, "", getResources().getDrawable(R.drawable.ic_menu_pedido), Color.TRANSPARENT));
        itens.add(new MenuItem(OPCAO_PRODUTO, MENU_PRODUTO, DESCRICAO_MENU_PRODUTOS, "", getResources().getDrawable(R.drawable.ic_menu_produto), Color.TRANSPARENT));

        gridMenu.setAdapter(new MenuAdapter(this, R.layout.menu_item, itens));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.top_toolbar);
        tb.inflateMenu(R.menu.menu_action);
        /**
         * Configura evento MenuItem click.
         */
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });

        return true;
    }

    /**
     * Evento menu item selecionado.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_settings:
                /**
                 * Abre activity de manutenção das tabelas.
                 */
                startActivity(new Intent(this, ManutencaoTabelasActivity.class));
                break;
            case R.id.menu_action_exit:
                /**
                 * Sai do sistema
                 */
                sairDoSistema();
                break;
        }
        return true;
    }

    /**
     * Evento tecla pressionada
     * @param keyCode Código da tecla
     * @param event Evento
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /**
         * Pressionou tecla voltar, sai do sistema.
         */
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            sairDoSistema();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Evento onClick no Menu.
     * @param adapterView Adapter
     * @param view View
     * @param position Posição
     * @param id id da View
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        switch ((int) id) {
            case OPCAO_CLIENTE:
                startActivity(new Intent(this, ClienteConsultaActivity.class));
                break;
            case OPCAO_PEDIDO:
                startActivity(new Intent(this, PedidoConsultaActivity.class));
                break;
            case OPCAO_PRODUTO:
                startActivity(new Intent(this, ProdutoConsultaActivity.class));
                break;
        }
    }

    /**
     * Fecha o sistema
     */
    private void sairDoSistema() {
        new QuestionAlert(this, "Atenção!", "Deseja sair do sistema?",
                new QuestionAlert.QuestionListener() {
                    @Override
                    public void onPositiveClick() {
                        finish();
                    }

                    @Override
                    public void onNegativeClick() {
                    }
                }).show();
    }
}
