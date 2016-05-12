package br.com.anteros.vendas.gui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.anteros.persistence.parameter.NamedParameter;
import android.view.View.OnClickListener;
import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.persistence.transaction.impl.TransactionException;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.Cliente;

/**
 * Created by eduardogreco on 5/10/16.
 */
public class ClienteConsultaAdapter extends ArrayAdapter<Cliente> {

    public ClienteConsultaAdapter(Context context, List<Cliente> objects) {
        super(context, R.layout.cliente_consulta_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cliente_consulta_item, null);
        }

        final Cliente item = (Cliente) getItem(position);

        TextView tvRazaoCliente = (TextView) convertView.findViewById(R.id.cliente_consulta_item_nomeCliente);
        TextView tvFantasia = (TextView) convertView.findViewById(R.id.cliente_consulta_item_fantasia);
        TextView tvEndereco = (TextView) convertView.findViewById(R.id.cliente_consulta_item_endereco);
        TextView tvCidade = (TextView) convertView.findViewById(R.id.cliente_consulta_item_cidade);
        ImageView imgDelete = (ImageView) convertView.findViewById(R.id.cliente_consulta_item_imgDelete);

        imgDelete.setEnabled(item != null);

        imgDelete.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                delete(item);
            }
        });

        if (item != null) {
            tvRazaoCliente.setText(item.getId() + " - " + item.getRazaoSocial());
            tvFantasia.setText(item.getNomeFantasia());
            tvEndereco.setText(item.getTpLogradouro() + " " + item.getLogradouro() + ", Nr. " + item.getNrLogradouro() + " - " + item.getBairro());
            tvCidade.setText(item.getCidade() + "/" + item.getEstado().name());
        }

        return convertView;
    }

    protected void delete(final Cliente cliente) {
        new QuestionAlert(getContext(), getContext().getString(
                R.string.app_name), "Remover Cliente ?",
                new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        SQLRepository<Cliente, Long> clienteFactory = AnterosVendasContext.getInstance().getSQLRepository(Cliente.class);

                        try {
                            Cliente cli = clienteFactory.findOne(
                                    "SELECT P.* FROM CLIENTE P WHERE P.ID_CLIENTE = :PID_CLIENTE",
                                    new NamedParameter("PID_CLIENTE", cliente.getId()));

                            clienteFactory.getTransaction().begin();
                            clienteFactory.remove(cli);
                            clienteFactory.getTransaction().commit();

                            remove(cliente);
                            notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                clienteFactory.getTransaction().rollback();
                            } catch (Exception e1) {
                            }
                            if (e instanceof TransactionException) {
                                new ErrorAlert(getContext(), getContext().getResources().getString(
                                        R.string.app_name), "Ocorreu um erro ao remover Cliente. " + e.getCause()).show();
                            } else {
                                new ErrorAlert(getContext(), getContext().getResources().getString(
                                        R.string.app_name), "Ocorreu um erro ao remover Cliente. " + e.getMessage()).show();
                            }
                        }
                    }

                    public void onNegativeClick() {

                    }
                }).show();

    }
}