package br.niltonvasques.moneycontrol.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.Ativo;
import br.niltonvasques.moneycontrol.database.bean.CartaoCredito;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.database.bean.Fatura;
import br.niltonvasques.moneycontrol.database.bean.MovimentacaoAtivo;
import br.niltonvasques.moneycontrol.database.bean.Orcamento;
import br.niltonvasques.moneycontrol.database.bean.TipoConta;
import br.niltonvasques.moneycontrol.database.bean.TipoTransacao;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.view.adapter.CategoriaChooseAdapter;
import br.niltonvasques.moneycontrol.view.adapter.IconeAdapter;
import br.niltonvasques.moneycontrolbeta.R;

@SuppressLint("NewApi")
public class MessageUtils {

	public interface MessageListener{
		public void onMessage(int result, Object data);
	}

	private static final String TAG = "[MessageUtils]";

	public static void showMessage(Context context, String title, String message){
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		dialog.create();
		dialog.show();
	}

	public static void showDefaultErrorMessage(Context context){
		MessageUtils.showMessage(context, context.getString(R.string.dialog_error_message_title), context.getString(R.string.dialog_error_message_message));
	}

	public static void showMessageYesNo(Context context, String title, String message, DialogInterface.OnClickListener listener){
		showMessageYesNo(context, title, message, listener, null);
	}

	public static void showMessageYesNo(Context context, String title, String message, DialogInterface.OnClickListener listener, DialogInterface.OnDismissListener dismissListener){
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setPositiveButton("Sim", listener);

		dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		dialog.setOnDismissListener(dismissListener);
		dialog.create();
		dialog.show();
	}

	public static void showTextDialog(final Context context, EditText input, DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setView(input);
		alert.setPositiveButton("Ok", listener);

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();        
	}

	public static void showAddConta(final Context context, final LayoutInflater inflater, final DatabaseHandler db, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.add_conta_dialog, null);
		alert.setView(view);

		final String[] icons = getIconsNameOnAsset(context);

		final Conta cc = new Conta();
		cc.setIcon("bb_icon.jpg");

		final ImageView imgIcon = (ImageView) view.findViewById(R.id.addContaDialogImgIcon);
		try {
			imgIcon.setImageDrawable(AssetUtil.loadDrawableFromAsset(context, "icons/"+icons[0]));
		} catch (IOException e) {
			e.printStackTrace();
		}
		imgIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(context, context.getString(R.string.add_conta_dialog_icon_choose_message), Toast.LENGTH_LONG).show();
				showIconesDialog(context, inflater, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							imgIcon.setImageDrawable(AssetUtil.loadDrawableFromAsset(context, "icons/"+icons[which]));
							cc.setIcon(icons[which]);
						}catch(Exception e){
							e.printStackTrace();
							MessageUtils.showDefaultErrorMessage(context);
						}
					}
				});
			}
		});

		HashMap<String, String> teste = new HashMap<String, String>();
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();

		Log.d(TAG, TipoConta.class.getSimpleName());

		List<TipoConta> tipos = db.select(TipoConta.class);

		final Spinner spinnerTipos = (Spinner) view.findViewById(R.id.addContaDialogSpinnerTipo);
		spinnerTipos.setAdapter(new ArrayAdapter<TipoConta>(context, android.R.layout.simple_list_item_1, tipos));
		spinnerTipos.setOnItemSelectedListener( new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				TipoConta tipo = (TipoConta)arg0.getItemAtPosition(position);
				if(tipo.getId() == 4){ //Cartão de Crédito
					view.findViewById(R.id.addContaDialogLayoutCartaoCredito).setVisibility(View.VISIBLE);
				}else{
					view.findViewById(R.id.addContaDialogLayoutCartaoCredito).setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});


		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					EditText editNome = (EditText) view.findViewById(R.id.addContaDialogEditTxtNome);
					EditText editSaldo = (EditText) view.findViewById(R.id.addContaDialogEditTxtSaldo);

					String ccNome = editNome.getText().toString();
					cc.setNome(ccNome);
					TipoConta tConta = (TipoConta) spinnerTipos.getSelectedItem();
					cc.setId_TipoConta(tConta.getId());

					float saldo = Float.valueOf(editSaldo.getText().toString());
					cc.setSaldo(saldo);

					db.insertConta(cc);

					if(cc.getId_TipoConta() == 4){
						EditText editTxtLimite = (EditText) view.findViewById(R.id.addContaDialogEditTxtLimite);
						Spinner spinnerFechamento = (Spinner) view.findViewById(R.id.addContaDialogSpinnerFechamento);
						Spinner spinnerVencimento = (Spinner) view.findViewById(R.id.addContaDialogSpinnerVencimento);
						float limite = 0;
						try{
							limite = Float.valueOf(editTxtLimite.getText().toString());
						}catch(Exception e){ }

						CartaoCredito cartao = new CartaoCredito();
						cartao.setId_Conta(cc.getId());
						cartao.setLimite(limite);
						cartao.setDia_fechamento(Integer.valueOf(Integer.valueOf((String)spinnerFechamento.getSelectedItem())));
						cartao.setDia_vencimento(Integer.valueOf(Integer.valueOf((String)spinnerVencimento.getSelectedItem())));

						db.insert(cartao);
					}
				}catch(Exception e){
					e.printStackTrace();
					MessageUtils.showDefaultErrorMessage(context);
				}

				listener.onClick(dialog, which);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});



		alert.show();        
	}

	public static void showEditConta(final Context context, final Conta conta, final LayoutInflater inflater, final DatabaseHandler db, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.edit_conta_dialog, null);
		alert.setView(view);

		final String[] icons = getIconsNameOnAsset(context);

		final ImageView imgIcon = (ImageView) view.findViewById(R.id.editContaDialogImgIcon);

		try {
			imgIcon.setImageDrawable(AssetUtil.loadDrawableFromAsset(context, "icons/"+conta.getIcon()));
		} catch (IOException e1) {
			try {
				imgIcon.setImageDrawable(AssetUtil.loadDrawableFromAsset(context, "icons/"+icons[0]));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		imgIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(context, context.getString(R.string.add_conta_dialog_icon_choose_message), Toast.LENGTH_LONG).show();
				showIconesDialog(context, inflater, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							imgIcon.setImageDrawable(AssetUtil.loadDrawableFromAsset(context, "icons/"+icons[which]));
							conta.setIcon(icons[which]);
						}catch(Exception e){
							e.printStackTrace();
							MessageUtils.showDefaultErrorMessage(context);
						}
					}
				});
			}
		});

		Log.d(TAG, TipoConta.class.getSimpleName());

		List<TipoConta> tipos = db.select(TipoConta.class);

		int pos = 0;
		for (TipoConta tipoConta : tipos) {
			if(tipoConta.getId() == conta.getId_TipoConta()) break;
			pos++;
		}

		final EditText editNome = (EditText) view.findViewById(R.id.editContaDialogEditTxtNome);
		editNome.setText(conta.getNome());
		final EditText editSaldo = (EditText) view.findViewById(R.id.editContaDialogEditTxtSaldo);
		editSaldo.setText(conta.getSaldo()+"");

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				try{
					String ccNome = editNome.getText().toString();
					conta.setNome(ccNome);

					float saldo = Float.valueOf(editSaldo.getText().toString());
					conta.setSaldo(saldo);
					db.update(conta);
				}catch(Exception e){
					e.printStackTrace();
					MessageUtils.showDefaultErrorMessage(context);
				}

				//				
				listener.onClick(dialog, which);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});

		alert.show();        
	}

	public static void showEditCartaoCredito(final Context context, final Conta conta, final CartaoCredito cartao, final LayoutInflater inflater, final DatabaseHandler db, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.edit_cartao_dialog, null);
		alert.setView(view);

		final String[] icons = getIconsNameOnAsset(context);

		final ImageView imgIcon = (ImageView) view.findViewById(R.id.editCartaoDialogImgIcon);

		try {
			imgIcon.setImageDrawable(AssetUtil.loadDrawableFromAsset(context, "icons/"+conta.getIcon()));
		} catch (IOException e1) {
			try {
				imgIcon.setImageDrawable(AssetUtil.loadDrawableFromAsset(context, "icons/"+icons[0]));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		imgIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(context,context.getString(R.string.add_conta_dialog_icon_choose_message), Toast.LENGTH_LONG).show();
				showIconesDialog(context, inflater, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							imgIcon.setImageDrawable(AssetUtil.loadDrawableFromAsset(context, "icons/"+icons[which]));
						} catch (IOException e) {
							e.printStackTrace();
						}
						conta.setIcon(icons[which]);
					}
				});
			}
		});

		final EditText editNome = (EditText) view.findViewById(R.id.editCartaoDialogEditTxtNome);
		editNome.setText(conta.getNome());
		final EditText editSaldo = (EditText) view.findViewById(R.id.editCartaoDialogEditTxtSaldo);
		editSaldo.setText(conta.getSaldo()+"");
		final EditText editTxtLimite = (EditText) view.findViewById(R.id.editCartaoDialogEditTxtLimite);
		final Spinner spinnerFechamento = (Spinner) view.findViewById(R.id.editCartaoDialogSpinnerFechamento);
		final Spinner spinnerVencimento = (Spinner) view.findViewById(R.id.editCartaoDialogSpinnerVencimento);

		editTxtLimite.setText(cartao.getLimite()+"");
		spinnerFechamento.setSelection(cartao.getDia_fechamento()-1);
		spinnerVencimento.setSelection(cartao.getDia_vencimento()-1);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				try{
					String ccNome = editNome.getText().toString();
					conta.setNome(ccNome);

					float saldo = Float.valueOf(editSaldo.getText().toString());
					conta.setSaldo(saldo);

					float limite = 0;
					limite = Float.valueOf(editTxtLimite.getText().toString());
					cartao.setLimite(limite);
					cartao.setDia_fechamento(Integer.valueOf(Integer.valueOf((String)spinnerFechamento.getSelectedItem())));
					cartao.setDia_vencimento(Integer.valueOf(Integer.valueOf((String)spinnerVencimento.getSelectedItem())));
					db.update(conta);
					db.update(cartao);

				}catch(Exception e){
					e.printStackTrace();
					MessageUtils.showDefaultErrorMessage(context);
				}							
				//				
				listener.onClick(dialog, which);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});

		alert.show();        
	}


	public static void showIconesDialog(final Context context, LayoutInflater inflater, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final AlertDialog alertDialog = alert.create();
		final View view = inflater.inflate(R.layout.icones_dialog, null);
		alertDialog.setView(view);

		final String[] icons = getIconsNameOnAsset(context);

		final GridView grid = (GridView) view.findViewById(R.id.iconesDialogGridView);
		grid.setTag(icons[0]);
		grid.setAdapter(new IconeAdapter(icons, inflater, context));

		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				grid.setTag(position);
				listener.onClick(alertDialog, (Integer)grid.getTag());
				alertDialog.cancel();
			}

		});


		//	    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		//			
		//			@Override
		//			public void onClick(DialogInterface dialog, int which) {
		//				listener.onClick(dialog, (Integer)grid.getTag());
		//			}
		//		});
		//
		//	    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		//	        public void onClick(DialogInterface dialog, int whichButton) {
		//	            dialog.cancel();
		//	        }
		//	    });


		alertDialog.show();        
	}

	private static String[] getIconsNameOnAsset(final Context context) {
		try {
			String[] iconss = context.getAssets().list("icons");
			for (String string : iconss) {
				Log.d(TAG, string);
			}
			return iconss;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressLint("NewApi")
	public static void showAddTransacao(final Context context, final LayoutInflater inflater, final DatabaseHandler db, int idConta, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.add_transacao_dialog, null);
		alert.setView(view);

		final GregorianCalendar value = new GregorianCalendar();
		final Button btnDate = (Button)view.findViewById(R.id.addTransacaoDialogBtnData);
		ViewUtil.adjustDateOnTextView(btnDate, value);

		final Transacao t = new Transacao();

		final List<TipoTransacao> tipos = db.select(TipoTransacao.class);
		final List<CategoriaTransacao> categorias = db.select(CategoriaTransacao.class, QuerysUtil.whereNoSystemCategorias(2));
		final List<Conta> contas = db.select(Conta.class);

		int startContaPos = 0;

		for(int i = 0; i < contas.size(); i++) {
			if(contas.get(i).getId() == idConta) {
				startContaPos = i;
				break;
			}
		}

		final Spinner spinnerTipos = (Spinner) view.findViewById(R.id.addTransacaoDialogSpinnerTipo);
		spinnerTipos.setAdapter(new ArrayAdapter<TipoTransacao>(context, android.R.layout.simple_list_item_1, tipos));
		spinnerTipos.setSelection(1);

		final Spinner spinnerCategoria = (Spinner) view.findViewById(R.id.addTransacaoDialogSpinnerCategoria);
		spinnerCategoria.setAdapter(new ArrayAdapter<CategoriaTransacao>(context, android.R.layout.simple_list_item_1, categorias));

		view.findViewById(R.id.addTransacaoDialogBtnAddCategoria).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MessageUtils.showAddCategoria(context, inflater, db, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						TipoTransacao tipo = (TipoTransacao)spinnerTipos.getSelectedItem();
						categorias.clear();
						categorias.addAll(db.select(CategoriaTransacao.class, QuerysUtil.whereNoSystemCategorias(tipo.getId())));
						((ArrayAdapter)spinnerCategoria.getAdapter()).notifyDataSetChanged();
					}
				});
			}
		});

		final Spinner spinnerContas = (Spinner) view.findViewById(R.id.addTransacaoDialogSpinnerConta);
		spinnerContas.setAdapter(new ArrayAdapter<Conta>(context, android.R.layout.simple_list_item_1, contas));
		spinnerContas.setSelection(startContaPos);

		spinnerTipos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
				TipoTransacao tipo = (TipoTransacao)spinnerTipos.getSelectedItem();
				categorias.clear();
				categorias.addAll(db.select(CategoriaTransacao.class, QuerysUtil.whereNoSystemCategorias(tipo.getId())));
				((ArrayAdapter)spinnerCategoria.getAdapter()).notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});


		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

					EditText editValor = (EditText) view.findViewById(R.id.addTransacaoDialogEditTxtValor);
					EditText editDescricao = (EditText) view.findViewById(R.id.addTransacaoDialogEditTxtDescrição);

					try{
						float valor = Float.valueOf(editValor.getText().toString());
						t.setValor(valor);
					}catch(Exception e){
						e.printStackTrace();
					}

					t.setDescricao(editDescricao.getText().toString());

					CategoriaTransacao cat = (CategoriaTransacao) spinnerCategoria.getSelectedItem();
					t.setId_CategoriaTransacao(cat.getId());

					Conta cc = (Conta) spinnerContas.getSelectedItem();
					t.setId_Conta(cc.getId());

					t.setData(format.format(value.getTime()));

					db.insert(t);
				}catch(Exception e){
					e.printStackTrace();
					MessageUtils.showDefaultErrorMessage(context);
				}

				listener.onClick(dialog, which);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});


		OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, monthOfYear);
				c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				value.set(Calendar.YEAR, year);
				value.set(Calendar.MONTH, monthOfYear);
				value.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				ViewUtil.adjustDateOnTextView(btnDate, value);

			}
		};

		final DatePickerDialog dateDialog = new DatePickerDialog(context, dateListener, 
				value.get(Calendar.YEAR), 
				value.get(Calendar.MONTH), 
				value.get(Calendar.DAY_OF_MONTH));

		btnDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dateDialog.show();				
			}
		});



		alert.show();        
	}

	@SuppressLint("NewApi")
	public static void showEditTransacao(final Context context, final Transacao t, final LayoutInflater inflater, final DatabaseHandler db, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.add_transacao_dialog, null);
		alert.setView(view);

		final GregorianCalendar value = new GregorianCalendar();
		try {
			Date date = DateUtil.sqlDateFormat().parse(t.getData());
			value.setTime(date);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		final TextView txtViewTitle = (TextView)view.findViewById(R.id.addTransacaoDialogTxtViewTitle);
		txtViewTitle.setText(R.string.edit_transacao_dialog_title);

		final Button btnDate = (Button)view.findViewById(R.id.addTransacaoDialogBtnData);
		ViewUtil.adjustDateOnTextView(btnDate, value);

		CategoriaTransacao c = db.select(CategoriaTransacao.class, " WHERE id = "+t.getId_CategoriaTransacao()).get(0);

		final List<TipoTransacao> tipos = db.select(TipoTransacao.class);
		final List<CategoriaTransacao> categorias = db.select(CategoriaTransacao.class, QuerysUtil.whereNoSystemCategorias(c.getId_TipoTransacao()));
		final List<Conta> contas = db.select(Conta.class);

		int startContaPos = 0;

		for(int i = 0; i < contas.size(); i++) {
			if(contas.get(i).getId() == t.getId_Conta()) {
				startContaPos = i;
				break;
			}
		}

		int startCategoria = 0;

		for(int i = 0; i < categorias.size(); i++) {
			if(categorias.get(i).getId() == t.getId_CategoriaTransacao()) {
				startCategoria = i;
				break;
			}
		}

		int startTipoTransacaoPos = 0;

		for(int i = 0; i < tipos.size(); i++) {
			if(tipos.get(i).getId() == c.getId_TipoTransacao()) {
				startTipoTransacaoPos = i;
				break;
			}
		}

		final Spinner spinnerTipos = (Spinner) view.findViewById(R.id.addTransacaoDialogSpinnerTipo);
		final Spinner spinnerCategoria = (Spinner) view.findViewById(R.id.addTransacaoDialogSpinnerCategoria);
		final Spinner spinnerContas = (Spinner) view.findViewById(R.id.addTransacaoDialogSpinnerConta);
		final Button btnAddCategory = (Button) 		    view.findViewById(R.id.addTransacaoDialogBtnAddCategoria);
		if(c.isSystem()){
			view.findViewById(R.id.addTransacaoDialogLayoutTipo).setVisibility(View.GONE);
			view.findViewById(R.id.addTransacaoDialogLayoutCategoria).setVisibility(View.GONE);
			view.findViewById(R.id.addTransacaoDialogLayoutConta).setVisibility(View.GONE);
		}else{
			spinnerTipos.setAdapter(new ArrayAdapter<TipoTransacao>(context, android.R.layout.simple_list_item_1, tipos));
			spinnerTipos.setSelection(startTipoTransacaoPos);

			spinnerCategoria.setAdapter(new ArrayAdapter<CategoriaTransacao>(context, android.R.layout.simple_list_item_1, categorias));
			spinnerCategoria.setSelection(startCategoria);

			spinnerContas.setAdapter(new ArrayAdapter<Conta>(context, android.R.layout.simple_list_item_1, contas));
			spinnerContas.setSelection(startContaPos);

			spinnerTipos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
					TipoTransacao tipo = (TipoTransacao)spinnerTipos.getSelectedItem();
					categorias.clear();
					categorias.addAll(db.select(CategoriaTransacao.class, QuerysUtil.whereNoSystemCategorias(tipo.getId())));
					((ArrayAdapter)spinnerCategoria.getAdapter()).notifyDataSetChanged();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			btnAddCategory.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MessageUtils.showAddCategoria(context, inflater, db, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							TipoTransacao tipo = (TipoTransacao)spinnerTipos.getSelectedItem();
							categorias.clear();
							categorias.addAll(db.select(CategoriaTransacao.class, QuerysUtil.whereNoSystemCategorias(tipo.getId())));
							((ArrayAdapter)spinnerCategoria.getAdapter()).notifyDataSetChanged();
						}
					});
				}
			});
		}





		final EditText editValor = (EditText) view.findViewById(R.id.addTransacaoDialogEditTxtValor);
		final EditText editDescricao = (EditText) view.findViewById(R.id.addTransacaoDialogEditTxtDescrição);
		editValor.setText(t.getValor()+"");
		editDescricao.setText(t.getDescricao());		

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");				

					try{
						float valor = Float.valueOf(editValor.getText().toString());
						t.setValor(valor);
					}catch(Exception e){
						e.printStackTrace();
					}

					t.setDescricao(editDescricao.getText().toString());

					CategoriaTransacao cat = (CategoriaTransacao) spinnerCategoria.getSelectedItem();
					if(cat != null){
						t.setId_CategoriaTransacao(cat.getId());					
					}

					Conta cc = (Conta) spinnerContas.getSelectedItem();
					if(cc != null){
						t.setId_Conta(cc.getId());					
					}

					t.setData(format.format(value.getTime()));

					db.update(t);
				}catch (Exception e){
					e.printStackTrace();
					MessageUtils.showDefaultErrorMessage(context);
				}
				listener.onClick(dialog, which);

			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});


		OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, monthOfYear);
				c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				value.set(Calendar.YEAR, year);
				value.set(Calendar.MONTH, monthOfYear);
				value.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				ViewUtil.adjustDateOnTextView(btnDate, value);

			}
		};

		final DatePickerDialog dateDialog = new DatePickerDialog(context, dateListener, 
				value.get(Calendar.YEAR), 
				value.get(Calendar.MONTH), 
				value.get(Calendar.DAY_OF_MONTH));

		btnDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dateDialog.show();				
			}
		});



		alert.show();        
	}

	public static void showAddCategoria(final Context context, final LayoutInflater inflater, final DatabaseHandler db, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.add_categoria_dialog, null);
		alert.setView(view);

		Log.d(TAG, TipoConta.class.getSimpleName());

		List<TipoTransacao> tipos = db.select(TipoTransacao.class);

		final Spinner spinnerTipos = (Spinner) view.findViewById(R.id.addCategoriaDialogSpinnerTipo);
		spinnerTipos.setAdapter(new ArrayAdapter<TipoTransacao>(context, android.R.layout.simple_list_item_1, tipos));


		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					EditText editNome = (EditText) view.findViewById(R.id.addCategoriaDialogEditTxtDescrição);

					CategoriaTransacao c = new CategoriaTransacao();

					c.setNome(editNome.getText().toString());

					TipoTransacao t = (TipoTransacao)spinnerTipos.getSelectedItem();
					c.setId_TipoTransacao(t.getId());

					db.insert(c);
				}catch(Exception e){
					e.printStackTrace();
					MessageUtils.showDefaultErrorMessage(context);
				}

				listener.onClick(dialog, which);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});

		alert.show();        
	}

	public static void showEditCategoria(final Context context, final CategoriaTransacao c, final LayoutInflater inflater, final DatabaseHandler db, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.add_categoria_dialog, null);
		alert.setView(view);

		Log.d(TAG, TipoConta.class.getSimpleName());

		List<TipoTransacao> tipos = db.select(TipoTransacao.class);

		int startTipos = 0;
		for(int i = 0; i < tipos.size(); i++){
			if(tipos.get(i).getId() == c.getId_TipoTransacao()){
				startTipos = i;
				break;
			}
		}

		final Spinner spinnerTipos = (Spinner) view.findViewById(R.id.addCategoriaDialogSpinnerTipo);
		spinnerTipos.setAdapter(new ArrayAdapter<TipoTransacao>(context, android.R.layout.simple_list_item_1, tipos));
		spinnerTipos.setSelection(startTipos);

		final EditText editNome = (EditText) view.findViewById(R.id.addCategoriaDialogEditTxtDescrição);

		editNome.setText(c.getNome());

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				try{
					c.setNome(editNome.getText().toString());

					TipoTransacao t = (TipoTransacao)spinnerTipos.getSelectedItem();
					c.setId_TipoTransacao(t.getId());

					db.update(c);
				}catch(Exception e){
					e.printStackTrace();
					MessageUtils.showDefaultErrorMessage(context);
				}

				listener.onClick(dialog, which);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});

		alert.show();        
	}

	@SuppressLint("NewApi")
	public static void showTransferencia(final Context context, final LayoutInflater inflater, final DatabaseHandler db, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.transferencia_dialog, null);
		alert.setView(view);

		final GregorianCalendar value = new GregorianCalendar();
		final Button btnDate = (Button)view.findViewById(R.id.transferenciaDialogBtnData);
		ViewUtil.adjustDateOnTextView(btnDate, value);

		final Transacao tCredito = new Transacao();
		final Transacao tDebito = new Transacao();

		final CategoriaTransacao transfCatDebito = db.select(CategoriaTransacao.class, QuerysUtil.whereTransacaoTransferenciaDespesa()).get(0);
		final CategoriaTransacao transfCatCredito = db.select(CategoriaTransacao.class, QuerysUtil.whereTransacaoTransferenciaReceita()).get(0);

		final List<Conta> contas = db.select(Conta.class);

		final Spinner spinnerContasOrigem = (Spinner) view.findViewById(R.id.transferenciaDialogSpinnerContaOrigem);
		spinnerContasOrigem.setAdapter(new ArrayAdapter<Conta>(context, android.R.layout.simple_list_item_1, contas));
		spinnerContasOrigem.setSelection(0);

		final Spinner spinnerContasDestino = (Spinner) view.findViewById(R.id.transferenciaDialogSpinnerContaDestino);
		spinnerContasDestino.setAdapter(new ArrayAdapter<Conta>(context, android.R.layout.simple_list_item_1, contas));
		spinnerContasDestino.setSelection(1);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					EditText editValor = (EditText) view.findViewById(R.id.transferenciaDialogEditTxtValor);

					float valor = Float.valueOf(editValor.getText().toString());
					tCredito.setValor(valor);
					tDebito.setValor(valor);

					Conta cDebito = (Conta) spinnerContasOrigem.getSelectedItem();
					tDebito.setId_Conta(cDebito.getId());

					Conta cCredito = (Conta) spinnerContasDestino.getSelectedItem();
					tCredito.setId_Conta(cCredito.getId());
					//				
					tCredito.setDescricao("Transferência de "+cDebito.getNome());
					tDebito.setDescricao("Transferência p/ "+cCredito.getNome());

					tCredito.setId_CategoriaTransacao(transfCatCredito.getId());
					tDebito.setId_CategoriaTransacao(transfCatDebito.getId());

					tCredito.setData(format.format(value.getTime()));
					tDebito.setData(format.format(value.getTime()));

					db.insert(tCredito);
					db.insert(tDebito);
				}catch(Exception e){
					e.printStackTrace();
					MessageUtils.showDefaultErrorMessage(context);
				}

				listener.onClick(dialog, which);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});


		OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, monthOfYear);
				c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				value.set(Calendar.YEAR, year);
				value.set(Calendar.MONTH, monthOfYear);
				value.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				ViewUtil.adjustDateOnTextView(btnDate, value);

			}
		};

		final DatePickerDialog dateDialog = new DatePickerDialog(context, dateListener, 
				value.get(Calendar.YEAR), 
				value.get(Calendar.MONTH), 
				value.get(Calendar.DAY_OF_MONTH));
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			dateDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
		}

		btnDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dateDialog.show();				
			}
		});



		alert.show();        
	}

	@SuppressLint("NewApi")
	public static void showPagarFatura(final Context context, final LayoutInflater inflater, final DatabaseHandler db, final Fatura fatura, 
			final Conta destino, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.pagar_fatura_dialog, null);
		alert.setView(view);

		final GregorianCalendar value = new GregorianCalendar();
		value.set(Calendar.DAY_OF_MONTH, fatura.getCartao().getDia_vencimento());
		value.set(Calendar.MONTH, fatura.getDate().get(Calendar.MONTH));	    
		final Button btnDate = (Button)view.findViewById(R.id.transferenciaDialogBtnData);
		ViewUtil.adjustDateOnTextView(btnDate, value);

		final Transacao tCredito = new Transacao();
		final Transacao tDebito = new Transacao();

		final CategoriaTransacao transfCatDebito = db.select(CategoriaTransacao.class, QuerysUtil.whereTransacaoTransferenciaDespesa()).get(0);
		final CategoriaTransacao transfCatCredito = db.select(CategoriaTransacao.class, QuerysUtil.whereTransacaoTransferenciaReceita()).get(0);

		final List<Conta> contas = db.select(Conta.class);

		final Spinner spinnerContasOrigem = (Spinner) view.findViewById(R.id.transferenciaDialogSpinnerContaOrigem);
		spinnerContasOrigem.setAdapter(new ArrayAdapter<Conta>(context, android.R.layout.simple_list_item_1, contas));
		spinnerContasOrigem.setSelection(0);
		final EditText editValor = (EditText) view.findViewById(R.id.transferenciaDialogEditTxtValor);
		editValor.setText(fatura.getValor()+"");

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				try{
					float valor = Float.valueOf(editValor.getText().toString());
					tCredito.setValor(valor);
					tDebito.setValor(valor);

					Conta cDebito = (Conta) spinnerContasOrigem.getSelectedItem();
					tDebito.setId_Conta(cDebito.getId());

					tCredito.setId_Conta(destino.getId());
					//				
					tCredito.setDescricao("Pago com "+cDebito.getNome());
					tDebito.setDescricao("Pagamento da fatura: "+destino.getNome());

					tCredito.setId_CategoriaTransacao(transfCatCredito.getId());
					tDebito.setId_CategoriaTransacao(transfCatDebito.getId());

					tCredito.setData(format.format(value.getTime()));
					tDebito.setData(format.format(value.getTime()));

					db.insert(tCredito);
					db.insert(tDebito);
				}catch(Exception e){
					e.printStackTrace();
					MessageUtils.showDefaultErrorMessage(context);
				}

				listener.onClick(dialog, which);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});


		OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, monthOfYear);
				c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				value.set(Calendar.YEAR, year);
				value.set(Calendar.MONTH, monthOfYear);
				value.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				ViewUtil.adjustDateOnTextView(btnDate, value);

			}
		};

		final DatePickerDialog dateDialog = new DatePickerDialog(context, dateListener, 
				value.get(Calendar.YEAR), 
				value.get(Calendar.MONTH), 
				value.get(Calendar.DAY_OF_MONTH));
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			dateDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
		}

		btnDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dateDialog.show();				
			}
		});



		alert.show();        
	}

	public static void showCategoriasChooseDialog(final Context context, final LayoutInflater inflater, final DatabaseHandler db, final MessageListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.report_categorias_by_month_dialog, null);
		alert.setView(view);

		Log.d(TAG, TipoConta.class.getSimpleName());

		List<CategoriaTransacao> tipos = db.select(CategoriaTransacao.class);

		final ListView spinnerTipos = (ListView) view.findViewById(R.id.reportCategoriasByMonthListViewCategorias);
		final CategoriaChooseAdapter adapter = new CategoriaChooseAdapter(tipos, inflater);
		spinnerTipos.setAdapter(adapter);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.onMessage(0, adapter.getCategoriasSelected());
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});

		alert.show();        
	}

	@SuppressLint("NewApi")
	public static void showAddMovimentacaoAtivo(final Context context, final LayoutInflater inflater, final DatabaseHandler db, final int idConta, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.add_ativo_dialog, null);
		alert.setView(view);

		final GregorianCalendar data = new GregorianCalendar();
		final Button btnDate = (Button)view.findViewById(R.id.addAtivoDialogBtnData);
		ViewUtil.adjustDateOnTextView(btnDate, data);

		final MovimentacaoAtivo movimentacaoAtivo = new MovimentacaoAtivo();

		final List<Ativo> tipos = db.select(Ativo.class);

		final Spinner spinnerTipos = (Spinner) view.findViewById(R.id.addAtivoDialogSpinnerTipo);
		spinnerTipos.setAdapter(new ArrayAdapter<Ativo>(context, android.R.layout.simple_list_item_1, tipos));

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

					EditText editValor = (EditText) view.findViewById(R.id.addAtivoDialogEditTxtMovimentacao);
					EditText editFinanceiro = (EditText) view.findViewById(R.id.addAtivoDialogEditTxtFinanceiro);

					Ativo ativo = (Ativo) spinnerTipos.getSelectedItem();

					float movimentacao = 0;
					try{
						movimentacao = Float.valueOf(editValor.getText().toString());
					}catch(NumberFormatException n){
					}
					movimentacaoAtivo.setMovimentacao(movimentacao);

					float financeiro = 0;
					try{
						financeiro = Float.valueOf(editFinanceiro.getText().toString());
					}catch(NumberFormatException n){
					}
					movimentacaoAtivo.setFinanceiro(financeiro);

					float lastPatrimonio = 0;
					float valorCota = 1f;
					float cotas = 0; 
					List<MovimentacaoAtivo> movimentacaoAtivoLast = db.select(MovimentacaoAtivo.class, QuerysUtil.whereLastMovimentacaoAtivo(ativo.getId(), data.getTime()));
					if(!movimentacaoAtivoLast.isEmpty()){
						MovimentacaoAtivo lastMovimentacao = movimentacaoAtivoLast.get(0);
						lastPatrimonio = lastMovimentacao.getPatrimonio();
						if(lastMovimentacao.getCotas() != 0){
							valorCota = (lastPatrimonio + financeiro ) / lastMovimentacao.getCotas();
							cotas = lastMovimentacao.getCotas();
						}			
					}
					float cotasEmitidas = movimentacao/valorCota;
					cotas += cotasEmitidas;

					float total = financeiro + lastPatrimonio;

					movimentacaoAtivo.setCotas_emitidas(cotasEmitidas);
					movimentacaoAtivo.setCotas(cotas);
					movimentacaoAtivo.setPatrimonio(total + movimentacao);

					movimentacaoAtivo.setData(format.format(data.getTime()));
					movimentacaoAtivo.setId_Ativo(ativo.getId());

					//Se não houve movimentação no período não precisa adicionar transação
					if(movimentacaoAtivo.getMovimentacao() != 0){
						Transacao t = new Transacao();
						t.setData(format.format(data.getTime()));
						t.setDescricao("Investimento -> "+ativo.getNome());
						t.setId_Conta(idConta);
						if(movimentacaoAtivo.getMovimentacao() > 0){
							t.setId_CategoriaTransacao(db.select(CategoriaTransacao.class, " WHERE nome = 'Investimento' AND system = 1 AND id_TipoTransacao = 2").get(0).getId());
							t.setValor(movimentacaoAtivo.getMovimentacao());
						}else{
							t.setId_CategoriaTransacao(db.select(CategoriaTransacao.class, " WHERE nome = 'Investimento' AND system = 1 AND id_TipoTransacao = 1").get(0).getId());
							t.setValor(movimentacaoAtivo.getMovimentacao()*(-1));
						}

						if(db.insert(t)){
							db.insert(movimentacaoAtivo);
						}else{
							MessageUtils.showMessage(context, "Erro!", context.getString(R.string.add_ativo_dialog_message_error));
						}
					}else{
						db.insert(movimentacaoAtivo);
					}

				}catch(Exception e){
					e.printStackTrace();
					MessageUtils.showDefaultErrorMessage(context);
				}


				listener.onClick(dialog, which);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});


		OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, monthOfYear);
				c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				data.set(Calendar.YEAR, year);
				data.set(Calendar.MONTH, monthOfYear);
				data.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				ViewUtil.adjustDateOnTextView(btnDate, data);

			}
		};

		final DatePickerDialog dateDialog = new DatePickerDialog(context, dateListener, 
				data.get(Calendar.YEAR), 
				data.get(Calendar.MONTH), 
				data.get(Calendar.DAY_OF_MONTH));
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			dateDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
		}

		btnDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dateDialog.show();				
			}
		});

		alert.show();        
	}


	public static void showAddAtivoFechamento(final Context context, final Ativo ativo, final LayoutInflater inflater, final DatabaseHandler db, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.add_fechamento_ativo_dialog, null);
		alert.setView(view);

		final GregorianCalendar data = new GregorianCalendar();
		final Button btnDate = (Button)view.findViewById(R.id.addAtivoDialogBtnData);
		ViewUtil.adjustDateOnTextView(btnDate, data);

		final MovimentacaoAtivo movimentacaoAtivo = new MovimentacaoAtivo();

		final List<Ativo> tipos = db.select(Ativo.class);

		int startAtivoPos = 0;
		for(int i = 0; i < tipos.size(); i++){
			if(ativo.getId() == tipos.get(i).getId()){
				startAtivoPos = i;
				break;
			}
		}

		final Spinner spinnerTipos = (Spinner) view.findViewById(R.id.addAtivoDialogSpinnerTipo);
		spinnerTipos.setAdapter(new ArrayAdapter<Ativo>(context, android.R.layout.simple_list_item_1, tipos));
		spinnerTipos.setSelection(startAtivoPos);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

					EditText editFechamento = (EditText) view.findViewById(R.id.addFechamentoAtivoDialogEditTxtFechamento);

					Ativo ativo = (Ativo) spinnerTipos.getSelectedItem();
					float movimentacao = 0;
					movimentacaoAtivo.setMovimentacao(movimentacao);

					float financeiro = 0;
					float fechamento = 0;
					try{
						fechamento = Float.valueOf(editFechamento.getText().toString());
					}catch(NumberFormatException n){
					}

					float lastPatrimonio = 0;
					float valorCota = 1f;
					float cotas = 0; 
					List<MovimentacaoAtivo> movimentacaoAtivoLast = db.select(MovimentacaoAtivo.class, QuerysUtil.whereLastMovimentacaoAtivo(ativo.getId(), data.getTime()));
					if(!movimentacaoAtivoLast.isEmpty()){
						MovimentacaoAtivo lastMovimentacao = movimentacaoAtivoLast.get(0);
						lastPatrimonio = lastMovimentacao.getPatrimonio();
						financeiro = fechamento - lastPatrimonio;
						if(lastMovimentacao.getCotas() != 0){
							valorCota = (lastPatrimonio + financeiro ) / lastMovimentacao.getCotas();
							cotas = lastMovimentacao.getCotas();
						}			
					}

					movimentacaoAtivo.setFinanceiro(financeiro);

					float cotasEmitidas = movimentacao/valorCota;
					cotas += cotasEmitidas;

					float total = financeiro + lastPatrimonio;

					movimentacaoAtivo.setCotas_emitidas(cotasEmitidas);
					movimentacaoAtivo.setCotas(cotas);
					movimentacaoAtivo.setPatrimonio(total + movimentacao);
					movimentacaoAtivo.setData(format.format(data.getTime()));
					movimentacaoAtivo.setId_Ativo(ativo.getId());

					db.insert(movimentacaoAtivo);
				}catch(Exception e){
					e.printStackTrace();
					MessageUtils.showDefaultErrorMessage(context);
				}


				listener.onClick(dialog, which);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});


		OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, monthOfYear);
				c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				data.set(Calendar.YEAR, year);
				data.set(Calendar.MONTH, monthOfYear);
				data.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				ViewUtil.adjustDateOnTextView(btnDate, data);

			}
		};

		final DatePickerDialog dateDialog = new DatePickerDialog(context, dateListener, 
				data.get(Calendar.YEAR), 
				data.get(Calendar.MONTH), 
				data.get(Calendar.DAY_OF_MONTH));
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			dateDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
		}

		btnDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dateDialog.show();				
			}
		});

		alert.show();       
	}

	public static void showAddOrcamento(final Context context, final LayoutInflater inflater, final DatabaseHandler db, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.add_orcamento_dialog, null);
		alert.setView(view);

		final GregorianCalendar value = new GregorianCalendar();
		final Button btnDate = (Button)view.findViewById(R.id.orcamentoDialogBtnData);
		ViewUtil.adjustMonthTextView(btnDate, value);

		Log.d(TAG, TipoConta.class.getSimpleName());

		List<CategoriaTransacao> tipos = db.select(CategoriaTransacao.class, "WHERE id_TipoTransacao = 2");

		final Spinner spinnerTipos = (Spinner) view.findViewById(R.id.addOrcamentoDialogSpinnerTipo);
		spinnerTipos.setAdapter(new ArrayAdapter<CategoriaTransacao>(context, android.R.layout.simple_list_item_1, tipos));


		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					EditText editNome = (EditText) view.findViewById(R.id.addOrcamentoDialogEditTxtValor);

					Orcamento c = new Orcamento();

					try{
						float valor = Float.valueOf(editNome.getText().toString());
						c.setValor(valor);
					}catch(Exception e){
					}

					c.setMes(format.format(value.getTime()));

					CategoriaTransacao t = (CategoriaTransacao)spinnerTipos.getSelectedItem();
					c.setId_CategoriaTransacao(t.getId());

					if(db.runQuery(QuerysUtil.checkExistsOrcamentoOnMonth(t.getId(), value.getTime())).equals("0") ){
						db.insert(c);				
					}else{
						MessageUtils.showMessage(context, context.getString(R.string.orcamento_dialog_error_message_title), context.getString(R.string.orcamento_dialog_error_message_message));
					}
				}catch(Exception e){
					e.printStackTrace();
					MessageUtils.showDefaultErrorMessage(context);
				}

				listener.onClick(dialog, which);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});

		OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, monthOfYear);
				c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				value.set(Calendar.YEAR, year);
				value.set(Calendar.MONTH, monthOfYear);
				value.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				ViewUtil.adjustMonthTextView(btnDate, value);

			}
		};

		final DatePickerDialog dateDialog = customDatePicker(context, dateListener, 
				value.get(Calendar.YEAR), 
				value.get(Calendar.MONTH), 
				value.get(Calendar.DAY_OF_MONTH));

		btnDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dateDialog.show();				
			}
		});

		alert.show();        
	}

	public static void showEditOrcamento(final Context context, final Orcamento c, final LayoutInflater inflater, final DatabaseHandler db, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.add_orcamento_dialog, null);
		alert.setView(view);

		final GregorianCalendar value = new GregorianCalendar();
		try {
			Date date = DateUtil.sqlDateFormat().parse(c.getMes());
			value.setTime(date);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		final Button btnDate = (Button)view.findViewById(R.id.orcamentoDialogBtnData);
		ViewUtil.adjustMonthTextView(btnDate, value);

		Log.d(TAG, TipoConta.class.getSimpleName());

		List<CategoriaTransacao> tipos = db.select(CategoriaTransacao.class, "WHERE id_TipoTransacao = 2");

		int startCat = 0;
		for(int i = 0; i < tipos.size(); i++){
			if(tipos.get(i).getId() == c.getId_CategoriaTransacao()) {
				startCat = i;
				break;
			}
		}

		final Spinner spinnerTipos = (Spinner) view.findViewById(R.id.addOrcamentoDialogSpinnerTipo);
		spinnerTipos.setAdapter(new ArrayAdapter<CategoriaTransacao>(context, android.R.layout.simple_list_item_1, tipos));
		spinnerTipos.setSelection(startCat);

		final EditText editNome = (EditText) view.findViewById(R.id.addOrcamentoDialogEditTxtValor);
		editNome.setText(c.getValor()+"");


		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

					float valor = Float.valueOf(editNome.getText().toString());
					c.setValor(valor);


					c.setMes(format.format(value.getTime()));

					CategoriaTransacao t = (CategoriaTransacao)spinnerTipos.getSelectedItem();
					c.setId_CategoriaTransacao(t.getId());

					db.update(c);
				}catch(Exception e){
					e.printStackTrace();
					MessageUtils.showDefaultErrorMessage(context);
				}

				listener.onClick(dialog, which);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});

		OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, monthOfYear);
				c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				value.set(Calendar.YEAR, year);
				value.set(Calendar.MONTH, monthOfYear);
				value.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				ViewUtil.adjustMonthTextView(btnDate, value);

			}
		};

		final DatePickerDialog dateDialog = customDatePicker(context, dateListener, 
				value.get(Calendar.YEAR), 
				value.get(Calendar.MONTH), 
				value.get(Calendar.DAY_OF_MONTH));

		btnDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dateDialog.show();				
			}
		});

		alert.show();        
	}

	private static DatePickerDialog customDatePicker(Context context, OnDateSetListener dateSetListener, int year, int month, int day) {
		DatePickerDialog dpd = new DatePickerDialog(context, dateSetListener,year, month, day);
		try {

			Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
			for (Field datePickerDialogField : datePickerDialogFields) {
				if (datePickerDialogField.getName().equals("mDatePicker")) {
					datePickerDialogField.setAccessible(true);
					DatePicker datePicker = (DatePicker) datePickerDialogField
							.get(dpd);
					Field datePickerFields[] = datePickerDialogField.getType()
							.getDeclaredFields();
					for (Field datePickerField : datePickerFields) {
						if ("mDayPicker".equals(datePickerField.getName())
								|| "mDaySpinner".equals(datePickerField
										.getName())) {
							datePickerField.setAccessible(true);
							Object dayPicker = new Object();
							dayPicker = datePickerField.get(datePicker);
							((View) dayPicker).setVisibility(View.GONE);
						}
					}
				}

			}
		} catch (Exception ex) {
		}
		return dpd;
	}

}
