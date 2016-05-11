package br.com.anteros.vendas.gui;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.anteros.validation.api.ConstraintViolation;
import br.com.anteros.vendas.R;

public class MensagemErrorDialog<T> extends Dialog implements View.OnClickListener {

    private Button btnOK;
    private Button next;
    private Button previous;
    private TextView edErro;
    private int listPosition;
    private List<ConstraintViolation<T>> messageViolations;

    public MensagemErrorDialog(Context context, String title, Set<ConstraintViolation<T>> violations) {
        super(context);
        setContentView(R.layout.dialog_error_alert);
        setTitle(title);

        listPosition = 0;

        btnOK = (Button) findViewById(R.id.error_alert_dialog_botaoOk);
        next = (Button) findViewById(R.id.error_alert_dialog_botaoProximo);
        previous = (Button) findViewById(R.id.error_alert_dialog_botaoAnterior);
        edErro = (TextView) findViewById(R.id.error_alert_dialog_textDialog);

        messageViolations = new ArrayList<ConstraintViolation<T>>(violations);
        edErro.setText(messageViolations.get(listPosition).getMessage());
        templateControl();

        btnOK.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnOK) {
            dismiss();
        } else if (v == next) {
            if (listPosition < (messageViolations.size() - 1)) {
                listPosition = listPosition + 1;
                edErro.setText(messageViolations.get(listPosition).getMessage());
                templateControl();
            }
        } else if (v == previous) {
            if (listPosition > 0) {
                listPosition = listPosition - 1;
                edErro.setText(messageViolations.get(listPosition).getMessage());
                templateControl();
            }
        }

    }

    private void templateControl() {
        if (listPosition < (messageViolations.size() - 1))
            next.setEnabled(true);
        else
            next.setEnabled(false);

        if (listPosition > 0)
            previous.setEnabled(true);
        else
            previous.setEnabled(false);

    }

}
