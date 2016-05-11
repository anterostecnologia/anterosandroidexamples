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

package br.com.anteros.vendas;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.vendas.gui.ClienteConsultaActivity;
import dalvik.system.DexClassLoader;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener, OnItemClickListener {

    private GridView gridMenu;
    private ImageView imgLogout;

    public static final int CLIENTE = 0;
    public static final int PEDIDO = 1;
    public static final int CADASTRO_SELECIONAR_CLIENTE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        /*
         * Inicializa o contexto da aplicação de vendas.
         */
        AnterosVendasContext.setApplication(this.getApplication());
        final AnterosVendasContext vendasContext = AnterosVendasContext.getInstance();

       // vendasContext.populateDatabase();

        gridMenu = (GridView) findViewById(R.id.lvMenu);
        gridMenu.setOnItemClickListener(this);
        imgLogout = (ImageView) findViewById(R.id.img_logout);
        imgLogout.setOnClickListener(this);

        adicionarItensMenu();
    }


    private void adicionarItensMenu() {
        List<MenuItem> itens = new ArrayList<MenuItem>();

        itens.add(new MenuItem(CLIENTE, "Cliente", "Cadastro e consulta de clientes", "", getResources().getDrawable(R.drawable.ic_menu_cliente), Color.TRANSPARENT));
        itens.add(new MenuItem(PEDIDO, "Pedido", "Cadastro e consulta de pedidos", "", getResources().getDrawable(R.drawable.ic_menu_pedido), Color.TRANSPARENT));

        gridMenu.setAdapter(new MenuAdapter(this, R.layout.menu_item, itens));
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        switch ((int) id) {
            case CLIENTE:
                startActivity(new Intent(this, ClienteConsultaActivity.class));
                break;
            case PEDIDO:
//
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == imgLogout) {
            sairDoSistema();
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
