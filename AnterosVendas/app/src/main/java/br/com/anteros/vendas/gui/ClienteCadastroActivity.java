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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Date;
import java.util.Set;

import br.com.anteros.android.core.communication.ws.PostmonResponse;
import br.com.anteros.android.core.communication.ws.PostmonWebService;
import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.InfoAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.validation.api.ConstraintViolation;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.Cliente;
import br.com.anteros.vendas.modelo.Estado;
import br.com.anteros.vendas.modelo.TipoLogradouro;
import br.com.anteros.vendas.modelo.ValidacaoCliente;

/**
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 10/05/16.
 */
public class ClienteCadastroActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private EditText edRazao;
    private EditText edFantasia;
    private Spinner spTipoLogradouro;
    private EditText edLogradouro;
    private EditText edNrLogradouro;
    private EditText edCep;
    private EditText edBairro;
    private EditText edComplemento;
    private EditText edCidade;
    private Spinner spEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_cadastro);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        edRazao = (EditText) findViewById(R.id.cliente_cadastro_razaoSocial);
        edFantasia = (EditText) findViewById(R.id.cliente_cadastro_fantasia);
        spTipoLogradouro = (Spinner) findViewById(R.id.cliente_cadastro_cb_tipo_logradouro);
        edLogradouro = (EditText) findViewById(R.id.cliente_cadastro_logradouro);
        edNrLogradouro = (EditText) findViewById(R.id.cliente_cadastro_numero);
        edCep = (EditText) findViewById(R.id.cliente_cadastro_CEP);
        edCep.setOnFocusChangeListener(this);

        edBairro = (EditText) findViewById(R.id.cliente_cadastro_bairro);
        edComplemento = (EditText) findViewById(R.id.cliente_cadastro_complemento);
        edCidade = (EditText) findViewById(R.id.cliente_cadastro_cidade);
        spEstado = (Spinner) findViewById(R.id.cliente_cadastro_cb_estado);


        spTipoLogradouro.setAdapter(new ArrayAdapter<TipoLogradouro>(this, android.R.layout.simple_list_item_1, TipoLogradouro.values()));
        spEstado.setAdapter(new ArrayAdapter<Estado>(this, android.R.layout.simple_list_item_1, Estado.values()));

        bindView();
    }

    private void bindView() {
        edRazao.setText(ClienteConsultaActivity.cliente.getRazaoSocial());
        edFantasia.setText(ClienteConsultaActivity.cliente.getNomeFantasia());
        edLogradouro.setText(ClienteConsultaActivity.cliente.getLogradouro());
        edNrLogradouro.setText(ClienteConsultaActivity.cliente.getNrLogradouro());
        edCep.setText(ClienteConsultaActivity.cliente.getCep());
        edBairro.setText(ClienteConsultaActivity.cliente.getBairro());
        edComplemento.setText(ClienteConsultaActivity.cliente.getComplemento());
        edCidade.setText(ClienteConsultaActivity.cliente.getCidade());

        if (ClienteConsultaActivity.cliente.getTpLogradouro() != null) {
            spTipoLogradouro.setSelection(ClienteConsultaActivity.cliente.getTpLogradouro().ordinal());
        }

        if (ClienteConsultaActivity.cliente.getEstado() != null) {
            spEstado.setSelection(ClienteConsultaActivity.cliente.getEstado().ordinal());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.cliente_cadastro_action);
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
                cancelarCliente();
                break;

            case R.id.cliente_cadastro_action_salvar:
                new QuestionAlert(this, this.getResources().getString(R.string.app_name), "Deseja salvar o cliente?",
                        new QuestionAlert.QuestionListener() {

                            @Override
                            public void onPositiveClick() {
                                new SalvarCliente().execute();
                            }

                            @Override
                            public void onNegativeClick() {

                            }

                        }).show();
                break;

            case R.id.cliente_cadastro_action_anexo:
                startActivity(new Intent(this, AnexoConsultaActivity.class));
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            cancelarCliente();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
    }

    private void cancelarCliente() {
        new QuestionAlert(this, this.getResources().getString(
                R.string.app_name), "Deseja cancelar o cliente?",
                new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        setResult(RESULT_CANCELED);
                        finish();
                    }

                    public void onNegativeClick() {

                    }

                }).show();
    }

    private void salvarDadosCliente() {
        ClienteConsultaActivity.cliente.setRazaoSocial(edRazao.getText().toString());
        ClienteConsultaActivity.cliente.setNomeFantasia(edFantasia.getText().toString());
        ClienteConsultaActivity.cliente.setLogradouro(edLogradouro.getText().toString());
        ClienteConsultaActivity.cliente.setNrLogradouro(edNrLogradouro.getText().toString());
        ClienteConsultaActivity.cliente.setCep(edCep.getText().toString());
        ClienteConsultaActivity.cliente.setBairro(edBairro.getText().toString());
        ClienteConsultaActivity.cliente.setComplemento(edComplemento.getText().toString());
        ClienteConsultaActivity.cliente.setCidade(edCidade.getText().toString());
        ClienteConsultaActivity.cliente.setDtCadastro(ClienteConsultaActivity.cliente.getDtCadastro() != null ? ClienteConsultaActivity.cliente.getDtCadastro() : new Date());
        ClienteConsultaActivity.cliente.setEstado((Estado) spEstado.getSelectedItem());
        ClienteConsultaActivity.cliente.setTpLogradouro((TipoLogradouro) spTipoLogradouro.getSelectedItem());
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == edCep) {
            if (!hasFocus) {
                new PostmonWebService() {
                    @Override
                    protected void onPostExecute(PostmonResponse postmonResponse) {
                        if (postmonResponse != null) {
                            edCidade.setText(postmonResponse.getCidade().toUpperCase());
                            spEstado.setSelection(Estado.getEstadoByName(postmonResponse.getEstado()).ordinal());
                        }
                    }
                }.execute(edCep.getText().toString());
            }
        }
    }

    public class SalvarCliente extends AsyncTask<Integer, Void, String> {

        SQLRepository<Cliente, Long> clienteRepository = AnterosVendasContext.getInstance().getSQLRepository(Cliente.class);
        Set<ConstraintViolation<Cliente>> violations;
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(ClienteCadastroActivity.this);
            dialog.setTitle(getResources().getString(R.string.app_name));
            dialog.setMessage("Salvando cliente...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                salvarDadosCliente();

                violations = AnterosVendasContext.getInstance().getDefaultValidator().validate(ClienteConsultaActivity.cliente, ValidacaoCliente.class);
                if (violations.size() > 0) {
                    return "ERRO_VALIDACAO";
                }

                ClienteConsultaActivity.cliente.setAnexos(AnexoConsultaActivity.anexosCliente);
                clienteRepository.getTransaction().begin();
                clienteRepository.save(ClienteConsultaActivity.cliente);
                clienteRepository.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    clienteRepository.getTransaction().rollback();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                return e.getMessage() + "";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing())
                dialog.dismiss();

            if (result == null) {

                new InfoAlert(ClienteCadastroActivity.this, getString(R.string.app_name),
                        "Cliente salvo com sucesso!", new InfoAlert.InfoListener() {
                    @Override
                    public void onOkClick() {
                        setResult(RESULT_OK);
                        finish();
                    }
                }).show();

            } else if (result.equals("ERRO_VALIDACAO")) {
                new MensagemErrorDialog<Cliente>(ClienteCadastroActivity.this,
                        "Atenção!", violations).show();
            } else {
                new ErrorAlert(ClienteCadastroActivity.this, getString(R.string.app_name),
                        "Salvando cliente: " + result).show();
            }
        }

    }
}
