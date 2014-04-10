package br.niltonvasques.moneycontrol.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.niltonvasques.moneycontrol.database.bean.Conta;

public class DatabaseHandler extends SQLiteOpenHelper{

	private static String TAG = "[DatabaseHandler]";

	private static final String DB_SEED_SQL_FILENAME = "db/dml.sql";

	private static final String DDL_FILENAME = "db/ddl.sql";

	private static final int DATABASE_VERSION = 2;

	private static final String DATABASE_NAME = "money-db";

	private Context context;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;

		System.out.println("DatabaseHandler()");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		System.out.println("DatabaseHandler.onCreate()");

		if(createTables(db)){
			seedDb(db);
		}

		//		db.close();

	}

	private boolean seedDb(SQLiteDatabase db) {
		try {
			StringBuilder completeDML = new StringBuilder();
			InputStreamReader stream = new InputStreamReader(context.getAssets().open(DB_SEED_SQL_FILENAME),"UTF-8");
			BufferedReader reader = new BufferedReader(stream);

			String line = "";
			while(line != null){
				completeDML.append(line);
				line = reader.readLine();				
			}
			reader.close();

			String[] ddls = completeDML.toString().split(";");

			for (String dml : ddls) {
				System.out.println("db.execSQL("+dml+");");
				db.execSQL(dml);				
			}

			return true;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean createTables(SQLiteDatabase db) {
		try {
			StringBuilder completeDDL = new StringBuilder();
			InputStreamReader stream = new InputStreamReader(context.getAssets().open(DDL_FILENAME),"UTF-8");
			BufferedReader reader = new BufferedReader(stream);

			String line = "";
			while(line != null){
				completeDDL.append(line);
				line = reader.readLine();				
			}
			reader.close();

			String[] ddls = completeDDL.toString().split(";");

			for (String ddl : ddls) {
				System.out.println("db.execSQL("+ddl+");");
				db.execSQL(ddl);				
			}

			return true;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		

	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	public void showTiposBem(){
		SQLiteDatabase db = this.getReadableDatabase();    	 

		Cursor c = db.rawQuery("SELECT * FROM TipoBem", null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Log.d(TAG, "TipoBem: id: "+c.getInt(0)+" nome: "+c.getString(1));
			} while (c.moveToNext());
		}

		db.close(); // Closing database connection
	}

	public Conta insertConta(Conta cc){
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("nome", cc.getNome());
		values.put("saldo", cc.getSaldo());
		values.put("icon", cc.getIcon());
		values.put("id_TipoConta", 1); 

		// Inserting Row
		long id = db.insert("Conta", null, values);
		db.close();
		
		if(id != -1){
			cc.setId((int)id);
			return cc;
		}
		return null;

	}
	
	public boolean deleteConta(Conta cc) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	int count = db.delete("Conta", "id = "+cc.getId(), null);
        
        return count > 0;
	}

	public <T> List<T> select(Class<T> type){
		
		return select(type, "");
	}
	
	public <T> List<T> select(Class<T> type, String where){
		
		List<T> items = new ArrayList<T>();
		
		SQLiteDatabase db = this.getReadableDatabase();    	 
	
		Cursor c = db.rawQuery("SELECT * FROM "+type.getSimpleName()+" "+where, null);
	
		// looping through all rows and adding to list
		try {
			if (c.moveToFirst()) {
				do {
					T cc = type.newInstance();
					DatabaseUtil.cursorToBean(c, cc);
					items.add(cc);
				} while (c.moveToNext());
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	
		db.close(); // Closing database connection		
		
		return items;
	}
	
	public String runQuery(String query){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor c = db.rawQuery(query, null);
		
		if(c.moveToFirst()){
			return c.getString(0);
		}
		
		return "";
	}
	
	public <T> boolean insert(T bean){
		
		SQLiteDatabase db = this.getWritableDatabase();    	 
	
		db.execSQL(DatabaseUtil.beanToSqlInsert(bean, bean.getClass().getSimpleName(), "id"));
	
		db.close(); // Closing database connection		
		
		return true;
	}

}
