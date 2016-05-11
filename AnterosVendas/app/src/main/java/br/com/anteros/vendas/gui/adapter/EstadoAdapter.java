package br.com.anteros.vendas.gui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.List;

import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.Estado;

/**
 * Created by eduardogreco on 5/11/16.
 */
public class EstadoAdapter extends BaseSpinnerAdapter<Estado> {

    public EstadoAdapter(Context context, List<Estado> itens) {
        super(context, R.layout.item_spinner, R.id.item_spinner_radio_button, itens);
    }

    public void bindDropDownListView(View convertView, ViewGroup parent,
                                     int position, Estado item) {
        RadioButton radio = (RadioButton) convertView.findViewById(R.id.item_spinner_radio_button);
        radio.setText(item.name());
    }

    public String getTextLabel(Estado item) {
        return item.name();
    }
}
