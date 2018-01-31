package com.enarc.blockchain;

public class Visit
{
	public double weight, height;
	public Insurance_Providers provider;
	public String summary;
	public String medication;

	public Visit(double weight, double height, Insurance_Providers provider, String summary, String meds)
	{
		this.weight = weight;
		this.height = height;
		this.provider = provider;
		this.summary = summary;
		this.medication = meds;
	}

	public Visit()
	{

	}

	public String toString()
	{
		return String.format("Weight: %5.1f, H: %5.1f, Provider: %s, Summary: %s, Medicine: %s\n", weight, height, provider, summary, medication);
	}
}
