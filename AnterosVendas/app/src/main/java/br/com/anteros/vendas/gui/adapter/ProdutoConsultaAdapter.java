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
import java.util.ArrayList;
import java.util.List;

import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.Produto;

/**
 * Created by edson on 12/05/16.
 */
public class ProdutoConsultaAdapter  extends BaseAdapter {
    private Context context;
    private LayoutInflater lInflater;
    private List<Produto> produtos;

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
        ((TextView) view.findViewById(R.id.produto_preco)).setText(p.getVlProduto() + "");
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

    private List<Produto> getBox() {
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