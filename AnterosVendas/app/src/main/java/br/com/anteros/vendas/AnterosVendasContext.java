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
import android.graphics.Bitmap;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
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
import br.com.anteros.persistence.session.query.SQLQuery;
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
 * @author Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 09/05/16.
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

    public void adicionaProdutos() {

        try {
            getSession().getTransaction().begin();
            SQLQuery query = getSession().createQuery("select count(*) as quant from produto");
            ResultSet resultSet = query.executeQuery();
            getSession().getTransaction().commit();
            if (resultSet.next()){
                if ((int)resultSet.getObject(1)==0){
                    new DownloadImagesTask() {
                        @Override
                        protected void onPostExecute(List<byte[]> bitmaps) {

                            try {
                                getSession().getTransaction().begin();
                                Produto notebookDell1 = new Produto();
                                notebookDell1.setNomeProduto("Notebook Dell 1");
                                notebookDell1.setFotoProduto(bitmaps.get(0));
                                notebookDell1.setVlProduto(new BigDecimal(2500));

                                Produto notebookDell2 = new Produto();
                                notebookDell2.setNomeProduto("Notebook Dell 2");
                                notebookDell2.setFotoProduto(bitmaps.get(1));
                                notebookDell2.setVlProduto(new BigDecimal(3000));

                                Produto impressoraHp1 = new Produto();
                                impressoraHp1.setNomeProduto("Impressora HP Officejet 7110");
                                impressoraHp1.setFotoProduto(bitmaps.get(2));
                                impressoraHp1.setVlProduto(new BigDecimal(800));

                                Produto impressoraHp2 = new Produto();
                                impressoraHp2.setNomeProduto("Multifuncional HP LaserJet Pro");
                                impressoraHp2.setFotoProduto(bitmaps.get(3));
                                impressoraHp2.setVlProduto(new BigDecimal(1000));

                                Produto celularS6 = new Produto();
                                celularS6.setNomeProduto("Celular Samsung Galaxy S6");
                                celularS6.setFotoProduto(bitmaps.get(4));
                                celularS6.setVlProduto(new BigDecimal(3000));

                                Produto celularMotoG = new Produto();
                                celularMotoG.setNomeProduto("Celular Moto G");
                                celularMotoG.setFotoProduto(bitmaps.get(5));
                                celularMotoG.setVlProduto(new BigDecimal(2800));

                                Produto relogioMoto360 = new Produto();
                                relogioMoto360.setNomeProduto("Relógio Moto 360");
                                relogioMoto360.setFotoProduto(bitmaps.get(6));
                                relogioMoto360.setVlProduto(new BigDecimal(1500));

                                Produto iphone6 = new Produto();
                                iphone6.setNomeProduto("Celular Iphone 6");
                                iphone6.setFotoProduto(bitmaps.get(7));
                                iphone6.setVlProduto(new BigDecimal(4000));

                                Produto relogioApple = new Produto();
                                relogioApple.setNomeProduto("Relógio Apple Watch");
                                relogioApple.setFotoProduto(bitmaps.get(8));
                                relogioApple.setVlProduto(new BigDecimal(4000));

                                getSession().save(notebookDell1);
                                getSession().save(notebookDell2);
                                getSession().save(impressoraHp1);
                                getSession().save(impressoraHp2);
                                getSession().save(celularS6);
                                getSession().save(celularMotoG);
                                getSession().save(relogioMoto360);
                                getSession().save(iphone6);
                                getSession().save(relogioApple);

                                getSession().getTransaction().commit();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }.execute(
                            "http://i.dell.com/sites/imagecontent/consumer/merchandizing/en/PublishingImages/Franchise-category/inspiron_polaris_sub_cat_franchise_laptops_mod-02d.jpg",
                            "http://scene7-cdn.dell.com/is/image/DellComputer/laptop-inspiron-15-5547-t-love-mixed-set-video?hei=200&wid=565",
                            "http://imagens.lojahp.com.br/Empresa/Impressora-Empresa/2177255/5349591/Impressora-HP-Officejet-7110-Wide-Format-ePrinter-Preta-2177255.jpg",
                            "http://imagens.lojahp.com.br/Empresa/Impressora-Empresa/2843830/5356309/Multifuncional-HP-LaserJet-Pro-MFP-M127fn-com-ePrint-Impressora-Copiadora-Scanner-e-Fax-2843830.jpg",
                            "http://images.samsung.com/is/image/samsung/br_SM-G928GZKAZTO_001_Front_black_10049808900909?$DT-Gallery$",
                            "http://www.motorola.com.br/sites/default/files/styles/homepage_feature_product_1x/public/library/br/homepage/features/motog_bundles_pretoazul-5.png?itok=AQq2Y-MV&timestamp=1462809447",
                            "http://www.motorola.com.br/sites/default/files/styles/homepage_feature_product_1x/public/library/br/homepage/feat-moto-360-gen1.png?itok=LgCtITqa&timestamp=1462809454",
                            "http://store.storeimages.cdn-apple.com/4973/as-images.apple.com/is/image/AppleInc/aos/published/images/i/ph/iphone6/plus/iphone6-plus-box-silver-2014_GEO_US?wid=478&hei=595&fmt=jpeg&qlt=95&op_sharpen=0&resMode=bicub&op_usm=0.5,0.5,0,0&iccEmbed=0&layer=comp&.v=J8El53",
                            "http://store.storeimages.cdn-apple.com/4973/as-images.apple.com/is/image/AppleInc/aos/published/images/s/38/s38sg/sbbk/s38sg-sbbk-sel-201603?wid=332&hei=392&fmt=jpeg&qlt=95&op_sharpen=0&resMode=bicub&op_usm=0.5,0.5,0,0&iccEmbed=0&layer=comp&.v=Kv3l50"
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }




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

            getSession().getTransaction().commit();

            getSession().getTransaction().begin();

            TypedSQLQuery<Cliente> query = getSession().createQuery("SELECT * FROM CLIENTE", Cliente.class);
            List<Cliente> clientes = query.getResultList();
            for (Cliente cliente : clientes) {
                PedidoVenda ped = new PedidoVenda();
                ped.setVlTotalPedido(new BigDecimal(750));
                ped.setNrPedido(new Long(12345));
                ped.setFormaPagamento(FormaPagamento.BOLETO);
                ped.setCondicaoPagamento(CondicaoPagamento.A_VISTA);
                ped.setCliente(cliente);
                ped.setDtPedido(new Date());

                List<ItemPedido> itens = new ArrayList<ItemPedido>();

                TypedSQLQuery<Produto> queryProdutos = getSession().createQuery("SELECT * FROM PRODUTO", Produto.class);
                List<Produto> produtos = queryProdutos.getResultList();
                for (Produto produto : produtos) {
                    ItemPedido ite = new ItemPedido();
                    ite.setVlTotal(produto.getVlProduto());
                    ite.setVlProduto(produto.getVlProduto());
                    ite.setQtProduto(new BigDecimal(1));
                    ite.setPedidoVenda(ped);
                    ite.setProduto(produto);
                    itens.add(ite);
                }
                ped.setItens(itens);
                getSession().save(ped);
            }
            getSession().getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
