package br.niltonvasques.moneycontrol.app;

import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.SharedPreferences;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;
import br.niltonvasques.moneycontrolbeta.R;

public class MoneyControlApp extends Application{
	
	public static final String PREFES_NAME 				= "money-control-prefs";
	private static final String PREFERENCE_CHANGE_DIALOG 				= "change_dialog_";
	
	private DatabaseHandler handler = null;
	private Object data;
	private HashMap<Integer, CategoriaTransacao> categoriasTransacao;
	private SharedPreferences settings = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		categoriasTransacao = new HashMap<Integer, CategoriaTransacao>();
		updateCategorias();
	}
	
	private SharedPreferences getSettings(){
		if(settings == null){
			settings = getSharedPreferences(PREFES_NAME, MODE_PRIVATE); 
		}
		return settings;
	}
	
	public DatabaseHandler getDatabase() {
		if(handler == null) handler = new DatabaseHandler(getApplicationContext());
		return handler;
	}
	
	public void updateCategorias(){
		categoriasTransacao.clear();
		List<CategoriaTransacao> categorias  = getDatabase().select(CategoriaTransacao.class);
		for (CategoriaTransacao categoriaTransacao : categorias) {
			categoriasTransacao.put(categoriaTransacao.getId(), categoriaTransacao);
		}
	}
	
	public HashMap<Integer, CategoriaTransacao> getCategoriasTransacao(){
		return categoriasTransacao;
	}
	
		
	public Object getData(){
		return data;
	}
	
	public void setData(Object data){
		this.data = data;
	}
	
	public boolean isAlreadyShowNewsChangeDialog() {
		return getSettings().getBoolean(PREFERENCE_CHANGE_DIALOG+getString(R.string.app_version), false);
	}
	
	public void alreadyShowChangeDialog() {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putBoolean(PREFERENCE_CHANGE_DIALOG+getString(R.string.app_version),true);
		editor.commit();
	}
	

}
