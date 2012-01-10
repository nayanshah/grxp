package com.whiz.grxp;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "person")
public class Person {
	
	@DatabaseField(id=true)
	private String name;
	@DatabaseField
	private double balance;
	
	public Person() {
		// TODO Auto-generated constructor stub
	}
	
	public Person(String name, double balance) {
		this.name = name;
		this.balance = balance;
	}
	
	public Person(String name) {
		this.name = name;
		this.balance = 0;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public String getName() {
		return name;
	}
	
	public void addAmount(double amount) {
		balance += amount;
	}
	
	public void reset(double amount) {
		balance = 0;
	}
	
	public void reset() {
		reset(0);
	}
	
	public void rename(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//sb.append(name);
		sb.append(name)
			.append("\n(Rs ")
			.append(String.format("%3.0f", balance))
			.append(")");
		return sb.toString();
	}

}
