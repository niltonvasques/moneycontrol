package br.niltonvasques.moneycontrol.util;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import br.niltonvasques.moneycontrol.R;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.bean.Conta;
import br.niltonvasques.moneycontrol.view.IconeAdapter;

public class MessageUtils {

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
	    
	    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText editNome = (EditText) view.findViewById(R.id.addContaDialogEditTxtNome);
				EditText editSaldo = (EditText) view.findViewById(R.id.addContaDialogEditTxtSaldo);
				
				String ccNome = editNome.getText().toString();
				float saldo = Float.valueOf(editSaldo.getText().toString());
				
				cc.setNome(ccNome);
				cc.setSaldo(saldo);				
//				cc.setId_TipoConta(1);
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
	
	public class Icon{
		int icon;
		
//		public Icon(int icon) {
//			this.icon = icon;
//		}
	}
}
