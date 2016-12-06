package com.support.SIDBMonitor;

import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.nio.file.*;

public class SIDBMonitor extends Thread {
	public static propertyHandler sidbmonitorProperties;
	public static String propertiesFile = "SIDBMonitor.properties";;
	public static String outputDir = "";
	public static String filename;
	public static String jsonFilename;
	public static BufferedWriter outputBuffer;
	public static BufferedWriter jsonOutputBuffer;

	public static ResultSet rset = null;
	public static Statement stmt = null;
	public static String result = "";
	public static boolean debugFlag = false;
	public static boolean fullReport = false;
	public static boolean appendMode = false;
	public static boolean encryptFlag = false;
	public static String schema = "";
	public static boolean transDataDistFlag = false;
	public static String DBVendor = "";

	/*
	 * Method that Writes results into the outputBuffer, if the show parameter
	 * is true it will also display the result into System.out
	 */
	public static void bufferAddString(String result, boolean show) {

		if (show && result != null && !result.isEmpty())
			System.out.println(result);
		try {

			if (result != null && !result.isEmpty()) {
				outputBuffer.write(result);
				outputBuffer.newLine();
				outputBuffer.flush();
				
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	/*
	 * Method that Writes results into the jsonOutputBuffer, if the show parameter
	 * is true it will also display the result into System.out
	 */
	public static void bufferAddJSONString(String result, boolean show) {

		
		try {

			if (result != null && !result.isEmpty()) {
				
				jsonOutputBuffer.write(result);
				if (result =="}") {jsonOutputBuffer.newLine();}
				
				jsonOutputBuffer.flush();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public static void createMonitorTableHeader(String[] reportHeader, int i,
			int tableWidth) {
		bufferAddString(
				"</p><br><table border=1 cellspacing=0 cellpadding=0 style='width:"
						+ tableWidth
						+ "pt;border-collapse:collapse; border:none'><tbody>",
				false);
		// build the table header
		int x = 0;
		bufferAddString("<tr class='header'>", false);
		while (x < i) {
			bufferAddString(
					"<th style='border:solid #7BA0CD 1.0pt; border-right:none;font-size: 8pt;'>"
							+ reportHeader[x] + "</th>", false);
						x++;

		}
		bufferAddString("</tr>", false);
	}

	public static void runMonitorSQL(String SQL, String description,String header) {
		// used for LW monitoring report
		
		long timeSpent = System.currentTimeMillis();
		try {

			//System.out.print(SQL+"...");
			System.out.print(description + "...");
			bufferAddString("<td>", false);

			rset = stmt.executeQuery(SQL);
			while (rset.next()) {
				result = rset.getString(1);

				bufferAddString(result, true);
				
				bufferAddJSONString("\""+header+"\":\"",false);
				
				bufferAddJSONString(result+"\"", false);
				if (header != "end_time"){
				bufferAddJSONString(",", false);}

				bufferAddString("</td>", false);

			}

		} catch (SQLException e1) {

			bufferAddString(".", true);
			System.out.println("failed SQL : " + description);
			if (debugFlag)
				e1.printStackTrace();
		}

		result = " [ Execution time -> "
				+ (System.currentTimeMillis() - timeSpent) + " ms ]";

		System.out.println(result);
	
	}

	public static void runSQLQuery(String SQL, String description) {
		// used for full report

		long timeSpent = System.currentTimeMillis();
		try {

			bufferAddString("<p>", false);
			bufferAddString(description, true);

			rset = stmt.executeQuery(SQL);
			while (rset.next()) {
				result = rset.getString(1);
				bufferAddString("<b>", false);
				bufferAddString(result, true);
				bufferAddString("</b>", false);

			}

		} catch (SQLException e1) {

			bufferAddString("Failed SQL... ", true);
			if (debugFlag)
				e1.printStackTrace();
		}

		result = "[Execution time in:  "
				+ (System.currentTimeMillis() - timeSpent) + " ms]";
		bufferAddString(result, true);
		bufferAddString("</p>", false);
		

	}

	public static void runSQLReportQuery(String SQL, String title,
			String description, int i, int tableWidth, String[] reportHeader) {
     //full report
		bufferAddString("<br><div class='title2'>", false);
		bufferAddString(title, true);
		bufferAddString("</div>", false);

		long timeSpent = System.currentTimeMillis();
		try {

			bufferAddString("<p>", false);
			bufferAddString(description, false);
			bufferAddString(
					"</p><br><table border=1 cellspacing=0 cellpadding=0 style='width:"
							+ tableWidth
							+ "pt;border-collapse:collapse; border:none'><tbody>",
					false);

			int x = 0;
			// build the table header
			bufferAddString("<tr class='header'>", false);
			while (x < i) {
				bufferAddString(
						"<th style='border:solid #7BA0CD 1.0pt; border-right:none;border-left:none;'>"
								+ reportHeader[x] + "</th>", false);

				x++;

			}
			bufferAddString("</tr>", false);

			int y = 0;

			rset = stmt.executeQuery(SQL);
			while (rset.next()) {

				x = 0;
				bufferAddString("<tr class='alternate" + y % 2 + "'>", false);
				while (x < i) {
					bufferAddString(
							"<td style='border:solid #7BA0CD 1.0pt; border-right:none;border-left:none;'>",
							false);
					result = rset.getString(x + 1);
					bufferAddString(result, false);
					x++;
					bufferAddString("</td>", false);
				}
				y++;
				bufferAddString("</tr>", false);
			}
		} catch (SQLException e1) {

			bufferAddString("Failed SQL... ", true);
			if (debugFlag)
				e1.printStackTrace();
		}

		bufferAddString("</tbody></table>", false);

		result = "[Execution time :  "
				+ (System.currentTimeMillis() - timeSpent) + " ms]";
		bufferAddString("<p>", false);
		bufferAddString(result, true);
		bufferAddString("</p><br>", false);

	}

	// =========================================================================
	public static void fullReportQueries() {

		if (DBVendor.substring(0, 6).equalsIgnoreCase("oracle")) {
			runSQLQuery(
					"select to_char(sysdate, '[Dy DD-Mon-YYYY] HH24:MI:SS')||' Connected to '||ora_database_name from dual",
					"Database info : ");
		}

		runSQLQuery("select BUILD_NUMBER from " + schema
				+ ".SI_VERSION where PRODUCT_LABEL='SI'", "SI build number : ");

		runSQLQuery("select count(*) from " + schema
				+ ".ARCHIVE_INFO where ARCHIVE_FLAG  in ( -1,-2,-5)",
				"Index count :");

		runSQLQuery("select count(*) from " + schema
				+ ".ARCHIVE_INFO where ARCHIVE_FLAG  in (1,2)",
				"Purge count  :");
		if (DBVendor.substring(0, 6).equalsIgnoreCase("oracle")) {
			runSQLQuery(
					"select count(*) from "
							+ schema
							+ ".ARCHIVE_INFO where ARCHIVE_FLAG in (1,2) and archive_date<sysdate",
					"Eligible Purge count  :");
		} else if (DBVendor.substring(0, 3).equalsIgnoreCase("db2")) {
			runSQLQuery(
					"select count(*) from "
							+ schema
							+ ".ARCHIVE_INFO where ARCHIVE_FLAG in (1,2) and archive_date<CURRENT TIMESTAMP",
					"Eligible Purge count  :");
		}
		else if (DBVendor.substring(0, 5).equalsIgnoreCase("micro")) {
			runSQLQuery(
					"select count(*) from "
							+ schema
							+ ".ARCHIVE_INFO where ARCHIVE_FLAG in (1,2) and ARCHIVE_DATE<GETDATE() ",
					"Eligible Purge count  :");
		}
		
		runSQLQuery(
				"select count(distinct WC.WORKFLOW_ID) from "
						+ schema
						+ ".WORKFLOW_CONTEXT WC, "
						+ schema
						+ ".ARCHIVE_INFO AI "
						+ " where WC.WFD_ID > 0 and (WC.BASIC_STATUS in (1, 2, 100, 200, 300)) AND "
						+ " AI.WF_ID = WC.WORKFLOW_ID AND AI.GROUP_ID = 1 AND AI.ARCHIVE_FLAG = -1",
				"Halted BPs  count  :");

		runSQLQuery(
				"SELECT count( distinct WC.WORKFLOW_ID) "
						+ "from "
						+ schema
						+ ".WORKFLOW_CONTEXT WC, "
						+ schema
						+ ".ARCHIVE_INFO AI "
						+ "WHERE WC.BASIC_STATUS = 450 "
						+ " AND AI.WF_ID = WC.WORKFLOW_ID AND AI.GROUP_ID = 1 AND AI.ARCHIVE_FLAG = -1 "
						+ " AND WC.WORKFLOW_ID NOT IN ( SELECT WC.WORKFLOW_ID "
						+ " from "
						+ schema
						+ ".WORKFLOW_CONTEXT WC, "
						+ schema
						+ ".ARCHIVE_INFO AI WHERE WC.BASIC_STATUS = 900 "
						+ " AND AI.WF_ID = WC.WORKFLOW_ID AND AI.GROUP_ID = 1 AND AI.ARCHIVE_FLAG = -1) ",
				"Manually Interrupted BPs count  :");

		String[] reportHeader0 = { "NODE NAME", "CREATE TIME",
				"HEARTBEAT TIME", "TOKEN", "STATUS", "EXT STATUS", "TYPE",
				"BASE PORT" };
		runSQLReportQuery(
				"select NODE_NAME,CREATE_TIMESTAMP,HEARTBT_TIMESTAMP,TOKEN,NODE_STATUS,EXTENDED_STATUS,NODE_TYPE,BASE_PORT from "
						+ schema + ".OPS_NODE_INFO", "SI Node(s) Info",
				"Node(s) information", 8, 620, reportHeader0);

		String[] reportHeader = { "ARCHIVE FLAG", "COUNT" };
		runSQLReportQuery(
				"select ARCHIVE_FLAG,count(*) from " + schema
						+ ".ARCHIVE_INFO group by ARCHIVE_FLAG",
				"Purge statistics :",
				"Purge statistics : ARCHIVE_FLAG : -1 -2 -5 Index, 0 : Backup, 1,2 : Purge",
				2, 136, reportHeader);

		String[] reportHeader11 = { "ARCHIVE FLAG", "GROUP_ID", "MIN DATE",
				"MAX DATE" };
		runSQLReportQuery(
				"SELECT ARCHIVE_FLAG,GROUP_ID, MIN(ARCHIVE_DATE) AS MIN_AD , MAX(ARCHIVE_DATE) AS MAX_AD "
						+ " FROM "
						+ schema
						+ ".ARCHIVE_INFO where ARCHIVE_FLAG >=0 GROUP BY ARCHIVE_FLAG,GROUP_ID",
				"Purge/Archive min/max dates :",
				"Purge/Archive statistics : min and max archive_dates by archive_flag",
				4, 560, reportHeader11);

		
		
		String[] reportHeader1 = { "ARCHIVE DATE Month", "COUNT" };

		if (DBVendor.substring(0, 5).equalsIgnoreCase("micro")) {
		runSQLReportQuery(
				"select CONVERT(VARCHAR(6), ARCHIVE_DATE,112),count(*) from "
						+ schema
						+ ".ARCHIVE_INFO where ARCHIVE_FLAG <> -1 group by CONVERT(VARCHAR(6), ARCHIVE_DATE,112) ",
				"Purge statistics by month :",
				"Purge/Backup statistics by month :", 2, 136, reportHeader1);
		}
		else {
			runSQLReportQuery(
			"select to_char(archive_date,'MM-YYYY'),count(*) from "
			+ schema
			+ ".archive_info where archive_flag <> -1 group by to_char(archive_date,'MM-YYYY') ",
	"Purge statistics by month :",
	"Purge/Backup statistics by month :", 2, 136, reportHeader1);
	
	}

		String[] reportHeader2 = { "ARCHIVE DATE HH24", "COUNT" };
		
		if (DBVendor.substring(0, 5).equalsIgnoreCase("micro")) {

		runSQLReportQuery(
				"select  DATEPART(hour,ARCHIVE_DATE),count(*) from "
						+ schema
						+ ".ARCHIVE_INFO where ARCHIVE_FLAG in (0,1,2) group by DATEPART(hour,ARCHIVE_DATE) order by 1",
				"Purge distribution per hour :",
				"Purge/Archive counts per hour cumulative :", 2, 136,
				reportHeader2);
		
		}
		
		else {
			runSQLReportQuery(
			"select  to_char(archive_date,'HH24'),count(*) from "
					+ schema
					+ ".archive_info where archive_flag in (0,1,2) group by to_char(archive_date,'HH24') order by 1",
			"Purge distribution per hour :",
			"Purge/Archive counts per hour cumulative :", 2, 136,
			reportHeader2);
		}

		String[] reportHeader3 = { "CREATE TIME Month", "COUNT" ,"SIZE MB"};
		
		if (DBVendor.substring(0, 5).equalsIgnoreCase("micro")) {
		runSQLReportQuery(
				"select convert(varchar(6),CREATE_TIME,112),count(*),cast(ROUND(sum(DOCUMENT_SIZE)/1024/1024,2,1) as decimal(10,2)) from " + schema
						+ ".DOCUMENT group by convert(varchar(6),CREATE_TIME,112) ",
				"Document statistics by month :",
				"Document statistics by month :", 3, 216, reportHeader3);
		
		}
		else {
		
			runSQLReportQuery(
					"select to_char(create_time,'MM-YYYY'),count(*),round(sum(DOCUMENT_SIZE)/1024/1024) from " + schema
							+ ".document group by to_char(create_time,'MM-YYYY') ",
					"Document statistics by month :",
					"Document statistics by month :", 3, 216, reportHeader3);
		}

		//	Workflow_context per hour 

		String[] reportHeader4 = { "START TIME HH24", "COUNT" };
		
		if (DBVendor.substring(0, 5).equalsIgnoreCase("micro")) {
		runSQLReportQuery(
				" select DATEPART(hour,START_TIME),count(DISTINCT WORKFLOW_ID) from "
						+ schema
						+ ".WORKFLOW_CONTEXT group by DATEPART(hour,START_TIME) order by 1  ",
				"Workflow_context per hour :",
				"Count of distinct BPs per hour cumulative :", 2, 136,
				reportHeader4);
		
		}
		else {
			runSQLReportQuery(
					" select to_char(start_time,'HH24'),count(DISTINCT workflow_id) from "
							+ schema
							+ ".workflow_context group by to_char(start_time,'HH24') order by 1  ",
					"Workflow_context per hour :",
					"Count of distinct BPs per hour cumulative :", 2, 136,
					reportHeader4);
			
		}
		
		//Communication Session per hour
String[] reportHeader18 = { "START TIME HH24", "COUNT" };
		
		if (DBVendor.substring(0, 5).equalsIgnoreCase("micro")) {
		runSQLReportQuery(
				" select DATEPART(hour,CON_START_TIME),count(*) from "
						+ schema
						+ ".ACT_SESSION group by DATEPART(hour,CON_START_TIME) order by 1  ",
				"Communication sessions per hour :",
				"Communication sessions per hour cumulative :", 2, 136,
				reportHeader18);
		
		}
		else {
			runSQLReportQuery(
					" select to_char(con_start_time,'HH24'),count(*) from "
							+ schema
							+ ".ACT_SESSION group by to_char(con_start_time,'HH24') order by 1  ",
					"Communication sessions per hour :",
					"Communication sessions per hour cumulative :", 2, 136,
					reportHeader18);
			
		}
		//
		

		if (DBVendor.substring(0, 6).equalsIgnoreCase("oracle")) {
			String[] reportHeader8 = { "SERVICE NAME", "MAX TIME (S)",
					"MIN_TIME(S)", "AVG TIME (S)", "EXEC COUNT" };
			runSQLReportQuery(
					"select SERVICE_NAME ,round(max(END_TIME-START_TIME)*86400) max_time_sec,round(min(END_TIME-START_TIME)*86400) min_time_sec  , "
							+ " round(avg(END_TIME-START_TIME)*86400) avg_time_sec, count(*) "
							+ "from "
							+ schema
							+ ".WORKFLOW_CONTEXT  group by SERVICE_NAME having round(max(END_TIME-START_TIME)*86400) > 0 order by 2 desc",
					"TOP Services statistics",
					"TOP services MAX, MIN, AVG execution times in seconds (MAX > 1s) ",
					5, 480, reportHeader8);
		}

		/*
		 * else if (DBVendor.substring(0, 3).equalsIgnoreCase("db2") ){}
		 */

		
		// TOP Business processes statistics
		String[] reportHeader9 = { "BP NAME", "NUM RUNS", "NUM STEPS" };
		runSQLReportQuery(
				"SELECT T2.NAME,  COUNT(DISTINCT WORKFLOW_ID) AS NO_OF_RUNS, "
						+ "COUNT(*) AS NO_OF_STEPS  from "
						+ schema
						+ ".WORKFLOW_CONTEXT T1, "
						+ schema
						+ ".WFD T2 "
						+ " WHERE T1.WFD_ID = T2.WFD_ID    AND T1.WFD_VERSION = T2.WFD_VERSION "
						+ " GROUP BY T2.NAME having COUNT(*)>100 order by 3 desc ",
				"TOP Business processes statistics",
				"TOP business process stats. number of steps (>100 total steps) and number of runs  ",
				3, 580, reportHeader9);

		String[] reportHeader10 = { "BP COUNT", "NODE NAME" };
		runSQLReportQuery(
				"select count(distinct WORKFLOW_ID),NODEEXECUTED from "
						+ schema + ".WORKFLOW_CONTEXT group by NODEEXECUTED",
				"NODE(S) Statistics",
				"Count of BP executions per cluster node :", 2, 200,
				reportHeader10);
		
		String[] reportHeader17 = { "PROTOCOL", "ADAPTER NAME","COUNT" };

		runSQLReportQuery("select  PROTOCOL,ADAPTER_NAME,COUNT(*) from "+ schema + ".ACT_SESSION group by ADAPTER_NAME, PROTOCOL HAVING COUNT(*) >10 ORDER BY 3 DESC",
				"Protocol statistics","Number of sessions per protocol and adapter name. session >10",3, 480, reportHeader17);
				

		if (DBVendor.substring(0, 6).equalsIgnoreCase("oracle")) {

			String[] reportHeader5 = { "TABLESPACE NAME", "TOTAL SPACE MB",
					"USED SPACE MB", "FREE SPACE MB", "PCT % FREE" };
			runSQLReportQuery(
					"SELECT df.tablespace_name TABLESPACE,  "
							+ "  df.total_space_mb TOTAL_SPACE_MB,"
							+ "(df.total_space_mb - fs.free_space_mb) USED_SPACE_MB, "
							+ " fs.free_space_mb FREE_SPACE_MB, "
							+ " ROUND(100 * (fs.free_space / df.total_space),2) PCT_FREE "
							+ " FROM (SELECT tablespace_name, SUM(bytes) TOTAL_SPACE, "
							+ " ROUND(SUM(bytes) / 1048576) TOTAL_SPACE_MB "
							+ " FROM dba_data_files "
							+ " GROUP BY tablespace_name) df, "
							+ " (SELECT tablespace_name, SUM(bytes) FREE_SPACE, "
							+ " ROUND(SUM(bytes) / 1048576) FREE_SPACE_MB "
							+ " FROM dba_free_space "
							+ " GROUP BY tablespace_name) fs "
							+ " WHERE df.tablespace_name = fs.tablespace_name(+) "
							+ " ORDER BY df.total_space_mb desc ",
					"Tablespace statistics :",
					"Tablespace statistics (space in MB):", 5, 360,
					reportHeader5);

			String[] reportHeader6 = { "TABLE NAME", "TABLESPACE", "NUM ROWS",
					"NUM BLOCKS", "LAST ANALAZED" };

			runSQLReportQuery(
					"select table_name,tablespace_name,num_rows,blocks,last_analyzed from user_tables where num_rows>10000 "
							+ " order by 3 desc",
					"TOP tables info",
					"Top tables (>10k rows) : number of rows and last analyzed data from user_tables : ",
					5, 680, reportHeader6);

			String[] reportHeader16 = { "OWNER", "TABLE NAME",
					"TOTAL SEGMENT SIZE MB" };

			runSQLReportQuery(
					"SELECT owner, table_name, TRUNC(sum(bytes)/1024/1024) "
							+ " FROM (SELECT segment_name table_name, owner, bytes "
							+ " FROM dba_segments WHERE segment_type = 'TABLE' "
							+ " UNION ALL "
							+ " SELECT i.table_name, i.owner, s.bytes "
							+ " FROM dba_indexes i, dba_segments s "
							+ " WHERE s.segment_name = i.index_name "
							+ " AND   s.owner = i.owner AND   s.segment_type = 'INDEX' "
							+ " UNION ALL "
							+ " SELECT l.table_name, l.owner, s.bytes FROM dba_lobs l, dba_segments s "
							+ " WHERE s.segment_name = l.segment_name AND   s.owner = l.owner AND   s.segment_type = 'LOBSEGMENT' "
							+ " UNION ALL "
							+ " SELECT l.table_name, l.owner, s.bytes FROM dba_lobs l, dba_segments s "
							+ " WHERE s.segment_name = l.index_name AND   s.owner = l.owner "
							+ " AND   s.segment_type = 'LOBINDEX') GROUP BY table_name, owner HAVING SUM(bytes)/1024/1024 > 100 "
							+ " ORDER BY SUM(bytes) desc ",
					"TOP TABLES > 100 MB",
					"TOP TABLES (TOTAL size table+Index+Lob segments) > 100 MB",
					3, 480, reportHeader16);

		
			
			
		
			if (transDataDistFlag) {
				String[] reportHeader13 = { "REF TABLE", "TYPE", "COUNT",
						"TOTAL MB" };
				runSQLReportQuery(
						"select reference_table,data_type,count(*),round(SUM (dbms_lob.getlength(DATA_OBJECT)) /1024/1024) total_MB from "
								+ schema
								+ ".trans_data group by reference_table,data_type order by 1,2",
						"TRANS_DATA Distribution : ",
						"TRANS_DATA Distribution and size by data_type and reference_table : ",
						4, 450, reportHeader13);

				String[] reportHeader12 = { "REF TABLE", "TYPE", "WF ID", "COUNT",
						"TOTAL MB" };
				runSQLReportQuery(
						"select reference_table,data_type,wf_id, count(*),round(SUM (dbms_lob.getlength(DATA_OBJECT)) /1024/1024) total_MB from "
								+ schema
								+ ".trans_data where wf_id <= 0 group by reference_table,data_type,wf_id order by 1,2",
						"TRANS_DATA (-1,0) Distribution : ",
						"TRANS_DATA Distribution and size by data_type,reference_table and wf_id for wf_id <=0 : ",
						5, 450, reportHeader12);

			}
		
		}

		
		
		//TOP Mailboxes
		
		String[] reportHeader7 = { "MAILBOX PATH", "MESSAGE COUNT" };

		
			
		
		runSQLReportQuery(
				"select PATH,count(*) from "
						+ schema
						+ ".MBX_MESSAGE mm, "
						+ schema
						+ ".MBX_MAILBOX mail "
						+ "  where mm.MAILBOX_ID = mail.MAILBOX_ID group by PATH having count(*) > 10 order by 2 desc ",
				"TOP Mailboxes",
				"TOP Mailboxes with message count > 10 messages : ", 2, 300,
				reportHeader7);
	
		
	}

	// =========================================================
	public static void monitoringLWReportQueries() {

		// read the property file

		String tableList = sidbmonitorProperties.getProperty("jdbc.table_list");

		String[] tbArray = tableList.split(",");

		List<String> reportHeaderList = new ArrayList<String>();
		List<String> reportSQLList = new ArrayList<String>();

		reportHeaderList.add("start time");
		String transData_1 = sidbmonitorProperties
				.getProperty("jdbc.trans_data-1");
		if (transData_1 == null) transData_1 = "true";
		String transData_0 = sidbmonitorProperties
				.getProperty("jdbc.trans_data-0");
		if (transData_0 == null) transData_0 = "true";
		String correlationSet_1 = sidbmonitorProperties
				.getProperty("jdbc.correlation_set-1");
		if (correlationSet_1 == null) correlationSet_1 = "true";
		String document_1 = sidbmonitorProperties
				.getProperty("jdbc.document-1");
		if (document_1 == null) document_1 = "true";
		String missingLifespanSweeper = sidbmonitorProperties
				.getProperty("jdbc.missingLifespanSweeper");
		if (missingLifespanSweeper == null) missingLifespanSweeper = "true";
		String correlationSweeper = sidbmonitorProperties
				.getProperty("jdbc.correlationSweeper");
		if (correlationSweeper == null) correlationSweeper = "true";
		String documentTotalSize = sidbmonitorProperties
				.getProperty("jdbc.documentTotalSize");
		if (documentTotalSize == null) documentTotalSize = "true";
		String eligiblePurgeCount = sidbmonitorProperties
				.getProperty("jdbc.eligiblePurgeCount");
		if (eligiblePurgeCount == null) eligiblePurgeCount = "true";
		String purgeCount = sidbmonitorProperties
				.getProperty("jdbc.purgeCount");
		if (purgeCount == null) purgeCount = "true";
		String nonIndexCount = sidbmonitorProperties
				.getProperty("jdbc.nonIndexCount");
		if (nonIndexCount == null) nonIndexCount = "true";
		String haltedBPCount = sidbmonitorProperties
				.getProperty("jdbc.haltedBPCount");
		if (haltedBPCount == null) haltedBPCount = "true";
		String interruptedBPCount = sidbmonitorProperties
				.getProperty("jdbc.interruptedBPCount");
		if (interruptedBPCount == null) interruptedBPCount = "true";
		String unassociatedCount = sidbmonitorProperties
				.getProperty("jdbc.unassociatedCount");
		if (unassociatedCount == null) unassociatedCount = "true";
		String transDataOrphansCount = sidbmonitorProperties
				.getProperty("jdbc.transDataOrphansCount");
		if (transDataOrphansCount == null) transDataOrphansCount = "true";
		String DBFreeSpace = sidbmonitorProperties
				.getProperty("jdbc.DBFreeSpace");
		if (DBFreeSpace == null) DBFreeSpace = "true";
		String DBTotalSpace = sidbmonitorProperties
				.getProperty("jdbc.DBTotalSpace");
		if (DBTotalSpace == null) DBTotalSpace = "true";
		String distinctWorkflowCount = sidbmonitorProperties
				.getProperty("jdbc.distinctWorkflowCount");
		if (distinctWorkflowCount == null) distinctWorkflowCount = "true";

		for (int i = 0; i < tbArray.length; i++)
			reportHeaderList.add(tbArray[i].replace("_", " "));

		// ToDO create a method

		if (transData_1.equalsIgnoreCase("true")
				|| transData_1.equalsIgnoreCase("false"))

		{
			if (Boolean.valueOf(transData_1)) {
				System.out.println("transData_1.. " + transData_1);
				reportHeaderList.add("trans data -1");
				
				if (DBVendor.substring(0, 3).equalsIgnoreCase("db2"))
				{	
				reportSQLList.add("select count(*) from " + schema
						+ ".TRANS_DATA where WF_ID =-1 with UR");
				}
				else {
					reportSQLList.add("select count(*) from " + schema
							+ ".TRANS_DATA where WF_ID =-1");
				
				}
				}

			}
		

		if (transData_0.equalsIgnoreCase("true")
				|| transData_0.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(transData_0)) {
				System.out.println("transData_0.. " + transData_0);
				reportHeaderList.add("trans data 0");
				if (DBVendor.substring(0, 3).equalsIgnoreCase("db2"))
				{	
				reportSQLList.add("select count(*) from " + schema
						+ ".TRANS_DATA where WF_ID =0 with UR");
				}
				else {
					reportSQLList.add("select count(*) from " + schema
							+ ".TRANS_DATA where WF_ID =0");
				}
			}
		}

		if (correlationSet_1.equalsIgnoreCase("true")
				|| correlationSet_1.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(correlationSet_1)) {
				System.out.println("correlationSet_1.. " + correlationSet_1);
				reportHeaderList.add("correl set -1");
				
				if (DBVendor.substring(0, 3).equalsIgnoreCase("db2"))
				{	
				reportSQLList.add("select count(*) from " + schema
						+ ".CORRELATION_SET where WF_ID=-1 with UR");
				}
				else {
					reportSQLList.add("select count(*) from " + schema
							+ ".CORRELATION_SET where WF_ID=-1");
				}
			}

		}

		if (document_1.equalsIgnoreCase("true")
				|| document_1.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(document_1)) {
				System.out.println("document_1.. " + document_1);
				reportHeaderList.add("document -1");
				
				if (DBVendor.substring(0, 3).equalsIgnoreCase("db2"))
				{
				reportSQLList.add("select count(*) from " + schema
						+ ".DOCUMENT where WORKFLOW_ID=-1 with UR");
				}
				else {
					reportSQLList.add("select count(*) from " + schema
							+ ".DOCUMENT where WORKFLOW_ID=-1");
				}

			}
		}
		
		if (distinctWorkflowCount.equalsIgnoreCase("true")
				|| distinctWorkflowCount.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(distinctWorkflowCount)) {
				System.out.println("distinctWorkflowCount.. "
						+ distinctWorkflowCount);
				reportHeaderList.add("Distinct WFID");
				reportSQLList
						.add("SELECT COUNT(DISTINCT WORKFLOW_ID) FROM "
								+ schema
								+ ".WORKFLOW_CONTEXT");
			}
		}
		

		if (missingLifespanSweeper.equalsIgnoreCase("true")
				|| missingLifespanSweeper.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(missingLifespanSweeper)) {
				System.out.println("missingLifespanSweeper.. "
						+ missingLifespanSweeper);
				reportHeaderList.add("MLS sweeper");
				reportSQLList.add("SELECT count(D.DOC_ID) from " + schema
						+ ".DOCUMENT D LEFT" + " OUTER JOIN " + schema
						+ ".DOCUMENT_LIFESPAN DL ON ( D.DOC_ID = DL.DOC_ID)"
						+ " WHERE DL.DOC_ID IS NULL AND D.WORKFLOW_ID = -1");
			}
		}

		if (correlationSweeper.equalsIgnoreCase("true")
				|| correlationSweeper.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(correlationSweeper)) {
				System.out
						.println("correlationSweeper.. " + correlationSweeper);
				reportHeaderList.add("CS sweeper");
				reportSQLList
						.add("SELECT count(DISTINCT A.OBJECT_ID) from "
								+ schema
								+ ".CORRELATION_SET A LEFT OUTER JOIN "
								+ schema
								+ ".DOCUMENT B ON (A.OBJECT_ID = B.DOC_ID) WHERE A.WF_ID <= 0 AND "
								+ "(B.WORKFLOW_ID IS NULL OR (B.WORKFLOW_ID != A.WF_ID))");
			}
		}

		if (documentTotalSize.equalsIgnoreCase("true")
				|| documentTotalSize.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(documentTotalSize)) {

				System.out.println("documentTotalSize.. " + documentTotalSize);
				reportHeaderList.add("doc size MB");

				if (DBVendor.substring(0, 5).equalsIgnoreCase("micro")) {

					reportSQLList.add("select cast(sum(DOCUMENT_SIZE)/1024/1024 as Decimal(12,2)) from "
									+ schema + ".DOCUMENT");

				}

				else {

					reportSQLList.add("select round(sum(DOCUMENT_SIZE)/1024/1024) from "
									+ schema + ".DOCUMENT");
				}
			}

		}

		// eligible purge count
		if (eligiblePurgeCount.equalsIgnoreCase("true")
				|| eligiblePurgeCount.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(eligiblePurgeCount)) {
				System.out
				.println("eligiblePurgeCount.. " + eligiblePurgeCount);
				reportHeaderList.add("eligible purge CT");
				
				if (DBVendor.substring(0, 5).equalsIgnoreCase("micro")) {
					
					reportSQLList
					.add("SELECT count(*) FROM "
							+ schema
							+ ".ARCHIVE_INFO where ARCHIVE_FLAG in (1,2) and ARCHIVE_DATE<GETDATE()");
					
				}
				
				else {
					
					reportSQLList
					.add("SELECT count(*) FROM "
							+ schema
							+ ".ARCHIVE_INFO where ARCHIVE_FLAG in (1,2) and ARCHIVE_DATE<sysdate");
				}
				
						
				
			}
		}
		
        // Purge count
		if (purgeCount.equalsIgnoreCase("true")
				|| purgeCount.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(purgeCount)) {
				System.out.println("purgeCount.. " + purgeCount);
				reportHeaderList.add("purge CT");
				reportSQLList.add("SELECT count(*) FROM " + schema
						+ ".ARCHIVE_INFO where ARCHIVE_FLAG  in (1,2)");

			}
		}

		if (nonIndexCount.equalsIgnoreCase("true")
				|| nonIndexCount.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(nonIndexCount)) {
				System.out.println("nonIndexCount.. " + nonIndexCount);
				reportHeaderList.add("non index CT");
				reportSQLList.add("select count(*) from " + schema
						+ ".ARCHIVE_INFO where ARCHIVE_FLAG < 0");
			}
		}
		if (haltedBPCount.equalsIgnoreCase("true")
				|| haltedBPCount.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(haltedBPCount)) {
				System.out.println("haltedBPCount.. " + haltedBPCount);
				reportHeaderList.add("haltedBP CT");
				reportSQLList
						.add("select count(distinct WC.WORKFLOW_ID) from "
								+ schema
								+ ".WORKFLOW_CONTEXT WC, "
								+ schema
								+ ".ARCHIVE_INFO AI "
								+ " where WC.WFD_ID > 0 and (WC.BASIC_STATUS in (1, 2, 100, 200, 300)) AND "
								+ " AI.WF_ID = WC.WORKFLOW_ID AND AI.GROUP_ID = 1 AND AI.ARCHIVE_FLAG = -1");
			}
		}
		if (interruptedBPCount.equalsIgnoreCase("true")
				|| interruptedBPCount.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(interruptedBPCount)) {
				System.out
						.println("interruptedBPCount.. " + interruptedBPCount);
				reportHeaderList.add("interrupted CT");
				reportSQLList
						.add("SELECT count( distinct WC.WORKFLOW_ID) "
								+ "from "
								+ schema
								+ ".WORKFLOW_CONTEXT WC, "
								+ schema
								+ ".ARCHIVE_INFO AI "
								+ "WHERE WC.BASIC_STATUS = 450 "
								+ " AND AI.WF_ID = WC.WORKFLOW_ID AND AI.GROUP_ID = 1 AND AI.ARCHIVE_FLAG = -1 "
								+ " AND WC.WORKFLOW_ID NOT IN ( SELECT WC.WORKFLOW_ID "
								+ " from "
								+ schema
								+ ".WORKFLOW_CONTEXT WC, "
								+ schema
								+ ".ARCHIVE_INFO AI WHERE WC.BASIC_STATUS = 900 "
								+ " AND AI.WF_ID = WC.WORKFLOW_ID AND AI.GROUP_ID = 1 AND AI.ARCHIVE_FLAG = -1) ");
			}
		}

		if (unassociatedCount.equalsIgnoreCase("true")
				|| unassociatedCount.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(unassociatedCount)) {
				System.out.println("unassociatedCount.. " + unassociatedCount);
				reportHeaderList.add("unassociated CT");
				if (DBVendor.substring(0, 6).equalsIgnoreCase("oracle")) {
					reportSQLList
							.add("SELECT count(distinct D.DOC_ID) from "
									+ schema
									+ ".DOCUMENT D, "
									+ schema
									+ ".DOCUMENT_LIFESPAN DL WHERE D.DOC_ID = DL.DOC_ID(+) "
									+ "and not exists (select * from "
									+ schema
									+ ".document_lifespan dl2 where dl2.doc_id = d.doc_id and (dl2.workflow_id > 0 or "
									+ "(D.CREATE_TIME + (DL2.LIFE_SPAN / 1440)) >= SYSDATE)) AND (D.WORKFLOW_ID = -1 OR D.WORKFLOW_ID = 0) and "
									+ "((D.CREATE_TIME + (DL.LIFE_SPAN / 1440)) < SYSDATE OR dl.DOC_ID IS NULL) and D.CREATE_TIME < SYSDATE -2 ");
				} else if (DBVendor.substring(0, 3).equalsIgnoreCase("db2")) {
					reportSQLList
							.add("SELECT count(distinct D.DOC_ID) from "
									+ schema
									+ ".DOCUMENT D LEFT OUTER JOIN "
									+ schema
									+ ".DOCUMENT_LIFESPAN DL ON D.DOC_ID = DL.DOC_ID "
									+ " WHERE NOT EXISTS (select * from "
									+ schema
									+ ".DOCUMENT_LIFESPAN dl2 where dl2.DOC_ID = d.DOC_ID and (dl2.WORKFLOW_ID > 0 or "
									+ "(D.CREATE_TIME + (DL2.LIFE_SPAN MINUTES)) >= CURRENT TIMESTAMP)) AND (D.WORKFLOW_ID <= 0) and "
									+ "((D.CREATE_TIME + (DL.LIFE_SPAN MINUTES)) < CURRENT TIMESTAMP OR DL.DOC_ID IS NULL) and D.CREATE_TIME < CURRENT TIMESTAMP -2 DAYS with UR");
				} else if (DBVendor.substring(0, 5).equalsIgnoreCase("micro")) {
					reportSQLList
							.add("SELECT count(distinct D.DOC_ID) from "
									+ schema
									+ ".DOCUMENT D LEFT OUTER JOIN "
									+ schema
									+ ".DOCUMENT_LIFESPAN DL ON D.DOC_ID = DL.DOC_ID "
									+ " WHERE NOT EXISTS (select * from "
									+ schema
									+ ".DOCUMENT_LIFESPAN DL2 where DL2.DOC_ID = D.DOC_ID and (D.WORKFLOW_ID > 0 or "
									+ "(D.CREATE_TIME + (DL2.LIFE_SPAN / 1440)) >= GETDATE())) AND (D.WORKFLOW_ID <= 0) and "
									+ "((D.CREATE_TIME + (DL.LIFE_SPAN / 1440)) < GETDATE() OR DL.DOC_ID IS NULL) and D.CREATE_TIME < GETDATE() -2 ");

				}

			}
		}

		if (transDataOrphansCount.equalsIgnoreCase("true")
				|| transDataOrphansCount.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(transDataOrphansCount)) {
				System.out.println("transDataOrphansCount.. "
						+ transDataOrphansCount);
				reportHeaderList.add("TD Orphans CT");
				reportSQLList
						.add("SELECT COUNT(*) FROM "
								+ schema
								+ ".TRANS_DATA WHERE (WF_ID=0 or WF_ID=-1) AND REFERENCE_TABLE ='DOCUMENT' "
								+ " AND NOT EXISTS(SELECT BODY FROM " + schema
								+ ".DOCUMENT WHERE BODY=" + schema
								+ ".TRANS_DATA.DATA_ID OR ENC_IV=" + schema
								+ ".TRANS_DATA.DATA_ID OR ENC_KEY=" + schema
								+ ".TRANS_DATA.DATA_ID) ");
			}
		}

		
		
		
		
		
		if (DBFreeSpace.equalsIgnoreCase("true")
				|| DBFreeSpace.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(DBFreeSpace)) {
				if (DBVendor.substring(0, 6).equalsIgnoreCase("oracle")) {

					System.out.println("Free Space GB.. " + DBFreeSpace);
					reportHeaderList.add("Free Space GB");
					reportSQLList
							.add("select round(sum(bytes)/1024/1024/1024,2) from dba_free_space");
				} else if (DBVendor.substring(0, 3).equalsIgnoreCase("db2")) {
					System.out.println("Free Space GB... " + DBFreeSpace);
					reportHeaderList.add("Free Space GB");
					reportSQLList
							.add("SELECT  (db_capacity-db_size)/1024/1024/1024 FROM systools.stmg_dbsize_info");

				}

			}
		}

		if (DBTotalSpace.equalsIgnoreCase("true")
				|| DBTotalSpace.equalsIgnoreCase("false")) {
			if (Boolean.valueOf(DBTotalSpace)) {

				if (DBVendor.substring(0, 6).equalsIgnoreCase("oracle")) {
					System.out.println("Total space GB.. " + DBTotalSpace);
					reportHeaderList.add("Total space GB");
					reportSQLList
							.add("select round(sum(bytes)/1024/1024/1024,2) from dba_data_files");

				} else if (DBVendor.substring(0, 3).equalsIgnoreCase("db2")) {
					System.out.println("Total space GB.. " + DBTotalSpace);
					reportHeaderList.add("Total space GB");
					reportSQLList
							.add("SELECT db_capacity/1024/1024/1024 FROM systools.stmg_dbsize_info");

				}
			}
		}

		// ToDO create a method

		reportHeaderList.add("end time");

		// create Header

		String[] reportHeader = reportHeaderList.toArray(new String[0]);

		if (!appendMode)
			createMonitorTableHeader(reportHeader, reportHeader.length,
					reportHeader.length * 45);

		// Run the LW monitoring SQLs
		bufferAddString("<tr>", false);
		bufferAddJSONString("{", false);

		runMonitorSQL(timeStamp(), "start time", "start_time");

		for (int i = 0; i < tbArray.length; i++) {
		
			if (DBVendor.substring(0, 3).equalsIgnoreCase("db2")) {
				runMonitorSQL("select count(*) from " + schema + "." + tbArray[i] + " with UR" ,
						tbArray[i] + " count",tbArray[i]);	
			}
			
			else {
			runMonitorSQL("select count(*) from " + schema + "." + tbArray[i],
					tbArray[i] + " count",tbArray[i]);
			}
		
		
		}

		//for (String s : reportSQLList) {
		int i=tbArray.length+1;
		for (String s : reportSQLList) {
			
			runMonitorSQL(s, s.substring(0, 30),reportHeaderList.get(i).toString());
         i++;
		}
 
		runMonitorSQL(timeStamp(), "end time","end_time");

		bufferAddString("</tr>", false);
		bufferAddJSONString("}", false);
	}

	// ==============================================
	public static String timeStamp() {

		if (DBVendor.substring(0, 3).equalsIgnoreCase("db2"))
			return "SELECT current timestamp FROM sysibm.sysdummy1";
		else if (DBVendor.substring(0, 6).equalsIgnoreCase("oracle"))
			return "select sysdate from dual";
		else
			return "SELECT GETDATE()";
	}

	// ==============================================
	public static void parseArgs(String[] args) {

		// Parse command line arguments
		for (int i = 0; i < args.length; i++) {
			if ( args[i].equalsIgnoreCase("-propFile") ) {
				i++;
				propertiesFile = args[i];
				System.out.println("Properties file: " + propertiesFile);
			}
			if ( args[i].equalsIgnoreCase("-outDir") ) {
				i++;
				// Check if output directory exists
				Path path = Paths.get(args[i]);
				if (Files.notExists(path)) {
					System.out.println("ERROR: output directory " + args[i] + " does not exist");
					System.exit(1);
				}
				outputDir = args[i] + "/";
				System.out.println("Output directory: " + outputDir);
			}
		}

		// Check if property file exists
		Path path = Paths.get(propertiesFile);
		if (Files.notExists(path)) {
			System.out.println("ERROR: properties file " + propertiesFile + " does not exist.");
			System.exit(1);
		}
	}

	/* ============================================== main */

	public static void main(String[] args) throws ClassNotFoundException,
			InterruptedException {

		System.out
				.println("==================================================");
		System.out
				.println("IBM Sterling B2B Integrator Database monitor 1.7.0");
		System.out
				.println("==================================================");
		
		// Parse arguments
		parseArgs(args);
		
		sidbmonitorProperties = new propertyHandler(propertiesFile);
		String url = sidbmonitorProperties.getProperty("jdbc.url");
		String username = sidbmonitorProperties.getProperty("jdbc.username");
		String password = sidbmonitorProperties.getProperty("jdbc.password");
		schema = sidbmonitorProperties.getProperty("jdbc.schema");
		String debug = sidbmonitorProperties.getProperty("jdbc.debug");
		String full = sidbmonitorProperties.getProperty("jdbc.fullReport");
		String encrypted = sidbmonitorProperties.getProperty("jdbc.encrypt");
		String transDataDist = sidbmonitorProperties.getProperty("jdbc.trans_data_dist");

		if (debug.equalsIgnoreCase("true") || debug.equalsIgnoreCase("false")) {
			debugFlag = Boolean.valueOf(debug);
			System.out.println("DEBUG.. " + debug);
		}

		if (full.equalsIgnoreCase("true") || full.equalsIgnoreCase("false")) {
			fullReport = Boolean.valueOf(full);
			System.out.println("fullReport.. " + full);
		}

		if (encrypted.equalsIgnoreCase("true")
				|| encrypted.equalsIgnoreCase("false")) {
			encryptFlag = Boolean.valueOf(encrypted);
			System.out.println("Encrypt.. " + encrypted);
		}

		if (transDataDist.equalsIgnoreCase("true") || transDataDist.equalsIgnoreCase("false")) {
			transDataDistFlag = Boolean.valueOf(transDataDist);
			System.out.println("transDataDist.. " + transDataDist);
		}

		if (encryptFlag) {

			String crypto;
			try {
				crypto = SecureString.decrypt("AES", password);
				password = crypto;

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {

			// display Encrypted Password
			String crypto;
			try {
				crypto = SecureString.encrypt("AES", password);
				System.out.println("Encrypted Password .. " + crypto);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// output file
		String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());

		if (fullReport) {
			filename = outputDir + "SIDBMonitorReport_" + timeStamp + ".html";
			jsonFilename= outputDir + "SIDBMonitorReport" +  timeStamp +".json";
		} else //LW report
		{
			filename = outputDir + "SIDBMonitorReport" + ".html";
		   jsonFilename= outputDir + "SIDBMonitorReport" + ".json";
		  
		
		}
		 File jsonFile = new File(jsonFilename);
		File myFile = new File(filename);
		if (myFile.exists())
			appendMode = true;
		
		if (jsonFile.exists())
			appendMode = true;

		FileWriter fstream;
		FileWriter fstream2;
		try {

			fstream = new FileWriter(filename, true);
			fstream2 = new FileWriter(jsonFilename, true);

			outputBuffer = new BufferedWriter(fstream);
			jsonOutputBuffer = new BufferedWriter(fstream2);
		} catch (IOException e) {

			e.printStackTrace();
		}

		HTMLDocument report = new HTMLDocument();
		if (!appendMode) {

			// Add HTML header
			result = report.addHeader().toString();
			bufferAddString(result, false);
			// End of HTML Header

		}

		// Print output filename
		System.out.println("HTML Output file.. " + filename);
		System.out.println("JSON Output file.. " + jsonFilename);
		// output file END

		// Connection test
		long timeSpent;

		timeSpent = System.currentTimeMillis();
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, username, password);
			result = "Connected to : " + url + " in:  "
					+ (System.currentTimeMillis() - timeSpent) + " ms";
			System.out.println("Connection Result: " + result);
			DBVendor = conn.getMetaData().getDatabaseProductName();
			System.out.println("Database Vendor... " + DBVendor);

			if (!appendMode)
				bufferAddString(result, true);

		} catch (SQLException e1) {

			System.out.println("ERROR: Connection failed please check your login details... ");
			e1.printStackTrace();

		}
		// The time to get a connection to the database

		if (conn != null)
			try {
				stmt = conn.createStatement();
			} catch (SQLException e1) {

				e1.printStackTrace();
			}

		if (fullReport)
			fullReportQueries();
		else
			monitoringLWReportQueries();

		System.out.println("JSON output " + jsonFilename);
		System.out.println("The Report was successfully generated! " + filename);
		

		// HTML footer

		if (fullReport) {
			result = report.addFooter().toString();
			bufferAddString(result, false);
		}

		/* closing */

		try {
			rset.close();

			stmt.close();
			conn.close();

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			outputBuffer.close();
			jsonOutputBuffer.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
