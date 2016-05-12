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
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.MenuItem;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.MenuAdapter;

public class MenuActivity extends AppCompatActivity implements OnItemClickListener {

    public static final int CLIENTE = 0;
    public static final int PEDIDO = 1;
    public static final int CADASTRO_SELECIONAR_CLIENTE = 3;
    private GridView gridMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
         * Inicializa o contexto da aplicação de vendas.
         */
        AnterosVendasContext.setApplication(this.getApplication());

        //AnterosVendasContext.getInstance().populateDatabase();

        gridMenu = (GridView) findViewById(R.id.activity_menu_gridMenu
        );
        gridMenu.setOnItemClickListener(this);

        adicionarItensMenu();
        verificaQtdeColunasPorLinha();
    }

    private void verificaQtdeColunasPorLinha() {
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


    private void adicionarItensMenu() {
        List<MenuItem> itens = new ArrayList<MenuItem>();

        itens.add(new MenuItem(CLIENTE, "Cliente", "Cadastro e consulta de clientes", "", getResources().getDrawable(R.drawable.ic_menu_cliente), Color.TRANSPARENT));
        itens.add(new MenuItem(PEDIDO, "Pedido", "Cadastro e consulta de pedidos", "", getResources().getDrawable(R.drawable.ic_menu_pedido), Color.TRANSPARENT));

        gridMenu.setAdapter(new MenuAdapter(this, R.layout.menu_item, itens));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.menu_action);
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
            case R.id.menu_action_settings:
                startActivity(new Intent(this, ManutencaoTabelasActivity.class));
                break;
            case R.id.menu_action_exit:
                sairDoSistema();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            sairDoSistema();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        switch ((int) id) {
            case CLIENTE:
                startActivity(new Intent(this, ClienteConsultaActivity.class));
                break;
            case PEDIDO:
                startActivity(new Intent(this, PedidoConsultaActivity.class));
                break;
        }
    }

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
