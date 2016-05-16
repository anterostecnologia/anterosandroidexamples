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

import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.core.utils.DateUtil;
import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.persistence.transaction.impl.TransactionException;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.PedidoVenda;

/**
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 10/05/16.
 */
public class PedidoConsultaAdapter extends ArrayAdapter<PedidoVenda> {

    public PedidoConsultaAdapter(Context context, List<PedidoVenda> objects) {
        super(context, R.layout.pedido_consulta_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.pedido_consulta_item, null);
        }

        final PedidoVenda item = (PedidoVenda) getItem(position);

        TextView tvDescricaoPedido = (TextView) convertView.findViewById(R.id.pedido_consulta_item_descricaoPedido);
        TextView tvDataPedido = (TextView) convertView.findViewById(R.id.pedido_consulta_item_dtPedido);
        TextView tvCliente = (TextView) convertView.findViewById(R.id.pedido_consulta_item_nomeCliente);
        TextView tvCondicao = (TextView) convertView.findViewById(R.id.pedido_consulta_item_tvCondicaoPagamento);
        TextView tvFormaPgto = (TextView) convertView.findViewById(R.id.pedido_consulta_item_formaPagamento);
        TextView tvValorTotal = (TextView) convertView.findViewById(R.id.pedido_consulta_item_valorTotal);
        ImageView imgDelete = (ImageView) convertView.findViewById(R.id.pedido_consulta_item_imgDelete);

        imgDelete.setEnabled(item != null);
        imgDelete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                delete(item);
            }
        });

        if (item != null) {
            tvDescricaoPedido.setText("PEDIDO NR. " + item.getNrPedido());
            tvDataPedido.setText(DateUtil.toStringDateDMA(item.getDtPedido()));
            tvCliente.setText(item.getCliente().getId() + " - " + item.getCliente().getRazaoSocial());
            tvCondicao.setText(item.getCondicaoPagamento().name());
            tvFormaPgto.setText(item.getFormaPagamento().name());
            tvValorTotal.setText(item.getVlTotalPedidoAsString());
        }

        return convertView;
    }

    protected void delete(final PedidoVenda pedido) {
        new QuestionAlert(getContext(), getContext().getString(
                R.string.app_name), "Remover Pedido ?",
                new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        SQLRepository<PedidoVenda, Long> pedidoFactory = AnterosVendasContext.getInstance().getSQLRepository(PedidoVenda.class);

                        try {
                            PedidoVenda p = pedidoFactory.findOne(
                                    "SELECT P.* FROM PEDIDOVENDA P WHERE P.ID_PEDIDOVENDA = :PID_PEDIDOVENDA",
                                    new NamedParameter("PID_PEDIDOVENDA", pedido.getId()));

                            pedidoFactory.getTransaction().begin();
                            pedidoFactory.remove(p);
                            pedidoFactory.getTransaction().commit();

                            remove(pedido);
                            notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                pedidoFactory.getTransaction().rollback();
                            } catch (Exception e1) {
                            }
                            if (e instanceof TransactionException) {
                                new ErrorAlert(getContext(), getContext().getResources().getString(
                                        R.string.app_name), "Ocorreu um erro ao remover Pedido. " + e.getCause()).show();
                            } else {
                                new ErrorAlert(getContext(), getContext().getResources().getString(
                                        R.string.app_name), "Ocorreu um erro ao remover Pedido. " + e.getMessage()).show();
                            }
                        }
                    }

                    public void onNegativeClick() {

                    }
                }).show();

    }
}
