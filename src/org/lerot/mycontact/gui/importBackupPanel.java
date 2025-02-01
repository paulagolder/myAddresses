package org.lerot.mycontact.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lerot.mycontact.mcDateDataType;
import org.lerot.mycontact.mcImportXML;
import org.lerot.mycontact.mcImportexception;
import org.lerot.mycontact.mcMappings;
import org.lerot.mycontact.mcdb;
import org.lerot.mywidgets.jswButton;
import org.lerot.mywidgets.jswHorizontalPanel;
import org.lerot.mywidgets.jswLabel;
import org.lerot.mywidgets.jswTextBox;
import org.lerot.mywidgets.jswVerticalPanel;

public class importBackupPanel extends jswVerticalPanel
		implements ActionListener
{

	private static final long serialVersionUID = 1L;
	jswButton exceptionbutton;
	LinkedHashMap<String, mcImportexception> exceptions;
	private File exportfile;
	JFileChooser fc;
	jswHorizontalPanel importbar;
	jswButton importbutton;
	private jswVerticalPanel importerrors;
	jswLabel messagelabel;
	File importfile;

	boolean importtested;

	jswButton selbutton;

	jswTextBox selectedfile;

	private String importfilename;
	private String importtype;
	private mcMappings importmappings;
	private mcImportXML xmlimporter;

	public importBackupPanel()
	{
        super("backup",false,false);

        importtested = false;
		showImportBackupPanel();
	}

	public void showImportBackupPanel()
	{

		this.removeAll();
		mcdb.selbox.setVisible(true);
		jswHorizontalPanel header = new jswHorizontalPanel();
		jswLabel heading = new jswLabel(" import a backup ");
		header.add(" FILLW ", heading);
		this.add(header);
		jswHorizontalPanel filebar = new jswHorizontalPanel();
		selbutton = new jswButton(this, "Select");
		filebar.add(" LEFT ", selbutton);
		String bufilename = mcdb.topgui.budir + "/contacts_bu_"
				+ mcDateDataType.getNow("yyyyMMdd") + ".xml";

		selectedfile = new jswTextBox(this,bufilename);
		selectedfile.setText(bufilename);
		selectedfile.setEnabled(true);
		// selectedfile.setSize(200,20);
		// System.out.println(bufilename);
		filebar.add(" LEFT WIDTH=400  ", selectedfile);

		this.add(filebar);
		importbar = new jswHorizontalPanel();

		importbar.setVisible(true);

		exceptionbutton = new jswButton(this, "Print Exceptions");
		importbar.add(" MIDDLE ", exceptionbutton);
		exceptionbutton.setVisible(false);

		importbutton = new jswButton(this, "Import File");
		importbar.add(" RIGHT ", importbutton);
		importbutton.setVisible(false);

		this.add(importbar);
		importerrors = new jswVerticalPanel("title",false,false);;
		jswLabel imptrace = new jswLabel("");
		importerrors.add(imptrace);
		imptrace.setVisible(true);
		imptrace.setText(" Starting Import ");
		importerrors.setVisible(false);
		this.add(" FILLH ", importerrors);
		try
		{
			if (importfile.canRead()) importbar.setVisible(true);

		} catch (Exception e)
		{

		}
		// filebar.setPreferredSize(new Dimension(0, 40));

		this.setVisible(true);
		// panel1.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if (command == "Select")
		{
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Select a file to import");
			String bufilename = selectedfile.getText();
			File file = new File(bufilename);
			fc.setCurrentDirectory(file.getParentFile());

			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Backup", "xml", "XML");
			fc.setFileFilter(filter);
			int returnVal = fc.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{

				importfile = fc.getSelectedFile();
				importfilename = importfile.getPath();
				selectedfile.setText(importfilename);
				selectedfile.repaint();
				String extension = "";
				int i = importfile.getName().lastIndexOf('.');
				if (i > 0)
				{
					extension = importfile.getName().substring(i + 1);
				}

				exceptionbutton.setVisible(false);
				importbutton.setVisible(true);
				importerrors.setVisible(false);

			} else
			{
				System.out.println("Open command cancelled by user.");
			}

		} else if (command == "Print Exceptions")
		{
			JFileChooser fc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("text",
					"text");
			fc.setFileFilter(filter);
			int returnVal = fc.showSaveDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				exportfile = fc.getSelectedFile();
				PrintWriter writer;
				try
				{
					writer = new PrintWriter(exportfile, "UTF-8");
					for (Entry<String, mcImportexception> except : exceptions
							.entrySet())
					{
						mcImportexception impex = except.getValue();
						writer.println(impex.getToken() + "," + impex.getCount()
								+ ",\"" + impex.getExample() + "\"");
					}
					writer.close();
				} catch (FileNotFoundException
						| UnsupportedEncodingException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else if (command == "Import File")
		{
			importerrors.removeAll();
			importerrors.setVisible(true);
			jswHorizontalPanel runningmessage = new jswHorizontalPanel();
			importerrors.add(" FILLW ", runningmessage);
			messagelabel = new jswLabel(" ");
			runningmessage.add(" FILLW ", messagelabel);
			this.repaint();
			System.out.println(" import xml ");
			xmlimporter = new mcImportXML(importfilename);
			exceptions = xmlimporter.importall(false, messagelabel);
			// exceptions = mcdb.topgui.imported.makeImport(false,
			// messagelabel);
			if (exceptions.size() > 0) exceptionbutton.setVisible(true);
			importbutton.setVisible(false);
			System.out.println(" import  ended");
			mcdb.topgui.startup();
			this.repaint();

			System.out.println("  import finished");

		} else
			System.out.println(
					" command in importsetup not recognised " + command);
		this.repaint();
	}

	public void refresh()
	{
		// TODO Auto-generated method stub

	}

}
