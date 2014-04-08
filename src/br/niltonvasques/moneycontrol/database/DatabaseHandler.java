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
	
	public <T> boolean insert(T bean){
		
		SQLiteDatabase db = this.getWritableDatabase();    	 
	
		db.execSQL(DatabaseUtil.beanToSqlInsert(bean, bean.getClass().getSimpleName(), "id"));
	
		db.close(); // Closing database connection		
		
		return true;
	}

	//    // Adding new contact
	//    public void add(TipoAndaime andaime) {
	//        SQLiteDatabase db = this.getWritableDatabase();
	// 
	//        ContentValues values = new ContentValues();
	//        values.put("DSC", andaime.getDSC()); // Contact Name
	//        values.put("COD", andaime.getCOD()); // Contact Phone
	// 
	//        // Inserting Row
	//        db.insert(TipoAndaime.getTabName(), null, values);
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+TipoAndaime.getTabName(), null);
	//        System.out.println("SELECT result: "+c.getCount());
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	TipoAndaime a = new TipoAndaime();
	//            	a.setDSC(c.getString(1));
	//                a.setCOD(Integer.parseInt(c.getString(0)));
	//                System.out.println(a);
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//    }
	//    
	//    public List<TipoAndaime> getAndaimes(){
	//    	List<TipoAndaime> andaimes = new ArrayList<TipoAndaime>();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+TipoAndaime.getTabName(), null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	TipoAndaime a = new TipoAndaime();
	//            	DatabaseUtil.cursorToBean(c, a);
	//                andaimes.add(a);
	//                System.out.println(a);
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//        
	//        return andaimes;
	//    }
	//    
	//    public TipoAndaime getAndaime(int COD){
	//    	TipoAndaime andaime = null;
	//    	
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+TipoAndaime.getTabName()+" WHERE COD = "+COD, null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//        	andaime = new TipoAndaime();
	//        	DatabaseUtil.cursorToBean(c, andaime);
	//        }
	//        
	//        db.close(); // Closing database connection
	//        
	//        return andaime;
	//    }
	//    
	//    public List<Encarregado> getEncarregados(){
	//    	List<Encarregado> encarregados = new ArrayList<Encarregado>();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Encarregado.getTabName(), null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	Encarregado a = new Encarregado();
	//            	DatabaseUtil.cursorToBean(c, a);
	//                encarregados.add(a);
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//        
	//        return encarregados;
	//    }
	//    
	//    public List<Especialidade> getEspecialidades(){
	//    	List<Especialidade> especialidades = new ArrayList<Especialidade>();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Especialidade.getTabName(), null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	Especialidade a = new Especialidade();
	//            	DatabaseUtil.cursorToBean(c, a);
	//                especialidades.add(a);
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//        
	//        return especialidades;
	//    }
	//
	//	public List<Empresa> getEmpresa() {
	//		List<Empresa> materiais = new ArrayList<Empresa>();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Empresa.getTabName(), null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	Empresa a = new Empresa();
	//            	DatabaseUtil.cursorToBean(c, a);
	//                materiais.add(a);
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//        return materiais;
	//	}
	//
	//	public List<TipoMedicao> getMedicao() {
	//		List<TipoMedicao> medicao = new ArrayList<TipoMedicao>();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+TipoMedicao.getTabName(), null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	TipoMedicao a = new TipoMedicao();
	//            	DatabaseUtil.cursorToBean(c, a);
	//                medicao.add(a);
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//        return medicao;
	//	}
	//
	//	public List<Area> getPacotes() {
	//		List<Area> pacotes = new ArrayList<Area>();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Area.getTabName(), null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	Area a = new Area();
	//            	DatabaseUtil.cursorToBean(c, a);
	//                pacotes.add(a);
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//        return pacotes;
	//	}
	//	
	//	public Area getPacote(int id) {
	//		Area area = new Area();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Area.getTabName()+" WHERE COD = "+id, null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	DatabaseUtil.cursorToBean(c, area);
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//        return area;
	//	}
	//
	//	public List<Servico> getServicos() {
	//		List<Servico> servicos = new ArrayList<Servico>();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Servico.getTabName(), null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	Servico a = new Servico();
	//            	DatabaseUtil.cursorToBean(c, a);
	//                servicos.add(a);
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//        return servicos;
	//	}
	//
	//	public List<Turno> getTurnos() {
	//		List<Turno> turnos = new ArrayList<Turno>();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Turno.getTabName(), null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	Turno a = new Turno();
	//            	DatabaseUtil.cursorToBean(c, a);
	//                turnos.add(a);
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//        return turnos;
	//	}
	//
	//	public List<Clima> getClimas() {
	//		List<Clima> climas = new ArrayList<Clima>();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Clima.getTabName(), null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	Clima a = new Clima();
	//            	DatabaseUtil.cursorToBean(c, a);
	//                climas.add(a);
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//        return climas;
	//	}
	//	
	//	public List<Projeto> getProjetos() {
	//		List<Projeto> projetos = new ArrayList<Projeto>();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Projeto.getTabName(), null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	Projeto a = new Projeto();
	//            	DatabaseUtil.cursorToBean(c, a);
	//                projetos.add(a);
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//        return projetos;
	//	}
	//	
	//	public Clima getClima(int COD) {
	//		Clima climas = null;
	//		SQLiteDatabase db = this.getReadableDatabase();    	 
	//	    
	//	    Cursor c = db.rawQuery("SELECT * FROM "+Clima.getTabName()+" WHERE COD = "+COD, null);
	//	    
	//	 // looping through all rows and adding to list
	//	    if (c.moveToFirst()) {
	//	        	climas = new Clima();
	//	        	DatabaseUtil.cursorToBean(c, climas);
	//	    }
	//	    
	//	    db.close(); // Closing database connection
	//	    return climas;
	//	}
	//
	//	public int getBoletosCount(){
	//		SQLiteDatabase db = this.getReadableDatabase();
	//        Cursor c = db.rawQuery("SELECT count(COD) FROM "+Boleto.getTabName(), null);
	//        c.moveToFirst();
	//        int count = Integer.parseInt(c.getString(0));
	//        return count;
	//	}
	//
	//	public List<Material> getMateriais() {
	//		List<Material> materiais = new ArrayList<Material>();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Material.getTabName(), null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	Material a = new Material();
	//            	DatabaseUtil.cursorToBean(c, a);
	//                materiais.add(a);
	//            } while (c.moveToNext());
	//        }
	//        
	//        for (Material material : materiais) {
	//			Cursor c2 = db.rawQuery("SELECT * FROM "+Tamanho.getTabName()+" WHERE COD_MAT = "+material.getCOD(),null);
	//			List<Tamanho> tamanhos = new ArrayList<Tamanho>();
	//			if(c2.moveToFirst()){
	//				do{
	//					Tamanho tam = new Tamanho();
	//					DatabaseUtil.cursorToBean(c2, tam);
	//					tam.setMaterial(material);					
	//					tamanhos.add(tam);
	//				}while(c2.moveToNext());
	//			}
	//			material.setTamanhos(tamanhos);
	//		}
	//        
	//        db.close(); // Closing database connection
	//        return materiais;
	//	}
	//	
	//	public Material getMaterial(int COD) {
	//		Material material = new Material();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Material.getTabName()+" WHERE COD = "+COD, null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//        	DatabaseUtil.cursorToBean(c, material);
	//        }
	//        
	//        db.close(); // Closing database connection
	//        return material;
	//	}
	//	
	//	public Tamanho getTamanho(int COD) {
	//		Tamanho tam = new Tamanho();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Tamanho.getTabName()+" WHERE COD = "+COD, null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//        	DatabaseUtil.cursorToBean(c, tam);
	//        }
	//        
	//        db.close(); // Closing database connection
	//        return tam;
	//	}
	//
	//	public void insertBoleto(Boleto boleto) {
	//		SQLiteDatabase db = this.getWritableDatabase();
	//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS");
	//		
	//        ContentValues values = new ContentValues();
	//        values.put(Boleto.FIELD_ALTURA, boleto.getALTURA()); // Contact Name
	//        values.put(Boleto.FIELD_ANDAIME, boleto.getCOD_TP_AND()); // Contact Name
	//        values.put(Boleto.FIELD_AVANCO, boleto.getAVANCO()); // Contact Name
	//        values.put(Boleto.FIELD_COMPRIMENTO, boleto.getCOMPRIMENTO()); // Contact Name
	//        values.put(Boleto.FIELD_CONFINADO, boleto.isCONFINADO()); // Contact Name
	//        values.put(Boleto.FIELD_DATA_DESMONT, format.format(boleto.getData().getTime()));
	//        values.put(Boleto.FIELD_DATA_MOVIMEN, format.format(boleto.getData().getTime()));
	//        values.put(Boleto.FIELD_EFETIVO, boleto.getEFETIVO());
	//        values.put(Boleto.FIELD_ELEVACAO, boleto.getELEVACAO());
	//        values.put(Boleto.FIELD_ENCARREGADO, boleto.getCOD_ENC());
	//        values.put(Boleto.FIELD_ESPECIALIDADE, boleto.getCOD_ESPEC());
	//        values.put(Boleto.FIELD_LARGURA, boleto.getLARGURA());
	//        values.put(Boleto.FIELD_FORNECEDOR_MATERIAL, boleto.getCOD_EMPRESA());
	//        values.put(Boleto.FIELD_MEDICAO, boleto.getCOD_TP_MED());
	//        values.put(Boleto.FIELD_NUMERO_PT, boleto.getNUM_PT());
	//        values.put(Boleto.FIELD_ORDEM, boleto.getORDEM());
	//        values.put(Boleto.FIELD_PACOTE, boleto.getCOD_AREA());
	//        values.put(Boleto.FIELD_PERICULOSIDADE, boleto.isPERICULOSIDADE());
	//        values.put(Boleto.FIELD_PLACA, boleto.getPLACA());
	//        values.put(Boleto.FIELD_PT_ABERTURA, format.format(boleto.getPtLiberacao().getTime()));
	//        values.put(Boleto.FIELD_PT_SOL_ABERT, format.format(boleto.getPtSolAbertura().getTime()));
	//        values.put(Boleto.FIELD_PT_ENCERRAME, format.format(boleto.getPtEncerramento().getTime()));
	//        values.put(Boleto.FIELD_PT_SOL_FECH, format.format(boleto.getPtSolEncerramento().getTime()));
	//        values.put(Boleto.FIELD_SERVICO, boleto.getSER_ID());
	//        values.put(Boleto.FIELD_TAG, boleto.getTAG());
	//        values.put(Boleto.FIELD_TEMPO, boleto.getCOD_TEMP());
	//        values.put(Boleto.FIELD_TURNO, boleto.getCOD_TURN());
	//        
	//        // Inserting Row
	//        long iD = db.insert(Boleto.getTabName(), null, values);
	//        
	//        boleto.setCOD((int)iD);
	//        
	//        for(Quantidade q: boleto.getQuantidades()){
	//        	if(q.getQTD()>0){
	//        		q.setCOD_BLT(boleto.getCOD());
	//        		insertQuantidade(q);
	//        	}
	//        }        	        
	//        
	//        db.close(); // Closing database connection
	//		
	//	}
	//	
	//	public boolean insertQuantidade(Quantidade qtd){
	//		
	//		try{
	//			SQLiteDatabase db = this.getWritableDatabase();
	//			
	//			ContentValues v = new ContentValues();
	//			v.put(Quantidade.FIELD_COD_TAMANHO,qtd.getTamanho().getCOD());
	//			v.put(Quantidade.FIELD_COD_MATERIAL,qtd.getMaterial().getCOD());
	//			v.put(Quantidade.FIELD_QUANTIDADE,qtd.getQTD());
	//			v.put(Quantidade.FIELD_BOLETO,qtd.getCOD_BLT());
	//				
	//			db.insert(Quantidade.getTabName(), null, v);
	//			db.close();
	//		}catch (Exception e) {
	//			return false;
	//		}
	//		
	//		return true;
	//	}
	//
	//	public List<Boleto> getBoletos() {
	//		List<Boleto> boletos = new ArrayList<Boleto>();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Boleto.getTabName(), null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	Boleto a = new Boleto();
	//            	DatabaseUtil.cursorToBean(c, a);
	//            	
	//            	a.setAndaime(getAndaime(a.getCOD_TP_AND()));
	//            	a.setClima(getClima(a.getCOD_TEMP()));
	//            	a.setEncarregado(getEncarregado(a.getCOD_ENC()));
	//            	a.setEspecialidade(getEspecialidade(a.getCOD_ESPEC()));
	//            	a.setEmpresa(getEmpresa(a.getCOD_EMPRESA()));
	//            	a.setMedicao(getMedicao(a.getCOD_TP_MED()));
	//                a.setPacote(getPacote(a.getCOD_AREA()));
	//                a.setQuantidades(getQuantidades(a.getCOD()));
	//                a.setServico(getServico(a.getSER_ID()));
	//                a.setTurno(getTurno(a.getCOD_TURN()));
	//                boletos.add(a);
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//        return boletos;
	//	}
	//	
	//	private Turno getTurno(int COD) {
	//		Turno t = null;
	//		SQLiteDatabase db = this.getReadableDatabase();    	 
	//	    
	//	    Cursor c = db.rawQuery("SELECT * FROM "+Turno.getTabName()+" WHERE COD = "+COD, null);
	//	    
	//	    if (c.moveToFirst()) {
	//	        	t = new Turno();
	//	        	DatabaseUtil.cursorToBean(c, t);
	//	    }
	//	    
	//	    db.close(); // Closing database connection
	//	    return t;
	//	}
	//
	//	private Servico getServico(int SER_ID) {
	//		Servico s = null;
	//		SQLiteDatabase db = this.getReadableDatabase();    	 
	//	    
	//	    Cursor c = db.rawQuery("SELECT * FROM "+Servico.getTabName()+" WHERE SER_ID = "+SER_ID, null);
	//	    
	//	    if (c.moveToFirst()) {
	//	        	s = new Servico();
	//	        	DatabaseUtil.cursorToBean(c, s);
	//	    }
	//	    
	//	    db.close(); // Closing database connection
	//	    return s;
	//	}
	//
	//	private TipoMedicao getMedicao(int COD) {
	//		TipoMedicao e = null;
	//		SQLiteDatabase db = this.getReadableDatabase();    	 
	//	    
	//	    Cursor c = db.rawQuery("SELECT * FROM "+TipoMedicao.getTabName()+" WHERE COD = "+COD, null);
	//	    
	//	    if (c.moveToFirst()) {
	//	        	e = new TipoMedicao();
	//	        	DatabaseUtil.cursorToBean(c, e);
	//	    }
	//	    
	//	    db.close(); // Closing database connection
	//	    return e;
	//	}
	//
	//	private Empresa getEmpresa(int COD) {
	//		Empresa e = null;
	//		SQLiteDatabase db = this.getReadableDatabase();    	 
	//	    
	//	    Cursor c = db.rawQuery("SELECT * FROM "+Empresa.getTabName()+" WHERE COD = "+COD, null);
	//	    
	//	    if (c.moveToFirst()) {
	//	        	e = new Empresa();
	//	        	DatabaseUtil.cursorToBean(c, e);
	//	    }
	//	    
	//	    db.close(); // Closing database connection
	//	    return e;
	//	}
	//
	//	private Especialidade getEspecialidade(int COD) {
	//		Especialidade e = null;
	//		SQLiteDatabase db = this.getReadableDatabase();    	 
	//	    
	//	    Cursor c = db.rawQuery("SELECT * FROM "+Especialidade.getTabName()+" WHERE COD = "+COD, null);
	//	    
	//	    if (c.moveToFirst()) {
	//	        	e = new Especialidade();
	//	        	DatabaseUtil.cursorToBean(c, e);
	//	    }
	//	    
	//	    db.close(); // Closing database connection
	//	    return e;
	//	}
	//
	//	private Encarregado getEncarregado(int COD) {
	//		Encarregado e = null;
	//		SQLiteDatabase db = this.getReadableDatabase();    	 
	//	    
	//	    Cursor c = db.rawQuery("SELECT * FROM "+Encarregado.getTabName()+" WHERE COD = "+COD, null);
	//	    
	//	    if (c.moveToFirst()) {
	//	        	e = new Encarregado();
	//	        	DatabaseUtil.cursorToBean(c, e);
	//	    }
	//	    
	//	    db.close(); // Closing database connection
	//	    return e;
	//	}
	//
	//	public List<Quantidade> getQuantidades(int boletoId) {
	//		List<Quantidade> quantidades = new ArrayList<Quantidade>();
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Quantidade.getTabName()+" WHERE "+Quantidade.FIELD_BOLETO+" = "+boletoId, null);
	//        
	//        // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            do {
	//            	Quantidade a = new Quantidade();
	//            	DatabaseUtil.cursorToBean(c, a);
	//            	a.setMaterial(getMaterial(a.getCOD_MAT()));
	//            	a.setTamanho(getTamanho(a.getCOD_TAM()));
	//                quantidades.add(a);
	//                Log.i(TAG, a.toString());
	//            } while (c.moveToNext());
	//        }
	//        
	//        db.close(); // Closing database connection
	//        return quantidades;
	//	}
	//
	//	public <T> void replaceTable(List<T> objects, String tabName) {
	//		replaceTable(objects, tabName,"COD");
	//	}
	//	
	//	public <T> void replaceTable(List<T> objects, String tabName, boolean dropTable) {
	//		replaceTable(objects, tabName,"COD", dropTable);
	//	}
	//	
	//	public <T> void replaceTable(List<T> objects, String tabName, String idField) {
	//		replaceTable(objects, tabName, idField, false);
	//	}
	//	
	//	public <T> void replaceTable(List<T> objects, String tabName, String idField, boolean dropTable) {
	//		
	//		if(dropTable){
	//			SQLiteDatabase db = this.getWritableDatabase();
	//			
	//			String sql = "DELETE FROM "+tabName;
	//			db.execSQL(sql);
	//			Log.d(TAG,sql);
	//			
	//			db.close();
	//		}
	//		
	//		List<String> sqls = new ArrayList<String>();
	//		for (T clima : objects) {
	//			String sql = "";
	//			if(checkItemExist(tabName,DatabaseUtil.getBeanId(clima,idField),idField)){
	//				sql = DatabaseUtil.beanToSqlUpdate(clima, tabName, idField);
	//			}else{
	//				sql = DatabaseUtil.beanToSqlInsert(clima, tabName);
	//			}
	//			sqls.add(sql);
	//		}
	//		
	//		SQLiteDatabase db = this.getWritableDatabase();
	//		for (String sql : sqls) {
	//			db.execSQL(sql);
	//			Log.d(TAG,sql);
	//		}
	//		db.close();
	//	}
	//	
	//	private boolean checkItemExist(String tabName,int cod) {
	//		return checkItemExist(tabName, cod, "COD");
	//	}
	//
	//	private boolean checkItemExist(String tabName,int cod, String idField) {
	//		if(!isTableExists(tabName)) return false;
	//		
	//		SQLiteDatabase db = this.getReadableDatabase();
	//		String sql = "SELECT distinct "+idField+" FROM "+tabName+" WHERE "+idField+" = "+cod+";";
	//		
	//		Cursor c = db.rawQuery(sql, null);
	//		Log.d(TAG,sql);
	//		if(c != null && c.getCount() > 0){
	//			db.close();
	//			return true;
	//		}
	//		db.close();
	//		return false;
	//	}
	//	
	//	public boolean isTableExists(String tableName) {
	//		SQLiteDatabase db = this.getReadableDatabase();
	//
	//		Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
	//		
	//		if(cursor!=null) {
	//			if(cursor.getCount()>0) {
	//				cursor.close();
	//				db.close();
	//				return true;
	//			}
	//			cursor.close();
	//		}
	//		db.close();
	//		return false;
	//	}
	//
	//	public void updateBoleto(Boleto boleto) {
	//		SQLiteDatabase db = this.getWritableDatabase();
	//		
	//		String sql = DatabaseUtil.beanToSqlUpdate(boleto, Boleto.getTabName());
	//		
	//		Log.d(TAG, "updateBoleto: "+sql);
	//		
	//		db.execSQL(sql);
	//		
	//		db.close();
	//	}
	//
	//	public Boleto getBoleto(int cod) {
	//		Boleto boleto = null;
	//    	SQLiteDatabase db = this.getReadableDatabase();    	 
	//        
	//        Cursor c = db.rawQuery("SELECT * FROM "+Boleto.getTabName()+" WHERE COD = "+cod, null);
	//        
	//     // looping through all rows and adding to list
	//        if (c.moveToFirst()) {
	//            	boleto = new Boleto();
	//            	DatabaseUtil.cursorToBean(c, boleto);
	//            	
	//            	boleto.setAndaime(getAndaime(boleto.getCOD_TP_AND()));
	//            	boleto.setClima(getClima(boleto.getCOD_TEMP()));
	//            	boleto.setEncarregado(getEncarregado(boleto.getCOD_ENC()));
	//            	boleto.setEspecialidade(getEspecialidade(boleto.getCOD_ESPEC()));
	//            	boleto.setEmpresa(getEmpresa(boleto.getCOD_EMPRESA()));
	//            	boleto.setMedicao(getMedicao(boleto.getCOD_TP_MED()));
	//                boleto.setPacote(getPacote(boleto.getCOD_AREA()));
	//                boleto.setQuantidades(getQuantidades(boleto.getCOD()));
	//                boleto.setServico(getServico(boleto.getSER_ID()));
	//                boleto.setTurno(getTurno(boleto.getCOD_TURN()));
	//        }
	//        
	//        db.close(); // Closing database connection
	//        return boleto;
	//	}
	//	
	//	public int deleteBoleto(int cod) {
	//    	SQLiteDatabase db = this.getWritableDatabase();
	//    	
	////    	db.execSQL("DELETE FROM "+Quantidade.getTabName()+" WHERE COD_BLT = "+cod);
	////    	
	////    	db.execSQL("DELETE FROM "+Boleto.getTabName()+" WHERE COD = "+cod);
	//    	
	//    	int count = db.delete(Quantidade.getTabName(),"COD_BLT = "+cod , null);
	//    	
	//    	count += db.delete(Boleto.getTabName(), "COD = "+cod, null);
	//        
	//        return count;
	//	}


}
