package br.com.anteros.vendas.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.InfoAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.validation.api.ConstraintViolation;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.EstadoAdapter;
import br.com.anteros.vendas.gui.adapter.TipoLogradouroAdapter;
import br.com.anteros.vendas.modelo.Cliente;
import br.com.anteros.vendas.modelo.Estado;
import br.com.anteros.vendas.modelo.TipoLogradouro;
import br.com.anteros.vendas.modelo.ValidacaoCliente;

/**
 * Created by eduardogreco on 5/10/16.
 */
public class ClienteCadastroActivity extends AppCompatActivity implements View.OnClickListener {

    private Cliente cliente;
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
        edBairro = (EditText) findViewById(R.id.cliente_cadastro_bairro);
        edComplemento = (EditText) findViewById(R.id.cliente_cadastro_complemento);
        edCidade = (EditText) findViewById(R.id.cliente_cadastro_cidade);
        spEstado = (Spinner) findViewById(R.id.cliente_cadastro_cb_estado);

        spTipoLogradouro.setAdapter(new TipoLogradouroAdapter(this, Arrays.asList(TipoLogradouro.values())));
        spEstado.setAdapter(new EstadoAdapter(this, Arrays.asList(Estado.values())));

        if (getIntent().hasExtra("cliente")) {
            cliente = (Cliente) getIntent().getSerializableExtra("cliente");
        } else {
            cliente = new Cliente();
        }
        bindView();
    }

    private void bindView() {
        edRazao.setText(cliente.getRazaoSocial());
        edFantasia.setText(cliente.getNomeFantasia());
        edLogradouro.setText(cliente.getLogradouro());
        edNrLogradouro.setText(cliente.getNrLogradouro());
        edCep.setText(cliente.getCep());
        edBairro.setText(cliente.getBairro());
        edComplemento.setText(cliente.getComplemento());
        edCidade.setText(cliente.getCidade());

        if (cliente.getTpLogradouro() != null) {
            spTipoLogradouro.setSelection(cliente.getTpLogradouro().ordinal());
        }

        if (cliente.getEstado() != null) {
            spEstado.setSelection(cliente.getEstado().ordinal());
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
        cliente.setRazaoSocial(edRazao.getText().toString());
        cliente.setNomeFantasia(edFantasia.getText().toString());
        cliente.setLogradouro(edLogradouro.getText().toString());
        cliente.setNrLogradouro(edNrLogradouro.getText().toString());
        cliente.setCep(edCep.getText().toString());
        cliente.setBairro(edBairro.getText().toString());
        cliente.setComplemento(edComplemento.getText().toString());
        cliente.setCidade(edCidade.getText().toString());
        cliente.setDtCadastro(cliente.getDtCadastro() != null ? cliente.getDtCadastro() : new Date());
        cliente.setEstado((Estado) spEstado.getSelectedItem());
        cliente.setTpLogradouro((TipoLogradouro) spTipoLogradouro.getSelectedItem());
    }

    public class SalvarCliente extends AsyncTask<Integer, Void, String> {

        SQLRepository<Cliente, Long> clienteRepository = AnterosVendasContext.getInstance().getSQLRepository(Cliente.class);
        private ProgressDialog dialog;
        Set<ConstraintViolation<Cliente>> violations;

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

                violations = AnterosVendasContext.getInstance().getDefaultValidator().validate(cliente, ValidacaoCliente.class);
                if (violations.size() > 0) {
                    return "ERRO_VALIDACAO";
                }
                clienteRepository.getTransaction().begin();
                clienteRepository.save(cliente);
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
