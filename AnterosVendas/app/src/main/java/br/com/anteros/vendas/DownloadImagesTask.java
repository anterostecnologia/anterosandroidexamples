package br.com.anteros.vendas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import br.com.anteros.core.utils.IOUtils;

/**
 * Created by edson on 12/05/16.
 */
public class DownloadImagesTask extends AsyncTask<String,String,List<byte[]>> {
    @Override
    protected List<byte[]> doInBackground(String... params) {

        List<byte[]> result = new ArrayList<>();
        for (String url : params){

            URLConnection conn = null;
            try {
                conn = new URL( url ).openConnection();
                conn.connect();
                byte[] byteArray = IOUtils.toByteArray(conn.getInputStream());
                result.add(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


        return result;
    }
}
