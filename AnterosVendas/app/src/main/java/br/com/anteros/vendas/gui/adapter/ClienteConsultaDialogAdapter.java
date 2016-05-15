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
import android.widget.TextView;

import java.util.List;

import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.Cliente;

/**
 * Created by eduardogreco on 5/12/16.
 */
public class ClienteConsultaDialogAdapter extends ArrayAdapter<Cliente> {

    public ClienteConsultaDialogAdapter(Context context, List<Cliente> objects) {
        super(context, R.layout.cliente_consulta_dialog_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cliente_consulta_dialog_item, null);
        }

        final Cliente item = (Cliente) getItem(position);

        TextView tvRazaoCliente = (TextView) convertView.findViewById(R.id.cliente_consulta_dialog_item_nomeCliente);
        TextView tvFantasia = (TextView) convertView.findViewById(R.id.cliente_consulta_dialog_item_fantasia);
        TextView tvEndereco = (TextView) convertView.findViewById(R.id.cliente_consulta_dialog_item_endereco);
        TextView tvCidade = (TextView) convertView.findViewById(R.id.cliente_consulta_dialog_item_cidade);

        if (item != null) {
            tvRazaoCliente.setText(item.getId() + " - " + item.getRazaoSocial());
            tvFantasia.setText(item.getNomeFantasia());
            tvEndereco.setText(item.getTpLogradouro() + " " + item.getLogradouro() + ", Nr. " + item.getNrLogradouro() + " - " + item.getBairro());
            tvCidade.setText(item.getCidade() + "/" + item.getEstado().name());
        }

        return convertView;
    }
}
