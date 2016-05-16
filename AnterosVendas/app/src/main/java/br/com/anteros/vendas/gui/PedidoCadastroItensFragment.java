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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.PedidoCadastroItensFragmentAdapter;


/**
 * Fragmento responsável por mostrar a lista de itens do pedido.
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 12/05/16.
 */
public class PedidoCadastroItensFragment extends Fragment {

    private ListView lvItens;
    public static PedidoCadastroItensFragmentAdapter adapter;

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
