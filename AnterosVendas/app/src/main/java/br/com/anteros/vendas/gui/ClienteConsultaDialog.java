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

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.ClienteConsultaDialogAdapter;
import br.com.anteros.vendas.modelo.Cliente;

/**
 * Dialog para consulta de clientes.
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 12/05/16.
 */
public class ClienteConsultaDialog extends DialogFragment implements AdapterView.OnItemLongClickListener {

    private SQLRepository<Cliente, Long> clienteRepository;
    private ListView listView;
    private ClienteConsultaDialogAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Cria o layout para a consulta de clientes na dialog.
         */
        View v = inflater.inflate(R.layout.cliente_consulta_dialog, container, false);
        listView = (ListView) v.findViewById(R.id.cliente_consulta_dialog_list_view);
        listView.setOnItemLongClickListener(this);


        /**
         * Obtém o repositório para buscar os clientes
         */
        clienteRepository = AnterosVendasContext.getInstance().getSQLRepository(Cliente.class);
        /**
         * Busca os objetos dos clientes trazendo apenas dados parciais e não objetos completos
         * o que otimiza o processo e memória.
         */
        List<Cliente> clientes = clienteRepository.find("SELECT CLI.ID_CLIENTE,      " +
                "CLI.RAZAO_SOCIAL,    " +
                "CLI.FANTASIA,        " +
                "CLI.TP_LOGRADOURO,   " +
                "CLI.LOGRADOURO,      " +
                "CLI.NR_LOGRADOURO,   " +
                "CLI.BAIRRO,          " +
                "CLI.DS_CIDADE,       " +
                "CLI.UF,              " +
                "CLI.TELEFONE,        " +
                "CLI.EMAIL            " +
                "FROM CLIENTE CLI     ");



        /**
         * Cria e atribui um adapter para a consulta de clientes
         */
        adapter = new ClienteConsultaDialogAdapter(getContext(), clientes);
        listView.setAdapter(adapter);

        /**
         * Adiciona listener para permitir fechar a dialog.
         */
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.cliente_consulta_dialog_action_fechar:
                        dismiss();
                        break;
                }
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.cliente_consulta_dialog_action);
        return v;
    }

    /**
     * Cria uma janela para a consulta dos clientes
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        /**
         * Remove o título da dialog.
         */
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    /**
     * Se pressionou um long click na view do cliente seleciona e retorna.
     * @param parent Pai da view
     * @param view View
     * @param position Posição onde ocorreu o evento
     * @param id id da view.
     * @return False para não propagar o evento.
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        /**
         * Obtém o cliente pressionado
         */
        Cliente cliente = adapter.getItem(position);
        /**
         * Atribui o cliente no objeto estático do pedido.
         */
        PedidoConsultaActivity.pedido.setCliente(cliente);
        /**
         * Atualiza os fragmento com os dados do cliente
         */
        PedidoCadastroDadosFragment.atualizarCliente();
        /**
         * Fecha dialog e retorna false para não propagar o evento.
         */
        dismiss();
        return false;
    }
}
