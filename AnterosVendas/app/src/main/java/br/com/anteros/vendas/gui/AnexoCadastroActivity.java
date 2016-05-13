package br.com.anteros.vendas.gui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import br.com.anteros.android.core.util.ImageUtils;
import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.vendas.FileUtil;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.modelo.Anexo;
import br.com.anteros.vendas.modelo.TipoConteudoAnexo;

/**
 * Created by eduardogreco on 5/13/16.
 */
public class AnexoCadastroActivity extends AppCompatActivity implements AdapterView.OnLongClickListener, View.OnClickListener {

    private static final int NAO_ALTEROU_ANEXO = 0;
    public static final int ALTEROU_ANEXO = 1;
    private static final int TIRAR_FOTO = 2;
    private static final int SELECIONAR_ARQUIVO = 3;
    private static Anexo anexo;
    private ImageView imgVisualizar;
    private ImageView imgFoto;
    private EditText edDescricao;
    private Uri mUri;
    private Bitmap fotoGaleria;
    private String filePath;
    private String fileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anexo_cadastro);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        edDescricao = (EditText) findViewById(R.id.anexo_edDescricao);

        imgFoto = (ImageView) findViewById(R.id.anexo_imgFoto);
        imgFoto.setOnLongClickListener(this);

        imgVisualizar = (ImageView) findViewById(R.id.anexo_imgVisualizar);
        imgVisualizar.setOnClickListener(this);

        filePath = getFilePath();
        fileName = getFileName();
        mUri = getUriFile();

        bindView();
    }

    private void bindView() {
        edDescricao.setText(anexo.getNome());

        File imgFile = null;

        if (anexo.hasConteudo()) {
            fileName = anexo.getNome();
            imgFile = new File(anexo.getConteudoPath());
        } else {
            imgFile = new File(filePath, fileName);
        }

        if (imgFile.exists()) {
            setImageFileToFotoGaleria(imgFile);
            imgFoto.setImageBitmap(fotoGaleria);
            imgVisualizar.setEnabled(true);
        } else {
            imgFoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_arquivo_nao_encontrado));
            imgVisualizar.setEnabled(false);
        }

        mUri = Uri.fromFile(imgFile);
    }

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

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                new QuestionAlert(this, getResources().getString(
                        R.string.app_name), "Cancelar anexo ?",
                        new QuestionAlert.QuestionListener() {

                            public void onPositiveClick() {
                                cancela();
                            }

                            public void onNegativeClick() {

                            }
                        }).show();

                break;

            case R.id.cliente_cadastro_action_upload:
                opcoesUpload();
                break;

            case R.id.cliente_cadastro_action_camera:
                iniciaCamera();
                break;

            case R.id.cliente_cadastro_action_salvar:
                new QuestionAlert(this,
                        getResources().getString(R.string.app_name),
                        "Salvar Anexo ?", new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        salva();
                    }

                    public void onNegativeClick() {

                    }
                }).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onLongClick(View v) {
        iniciaCamera();
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == imgVisualizar) {
            openAnexo(mUri);
        }
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    public class SalvaArquivoAnexo extends AsyncTask<Integer, Void, String> {

        private ProgressDialog pg;

        @Override
        protected void onPreExecute() {
            pg = ProgressDialog.show(AnexoCadastroActivity.this, getResources()
                    .getString(R.string.app_name), "Salvando Anexo...");
            pg.setCancelable(false);
            pg.setCanceledOnTouchOutside(false);
            imgFoto.setBackgroundDrawable(null);
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                int requestCode = params[0];
                if (requestCode == TIRAR_FOTO) {
                    fotoGaleria = ImageUtils.resizeAndStorageImage(mUri.getEncodedPath(), 800, 600);
                } else if (requestCode == SELECIONAR_ARQUIVO) {
                    File sourceFile = new File(FileUtil.getFilePathByURI(AnexoCadastroActivity.this, mUri));
                    File destinationFile = new File(filePath, getFileName());

                    if (!sourceFile.exists())
                        throw new RuntimeException("Arquivo de origem não encontrado! Source path["
                                + sourceFile.getPath() + "]");

                    FileUtils.copyFile(sourceFile, destinationFile);
                    mUri = Uri.fromFile(destinationFile);

                    setImageFileToFotoGaleria(destinationFile);
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage() + "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pg.dismiss();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            new QuestionAlert(this, getResources().getString(
                    R.string.app_name), "Cancelar Anexo?",
                    new QuestionAlert.QuestionListener() {
                        public void onPositiveClick() {
                            cancela();
                        }

                        public void onNegativeClick() {
                        }
                    }).show();

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private String getFilePath() {
        if (filePath == null) {
            filePath = Environment.getExternalStorageDirectory() + "/anterosvendas/anexo";
            File path = new File(filePath);
            if (!path.exists()) {
                path.mkdirs();
            }
        }
        return filePath;
    }

    public String getFileName() {
        String extensao = "";
        if (mUri != null) {
            extensao = FilenameUtils.getExtension(FileUtil.getFilePathByURI(AnexoCadastroActivity.this, mUri));
            if (extensao != null && !extensao.equals(""))
                extensao = "." + extensao;
        }

        if (fileName == null) {
            fileName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date()) + extensao;
        }
        String fileExtensao = "." + FilenameUtils.getExtension(fileName);
        if (fileExtensao == null || fileExtensao.equals("") || !fileExtensao.equals(extensao))
            fileName = FilenameUtils.removeExtension(fileName) + extensao;
        return fileName;
    }

    private Uri getUriFile() {
        if (mUri == null) {
            File f = new File(getFilePath(), getFileName());
            mUri = Uri.fromFile(f);
        }
        return mUri;
    }

    private void iniciaCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        startActivityForResult(takePictureIntent, TIRAR_FOTO);
    }

    private void opcoesUpload() {
        String items[] = {"Imagem", "Outros tipos"};
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("Upload de arquivos");
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

    private void selecionarImagem() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione"),
                SELECIONAR_ARQUIVO);
    }

    private void selecionarOutrosTiposArquivo() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("file/*");
        startActivityForResult(Intent.createChooser(intent, "Selecione"), SELECIONAR_ARQUIVO);
    }

    private void setImageFileToFotoGaleria(File file) {
        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        TipoConteudoAnexo tipoConteudoAnexo = TipoConteudoAnexo.getTipoConteudoAnexoPorExtensao(extension);

        switch (tipoConteudoAnexo) {
            case IMAGEM:
                fotoGaleria = ImageUtils.loadScaledImage(file.getPath(), 800, 600);
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

    protected void cancela() {
        setResult(NAO_ALTEROU_ANEXO);
        finish();
    }

    protected void salva() {
        File file = new File(filePath, getFileName());
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

    private TipoConteudoAnexo getTipoConteudoAnexo(String path) {
        String extension = FilenameUtils.getExtension(path);
        return TipoConteudoAnexo.getTipoConteudoAnexoPorExtensao(extension);
    }

    private void openAnexo(Uri mUri) {
        String extension = FilenameUtils.getExtension(FileUtil.getFilePathByURI(AnexoCadastroActivity.this, mUri));
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

    public static void setAnexo(Anexo anex) {
        anexo = anex;
    }

    public static Anexo getAnexo() {
        return anexo;
    }
}
