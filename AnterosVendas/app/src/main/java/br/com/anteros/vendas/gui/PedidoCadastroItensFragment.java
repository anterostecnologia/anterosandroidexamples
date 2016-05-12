package br.com.anteros.vendas.gui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Set;

import br.com.anteros.vendas.R;

/**
 * Created by eduardogreco on 5/12/16.
 */
public class PedidoCadastroItensFragment extends Fragment {

    private static Uri replaceUriParameter(Uri uri, String key, String newValue) {
        final Set<String> params = uri.getQueryParameterNames();
        final Uri.Builder newUri = uri.buildUpon().clearQuery();
        for (String param : params) {
            String value;
            if (param.equals(key)) {
                value = newValue;
            } else {
                value = uri.getQueryParameter(param);
            }

            newUri.appendQueryParameter(param, value);
        }

        return newUri.build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pedido_cadastro_itens, null);

        try {
            //  PedidoVenda pedido = getArguments().getParcelable("pedido");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

}
