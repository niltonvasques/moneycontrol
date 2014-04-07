package br.niltonvasques.moneycontrol.app;

import android.app.Application;
import br.niltonvasques.moneycontrol.database.DatabaseHandler;

public class MoneyControlApp extends Application{
	
	private DatabaseHandler handler = null;
	
	
	public DatabaseHandler getDatabase() {
		if(handler == null) handler = new DatabaseHandler(getApplicationContext());
		return handler;
	}

}
