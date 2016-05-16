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
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.Produto;

/**
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 12/05/16.
 */
public class ProdutoConsultaAdapter  extends BaseAdapter {
    private Context context;
    private LayoutInflater lInflater;
    private List<Produto> produtos;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public ProdutoConsultaAdapter(Context context, List<Produto> produtos) {
        this.context = context;
        this.produtos = produtos;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return produtos.size();
    }

    @Override
    public Object getItem(int position) {
        return produtos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.produto_consulta_item, parent, false);
        }

        Produto p = getProduto(position);

        ((TextView) view.findViewById(R.id.produto_descricao)).setText(p.getNomeProduto());
        TextView tvPreco = (TextView) view.findViewById(R.id.produto_preco);
        BigDecimal price = p.getVlProduto().setScale(2, RoundingMode.HALF_EVEN);
        tvPreco.setText(currencyFormat.format(price));

        ((ImageView) view.findViewById(R.id.produto_foto)).setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(p.getFotoProduto())));

        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
        cbBuy.setOnCheckedChangeListener(myCheckChangList);
        cbBuy.setTag(position);
        cbBuy.setChecked(p.isSelected());
        return view;
    }

    private Produto getProduto(int position) {
        return ((Produto) getItem(position));
    }

    public List<Produto> getSelectedItens() {
        List<Produto> box = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.isSelected())
                box.add(p);
        }
        return box;
    }

    private CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            getProduto((Integer) buttonView.getTag()).setSelected(isChecked);
        }
    };
}