package br.niltonvasques.moneycontrol.view.custom;

public class Legend {
	
	private String name;
	private int color;
	
	public String getLegendName(){
		return this.name;
	}
	
	public int getLegendColor(){
		return this.color;
	}
	
	public void setLegendName(String name){
		this.name = name;
	}
	
	public void setLegendColor(int color){
		this.color = color;
	}

}
