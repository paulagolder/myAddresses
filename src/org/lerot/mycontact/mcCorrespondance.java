package org.lerot.mycontact;

import java.util.ArrayList;
import java.util.Map;

public class mcCorrespondance extends mcDataObject 
{

	private int correspondanceid;
	private int cid;
	private String subject;
	private String date;
	private String mime;
	private String path;
	private String status;
	
	
	mcCorrespondance()
{
}
	public mcCorrespondance(int i)
	{
		setCorrespondanceid(i);
	}
		
		public void fill(Map<String, String> row)
		{
			setCid(Integer.parseInt(row.get("cid")));
			correspondanceid = Integer.parseInt(row.get("correspondanceid"));
			subject = row.get("subject");
			date = row.get("date");
			mime= row.get("mime");
			setPath(row.get("path"));
			setStatus(row.get("status"));
						
		}
		public int getCorrespondanceid()
		{
			return correspondanceid;
		}
		public void setCorrespondanceid(int correspondanceid)
		{
			this.correspondanceid = correspondanceid;
		}
		
		
		@Override
		public String toString()
		{
			
			return getStatus()+" "+date+""+ subject+ " "+mime;
		
		}
		
		public  void getLetter(int lettkey)
		{
			ArrayList<Map<String, String>> rowlist = doQuery(
					"select * from correspondance where correspondanceid = "+lettkey);
			//System.out.println("letters found:"+ rowlist.size()+" "+lettkey);	
			//mcCorrespondance aletter = new mcCorrespondance(0);
			//System.out.println(rowlist.toString());
			this.fill(rowlist.get(0));
			//return aletter;
		}
		
		public  void saveLetter()
		{
			doExecute("update correspondance set status ='"
			+this.status+"', date='"+this.date+"', subject = '"+this.subject+"' where correspondanceid = "+this.correspondanceid);
				
			System.out.println("letter updated:"+  +this.correspondanceid);	
		}
		
	
		public String getPath()
		{
			return path;
		}
		public void setPath(String text)
		{
			this.path = text;
		}
		public String getDate()
		{
			return date;
		}
		public void setDate(String text)
		{
			this.date = text;
		}
		public String getSubject()
		{
			return subject;
		}
		public void setSubject(String text)
		{
			this.subject = text;
		}
		public int getCid()
		{
			return cid;
		}
		public void setCid(int cid)
		{
			this.cid = cid;
		}
		public String getStatus()
		{
			return status;
		}
		public void setStatus(String test)
		{
			this.status = test;
		}
	
	
	
}
