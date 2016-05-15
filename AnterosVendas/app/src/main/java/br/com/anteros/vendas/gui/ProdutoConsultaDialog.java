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
import android.content.DialogInterface;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.anteros.android.ui.controls.WarningAlert;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.ClienteConsultaDialogAdapter;
import br.com.anteros.vendas.gui.adapter.ProdutoConsultaAdapter;
import br.com.anteros.vendas.modelo.Cliente;
import br.com.anteros.vendas.modelo.ItemPedido;
import br.com.anteros.vendas.modelo.Produto;

/**
 * Created by eduardogreco on 5/12/16.
 */
public class ProdutoConsultaDialog extends DialogFragment {

    private SQLRepository<Produto, Long> produtoRepository;
    private ListView listView;
    private ProdutoConsultaAdapter adapter;
    private OnDismissListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.produto_consulta_dialog, container, false);
        listView = (ListView) v.findViewById(R.id.produto_consulta_dialog_list_view);


        produtoRepository = AnterosVendasContext.getInstance().getSQLRepository(Produto.class);
        List<Produto> produtos = produtoRepository.find("SELECT PRO.* FROM PRODUTO PRO");
        adapter = new ProdutoConsultaAdapter(getContext(), produtos);

        listView.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.produto_consulta_dialog_action_selecionar:
                        selecionarProdutos();
                        break;
                }
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.produto_consulta_dialog_action);
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void selecionarProdutos() {
        List<Produto> selectedItens = adapter.getSelectedItens();
        List<ItemPedido> pedidoItens = PedidoConsultaActivity.pedido.getItens();
        List<Produto> itensDeleted = new ArrayList<>();

        if (!pedidoItens.isEmpty()) {
            for (ItemPedido item : pedidoItens) {
                for (Produto prod: selectedItens) {
                    if (item.getProduto().getId().equals(prod.getId())) {
                        item.setQtProduto(item.getQtProduto().add(BigDecimal.ONE));
                        item.setVlTotal(item.getVlProduto().multiply(item.getQtProduto()));
                        itensDeleted.add(prod);
                        break;
                    }
                }
            }

            selectedItens.removeAll(itensDeleted);

            for (Produto prod: selectedItens) {
                ItemPedido item = new ItemPedido();
                item.setQtProduto(BigDecimal.ONE);
                item.setPedidoVenda(PedidoConsultaActivity.pedido);
                item.setProduto(prod);
                item.setVlProduto(prod.getVlProduto());
                item.setVlTotal(item.getVlProduto().multiply(item.getQtProduto()));
                pedidoItens.add(item);
            }


        } else {
            for (Produto prod: selectedItens) {
                ItemPedido item = new ItemPedido();
                item.setQtProduto(BigDecimal.ONE);
                item.setPedidoVenda(PedidoConsultaActivity.pedido);
                item.setProduto(prod);
                item.setVlProduto(prod.getVlProduto());
                item.setVlTotal(item.getVlProduto().multiply(item.getQtProduto()));
                pedidoItens.add(item);
            }
        }
        PedidoConsultaActivity.pedido.setItens(pedidoItens);
        dismiss();
    }

    public OnDismissListener getOnDismissListener() {
        return listener;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null)
            listener.onDismiss(dialog);
    }

    public interface OnDismissListener {
        public void onDismiss(DialogInterface dialog);
    }
}
