package gr.wind.spectra.business;

public class Update_CallerDataTable_ForMassiveOutage extends Thread
{
	private DB_Operations dbs;
	private s_DB_Operations s_dbs;
	String CLIProvided;
	String Company;
	String IncidentID;
	String allAffectedServices;
	String foundScheduled;
	String message;
	String backupEligible;
	String requestID;
	String systemID;

	public Update_CallerDataTable_ForMassiveOutage(DB_Operations dbs, s_DB_Operations s_dbs, String CLIProvided, String IncidentID,
                                                   String allAffectedServices, String foundScheduled, String message, String backupEligible, String requestID,
                                                   String systemID, String Company)
	{
		this.dbs = dbs;
		this.s_dbs = s_dbs;
		this.CLIProvided = CLIProvided;
		this.IncidentID = IncidentID;
		this.allAffectedServices = allAffectedServices;
		this.foundScheduled = foundScheduled;
		this.message = message;
		this.backupEligible = backupEligible;
		this.requestID = requestID;
		this.systemID = systemID;
		this.Company = Company;
	}

	@Override
	public void run()
	{
		// System.out.println("Running thread for Test Update_CallerDataTable...");
		Help_Func hf = new Help_Func();

		if (message == null) {
			message = "";
		}

		try
		{
				s_dbs.insertValuesInTable("Caller_Data",
						new String[] { "Requestor", "CliValue", "DateTimeCalled", "Affected_by_IncidentID",
								"AffectedServices", "Scheduled", "Message", "BackupEligible", "CSSCOLLECTIONNAME",
								"PAYTVSERVICES", "NGA_TYPE", "GeneralArea", "SiteName", "Concentrator", "AccessService",
								"PoP_Name", "PoP_Code", "OltElementName", "OltRackNo", "OltSubRackNo", "OltSlot",
								"OltPort", "Onu", "KvCode", "CabinetCode", "ActiveElement", "Rack", "Subrack", "Slot",
								"Port", "PORT_LOCATION", "PORT_CABLE_CODE", "PORT_ID", "CLID", "Username",
								"PASPORT_COID", "LOOP_NUMBER", "CLI_TYPE", "Domain", "ServiceType", "BRASNAME" },
						new String[] { systemID, CLIProvided, hf.now(), IncidentID, allAffectedServices, foundScheduled, message, backupEligible, "", "", "", "", "", "", "",
								"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
								"", "", "", "" },
						new String[] { "String", "String", "DateTime", "String", "String", "String", "String", "String",
								"String", "String", "String", "String", "String", "String", "String", "String",
								"String", "String", "String", "String", "String", "String", "String", "String",
								"String", "String", "String", "String", "String", "String", "String", "String",
								"String", "String", "String", "String", "String", "String", "String", "String",
								"String" });

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}