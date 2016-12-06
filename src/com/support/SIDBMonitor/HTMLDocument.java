package com.support.SIDBMonitor;

public class HTMLDocument {

	
	private StringBuilder HTMLHeader;
	private StringBuilder HTMLFooter;
	
	
	public  HTMLDocument()
	{
		HTMLHeader= new StringBuilder();
		HTMLFooter= new StringBuilder();
	}
			
	
	public StringBuilder addHeader()
	{
		
		HTMLHeader.append("<!doctype html>\n");
		HTMLHeader.append("<html lang='en'>\n") ;
		
		HTMLHeader.append("<head>\n");
		HTMLHeader.append("<meta charset='utf-8'>\n");
		HTMLHeader.append("<title>Database monitor report for IBM B2B Sterling Integrator 1.7</title>\n");
		HTMLHeader.append("<style type='text/css'>");
		HTMLHeader.append("body {   font-family: Verdana,Helvetica,Arial,sans-serif;   font-size: 10pt}\n");
		HTMLHeader.append("p {   font-family: Verdana,Helvetica,Arial,sans-serif;   font-size: 10pt}\n");
		HTMLHeader.append("div.title0 {color: #7896CF;font-family:Verdana,Helvetica,Arial,sans-serif;font-size:16pt;font-weight: bold;}\n");
		HTMLHeader.append("div.title1 {color: #336699;font-family:Verdana,Helvetica,Arial,sans-serif;font-size:16pt;font-weight: bold;border-bottom:1px solid #cccc99;margin-top:0pt; margin-bottom:0pt;padding:0px 0px 0px 0px;}\n");
		HTMLHeader.append("div.title2 {color: #7896CF;font-family:Verdana,Helvetica,Arial,sans-serif;font-size:12pt;font-weight: bold;}\n");
		HTMLHeader.append("div.title3 {color: #7896CF;font-family:Verdana,Helvetica,Arial,sans-serif;font-size:12pt;font-weight: bold;}\n");
		HTMLHeader.append(".Title {color: #7896CF; font-size: 10pt;font-weight: bold;}");
		HTMLHeader.append(".alternate0  {	background-color: #F2F9F9; color: black;}");
		HTMLHeader.append(".alternate1  {	background-color:#D3DFEE; color: black;}");
		HTMLHeader.append(".header  {	background-color:#336699; color: white;}");
		
		
		HTMLHeader.append("</style>");
		HTMLHeader.append("</head>\n\n");
		HTMLHeader.append("<body>\n\n");
		HTMLHeader.append("<div class='title1'>IBM Sterling B2B Integrator Database monitor 1.7</div><br/>");
			
		return HTMLHeader;
		
	}
	
	public StringBuilder addFooter()
	{
		
				
		HTMLFooter.append("\n\n</body>\n\n");
		HTMLFooter.append("</html>");
		return HTMLFooter;
	}
	
	
}
