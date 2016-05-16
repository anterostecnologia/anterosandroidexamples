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

package br.com.anteros.vendas.gui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.anteros.android.core.util.AndroidFileUtils;
import br.com.anteros.android.core.util.ImageUtils;
import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.Anexo;
import br.com.anteros.vendas.modelo.TipoConteudoAnexo;

/**
 * Acitivity para o cadastro de anexos do cliente.
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 13/05/16.
 */
public class AnexoCadastroActivity extends AppCompatActivity implements AdapterView.OnLongClickListener, View.OnClickListener {

    /**
     * Constantes dos códigos de resultados a chamadas de outras activities.
     */
    public static final int NAO_ALTEROU_ANEXO = 0;
    public static final int ALTEROU_ANEXO = 1;
    public static final int TIRAR_FOTO = 2;
    public static final int SELECIONAR_ARQUIVO = 3;

    public static final String ANTEROSVENDAS_ANEXO = "/anterosvendas/anexo";
    /**
     * Objeto Anexo sendo editado
     */
    private static Anexo anexo;
    /**
     * Controles visuais
     */
    private ImageView imgVisualizar;
    private ImageView imgFoto;
    private EditText edDescricao;
    private Bitmap fotoGaleria;

    /**
     * Dados do arquivo para captura da câmera.
     */
    private String filePath;
    private String fileName;
    private Uri mUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anexo_cadastro);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        /**
         * Obtém os controles visuais do layout
         */
        edDescricao = (EditText) findViewById(R.id.anexo_edDescricao);

        /**
         * Atribui os eventos
         */
        imgFoto = (ImageView) findViewById(R.id.anexo_imgFoto);
        imgFoto.setOnLongClickListener(this);

        imgVisualizar = (ImageView) findViewById(R.id.anexo_imgVisualizar);
        imgVisualizar.setOnClickListener(this);

        /**
         * Inicia os dados do arquivo
         */
        filePath = getCaminhoArquivo();
        fileName = getFileName();
        mUri = getUriArquivo();

        /**
         * Carrega os dados na view
         */
        carregaDadosParaView();
    }

    /**
     * Carrega os dados do objeto Anexo na view.
     */
    private void carregaDadosParaView() {
        edDescricao.setText(anexo.getNome());

        File imgFile = null;

        if (anexo.hasConteudo()) {
            fileName = anexo.getNome();
            imgFile = new File(anexo.getConteudoPath());
        } else {
            imgFile = new File(filePath, fileName);
        }

        if (imgFile.exists()) {
            atribuiImageParaFotoGaleria(imgFile);
            imgFoto.setImageBitmap(fotoGaleria);
            imgVisualizar.setEnabled(true);
        } else {
            imgFoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_arquivo_nao_encontrado));
            imgVisualizar.setEnabled(false);
        }

        /**
         * Obtém a URI referente ao arquivo anexo
         */
        mUri = Uri.fromFile(imgFile);
    }

    /**
     * Cria as opções no menu
     * @param menu Menu
     * @return True se criou
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.anexo_cadastro_action);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });

        return true;
    }

    /**
     * Evento quando um item do menu foi clicado
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /**
                 * Volta para tela anterior. Se estiver editando pergunta
                 * ao usuário se deseja cancelar.
                 */
                new QuestionAlert(this, getResources().getString(
                        R.string.app_name), "Cancelar anexo ?",
                        new QuestionAlert.QuestionListener() {

                            public void onPositiveClick() {
                                cancelaEdicaoAnexo();
                            }

                            public void onNegativeClick() {

                            }
                        }).show();

                break;

            case R.id.cliente_cadastro_action_upload:
                /**
                 * Chama o método para anexar arquivos.
                 */
                anexarArquivos();
                break;

            case R.id.cliente_cadastro_action_camera:
                /**
                 * Verifica se App possuí permissão para usar a câmera e gravar arquivos. Se não tiver
                 * requisita permissão.
                 */
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    /**
                     * Se já tem permissão inicia a câmera.
                     */
                    iniciaCamera();
                }
                break;

            case R.id.cliente_cadastro_action_salvar:
                /**
                 * Pergunta ao usuário se deseja salvar o anexo.
                 */
                new QuestionAlert(this,
                        getResources().getString(R.string.app_name),
                        "Salvar Anexo ?", new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        salvarAnexo();
                    }

                    public void onNegativeClick() {

                    }
                }).show();
                break;
        }
        return true;
    }

    /**
     * Evento long click para iniciar captura de imagem da câmera. Não foi usado onClick pois já foi
     * usado na imagem de visualizar para ver o anexo.
     * @param v View
     * @return True se executou
     */
    @Override
    public boolean onLongClick(View v) {
        /**
         * Verifica se App possuí permissão para usar a câmera e gravar arquivos. Se não tiver
         * requisita permissão.
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            /**
             * Se já tem permissão inicia a câmera.
             */
            iniciaCamera();
        }
        return false;
    }

    /**
     * Evento que ocorre quando o usuário confirmou a requisição de permissão.
     * @param requestCode Código da requisição
     * @param permissions Permissões
     * @param grantResults Grants
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /**
         * Se requestcode for igual 0(Zero) ele confirmou requisição
         */
        if (requestCode == 0) {
            /**
             * Verifica se ele aceitou a requisição com as permissões.
             */
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                /**
                 * Se já tem permissão inicia a câmera.
                 */
                iniciaCamera();
            }
        }
    }

    /**
     * Evento atribuido a imagem para visualizar o anexo.
     * @param v View
     */
    @Override
    public void onClick(View v) {
        /**
         * Se clicou na imagem abre o anexo.
         */
        if (v == imgVisualizar) {
            abrirAnexo(mUri);
        }
    }

    /**
     * Evento que ocorre quando a Activity é destruida. Aqui destruímos a foto.
     */
    @Override
    protected void onDestroy() {
        try {
            if (fotoGaleria != null && !fotoGaleria.isRecycled()) {
                fotoGaleria.recycle();
                fotoGaleria = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * Evento que retorna o resultado da chamada de outras activities.
     * @param requestCode Código da requisição
     * @param resultCode Código do resultado
     * @param data Dados
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * Se a requisção era para tira foto.
         */
        if (requestCode == TIRAR_FOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    new SalvaArquivoAnexo().execute(requestCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    new ErrorAlert(this, getResources().getString(
                            R.string.app_name), "Ocorreu um erro ao salvar a foto. "
                            + e.getMessage()).show();
                }
            }
            /**
             * Se a requisição era para selecionar arquivo
             */
        } else if (requestCode == SELECIONAR_ARQUIVO) {
            if (resultCode == RESULT_OK) {
                try {
                    mUri = data.getData();
                    new SalvaArquivoAnexo().execute(requestCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    new ErrorAlert(this, getResources().getString(
                            R.string.app_name), "Ocorreu um erro ao selecionar o arquivo. "
                            + e.getMessage()).show();
                }
            }
        }
    }


    /**
     * Se clicou no botão voltar para sair da Activity.
     * @param keyCode Tecla pressionada
     * @param event Evento
     * @return true se OK
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            /**
             * Pergunta ao usuário se deseja cancelar a edição do anexo.
             */
            new QuestionAlert(this, getResources().getString(
                    R.string.app_name), "Cancelar o anexo?",
                    new QuestionAlert.QuestionListener() {
                        public void onPositiveClick() {
                            cancelaEdicaoAnexo();
                        }

                        public void onNegativeClick() {
                        }
                    }).show();

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Devolve o caminho do arquivo do anexo.
     * @return
     */
    private String getCaminhoArquivo() {
        if (filePath == null) {
            filePath = Environment.getExternalStorageDirectory() + ANTEROSVENDAS_ANEXO;
            File path = new File(filePath);
            if (!path.exists()) {
                path.mkdirs();
            }
        }
        return filePath;
    }

    /**
     * Cria um novo nome de arquivo para o anexo.
     * @return Nome do arquivo
     */
    public String getFileName() {
        return "img_" + new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date()) + ".png";
    }

    /**
     * Retorna a URI do arquivo de anexo.
     * @return
     */
    private Uri getUriArquivo() {
        File f = new File(getCaminhoArquivo(), getFileName());
        return Uri.fromFile(f);
    }

    /**
     * Inicia a câmera para captura da imagem de anexo.
     */
    private void iniciaCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        startActivityForResult(takePictureIntent, TIRAR_FOTO);
    }

    /**
     * Anexar arquivos
     */
    private void anexarArquivos() {
        String items[] = {"Imagem", "Outros tipos"};
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("Seleção de arquivos");
        ab.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int choice) {
                if (choice == 0) {
                    selecionarImagem();
                } else if (choice == 1) {
                    selecionarOutrosTiposArquivo();
                }
            }
        });
        ab.show();
    }

    /**
     * Seleciona um arquivo do tipo imagem para anexar
     */
    private void selecionarImagem() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione"),
                SELECIONAR_ARQUIVO);
    }

    /**
     * Seleciona outros tipos de arquivos para anexar
     */
    private void selecionarOutrosTiposArquivo() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("file/*");
        startActivityForResult(Intent.createChooser(intent, "Selecione"), SELECIONAR_ARQUIVO);
    }

    private void atribuiImageParaFotoGaleria(File file) {
        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        TipoConteudoAnexo tipoConteudoAnexo = TipoConteudoAnexo.getTipoConteudoAnexoPorExtensao(extension);

        switch (tipoConteudoAnexo) {
            case IMAGEM:
                fotoGaleria = ImageUtils.loadScaledImage(file.getPath(), 640, 480);
                break;
            case PDF:
                fotoGaleria = BitmapFactory.decodeResource(getResources(), R.drawable.ic_file_extension_pdf);
                break;
            case PLANILHA:
                fotoGaleria = BitmapFactory.decodeResource(getResources(), R.drawable.ic_file_extension_xls);
                break;
            case TEXTO:
                fotoGaleria = BitmapFactory.decodeResource(getResources(), R.drawable.ic_file_extension_txt);
                break;
            case DOCUMENTO:
                fotoGaleria = BitmapFactory.decodeResource(getResources(), R.drawable.ic_file_extension_doc);
                break;
            case APRESENTACAO:
                fotoGaleria = BitmapFactory.decodeResource(getResources(), R.drawable.ic_file_extension_ppt);
                break;
            case HTML:
                fotoGaleria = BitmapFactory.decodeResource(getResources(), R.drawable.ic_file_extension_html);
                break;
            case RAR:
                fotoGaleria = BitmapFactory.decodeResource(getResources(), R.drawable.ic_file_extension_rar);
                break;
            case ZIP:
                fotoGaleria = BitmapFactory.decodeResource(getResources(), R.drawable.ic_file_extension_zip);
                break;
            case XML:
                fotoGaleria = BitmapFactory.decodeResource(getResources(), R.drawable.ic_file_extension_xml);
                break;
            default:
                fotoGaleria = BitmapFactory.decodeResource(getResources(), R.drawable.ic_file_extension_unk);
                break;
        }
    }

    /**
     * Cancela edição do anexo
     */
    protected void cancelaEdicaoAnexo() {
        setResult(NAO_ALTEROU_ANEXO);
        finish();
    }

    /**
     * Salva o anexo e finaliza retornando que ALTEROU_ANEXO
     */
    protected void salvarAnexo() {
        File file = AndroidFileUtils.getFile(this, mUri);
        if (edDescricao.getText().length() == 0) {
            new ErrorAlert(this, getResources().getString(R.string.app_name),
                    "O campo DESCRIÇÃO deve ser informado.").show();
        } else if (imgFoto.getDrawable() == null) {
            new ErrorAlert(this, getResources().getString(R.string.app_name),
                    "Não existe arquivo selecionado.").show();
        } else if (!file.exists()) {
            new ErrorAlert(this, getResources().getString(R.string.app_name),
                    "O arquivo selecionado não foi encontrado.").show();
        } else {

            anexo.setNome(edDescricao.getText().toString());
            anexo.setConteudoPath(file.getAbsolutePath());
            anexo.setNome(getFileName());
            anexo.setTipoConteudo(getTipoConteudoAnexo(anexo.getConteudoPath()));

            setResult(ALTEROU_ANEXO);
            finish();
        }
    }

    /**
     * Retorna tipo do conteúdo do anexo
     * @param caminho Caminho do arquivo
     * @return Tipo de conteúdo
     */
    private TipoConteudoAnexo getTipoConteudoAnexo(String caminho) {
        String extension = FilenameUtils.getExtension(caminho);
        return TipoConteudoAnexo.getTipoConteudoAnexoPorExtensao(extension);
    }

    /**
     * Abre o anexo pela URI do arquivo anexado para visualização.
     * @param mUri
     */
    private void abrirAnexo(Uri mUri) {
        String extension = FilenameUtils.getExtension(AndroidFileUtils.getPath(AnexoCadastroActivity.this, mUri));
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            MimeTypeMap mime = MimeTypeMap.getSingleton();

            String type = mime.getMimeTypeFromExtension(extension);

            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(mUri, type);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);

        } catch (ActivityNotFoundException e) {
            new ErrorAlert(AnexoCadastroActivity.this, getResources().getString(R.string.app_name),
                    "Não foi encontrado nenhum aplicativo nesse aparelho que suporte abrir a extensão '"
                            + extension + "', entre em contato com a equipe de Suporte para resolver esse problema.")
                    .show();
        } catch (Exception e) {
            new ErrorAlert(AnexoCadastroActivity.this,
                    getResources().getString(R.string.app_name), "Não foi possível abrir o anexo " + anexo.getId()
                    + ". " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    /**
     * Atribui o objeto estático anexo
     * @param anexo
     */
    public static void setAnexo(Anexo anexo) {
        AnexoCadastroActivity.anexo = anexo;
    }

    /**
     * Retorna o objeto estático anexo
     * @return Anexo
     */
    public static Anexo getAnexo() {
        return anexo;
    }

    /**
     * AsynTask para salvar o anexo.
     */
    public class SalvaArquivoAnexo extends AsyncTask<Integer, Void, String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(AnexoCadastroActivity.this, getResources()
                    .getString(R.string.app_name), "Salvando Anexo...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            imgFoto.setBackgroundDrawable(null);
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                int requestCode = params[0];
                if (requestCode == TIRAR_FOTO) {
                    /**
                     * Redimensiona a imagem para 640x480
                     */
                    fotoGaleria = ImageUtils.resizeAndStorageImage(mUri.getEncodedPath(), 640, 480);
                } else if (requestCode == SELECIONAR_ARQUIVO) {
                    /**
                     * Obtém o arquivo selecionado e salva com novo nome para anexar.
                     */
                    File sourceFile = AndroidFileUtils.getFile(AnexoCadastroActivity.this, mUri);
                    File destinationFile = new File(filePath, getFileName());

                    if (!sourceFile.exists())
                        throw new RuntimeException("Arquivo de origem não encontrado. Caminho["
                                + sourceFile.getPath() + "]");

                    /**
                     * Copia arquivo de origem para o novo arquivo
                     */
                    FileUtils.copyFile(sourceFile, destinationFile);
                    mUri = Uri.fromFile(destinationFile);

                    /**
                     * Atribui a imagem para a foto galeria para visualizar.
                     */
                    atribuiImageParaFotoGaleria(destinationFile);
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage() + "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result == null) {
                imgFoto.setImageBitmap(fotoGaleria);
                imgVisualizar.setEnabled(true);
            } else {
                imgFoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_arquivo_nao_encontrado));
                imgVisualizar.setEnabled(false);
                new ErrorAlert(AnexoCadastroActivity.this, getResources().getString(
                        R.string.app_name), "Ocorreu um erro ao salvar o anexo: " + result).show();
            }
        }
    }
}
