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
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.android.ui.controls.adapter.AnterosArrayAdapterWithViewHolder;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.persistence.transaction.impl.TransactionException;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.PedidoCadastroActivity;
import br.com.anteros.vendas.gui.PedidoCadastroDadosFragment;
import br.com.anteros.vendas.gui.PedidoConsultaActivity;
import br.com.anteros.vendas.modelo.ItemPedido;

/**
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 13/05/16.
 */
public class PedidoCadastroItensFragmentAdapter extends AnterosArrayAdapterWithViewHolder<ItemPedido> {

    private List<ItemPedido> itens;

    public PedidoCadastroItensFragmentAdapter(Context context, List<ItemPedido> objects) {
        super(context, R.layout.pedido_cadastro_itens_lista, objects);
        this.itens = objects;
    }

    @Override
    public void loadValuesFromCurrentBean(AnterosViewHolder viewHolder, ItemPedido currentBean) {
        TextView produto = (TextView) viewHolder.getViewById(R.id.pedido_cadastro_itens_produto);
        TextView vlTotal = (TextView) viewHolder.getViewById(R.id.pedido_cadastro_itens_valorTotal);
        TextView preco = (TextView) viewHolder.getViewById(R.id.pedido_cadastro_itens_edPreco);
        EditText quantidade = (EditText) viewHolder.getViewById(R.id.pedido_cadastro_itens_edQuantidade);
        ImageView fotoProduto = (ImageView) viewHolder.getViewById(R.id.pedido_cadastro_itens_item_fotoProduto);
        ItemPedido item = viewHolder.getBean();

        produto.setText(item.getProduto().getId() + " - " + item.getProduto().getNomeProduto());
        vlTotal.setText(item.getVlTotalAsString());
        preco.setText(item.getVlProdutoAsString());
        quantidade.setText(item.getQtProduto().toPlainString());
        if (item.getProduto().getFotoProduto() != null) {
            fotoProduto.setImageBitmap(BitmapFactory.decodeByteArray(item.getProduto().getFotoProduto(), 0, item.getProduto().getFotoProduto().length));
        }
    }

    @Override
    public Set<View> getViewsToHolderController(View row) {
        Set<View> result = new HashSet<>();
        result.add(row.findViewById(R.id.pedido_cadastro_itens_produto));
        result.add(row.findViewById(R.id.pedido_cadastro_itens_valorTotal));
        result.add(row.findViewById(R.id.pedido_cadastro_itens_edQuantidade));
        result.add(row.findViewById(R.id.pedido_cadastro_itens_edPreco));
        result.add(row.findViewById(R.id.pedido_cadastro_itens_item_fotoProduto));
        result.add(row.findViewById(R.id.pedido_cadastro_itens_imgDelete));
        return result;
    }

    @Override
    public List<ItemPedido> geBeans() {
        return itens;
    }

    @Override
    public View getRowView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.pedido_cadastro_itens_lista, null);
    }

    @Override
    public void onTextChanged(View view, String text, AnterosViewHolder viewHolder) {
        if (viewHolder.viewEqualsTo(view, R.id.pedido_cadastro_itens_edQuantidade)) {
            if (!StringUtils.isEmpty(text)) {
                viewHolder.getBean().setQtProduto(new BigDecimal(text));
                viewHolder.getBean().setVlTotal(viewHolder.getBean().getVlProduto().multiply(viewHolder.getBean().getQtProduto()));
                TextView vlTotal = (TextView) viewHolder.getViewById(R.id.pedido_cadastro_itens_valorTotal);
                vlTotal.setText(viewHolder.getBean().getVlTotalAsString());
                PedidoCadastroActivity.calcularTotalPedido();
            }
        }
    }

    @Override
    public void onAfterTextChanged(View view, String text, AnterosViewHolder viewHolder) {

    }

    @Override
    public void onClickView(View view, AnterosViewHolder viewHolder) {
        if (viewHolder.viewEqualsTo(view, R.id.pedido_cadastro_itens_imgDelete)) {
            delete(viewHolder.getBean());
        }
    }

    protected void delete(final ItemPedido itemPedido) {
        new QuestionAlert(getContext(), getContext().getString(
                R.string.app_name), "Remover o item ?",
                new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        SQLRepository<ItemPedido, Long> pedidoItemFactory = AnterosVendasContext.getInstance().getSQLRepository(ItemPedido.class);

                        try {
                            if (itemPedido.getId() != null) {
                                ItemPedido ite = pedidoItemFactory.findOne(
                                        "SELECT PI.* FROM PEDIDO_ITEM PI WHERE PI.ID_ITEM = :PID_ITEM",
                                        new NamedParameter("PID_ITEM", itemPedido.getId()));

                                pedidoItemFactory.getTransaction().begin();
                                pedidoItemFactory.remove(ite);
                                pedidoItemFactory.getTransaction().commit();
                            }

                            remove(itemPedido);
                            PedidoConsultaActivity.pedido.setVlTotalPedido(PedidoConsultaActivity.pedido.getVlTotalPedido().subtract(itemPedido.getVlTotal()));
                            PedidoCadastroDadosFragment.atualizarValorTotal();
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                pedidoItemFactory.getTransaction().rollback();
                            } catch (Exception e1) {
                            }
                            if (e instanceof TransactionException) {
                                new ErrorAlert(getContext(), getContext().getResources().getString(
                                        R.string.app_name), "Ocorreu um erro ao remover o item do pedido. " + e.getCause()).show();
                            } else {
                                new ErrorAlert(getContext(), getContext().getResources().getString(
                                        R.string.app_name), "Ocorreu um erro ao remover o item do pedido. " + e.getMessage()).show();
                            }
                        }
                    }

                    public void onNegativeClick() {

                    }
                }).show();

    }

}


