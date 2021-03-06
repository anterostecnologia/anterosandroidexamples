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

package br.com.anteros.vendas.gui;

import br.com.anteros.android.persistence.backup.DatabaseMaintenanceFragment;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.vendas.AnterosVendasContext;

/**
 *  Fragmento responsável pelo conteúdo da Activity ManutencaoTabelas.
 * @author Eduardo Greco (eduardogreco93@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Edson Martins (edsonmartins2005@gmail.com)
 *         Data: 12/05/16.
 */
public class ManutencaoTabelasFragment extends DatabaseMaintenanceFragment {

    /**
     * Atribui a sessão de persistência
     * @return
     */
    @Override
    public SQLSession getSQLSession() {
        return AnterosVendasContext.getInstance().getSession();
    }
}
