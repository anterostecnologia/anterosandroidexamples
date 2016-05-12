package br.com.anteros.vendas.gui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;

import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.ClienteConsultaDialogAdapter;
import br.com.anteros.vendas.modelo.Cliente;

/**
 * Created by eduardogreco on 5/12/16.
 */
public class ClienteConsultaDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    private SQLRepository<Cliente, Long> clienteRepository;
    private ListView listView;
    private ClienteConsultaDialogAdapter adapter;
    private ImageView imgFechar;

    public ClienteConsultaDialog(Context context) {
        super(context);
        setContentView(R.layout.cliente_consulta_dialog);
        getWindow().setLayout(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        listView = (ListView) findViewById(R.id.cliente_consulta_dialog_list_view);
        listView.setOnItemLongClickListener(this);
        imgFechar = (ImageView) findViewById(R.id.cliente_consulta_dialog_img_close);
        imgFechar.setOnClickListener(this);


        clienteRepository = AnterosVendasContext.getInstance().getSQLRepository(Cliente.class);
        List<Cliente> clientes = clienteRepository.find("SELECT CLI.ID_CLIENTE,      " +
                                                                "CLI.RAZAO_SOCIAL,    " +
                                                                "CLI.FANTASIA,        " +
                                                                "CLI.TP_LOGRADOURO,   " +
                                                                "CLI.LOGRADOURO,      " +
                                                                "CLI.NR_LOGRADOURO,   " +
                                                                "CLI.BAIRRO,          " +
                                                                "CLI.DS_CIDADE,       " +
                                                                "CLI.UF               " +
                                                                "FROM CLIENTE CLI         ");
        adapter = new ClienteConsultaDialogAdapter(context, clientes);

        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == imgFechar) {
            dismiss();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Cliente cliente = adapter.getItem(position);
        PedidoConsultaActivity.pedido.setCliente(cliente);
        PedidoCadastroDadosFragment.setDadosEdCliente();
        dismiss();
        return false;
    }
}
