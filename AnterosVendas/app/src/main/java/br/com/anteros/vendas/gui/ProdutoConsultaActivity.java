package br.com.anteros.vendas.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ListView;

import java.util.List;

import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.ProdutoConsultaAdapter;
import br.com.anteros.vendas.modelo.Produto;

public class ProdutoConsultaActivity extends AppCompatActivity {

    private ListView lvProdutos;
    private ProdutoConsultaAdapter adapter;
    private final int REQUISICAO = 1000;
    private SQLRepository<Produto, Long> produtoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_consulta);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        produtoRepository = AnterosVendasContext.getInstance().getSQLRepository(Produto.class);
        lvProdutos = (ListView) findViewById(R.id.lv_produto);

        new BuscarProdutos().execute();

    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.cliente_consulta_action);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });

        return true;
    }
*/
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

//            case R.id.cliente_consulta_action_adicionar:
//                Intent intent = new Intent(this, ClienteCadastroActivity.class);
//                startActivityForResult(intent, REQUISICAO);
//                break;
        }
        return true;
    }

    private class BuscarProdutos extends AsyncTask<Void, Void, List<Produto>> {

        private ProgressDialog progress;

        @Override
        public void onPreExecute() {
            progress = ProgressDialog.show(ProdutoConsultaActivity.this,
                    getResources().getString(R.string.app_name), "Aguarde...",
                    true);
        }

        @Override
        protected List<Produto> doInBackground(Void... params) {
            return produtoRepository.find("SELECT PRO.* FROM PRODUTO PRO");
        }

        @Override
        public void onPostExecute(List<Produto> produtos) {
            adapter = new ProdutoConsultaAdapter(ProdutoConsultaActivity.this, produtos);
            lvProdutos.setAdapter(adapter);
            progress.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUISICAO) {
            if (resultCode == RESULT_OK) {
                new BuscarProdutos().execute();
            }
        }
    }

}
