package br.com.anteros.vendas;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import br.com.anteros.android.core.util.FileUtil;
import br.com.anteros.vendas.gui.ManutencaoTabelasActivity;

public class ImportDatabaseTask extends AsyncTask<Void, Void, String> {
	private final ProgressDialog dialog;
	private AppCompatActivity activity;
	private String databaseName;
	private File importDatabaseFile;

	public ImportDatabaseTask(AppCompatActivity activity, File importDatabaseFile,
			String databaseName) {
		this.importDatabaseFile = importDatabaseFile;
		this.activity = activity;
		this.dialog = new ProgressDialog(activity);
		this.dialog.setCancelable(false);
		this.databaseName = databaseName;
	}

	@Override
	protected void onPreExecute() {
		dialog.setMessage("Importando banco de dados...");
		dialog.show();
	}

	@Override
	protected String doInBackground(final Void... args) {

		try {
			AnterosVendasContext.getInstance().getSession().close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		File dbBackupFile = importDatabaseFile;
		if (!dbBackupFile.exists()) {
			return "Arquivo de backup não foi encontrado. Não foi possível importar.";
		} else if (!dbBackupFile.canRead()) {
			return "Arquivo de backup não pode ser lido. Não foi possível importar.";
		}

		File dbFile = new File(databaseName);

		if (dbFile.exists()) {
			dbFile.delete();
		}

		try {
			FileUtil.copyFile(dbBackupFile, dbFile);
			return "OK";
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	@Override
	protected void onPostExecute(final String errMsg) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}

		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setTitle("Aviso");
		if (errMsg.equals("OK")) {
			dialog.setMessage("Importação realizada com sucesso!");
			Toast.makeText(activity, "Importação realizada com sucesso!", Toast.LENGTH_SHORT).show();
		} else {
			dialog.setMessage("Importação falhou - " + errMsg);
			Toast.makeText(activity, "Importação falhou - " + errMsg, Toast.LENGTH_LONG).show();
		}
		dialog.setCancelable(false);
		dialog.setPositiveButton("Ok", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog dlg = dialog.create();
		dlg.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				activity.setResult(ManutencaoTabelasActivity.RECRIOU_TABELAS);
				activity.finish();
			}
		});
		dlg.show();
	}
}
