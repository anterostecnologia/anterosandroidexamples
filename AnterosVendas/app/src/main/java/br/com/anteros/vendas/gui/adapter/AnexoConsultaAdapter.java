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

package br.com.anteros.vendas.gui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import br.com.anteros.android.core.util.AndroidFileUtils;
import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.persistence.transaction.impl.TransactionException;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.AnexoConsultaActivity;
import br.com.anteros.vendas.modelo.Anexo;
import br.com.anteros.vendas.modelo.TipoConteudoAnexo;

/**
 *  Adapter responsável por apresentar a consulta de anexos.
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 10/05/16.
 */
public class AnexoConsultaAdapter extends ArrayAdapter<Anexo> {

    public AnexoConsultaAdapter(Context context, List<Anexo> objects) {
        super(context, R.layout.anexo_consulta_item, objects);
    }

    /**
     * Retorna a view para apresentação na lista
     * @param position Posição dentro da view
     * @param convertView View
     * @param parent View pai
     * @return View criada
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /**
         * Cria a view com o layout da consulta de anexo
         */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.anexo_consulta_item, null);
        }

        /**
         * Obtém o anexo de acordo com a posição
         */
        final Anexo item = (Anexo) getItem(position);

        /**
         * Obtém os campos dentro do layout
         */
        TextView tvIdAnexo = (TextView) convertView.findViewById(R.id.anexo_consulta_item_idAnexo);
        TextView tvNomeAnexo = (TextView) convertView.findViewById(R.id.anexo_consulta_item_nomeAnexo);
        TextView tvTipoAnexo = (TextView) convertView.findViewById(R.id.anexo_consulta_item_tipoAnexo);
        TextView tvDescricao = (TextView) convertView.findViewById(R.id.anexo_consulta_item_descricao);
        ImageView imgVisualizar = (ImageView) convertView.findViewById(R.id.anexo_consulta_item_imgVisualizar);
        ImageView imgDelete = (ImageView) convertView.findViewById(R.id.anexo_consulta_item_imgRemover);
        ImageView imgIcone = (ImageView) convertView.findViewById(R.id.anexo_consulta_item_imgIcon);


        /**
         * Atribui o evento de click na imagem removerCliente
         */
        imgDelete.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                removerAnexo(item);
            }
        });

        /**
         * Atribui o evento de click na imagem visualizar
         */
        imgVisualizar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                abrirAnexo(item);
            }
        });

        /**
         * Se o item não for nulo atribui os valores nos campos da view
         */
        if (item != null) {
            tvIdAnexo.setText(item.getId() + "");
            if (StringUtils.isNotEmpty(item.getConteudoPath())){
                File file = new File(item.getConteudoPath());
                tvNomeAnexo.setText(file.getName());
            } else {
                tvNomeAnexo.setText("");
            }


            tvDescricao.setText(item.getNome());

            /**
             * Atribui a imagem de acordo com o conteúdo
             */
            if (item.getTipoConteudo() != null) {
                tvTipoAnexo.setText(item.getTipoConteudo().name());
                imgIcone.setImageResource(item.getTipoConteudo().getResourcePorTipoConteudo());
            }
        }

        return convertView;
    }

    /**
     * Remove o anexo passado como parâmetro
     * @param anexo Anexo
     */
    protected void removerAnexo(final Anexo anexo) {
        new QuestionAlert(getContext(), getContext().getString(
                R.string.app_name), "Remover Anexo ?",
                new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        try {
                            remove(anexo);
                            /**
                             * Notifica o adapter que houve alteração na lista
                             */
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                            new ErrorAlert(getContext(), getContext().getResources().getString(
                                    R.string.app_name), "Ocorreu um erro ao remover Anexo. " + e.getMessage()).show();
                        }
                    }

                    public void onNegativeClick() {

                    }
                }).show();

    }


    /**
     * Abre o anexo para visualizção
     * @param anexo Anexo
     */
    private void abrirAnexo(Anexo anexo) {
        /**
         * Obtém o arquivo correspondente a url armazenada
         */
        File file = new File(anexo.getConteudoPath());
        Uri uri = Uri.fromFile(file);
        /**
         * Obtém a extensão do arquivo
         */
        String extension = AndroidFileUtils.getExtension(uri.toString());
        if (extension.contains(".")){
            extension = extension.replace(".","");
        }

        /**
         * Cria um MIME type correspondente a extensão para passar para Intent como parâmetro
         * para que possa filtrar as possíveis activities que podem abrir o arquivo
         */
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        /**
         * Inicia a activity com a intenção.
         */
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().getApplicationContext().startActivity(intent);
    }
}
