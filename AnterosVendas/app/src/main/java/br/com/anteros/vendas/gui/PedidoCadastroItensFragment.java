package br.com.anteros.vendas.gui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.PedidoCadastroItensFragmentAdapter;

/**
 * Created by eduardogreco on 5/12/16.
 */
public class PedidoCadastroItensFragment extends Fragment {

    private ListView lvItens;
    private PedidoCadastroItensFragmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pedido_cadastro_itens, null);

        lvItens = (ListView) view.findViewById(R.id.pedido_cadastro_itens_list_view);

        adapter = new PedidoCadastroItensFragmentAdapter(getContext(), PedidoConsultaActivity.pedido.getItens());
        lvItens.setAdapter(adapter);

        return view;
    }

}
