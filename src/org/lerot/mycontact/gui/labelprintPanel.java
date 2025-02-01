package org.lerot.mycontact.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lerot.mywidgets.*;
import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcLetter;
import org.lerot.mycontact.mcPDF;
import org.lerot.mycontact.mcdb;

import static org.lerot.mycontact.mcdb.topgui;

public class labelprintPanel extends jswVerticalPanel implements ActionListener, ComponentListener
{
    private static final long serialVersionUID = 1L;
    private final jswOption selectedoption;
    private final jswOption hashtagsource;
    private final jswButton selbutton;
    private final jswTextBox selectedfile;
    private final jswLabel prog;
    private final jswDropDownBox layoutpanel;
    private final jswThumbwheel startpos;
    //private final jswLabel selectedcontacts;
    private final jswCheckbox showcountry;
    jswTextBox searchfield;
    private ActionListener parentlistener;
    private File exportfile;
    private mcPDF labelpages;
    private mcContacts sellist;

    public labelprintPanel()
    {

        super("labelprint", false, false);
        this.addComponentListener(this);
        int ncontacts = mcdb.selbox.countAll();
        int nbrowsecontacts = mcdb.selbox.getBrowsecontactlist().size();
        //String searchterm = mcdb.selbox.getSearchterm();
        this.removeAll();
        jswHorizontalPanel header = new jswHorizontalPanel();
        jswLabel heading = new jswLabel(" Print address Labels ");
        header.add(" FILLW ", heading);
        this.add(header);
        jswOptionset labelsource = new jswOptionset(this, "source", false, true, true);
        jswHorizontalPanel filterbar = new jswHorizontalPanel("fiterbar", true, true);
        // selectedcontacts = new jswLabel("Selected Contacts");
        // selectedcontacts.setText("Selected contacts (" + nbrowsecontacts + ")");
        selectedoption = labelsource.addNewOption("Selected contacts (" + nbrowsecontacts + ")", false);
        selectedoption.setTag("selecting");
        filterbar.add(" LEFT ", selectedoption);
        selectedoption.setStyleAttribute("width", 300);
        selectedoption.applyStyle();
        hashtagsource = labelsource.addNewOption("temptag", false);
        hashtagsource.setTag("hashtag");
        filterbar.add(" ", hashtagsource);
        hashtagsource.setStyleAttribute("mywidth", 300);
        hashtagsource.applyStyle();
        add(" fillw ", filterbar);
        jswHorizontalPanel filebar = new jswHorizontalPanel();
        selbutton = new jswButton(this, "Select");
        filebar.add(" LEFT ", selbutton);
        selectedfile = new jswTextBox(this, " ");
        selectedfile.setText("Output File");
        selectedfile.setEnabled(true);
        this.add(" ", filebar);
        filebar.add(" LEFT  ", selectedfile);
        selectedfile.setStyleAttribute("mywidth", 300);
        selectedfile.applyStyle();
        jswHorizontalPanel optionbar = new jswHorizontalPanel();
        layoutpanel = new jswDropDownBox(this, "Select layout");
        for (Entry<String, Map<String, String>> entry : mcdb.labeltemplates.entrySet())
        {
            layoutpanel.addItem(entry.getKey()); // paul fixing
        }
        optionbar.add(layoutpanel);
        startpos = new jswThumbwheel(this, "Start Position", 1, 10);
        //	startpos.setValue(1);
        optionbar.add(startpos);
        this.add(optionbar);
        jswHorizontalPanel countrybar = new jswHorizontalPanel();
        showcountry = new jswCheckbox(this, "Show UK?");
        showcountry.setEnabled(true);
        showcountry.setSelected(false);
        countrybar.add(showcountry);
        this.add(countrybar);
        jswHorizontalPanel printbar = new jswHorizontalPanel();
        this.add(printbar);
        jswButton testbutton = new jswButton(this, "PRINT");
        printbar.add(" MIDDLE ", testbutton);
        jswHorizontalPanel progressbar = new jswHorizontalPanel();
        this.add(progressbar);
        prog = new jswLabel(" Selecting source and output file ");
        progressbar.add(" FILLW ", prog);

        topgui.mainpanel.repaint();
        topgui.getContentPane().validate();
    }

    @Override
    public void actionPerformed(ActionEvent evt)
    {
        String cmd = evt.getActionCommand().toUpperCase();
        System.out.println(" here we are lp" + cmd);
        HashMap<String, String> cmdmap = jswUtils.parsecsvstring(cmd);
        String action = cmdmap.get("COMMAND").toUpperCase();
        System.out.println("action " + action);
        if (action.equals("OPTIONSELECTED"))
        {
            String sourcetag = cmdmap.get("VALUE");
            if (sourcetag.equalsIgnoreCase("hashtag"))
            {
                System.out.println("hashtag selected ");
                sellist = topgui.templist.toContactList();
            } else
            {
                sellist = mcdb.selbox.getBrowsecontactlist();
                System.out.println("selection selected ");
            }
        } else if (action.equals("SELECT"))
        {
            JFileChooser fc = new JFileChooser(mcdb.letterfolder);
            fc.setDialogTitle("Specify a file to save label");
            String labelname = mcLetter.makeFileName("Labels");
            fc.setSelectedFile(
                    new File(mcdb.letterfolder + "/" + labelname + ".pdf"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF",
                    "pdf");
            fc.setDialogTitle("Specify a file to save");
            fc.setFileFilter(filter);
            int returnVal = fc.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File fileToSave = fc.getSelectedFile();
                selectedfile.setText(fileToSave.getPath());
                exportfile = fileToSave;
                String filename = selectedfile.getText();
                File afile = new File(filename);
                //labelpages = new mcPDF(afile, "Lerot Contacts Labels");

            } else
            {
                System.out.println("Open command cancelled by user.");
            }
        } else if (action.startsWith("PRINT"))
        {
            prog.setText(" Printing ");
            mcdb.labeltemplates = mcPDF.readTemplates();
            String filename = selectedfile.getText();
            File afile = new File(filename);
            int sp = startpos.getValue();
            boolean showcountryselected = showcountry.isSelected();
            String pagelayout = layoutpanel.getSelectedValue();
            labelpages = new mcPDF(afile, "Lerot Contacts Labels");
            labelpages.setLayout(pagelayout);
            System.out.println("label print : " + sellist.size());
            int ncount = labelpages.makeLabelsPages(sellist, sp, showcountryselected);
            prog.setText(" Printing complete " + ncount + " pages");
        } else
            System.out.println("label print action " + action + " unrecognised ");
        topgui.refreshView();
        topgui.getContentPane().validate();
    }

    public void refresh()
    {
        mcContacts slist = mcdb.selbox.getBrowsecontactlist();
        int nbrowsecontacts = mcdb.selbox.getBrowsecontactlist().size();
        String searchterm = mcdb.selbox.getSearchterm();
        selectedoption.setText(searchterm + " (" + nbrowsecontacts + ")");
        System.out.println(" selected contacts " + nbrowsecontacts);
        int hashtagcontacts = topgui.templist.size();
        hashtagsource.setText("#TAGGED (" + hashtagcontacts + ")");
        System.out.println(" #tag contacts " + hashtagcontacts);
        topgui.refreshView();
        topgui.getContentPane().validate();
    }

    @Override
    public void componentResized(ComponentEvent componentEvent)
    {

    }

    @Override
    public void componentMoved(ComponentEvent componentEvent)
    {

    }

    @Override
    public void componentShown(ComponentEvent componentEvent)
    {
        refresh();
    }

    @Override
    public void componentHidden(ComponentEvent componentEvent)
    {

    }
}
