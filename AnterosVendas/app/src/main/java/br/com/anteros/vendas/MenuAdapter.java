package br.com.anteros.vendas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by eduardogreco on 5/10/16.
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
