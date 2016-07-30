package br.niltonvasques.moneycontrol.database.bean;

import java.util.HashMap;
import java.util.GregorianCalendar;

import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;

public class ContaAPagar implements Cloneable{

    public enum Tipo{
        NORMAL, CARTAO_DE_CREDITO;
    }

    private int id;
    private Tipo tipo = Tipo.NORMAL;
    private float valor;
    private String data;
    private int id_CategoriaTransacao;
    private int id_Repeticao;
    private String descricao;
    private boolean status;
    private int quantidade;
    private HashMap<String, Object> params;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public float getValor() {
        return valor;
    }
    public void setValor(float valor) {
        this.valor = valor;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public int getId_CategoriaTransacao() {
        return id_CategoriaTransacao;
    }
    public void setId_CategoriaTransacao(int id_CategoriaTransacao) {
        this.id_CategoriaTransacao = id_CategoriaTransacao;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    public int getId_Repeticao() {
        return id_Repeticao;
    }

    public void setId_Repeticao(int id_Repeticao) {
        this.id_Repeticao = id_Repeticao;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isPaga(DatabaseHandler database, GregorianCalendar contaData){
        boolean paga = false;
        if(getTipo() == ContaAPagar.Tipo.CARTAO_DE_CREDITO) {
            Fatura f = (Fatura) getParams().get("fatura");
            if(f.getStatus() == Fatura.Status.PAGA){
                paga = true;
            }
        } else {
            String id =
                database.runQuery(QuerysUtil.checkContaPagaOnDate(getId(), contaData.getTime()));
            if(id !=null && !id.equals("")) paga = true;
        }
        return paga;
    }
}
