package gr.wind.spectra.business;

import gr.wind.spectra.web.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Outage_For_Massive_TV {
	private DB_Operations dbs;
	private s_DB_Operations s_dbs;
	private String requestID;
	private String systemID;

	private final String OTT_OUTAGE_HIERARCHY = "Massive_TV_Outage->TV_Service=ALL_EON_Boxes";
	private final String SATELLITE_OUTAGE_HIERARCHY = "Massive_TV_Outage->TV_Service=ALL_Satellite_Boxes";
	Help_Func hf = new Help_Func();

	DateFormat dateFormat = new SimpleDateFormat(hf.DATE_FORMAT);

	// Logger instance
	private static final Logger logger = LogManager.getLogger(Outage_For_Massive_TV.class.getName());

	public Outage_For_Massive_TV(DB_Operations dbs, s_DB_Operations s_dbs, String requestID, String systemID) throws Exception {
		this.dbs = dbs;
		this.s_dbs = s_dbs;
		this.requestID = requestID;
		this.systemID = systemID;

	}

	public void checkMassiveTVOutage(String TV_ID)
			throws SQLException, InvalidInputException, ParseException
	{
		// Check if TV_ID Exists in our Database

		if (!dbs.checkIfStringExistsInSpecificColumn("OTT_DTH_Data","TV_ID", TV_ID)) {
			logger.info("SysID: " + systemID + " ReqID: " + requestID + " - TV_ID: "
					+ TV_ID + " was not found in Table: OTT_DTH_Data");

			// Update asynchronously - Add Caller to Caller data table (Caller_Data) with empty values for IncidentID, Affected Services & Scheduling
			Update_CallerDataTable_ForMassiveOutage ucdt = new Update_CallerDataTable_ForMassiveOutage(dbs, s_dbs, TV_ID, "", "", "", "", "",
					requestID, systemID, "Nova");
			ucdt.run();

			// If it doesn't exist then Exit
			return;
		}

		// Get the Value of TV_Service for that TV_ID - Possible Values: OTT or DTH
		String TypeOfTV_ID = dbs.getOneValue("OTT_DTH_Data", "TV_Service", new String[]{"TV_ID"}, new String[]{TV_ID}, new String[]{"String"});
		TypeOfTV_ID = TypeOfTV_ID.trim();

		logger.info("SysID: " + systemID + " ReqID: " + requestID + " - Checking Massive TV Outage For " + TypeOfTV_ID + " TV_ID: " + TV_ID);

		// Check if it is OTT or DTH - If not then Exit
		if (!TypeOfTV_ID.equals("OTT") && !TypeOfTV_ID.equals("DTH")) {
			logger.info("SysID: " + systemID + " ReqID: " + requestID + " - TV_ID: "
					+ TV_ID + " has TV_Service: " + TypeOfTV_ID + " - Expected OTT or DTH Only - Aborting Check");

			return;
		}

		// Check if we have Open EON TV Outage Incident
		boolean weHaveMassiveEONIncident = s_dbs.checkIfCriteriaExists("SubmittedIncidents", new String[]{"IncidentStatus", "HierarchySelected"},
				new String[]{"OPEN", OTT_OUTAGE_HIERARCHY}, new String[]{"String", "String"});

		// Check if we have Open Satellite TV Outage Incident
		boolean weHaveMassiveSatelliteIncident = s_dbs.checkIfCriteriaExists("SubmittedIncidents", new String[]{"IncidentStatus", "HierarchySelected"},
				new String[]{"OPEN", SATELLITE_OUTAGE_HIERARCHY}, new String[]{"String", "String"});

		if (TypeOfTV_ID.equals("OTT") && weHaveMassiveEONIncident) {

			try {
				ResultSet rs = null;
				// Get Lines with IncidentStatus = "OPEN"
				rs = s_dbs.getRows("SubmittedIncidents",
						new String[]{"WillBePublished", "IncidentID", "OutageID", "BackupEligible",
								"HierarchySelected", "Priority", "AffectedServices", "Scheduled", "Duration",
								"StartTime", "EndTime", "Impact", "OutageMsg"},
						new String[]{"HierarchySelected"}, new String[]{OTT_OUTAGE_HIERARCHY}, new String[]{"String"});

				String IncidentID = null;
				int OutageID = 0;
				String Scheduled = null;
				String OutageMsg = null;
				String BackupEligible = null;

				while (rs.next()) {
					IncidentID = rs.getString("IncidentID");
					OutageID = rs.getInt("OutageID");
					Scheduled = rs.getString("Scheduled");
					OutageMsg = rs.getString("OutageMsg");
					BackupEligible = rs.getString("BackupEligible");
				}

				logger.info("SysID: " + systemID + " ReqID: " + requestID + " - Found Affected OTT TV_ID: "
						+ TV_ID + " | " + IncidentID
						+ " | OutageID: " + OutageID + " | " + "IPTV" + " | "
						+ OutageMsg + " | " + BackupEligible);

				// Update asynchronously - Add Caller to Caller data table (Caller_Data) with empty values for IncidentID, Affected Services & Scheduling
				Update_CallerDataTable_ForMassiveOutage ucdt = new Update_CallerDataTable_ForMassiveOutage(dbs, s_dbs, TV_ID, IncidentID, "OTT", Scheduled, OutageMsg, BackupEligible,
						requestID, systemID, "Nova");

				if (dbs == null) {
					System.out.println("dbs is null");
				}
				if (s_dbs == null) {
					System.out.println("s_dbs is null");
				}
				System.out.println("HELLO");
				ucdt.run();

				// Update asynchronously Stats_Pos_NLU_Requests to count number of successful NLU requests per CLI
				Update_ReallyAffectedTable uRat = new Update_ReallyAffectedTable(s_dbs, systemID, IncidentID,
						"OTT", Scheduled, TV_ID);
				uRat.run();

				// Update Statistics
				s_dbs.updateUsageStatisticsForMethod("NLU_Active_Pos_IPTV");

				return;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		if (TypeOfTV_ID.equals("DTH") && weHaveMassiveSatelliteIncident) {

			try {
			ResultSet rs = null;
			// Get Lines with IncidentStatus = "OPEN"
			rs = s_dbs.getRows("SubmittedIncidents",
					new String[]{"WillBePublished", "IncidentID", "OutageID", "BackupEligible",
							"HierarchySelected", "Priority", "AffectedServices", "Scheduled", "Duration",
							"StartTime", "EndTime", "Impact", "OutageMsg"},
					new String[]{"HierarchySelected"}, new String[]{SATELLITE_OUTAGE_HIERARCHY}, new String[]{"String"});

			String IncidentID = null;
			int OutageID = 0;
			String outageAffectedService = null;
			String Scheduled = null;
			String Duration = null;
			Date StartTime = null;
			Date EndTime = null;
			String OutageMsg = null;
			String BackupEligible = null;

			while (rs.next()) {
				IncidentID = rs.getString("IncidentID");
				OutageID = rs.getInt("OutageID");
				outageAffectedService = rs.getString("AffectedServices");
				Scheduled = rs.getString("Scheduled");
				Duration = rs.getString("Duration");
				StartTime = rs.getTimestamp("StartTime");
				EndTime = rs.getTimestamp("EndTime");
				OutageMsg = rs.getString("OutageMsg");
				BackupEligible = rs.getString("BackupEligible");

			}

			logger.info("SysID: " + systemID + " ReqID: " + requestID + " - Found Affected DTH TV_ID: "
					+ TV_ID + " from Massive INC: " + IncidentID
					+ " | OutageID: " + OutageID + " | " + outageAffectedService + " | "
					+ OutageMsg + " | " + BackupEligible);

			// Update asynchronously - Add Caller to Caller data table (Caller_Data) with empty values for IncidentID, Affected Services & Scheduling
			Update_CallerDataTable_ForMassiveOutage ucdt = new Update_CallerDataTable_ForMassiveOutage(dbs, s_dbs, TV_ID, IncidentID, "DTH", Scheduled, OutageMsg, BackupEligible,
					requestID, systemID, "Nova");
			ucdt.run();

			// Update asynchronously Stats_Pos_NLU_Requests to count number of successful NLU requests per CLI
			Update_ReallyAffectedTable uRat = new Update_ReallyAffectedTable(s_dbs, systemID, IncidentID,
					"DTH", Scheduled, TV_ID);
			uRat.run();

			// Update Statistics
			s_dbs.updateUsageStatisticsForMethod("NLU_Active_Pos_IPTV");

			return;

		} catch (Exception e) {
			e.printStackTrace();
		}
		}

		// Default No Outage for TV_ID
		logger.info("SysID: " + systemID + " ReqID: " + requestID + " - No Service affection for " + TypeOfTV_ID + " TV_ID: "
				+ TV_ID);

		// Update asynchronously - Add Caller to Caller data table (Caller_Data) with empty values for IncidentID, Affected Services & Scheduling
		Update_CallerDataTable_ForMassiveOutage ucdt = new Update_CallerDataTable_ForMassiveOutage(dbs, s_dbs, TV_ID, "", "", "", "", "",
				requestID, systemID, "Nova");
		ucdt.run();

		// Update Statistics
		s_dbs.updateUsageStatisticsForMethod("NLU_Active_Neg");
	}
}
