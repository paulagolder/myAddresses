package org.lerot.mycontact.gui.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.imageio.ImageIO;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JLabel;
//import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.lerot.mywidgets.jswPanel;
import org.lerot.mywidgets.jswStyle;
import org.lerot.mywidgets.jswStyles;
import org.lerot.mycontact.mcAttribute;
import org.lerot.mycontact.mcContact;
import org.lerot.mycontact.mcUtilities;
import org.lerot.mycontact.mcdb;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.property.StructuredName;

public class jswDropPane extends jswPanel
{

	private static final long serialVersionUID = 1L;
	private DropTarget dropTarget;
	private DropTargetHandler dropTargetHandler;
	private Point dragPoint;

	private boolean dragOver = false;
	private BufferedImage target;

	private JLabel message;
	private String direction;
	protected jswStyles containerstyles = new jswStyles();
	private jswStyle style = new jswStyle();
	
	
	public jswDropPane(String adirection)
	{
		super(adirection);
		try
		{
			target = ImageIO
					.read(new File("/home/paul/.mccontacts/mccontacts.png"));
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
		String stylename = this.getClass().getSimpleName();		
		containerstyles.copyStyles(mcdb.panelstyles);
		style.copyAll(containerstyles.getStyle("jswContainer"));
		style.copyAll(containerstyles.getStyle(stylename));
		style.setStyleName(stylename);
		doStyling(style);
        direction = adirection;
		setLayout(new GridBagLayout());
		message = new JLabel(direction);
		message.setFont(message.getFont().deriveFont(Font.BOLD, 24));
		add(message);

	}

	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(100, 100);
	}

	protected DropTarget getMyDropTarget()
	{
		if (dropTarget == null)
		{
			dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE,
					null);
		}
		return dropTarget;
	}

	protected DropTargetHandler getDropTargetHandler()
	{
		if (dropTargetHandler == null)
		{
			dropTargetHandler = new DropTargetHandler();
		}
		return dropTargetHandler;
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		try
		{
			getMyDropTarget().addDropTargetListener(getDropTargetHandler());
		} catch (TooManyListenersException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void removeNotify()
	{
		super.removeNotify();
		getMyDropTarget().removeDropTargetListener(getDropTargetHandler());
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (dragOver)
		{
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setColor(new Color(0, 255, 0, 64));
			g2d.fill(new Rectangle(getWidth(), getHeight()));
			if (dragPoint != null && target != null)
			{
				int x = dragPoint.x - 12;
				int y = dragPoint.y - 12;
				g2d.drawImage(target, x, y, this);
			}
			g2d.dispose();
		}
	}

	protected void importFiles(final ArrayList<Path> files)
	{
		Runnable run = new Runnable()
		{
			@Override
			public void run()
			{
				mcContact selcontact = mcdb.selbox.getSelcontact();

				message.setText("You dropped " + files.size() + " files");
				// List objects =
				// (List)transfer.getTransferData(DataFlavor.javaFileListFlavor);
				for (Path object : files)
				{
					//File source = (File) object;
					String ext = mcUtilities.getFileExtension(object);
					if ("vcf".contentEquals(ext))
					{
						
						try
						{
							VCard vcard = Ezvcard.parse(object).first();
							selcontact.importVcard(vcard);
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
					File directory = new File(mcdb.docsfolder + File.separator
							+ selcontact.getID());
					if (!directory.exists())
					{
						directory.mkdir();
					}
					if (object instanceof java.nio.file.Path)
					{
						
						SimpleDateFormat sdf = new SimpleDateFormat(
								"YYYY-MM-dd");
                        BasicFileAttributes attr = null;
                        try
                        {
                            attr = Files.readAttributes(object, BasicFileAttributes.class);
                        } catch (IOException e)
                        {
                            throw new RuntimeException(e);
                        }

                        System.out.println("lastModifiedTime: " + attr.lastModifiedTime());
						String date = sdf.format(attr.lastModifiedTime());
						File dest = new File(mcdb.docsfolder + File.separator
								+ selcontact.getID() + File.separator
								+ object.getName(-1));
                        String status = direction;
						try
						{
							File afile = new File(object.toString());
							//Paths.get(afile).toAbsolutePath().toString();
							Files.copy(Paths.get(afile.getAbsolutePath()),
									Paths.get(dest.getAbsolutePath()),
									StandardCopyOption.REPLACE_EXISTING);

							if ("eml".contentEquals(ext))
							{
								Properties props = System.getProperties();
								props.put("mail.host", "smtp.dummydomain.com");
								props.put("mail.transport.protocol", "smtp");
								Session mailSession = Session
										.getDefaultInstance(props, null);
								InputStream isource = new FileInputStream(afile
										.getAbsolutePath());
								MimeMessage message = new MimeMessage(
										mailSession, isource);
								date = sdf.format(message.getSentDate());
								Address[] froms = message.getFrom();
								String from = ((InternetAddress) froms[0]).getAddress();
								Address[] tos =message.getAllRecipients();
								System.out.println(from.toString());
								System.out.println(((InternetAddress) tos[0]).getAddress());
							}else if ("doc".contentEquals(ext) ||"odt".contentEquals(ext)  )
								{
									status= "draft";
								}else if ("pdf".contentEquals(ext))
								{
									//status= "sent";
									
								}
								
							selcontact.addCorrespondance(afile.getName(), date,status,
									dest);


						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (MessagingException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					}

				}
				mcdb.topgui.refreshView();
			}

		};
		SwingUtilities.invokeLater(run);
	};

	protected class DropTargetHandler implements DropTargetListener
	{

		protected void processDrag(DropTargetDragEvent dtde)
		{
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
			} else
			{
				dtde.rejectDrag();
			}
		}

		@Override
		public void dragEnter(DropTargetDragEvent dtde)
		{
			processDrag(dtde);
			SwingUtilities
					.invokeLater(new DragUpdate(true, dtde.getLocation()));
			repaint();
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde)
		{
			processDrag(dtde);
			SwingUtilities
					.invokeLater(new DragUpdate(true, dtde.getLocation()));
			repaint();
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde)
		{
		}

		@Override
		public void dragExit(DropTargetEvent dte)
		{
			SwingUtilities.invokeLater(new DragUpdate(false, null));
			repaint();
		}

		@Override
		public void drop(DropTargetDropEvent dtde)
		{

			SwingUtilities.invokeLater(new DragUpdate(false, null));

			Transferable transferable = dtde.getTransferable();
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				dtde.acceptDrop(dtde.getDropAction());
				try
				{
					ArrayList transferdata1 = (ArrayList) transferable
							.getTransferData(DataFlavor.javaFileListFlavor);
					if (transferdata1 != null && transferdata1.size() > 0)
					{
						importFiles(transferdata1);
						dtde.dropComplete(true);
					}

				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			} else
			{
				dtde.rejectDrop();
			}
		}
	}

	public class DragUpdate implements Runnable
	{

		private boolean dragOver;
		private Point dragPoint;

		public DragUpdate(boolean dragOver, Point dragPoint)
		{
			this.dragOver = dragOver;
			this.dragPoint = dragPoint;
		}

		@Override
		public void run()
		{
			jswDropPane.this.dragOver = dragOver;
			jswDropPane.this.dragPoint = dragPoint;
			jswDropPane.this.repaint();
		}
	}


	public void doStyling()
	{
		// TODO Auto-generated method stub
		
	}


	public void doStyling(jswStyle style)
	{
		// TODO Auto-generated method stub
		
	}


	public jswStyle getStyle()
	{
		// TODO Auto-generated method stub
		return null;
	}


	public Dimension jswGetMinimumSize()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
