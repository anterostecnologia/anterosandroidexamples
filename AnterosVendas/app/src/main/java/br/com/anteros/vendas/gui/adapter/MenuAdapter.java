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

/**
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 10/05/16.
 */
public class MenuAdapter extends ArrayAdapter<MenuItem> {

    public MenuAdapter(Context context, int textViewResourceId, List<MenuItem> lista) {
        super(context, textViewResourceId, lista);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.menu_item, null);
        }

        MenuItem item = (MenuItem) getItem(position);

        if (item != null) {
            TextView tvTitulo = (TextView) convertView.findViewById(R.id.menu_item_titulo);
            TextView tvDescricao = (TextView) convertView.findViewById(R.id.menu_item_descricao);
            TextView tvDescricao2 = (TextView) convertView.findViewById(R.id.menu_item_descricao2);

            ImageView imgItem = (ImageView) convertView.findViewById(R.id.menu_item_imgItem);
            View view = convertView.findViewById(R.id.menu_item_viewColor);

            tvTitulo.setText(item.getTitle());
            tvDescricao.setText(item.getDescricao());

            if (!item.getDescricao2().equals("")) {
                tvDescricao2.setText(item.getDescricao2());
                tvDescricao2.setVisibility(View.VISIBLE);

            } else {
                tvDescricao2.setVisibility(View.INVISIBLE);
            }

            imgItem.setImageDrawable(item.getIcon());
            view.setBackgroundColor(item.getCor());
        }

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        MenuItem item = getItem(position);
        return item.getId();
    }

}
