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

package br.com.anteros.vendas.gui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.anteros.persistence.parameter.NamedParameter;
import android.view.View.OnClickListener;
import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.persistence.transaction.impl.TransactionException;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.Cliente;

/**
 * Adapter responsável por mostrar a consulta de clientes.
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 10/05/16.
 */
public class ClienteConsultaAdapter extends ArrayAdapter<Cliente> {

    public ClienteConsultaAdapter(Context context, List<Cliente> objects) {
        super(context, R.layout.cliente_consulta_item, objects);
    }

    /**
     * Retorna a view para apresentação na lista
     * @param position Posição dentro da view
     * @param convertView View
     * @param parent View pai
     * @return View criada
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /**
         * Cria a view com o layout da consulta de cliente.
         */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cliente_consulta_item, null);
        }

        /**
         * Obtém o cliente correspondente a posição na lista
         */
        final Cliente item = (Cliente) getItem(position);

        /**
         * Obtém os campos dentro do layout
         */
        TextView tvRazaoCliente = (TextView) convertView.findViewById(R.id.cliente_consulta_item_nomeCliente);
        TextView tvFantasia = (TextView) convertView.findViewById(R.id.cliente_consulta_item_fantasia);
        TextView tvEndereco = (TextView) convertView.findViewById(R.id.cliente_consulta_item_endereco);
        TextView tvCidade = (TextView) convertView.findViewById(R.id.cliente_consulta_item_cidade);
        ImageView imgDelete = (ImageView) convertView.findViewById(R.id.cliente_consulta_item_imgDelete);

        /**
         * Habilita o removerCliente apenas se o item for diferente de null
         */
        imgDelete.setEnabled(item != null);

        /**
         * Atribui o evento onClick para a imagem delete
         */
        imgDelete.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                removerCliente(item);
            }
        });

        /**
         * Se o item não for nulo atribui os valores nos campos da view
         */
        if (item != null) {
            tvRazaoCliente.setText(item.getId() + " - " + item.getRazaoSocial());
            tvFantasia.setText(item.getNomeFantasia());
            tvEndereco.setText(item.getTpLogradouro() + " " + item.getLogradouro() + ", Nr. " + item.getNrLogradouro() + " - " + item.getBairro());
            tvCidade.setText(item.getCidade() + "/" + item.getEstado().name());
        }

        return convertView;
    }


    /**
     * Remove o cliente
     * @param cliente Cliente
     */
    protected void removerCliente(final Cliente cliente) {
        new QuestionAlert(getContext(), getContext().getString(
                R.string.app_name), "Remover o cliente ?",
                new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        /**
                         * Obtém um repositório para remover o cliente.
                         */
                        SQLRepository<Cliente, Long> clienteRepository = AnterosVendasContext.getInstance().getSQLRepository(Cliente.class);

                        try {
                            /**
                             * Busca o objeto cliente completo para remover já que o objeto que está na lista
                             * do adapter é parcial
                             */
                            Cliente cli = clienteRepository.findOne(
                                    "SELECT P.* FROM CLIENTE P WHERE P.ID_CLIENTE = :PID_CLIENTE",
                                    new NamedParameter("PID_CLIENTE", cliente.getId()));

                            /**
                             * Inicia a transação e remove o cliente
                             */
                            clienteRepository.getTransaction().begin();
                            clienteRepository.remove(cli);
                            /**
                             * Realiza o commit na transação
                             */
                            clienteRepository.getTransaction().commit();

                            /**
                             * Remove o cliente da lista do adapter
                             */
                            remove(cliente);
                            /**
                             * Notifica o adapter que a lista mudou
                             */
                            notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                clienteRepository.getTransaction().rollback();
                            } catch (Exception e1) {
                            }
                            if (e instanceof TransactionException) {
                                new ErrorAlert(getContext(), getContext().getResources().getString(
                                        R.string.app_name), "Ocorreu um erro ao remover Cliente. " + e.getCause()).show();
                            } else {
                                new ErrorAlert(getContext(), getContext().getResources().getString(
                                        R.string.app_name), "Ocorreu um erro ao remover Cliente. " + e.getMessage()).show();
                            }
                        }
                    }

                    public void onNegativeClick() {

                    }
                }).show();

    }
}
