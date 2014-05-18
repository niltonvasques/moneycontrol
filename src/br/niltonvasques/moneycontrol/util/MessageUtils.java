package br.niltonvasques.moneycontrol.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import android.widget.Toast;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.DatabaseUtil;
import br.niltonvasques.moneycontrol.database.bean.Ativo;
import br.niltonvasques.moneycontrol.database.bean.CartaoCredito;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.database.bean.RentabilidadeAtivo;
import br.niltonvasques.moneycontrol.database.bean.TipoAtivo;
import br.niltonvasques.moneycontrol.database.bean.TipoConta;
import br.niltonvasques.moneycontrol.database.bean.TipoTransacao;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.view.adapter.CategoriaChooseAdapter;
import br.niltonvasques.moneycontrol.view.adapter.IconeAdapter;

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
	
	public static void showMessageYesNo(Context context, String title, String message, DialogInterface.OnClickListener listener){
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setPositiveButton("Sim", listener);
		
		dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
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
				Toast.makeText(context, "Escolha um ícone!", Toast.LENGTH_LONG).show();
				showIconesDialog(context, inflater, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							imgIcon.setImageDrawable(AssetUtil.loadDrawableFromAsset(context, "icons/"+icons[which]));
						} catch (IOException e) {
							e.printStackTrace();
						}
						cc.setIcon(icons[which]);
					}
				});
			}
		});
	    
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
				EditText editNome = (EditText) view.findViewById(R.id.addContaDialogEditTxtNome);
				EditText editSaldo = (EditText) view.findViewById(R.id.addContaDialogEditTxtSaldo);

				String ccNome = editNome.getText().toString();
				cc.setNome(ccNome);
				TipoConta tConta = (TipoConta) spinnerTipos.getSelectedItem();
				cc.setId_TipoConta(tConta.getId());
				
				try{
					float saldo = Float.valueOf(editSaldo.getText().toString());
					cc.setSaldo(saldo);
				}catch(Exception e){
					
				}
				
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
		final View view = inflater.inflate(R.layout.add_conta_dialog, null);
	    alert.setView(view);
	    
	    final String[] icons = getIconsNameOnAsset(context);
	    
	    final ImageView imgIcon = (ImageView) view.findViewById(R.id.addContaDialogImgIcon);
	    
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
				Toast.makeText(context, "Escolha um ícone!", Toast.LENGTH_LONG).show();
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
	    
	    Log.d(TAG, TipoConta.class.getSimpleName());
	    
	    List<TipoConta> tipos = db.select(TipoConta.class);
	    
	    int pos = 0;
	    for (TipoConta tipoConta : tipos) {
			if(tipoConta.getId() == conta.getId_TipoConta()) break;
			pos++;
		}
	    
	    final Spinner spinnerTipos = (Spinner) view.findViewById(R.id.addContaDialogSpinnerTipo);
	    spinnerTipos.setAdapter(new ArrayAdapter<TipoConta>(context, android.R.layout.simple_list_item_1, tipos));
	    spinnerTipos.setSelection(pos);
	    
	    final EditText editNome = (EditText) view.findViewById(R.id.addContaDialogEditTxtNome);
	    editNome.setText(conta.getNome());
	    final EditText editSaldo = (EditText) view.findViewById(R.id.addContaDialogEditTxtSaldo);
	    editSaldo.setText(conta.getSaldo()+"");
	    
	    
	    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String ccNome = editNome.getText().toString();
				conta.setNome(ccNome);
				TipoConta tConta = (TipoConta) spinnerTipos.getSelectedItem();
				conta.setId_TipoConta(tConta.getId());
				
				try{
					float saldo = Float.valueOf(editSaldo.getText().toString());
					conta.setSaldo(saldo);
				}catch(Exception e){
					
				}
				
				db.update(conta);
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
	    final List<CategoriaTransacao> categorias = db.select(CategoriaTransacao.class, "WHERE id_TipoTransacao = "+2);
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
			    		categorias.addAll(db.select(CategoriaTransacao.class, "WHERE id_TipoTransacao = "+tipo.getId()));
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
	    		categorias.addAll(db.select(CategoriaTransacao.class, "WHERE id_TipoTransacao = "+tipo.getId()));
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
	    final Button btnDate = (Button)view.findViewById(R.id.addTransacaoDialogBtnData);
	    ViewUtil.adjustDateOnTextView(btnDate, value);
	    
	    CategoriaTransacao c = db.select(CategoriaTransacao.class, " WHERE id = "+t.getId_CategoriaTransacao()).get(0);
	    
	    final List<TipoTransacao> tipos = db.select(TipoTransacao.class);
	    final List<CategoriaTransacao> categorias = db.select(CategoriaTransacao.class, "WHERE id_TipoTransacao = "+c.getId_TipoTransacao());
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
	    spinnerTipos.setAdapter(new ArrayAdapter<TipoTransacao>(context, android.R.layout.simple_list_item_1, tipos));
	    spinnerTipos.setSelection(startTipoTransacaoPos);
	    
	    final Spinner spinnerCategoria = (Spinner) view.findViewById(R.id.addTransacaoDialogSpinnerCategoria);
	    spinnerCategoria.setAdapter(new ArrayAdapter<CategoriaTransacao>(context, android.R.layout.simple_list_item_1, categorias));
	    spinnerCategoria.setSelection(startCategoria);
	    
	    view.findViewById(R.id.addTransacaoDialogBtnAddCategoria).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MessageUtils.showAddCategoria(context, inflater, db, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						TipoTransacao tipo = (TipoTransacao)spinnerTipos.getSelectedItem();
			    		categorias.clear();
			    		categorias.addAll(db.select(CategoriaTransacao.class, "WHERE id_TipoTransacao = "+tipo.getId()));
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
	    		categorias.addAll(db.select(CategoriaTransacao.class, "WHERE id_TipoTransacao = "+tipo.getId()));
	    		((ArrayAdapter)spinnerCategoria.getAdapter()).notifyDataSetChanged();
	    	}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    final EditText editValor = (EditText) view.findViewById(R.id.addTransacaoDialogEditTxtValor);
		final EditText editDescricao = (EditText) view.findViewById(R.id.addTransacaoDialogEditTxtDescrição);
		editValor.setText(t.getValor()+"");
		editDescricao.setText(t.getDescricao());
		
	    
	    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				
				
				
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
				
				db.update(t);
				
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
				EditText editNome = (EditText) view.findViewById(R.id.addCategoriaDialogEditTxtDescrição);
				
				CategoriaTransacao c = new CategoriaTransacao();
				
				c.setNome(editNome.getText().toString());
				
				TipoTransacao t = (TipoTransacao)spinnerTipos.getSelectedItem();
				c.setId_TipoTransacao(t.getId());
				
				db.insert(c);				
				
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
	    
	    final CategoriaTransacao transfCatDebito = db.select(CategoriaTransacao.class, "WHERE system = 1 AND id_TipoTransacao = "+2).get(0);
	    final CategoriaTransacao transfCatCredito = db.select(CategoriaTransacao.class, "WHERE system = 1 AND id_TipoTransacao = "+1).get(0);
	    
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
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				EditText editValor = (EditText) view.findViewById(R.id.transferenciaDialogEditTxtValor);
				
				try{
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
					MessageUtils.showMessage(context, context.getResources().getString(R.string.error_title), context.getResources().getString(R.string.error_message));
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
	public static void showAddAtivo(final Context context, final LayoutInflater inflater, final DatabaseHandler db, int idConta, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.add_ativo_dialog, null);
	    alert.setView(view);
	    
	    final GregorianCalendar data = new GregorianCalendar();
	    final GregorianCalendar vencimento = new GregorianCalendar();
	    final Button btnDate = (Button)view.findViewById(R.id.addAtivoDialogBtnData);
	    final Button btnVencimento = (Button)view.findViewById(R.id.addAtivoDialogBtnVencimento);
	    ViewUtil.adjustDateOnTextView(btnDate, data);
	    ViewUtil.adjustDateOnTextView(btnVencimento, vencimento);
	    
	    final Ativo ativo = new Ativo();
	    
	    final List<TipoAtivo> tipos = db.select(TipoAtivo.class);
	    final List<Conta> contas = db.select(Conta.class);
	    
	    int startContaPos = 0;
	    
	    for(int i = 0; i < contas.size(); i++) {
	    	if(contas.get(i).getId() == idConta) {
	    		startContaPos = i;
	    		break;
	    	}
	    }
	    
	    final Spinner spinnerTipos = (Spinner) view.findViewById(R.id.addAtivoDialogSpinnerTipo);
	    spinnerTipos.setAdapter(new ArrayAdapter<TipoAtivo>(context, android.R.layout.simple_list_item_1, tipos));
	    spinnerTipos.setSelection(1);
	    
	    final Spinner spinnerContas = (Spinner) view.findViewById(R.id.addAtivoDialogSpinnerConta);
	    spinnerContas.setAdapter(new ArrayAdapter<Conta>(context, android.R.layout.simple_list_item_1, contas));
	    spinnerContas.setSelection(startContaPos);
	    
	    spinnerTipos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	    	@Override
	    	public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
	    		TipoAtivo tipo = (TipoAtivo)spinnerTipos.getSelectedItem();
	    		if(tipo.getNome().equals("Ações") || tipo.getNome().equals("Fundos Imobiliários")){
	    			view.findViewById(R.id.addAtivoLayoutVencimento).setVisibility(View.GONE);
	    		}else{
	    			view.findViewById(R.id.addAtivoLayoutVencimento).setVisibility(View.VISIBLE);
	    		}
	    	}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    
	    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				
				EditText editValor = (EditText) view.findViewById(R.id.addAtivoDialogEditTxtValor);
				EditText editDescricao = (EditText) view.findViewById(R.id.addAtivoDialogEditTxtDescrição);
				EditText editQuantidade = (EditText) view.findViewById(R.id.addAtivoDialogEditTxtQtd);
				EditText editSigla = (EditText) view.findViewById(R.id.addAtivoDialogEditTxtSigla);
				
				try{
					float valor = Float.valueOf(editValor.getText().toString());
					ativo.setValor(valor);
					float quantidade = Float.valueOf(editQuantidade.getText().toString());
					ativo.setQuantidade(quantidade);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				ativo.setNome(editDescricao.getText().toString());
				ativo.setSigla(editSigla.getText().toString());
				
				Conta cc = (Conta) spinnerContas.getSelectedItem();
				ativo.setId_Conta(cc.getId());
				
				TipoAtivo tA = (TipoAtivo) spinnerTipos.getSelectedItem();
				ativo.setId_TipoAtivo(tA.getId());
				
				ativo.setData(format.format(data.getTime()));
				ativo.setVencimento(format.format(vencimento.getTime()));
				
				Transacao t = new Transacao();
				t.setData(format.format(data.getTime()));
				t.setDescricao("Investimento -> "+ativo.getNome());
				t.setId_Conta(ativo.getId_Conta());
				t.setValor(ativo.getValor()*ativo.getQuantidade());
				t.setId_CategoriaTransacao(db.select(CategoriaTransacao.class, " WHERE nome = 'Investimento' AND system = 1").get(0).getId());
				
				if(db.insert(t)){
					ativo.setId_Transacao(t.getId());
					db.insert(ativo);
				}else{
					MessageUtils.showMessage(context, "Erro!", "Não foi possível realizar o investimento!");
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
		
		OnDateSetListener vencimentoListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, monthOfYear);
				c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				vencimento.set(Calendar.YEAR, year);
				vencimento.set(Calendar.MONTH, monthOfYear);
				vencimento.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				ViewUtil.adjustDateOnTextView(btnVencimento, vencimento);
			}
		};

		final DatePickerDialog vencimentoDialog = new DatePickerDialog(context, vencimentoListener, 
				vencimento.get(Calendar.YEAR), 
				vencimento.get(Calendar.MONTH), 
				vencimento.get(Calendar.DAY_OF_MONTH));

		btnVencimento.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vencimentoDialog.show();				
			}
		});
	    
	    
	    alert.show();        
	}
	
	@SuppressLint("NewApi")
	public static void showEditAtivo(final Context context, final Ativo ativo,  final LayoutInflater inflater, final DatabaseHandler db, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.add_ativo_dialog, null);
	    alert.setView(view);
	    
	    final GregorianCalendar data = new GregorianCalendar();
	    final GregorianCalendar vencimento = new GregorianCalendar();
	    try {
			Date date = DateUtil.sqlDateFormat().parse(ativo.getData());
			data.setTime(date);
			Date vencDate = DateUtil.sqlDateFormat().parse(ativo.getVencimento());
			vencimento.setTime(vencDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	    final Button btnDate = (Button)view.findViewById(R.id.addAtivoDialogBtnData);
	    final Button btnVencimento = (Button)view.findViewById(R.id.addAtivoDialogBtnVencimento);
	    ViewUtil.adjustDateOnTextView(btnDate, data);
	    ViewUtil.adjustDateOnTextView(btnVencimento, vencimento);
	    
	    final List<TipoAtivo> tipos = db.select(TipoAtivo.class);
	    final List<Conta> contas = db.select(Conta.class);
	    
	    int startContaPos = 0;
	    
	    for(int i = 0; i < contas.size(); i++) {
	    	if(contas.get(i).getId() == ativo.getId_Conta()) {
	    		startContaPos = i;
	    		break;
	    	}
	    }
	    
	    int startTipoAtivoPos = 0;
	    
	    for(int i = 0; i < tipos.size(); i++) {
	    	if(tipos.get(i).getId() == ativo.getId_TipoAtivo()) {
	    		startTipoAtivoPos = i;
	    		break;
	    	}
	    }
	    
	    final Spinner spinnerTipos = (Spinner) view.findViewById(R.id.addAtivoDialogSpinnerTipo);
	    spinnerTipos.setAdapter(new ArrayAdapter<TipoAtivo>(context, android.R.layout.simple_list_item_1, tipos));
	    spinnerTipos.setSelection(startTipoAtivoPos);
	    
	    final Spinner spinnerContas = (Spinner) view.findViewById(R.id.addAtivoDialogSpinnerConta);
	    spinnerContas.setAdapter(new ArrayAdapter<Conta>(context, android.R.layout.simple_list_item_1, contas));
	    spinnerContas.setSelection(startContaPos);
	    
	    spinnerTipos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	    	@Override
	    	public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
	    		TipoAtivo tipo = (TipoAtivo)spinnerTipos.getSelectedItem();
	    		if(tipo.getNome().equals("Ações") || tipo.getNome().equals("Fundos Imobiliários")){
	    			view.findViewById(R.id.addAtivoLayoutVencimento).setVisibility(View.GONE);
	    		}else{
	    			view.findViewById(R.id.addAtivoLayoutVencimento).setVisibility(View.VISIBLE);
	    		}
	    	}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    final EditText editValor = (EditText) view.findViewById(R.id.addAtivoDialogEditTxtValor);
	    final EditText editDescricao = (EditText) view.findViewById(R.id.addAtivoDialogEditTxtDescrição);
	    final EditText editQuantidade = (EditText) view.findViewById(R.id.addAtivoDialogEditTxtQtd);
	    final EditText editSigla = (EditText) view.findViewById(R.id.addAtivoDialogEditTxtSigla);
	    
	    editValor.setText(ativo.getValor()+"");
	    editDescricao.setText(ativo.getNome());
	    editQuantidade.setText(ativo.getQuantidade()+"");
	    editSigla.setText(ativo.getSigla()+"");
	    
	    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				
				
				try{
					float valor = Float.valueOf(editValor.getText().toString());
					ativo.setValor(valor);
					float quantidade = Float.valueOf(editQuantidade.getText().toString());
					ativo.setQuantidade(quantidade);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				ativo.setNome(editDescricao.getText().toString());
				ativo.setSigla(editSigla.getText().toString());
				
				Conta cc = (Conta) spinnerContas.getSelectedItem();
				ativo.setId_Conta(cc.getId());
				
				TipoAtivo tA = (TipoAtivo) spinnerTipos.getSelectedItem();
				ativo.setId_TipoAtivo(tA.getId());
				
				ativo.setData(format.format(data.getTime()));
				ativo.setVencimento(format.format(vencimento.getTime()));
				
				Transacao t = db.select(Transacao.class, " WHERE id = "+ativo.getId_Transacao()).get(0);
				t.setData(format.format(data.getTime()));
				t.setDescricao("Investimento -> "+ativo.getNome());
				t.setId_Conta(ativo.getId_Conta());
				t.setValor(ativo.getValor()*ativo.getQuantidade());
				t.setId_CategoriaTransacao(db.select(CategoriaTransacao.class, " WHERE nome = 'Investimento' AND system = 1").get(0).getId());
				
				if(db.update(t)){
					db.update(ativo);
				}else{
					MessageUtils.showMessage(context, "Erro!", "Não foi possível atualizar o investimento!");
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
		
		OnDateSetListener vencimentoListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, monthOfYear);
				c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				vencimento.set(Calendar.YEAR, year);
				vencimento.set(Calendar.MONTH, monthOfYear);
				vencimento.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				ViewUtil.adjustDateOnTextView(btnVencimento, vencimento);
			}
		};

		final DatePickerDialog vencimentoDialog = new DatePickerDialog(context, vencimentoListener, 
				vencimento.get(Calendar.YEAR), 
				vencimento.get(Calendar.MONTH), 
				vencimento.get(Calendar.DAY_OF_MONTH));

		btnVencimento.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vencimentoDialog.show();				
			}
		});
	    
	    
	    alert.show();        
	}
	
	public static void showAddAtivoRentabilidade(final Context context, final Ativo ativo, final LayoutInflater inflater, final DatabaseHandler db, final DialogInterface.OnClickListener listener){
		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View view = inflater.inflate(R.layout.add_ativo_event_dialog, null);
	    alert.setView(view);
	    
	    final GregorianCalendar data = new GregorianCalendar();
	    final Button btnDate = (Button)view.findViewById(R.id.addAtivoEventDialogBtnData);
	    ViewUtil.adjustDateOnTextView(btnDate, data);
	    
	    Log.d(TAG, TipoConta.class.getSimpleName());
	    
	    
	    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText editNome = (EditText) view.findViewById(R.id.addAtivoEventDialogEditTxtPrice);
				
				RentabilidadeAtivo r = new RentabilidadeAtivo();
				
				try {
					float valor = Float.valueOf(editNome.getText().toString());
					r.setValor(valor);
					r.setId_Ativo(ativo.getId());
					r.setData(DateUtil.sqlDateFormat().format(data.getTime()));
					db.insert(r);				
				} catch (Exception e) {
					MessageUtils.showMessage(context, context.getString(R.string.add_ativo_event_dialog_msg_title), context.getString(R.string.add_ativo_event_dialog_msg_error));
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
	
}
