package br.com.anteros.vendas.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Date;
import java.util.List;

import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.PedidoConsultaAdapter;
import br.com.anteros.vendas.modelo.PedidoVenda;

/**
 * Created by eduardogreco on 5/10/16.
 */
public class PedidoConsultaActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    public static PedidoVenda pedido;
    private ListView lvPedidos;
    private PedidoConsultaAdapter adapter;
    private final int REQUISICAO = 1000;
    private SQLRepository<PedidoVenda, Long> pedidoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedido_consulta);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        pedidoRepository = AnterosVendasContext.getInstance().getSQLRepository(PedidoVenda.class);

        lvPedidos = (ListView) findViewById(R.id.pedido_consulta_list_view);
        lvPedidos.setOnItemLongClickListener(this);

        new BuscarPedidosConsulta().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.pedido_consulta_action);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.pedido_consulta_action_adicionar:
                pedido = new PedidoVenda();
                pedido.setNrPedido(Long.valueOf(new Date().getTime()));
                pedido.setDtPedido(new Date());
                Intent intent = new Intent(this, PedidoCadastroActivity.class);
                startActivityForResult(intent, REQUISICAO);
                break;
        }
        return true;
    }

    private class BuscarPedidosConsulta extends AsyncTask<Void, Void, List<PedidoVenda>> {

        private ProgressDialog progress;

        @Override
        public void onPreExecute() {
            progress = ProgressDialog.show(PedidoConsultaActivity.this,
                    getResources().getString(R.string.app_name), "Aguarde...",
                    true);
        }

        @Override
        protected List<PedidoVenda> doInBackground(Void... params) {
            return pedidoRepository.find("SELECT  P.ID_PEDIDOVENDA,                " +
                                                " P.NR_PEDIDO,                     " +
                                                " P.DT_PEDIDO,                     " +
                                                " P.TP_CONDICAO_PGTO,              " +
                                                " P.FORMA_PAGTO,                   " +
                                                " P.VL_TOTAL_PEDIDO,               " +
                                                " C.ID_CLIENTE,                    " +
                                                " C.RAZAO_SOCIAL                   " +
                                         " FROM PEDIDOVENDA P, CLIENTE C           " +
                                        " WHERE C.ID_CLIENTE = P.ID_CLIENTE        ");
        }

        @Override
        public void onPostExecute(List<PedidoVenda> pedidos) {
            adapter = new PedidoConsultaAdapter(PedidoConsultaActivity.this, pedidos);
            lvPedidos.setAdapter(adapter);
            progress.dismiss();
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == lvPedidos) {
            PedidoVenda p = adapter.getItem(position);
            pedido = pedidoRepository.findOne(
                    "SELECT P.* FROM PEDIDOVENDA P WHERE P.ID_PEDIDOVENDA = :PID_PEDIDOVENDA",
                    new NamedParameter("PID_PEDIDOVENDA", p.getId()));
            Intent intent = new Intent(this, PedidoCadastroActivity.class);
            startActivityForResult(intent, REQUISICAO);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUISICAO) {
            if (resultCode == RESULT_OK) {
                new BuscarPedidosConsulta().execute();
            }
        }
    }
}
