package br.com.anteros.vendas.gui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.List;

import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.CondicaoPagamento;

/**
 * Created by eduardogreco on 5/12/16.
 */
public class CondicaoPagamentoAdapter extends BaseSpinnerAdapter<CondicaoPagamento> {

    public CondicaoPagamentoAdapter(Context context, List<CondicaoPagamento> itens) {
        super(context, R.layout.item_spinner, R.id.item_spinner_radio_button, itens);
    }

    public void bindDropDownListView(View convertView, ViewGroup parent,
                                     int position, CondicaoPagamento item) {
        RadioButton radio = (RadioButton) convertView.findViewById(R.id.item_spinner_radio_button);
        radio.setText(item.name());
    }

    public String getTextLabel(CondicaoPagamento item) {
        return item.name();
    }
}
