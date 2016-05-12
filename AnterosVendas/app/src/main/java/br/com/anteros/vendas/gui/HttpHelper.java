package br.com.anteros.vendas.gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by edson on 03/05/16.
 */
public class HttpHelper {

    public static String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static String CONTENT_TYPE_JSON = "application/json";
    public static String GET = "GET";
    public static String POST = "POST";

    public static String getJSON(String url, String data, int timeout, String method, String contentType) throws Exception {
        HttpURLConnection connection = null;
        try {

            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod(method);

            //set the sending type and receiving type to json
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Accept", CONTENT_TYPE_JSON);

            connection.setAllowUserInteraction(false);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);

            if (data != null) {
                //set the content length of the body
                connection.setRequestProperty("Content-length", data.getBytes().length + "");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                //send the json as body of the request
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data.getBytes("UTF-8"));
                outputStream.close();
            }

            //Connect to the server
            connection.connect();

            int status = connection.getResponseCode();
            Logger.getLogger(HttpHelper.class.getName()).log(Level.INFO,"HTTP Client", "HTTP status code : " + status);
            switch (status) {
                case 200:
                case 201:
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    bufferedReader.close();
                    Logger.getLogger(HttpHelper.class.getName()).log(Level.INFO,"HTTP Client", "Received String : " + sb.toString());
                    //return received string
                    return sb.toString();
                case 401:
                    throw new RuntimeException("Aplicação não autorizada. Retornou erro 401. Verifique com o responsável pela aplicação.");
            }
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE,"HTTP Client", "Error in http connection" + ex.toString());
                }
            }
        }
        return null;
    }
}
