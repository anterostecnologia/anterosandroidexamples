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

package br.com.anteros.vendas;

import android.app.Application;
import android.util.Log;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.anteros.android.persistence.session.AndroidSQLConfiguration;
import br.com.anteros.android.persistence.session.AndroidSQLSessionFactory;
import br.com.anteros.android.persistence.sql.jdbc.SQLiteConnection;
import br.com.anteros.persistence.schema.type.TableCreationType;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.persistence.session.configuration.AnterosPersistenceProperties;
import br.com.anteros.persistence.session.query.TypedSQLQuery;
import br.com.anteros.persistence.session.repository.AbstractSQLRepositoryFactory;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.validation.api.Validation;
import br.com.anteros.validation.api.Validator;
import br.com.anteros.vendas.modelo.Anexo;
import br.com.anteros.vendas.modelo.Cliente;
import br.com.anteros.vendas.modelo.CondicaoPagamento;
import br.com.anteros.vendas.modelo.Estado;
import br.com.anteros.vendas.modelo.FormaPagamento;
import br.com.anteros.vendas.modelo.ItemPedido;
import br.com.anteros.vendas.modelo.PedidoVenda;
import br.com.anteros.vendas.modelo.Produto;
import br.com.anteros.vendas.modelo.TipoLogradouro;

/**
 * Created by edson on 09/05/16.
 */
public class AnterosVendasContext {

    private static Application application;
    private static AnterosVendasContext appContext;

    private SQLSessionFactory sessionFactory;
    private SQLSession session;
    private Validator validator;

    public static AnterosVendasContext getInstance() {
        try {
            boolean criouSessao = false;
            if (application == null) {
                throw new RuntimeException("Parâmetro application não pode ser nulo na classe AnterosVendasContext.");
            }
            if (appContext == null) {
                appContext = new AnterosVendasContext();
                Log.d("appContext", "Criou appContext");
            }
            if (appContext.getSessionFactory() == null) {
                appContext.setSessionFactory(createSessionFactory(application));
                Log.d("appContext", "Criou sessionFactory");
            }
            if (appContext.getSession() == null || appContext.getSession().isClosed()) {
                appContext.setSession(appContext.getSessionFactory().getCurrentSession());
                criouSessao = true;
                Log.d("appContext", "Criou session");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return appContext;
    }

    public static SQLSessionFactory createSessionFactory(Application context) throws Exception {
        return new AndroidSQLConfiguration().context(context.getApplicationContext())
                .addAnnotatedClass(Anexo.class)
                .addAnnotatedClass(Cliente.class)
                .addAnnotatedClass(CondicaoPagamento.class)
                .addAnnotatedClass(ItemPedido.class)
                .addAnnotatedClass(PedidoVenda.class)
                .addAnnotatedClass(Produto.class)
                .addProperty(AnterosPersistenceProperties.JDBC_DRIVER, "br.com.anteros.android.persistence.sql.jdbc.SQLiteDriver")
                .addProperty(AnterosPersistenceProperties.JDBC_URL, "jdbc:anteros:vendas.db")
                .addProperty(AnterosPersistenceProperties.JDBC_USER, "")
                .addProperty(AnterosPersistenceProperties.JDBC_PASSWORD, "")
                .addProperty(AnterosPersistenceProperties.DIALECT, "br.com.anteros.android.persistence.sql.dialect.AndroidSQLiteDialect")
                .addProperty(AnterosPersistenceProperties.SHOW_SQL, "true")
                .addProperty(AnterosPersistenceProperties.FORMAT_SQL, "true")
                .addProperty(AnterosPersistenceProperties.CREATE_REFERENCIAL_INTEGRITY, "true")
                .addProperty(AnterosPersistenceProperties.DATABASE_DDL_GENERATION, AnterosPersistenceProperties.CREATE_OR_EXTEND)
                .addProperty(AnterosPersistenceProperties.SCRIPT_DDL_GENERATION, AnterosPersistenceProperties.NONE)
                .addProperty(AnterosPersistenceProperties.DDL_OUTPUT_MODE, AnterosPersistenceProperties.DDL_DATABASE_OUTPUT)
                .addProperty(AnterosPersistenceProperties.APPLICATION_LOCATION, "/mnt/sdcard/backup").buildSessionFactory();
    }

    public static void setApplication(Application application) {
        AnterosVendasContext.application = application;
    }

    public SQLSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SQLSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SQLSession getSession() {
        return session;
    }

    public void setSession(SQLSession session) {
        this.session = session;
    }

    public String getAbsolutPathDb() {
        return application.getDatabasePath(getDatabaseName()).getAbsolutePath();
    }

    public Validator getDefaultValidator() {
        if (validator == null)
            validator = Validation.buildDefaultValidatorFactory().getValidator();

        return validator;
    }

    public String getDatabaseName() {
        SQLiteConnection connection;
        try {
            connection = (SQLiteConnection) getSession().getConnection();
            return connection.getDatbaseName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void recriarBancoDados() throws Exception {
        ((AndroidSQLSessionFactory) getSessionFactory()).generateDDL(TableCreationType.DROP,
                TableCreationType.NONE, false);
    }

    public String getApplicationName() {
        return application.getString(R.string.app_name);
    }

    public AbstractSQLRepositoryFactory getSQLRepositoryFactory() {
        return AbstractSQLRepositoryFactory.getInstance();
    }

    public <T, ID extends Serializable> SQLRepository<T, ID> getSQLRepository(Class<T> clazz) {
        SQLRepository<T, ID> repository = getSQLRepositoryFactory().getRepository(getSession(), clazz);
        return repository;
    }

    public void populateDatabase() {
        try {
            getSession().getTransaction().begin();

            Cliente cliente1 = new Cliente();
            cliente1.setRazaoSocial("JOAO DA SILVA E CIA LTDA");
            cliente1.setNomeFantasia("JOAO DA SILVA");
            cliente1.setTpLogradouro(TipoLogradouro.AVENIDA);
            cliente1.setNrLogradouro("240");
            cliente1.setLogradouro("PRESIDENTE VARGAS");
            cliente1.setBairro("CENTRO");
            cliente1.setComplemento("");
            cliente1.setCep("87300000");
            cliente1.setCidade("CAMPO MOURAO");
            cliente1.setEstado(Estado.PR);
            cliente1.setDtCadastro(new Date());

            getSession().save(cliente1);

            TypedSQLQuery<Cliente> query = getSession().createQuery("SELECT * FROM CLIENTE", Cliente.class);
            List<Cliente> clientes = query.getResultList();
            for (Cliente cliente : clientes) {
                Produto p = new Produto();
                p.setVlProduto(new BigDecimal(750));
                p.setNomeProduto("Monitor LG 22");

                getSession().save(p);

                PedidoVenda ped = new PedidoVenda();
                ped.setVlTotalPedido(new BigDecimal(750));
                ped.setNrPedido(new Long(12345));
                ped.setFormaPagamento(FormaPagamento.BOLETO);
                ped.setCondicaoPagamento(CondicaoPagamento.A_VISTA);
                ped.setCliente(cliente);
                ped.setDtPedido(new Date());

                ItemPedido ite = new ItemPedido();
                ite.setVlTotal(new BigDecimal(750));
                ite.setVlProduto(new BigDecimal(750));
                ite.setQtProduto(new BigDecimal(1));
                ite.setPedidoVenda(ped);
                ite.setProduto(p);

                List<ItemPedido> itens = new ArrayList<ItemPedido>();
                itens.add(ite);

                ped.setItens(itens);

                getSession().save(ped);
                getSession().save(ite);
            }
            getSession().getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
