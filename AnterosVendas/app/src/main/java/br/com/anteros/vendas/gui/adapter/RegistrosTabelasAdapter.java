package br.com.anteros.vendas.gui.adapter;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.anteros.vendas.R;

public class RegistrosTabelasAdapter extends CursorAdapter {
    public RegistrosTabelasAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewGroup root = (ViewGroup) view.findViewById(R.id.registros_tabelas_item_root);
        root.removeAllViews();

        for (int i = 0; i < cursor.getColumnCount() - 1; i++) {
            LinearLayout container = new LinearLayout(context);
            container.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            container.setOrientation(LinearLayout.HORIZONTAL);

            TextView lbColuna = new TextView(context);
            lbColuna.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            lbColuna.setPadding(2, 2, 2, 2);
            lbColuna.setText(cursor.getColumnName(i));
            lbColuna.append(": ");
            lbColuna.setTypeface(null, Typeface.BOLD);

            TextView lbValor = new TextView(context);
            lbValor
                    .setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            lbValor.setPadding(2, 2, 2, 2);
            lbValor.setId(i);

            try {
                lbValor.setText(cursor.getString(i));
            } catch (Exception ex) {
                if ((ex.getMessage() + "").contains("BLOB")) {
                    try {
                        lbValor.setText(new String(cursor.getBlob(i), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    ex.printStackTrace();
                }
            }

            container.addView(lbColuna);
            container.addView(lbValor);

            root.addView(container);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.registros_tabelas_item, parent, false);
        // ViewGroup root = (ViewGroup)
        // view.findViewById(R.registros_tabelas_item.root);
        //
        // for (int i = 0; i < cursor.getColumnCount() - 1; i++) {
        // LinearLayout container = new LinearLayout(context);
        // container.setLayoutParams(new LinearLayout.LayoutParams(
        // LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        // container.setOrientation(LinearLayout.HORIZONTAL);
        //
        // TextView lbColuna = new TextView(context);
        // lbColuna.setLayoutParams(new
        // LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
        // LayoutParams.WRAP_CONTENT));
        // lbColuna.setPadding(2, 2, 2, 2);
        // lbColuna.setText(cursor.getColumnName(i));
        // lbColuna.append(": ");
        // lbColuna.setTypeface(null, Typeface.BOLD);
        //
        // TextView lbValor = new TextView(context);
        // lbValor
        // .setLayoutParams(new
        // LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
        // LayoutParams.WRAP_CONTENT));
        // lbValor.setPadding(2, 2, 2, 2);
        // lbValor.setId(i);
        //
        // lbValor.setText(cursor.getString(i));
        //
        // container.addView(lbColuna);
        // container.addView(lbValor);
        //
        // root.addView(container);
        // }

        bindView(view, context, cursor);

        return view;
    }

}
