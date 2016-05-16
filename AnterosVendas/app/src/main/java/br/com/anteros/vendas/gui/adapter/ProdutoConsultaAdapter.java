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
 *  Adapter responsável por apresentar a consulta dos produtos.
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 12/05/16.
 */
public class ProdutoConsultaAdapter  extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
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

    /**
     * Retorna o tamanho da lista de produtos
     * @return Tamanho
     */
    @Override
    public int getCount() {
        return produtos.size();
    }

    /**
     * Retorna o produto de acordo com a posição
     * @param position Posição
     * @return Produto
     */
    @Override
    public Produto getItem(int position) {
        return produtos.get(position);
    }

    /**
     * Retorna o id do Item na posição
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Retorna a view de acordo com o layout da consulta de produtos
     * @param position Posição da view
     * @param convertView View onde será mostrado os dados
     * @param parent Grupo pai da view
     * @return View para apresentação
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.produto_consulta_item, parent, false);
        }

        /**
         * Obtém o produto de acordo com a posição da view
         */
        Produto p = getProduto(position);

        /**
         * Atribui dados do produto nos campos do layout da view.
         */
        ((TextView) view.findViewById(R.id.produto_descricao)).setText(p.getNomeProduto());
        TextView tvPreco = (TextView) view.findViewById(R.id.produto_preco);
        BigDecimal price = p.getVlProduto().setScale(2, RoundingMode.HALF_EVEN);
        tvPreco.setText(currencyFormat.format(price));

        /**
         * Atribui a imagem do produto no imageview
         */
        ((ImageView) view.findViewById(R.id.produto_foto)).setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(p.getFotoProduto())));

        /**
         * Configura checkbox do produto
         */
        CheckBox chProduto = (CheckBox) view.findViewById(R.id.cbBox);
        chProduto.setOnCheckedChangeListener(this);
        chProduto.setTag(position);
        chProduto.setChecked(p.isSelected());
        return view;
    }

    /**
     * Retorna o produto de acordo com a posição
     * @param position Posição do produto dentro da lista
     * @return Produto
     */
    private Produto getProduto(int position) {
        return ((Produto) getItem(position));
    }

    /**
     * Retorna os itens selecionados(checados) na lista
     * @return
     */
    public List<Produto> getItensSelecionados() {
        List<Produto> result = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.isSelected())
                result.add(p);
        }
        return result;
    }

    /**
     * Evento para tratar item selecionado(checado) na lista.
     * @param buttonView checkbox
     * @param isChecked informa se está checado ou não
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        getProduto((Integer) buttonView.getTag()).setSelected(isChecked);
    }
}