package br.com.anteros.vendas.gui.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.TipoLogradouro;

/**
 * Created by eduardogreco on 5/11/16.
 */
public class TipoLogradouroAdapter extends BaseSpinnerAdapter<TipoLogradouro> {

    public TipoLogradouroAdapter(Context context, List<TipoLogradouro> itens) {
        super(context, R.layout.item_spinner, R.id.item_spinner_radio_button, itens);
    }

    public void bindDropDownListView(View convertView, ViewGroup parent,
                                     int position, TipoLogradouro item) {
        RadioButton radio = (RadioButton) convertView.findViewById(R.id.item_spinner_radio_button);
        radio.setText(item.name());
    }

    public String getTextLabel(TipoLogradouro item) {
        return item.name();
    }
}
