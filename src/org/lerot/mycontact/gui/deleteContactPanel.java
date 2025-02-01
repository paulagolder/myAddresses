package org.lerot.mycontact.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Vector;

import javax.swing.ButtonGroup;

import org.lerot.mywidgets.jswButton;
import org.lerot.mywidgets.jswCheckbox;
import org.lerot.mywidgets.jswHorizontalPanel;
import org.lerot.mywidgets.jswLabel;
import org.lerot.mywidgets.jswTable;
import org.lerot.mywidgets.jswTextBox;
import org.lerot.mywidgets.jswVerticalPanel;
import org.lerot.mycontact.mcContact;
import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcdb;

public class deleteContactPanel extends jswVerticalPanel implements
		ActionListener, ComponentListener
{
	private static final long serialVersionUID = 1L;
	private ButtonGroup bg;
	private jswCheckbox[] checkboxlist;
	mcContacts deleteablelist;
	private Vector<mcContact> deletelist;
	int nsearchcontacts;

	private jswLabel prog;
	jswTextBox searchfield;
	private jswTable resulttable;
	//private jswTable resulttable;

	public deleteContactPanel()
	{
        super("deletecongtent",false,false);
        this.addComponentListener(this);
		jswHorizontalPanel header = new jswHorizontalPanel();
		jswLabel heading = new jswLabel(" Delete Contacts ");
		header.add(" FILLW ", heading);
		this.add(header);
		jswHorizontalPanel printbar = new jswHorizontalPanel();
		this.add(printbar);
		jswButton testbutton = new jswButton(this, "DELETE SELECTED","deletesel");
		printbar.add(" MIDDLE ", testbutton);
		jswButton test2button = new jswButton(this, "DELETE UNSELECTED","deleteunsel");
		printbar.add(" MIDDLE ", test2button);
		resulttable = new jswTable(this,"contactsfound", mcdb.getTableStyles() );
		this.add(resulttable);
		jswHorizontalPanel progressbar = new jswHorizontalPanel();
		this.add(progressbar);
		prog = new jswLabel(" Selecting contacts to delete");
		progressbar.add(" FILLW ", prog);
		this.repaint();
		mcdb.topgui.mainpanel.repaint();
		mcdb.topgui.getContentPane().validate();

	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String action = evt.getActionCommand().toUpperCase();
		// TreeSet<mcContact> found = new TreeSet<mcContact>();
		System.out.println("action " + action);
		
		if (action.equals("DELETESEL"))
		{
			int delcount = 0;
			deletelist = new Vector<mcContact>();
			//mcContact mergeto = null;
			int row = 0;
			for (mcContact acontact : deleteablelist.makeOrderedContactsVector())
			{
				if (checkboxlist[row].isSelected())
				{
					deletelist.add(acontact);
					acontact.deleteContact();
					System.out.println("deleting"+acontact);
					delcount++;
				}
				row++;
			}
			
			System.out.println("deleted "+delcount);
			for(mcContact dcontact : deletelist)
			{
				deleteablelist.remove(dcontact);
			}
			refresh();
		} else if (action.equals("DELETEUNSEL"))
		{
			int delcount = 0;
			deletelist = new Vector<mcContact>();
			//mcContact mergeto = null;
			int row = 0;
			for (mcContact acontact : deleteablelist.makeOrderedContactsVector())
			{
				if (!checkboxlist[row].isSelected())
				{
					deletelist.add(acontact);
					acontact.deleteContact();
					System.out.println("deleting"+acontact);
					delcount++;
				}
				row++;
			}
			
			System.out.println("deleted "+delcount);
			for(mcContact dcontact : deletelist)
			{
				deleteablelist.remove(dcontact);
			}
			refresh();
		} else
			System.out.println("contact delete action " + action
					+ " unrecognised ");

		mcdb.topgui.getContentPane().validate();
	}

	
	 
	
	 

	public void initialise()
	{
		nsearchcontacts = mcdb.selbox.getSearchResultList().size();
		deleteablelist = mcdb.selbox.getSearchResultList();

		resulttable.removeAll();
		if (nsearchcontacts == 0)
		{
			
		} else 
		{
			checkboxlist = new jswCheckbox[nsearchcontacts];
			bg = new ButtonGroup();
		
			// this.setPreferredSize(new Dimension(0, 800));
			// resulttable.setPreferredSize(new Dimension(0, 800));

			int row = 0;
			for (mcContact acontact : deleteablelist.makeOrderedContactsVector())
			{
				jswLabel atid = new jswLabel(acontact.getIDstr());
				jswLabel atTID = new jswLabel(acontact.getTID());	
				resulttable.addCell(atid, row, 0);
				resulttable.addCell(atTID, row, 1);
				jswHorizontalPanel optionpanel = new jswHorizontalPanel();
				checkboxlist[row] = new jswCheckbox(this,"delete");
				optionpanel.add(checkboxlist[row]);
				resulttable.addCell(optionpanel, row, 3);
		
				row++;
			}

		} 

		this.repaint();
		mcdb.topgui.mainpanel.repaint();
		mcdb.topgui.getContentPane().validate();

	}
	
	public void refresh()
	{
		nsearchcontacts = deleteablelist.size();
		//deleteablelist = mcdb.selbox.getSearchResultList();

		resulttable.removeAll();
		if (nsearchcontacts == 0)
		{
			
		} else 
		{
			checkboxlist = new jswCheckbox[nsearchcontacts];
			bg = new ButtonGroup();
		
			// this.setPreferredSize(new Dimension(0, 800));
			// resulttable.setPreferredSize(new Dimension(0, 800));

			int row = 0;
			for (mcContact acontact : deleteablelist.makeOrderedContactsVector())
			{
				jswLabel atid = new jswLabel(acontact.getIDstr());
				jswLabel atTID = new jswLabel(acontact.getTID());	
				resulttable.addCell(atid, row, 0);
				resulttable.addCell(atTID, row, 1);
				jswHorizontalPanel optionpanel = new jswHorizontalPanel();
				checkboxlist[row] = new jswCheckbox(this,"delete");
				optionpanel.add(checkboxlist[row]);
				resulttable.addCell(optionpanel, row, 3);
		
				row++;
			}

		} 

		this.repaint();
		mcdb.topgui.mainpanel.repaint();
		mcdb.topgui.getContentPane().validate();

	}

	@Override
	public void componentHidden(ComponentEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void componentShown(ComponentEvent event) 
	{
		int n= mcdb.selbox.browsecontactlist.size();
	    initialise();
		System.out.println("contact delete - action View "+n );
	 
	}
}
