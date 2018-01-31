package com.enarc.blockchain;

import java.sql.Date;
import java.util.ArrayList;

public class Patient
{
	public String firstName, lastName;
	public String gender;
	public String sport;
	public Date birth;
	public ArrayList<Visit> vList;

	public Patient(String firstName, String lastName, String sport, String gender, Date birth)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.sport = sport;
		this.gender = gender;
		this.birth = birth;

		vList = new ArrayList<Visit>();
	}

	public Patient()
	{
		vList = new ArrayList<Visit>();
	}

	public String toString()
	{
		return firstName + " " + lastName + " plays " + sport + " born on " + birth + " identifies as " + gender;
	}
}
