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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.validation.groups.Default;

import br.com.anteros.bean.validation.constraints.Required;
import br.com.anteros.persistence.metadata.annotation.Cascade;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.EnumValues;
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

/**
 * Created by edson on 09/05/16.
 */
@Entity
@Table(name="PEDIDOVENDA")
public class PedidoVenda implements Serializable {

    /*
     * Id do Pedido de venda
     */
    @Id
    @GeneratedValue(strategy = GeneratedType.TABLE)
    @TableGenerator(value= "SEQ_PEDIDOVENDA", name = "SEQUENCIA", initialValue= 1, pkColumnName = "ID_SEQUENCIA", valueColumnName = "NR_SEQUENCIA")
    @Column(name="ID_PEDIDOVENDA", required = true, length = 8)
    private Long id;

    /*
     * Número do pedido
     */
    @Required(groups = { Default.class, ValidacaoCliente.class })
    @Column(name="NR_PEDIDO", length = 8, required = true, label = "Nr.pedido")
    private Long nrPedido;

    /*
     * Data do pedido
     */
    @Required(groups = { Default.class, ValidacaoCliente.class })
    @Temporal(TemporalType.DATE)
    @Column(name="DT_PEDIDO", required = true, label = "Data do pedido")
    private Date dtPedido;

    /*
     * Cliente a qual pertence o pedido
     */
    @Required(groups = { Default.class, ValidacaoCliente.class })
    @ForeignKey
    private Cliente cliente;

    /*
     * Valor total do pedido
     */
    @Required(groups = { Default.class, ValidacaoCliente.class })
    @Column(name="VL_TOTAL_PEDIDO", required = true, defaultValue = "0", precision = 14, scale = 2, label="Valor total do pedido")
    private BigDecimal vlTotalPedido;

    /*
     * Condição de pagamento
     */
    @Required(groups = { Default.class, ValidacaoCliente.class })
    @Enumerated(EnumType.STRING)
    @Column(name="TP_CONDICAO_PGTO", length = 20, required = true, label = "Tipo de condição de pagamento")
    private CondicaoPagamento condicaoPagamento;

    /*
     * Forma de pagamento
     */
    @Required(groups = { Default.class, ValidacaoCliente.class })
    @Enumerated
    @Column(name="FORMA_PAGTO", required = true, length = 20)
    private FormaPagamento formaPagamento;

    /*
     * Itens do pedido de venda
     */
    @Required(groups = { Default.class, ValidacaoCliente.class })
    @Fetch(type = FetchType.LAZY, mode = FetchMode.ONE_TO_MANY, mappedBy = "pedidoVenda")
    @Cascade(values = { CascadeType.DELETE_ORPHAN })
    private Set<ItemPedido> itens;

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

    public Set<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(Set<ItemPedido> itens) {
        this.itens = itens;
    }
}
