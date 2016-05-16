
/*
 * Copyright 2016 Anteros Tecnologia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.anteros.vendas;


import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import br.com.anteros.core.utils.IOUtils;

/**
 * Realiza o download de várias imagens na mesma tarefa.
 *
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 09/05/16.
 */
public class DownloadImagesTask extends AsyncTask<String,String,List<byte[]>> {
    @Override
    protected List<byte[]> doInBackground(String... params) {

        List<byte[]> result = new ArrayList<>();
        for (String url : params){

            URLConnection conn = null;
            try {
                /**
                 * Abre conexão com url da imagem
                 */
                conn = new URL( url ).openConnection();
                conn.connect();
                /**
                 * Le os bytes da imagem e adiciona na lista de retorno
                 */
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
