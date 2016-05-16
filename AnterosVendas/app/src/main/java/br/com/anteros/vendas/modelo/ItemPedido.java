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

import android.content.ClipData;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import br.com.anteros.bean.validation.constraints.Required;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.ForeignKey;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.TableGenerator;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;
import br.com.anteros.validation.api.groups.Default;

/**
 * @author Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 09/05/16.
 */
@Entity
@Table(name = "PEDIDO_ITEM")
public class ItemPedido implements Serializable, Parcelable {

    /*
   * Id do Item do pedido
   */
    @Id
    @GeneratedValue(strategy = GeneratedType.TABLE)
    @TableGenerator(value = "SEQ_ITEM", name = "SEQUENCIA", initialValue = 1, pkColumnName = "ID_SEQUENCIA", valueColumnName = "NR_SEQUENCIA")
    @Column(name = "ID_ITEM", required = true, length = 8)
    private Long id;

    @Required(groups = {Default.class, ValidacaoCliente.class})
    @ForeignKey
    private PedidoVenda pedidoVenda;

    @Required(groups = {Default.class, ValidacaoCliente.class})
    @ForeignKey
    private Produto produto;

    @Required(groups = {Default.class, ValidacaoCliente.class})
    @Column(name = "QT_PRODUTO", precision = 11, scale = 3, defaultValue = "0", label = "Quantidade do produto")
    private BigDecimal qtProduto;

    @Required(groups = {Default.class, ValidacaoCliente.class})
    @Column(name = "VL_PRODUTO", precision = 14, scale = 2, defaultValue = "0", label = "Valor do produto")
    private BigDecimal vlProduto;

    @Required(groups = {Default.class, ValidacaoCliente.class})
    @Column(name = "VL_TOTAL", precision = 14, scale = 2, defaultValue = "0", label = "Valor total")
    private BigDecimal vlTotal;

    public ItemPedido(){

    }

    protected ItemPedido(Parcel in) {
        id = in.readLong();
        pedidoVenda = in.readParcelable(PedidoVenda.class.getClassLoader());
        produto = in.readParcelable(Produto.class.getClassLoader());
        qtProduto = new BigDecimal(in.readString());
        vlProduto = new BigDecimal(in.readString());
        vlTotal = new BigDecimal(in.readString());
    }

    public static final Creator<ItemPedido> CREATOR = new Creator<ItemPedido>() {
        @Override
        public ItemPedido createFromParcel(Parcel in) {
            return new ItemPedido(in);
        }

        @Override
        public ItemPedido[] newArray(int size) {
            return new ItemPedido[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PedidoVenda getPedidoVenda() {
        return pedidoVenda;
    }

    public void setPedidoVenda(PedidoVenda pedidoVenda) {
        this.pedidoVenda = pedidoVenda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getQtProduto() {
        return qtProduto;
    }

    public void setQtProduto(BigDecimal qtProduto) {
        this.qtProduto = qtProduto;
    }

    public BigDecimal getVlProduto() {
        return vlProduto;
    }

    public String getVlProdutoAsString() {
        return formatMoeda(getVlProduto());
    }

    public void setVlProduto(BigDecimal vlProduto) {
        this.vlProduto = vlProduto;
    }

    public BigDecimal getVlTotal() {
        return vlTotal;
    }

    public String getVlTotalAsString() {
        return formatMoeda(getVlTotal());
    }

    public void setVlTotal(BigDecimal vlTotal) {
        this.vlTotal = vlTotal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(pedidoVenda, flags);
        dest.writeParcelable(produto, flags);
        dest.writeString(qtProduto.toString());
        dest.writeString(vlProduto.toString());
        dest.writeString(vlTotal.toString());
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
