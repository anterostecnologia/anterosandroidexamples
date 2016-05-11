package br.com.anteros.vendas.gui;

import java.sql.ResultSetMetaData;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import br.com.anteros.android.persistence.sql.jdbc.SQLiteResultSet;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.RegistrosTabelasAdapter;

public class RegistrosTabelasActivity extends AppCompatActivity {
	private static String tabela;
	private ListView lvRegistros;
	private RegistrosTabelasAdapter registrosAdapter;
	private SQLiteResultSet resultSet;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registros_tabelas);

		SQLSession session = AnterosVendasContext.getInstance().getSession();
		try {
			resultSet = (SQLiteResultSet) session.createQuery("select *, rowid as _id from " + tabela).executeQuery();

			lvRegistros = (ListView) findViewById(R.id.registros_tabelas_lvRegistros);
			lvRegistros.setHorizontalScrollBarEnabled(true);
			
			
			ResultSetMetaData metaData = resultSet.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
			    System.out.println("COLUMN " + i + ": " + metaData.getColumnName(i));
			}

			registrosAdapter = new RegistrosTabelasAdapter(this, resultSet.getCursor());
			lvRegistros.setAdapter(registrosAdapter);

			registrosAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void setTabela(String tab) {
		tabela = tab;
	}

	@Override
	protected void onDestroy() {
		try {
			resultSet.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		super.onDestroy();
	}

}
