/*
 * Copyright 2016 Anteros Tecnologia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.AnexoConsultaAdapter;
import br.com.anteros.vendas.gui.adapter.ClienteConsultaAdapter;
import br.com.anteros.vendas.modelo.Anexo;
import br.com.anteros.vendas.modelo.Cliente;

/**
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 10/05/16.
 */
public class AnexoConsultaActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private static final int REQ_NOVO_ANEXO = 0;
    private static final int REQ_EDITAR_ANEXO = 2;
    private ListView lvAnexos;
    private AnexoConsultaAdapter adapter;
    public static List<Anexo> anexosCliente;
    private SQLRepository<Anexo, Long> anexoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anexo_consulta);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        anexoRepository = AnterosVendasContext.getInstance().getSQLRepository(Anexo.class);

        lvAnexos = (ListView) findViewById(R.id.anexo_consulta_list_view);
        lvAnexos.setOnItemLongClickListener(this);

        new BuscarAnexos().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.anexo_consulta_action);
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

            case R.id.anexo_consulta_action_adicionar:
                Anexo novoAnexo = new Anexo();
                novoAnexo.setCliente(ClienteConsultaActivity.cliente);
                AnexoCadastroActivity.setAnexo(novoAnexo);
                startActivityForResult(new Intent(this, AnexoCadastroActivity.class), REQ_NOVO_ANEXO);
                break;
        }
        return true;
    }

    private class BuscarAnexos extends AsyncTask<Void, Void, List<Anexo>> {

        private ProgressDialog progress;

        @Override
        public void onPreExecute() {
            progress = ProgressDialog.show(AnexoConsultaActivity.this,
                    getResources().getString(R.string.app_name), "Aguarde...",
                    true);
        }

        @Override
        protected List<Anexo> doInBackground(Void... params) {
            return anexoRepository.find(
                    "SELECT A.* FROM ANEXO A WHERE A.ID_CLIENTE = :PID_CLIENTE",
                    new NamedParameter("PID_CLIENTE", ClienteConsultaActivity.cliente.getId()));
        }

        @Override
        public void onPostExecute(List<Anexo> anexos) {
            anexosCliente = anexos;
            adapter = new AnexoConsultaAdapter(AnexoConsultaActivity.this, anexosCliente);
            lvAnexos.setAdapter(adapter);
            progress.dismiss();
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == lvAnexos) {
            Anexo anexo = adapter.getItem(position);
            AnexoCadastroActivity.setAnexo(anexo);
            startActivityForResult(new Intent(this, AnexoCadastroActivity.class), REQ_EDITAR_ANEXO);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_NOVO_ANEXO) {
            switch (resultCode) {
                case AnexoCadastroActivity.ALTEROU_ANEXO:

                    if (anexosCliente == Collections.EMPTY_LIST) {
                        anexosCliente = new ArrayList<Anexo>();
                    }

                    anexosCliente.add(AnexoCadastroActivity.getAnexo());
                    adapter = new AnexoConsultaAdapter(AnexoConsultaActivity.this, anexosCliente);
                    lvAnexos.setAdapter(adapter);
                    break;
            }
        } else if (requestCode == REQ_EDITAR_ANEXO) {
            switch (resultCode) {
                case AnexoCadastroActivity.ALTEROU_ANEXO:
                    adapter = new AnexoConsultaAdapter(AnexoConsultaActivity.this, anexosCliente);
                    lvAnexos.setAdapter(adapter);
                    break;
            }
        }
    }
}
