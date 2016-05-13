package br.com.anteros.vendas.gui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

import br.com.anteros.core.utils.DateUtil;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.CondicaoPagamento;
import br.com.anteros.vendas.modelo.FormaPagamento;


/**
 * Created by eduardogreco on 5/12/16.
 */
public class PedidoCadastroDadosFragment extends Fragment implements View.OnClickListener {

    private EditText edNumero;
    private EditText edValorTotal;
    public static EditText edData;
    private static EditText edCliente;
    public static Spinner spCondicaoPagamento;
    public static Spinner spFormaPagamento;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pedido_cadastro_dados, null);

        edNumero = (EditText) view.findViewById(R.id.pedido_cadastro_dados_nr_pedido);
        edData = (EditText) view.findViewById(R.id.pedido_cadastro_dados_data_pedido);
        edData.setOnClickListener(this);
        spCondicaoPagamento = (Spinner) view.findViewById(R.id.pedido_cadastro_dados_cb_condicao_pagamento);
        spFormaPagamento = (Spinner) view.findViewById(R.id.pedido_cadastro_dados__cb_tipo_formaPagamento);
        edCliente = (EditText) view.findViewById(R.id.pedido_cadastro_dados_cliente);
        edCliente.setOnClickListener(this);
        edValorTotal = (EditText) view.findViewById(R.id.pedido_cadastro_dados_valor);

        spCondicaoPagamento.setAdapter(new ArrayAdapter<CondicaoPagamento>(getContext(), android.R.layout.simple_list_item_1, CondicaoPagamento.values()));
        spFormaPagamento.setAdapter(new ArrayAdapter<FormaPagamento>(getContext(), android.R.layout.simple_list_item_1, FormaPagamento.values()));
        try {
            bindView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void bindView() {
        edNumero.setText(PedidoConsultaActivity.pedido.getNrPedido().toString());
        edData.setText(DateUtil.toStringDateDMA(PedidoConsultaActivity.pedido.getDtPedido()));

        setDadosEdCliente();

        edValorTotal.setText(PedidoConsultaActivity.pedido.getVlTotalPedidoAsString());

        if (PedidoConsultaActivity.pedido.getCondicaoPagamento() != null) {
            spCondicaoPagamento.setSelection(PedidoConsultaActivity.pedido.getCondicaoPagamento().ordinal());
        }
        if (PedidoConsultaActivity.pedido.getFormaPagamento() != null) {
            spFormaPagamento.setSelection(PedidoConsultaActivity.pedido.getFormaPagamento().ordinal());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == edCliente) {
            new ClienteConsultaDialog(getContext()).show();
        } else if (v == edData) {
            selecionarData();
        }
    }

    public static void setDadosEdCliente() {
        if (PedidoConsultaActivity.pedido.getCliente() != null) {
            edCliente.setText(PedidoConsultaActivity.pedido.getCliente().getId() + " - " + PedidoConsultaActivity.pedido.getCliente().getRazaoSocial());
        }
    }

    private void selecionarData() {
        Calendar cal = Calendar.getInstance();
        if (edData.getText().length() > 0)
            cal.setTime(DateUtil.stringToDate(edData.getText().toString(), DateUtil.DATE));
        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                edData.setText(dayOfMonth + "/" + (monthOfYear + 1)
                        + "/" + year);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}
