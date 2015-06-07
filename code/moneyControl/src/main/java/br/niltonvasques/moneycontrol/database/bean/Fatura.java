package br.niltonvasques.moneycontrol.database.bean;

import java.util.Date;
import java.util.GregorianCalendar;

public class Fatura{
	
		public enum Status{
			PAGA, PENDENTE, NENHUM
		}
		private float valor;
		private Status status = Status.NENHUM;
		private GregorianCalendar date;
		private float limite;
		private CartaoCredito cartao;
		
		public Fatura(float valor, float limite, Status st, GregorianCalendar date) {
			this.valor = valor;
			this.status = st;
			this.date = date;
		}
		
		public Fatura(){}

		public float getValor() {
			return valor;
		}

		public void setValor(float valor) {
			this.valor = valor;
		}

		public Status getStatus() {
			return status;
		}

		public void setStatus(Status status) {
			this.status = status;
		}

		public GregorianCalendar getDate() {
			return date;
		}

		public void setDate(GregorianCalendar date) {
			this.date = date;
		}

		public float getLimite() {
			return limite;
		}

		public void setLimite(float limite) {
			this.limite = limite;
		}

		public CartaoCredito getCartao() {
			return cartao;
		}

		public void setCartao(CartaoCredito cartao) {
			this.cartao = cartao;
		}
		
	}