package br.com.anteros.vendas.gui;

import br.com.anteros.android.persistence.backup.DatabaseMaintenanceFragment;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.vendas.AnterosVendasContext;

/**
 * Created by edson on 12/05/16.
 */
public class ManutencaoTabelasFragment extends DatabaseMaintenanceFragment {

    @Override
    public SQLSession getSQLSession() {
        return AnterosVendasContext.getInstance().getSession();
    }
}
