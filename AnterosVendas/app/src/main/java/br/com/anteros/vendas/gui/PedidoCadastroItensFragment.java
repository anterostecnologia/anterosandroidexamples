package br.com.anteros.vendas.gui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.anteros.vendas.R;

/**
 * Created by eduardogreco on 5/12/16.
 */
public class PedidoCadastroItensFragment extends Fragment {

    private ImageView imgDelete;
    private TextView tvProduto;
    private EditText edQuantidade;
    private EditText edPreco;
    private TextView tvValorTotal;
    private ImageView imgProduto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pedido_cadastro_itens, null);

        tvProduto = (TextView) view.findViewById(R.id.pedido_cadastro_itens_produto);
        edQuantidade = (EditText) view.findViewById(R.id.pedido_cadastro_itens_edQuantidade);
        edPreco = (EditText) view.findViewById(R.id.pedido_cadastro_itens_edPreco);
        tvValorTotal = (TextView) view.findViewById(R.id.pedido_cadastro_itens_valorTotal);
        imgDelete = (ImageView) view.findViewById(R.id.pedido_cadastro_itens_imgDelete);
        imgProduto = (ImageView) view.findViewById(R.id.pedido_cadastro_itens_item_fotoProduto);

        try {
            bindView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void bindView() {

    }

}
