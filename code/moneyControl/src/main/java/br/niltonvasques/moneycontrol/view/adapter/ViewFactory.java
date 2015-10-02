package br.niltonvasques.moneycontrol.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.FontAwesomeText;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.business.CartaoBusiness;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Ativo;
import br.niltonvasques.moneycontrol.database.bean.CartaoCredito;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.database.bean.ContaAPagar;
import br.niltonvasques.moneycontrol.database.bean.Fatura;
import br.niltonvasques.moneycontrol.database.bean.MovimentacaoAtivo;
import br.niltonvasques.moneycontrol.database.bean.Orcamento;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.util.AssetUtil;
import br.niltonvasques.moneycontrol.util.DateUtil;
import br.niltonvasques.moneycontrol.util.MessageUtils;
import br.niltonvasques.moneycontrol.util.ViewUtil;
import br.niltonvasques.moneycontrolbeta.R;

public class ViewFactory {

    private static final String TAG = "[ViewFactory]";

    public static View createTransacaoItemView(Transacao tr, MoneyControlApp app, LayoutInflater inflater){
        View view = inflater.inflate(R.layout.transacao_list_item, null);

        TextView txtNome = (TextView) view.findViewById(R.id.transacaoListItemTxtDescricao);
        TextView txtSaldo = (TextView) view.findViewById(R.id.transacaoListItemTxtValor);
        TextView txtData = (TextView) view.findViewById(R.id.transacaoListItemTxtData);
        FontAwesomeText fontAwesome = (FontAwesomeText) view.findViewById(R.id.transacaoListItemFontAwe);

        String tipo = app.getDatabase().runQuery(QuerysUtil.checkTipoTransacao(tr.getId()));

        CategoriaTransacao c = app.getCategoriasTransacao().get(tr.getId_CategoriaTransacao());

        if(tipo.equals("2")){
            txtSaldo.setTextColor(Color.RED);
        }else{
            txtSaldo.setTextColor(app.getResources().getColor(R.color.dark_green));
            fontAwesome.setTextColor(app.getResources().getColor(R.color.dark_green));
            fontAwesome.setIcon("fa-arrow-up");
        }

        txtNome.setText(tr.getDescricao());
        txtSaldo.setText("R$ " + String.format("%.2f", tr.getValor()));
        try {
            GregorianCalendar g = new GregorianCalendar();
            g.setTime(DateUtil.sqlDateFormat().parse(tr.getData()));
            ViewUtil.adjustDateOnTextView(txtData,g);
            txtData.setText(txtData.getText().toString()+(c != null ? " - "+c.getNome() : ""));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    public static View createAtivoItemView(final Ativo ativo, final Context context, Date dateRange, final MoneyControlApp app,
                                           final LayoutInflater inflater, final DialogInterface.OnClickListener listener) {
        View view = inflater.inflate(R.layout.ativo_list_item, null);

        TextView txtNome = (TextView) view.findViewById(R.id.transacaoListItemTxtDescricao);
        TextView txtValor = (TextView) view.findViewById(R.id.ativoListItemTxtPrice);
        TextView txtProfit = (TextView) view.findViewById(R.id.ativoListItemTxtProfit);
        TextView txtYearProfit = (TextView) view.findViewById(R.id.ativoListItemTxtYearProfit);
//		TextView txtData = (TextView) view.findViewById(R.id.transacaoListItemTxtData);
        Button btnNewEvent = (Button) view.findViewById(R.id.ativoListItemBtnNewEvent);

//		String tipo = app.getDatabase().runQuery(QuerysUtil.checkTipoAtivo(ativo.getId()));

        //Compute historical profit
        List<MovimentacaoAtivo> movimentacoes = app.getDatabase().select(MovimentacaoAtivo.class, QuerysUtil.whereLastMovimentacoesAtivoOrderByDateDesc(ativo.getId(), dateRange));
        if( ! movimentacoes.isEmpty()){
            MovimentacaoAtivo lastMovimentacao = movimentacoes.get(0);
            try {
                float profit = 0;

                float lastValorCota = 1, penultValorCota = 1;

                if(lastMovimentacao.getCotas() != 0)
                    lastValorCota = (lastMovimentacao.getPatrimonio()/lastMovimentacao.getCotas());

                profit = ((lastValorCota/penultValorCota)-1)*100;

                txtValor.setText("R$ "+String.format("%.2f",lastMovimentacao.getPatrimonio()));
                txtProfit.setText(String.format("%.2f",profit)+" %");

                if(profit < 0){
                    txtProfit.setTextColor(Color.RED);
                }

            } catch (Exception e) {

            }

        }else{
            txtValor.setText("R$ "+String.format("%.2f", 0f));
            txtProfit.setText("0.00 %");
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dateRange);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        List<MovimentacaoAtivo> first = app.getDatabase().select(MovimentacaoAtivo.class, QuerysUtil.whereFirstMovimentacaoAtivoOnYear(ativo.getId(), cal.get(Calendar.YEAR)));
        List<MovimentacaoAtivo> last = app.getDatabase().select(MovimentacaoAtivo.class, QuerysUtil.whereLastMovimentacaoAtivoOnYear(ativo.getId(), cal.getTime()));
        if( !last.isEmpty()){
            try {
                float profit = 0;

                float lastValorCota = 1, penultValorCota = 1;

                if(!first.isEmpty() && first.get(0).getCotas() != 0)
                    penultValorCota = first.get(0).getPatrimonio() / first.get(0).getCotas();

                if(last.get(0).getCotas() != 0)
                    lastValorCota = last.get(0).getPatrimonio() / last.get(0).getCotas();

                profit = ((lastValorCota/penultValorCota)-1)*100;

                txtYearProfit.setText(String.format("%.2f", profit) + " %");

                if(profit < 0){
                    txtYearProfit.setTextColor(Color.RED);
                }

            } catch (Exception e) {

            }

        }else{
            txtYearProfit.setText("0.00 %");
        }
//
        txtNome.setText(ativo.getNome());
//		try {
//			GregorianCalendar g = new GregorianCalendar();
//			g.setTime(DateUtil.sqlDateFormat().parse(ativo.getData()));
//			ViewUtil.adjustDateOnTextView(txtData,g);
//			txtData.setText(txtData.getText().toString()+" - "+tipo); 
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
        btnNewEvent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageUtils.showAddAtivoFechamento(context, ativo, inflater, app.getDatabase(), listener);
            }
        });


        return view;
    }

    public static View createMovimentacaoAtivoItemView(final MovimentacaoAtivo ativo, final Context context, final MoneyControlApp app,
                                                       final LayoutInflater inflater, final DialogInterface.OnClickListener listener) {
        View view = inflater.inflate(R.layout.rentabilidade_ativo_list_item, null);

        TextView txtValor = (TextView) view.findViewById(R.id.ativoListItemTxtPrice);
        TextView txtData = (TextView) view.findViewById(R.id.transacaoListItemTxtData);

        txtValor.setText("R$ "+String.format("%.2f", ativo.getPatrimonio()));

        try {
            GregorianCalendar g = new GregorianCalendar();
            g.setTime(DateUtil.sqlDateFormat().parse(ativo.getData()));
            ViewUtil.adjustDateOnTextView(txtData,g);
            txtData.setText(txtData.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    public static View createOrcamentoItemView(Orcamento tr, MoneyControlApp app, LayoutInflater inflater){
        View view = inflater.inflate(R.layout.orcamento_list_item, null);

        TextView txtNome = (TextView) view.findViewById(R.id.orcamentoListItemTxtCategoria);
        TextView txtSaldo = (TextView) view.findViewById(R.id.orcamentoListItemTxtValorPlanejado);
        TextView txtRealizado = (TextView) view.findViewById(R.id.orcamentoListItemTxtValorRealizado);
        TextView txtRestante = (TextView) view.findViewById(R.id.orcamentoListItemTxtValorRestante);
        ProgressBar progress = (ProgressBar) view.findViewById(R.id.orcamentoListItemProgressBar);
//		progress.getProgressDrawable().setColorFilter(app.getResources().getColor(R.color.amber), Mode.SRC_IN);

//		String tipo = app.getDatabase().runQuery(QuerysUtil.checkTipoTransacao(tr.getId()));

        CategoriaTransacao c = app.getCategoriasTransacao().get(tr.getId_CategoriaTransacao());

        try {
            GregorianCalendar g = new GregorianCalendar();
            g.setTime(DateUtil.sqlDateFormat().parse(tr.getMes()));
            Cursor cur = app.getDatabase().runQueryCursor(QuerysUtil.reportCategoriaWithDateInterval(tr.getId_CategoriaTransacao(), g.get(Calendar.MONTH)+1, g.get(Calendar.YEAR)));
            if(cur.moveToFirst()){
                float total = cur.getFloat(0);
                float restante = tr.getValor()-total;
                txtRealizado.setText("R$ "+String.format("%.2f", total));
                txtRestante.setText("R$ "+String.format("%.2f", restante));
                if(restante < 0){
                    txtRestante.setTextColor(Color.RED);
                }
                int progressSize = (int) (total/tr.getValor()* 100);
                progress.setProgress(progressSize);
            }
            cur.close();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        txtNome.setText(c.getNome());
        txtSaldo.setText("R$ "+String.format("%.2f", tr.getValor()));

        return view;
    }

    public static View createContaItemView(final Context context, final Conta cc, final GregorianCalendar dateRange, final MoneyControlApp app, final LayoutInflater inflater,
                                           final List<Conta> contas, final ContaAdapter adapter){

        CartaoCredito cartao = null;

        View view = inflater.inflate(R.layout.conta_list_item, null);

        BootstrapCircleThumbnail imgIcon = (BootstrapCircleThumbnail) view.findViewById(R.id.contaListItemImgIcon);
        final TextView txtNome = (TextView) view.findViewById(R.id.contaListItemTxtNome);
        final TextView txtSaldo = (TextView) view.findViewById(R.id.contaListItemTxtSaldo);
        final TextView txtDebitos = (TextView) view.findViewById(R.id.contaListItemTxtDebitos);
        final TextView txtCreditos = (TextView) view.findViewById(R.id.contaListItemTxtCreditos);

        txtNome.setText(cc.getNome());
        try {
            imgIcon.setImage(AssetUtil.loadBitmapFromAsset(app, "icons/" + cc.getIcon()));
        } catch (Exception e) {
            System.out.println("icon: "+cc.getIcon());
            e.printStackTrace();
        }

        BootstrapButton btnEditConta = (BootstrapButton) view.findViewById(R.id.contaListItemBtnEditConta);


        Date range = dateRange.getTime();
        GregorianCalendar cartaoDateRange = null;

        String debitos = null;
        String creditos = null;
        float debitoSum = 0;
        float creditoSum = 0;

        if(cc.getId_TipoConta() == 4){

            cartao = app.getDatabase().select(CartaoCredito.class, " WHERE id_Conta = "+cc.getId()).get(0);
            cartaoDateRange = (GregorianCalendar)dateRange.clone();
            if(cartao.getDia_fechamento() <= cartao.getDia_vencimento())
                cartaoDateRange.add(GregorianCalendar.MONTH, -2);
            else
                cartaoDateRange.add(GregorianCalendar.MONTH, -3);
            cartaoDateRange.set(GregorianCalendar.DAY_OF_MONTH, cartao.getDia_fechamento());
            range = cartaoDateRange.getTime();
            cartaoDateRange.add(GregorianCalendar.MONTH, 1);

            btnEditConta.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CartaoCredito cartaoFinal = app.getDatabase().select(CartaoCredito.class, " WHERE id_Conta = "+cc.getId()).get(0);
                    MessageUtils.showEditCartaoCredito(inflater.getContext(), cc, cartaoFinal, inflater, app.getDatabase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            contas.clear();
                            contas.addAll(app.getDatabase().select(Conta.class));
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        }else{
            creditos = app.getDatabase().runQuery(QuerysUtil.sumTransacoesCreditoFromContaWithDateInterval(cc.getId(),range));
            if(creditos != null && creditos.length() > 0) creditoSum = Float.valueOf(creditos);
            btnEditConta.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageUtils.showEditConta(inflater.getContext(), cc, inflater, app.getDatabase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            contas.clear();
                            contas.addAll(app.getDatabase().select(Conta.class));
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        }

        debitos = app.getDatabase().runQuery(QuerysUtil.sumTransacoesDebitoFromContaWithDateInterval(cc.getId(),range));
        if(debitos != null && debitos.length() > 0)  debitoSum = Float.valueOf(debitos);


        String saldo = app.getDatabase().runQuery(QuerysUtil.computeSaldoFromContaBeforeDate(cc.getId(), cc.getId_TipoConta() == 4 ? cartaoDateRange.getTime() : range));
        float saldoAnterior = 0;
        if(saldo != null && saldo.length() > 0) saldoAnterior = Float.valueOf(saldo);


        if(cc.getId_TipoConta() == 4){
            final Fatura f1 = CartaoBusiness.computeFatura(app.getDatabase(), cartao, cartaoDateRange);

            final BootstrapButton btnPagarConta = (BootstrapButton) view.findViewById(R.id.contaListItemBtnPagarFatura);
            btnPagarConta.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    f1.setDate(dateRange);
                    MessageUtils.showPagarFatura(inflater.getContext(), inflater, app.getDatabase(), f1, cc, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            btnPagarConta.setVisibility(View.GONE);
                            txtSaldo.setText(R.string.contas_fragment_fatura_status_paga);
                        }
                    });
                }
            });

            txtCreditos.setText("limite: R$ "+String.format("%.2f",f1.getLimite()));
            txtDebitos.setText("fatura: R$ "+String.format("%.2f",f1.getValor()));

            switch (f1.getStatus()) {
                case PAGA:
                    txtSaldo.setText(R.string.contas_fragment_fatura_status_paga);
                    break;

                case PENDENTE:
                    btnPagarConta.setVisibility(View.VISIBLE);
                    txtSaldo.setText(R.string.contas_fragment_fatura_status_pendente);
                    break;

                case NENHUM:
                    txtSaldo.setText("-");
                    break;

                default:
                    break;
            }
        }else{
            txtCreditos.setText("R$ "+String.format("%.2f",creditoSum));
            txtDebitos.setText("R$ "+String.format("%.2f",debitoSum));
            txtSaldo.setText("R$ "+String.format("%.2f",(saldoAnterior+creditoSum-debitoSum)));
        }
        return view;
    }

    public static View createContaAPagarItemView(final Context context, final ContaAPagar tr, final GregorianCalendar range, final MoneyControlApp app, final LayoutInflater inflater,
                                                 final DialogInterface.OnClickListener listener){
        View view = inflater.inflate(R.layout.conta_a_pagar_list_item, null);

        TextView txtNome = (TextView) view.findViewById(R.id.contaAPagarListItemTxtDescricao);
        TextView txtSaldo = (TextView) view.findViewById(R.id.contaAPagarListItemTxtValor);
        TextView txtData = (TextView) view.findViewById(R.id.contaAPagarListItemTxtData);
        FontAwesomeText fontAwesome = (FontAwesomeText) view.findViewById(R.id.contaAPagarListItemFontAwe);

        final GregorianCalendar contaData = new GregorianCalendar();
        try {
            Log.d(TAG, "create conta " + tr.getDescricao() + " " + tr.getData());
            contaData.setTime(DateUtil.sqlDateFormat().parse(tr.getData()));
            contaData.set(Calendar.MONTH, range.get(Calendar.MONTH));
            contaData.set(Calendar.YEAR, range.get(Calendar.YEAR));
            ViewUtil.adjustDateOnTextView(txtData, contaData);
//			txtData.setText(txtData.getText().toString()+(c != null ? " - "+c.getNome() : ""));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        float valor = tr.getValor();

        String id = "";
        if(tr.getTipo() == ContaAPagar.Tipo.CARTAO_DE_CREDITO) {
            Fatura f = (Fatura) tr.getParams().get("fatura");
            if(f.getStatus() == Fatura.Status.PAGA){
                valor = f.getValorPago();
                id = "pago";
            }
        }else {
            id = app.getDatabase().runQuery(QuerysUtil.checkContaPagaOnDate(tr.getId(), contaData.getTime()));
        }

        GregorianCalendar now = new GregorianCalendar();

        boolean paid = id != null && !id.equals("");
        boolean delayed = now.compareTo(contaData) > 0 && now.get(Calendar.DAY_OF_MONTH) > contaData.get(Calendar.DAY_OF_MONTH);
//
//		CategoriaTransacao c = app.getCategoriasTransacao().get(tr.getId_CategoriaTransacao());
//
        if(!paid && !delayed) {
            txtSaldo.setTextColor(app.getResources().getColor(R.color.amber));
            fontAwesome.setTextColor(app.getResources().getColor(R.color.amber));
            fontAwesome.setIcon("fa-warning");
        }else if(!paid && delayed){
            txtSaldo.setTextColor(app.getResources().getColor(R.color.red));
            fontAwesome.setTextColor(app.getResources().getColor(R.color.red));
            fontAwesome.setIcon("fa-times");
        }else{
            if(tr.getTipo() == ContaAPagar.Tipo.NORMAL) {
                List<Transacao> list = app.getDatabase().select(Transacao.class, "WHERE id = " + id);
                if (list != null && !list.isEmpty())
                    valor = list.get(0).getValor();
            }
            txtSaldo.setTextColor(app.getResources().getColor(R.color.dark_green));
            fontAwesome.setTextColor(app.getResources().getColor(R.color.dark_green));
            fontAwesome.setIcon("fa-check");
            view.findViewById(R.id.contaAPagarListItemBtnPagarFatura).setVisibility(View.INVISIBLE);
        }
//
        txtNome.setText(tr.getDescricao());
        txtSaldo.setText("R$ " + String.format("%.2f", valor));

        BootstrapButton btn = (BootstrapButton) view.findViewById(R.id.contaAPagarListItemBtnPagarFatura);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tr.getTipo() == ContaAPagar.Tipo.NORMAL)
                    MessageUtils.showPagarConta(context, inflater, app.getDatabase(), tr, contaData,listener);
                else{
                    final Fatura f = (Fatura) tr.getParams().get("fatura");
                    Conta destino = app.getDatabase().select(Conta.class, "WHERE id = "+f.getCartao().getId_Conta()).get(0);
                    MessageUtils.showPagarFatura(context, inflater, app.getDatabase(), f, destino, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GregorianCalendar d = CartaoBusiness.computeRangeFatura(range, f.getCartao());
                            Fatura f2 = CartaoBusiness.computeFatura(app.getDatabase(), f.getCartao(), d);
                            f.setStatus(f2.getStatus());
                            f.setValorPago(f2.getValorPago());
                            listener.onClick(dialog, which);
                        }
                    });
                }
            }
        });


        return view;
    }

}
