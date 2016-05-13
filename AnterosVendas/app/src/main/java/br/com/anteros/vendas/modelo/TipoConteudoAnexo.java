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

/**
 * Created by edson on 09/05/16.
 */
public enum TipoConteudoAnexo {
    IMAGEM, TEXTO, DOCUMENTO, PLANILHA, APRESENTACAO, PDF, HTML, XML, ZIP, RAR, OUTROS;

    public static TipoConteudoAnexo getTipoConteudoAnexoPorExtensao(String extensao) {
        if (extensao != null) {
            extensao = extensao.toUpperCase();
            if (extensao.equals("JPEG") || extensao.equals("JPG") || extensao.equals("GIF") || extensao.equals("BMP")
                    || extensao.equals("PNG"))
                return TipoConteudoAnexo.IMAGEM;
            else if (extensao.equals("TXT") || extensao.equals("ASC"))
                return TipoConteudoAnexo.TEXTO;
            else if (extensao.equals("DOC") || extensao.equals("DOCX") || extensao.equals("ODM")
                    || extensao.equals("ODT") || extensao.equals("OTT"))
                return TipoConteudoAnexo.DOCUMENTO;
            else if (extensao.equals("XLR") || extensao.equals("XLS") || extensao.equals("XLSX")
                    || extensao.equals("ODS") || extensao.equals("OTS"))
                return TipoConteudoAnexo.PLANILHA;
            else if (extensao.equals("ODP") || extensao.equals("OTP") || extensao.equals("PPS")
                    || extensao.equals("PPT") || extensao.equals("PPTX"))
                return TipoConteudoAnexo.APRESENTACAO;
            else if (extensao.equals("PDF"))
                return TipoConteudoAnexo.PDF;
            else if (extensao.equals("HTML"))
                return TipoConteudoAnexo.HTML;
            else if (extensao.equals("XML"))
                return TipoConteudoAnexo.XML;
            else if (extensao.equals("ZIP"))
                return TipoConteudoAnexo.ZIP;
            else if (extensao.equals("RAR"))
                return TipoConteudoAnexo.RAR;
        }
        return TipoConteudoAnexo.OUTROS;
    }
}
