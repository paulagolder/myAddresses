package org.lerot.mycontact.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.ScrollPaneConstants;

import org.lerot.mywidgets.jswButton;
import org.lerot.mywidgets.jswDropDownBox;
import org.lerot.mywidgets.jswHorizontalPanel;
import org.lerot.mywidgets.jswLabel;
import org.lerot.mywidgets.jswScrollPane;
import org.lerot.mywidgets.jswTable;
import org.lerot.mywidgets.jswTextBox;
import org.lerot.mywidgets.jswVerticalPanel;
import org.lerot.mycontact.mcContact;
import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcdb;
import org.lerot.mycontact.mctagList;

public class editListPanel extends jswVerticalPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final int YES = 0;
	private jswDropDownBox taglistbox;
	private String tag;
	jswTextBox searchfield;
	private jswTable atttable;
	private jswButton deletebutton;
	private mcContacts selectedcontacts;
	private jswScrollPane scrollableTextArea;

	public editListPanel()
	{
        super("editlist",false,false);
        jswHorizontalPanel header = new jswHorizontalPanel();
		jswLabel heading = new jswLabel(" Edit Lists (Tags) ");
		header.add(" FILLW ", heading);
		this.add(header);

		jswHorizontalPanel toolbar = new jswHorizontalPanel("toolbar", false);
		deletebutton = new jswButton(this, "delete all", "deleteall");
		toolbar.add(deletebutton);
		this.add(" FILLW ", toolbar);
		jswHorizontalPanel progressbar = new jswHorizontalPanel("progressbar",
				false);
		this.add(" FILLW ", progressbar);
		taglistbox = new jswDropDownBox((ActionListener)this,"tags", "selectlist");
	
		mctagList tags = new mctagList();
		tags.reloadTags();
		progressbar.add(" FILLW ", taglistbox);
		atttable = new jswTable(this,"members", mcdb.getTableStyles());
		atttable.setBackground(Color.lightGray);
		atttable.setBorder(BorderFactory.createLineBorder(Color.blue));
		taglistbox.setList(tags.getTaglist());
		tag = taglistbox.getSelectedValue();
		displaylist(tag);
		jswScrollPane scrollpane = new jswScrollPane(atttable,0, 0);
		scrollpane.setName("resultscroll");
		scrollpane
				.setBorder(BorderFactory.createLineBorder(Color.green));
		this.add(" FILLH ", scrollpane);
		//scrollableTextArea.setMaximumSize(new Dimension(600, 200));
		atttable.setVisible(true);
		scrollpane.setVisible(true);
		this.repaint();
		mcdb.topgui.mainpanel.repaint();
		mcdb.topgui.getContentPane().validate();

	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String action = evt.getActionCommand().toUpperCase();

		if (action.equals("SELECTLIST"))
		{
			tag = taglistbox.getSelectedValue();
			displaylist(tag);

		} else if (action.equals("DELETEALL"))
		{
			String cnstr = action.substring(7);
			System.out.println("delete all listed contact ");
			int dcount = taglistbox.getItemCount();
			int n = JOptionPane.showConfirmDialog(this,
					"Do you want to delete " + dcount + " contacts",
					"DELETE CONTACTS?", JOptionPane.YES_NO_OPTION);
			System.out.println("reply =" + n);
			if (n == YES)
			{
				selectedcontacts.deleteAllContacts();
			}
			displaylist(tag);
		} else if (action.startsWith("DELETE"))
		{
			String cnstr = action.substring(7);
			System.out.println("delete contact " + cnstr);
			mcContact selcon = mcContacts.retrieveContact(cnstr);
			int n = JOptionPane.showConfirmDialog(this,
					"Do you want to delete contact:" + selcon.getName(),
					"DELETE CONTACT?", JOptionPane.YES_NO_OPTION);

			if (n == YES)
			{
				selcon.deleteContact();
				System.out.println("reply =" + n);
			}
			displaylist(tag);
		} else if (action.startsWith("REMOVE"))
		{
			String cnstr = action.substring(7);
			System.out.println("remove contact " + cnstr);
			mcContact selcon = mcContacts.retrieveContact(cnstr);
			int n = JOptionPane.showConfirmDialog(this,
					"Do you want to remove tag " + tag + " from :"
							+ selcon.getName(),
					"REMOVE CONTACT?", JOptionPane.YES_NO_OPTION);
			if (n == YES)
			{
				selcon.deleteTag(tag);
				// System.out.println("reply =" + n);
			}
			displaylist(tag);
		} else
			System.out.println("contact  action " + action + " unrecognised ");

		mcdb.topgui.getContentPane().validate();
	}

	public void displaylist(String tag)
	{

		selectedcontacts = mcdb.selbox.searchTag(tag);
		atttable.removeAll();
		int i = 0;
		if(selectedcontacts != null)
		{
		for (Entry<String, mcContact> contactentry : selectedcontacts
				.entrySet())
		{

			mcContact ct = contactentry.getValue();
			String cname = contactentry.getValue().getName();
			atttable.addCell(cname, i, 0);
			jswButton removecontact = new jswButton(this, "REMOVE",
					"REMOVE:" + ct.getIDstr());
			atttable.addCell(removecontact, i, 2);
			jswButton deletecontact = new jswButton(this, "DELETE",
					"DELETE:" + ct.getIDstr());
			atttable.addCell(deletecontact, i, 3);
			i++;
			// System.out.println(" adding "+ ct);
		}
		atttable.repaint();
		}

	}

	public void initialise()
	{

		mctagList tags = new mctagList();
		tags.reloadTags();
		taglistbox.setList(tags.getTaglist());
		displaylist(tags.get(0));
		this.repaint();
		mcdb.topgui.mainpanel.repaint();
		mcdb.topgui.getContentPane().validate();
		// taglist.addActionListener(this, "selectlist");

	}

	

}
