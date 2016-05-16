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
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.PedidoCadastroActivity;
import br.com.anteros.vendas.gui.PedidoCadastroDadosFragment;
import br.com.anteros.vendas.gui.PedidoConsultaActivity;
import br.com.anteros.vendas.modelo.ItemPedido;

/**
 * Adapter responsável pela lista com edição dos itens do pedido.
 * Este adapter extends AnterosArrayAdapterWithViewHolder que implementa o
 * padrão de projeto View Holder e permite a edição dos campos dentro da ListView.
 * Importante: Para o correto funcionamento da edição a declaração da Activity
 * no manifesto deve conter o seguinte atributo:
 *
 *  android:windowSoftInputMode="adjustPan|stateHidden"
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 13/05/16.
 */
public class PedidoCadastroItensFragmentAdapter extends AnterosArrayAdapterWithViewHolder<ItemPedido> {

    /**
     * Lista de itens do pedido
     */
    private List<ItemPedido> itens;

    public PedidoCadastroItensFragmentAdapter(Context context, List<ItemPedido> objects) {
        super(context, R.layout.pedido_cadastro_itens_lista, objects);
        this.itens = objects;
    }

    /**
     * Carrega os valores do ItemPedido do pedido nos campos da ViewHolder que controla o registro
     * dentro da ListView.
     * @param viewHolder
     * @param currentBean
     */
    @Override
    public void loadValuesFromCurrentBean(AnterosViewHolder viewHolder, ItemPedido currentBean) {
        /**
         * Obtém os campos do layout dentro da ViewHolder
         */
        TextView produto = (TextView) viewHolder.getViewById(R.id.pedido_cadastro_itens_produto);
        TextView vlTotal = (TextView) viewHolder.getViewById(R.id.pedido_cadastro_itens_valorTotal);
        TextView preco = (TextView) viewHolder.getViewById(R.id.pedido_cadastro_itens_edPreco);
        EditText quantidade = (EditText) viewHolder.getViewById(R.id.pedido_cadastro_itens_edQuantidade);
        ImageView fotoProduto = (ImageView) viewHolder.getViewById(R.id.pedido_cadastro_itens_item_fotoProduto);

        /**
         * Obtém o ItemPedido dentro da ViewHolder.
         */
        ItemPedido item = viewHolder.getBean();

        /**
         * Atribui os valores do ItemPedido nos campos
         */
        produto.setText(item.getProduto().getId() + " - " + item.getProduto().getNomeProduto());
        vlTotal.setText(item.getVlTotalAsString());
        preco.setText(item.getVlProdutoAsString());
        quantidade.setText(item.getQtProduto().toPlainString());
        /**
         * Atribui a foto do produto
         */
        if (item.getProduto().getFotoProduto() != null) {
            fotoProduto.setImageBitmap(BitmapFactory.decodeByteArray(item.getProduto().getFotoProduto(), 0, item.getProduto().getFotoProduto().length));
        }
    }

    /**
     * Retorna a lista de views(campos) que se deseja que a ViewHolder controla.
     * Somente as views retornadas poderão ser acessadas no método loadValuesFromCurrentBean.
     * @param row View correspondente a linha dentro layout da ListView.
     * @return Conjunto de views para controlar.
     */
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

    /**
     * Lista de itens do pedido
     * @return Lista de itens
     */
    @Override
    public List<ItemPedido> geBeans() {
        return itens;
    }

    /**
     * Devolve a view correspondente ao layout da linha dentro da ListView.
     * @param position Posição dentro da lista
     * @param convertView View
     * @param parent Pai da view
     * @return View criada
     */
    @Override
    public View getRowView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.pedido_cadastro_itens_lista, null);
        }
        return convertView;
    }

    /**
     * Evento quando qualquer campo de edição sofrer alteração. Neste evento é possível realizar
     * cálculos ou atribuir valores a outros campos.
     * @param view View
     * @param text Texto alterado
     * @param viewHolder View Holder
     */
    @Override
    public void onTextChanged(View view, String text, AnterosViewHolder viewHolder) {
        /**
         * Aqui é necessário verificar se view recebida é a que vc quer usando o
         * método viewEqualsTo da viewHolder.
         */
        if (viewHolder.viewEqualsTo(view, R.id.pedido_cadastro_itens_edQuantidade)) {
            if (!StringUtils.isEmpty(text)) {
                /**
                 * Calcula o valor total e atribui no TextView vlTotal.
                 */
                viewHolder.getBean().setQtProduto(new BigDecimal(text));
                viewHolder.getBean().setVlTotal(viewHolder.getBean().getVlProduto().multiply(viewHolder.getBean().getQtProduto()));
                TextView vlTotal = (TextView) viewHolder.getViewById(R.id.pedido_cadastro_itens_valorTotal);
                vlTotal.setText(viewHolder.getBean().getVlTotalAsString());
                /**
                 * Calculo o total do pedido
                 */
                PedidoCadastroActivity.calcularTotalPedido();
            }
        }
    }

    /**
     * Evento que ocorre logo após ocorrer alteração em qualquer campo dentro do layout.
     * @param view View
     * @param text Texto alterado
     * @param viewHolder View Holder.
     */
    @Override
    public void onAfterTextChanged(View view, String text, AnterosViewHolder viewHolder) {

    }

    /**
     * Evento que ocorre se clicar em qualquer view sendo controlada pela View Holder.
     * @param view View
     * @param viewHolder View Holder
     */
    @Override
    public void onClickView(View view, AnterosViewHolder viewHolder) {
        /**
         * Aqui é necessário verificar se view recebida é a que vc quer usando o
         * método viewEqualsTo da viewHolder.
         */
        if (viewHolder.viewEqualsTo(view, R.id.pedido_cadastro_itens_imgDelete)) {
            /**
             * Clicou no removerPedido chama o remover pedido.
             */
            removePedido(viewHolder.getBean());
        }
    }

    /**
     * Remove o pedido
     * @param itemPedido ItemPedido a ser removido
     */
    protected void removePedido(final ItemPedido itemPedido) {
        new QuestionAlert(getContext(), getContext().getString(
                R.string.app_name), "Remover o item ?",
                new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        try {
                            /**
                             * Remove o item do pedido da lista do pedido. Aqui não é necessário
                             * remover no banco de dados pois o framework irá fazer isso quando salvar o pedido.
                             */
                            remove(itemPedido);

                            /**
                             * Recalcula total do pedido
                             */
                            PedidoConsultaActivity.pedido.setVlTotalPedido(PedidoConsultaActivity.pedido.getVlTotalPedido().subtract(itemPedido.getVlTotal()));
                            PedidoCadastroDadosFragment.atualizarValorTotal();
                            /**
                             * Notifica o adapter que a lista alterou.
                             */
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                            new ErrorAlert(getContext(), getContext().getResources().getString(
                                    R.string.app_name), "Ocorreu um erro ao remover o item do pedido. " + e.getMessage()).show();

                        }
                    }

                    public void onNegativeClick() {

                    }
                }).show();

    }

}


