package com.enarc.blockchain;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

public class TransButton extends JButton
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean hoveredOver = false;

	public TransButton(String s)
	{
		super(s);
	}

	public TransButton(TransButton tb)
	{
		this.setText(tb.getText());
		this.setBounds(tb.getBounds());

		this.addActionListener(tb.getActionListeners()[0]);
		this.addKeyListener(tb.getKeyListeners()[0]);
		this.addMouseListener(tb.getMouseListeners()[0]);
	}

	@Override
	public void paintComponent(Graphics g)
	{

		Graphics2D g2d = (Graphics2D) g;

		if (hoveredOver)
		{

			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}
		else
		{
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .65f));
		}

		super.paintComponent(g);

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

	}
}
