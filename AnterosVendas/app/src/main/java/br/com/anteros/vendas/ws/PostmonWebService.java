package br.com.anteros.vendas.ws;

import android.os.AsyncTask;

import com.google.gson.Gson;

import br.com.anteros.vendas.gui.HttpHelper;

/**
 * Created by edson on 12/05/16.
 */
public class PostmonWebService extends AsyncTask<String,String,PostmonResponse> {

    @Override
    protected PostmonResponse doInBackground(String... params) {
        PostmonResponse result = null;
        try {
            String response = HttpHelper.getJSON("http://api.postmon.com.br/v1/cep/" + params[0], null, 2000, HttpHelper.GET, HttpHelper.CONTENT_TYPE_JSON);
            if (response!=null){
                result = new Gson().fromJson(response, PostmonResponse.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
