package br.com.anteros.vendas.gui.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.persistence.transaction.impl.TransactionException;
import br.com.anteros.vendas.AnterosVendasContext;
import br.com.anteros.vendas.R;
import br.com.anteros.vendas.gui.PedidoCadastroActivity;
import br.com.anteros.vendas.gui.PedidoCadastroDadosFragment;
import br.com.anteros.vendas.gui.PedidoConsultaActivity;
import br.com.anteros.vendas.modelo.ItemPedido;

/**
 * Created by eduardogreco on 5/13/16.
 */
public class PedidoCadastroItensFragmentAdapter extends ArrayAdapter<ItemPedido> {

    private List<ItemPedido> itens;

    public PedidoCadastroItensFragmentAdapter(Context context, List<ItemPedido> objects) {
        super(context, R.layout.pedido_cadastro_itens_lista, objects);
        this.itens = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.pedido_cadastro_itens_lista, null);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.edQuantidade = (EditText) view.findViewById(R.id.pedido_cadastro_itens_edQuantidade);

            view.setTag(viewHolder);
            viewHolder.edQuantidade.setTag(itens.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).edQuantidade.setTag(itens.get(position));
        }

        final ItemPedido item = itens.get(position);

        TextView tvProduto = (TextView) view.findViewById(R.id.pedido_cadastro_itens_produto);
        final TextView tvValorTotal = (TextView) view.findViewById(R.id.pedido_cadastro_itens_valorTotal);
        TextView edPreco = (TextView) view.findViewById(R.id.pedido_cadastro_itens_edPreco);
        ImageView imgProduto = (ImageView) view.findViewById(R.id.pedido_cadastro_itens_item_fotoProduto);
        ImageView imgDelete = (ImageView) view.findViewById(R.id.pedido_cadastro_itens_imgDelete);
        imgDelete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                delete(item);
            }
        });


        if (item != null) {
            tvProduto.setText(item.getProduto().getId() + " - " + item.getProduto().getNomeProduto());
            edPreco.setText(item.getVlProdutoAsString());

            if (item.getProduto().getFotoProduto() != null) {
                imgProduto.setImageBitmap(BitmapFactory.decodeByteArray(item.getProduto().getFotoProduto(), 0, item.getProduto().getFotoProduto().length));
            }

            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.edQuantidade.setText(item.getQtProduto().toPlainString());
            tvValorTotal.setText(item.getVlTotalAsString());
            holder.edQuantidade.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!StringUtils.isEmpty(s.toString())) {
                        item.setQtProduto(new BigDecimal(s.toString()));
                        item.setVlTotal(item.getVlProduto().multiply(item.getQtProduto()));
                        tvValorTotal.setText(item.getVlTotalAsString());
                        PedidoCadastroActivity.calcularTotalPedido();
                    }
                }
            });
        }

        return view;
    }

    protected void delete(final ItemPedido itemPedido) {
        new QuestionAlert(getContext(), getContext().getString(
                R.string.app_name), "Remover o item ?",
                new QuestionAlert.QuestionListener() {

                    public void onPositiveClick() {
                        SQLRepository<ItemPedido, Long> pedidoItemFactory = AnterosVendasContext.getInstance().getSQLRepository(ItemPedido.class);

                        try {
                            if (itemPedido.getId() != null) {
                                ItemPedido ite = pedidoItemFactory.findOne(
                                        "SELECT PI.* FROM PEDIDO_ITEM PI WHERE PI.ID_ITEM = :PID_ITEM",
                                        new NamedParameter("PID_ITEM", itemPedido.getId()));

                                pedidoItemFactory.getTransaction().begin();
                                pedidoItemFactory.remove(ite);
                                pedidoItemFactory.getTransaction().commit();
                            }

                            remove(itemPedido);
                            PedidoConsultaActivity.pedido.setVlTotalPedido(PedidoConsultaActivity.pedido.getVlTotalPedido().subtract(itemPedido.getVlTotal()));
                            PedidoCadastroDadosFragment.atualizarValorTotal();
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                pedidoItemFactory.getTransaction().rollback();
                            } catch (Exception e1) {
                            }
                            if (e instanceof TransactionException) {
                                new ErrorAlert(getContext(), getContext().getResources().getString(
                                        R.string.app_name), "Ocorreu um erro ao remover o item do pedido. " + e.getCause()).show();
                            } else {
                                new ErrorAlert(getContext(), getContext().getResources().getString(
                                        R.string.app_name), "Ocorreu um erro ao remover o item do pedido. " + e.getMessage()).show();
                            }
                        }
                    }

                    public void onNegativeClick() {

                    }
                }).show();

    }

    static class ViewHolder {
        protected EditText edQuantidade;
    }
}


