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

import br.com.anteros.bean.validation.constraints.Required;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.Lob;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.TableGenerator;
import br.com.anteros.persistence.metadata.annotation.Transient;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;
import br.com.anteros.validation.api.groups.Default;

/**
 * Created by edson on 09/05/16.
 */
@Entity
@Table(name = "PRODUTO")
public class Produto implements Serializable, Parcelable {
    /*
     * Id do Produto
     */
    @Id
    @GeneratedValue(strategy = GeneratedType.TABLE)
    @TableGenerator(value = "SEQ_PRODUTO", name = "SEQUENCIA", initialValue = 1, pkColumnName = "ID_SEQUENCIA", valueColumnName = "NR_SEQUENCIA")
    @Column(name = "ID_PRODUTO", required = true, length = 8, label = "Id.produto")
    private Long id;

    /*
     * Nome do produto
     */
    @Required(groups = {Default.class, ValidacaoCliente.class})
    @Column(name = "DS_PRODUTO", required = true, length = 50, label="Nome do produto")
    private String nomeProduto;

    /*
     * Foto do produto
     */
    @Lob
    @Column(name = "FOTO_PRODUTO", required=true, label = "Foto do produto")
    private byte[] fotoProduto;

    /*
     * Valor do produto
     */
    @Required(groups = {Default.class, ValidacaoCliente.class})
    @Column(name = "VL_PRODUTO", required = true, precision = 14, scale = 2, defaultValue = "0", label="Valor do produto")
    private BigDecimal vlProduto;

    @Transient
    private Boolean selected=false;

    public Produto(){

    }

    protected Produto(Parcel in) {
        id = in.readLong();
        nomeProduto = in.readString();
        fotoProduto = in.createByteArray();
        vlProduto = new BigDecimal(in.readString());
    }

    public static final Creator<Produto> CREATOR = new Creator<Produto>() {
        @Override
        public Produto createFromParcel(Parcel in) {
            return new Produto(in);
        }

        @Override
        public Produto[] newArray(int size) {
            return new Produto[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public byte[] getFotoProduto() {
        return fotoProduto;
    }

    public void setFotoProduto(byte[] fotoProduto) {
        this.fotoProduto = fotoProduto;
    }

    public BigDecimal getVlProduto() {
        return vlProduto;
    }

    public void setVlProduto(BigDecimal vlProduto) {
        this.vlProduto = vlProduto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(nomeProduto);
        dest.writeByteArray(fotoProduto);
        dest.writeString(vlProduto.toString());
    }

    public Boolean isSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
