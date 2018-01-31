package com.enarc.blockchain;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

public class Main
{
	private JFrame frame;
	private JButton submitBtn;
	private JTextField username;
	private JPasswordField password;

	private JLabel usernameLabel, passwordLabel;

	private final Font def_font = new Font("Raleway", Font.PLAIN, 14);

	private final BufferedImage bkgImage = Resources.getImage("client_bkg.jpg"),
			soccer = Resources.getImage("lightblue.jpg"), tile = Resources.getImage("lightblue.jpg");

	public static int width, height;

	public HashMap<String, String> logins;
	public HashMap<String, Login_Type> typeof;

	public enum Login_Type
	{
		Client(), Provider(), Doctor();
	}

	public JFrame defaultFrame()
	{
		JFrame frame = new JFrame("Sport Chain");
		frame.setResizable(false);
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		return frame;
	}

	public void validate(String username, String password)
	{
		frame.setVisible(false);
		frame.dispose();

		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("SHA-256");

			md.update(password.getBytes(StandardCharsets.UTF_8));

			String stored_pass = logins.get(username);

			if (stored_pass == null)
			{
				System.out.println("CLIENT BY DEFAULT");
				showNextScreen(Login_Type.Client, username);
			}
			else if (stored_pass.equals(new String(md.digest(), StandardCharsets.UTF_8))
					|| logins.keySet().contains(username))
			{
				System.out.println("Show next screen: " + typeof.get(username) + ", username " + username);
				showNextScreen(typeof.get(username), username);
			}
			else
			{
				System.out.println("Stored pass: " + stored_pass + ", entered pass: "
						+ new String(md.digest(), StandardCharsets.UTF_8));
				System.out.println("Fit none of them.");
			}

		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
	}

	public String substring(String str, int start, int end)
	{
		int lesser = end > str.length() ? str.length() : end;
		return str.substring(start, lesser);
	}

	public void printfield(JTextArea dataField, String brand, String alt, String sym)
	{
		dataField.append(
				String.format("%s\t%s\t\t%s\n", substring(brand, 0, 30), substring(alt, 0, 30), substring(sym, 0, 30)));
	}

	public void showNextScreen(Login_Type nextType, String loginName)
	{
		frame = defaultFrame();

		System.out.println(nextType);

		switch (nextType)
		{
		case Client:
			showClientScreen(loginName);
			break;
		case Doctor:
			showDoctorScreen(loginName);
			break;
		case Provider:
		{
			showInsurerScreen(loginName);
			break;
		}
		default:

		}
	}

	public void setMedFieldToEmptyText(JTextField medField)
	{
		medField.setText("Enter the name brand medicine's name ");
		medField.setFont(def_font);
	}

	public void searchForBrandName(ArrayList<Medicine> medlist, String brandname, JTextArea reportField)
	{
		for (Medicine med : medlist)
		{
			if (med.brand_name.equalsIgnoreCase(brandname))
			{
				String alt_names = Arrays.toString(med.alt_names);
				String symptoms = Arrays.toString(med.symptoms);

				if (!reportField.getText().contains(brandname))
					printfield(reportField, brandname, alt_names.substring(1, alt_names.length() - 1),
							symptoms.substring(1, symptoms.length() - 1));
			}
		}
	}

	public void showClientScreen(String clientUsername)
	{

		JTextArea data = new JTextArea();
		data.setOpaque(false);
		data.setFont(new Font("Raleway", Font.BOLD, 18));
		data.setBounds(100, 120, 800, 200);
		data.setColumns(4);
		data.setEditable(false);
		printfield(data, "Prescription", "Alternatives", "Treatment For");
		data.setLineWrap(true);

		int txtW = 400, txtH = 30;

		ArrayList<Medicine> medlist = new ArrayList<Medicine>();

		JTextField medField = new JTextField();

		JButton searchBtn = new JButton("SEARCH");
		searchBtn.setFont(def_font);
		searchBtn.setFocusable(false);
		searchBtn.setBounds(95 + txtW, txtH + 40, 100, txtH);

		searchBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource() == searchBtn)
				{
					String str = medField.getText();

					if (!str.trim().equals(""))
					{
						searchForBrandName(medlist, str, data);
					}
				}
			}
		});

		JLabel medFieldLabel = new JLabel("Medicine Name ");
		medFieldLabel.setHorizontalAlignment(JLabel.CENTER);
		medFieldLabel.setFont(new Font("Raleway", Font.BOLD, 20));
		medFieldLabel.setBounds(95, 40, txtW, txtH);

		setMedFieldToEmptyText(medField);
		medField.setHorizontalAlignment(JTextField.CENTER);
		medField.setBounds(85, 40 + txtH, txtW, txtH);
		medField.setVisible(true);
		medField.setBackground(Color.white);
		medField.setForeground(Color.gray);

		medField.addKeyListener(new KeyAdapter()
		{
			boolean wipedOnce = false;

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (medField.hasFocus())
				{
					if (!wipedOnce)
					{
						medField.setText("");
						wipedOnce = true;

						medField.removeMouseListener(medField.getMouseListeners()[0]);

						medField.setForeground(Color.black);
						medField.setFont(def_font);
					}
				}

				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{

					String str = medField.getText();

					if (!str.trim().equals(""))
					{
						searchForBrandName(medlist, str, data);
					}
				}
			}
		});

		JPanel bkgPanel = new JPanel(null)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				g.drawImage(soccer, 0, 0, width, height, null);
			}
		};

		bkgPanel.setLayout(null);

		bkgPanel.add(medField);
		bkgPanel.add(medFieldLabel);
		bkgPanel.add(searchBtn);
		bkgPanel.add(data);

		BufferedReader br = null;

		try
		{
			br = new BufferedReader(new FileReader("res/drug_map.txt"));

			String nextLine = "";
			while ((nextLine = br.readLine()) != null)
			{
				String[] data_split = nextLine.split(" / ");

				String brand_drug = data_split[0].trim();

				String[] alt_names = data_split[1].trim().split(",");

				String[] problems = data_split[2].trim().split(",");

				Medicine med = new Medicine(brand_drug, alt_names, problems);
				medlist.add(med);
			}

			br.close();
		} catch (FileNotFoundException e1)
		{
			System.err.println("Drug map file not found mofo.");
			e1.printStackTrace();
		} catch (IOException e)
		{
			System.err.println("IO exception");
			e.printStackTrace();
		}

		try
		{
			br = new BufferedReader(new FileReader("res/drset.txt"));

			String str = "";

			while ((str = br.readLine()) != null)
			{
				int firstspace = str.indexOf(' ');
				if (firstspace == -1)
					continue;

				String op_code = str.substring(0, firstspace);

				if (op_code.equals("V") && str.substring(firstspace).trim().equalsIgnoreCase(clientUsername))
				{
					for (int i = 0; i < 4; i++)
					{
						br.readLine();
					}

					String med_assigned = br.readLine().trim();

					System.out.println("Searching for: " + med_assigned);

					searchForBrandName(medlist, med_assigned, data);
				}
			}

			br.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		frame.setContentPane(bkgPanel);
		frame.setVisible(true);
	}

	public Patient getPatient(ArrayList<Patient> pats, String firstName, String lastName)
	{
		for (Patient p : pats)
		{
			if (p.firstName.equalsIgnoreCase(firstName) && p.lastName.equalsIgnoreCase(lastName))
			{
				return p;
			}
		}

		return null;
	}

	public void showDoctorScreen(String loginName)
	{
		System.out.println("Showing doctor screen.");

		JPanel bkgPanel = new JPanel(null)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				g.drawImage(tile, 0, 0, width, height, null);
				g.setFont(new Font("Raleway", Font.BOLD, 24));
				g.drawString("Patient Records", width / 2 - 50, 30);
			}
		};

		BufferedReader br = null;

		ArrayList<Patient> patients = new ArrayList<Patient>();

		try
		{
			br = new BufferedReader(new FileReader("res/drset.txt"));

			String newLine = "";

			while ((newLine = br.readLine()) != null)
			{
				System.out.println(newLine);

				int firstSpace = newLine.indexOf(' ');

				if (firstSpace == -1)
					continue;

				String op_code = newLine.substring(0, firstSpace);

				switch (op_code)
				{
				case "Dr.":
				{
					if (newLine.contains(loginName))
					{
						System.out.println("NEWLINE: " + newLine);
						String patient_name = "";
						while (!(patient_name = br.readLine().trim()).equals(""))
						{
							Patient empty_patient = new Patient();

							empty_patient.firstName = patient_name.split(" ")[0];
							empty_patient.lastName = patient_name.split(" ")[1];

							patients.add(empty_patient);
						}
					}
					break;
				}
				case "P":
				{
					String name = newLine.substring(2).trim();

					Patient curr_patient = getPatient(patients, name.split(" ")[0], name.split(" ")[1]);

					if (curr_patient == null)
					{
						for (int i = 0; i < 4; i++)
						{
							br.readLine();
						}
						break;
					}

					curr_patient.gender = br.readLine().trim();
					curr_patient.sport = br.readLine().trim();

					String date = br.readLine().trim();

					System.out.println(date);

					curr_patient.birth = Date.valueOf(date);

					break;
				}
				case "V":
				{
					String name = newLine.substring(2).trim();

					Patient pat = getPatient(patients, name.split(" ")[0], name.split(" ")[1]);

					if (pat == null)
						break;

					Visit visit = new Visit();

					double weight = Double.parseDouble(br.readLine().trim().split(" ")[0]);
					double height = Double.parseDouble(br.readLine().trim().split(" ")[0]);

					String insurer = br.readLine().trim();

					Insurance_Providers ip = null;

					switch (insurer)
					{
					case "BlueCross Insurance":
					{
						ip = Insurance_Providers.Blue_Cross_Blue_Shield;
						break;
					}
					case "XYZ Insurance":
						ip = Insurance_Providers.XYZ;
						break;
					case "United HealthCare":
						ip = Insurance_Providers.United_Health;
						break;
					default:
						ip = Insurance_Providers.Other;
						break;
					}

					String summary = br.readLine().trim();

					String medicine = br.readLine().trim();

					visit.weight = weight;
					visit.height = height;
					visit.provider = ip;
					visit.summary = summary;
					visit.medication = medicine;

					pat.vList.add(visit);

					break;
				}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		JTextArea data = new JTextArea();
		data.setColumns(4);
		int xOff = 90;
		int bW = width - 2 * xOff, bH = 50;
		for (int i = 0; i < patients.size(); i++)
		{
			Patient p = patients.get(i);
			TransButton btn = new TransButton(String.format("%-5s  %-5s  %-5s  %-5s  %-5s", p.firstName, p.lastName,
					p.gender, p.sport, p.birth.toString()));
			btn.setBounds(xOff, 95 + i * bH, bW, bH);
			btn.setForeground(Color.black);
			btn.addMouseListener(new MouseAdapter()
			{

				boolean wasExpanded = false;

				int yShift = (int) btn.getY() - 95;

				@Override
				public void mouseEntered(MouseEvent e)
				{
					btn.hoveredOver = true;
				}

				@Override
				public void mouseExited(MouseEvent arg0)
				{
					btn.hoveredOver = false;
				}

				@Override
				public void mousePressed(MouseEvent e)
				{
					if (btn.hoveredOver)
					{
						wasExpanded = !wasExpanded;

						if (wasExpanded)
						{
							System.out.println("All people are visible.");

							for (int i = 0; i < bkgPanel.getComponentCount(); i++)
							{
								Component n = bkgPanel.getComponent(i);

								if (n == btn)
								{
									n.setBounds(n.getX(), n.getY() - yShift, n.getWidth(), n.getHeight());
									n.setVisible(false);
									n.setVisible(true);

									Patient pat = getPatient(patients, btn.getText().split("\\s")[0],
											btn.getText().split("\\s+")[1]);

									for (Visit v : pat.vList)
									{
										data.append(String.format("%-20s %-20s %-20s %-20s %-20s\n", v.weight, v.height,
												v.provider, v.summary,
												!v.medication.equals("NULL") ? v.medication : "-"));
									}
								}
								else
								{
									if (n != data)
									{
										n.setVisible(false);
									}
									else
									{
										data.setVisible(true);
									}
								}
							}
						}
						else
						{
							for (int j = 0; j < bkgPanel.getComponentCount(); j++)
							{
								Component n = bkgPanel.getComponent(j);

								if (n == btn)
								{
									n.setVisible(true);
									n.setBounds(n.getX(), n.getY() + yShift, n.getWidth(), n.getHeight());
									n.setVisible(false);
									n.setVisible(true);
								}
								else
								{
									if (n != data)
										n.setVisible(true);
									else
									{
										data.setText("");
									}
								}
							}
						}

						bkgPanel.setVisible(false);
						bkgPanel.setVisible(true);
					}
				}
			});
			btn.setFont(new Font("Raleway", Font.BOLD, 20));
			btn.setFocusable(false);
			btn.setOpaque(false);
			btn.setContentAreaFilled(true);
			btn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.black, Color.white));
			btn.setBorderPainted(true);

			data.setOpaque(false);
			data.setForeground(Color.black);
			data.setFont(new Font("Raleway", Font.BOLD, 16));
			data.setBounds(90, 250, width - 90 * 2, 800);
			data.setWrapStyleWord(true);
			data.setLineWrap(true);
			data.setEditable(false);

			bkgPanel.add(data);
			bkgPanel.add(btn);
		}

		frame.setContentPane(bkgPanel);

		frame.setVisible(true);
	}

	public void showInsurerScreen(String provider)
	{
		System.out.println("Showing doctor screen.");

		JPanel bkgPanel = new JPanel(null)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				g.drawImage(tile, 0, 0, width, height, null);
				g.setFont(new Font("Raleway", Font.BOLD, 24));
				g.drawString("Patient Records", width / 2 - 50, 30);
			}
		};

		BufferedReader br = null;

		ArrayList<Patient> patients = new ArrayList<Patient>();

		try
		{
			br = new BufferedReader(new FileReader("res/drset.txt"));

			String newLine = "";

			while ((newLine = br.readLine()) != null)
			{
				System.out.println(newLine);

				int firstSpace = newLine.indexOf(' ');

				if (firstSpace == -1)
					continue;

				String op_code = newLine.substring(0, firstSpace);

				switch (op_code)
				{
				case "Dr.":
				{
					String patient_name = "";
					while (!(patient_name = br.readLine().trim()).equals(""))
					{
						Patient empty_patient = new Patient();

						empty_patient.firstName = patient_name.split(" ")[0];
						empty_patient.lastName = patient_name.split(" ")[1];

						patients.add(empty_patient);
					}

					break;
				}
				case "P":
				{
					String name = newLine.substring(2).trim();

					Patient curr_patient = getPatient(patients, name.split(" ")[0], name.split(" ")[1]);

					if (curr_patient == null)
					{
						for (int i = 0; i < 4; i++)
						{
							br.readLine();
						}
						break;
					}

					curr_patient.gender = br.readLine().trim();
					curr_patient.sport = br.readLine().trim();

					String date = br.readLine().trim();

					System.out.println(date);

					curr_patient.birth = Date.valueOf(date);

					break;
				}
				case "V":
				{
					System.out.println("Visit time");

					String name = newLine.substring(2).trim();

					Patient pat = getPatient(patients, name.split(" ")[0], name.split(" ")[1]);

					if (pat == null)
						break;

					Visit visit = new Visit();

					double weight = Double.parseDouble(br.readLine().trim().split(" ")[0]);
					double height = Double.parseDouble(br.readLine().trim().split(" ")[0]);

					String insurer = br.readLine().trim();

					Insurance_Providers ip = null;

					switch (insurer)
					{
					case "BlueCross Insurance":
					{
						ip = Insurance_Providers.Blue_Cross_Blue_Shield;
						break;
					}
					case "XYZ Insurance":
						ip = Insurance_Providers.XYZ;
						break;
					case "United HealthCare":
						ip = Insurance_Providers.United_Health;
						break;
					default:
						ip = Insurance_Providers.Other;
						break;
					}

					String summary = br.readLine().trim();

					String medicine = br.readLine().trim();

					visit.weight = weight;
					visit.height = height;
					visit.provider = ip;
					visit.summary = summary;
					visit.medication = medicine;

					pat.vList.add(visit);

					break;
				}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		for (int i = patients.size() - 1; i >= 0; i--)
		{
			Patient p = patients.get(i);

			boolean exists = false;

			for (Visit v : p.vList)
			{
				if (v.provider.equals(toProvider(provider)))
				{
					exists = true;
				}
			}

			if (!exists)
			{
				System.out.println("Removing " + p);
				patients.remove(p);
			}
		}

		JTextArea data = new JTextArea();
		data.setColumns(4);
		int xOff = 90;
		int bW = width - 2 * xOff, bH = 50;
		for (int i = 0; i < patients.size(); i++)
		{
			Patient p = patients.get(i);
			TransButton btn = new TransButton(String.format("%-5s  %-5s  %-5s  %-5s  %-5s", p.firstName, p.lastName,
					p.gender, p.sport, p.birth.toString()));
			btn.setBounds(xOff, 95 + i * bH, bW, bH);
			btn.setForeground(Color.black);
			btn.addMouseListener(new MouseAdapter()
			{

				boolean wasExpanded = false;

				int yShift = (int) btn.getY() - 95;

				@Override
				public void mouseEntered(MouseEvent e)
				{
					btn.hoveredOver = true;
				}

				@Override
				public void mouseExited(MouseEvent arg0)
				{
					btn.hoveredOver = false;
				}

				@Override
				public void mousePressed(MouseEvent e)
				{
					if (btn.hoveredOver)
					{
						wasExpanded = !wasExpanded;

						if (wasExpanded)
						{
							System.out.println("All people are visible.");

							for (int i = 0; i < bkgPanel.getComponentCount(); i++)
							{
								Component n = bkgPanel.getComponent(i);

								if (n == btn)
								{
									n.setBounds(n.getX(), n.getY() - yShift, n.getWidth(), n.getHeight());
									n.setVisible(false);
									n.setVisible(true);

									Patient pat = getPatient(patients, btn.getText().split("\\s")[0],
											btn.getText().split("\\s+")[1]);

									for (Visit v : pat.vList)
									{
										data.append(String.format("%-20s %-20s %-20s %-20s %-20s\n", v.weight, v.height,
												v.provider, v.summary,
												!v.medication.equals("NULL") ? v.medication : "-"));
									}
								}
								else
								{
									if (n != data)
									{
										n.setVisible(false);
									}
									else
									{
										data.setVisible(true);
									}
								}
							}
						}
						else
						{
							for (int j = 0; j < bkgPanel.getComponentCount(); j++)
							{
								Component n = bkgPanel.getComponent(j);

								if (n == btn)
								{
									n.setVisible(true);
									n.setBounds(n.getX(), n.getY() + yShift, n.getWidth(), n.getHeight());
									n.setVisible(false);
									n.setVisible(true);
								}
								else
								{
									if (n != data)
										n.setVisible(true);
									else
									{
										data.setText("");
									}
								}
							}
						}

						bkgPanel.setVisible(false);
						bkgPanel.setVisible(true);
					}
				}
			});
			btn.setFont(new Font("Raleway", Font.BOLD, 20));
			btn.setFocusable(false);
			btn.setOpaque(false);
			btn.setContentAreaFilled(true);
			btn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.black, Color.white));
			btn.setBorderPainted(true);

			data.setOpaque(false);
			data.setForeground(Color.black);
			data.setFont(new Font("Raleway", Font.BOLD, 16));
			data.setBounds(90, 250, width - 90 * 2, 800);
			data.setWrapStyleWord(true);
			data.setLineWrap(true);
			data.setEditable(false);

			bkgPanel.add(data);
			bkgPanel.add(btn);
		}

		frame.setContentPane(bkgPanel);

		frame.setVisible(true);

	}

	public Insurance_Providers toProvider(String provider)
	{
		Insurance_Providers ip = null;

		switch (provider)
		{
		case "BlueCross Insurance":
		{
			ip = Insurance_Providers.Blue_Cross_Blue_Shield;
			break;
		}
		case "XYZ Insurance":
			ip = Insurance_Providers.XYZ;
			break;
		case "United HealthCare":
			ip = Insurance_Providers.United_Health;
			break;
		default:
			ip = Insurance_Providers.Other;
			break;
		}

		return ip;
	}

	public void saveToDatabase()
	{
		try
		{
			File file = new File("output.txt");
			FileWriter fw = new FileWriter(file);

			fw.close();
		} catch (IOException e)
		{
			System.out.println("");
			e.printStackTrace();
		}
	}

	void init()
	{

		frame = defaultFrame();

		Color green = new Color(34, 139, 34);

		BufferedImage icon = Resources.getImage("logo.png");

		frame.setIconImage(icon);

		JPanel bkgPanel = new JPanel(null)
		{

			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				g.drawImage(bkgImage, 0, 0, width, height, null);
				g.setColor(green);
				g.setFont(new Font("Raleway", Font.BOLD, 30));

				g.drawString("SPORTS CHAIN LOGIN", width / 2 - 170, 100);
			}
		};

		Font labelFont = new Font("Raleway", Font.BOLD, 20);

		usernameLabel = new JLabel("Username");
		usernameLabel.setHorizontalAlignment(JLabel.CENTER);
		passwordLabel = new JLabel("Password");
		passwordLabel.setHorizontalAlignment(JLabel.CENTER);
		usernameLabel.setForeground(Color.black);
		passwordLabel.setForeground(Color.black);

		int btnW = 200, btnH = 30;

		submitBtn = new JButton();

		submitBtn.setForeground(green);
		submitBtn.setBounds(width / 2 - btnW / 2, height / 2 + btnH + 40, btnW, btnH);
		submitBtn.setText("LOGIN");
		submitBtn.setFocusable(false);

		int txtW = 200, txtH = 30;

		usernameLabel.setFont(labelFont);
		usernameLabel.setBounds(width / 2 - txtW / 2, height / 2 - txtH - 100, txtW, txtH);

		passwordLabel.setFont(labelFont);
		passwordLabel.setBounds(width / 2 - txtW / 2, height / 2 - txtH - 10, txtW, txtH);

		username = new JTextField();
		password = new JPasswordField();

		username.setBounds(width / 2 - txtW / 2, height / 2 - txtH - 70, txtW, txtH);
		username.setFont(def_font);
		password.setBounds(width / 2 - txtW / 2, height / 2 - 10, txtW, txtH);
		password.setFont(def_font);

		submitBtn.setForeground(Color.black);
		submitBtn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, green, green));
		submitBtn.setBorderPainted(true);

		username.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, green, green));
		username.setHorizontalAlignment(JTextField.CENTER);

		password.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, green, green));
		password.setHorizontalAlignment(JTextField.CENTER);

		password.addKeyListener(new KeyAdapter()
		{

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					String usernameText = username.getText(), passwordText = new String(password.getPassword());

					System.out.println("Username: " + usernameText + ", password: " + passwordText);

					if (!usernameText.equals("") && !passwordText.equals(""))
					{
						validate(usernameText, passwordText);
					}
				}
			}
		});

		submitBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource() == submitBtn)
				{
					String usernameText = username.getText(), passwordText = new String(password.getPassword());

					if (!usernameText.equals("") && !passwordText.equals(""))
					{
						validate(usernameText, passwordText);
					}
				}
			}
		});

		submitBtn.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				submitBtn.setBackground(green);
				submitBtn.setForeground(Color.white);
			}

			@Override
			public void mouseExited(MouseEvent arg0)
			{
				submitBtn.setBackground(Color.white);
				submitBtn.setForeground(green);
			}
		});

		bkgPanel.add(submitBtn);
		bkgPanel.add(username);
		bkgPanel.add(password);
		bkgPanel.add(usernameLabel);
		bkgPanel.add(passwordLabel);

		bkgPanel.setVisible(true);

		frame.setContentPane(bkgPanel);
	}

	public Main()
	{
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

		width = 42 * screenDim.width / 100;
		height = screenDim.height / 2;

		init();

		logins = new HashMap<String, String>();
		typeof = new HashMap<String, Login_Type>();

		try
		{
			BufferedReader readLogins = new BufferedReader(new FileReader("res/logins.txt"));
			BufferedReader readTypeOf = new BufferedReader(new FileReader("res/usertype.txt"));

			String str = "";
			while ((str = readLogins.readLine()) != null)
			{
				String[] pieces = str.split(" ");

				if (pieces.length == 3)
					logins.put(pieces[0] + " " + pieces[1], pieces[2]);
				else
					logins.put(pieces[0], pieces[1]);
			}

			while ((str = readTypeOf.readLine()) != null)
			{
				String[] pieces = str.split(" ");

				String name, login_string;

				if (pieces.length == 3)
				{
					name = pieces[0] + " " + pieces[1];
					login_string = pieces[2];
				}
				else
				{
					name = pieces[0];
					login_string = pieces[1];
				}

				System.out.println("Logging in w/ " + login_string);

				Login_Type login = null;

				if (login_string.trim().equalsIgnoreCase("doctor"))
				{
					login = Login_Type.Doctor;
				}
				else if (login_string.trim().equalsIgnoreCase("client"))
				{
					login = Login_Type.Client;
				}
				else if (login_string.trim().equalsIgnoreCase("provider"))
				{
					login = Login_Type.Provider;
				}
				typeof.put(name, login);
			}

			readLogins.close();
			readTypeOf.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		System.out.println(logins);
		System.out.println(typeof);

		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		new Main();
	}
}