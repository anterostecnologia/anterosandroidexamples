package br.com.anteros.vendas.gui.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.persistence.transaction.impl.TransactionException;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.Anexo;
import br.com.anteros.vendas.modelo.TipoConteudoAnexo;

/**
 * Created by eduardogreco on 5/10/16.
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
                delete(item);
            }
        });

        imgVisualizar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                openAnexo(item);
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

    protected void delete(final Anexo anexo) {
        new QuestionAlert(getContext(), getContext().getString(
                R.string.app_name), "Remover Anexo ?",
                new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        SQLRepository<Anexo, Long> anexoRepository = AnterosVendasContext.getInstance().getSQLRepository(Anexo.class);

                        try {
                            if (anexo.getId() != null) {


                                Anexo an = anexoRepository.findOne(
                                        "SELECT A.* FROM ANEXO A WHERE A.ID_ANEXO = :PID_ANEXO",
                                        new NamedParameter("PID_ANEXO", anexo.getId()));

                                anexoRepository.getTransaction().begin();
                                anexoRepository.remove(an);
                                anexoRepository.getTransaction().commit();
                            }
                            remove(anexo);
                            notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                anexoRepository.getTransaction().rollback();
                            } catch (Exception e1) {
                            }
                            if (e instanceof TransactionException) {
                                new ErrorAlert(getContext(), getContext().getResources().getString(
                                        R.string.app_name), "Ocorreu um erro ao remover Anexo. " + e.getCause()).show();
                            } else {
                                new ErrorAlert(getContext(), getContext().getResources().getString(
                                        R.string.app_name), "Ocorreu um erro ao remover Anexo. " + e.getMessage()).show();
                            }
                        }
                    }

                    public void onNegativeClick() {

                    }
                }).show();

    }

    private void openAnexo(Anexo anexo) {
        try {

            Intent intent = new Intent(Intent.ACTION_VIEW);

            if (anexo.getTipoConteudo() == TipoConteudoAnexo.IMAGEM)
                if ((anexo.getNome().substring(anexo.getNome().lastIndexOf(".") + 1).equals(anexo.getNome())))
                    anexo.setNome(anexo.getNome() + ".png");

            MimeTypeMap mime = MimeTypeMap.getSingleton();

            String extension = anexo.getNome().substring(anexo.getNome().lastIndexOf(".") + 1);
            String type = mime.getMimeTypeFromExtension(extension);

            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().getApplicationContext().startActivity(intent);

        } catch (ActivityNotFoundException e) {
            new ErrorAlert(getContext(), getContext().getResources().getString(R.string.app_name),
                    "Não foi encontrado nenhum aplicativo nesse aparelho que suporte abrir a extensão '"
                            + anexo.getNome().substring(anexo.getNome().lastIndexOf("."))
                            + "', entre em contato com a equipe de Suporte para resolver esse problema.").show();
        } catch (Exception e) {
            new ErrorAlert(getContext(), getContext().getResources().getString(R.string.app_name),
                    "Não foi possível abrir o anexo " + anexo.getId()
                            + ". " + e.getMessage()).show();
            e.printStackTrace();
        }
    }
}
