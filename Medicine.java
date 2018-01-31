package com.enarc.blockchain;

public class Medicine
{
	public String brand_name;
	public String[] alt_names;
	public String[] symptoms;

	public Medicine(String brand_name, String[] alt_names, String[] symptoms)
	{
		this.brand_name = brand_name;
		this.alt_names = alt_names;
		this.symptoms = symptoms;
	}
}
