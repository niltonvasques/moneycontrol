package br.niltonvasques.moneycontrol.database;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import android.database.Cursor;

public class DatabaseUtil {
	
	private static final String TAG = "[DatabaseUtil]";

	private static String GET_CHAR_SEQUENCE = "get";
	
	public static String beanToSqlInsert(Object classObject,String tableName) {
		String insert = "INSERT INTO "+tableName+" (";
		try {
			
			Field[] fields = classObject.getClass().getDeclaredFields();
			
			for(int i = 0;i< fields.length; i++){
				insert+=fields[i].getName()+( i== fields.length-1 ? ")":",");
			}
			
			insert+=" VALUES(";
			
			for(int i = 0;i< fields.length; i++){
				fields[i].setAccessible(true);
				if(fields[i].getType().equals(String.class)){
					insert+="'"+fields[i].get(classObject)+"'"
					+( i== fields.length-1 ? ")":",");
				}else{
					insert+=fields[i].get(classObject)+( i== fields.length-1 ? ")":",");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
        
        insert += ";";
		
		return insert;
    }
	
	public static String beanToSqlInsert(Object classObject,String tableName, String idName) {
		String insert = "INSERT INTO "+tableName+" (";
		try {
			
			Field[] fields = classObject.getClass().getDeclaredFields();
			
			for(int i = 0;i< fields.length; i++){
				if(fields[i].getName().equals(idName)){
					if(i == fields.length-1){
						insert = insert.substring(0, insert.length()-1);
						insert+=")";
					}
					continue;
				}
				insert+=fields[i].getName()+( i== fields.length-1 ? ")":",");
			}
			
			insert+=" VALUES(";
			
			for(int i = 0;i< fields.length; i++){
				if(fields[i].getName().equals(idName)){
					if(i == fields.length-1) {
						insert = insert.substring(0, insert.length()-1);
						insert+=")";
					}
					continue;
				}
				fields[i].setAccessible(true);
				if(fields[i].getType().equals(String.class)){
					insert+="'"+fields[i].get(classObject)+"'"
					+( i== fields.length-1 ? ")":",");
				}else{
					insert+=fields[i].get(classObject)+( i== fields.length-1 ? ")":",");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
        
        insert += ";";
		
		return insert;
    }
	
	public static String beanToSqlUpdate(Object classObject,String tableName) {
		return beanToSqlUpdate(classObject, tableName, "COD");
    }
	
	public static String beanToSqlUpdate(Object classObject,String tableName, String idField) {
//		 UPDATE TAB_TEMPO SET DSC='BOM' WHERE COD = 1;
		
		String insert = "UPDATE "+tableName+" SET ";
		try {
			
			Field[] fields = classObject.getClass().getDeclaredFields();
			
			for(int i = 0;i< fields.length; i++){
				
				if( !Modifier.isPrivate(fields[i].getModifiers()) || Modifier.isStatic(fields[i].getModifiers()) ) continue;
				
				Type t = fields[i].getType();
				if(t.equals(String.class) || t.equals(int.class) || t.equals(float.class) || t.equals(boolean.class)){ // Skipping another objects
				
					if(!fields[i].getName().equals(idField)){
					
						fields[i].setAccessible(true);
						insert+=fields[i].getName()+"=";
						
						if(fields[i].getType().equals(String.class)){
							insert+="'"+fields[i].get(classObject)+"'";
						}else if(fields[i].getType().equals(boolean.class)){
							insert+= ((Boolean) fields[i].get(classObject)) ? "1":"0";
						}else{
							insert+=fields[i].get(classObject);
						}
						
						insert+=( i== fields.length-1 ? " ":",");
					}else{
						if(i==fields.length-1){
							StringBuilder builder = new StringBuilder(insert);
							builder.replace(builder.length()-1, builder.length(), " ");
							insert = builder.toString();
						}
					}
				}
			}
			
			insert+=" WHERE "+idField+" ="+getBeanId(classObject,idField)+";";
			
		}catch (Exception e) {
			e.printStackTrace();
		}
       
		return insert;
   }
	
	public static <T> T cursorToBean(Cursor c, T classObject){
    	try{
        	for (Field field : classObject.getClass().getDeclaredFields()){
				int index = c.getColumnIndex(field.getName());
				if(index != -1){//Checking if field name exists on table
					field.setAccessible(true);
					if(field.getType().equals(String.class)){
						field.set(classObject, c.getString(index));
					}else if(field.getType().equals(int.class)){
						field.set(classObject, c.getInt(index));
					}else if(field.getType().equals(float.class)){
						field.set(classObject, c.getFloat(index));
					}else if(field.getType().equals(boolean.class)){
						field.set(classObject, c.getInt(index) == 1);
					}
				}
			}
            return classObject;
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    	
    	return null;
	}
	
	public static int getBeanId(Object bean){
		return getBeanId(bean, "COD");
	}
	
	public static int getBeanId(Object bean, String idField){
		try {
			
			Field[] fields = bean.getClass().getDeclaredFields();
			
			for(int i = 0;i< fields.length; i++){
				fields[i].setAccessible(true);
				if(fields[i].getName().equals(idField)){
					return fields[i].getInt(bean);
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return -1;
	}
}
