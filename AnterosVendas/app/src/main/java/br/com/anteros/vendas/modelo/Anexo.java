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

import br.com.anteros.bean.validation.constraints.Required;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.Enumerated;
import br.com.anteros.persistence.metadata.annotation.ForeignKey;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.Lob;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.TableGenerator;
import br.com.anteros.persistence.metadata.annotation.type.EnumType;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;
import br.com.anteros.validation.api.constraints.Size;
import br.com.anteros.validation.api.groups.Default;

/**
 * Created by edson on 09/05/16.
 */
@Entity
@Table(name = "ANEXO")
public class Anexo implements Serializable {

    /*
 * Identificação do Anexo
 */
    @Id
    @Column(name = "ID_ANEXO", length = 8, label = "ID")
    @GeneratedValue(strategy = GeneratedType.TABLE)
    @TableGenerator(value = "SEQ_ANEXO", name = "SEQUENCIA", initialValue = 1, pkColumnName = "ID_SEQUENCIA", valueColumnName = "NR_SEQUENCIA")
    private Long id;

    /*
     * Nome do Anexo
     */
    @Required(groups = {Default.class, ValidacaoCliente.class})
    @Size(min = 1, max = 100, groups = {Default.class})
    @Column(name = "DS_ANEXO", required = true, label = "Descrição", length = 100)
    private String nome;

    /*
     * Conteúdo do Anexo
     */
    @Lob
    @Column(name = "CONTEUDO", label = "Conteúdo", required = true)

    private byte[] conteudo;

    /*
     * Tipo de Conteúdo do Anexo
     */
    @Size(max = 20, min = 1, groups = {Default.class})
    @Column(name = "TP_CONTEUDO", length = 20, required = true, label = "Tipo do conteúdo")
    @Enumerated(EnumType.STRING)
    @Required(groups = {Default.class, ValidacaoCliente.class})
    private TipoConteudoAnexo tipoConteudo;

    /*
     * Cliente a qual pertence o anexo.
     */
    @ForeignKey
    private Cliente cliente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public TipoConteudoAnexo getTipoConteudo() {
        return tipoConteudo;
    }

    public void setTipoConteudo(TipoConteudoAnexo tipoConteudo) {
        this.tipoConteudo = tipoConteudo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
