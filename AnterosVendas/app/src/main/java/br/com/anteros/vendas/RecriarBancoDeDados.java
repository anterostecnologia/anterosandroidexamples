package br.com.anteros.vendas;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

public abstract class RecriarBancoDeDados extends AsyncTask<Void, Void, String> {

    private ProgressDialog progress;
    private AppCompatActivity activity;

    public RecriarBancoDeDados(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        progress = ProgressDialog.show(activity, activity.getResources()
                .getString(R.string.app_name), "Aguarde...", true);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            AnterosVendasContext.getInstance().recriarBancoDados();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage() + "";
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        progress.dismiss();
        if (result == null) {
            onSuccess();
        } else {
            onError("Ocorreu um erro ao recriar o banco de dados: " + result);
        }
    }

    public abstract void onSuccess();

    public abstract void onError(String message);

}
