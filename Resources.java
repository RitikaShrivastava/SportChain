package com.enarc.blockchain;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Resources
{

	public static BufferedImage getImage(String fileName)
	{
		try
		{

			return ImageIO.read(Resources.class.getResource("/" + fileName));

		} catch (Exception e)
		{
			System.err.println("Error reading image file " + fileName);
			return null;
		}
	}
}
