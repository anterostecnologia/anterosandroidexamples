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

import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.persistence.transaction.impl.TransactionException;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.AnexoConsultaActivity;
import br.com.anteros.vendas.modelo.Anexo;

/**
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 10/05/16.
 */
public class AnexoConsultaAdapter extends ArrayAdapter<Anexo> {

    public AnexoConsultaAdapter(Context context, List<Anexo> objects) {
        super(context, R.layout.anexo_consulta_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.anexo_consulta_item, null);
        }

        final Anexo item = (Anexo) getItem(position);

        TextView tvIdAnexo = (TextView) convertView.findViewById(R.id.anexo_consulta_item_idAnexo);
        TextView tvNomeAnexo = (TextView) convertView.findViewById(R.id.anexo_consulta_item_nomeAnexo);
        TextView tvTipoAnexo = (TextView) convertView.findViewById(R.id.anexo_consulta_item_tipoAnexo);
        TextView tvDescricao = (TextView) convertView.findViewById(R.id.anexo_consulta_item_descricao);
        ImageView imgVisualizar = (ImageView) convertView.findViewById(R.id.anexo_consulta_item_imgVisualizar);
        ImageView imgDelete = (ImageView) convertView.findViewById(R.id.anexo_consulta_item_imgRemover);
        ImageView imgIcone = (ImageView) convertView.findViewById(R.id.anexo_consulta_item_imgIcon);


        imgDelete.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                removerAnexo(item);
            }
        });

        imgVisualizar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                abrirAnexo(item);
            }
        });

        if (item != null) {
            tvIdAnexo.setText(item.getId() + "");
            tvNomeAnexo.setText(item.getNome());
            tvDescricao.setText(item.getNome());

            if (item.getTipoConteudo() != null) {
                tvTipoAnexo.setText(item.getTipoConteudo().name());

                switch (item.getTipoConteudo()) {
                    case IMAGEM:
                        imgIcone.setImageResource(R.drawable.ic_file_extension_image);
                        break;
                    case PDF:
                        imgIcone.setImageResource(R.drawable.ic_file_extension_pdf);
                        break;
                    case PLANILHA:
                        imgIcone.setImageResource(R.drawable.ic_file_extension_xls);
                        break;
                    case TEXTO:
                        imgIcone.setImageResource(R.drawable.ic_file_extension_txt);
                        break;
                    case DOCUMENTO:
                        imgIcone.setImageResource(R.drawable.ic_file_extension_doc);
                        break;
                    case APRESENTACAO:
                        imgIcone.setImageResource(R.drawable.ic_file_extension_ppt);
                        break;
                    case HTML:
                        imgIcone.setImageResource(R.drawable.ic_file_extension_html);
                        break;
                    case RAR:
                        imgIcone.setImageResource(R.drawable.ic_file_extension_rar);
                        break;
                    case ZIP:
                        imgIcone.setImageResource(R.drawable.ic_file_extension_zip);
                        break;
                    case XML:
                        imgIcone.setImageResource(R.drawable.ic_file_extension_xml);
                        break;
                    default:
                        imgIcone.setImageResource(R.drawable.ic_file_extension_unk);
                        break;
                }
            }
        }

        return convertView;
    }

    protected void removerAnexo(final Anexo anexo) {
        new QuestionAlert(getContext(), getContext().getString(
                R.string.app_name), "Remover Anexo ?",
                new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        try {
                            remove(anexo);
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

    private void abrirAnexo(Anexo anexo) {
        File file = new File(anexo.getConteudoPath());
        Uri uri = Uri.fromFile(file);
        String extension = anexo.getNome().substring(anexo.getNome().lastIndexOf(".") + 1);

        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().getApplicationContext().startActivity(intent);
    }
}
