package br.niltonvasques.moneycontrol.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.database.bean.TipoConta;
import br.niltonvasques.moneycontrol.database.bean.TipoTransacao;
import br.niltonvasques.moneycontrol.database.bean.Transacao;
import br.niltonvasques.moneycontrol.view.adapter.IconeAdapter;

public class MessageUtils {
	
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
	    
	    final Conta cc = new Conta();
	    cc.setIcon(R.drawable.bradesco_icon);
	    
	    final ImageView imgIcon = (ImageView) view.findViewById(R.id.addContaDialogImgIcon);
	    imgIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Escolha um ícone!", Toast.LENGTH_LONG).show();
				showIconesDialog(context, inflater, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						imgIcon.setBackgroundResource(which);
						cc.setIcon(which);
					}
				});
			}
		});
	    
	    Log.d(TAG, TipoConta.class.getSimpleName());
	    
	    List<TipoConta> tipos = db.select(TipoConta.class);
	    
	    final Spinner spinnerTipos = (Spinner) view.findViewById(R.id.addContaDialogSpinnerTipo);
	    spinnerTipos.setAdapter(new ArrayAdapter<TipoConta>(context, android.R.layout.simple_list_item_1, tipos));
	    
	    
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
		final View view = inflater.inflate(R.layout.icones_dialog, null);
	    alert.setView(view);
	    
	    final List<Integer> icons = new ArrayList<Integer>();
	    icons.add(R.drawable.bb_icon);
	    icons.add(R.drawable.bradesco_icon);
	    icons.add(R.drawable.sofisa_icon);
	    icons.add(R.drawable.directa_icon);
	    icons.add(R.drawable.visa_icon);
	    
	    final GridView grid = (GridView) view.findViewById(R.id.iconesDialogGridView);
	    grid.setTag(icons.get(0));
	    grid.setAdapter(new IconeAdapter(icons, inflater));
	    
	    grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				grid.setTag(icons.get(position));
			}
	    	
		});
	    
	    	    
	    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				
				listener.onClick(dialog, (Integer)grid.getTag());
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
	
}
