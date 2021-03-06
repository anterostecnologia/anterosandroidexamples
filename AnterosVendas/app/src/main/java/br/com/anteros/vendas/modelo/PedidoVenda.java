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

package br.com.anteros.vendas.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.anteros.bean.validation.constraints.Required;
import br.com.anteros.persistence.metadata.annotation.Cascade;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.Enumerated;
import br.com.anteros.persistence.metadata.annotation.Fetch;
import br.com.anteros.persistence.metadata.annotation.ForeignKey;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.TableGenerator;
import br.com.anteros.persistence.metadata.annotation.Temporal;
import br.com.anteros.persistence.metadata.annotation.type.CascadeType;
import br.com.anteros.persistence.metadata.annotation.type.EnumType;
import br.com.anteros.persistence.metadata.annotation.type.FetchMode;
import br.com.anteros.persistence.metadata.annotation.type.FetchType;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;
import br.com.anteros.persistence.metadata.annotation.type.TemporalType;
import br.com.anteros.validation.api.constraints.Past;
import br.com.anteros.validation.api.groups.Default;

/**
 * @author Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 09/05/16.
 */
@Entity
@Table(name = "PEDIDOVENDA")
public class PedidoVenda implements Serializable {

    /*
     * Id do Pedido de venda
     */
    @Id
    @GeneratedValue(strategy = GeneratedType.TABLE)
    @TableGenerator(value = "SEQ_PEDIDOVENDA", name = "SEQUENCIA", initialValue = 1, pkColumnName = "ID_SEQUENCIA", valueColumnName = "NR_SEQUENCIA")
    @Column(name = "ID_PEDIDOVENDA", required = true, length = 8)
    private Long id;

    /*
     * Número do pedido
     */
    @Required(groups = {Default.class, ValidacaoPadrao.class})
    @Column(name = "NR_PEDIDO", length = 8, required = true, label = "Nr.pedido")
    private Long nrPedido;

    /*
     * Data do pedido
     */
    @Required(groups = {Default.class, ValidacaoPadrao.class})
    @Temporal(TemporalType.DATE)
    @Past
    @Column(name = "DT_PEDIDO", required = true, label = "Data do pedido")
    private Date dtPedido;

    /*
     * Cliente a qual pertence o pedido
     */
    @Required(groups = {Default.class, ValidacaoPadrao.class})
    @ForeignKey
    @Column(name = "ID_CLIENTE", required = true, label = "Cliente")
    private Cliente cliente;

    /*
     * Valor total do pedido
     */
    @Required(groups = {Default.class, ValidacaoPadrao.class})
    @Column(name = "VL_TOTAL_PEDIDO", required = true, defaultValue = "0", precision = 14, scale = 2, label = "Valor total do pedido")
    private BigDecimal vlTotalPedido;

    /*
     * Condição de pagamento
     */
    @Required(groups = {Default.class, ValidacaoPadrao.class})
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_CONDICAO_PGTO", length = 20, required = true, label = "Tipo de condição de pagamento")
    private CondicaoPagamento condicaoPagamento;

    /*
     * Forma de pagamento
     */
    @Required(groups = {Default.class, ValidacaoPadrao.class})
    @Enumerated
    @Column(name = "FORMA_PAGTO", required = true, length = 20, label = "Forma de pagamento")
    private FormaPagamento formaPagamento;

    /*
     * Itens do pedido de venda
     */
    @Required(groups = {Default.class, ValidacaoPadrao.class})
    @Fetch(type = FetchType.LAZY, mode = FetchMode.ONE_TO_MANY, mappedBy = "pedidoVenda")
    @Cascade(values = {CascadeType.ALL})
    private List<ItemPedido> itens;

    public PedidoVenda() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNrPedido() {
        return nrPedido;
    }

    public void setNrPedido(Long nrPedido) {
        this.nrPedido = nrPedido;
    }

    public Date getDtPedido() {
        return dtPedido;
    }

    public void setDtPedido(Date dtPedido) {
        this.dtPedido = dtPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public BigDecimal getVlTotalPedido() {
        return vlTotalPedido;
    }

    public String getVlTotalPedidoAsString() {
        return formatMoeda(getVlTotalPedido());
    }

    public void setVlTotalPedido(BigDecimal vlTotalPedido) {
        this.vlTotalPedido = vlTotalPedido;
    }

    public CondicaoPagamento getCondicaoPagamento() {
        return condicaoPagamento;
    }

    public void setCondicaoPagamento(CondicaoPagamento condicaoPagamento) {
        this.condicaoPagamento = condicaoPagamento;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public List<ItemPedido> getItens() {
        if (itens == null)
            itens = new ArrayList<ItemPedido>();
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public static String formatMoeda(BigDecimal valor) {
        Locale ptBr;
        ptBr = new Locale("pt", "BR");

        DecimalFormat moedaFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(ptBr);
        moedaFormat.setNegativePrefix("-");
        moedaFormat.setNegativeSuffix("");

        if (valor == null) {
            valor = BigDecimal.ZERO;
        }
        return moedaFormat.format(valor).replace("R$", "");
    }
}
