package br.niltonvasques.moneycontrol.app;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import android.app.Application;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.bean.CategoriaTransacao;

public class MoneyControlApp extends Application{
	
	private DatabaseHandler handler = null;
	private Object data;
	private HashMap<Integer, CategoriaTransacao> categoriasTransacao;
	
	@Override
	public void onCreate() {
		super.onCreate();
		categoriasTransacao = new HashMap<Integer, CategoriaTransacao>();
		updateCategorias();
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
	
	

}
