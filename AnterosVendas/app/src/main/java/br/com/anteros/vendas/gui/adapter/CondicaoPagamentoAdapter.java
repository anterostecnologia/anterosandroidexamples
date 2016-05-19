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

import br.com.anteros.vendas.MenuItem;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.CondicaoPagamento;

/**
 * Adapter responsável por apresenta a lista de condições de pagamento
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 10/05/16.
 */
public class CondicaoPagamentoAdapter extends ArrayAdapter<CondicaoPagamento> {

    public CondicaoPagamentoAdapter(Context context, CondicaoPagamento[] lista) {
        super(context, android.R.layout.simple_list_item_1, lista);
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
         * Cria a view com o layout da condição.
         */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
        }

        /**
         * Obtém a condição de pagamento
         */
        CondicaoPagamento item = getItem(position);

        /**
         * Se o item não for nulo atribui os valores nos campos da view
         */
        if (item != null) {
            TextView tvCondicao = (TextView) convertView.findViewById(android.R.id.text1);
            tvCondicao.setText(item.getNomeFormatado());
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        /**
         * Cria a view com o layout da condição.
         */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
        }

        /**
         * Obtém a condição de pagamento
         */
        CondicaoPagamento item = getItem(position);

        /**
         * Se o item não for nulo atribui os valores nos campos da view
         */
        if (item != null) {
            TextView tvCondicao = (TextView) convertView.findViewById(android.R.id.text1);
            tvCondicao.setText(item.getNomeFormatado());
        }

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        CondicaoPagamento item = getItem(position);
        return item.ordinal();
    }

}
