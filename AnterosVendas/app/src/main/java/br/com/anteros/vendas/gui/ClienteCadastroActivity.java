package br.com.anteros.vendas.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.Date;

import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.persistence.transaction.impl.TransactionException;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.EstadoAdapter;
import br.com.anteros.vendas.gui.adapter.TipoLogradouroAdapter;
import br.com.anteros.vendas.modelo.Cliente;
import br.com.anteros.vendas.modelo.Estado;
import br.com.anteros.vendas.modelo.TipoLogradouro;

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
    private ImageView imgSave;
    private ImageView imgCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_cadastro);

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

        imgSave = (ImageView) findViewById(R.id.cliente_cadastro_img_save);
        imgSave.setOnClickListener(this);

        imgCancel = (ImageView) findViewById(R.id.cliente_cadastro_img_cancel);
        imgCancel.setOnClickListener(this);

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
    public void onClick(View v) {
        if (v == imgSave) {
            salvar();
        } else if (v == imgCancel) {
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
    }

    private void salvar() {
        new QuestionAlert(this, this.getResources().getString(
                R.string.app_name), "Deseja salvar o Cliente?",
                new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        SQLRepository<Cliente, Long> clienteRepository = AnterosVendasContext.getInstance().getSQLRepository(Cliente.class);
                        try {
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

                            clienteRepository.getTransaction().begin();
                            clienteRepository.save(cliente);
                            clienteRepository.getTransaction().commit();

                            setResult(RESULT_OK);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                clienteRepository.getTransaction().rollback();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            if (e instanceof TransactionException) {
                                new ErrorAlert(ClienteCadastroActivity.this, "Erro",
                                        "Ocorreu um erro ao salvar o cliente. " + e.getCause()).show();
                            } else {
                                new ErrorAlert(ClienteCadastroActivity.this, "Erro",
                                        "Ocorreu um erro ao salvar o cliente. " + e.getMessage()).show();
                            }
                        }
                    }

                    public void onNegativeClick() {

                    }

                }).show();
    }
}
