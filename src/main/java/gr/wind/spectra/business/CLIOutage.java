package gr.wind.spectra.business;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

//Import log4j classes.
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gr.wind.spectra.model.ProductOfNLUActive;
import gr.wind.spectra.web.InvalidInputException;

public class CLIOutage
{
	private DB_Operations dbs;
	private s_DB_Operations s_dbs;
	private String requestID;
	private String systemID;

	Help_Func hf = new Help_Func();

	DateFormat dateFormat = new SimpleDateFormat(hf.DATE_FORMAT);

	// Logger instance
	Logger logger = LogManager.getLogger(gr.wind.spectra.business.CLIOutage.class.getName());

	public CLIOutage(DB_Operations dbs, s_DB_Operations s_dbs, String requestID, String systemID) throws Exception
	{
		this.dbs = dbs;
		this.s_dbs = s_dbs;
		this.requestID = requestID;
		this.systemID = systemID;
	}

	public String replaceHierarchyColumns(String hierarchyProvided, String technology)
			throws SQLException, InvalidInputException
	{
		Help_Func hf = new Help_Func();
		String newHierarchyValue = "";

		if (technology.equals("Voice"))
		{
			// Get root hierarchy String
			String rootElementInHierarchy = hf.getRootHierarchyNode(hierarchyProvided);

			String fullVoiceSubsHierarchyFromDB;
			String[] fullVoiceSubsHierarchyFromDBSplit;
			// Get Full Voice hierarchy in style :
			// OltElementName->OltSlot->OltPort->Onu->ActiveElement->Slot
			fullVoiceSubsHierarchyFromDB = dbs.getOneValue("HierarchyTablePerTechnology2",
					"VoiceSubscribersTableNamePath", new String[] { "RootHierarchyNode" },
					new String[] { rootElementInHierarchy }, new String[] { "String" });

			// Split the Data hierarchy retrieved from DB into fields
			fullVoiceSubsHierarchyFromDBSplit = fullVoiceSubsHierarchyFromDB.split("->");

			// Replace Hierarchy Columns from the relevant subscribers table
			newHierarchyValue = hf.replaceHierarchyForSubscribersAffected(hierarchyProvided,
					fullVoiceSubsHierarchyFromDBSplit);
		} else if (technology.equals("Data"))
		{
			// Get root hierarchy String
			String rootElementInHierarchy = hf.getRootHierarchyNode(hierarchyProvided);

			String fullVoiceSubsHierarchyFromDB;
			String[] fullVoiceSubsHierarchyFromDBSplit;
			// Get Full Voice hierarchy in style :
			// OltElementName->OltSlot->OltPort->Onu->ActiveElement->Slot
			fullVoiceSubsHierarchyFromDB = dbs.getOneValue("HierarchyTablePerTechnology2",
					"DataSubscribersTableNamePath", new String[] { "RootHierarchyNode" },
					new String[] { rootElementInHierarchy }, new String[] { "String" });

			// Split the Data hierarchy retrieved from DB into fields
			fullVoiceSubsHierarchyFromDBSplit = fullVoiceSubsHierarchyFromDB.split("->");

			// Replace Hierarchy Columns from the relevant subscribers table
			newHierarchyValue = hf.replaceHierarchyForSubscribersAffected(hierarchyProvided,
					fullVoiceSubsHierarchyFromDBSplit);
		} else if (technology.equals("IPTV"))
		{
			// Get root hierarchy String
			String rootElementInHierarchy = hf.getRootHierarchyNode(hierarchyProvided);

			String fullVoiceSubsHierarchyFromDB;
			String[] fullVoiceSubsHierarchyFromDBSplit;
			// Get Full Voice hierarchy in style :
			// OltElementName->OltSlot->OltPort->Onu->ActiveElement->Slot
			fullVoiceSubsHierarchyFromDB = dbs.getOneValue("HierarchyTablePerTechnology2",
					"IPTVSubscribersTableNamePath", new String[] { "RootHierarchyNode" },
					new String[] { rootElementInHierarchy }, new String[] { "String" });

			// Split the Data hierarchy retrieved from DB into fields
			fullVoiceSubsHierarchyFromDBSplit = fullVoiceSubsHierarchyFromDB.split("->");

			// Replace Hierarchy Columns from the relevant subscribers table
			newHierarchyValue = hf.replaceHierarchyForSubscribersAffected(hierarchyProvided,
					fullVoiceSubsHierarchyFromDBSplit);
		}
		return newHierarchyValue;
	}

	public ProductOfNLUActive checkAdHocOutage(String RequestID, String CLIProvided) throws SQLException, ParseException
	{
		ProductOfNLUActive ponla = new ProductOfNLUActive();
		Help_Func hf = new Help_Func();

		// Check if CLI is affected by AdHoc Outage
		// Get Lines with CliValue = CLIProvided
		ResultSet myRS = null;
		myRS = s_dbs.getRows("AdHocOutage_CLIS",
				new String[] { "CliValue", "Start_DateTime", "End_DateTime", "BackupEligible", "Message" },
				new String[] { "CliValue" }, new String[] { CLIProvided }, new String[] { "String" });

		while (myRS.next())
		{
			String AdHoc_CliValue = myRS.getString("CliValue").trim();
			Date AdHoc_StartTime = myRS.getTimestamp("Start_DateTime");
			Date AdHoc_EndTime = myRS.getTimestamp("End_DateTime");
			String BackupEligible = myRS.getString("BackupEligible").trim();
			String adHocMessage = myRS.getString("Message").trim();

			// If it's null it's msg2
			if (adHocMessage == null)
			{
				adHocMessage = "msg2";
			}

			// Backup Eligible response should be "Y" or "N"
			if (BackupEligible == "Yes")
			{
				BackupEligible = "Y";
			} else
			{
				BackupEligible = "N";
			}

			// Get current date
			LocalDateTime now = LocalDateTime.now();

			// Convert StartTime date to LocalDateTime object
			LocalDateTime StartTimeInLocalDateTime = Instant.ofEpochMilli(AdHoc_StartTime.getTime())
					.atZone(ZoneId.systemDefault()).toLocalDateTime();

			// Convert EndTime date to LocalDateTime object
			LocalDateTime EndTimeInLocalDateTime = Instant.ofEpochMilli(AdHoc_EndTime.getTime())
					.atZone(ZoneId.systemDefault()).toLocalDateTime();

			// if Start time is after NOW and End Time is Before NOW then we have outage
			if (now.isAfter(StartTimeInLocalDateTime) && now.isBefore(EndTimeInLocalDateTime))
			{
				logger.debug(
						"ReqID: " + RequestID + " - AdHoc Incident for CliValue " + AdHoc_CliValue + " is ongoing");

				// Convert End DateTime to String
				String AdHoc_EndTimeString = "";
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				AdHoc_EndTimeString = dateFormat.format(AdHoc_EndTime);

				ponla = new ProductOfNLUActive(this.requestID, CLIProvided, "Yes", "AdHoc_Outage", "Critical",
						"Voice|Data|IPTV", "Yes", null, AdHoc_EndTimeString, "LoS", adHocMessage, BackupEligible,
						"NULL");

				logger.info("SysID: " + systemID + " ReqID: " + RequestID + " - Found Affected CLI: " + CLIProvided
						+ " from AdHoc Outage - Message: " + adHocMessage + " | Backup: " + BackupEligible);

				// Update Statistics
				s_dbs.updateUsageStatisticsForMethod("AdHoc_Pos");

				// Update asynchronously - Add Caller to Caller data table (Caller_Data) with empty values for IncidentID, Affected Services & Scheduling
				Update_CallerDataTable ucdt = new Update_CallerDataTable(dbs, s_dbs, CLIProvided, "AdHoc_Outage",
						"Voice|Data|IPTV", "Yes", adHocMessage, BackupEligible, RequestID, systemID);
				ucdt.run();

				// Update asynchronously Stats_Pos_NLU_Requests to count number of successful NLU requests per CLI
				Update_ReallyAffectedTable uRat = new Update_ReallyAffectedTable(s_dbs, systemID,"AdHoc_Outage",
						"Voice|Data|IPTV", "Yes", CLIProvided);
				uRat.run();

				return ponla;
			} else
			{
				logger.debug(
						"ReqID: " + RequestID + " - AdHoc Incident for CliValue " + AdHoc_CliValue + " is NOT ongoing");
				continue;
			}

		}

		return null;
	}
	public ProductOfNLUActive checkCLIOutage(String RequestID, String CLIProvided, String ServiceType)
			throws Exception {
		ProductOfNLUActive ponla = new ProductOfNLUActive();
		boolean foundAtLeastOneCLIAffected = false;
		boolean voiceAffected = false;
		boolean dataAffected = false;
		boolean iptvAffected = false;
		ArrayList<String> allAffectedServices = new ArrayList<>();

		Help_Func hf = new Help_Func();

		// Check if we have Ad-Hoc Outage from Table: AdHocOutage_CLIS
		ProductOfNLUActive ponla_AdHoc = checkAdHocOutage(RequestID, CLIProvided);
		if (ponla_AdHoc != null)
		{
			return ponla_AdHoc;
		}

		// Check if we have at least one OPEN incident
		boolean weHaveOpenIncident = s_dbs.checkIfStringExistsInSpecificColumn("SubmittedIncidents",
				"IncidentStatus", "OPEN");

		// Services that will be checked
		// TODO: Input must come from NLU Active method
		String[] queryForServices;
		queryForServices = new String[] {"Voice", "Data", "IPTV"};

		logger.info("SysID: " + systemID + " ReqID: " + RequestID + " - Checking CLI Outage CLI: " + CLIProvided + " | " + ServiceType);

		// Split ServiceType
		String delimiterCharacter = "\\|";
		String[] ServiceTypeSplitted = ServiceType.split(delimiterCharacter);

		// No Open Incidents
		if (!weHaveOpenIncident) {
			// Update Statistics
			s_dbs.updateUsageStatisticsForMethod("NLU_Active_Neg");

			// Update asynchronously - Add Caller to Caller data table (Caller_Data) with empty values for IncidentID, Affected Services & Scheduling
			Update_CallerDataTable ucdt = new Update_CallerDataTable(dbs, s_dbs, CLIProvided, "", "", "", "", "",
					RequestID, systemID);
			ucdt.run();

			logger.info("SysID: " + systemID + " ReqID: " + RequestID + " - No Service affection for CLI: "
					+ CLIProvided + " | " + ServiceType);

			ponla = new ProductOfNLUActive(this.requestID, CLIProvided, "No", "none", "none", "none", "none", "none",
					"none", "none", "NULL", "NULL", "NULL");

			dbs = null;
			s_dbs = null;
			requestID = null;

			return ponla;
		}


		// We have at least one opened incident...
		String foundPriority = "";
		// String foundOutageAffectedService = "";
		String foundIncidentID = "";
		String foundScheduled = "";
		String foundDuration = "";
		Date foundStartTime = null;
		Date foundEndTime = null;
		String foundImpact = "";
		String EndTimeString = null;
		String foundOutageMsg = "";
		String foundBackupEligible = "";
		ProductOfNLUActive tofmTV_Result = null;
		Outage_For_Massive_TV tofmTV = null;

		ResultSet rs = null;

		// Get Lines with IncidentStatus = "OPEN" and WillBePublished = "Yes"
		rs = s_dbs.getRows( "SubmittedIncidents",
				new String[] { "WillBePublished", "IncidentID", "OutageID", "BackupEligible",
						"HierarchySelected", "Priority", "AffectedServices", "Scheduled", "Duration",
						"StartTime", "EndTime", "Impact", "OutageMsg" },
				new String[] { "WillBePublished", "IncidentStatus" }, new String[] { "Yes", "OPEN" }, new String[] { "String", "String" });

		for (String service: queryForServices) {
			while (rs.next()) {
				boolean isOutageWithinScheduledRange = false;
				String rootElementInHierarchy = "";
				String IncidentID = rs.getString("IncidentID");
				int OutageID = rs.getInt("OutageID");
				String HierarchySelected = rs.getString("HierarchySelected");
				String Priority = rs.getString("Priority");
				String outageAffectedService = rs.getString("AffectedServices");
				String Scheduled = rs.getString("Scheduled");
				String Duration = rs.getString("Duration");
				Date StartTime = rs.getTimestamp("StartTime");
				Date EndTime = rs.getTimestamp("EndTime");
				String Impact = rs.getString("Impact");
				String OutageMsg = rs.getString("OutageMsg");
				String BackupEligible = rs.getString("BackupEligible");

				// Backup Eligible response should be "Y" or "N"
				if (BackupEligible == null) {
					BackupEligible = "N";
				} else {
					if (BackupEligible.equals("Yes")) {
						BackupEligible = "Y";
					} else {
						BackupEligible = "N";
					}
				}

				// If it is OPEN & Scheduled & Date(Now) > StartTime then set
				// isOutageWithinScheduledRange to TRUE
				if (Scheduled.equals("Yes")) {
					// Get current date
					LocalDateTime now = LocalDateTime.now();

					// Convert StartTime date to LocalDateTime object
					LocalDateTime StartTimeInLocalDateTime = Instant.ofEpochMilli(StartTime.getTime())
							.atZone(ZoneId.systemDefault()).toLocalDateTime();

					// Convert EndTime date to LocalDateTime object
					LocalDateTime EndTimeInLocalDateTime = Instant.ofEpochMilli(EndTime.getTime())
							.atZone(ZoneId.systemDefault()).toLocalDateTime();

					// if Start time is after NOW and End Time is Before NOW then we have outage
					if (now.isAfter(StartTimeInLocalDateTime) && now.isBefore(EndTimeInLocalDateTime)) {
						isOutageWithinScheduledRange = true;
						logger.debug(
								"ReqID: " + RequestID + " - Scheduled Incident: " + IncidentID + " is ongoing");
					} else {
						isOutageWithinScheduledRange = false;
						logger.debug(
								"ReqID: " + RequestID + " - Scheduled Incident: " + IncidentID + " is NOT ongoing");
						continue;
					}
				}

				// If it is a Massive TV Outage Hierarchy then convert Cli to TV_ID and use Outage_For_Massive_TV Class
				if (HierarchySelected.equals("Massive_TV_Outage->TV_Service=ALL_Satellite_Boxes") ||
						HierarchySelected.equals("Massive_TV_Outage->TV_Service=ALL_EON_Boxes")

				) {
					if (iptvAffected) { continue; } // Already found IPTV Affection
					if (!outageAffectedService.equals("IPTV")){ continue; } // We care only for IPTV submitted incidents here

					// Check if Cli Value Exists in our Database
					if (!dbs.checkIfStringExistsInSpecificColumn("OTT_DTH_Data", "CLI_FIXED", CLIProvided)) {
						continue;
					}

					// Get the Value of TV_ID for that CLI Value
					String TV_ID = dbs.getOneValue("OTT_DTH_Data", "TV_ID", new String[] { "CLI_FIXED" }, new String[] { CLIProvided }, new String[] { "String" });
					TV_ID = TV_ID.trim();

					if (TV_ID == null || TV_ID.isEmpty()) {
						logger.warn("SysID: " + systemID + " ReqID: " + RequestID + " - TV_ID is not defined in OTT_DTH_Data Table for Cli Value: "
								+ CLIProvided);
						continue;
					}

					logger.info("SysID: " + systemID + " ReqID: " + RequestID + " - Converting CLI: " + CLIProvided + " --> TV_ID: " + TV_ID);

					tofmTV = new Outage_For_Massive_TV(dbs, s_dbs, RequestID, systemID);
					tofmTV_Result = tofmTV.checkMassiveTVOutage(RequestID, TV_ID);

					// If There is Massive TV Service Affection Then Publish it
					if (tofmTV_Result.getAffected().equals("Yes")) {
						if (Scheduled.equals("No") || (Scheduled.equals("Yes") && isOutageWithinScheduledRange)) {
							foundIncidentID = tofmTV_Result.getIncidentID();
							foundPriority = tofmTV.getPriority();
							foundScheduled = tofmTV_Result.getScheduled();
							foundDuration = tofmTV_Result.getDuration();
							foundStartTime = tofmTV.getStartTime();
							foundEndTime = tofmTV.getEndTime();
							foundImpact = tofmTV.getImpact();
							foundOutageMsg = tofmTV.getOutageMsg();
							foundBackupEligible = BackupEligible;

							foundAtLeastOneCLIAffected = true;
							iptvAffected = true;

							logger.info("SysID: " + systemID + " ReqID: " + RequestID + " - Found Affected CLI: "
									+ CLIProvided + " -> TV_ID: " + TV_ID + " from Massive INC: " + tofmTV_Result.getIncidentID()
									+ " | " + tofmTV.getTypeOfMassiveTVOutage() + " | "
									+ foundOutageMsg + " | " + BackupEligible);
						}
						// Found affected for service IPTV so now break
						break;
					}
					// Do not perform the below checks for those 2 specific massive hierarchies
					continue;
				}

				if (outageAffectedService.equals("Voice") && Arrays.asList(queryForServices).contains("Voice")) {
					// Replace Hierarchy keys from the correct column names of Hierarchy Subscribers table
					HierarchySelected = this.replaceHierarchyColumns(HierarchySelected, "Voice");

					// Add CLI Value in Hierarcy
					HierarchySelected += "->CliValue=" + CLIProvided;

					// Get root hierarchy String
					rootElementInHierarchy = hf.getRootHierarchyNode(HierarchySelected);

					// Get Hierarchy Table for that root hierarchy
					String table = dbs.getOneValue("HierarchyTablePerTechnology2", "VoiceSubscribersTableName",
							new String[]{"RootHierarchyNode"}, new String[]{rootElementInHierarchy},
							new String[]{"String"});

					String numOfRowsFound = dbs.numberOfRowsFound(table, hf.hierarchyKeys(HierarchySelected),
							hf.hierarchyValues(HierarchySelected), hf.hierarchyStringTypes(HierarchySelected));

					if (Integer.parseInt(numOfRowsFound) > 0) {
						foundAtLeastOneCLIAffected = true;
						voiceAffected = true;

						if (Scheduled.equals("Yes") && isOutageWithinScheduledRange) {
							foundIncidentID = IncidentID;
							foundPriority = Priority;
							foundScheduled = Scheduled;
							foundDuration = Duration;
							foundStartTime = StartTime;
							foundEndTime = EndTime;
							foundImpact = Impact;
							foundOutageMsg = OutageMsg;
							foundBackupEligible = BackupEligible;

							logger.info("SysID: " + systemID + " ReqID: " + RequestID + " - Found Affected CLI: "
									+ CLIProvided + " from Scheduled INC: " + IncidentID
									+ " | OutageID: " + OutageID + " | " + outageAffectedService + " | "
									+ foundOutageMsg + " | " + BackupEligible);
							break;
						}

						// Not Scheduled Incident But Affected Service
						foundIncidentID = IncidentID;
						foundPriority = Priority;
						foundScheduled = Scheduled;
						foundDuration = Duration;
						foundStartTime = StartTime;
						foundEndTime = EndTime;
						foundImpact = Impact;
						foundOutageMsg = OutageMsg;
						foundBackupEligible = BackupEligible;

						logger.info("SysID: " + systemID + " ReqID: " + RequestID + " - Found Affected CLI: "
								+ CLIProvided + " from Non-scheduled INC: " + IncidentID
								+ " | OutageID: " + OutageID + " | " + outageAffectedService + " | "
								+ foundOutageMsg + " | " + BackupEligible);
						break;
					}
				}

				if (outageAffectedService.equals("Data") && Arrays.asList(queryForServices).contains("Data")) {
					// Replace Hierarchy keys from the correct column names of Hierarchy Subscribers table
					HierarchySelected = this.replaceHierarchyColumns(HierarchySelected, "Data");

					// Add CLI Value in Hierarcy
					HierarchySelected += "->CliValue=" + CLIProvided;

					// Get root hierarchy String
					rootElementInHierarchy = hf.getRootHierarchyNode(HierarchySelected);

					// Get Hierarchy Table for that root hierarchy
					String table = dbs.getOneValue("HierarchyTablePerTechnology2", "DataSubscribersTableName",
							new String[]{"RootHierarchyNode"}, new String[]{rootElementInHierarchy},
							new String[]{"String"});

					String numOfRowsFound = dbs.numberOfRowsFound(table, hf.hierarchyKeys(HierarchySelected),
							hf.hierarchyValues(HierarchySelected), hf.hierarchyStringTypes(HierarchySelected));

					if (Integer.parseInt(numOfRowsFound) > 0)
					{
						foundAtLeastOneCLIAffected = true;
						dataAffected = true;

						if (Scheduled.equals("Yes") && isOutageWithinScheduledRange) {
							foundIncidentID = IncidentID;
							foundPriority = Priority;
							foundScheduled = Scheduled;
							foundDuration = Duration;
							foundStartTime = StartTime;
							foundEndTime = EndTime;
							foundImpact = Impact;
							foundOutageMsg = OutageMsg;
							foundBackupEligible = BackupEligible;

							logger.info("SysID: " + systemID + " ReqID: " + RequestID + " - Found Affected CLI: "
									+ CLIProvided + " from Scheduled INC: " + IncidentID
									+ " | OutageID: " + OutageID + " | " + outageAffectedService + " | "
									+ foundOutageMsg + " | " + BackupEligible);
							break;
						}

						// Not Scheduled Incident But Affected Service
						foundIncidentID = IncidentID;
						foundPriority = Priority;
						foundScheduled = Scheduled;
						foundDuration = Duration;
						foundStartTime = StartTime;
						foundEndTime = EndTime;
						foundImpact = Impact;
						foundOutageMsg = OutageMsg;
						foundBackupEligible = BackupEligible;

						logger.info("SysID: " + systemID + " ReqID: " + RequestID + " - Found Affected CLI: "
								+ CLIProvided + " from Non-scheduled INC: " + IncidentID
								+ " | OutageID: " + OutageID + " | " + outageAffectedService + " | "
								+ foundOutageMsg + " | " + BackupEligible);
						break;
					}
				}

				if (outageAffectedService.equals("IPTV") && Arrays.asList(queryForServices).contains("IPTV")) {
					// Replace Hierarchy keys from the correct column names of Hierarchy Subscribers table
					HierarchySelected = this.replaceHierarchyColumns(HierarchySelected, "IPTV");

					// Add CLI Value in Hierarcy
					HierarchySelected += "->CliValue=" + CLIProvided;

					// Get root hierarchy String
					rootElementInHierarchy = hf.getRootHierarchyNode(HierarchySelected);

					// Get Hierarchy Table for that root hierarchy
					String table = dbs.getOneValue("HierarchyTablePerTechnology2", "IPTVSubscribersTableName",
							new String[]{"RootHierarchyNode"}, new String[]{rootElementInHierarchy},
							new String[]{"String"});

					String numOfRowsFound = dbs.numberOfRowsFound(table, hf.hierarchyKeys(HierarchySelected),
							hf.hierarchyValues(HierarchySelected), hf.hierarchyStringTypes(HierarchySelected));

					if (Integer.parseInt(numOfRowsFound) > 0)
					{
						foundAtLeastOneCLIAffected = true;
						iptvAffected = true;

						if (Scheduled.equals("Yes") && isOutageWithinScheduledRange) {
							foundIncidentID = IncidentID;
							foundPriority = Priority;
							foundScheduled = Scheduled;
							foundDuration = Duration;
							foundStartTime = StartTime;
							foundEndTime = EndTime;
							foundImpact = Impact;
							foundOutageMsg = OutageMsg;
							foundBackupEligible = BackupEligible;

							logger.info("SysID: " + systemID + " ReqID: " + RequestID + " - Found Affected CLI: "
									+ CLIProvided + " from Scheduled INC: " + IncidentID
									+ " | OutageID: " + OutageID + " | " + outageAffectedService + " | "
									+ foundOutageMsg + " | " + BackupEligible);
							break;
						}

						// Not Scheduled Incident But Affected Service
						foundIncidentID = IncidentID;
						foundPriority = Priority;
						foundScheduled = Scheduled;
						foundDuration = Duration;
						foundStartTime = StartTime;
						foundEndTime = EndTime;
						foundImpact = Impact;
						foundOutageMsg = OutageMsg;
						foundBackupEligible = BackupEligible;

						logger.info("SysID: " + systemID + " ReqID: " + RequestID + " - Found Affected CLI: "
								+ CLIProvided + " from Non-scheduled INC: " + IncidentID
								+ " | OutageID: " + OutageID + " | " + outageAffectedService + " | "
								+ foundOutageMsg + " | " + BackupEligible);
						break;
					}
				}

			}
		}

		// CLI is not affected from outage
		if (!foundAtLeastOneCLIAffected) {
			// Update Statistics
			s_dbs.updateUsageStatisticsForMethod("NLU_Active_Neg");

			logger.info("SysID: " + systemID + " ReqID: " + RequestID + " - No Service affection for CLI: "
					+ CLIProvided + " | " + ServiceType);

			// Update asynchronously - Add Caller to Caller data table (Caller_Data) with empty values for IncidentID, Affected Services & Scheduling
			Update_CallerDataTable ucdt = new Update_CallerDataTable(dbs, s_dbs, CLIProvided, "", "", "", "", "",
					RequestID, systemID);
			ucdt.run();

			ponla = new ProductOfNLUActive(this.requestID, CLIProvided, "No", "none", "none", "none", "none",
					"none", "none", "none", "NULL", "NULL", "NULL");

			dbs = null;
			s_dbs = null;
			requestID = null;

			return ponla; // Spectra Negative Response
		}

		if (voiceAffected)	{
			// Update Statistics
			s_dbs.updateUsageStatisticsForMethod("NLU_Active_Pos_Voice");

			allAffectedServices.add("Voice");
		}

		if (dataAffected) {
			// Update Statistics
			s_dbs.updateUsageStatisticsForMethod("NLU_Active_Pos_Data");

			allAffectedServices.add("Data");
		}

		if (iptvAffected) {
			// Update Statistics
			s_dbs.updateUsageStatisticsForMethod("NLU_Active_Pos_IPTV");

			if (tofmTV_Result != null && tofmTV_Result.getAffected().equals("Yes")) {
				allAffectedServices.add(tofmTV.getTypeOfMassiveTVOutage()); // OTT or DTH
			} else {
				allAffectedServices.add("IPTV");
			}
		}

		// Get String representation of EndTime Date object
		// If End Time is NOT set but Duration is set then calculate the new published End Time...
		// else use the EndTime defined from the Sumbission of the ticket
		if (foundEndTime != null)
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			EndTimeString = dateFormat.format(foundEndTime);

		} else if (foundDuration != null && foundDuration != "NULL")
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Calendar cal = Calendar.getInstance(); // creates calendar
			cal.setTime(foundStartTime); // sets calendar time/date
			cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(foundDuration));
			Date myActualEndTime = cal.getTime(); // returns new date object, one hour in the future

			EndTimeString = dateFormat.format(myActualEndTime);
		}

		// Update asynchronously Stats_Pos_NLU_Requests to count number of successful NLU requests per CLI
		Update_ReallyAffectedTable uRat = new Update_ReallyAffectedTable(s_dbs, systemID, foundIncidentID,
				String.join("|", allAffectedServices), foundScheduled, CLIProvided);
		uRat.run();


		// Update asynchronously - Add Caller to Caller data table (Caller_Data) with empty values for IncidentID, Affected Services & Scheduling
		Update_CallerDataTable ucdt = new Update_CallerDataTable(dbs, s_dbs, CLIProvided, foundIncidentID,
				String.join("|", allAffectedServices), foundScheduled, foundOutageMsg, foundBackupEligible, RequestID, systemID);
		ucdt.run();

		// Replace possible OTT or DTH values with IPTV for the SOAP response
		String replacement = "IPTV";

		for (int i = 0; i < allAffectedServices.size(); i++) {
			if (allAffectedServices.get(i).equals("OTT") || allAffectedServices.get(i).equals("DTH")) {
				allAffectedServices.set(i, replacement);
			}
		}

		ponla = new ProductOfNLUActive(this.requestID, CLIProvided, "Yes", foundIncidentID, foundPriority,
				String.join("|", allAffectedServices), foundScheduled, foundDuration, EndTimeString, foundImpact, foundOutageMsg,
				foundBackupEligible, "NULL");

		dbs = null;
		s_dbs = null;
		requestID = null;

		return ponla;
	}
}