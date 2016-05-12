package br.com.anteros.vendas.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.adapter.PedidoCadastroPageViewAdapter;
import br.com.anteros.vendas.modelo.PedidoVenda;

public class PedidoCadastroActivity extends AppCompatActivity {

    private PedidoVenda pedido;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager viewPager;

    private PedidoCadastroDadosFragment pedidoCadastroDadosFragment;
    private PedidoCadastroItensFragment pedidoCadastroItensFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//
//        mViewPager = (ViewPager) findViewById(R.id.container);
//        mViewPager.setAdapter(mSectionsPagerAdapter);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_pedido_tabs);
        tabLayout.setupWithViewPager(viewPager);


        if (getIntent().hasExtra("pedido")) {
            pedido = (PedidoVenda) getIntent().getSerializableExtra("pedido");
        } else {
            pedido = new PedidoVenda();
        }
        bindView();
    }

    private void bindView() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pedido, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_action_settings:
                //
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pedido, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "DADOS";
                case 1:
                    return "ITENS";
            }
            return null;
        }
    }

    private void setupViewPager(final ViewPager viewPager) {
        PedidoCadastroPageViewAdapter adapter = new PedidoCadastroPageViewAdapter(getSupportFragmentManager());

        Bundle arguments = new Bundle();
        arguments.putParcelable("teste", pedido);

        pedidoCadastroDadosFragment = new PedidoCadastroDadosFragment();
        pedidoCadastroDadosFragment.setArguments(arguments);
        adapter.addFragment(pedidoCadastroDadosFragment, "Dados");

        pedidoCadastroItensFragment = new PedidoCadastroItensFragment();
        pedidoCadastroItensFragment.setArguments(arguments);
        adapter.addFragment(pedidoCadastroItensFragment, "Itens");

        viewPager.setAdapter(adapter);
    }
}
