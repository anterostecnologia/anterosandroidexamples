package br.com.anteros.vendas.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import br.com.anteros.vendas.R;

/**
 * Created by eduardogreco on 5/11/16.
 */
public class PedidoConsultaActivity extends AppCompatActivity implements
        AdapterView.OnItemLongClickListener, View.OnClickListener {

    private ListView lvPedidos;
    private ImageView imgFinalizar;
    private ImageView imgAddPedido;
    private TextView lbQtdade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedido_consulta);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
}
