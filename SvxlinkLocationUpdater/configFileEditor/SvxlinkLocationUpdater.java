package configFileEditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import configFileEditor.INIFile.INIParam;

public class SvxlinkLocationUpdater {

	/*
	final static String PYMULTIMONAPRS_PATH = "X:\\etc\\pymultimonaprs.json";
	final static String SVXLINK_CONFIG_DIR = "X:\\etc\\svxlink";
	final static String _SLASH_ = "\\";
	*/
	
	final static String PYMULTIMONAPRS_PATH = "/etc/pymultimonaprs.json";
	final static String SVXLINK_CONFIG_DIR = "/etc/svxlink";
	final static String _SLASH_ = "/";
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		
		String svxlinkConfigPath=SVXLINK_CONFIG_DIR+_SLASH_+"svxlink.conf";
		System.out.println("Attempting to load file: "+svxlinkConfigPath);
		INIFile svxlinkConfig = new INIFile(svxlinkConfigPath);
		svxlinkConfig.loadFromFile();
		System.out.println("Read in main-config OK.");

		//Parse out echolink config
		INIParam Global_CfgDir       = svxlinkConfig.getOneParam("GLOBAL", "CFG_DIR");
		String moduleEcholinkConfigPath=SVXLINK_CONFIG_DIR+_SLASH_+Global_CfgDir.getValue()+_SLASH_+"ModuleEchoLink.conf";
		System.out.println("Attempting to load file: "+moduleEcholinkConfigPath);
		INIFile moduleEcholinkConfig = new INIFile(moduleEcholinkConfigPath);
		moduleEcholinkConfig.loadFromFile();
		System.out.println("Read in module-echolink-config OK.");
		
		
		/*
		 * Important fields:
		 * svxlink.conf
		 * GLOBAL -> LOCATION_INFO
		 * <LOCATION_INFO value> ->
		 * 							 LON_POSITION
		 * 							 LAT_POSITION
		 * 							 FREQUENCY
		 * 							 TONE
		 * 							 TX_POWER
		 * 							 ANTENNA_HEIGHT
		 * 							 COMMENT
		 * GLOBAL -> LOGICS  (for each)
		 * <LOGICS value, split comma> -> REPORT_CTCSS
		 * GLOBAL -> CFG_DIR (parse ModuleEcholink.conf)
		 * 
		 * <CFG_DIR value>/ModuleEcholink
		 * ModuleEchoLink -> 
		 * 					 SYSOPNAME
		 * 					 LOCATION (use [svx] City, ST)
		 * 					 DESCRIPTION (multi-line)
		 * 								Describe location\n
		 *                              QTH:     City, State\n
		 * 								QRG:     Simplex <FREQ> MHz\n
		 * 								CTCSS:   100.0 Hz
		 * 								Trx:     Yaesu FT-7900\n
		 * 								Antenna: Indoor 1/4 wave ground-plane\n
		 * 								Output:  10 watts
		 * 								Pwr Src: AC Mains (no backup)\n
		 */
		
		@SuppressWarnings("unused")
		SvxlinkLocationConfig locationConfig = new SvxlinkLocationConfig(svxlinkConfig, moduleEcholinkConfig);
		
		
		
		//Menu
		boolean keepGoing = true;
		while(keepGoing)
		{
			System.out.println();
			System.out.println("+------------------------------+");
			System.out.println("|    Svxlink Location Updater  |");
			System.out.println("|           Main Menu          |");
			System.out.println("+------------------------------+");
			System.out.println("| 1) Load custom config file   |");
			System.out.println("| 2) Save custom config file   |");
			System.out.println("| 3) Show settings             |");
			System.out.println("|                              |");
			System.out.println("|   Specify custom values:     |");
			System.out.println("| 4)    Frequency/Tone         |");
			System.out.println("| 5)    Transmitter Type/Power |");
			System.out.println("| 6)    Geographic Location    |");
			System.out.println("|                              |");
			System.out.println("| 7) Save to system config     |");
			System.out.println("| 8) Exit (save first!)        |");
			System.out.println("+------------------------------+");
			int choice = MM.readInt("Choice? ");
			System.out.println();
			switch(choice)
			{
				case 1:
					locationConfig.readFile(MM.readString("Filename? "));
					break;
				case 2:
					locationConfig.writeFile(MM.readString("Filename? "));
					break;
				case 3:
					locationConfig.printToConsole();
					break;
				case 4:
					locationConfig.promptForFrequencyValues();
					break;
				case 5:
					locationConfig.promptForTransmitterValues();
					break;
				case 6:
					locationConfig.promptForLocationValues();
					break;
				case 7:
					File f = new File(svxlinkConfig.getPath());
					boolean canWrite = f.canWrite();
					f = new File(moduleEcholinkConfig.getPath());
					canWrite = canWrite && f.canWrite();
					if(canWrite)
					{
						System.out.println("Saving svxlink-config");
						//svxlinkConfig.writeToConsole();
						svxlinkConfig.writeToFile();
						System.out.println("Saving module-echolink-config");
						//moduleEcholinkConfig.writeToConsole();
						moduleEcholinkConfig.writeToFile();
						tryUpdatePymultimonaprsLatLon(locationConfig.getLatitude(), locationConfig.getLongitude());
					}
					else
					{
						System.out.println("ERROR: Can't write to file.  Check permissions!");
					}
					break;
				case 8:
					keepGoing=false;
					break;
				default:
					System.out.println("Invalid choice.");
			}
		}
		
		System.out.println("Done.");
	}
	
	private static void tryUpdatePymultimonaprsLatLon(double lat, double lon)
	{
		File file = new File(PYMULTIMONAPRS_PATH);
		if(file.exists() &&
		   MM.readString("Update pymultimonaprs lat/lon?  [Y/n]").equalsIgnoreCase("Y"))
		{
			//TODO
		}
	}
	
	static class SvxlinkLocationConfig
	{
		//Global Parameters
		INIParam Global_LocationInfo = null;		//used to look up other parts of config, pull from ini
		INIParam Global_Logics       = null;		//used to look up other parts of config, pull from ini
		
		//Location Parameters
		INIParam LocationInfo_LonPosition   = null;	//format DD.MM.ssX where X=E(East)/W(West)
		double longitude = 0;						//decimal degrees for whatever
		INIParam LocationInfo_LatPosition   = null;	//format DD.MM.ssX where X=N(North)/S(South)
		double latitude = 0;						//decimal degrees for whatever
		INIParam LocationInfo_Frequency     = null;	//frequency (decimal number)
		double frequency = 0;						//decimal frequency
		INIParam LocationInfo_Tone          = null;	//integer, represents CTCSS tone (drop decimal part)
		double ctcssTone = 0;						//decimal tone for whatever
		INIParam LocationInfo_TxPower       = null; //integer, unit of watts
		int txPower = 0;							//integer watts
		INIParam LocationInfo_AntennaHeight = null;	//integer with suffix, unit of meters (e.g. "10m")
		int antennaHeight = 0;						//antenna height in meters
		INIParam LocationInfo_Comment       = null;	//string, one-line comment about this node
		
		//Logics CTCSS Parameters
		ArrayList<INIParam> LogicsEnabledReportCtcss = new ArrayList<INIParam>(); //decimal, in case we want to update what it speaks
		
		//Echolink Parameters
		INIParam ModuleEcholink_Location    = null;				//string, "[svx] City, State"
		ArrayList<INIParam> ModuleEcholink_Description = null;	//description multi line string
		
		/*
		 * Fields we need to write out for re-loading (minimum)
		 * longitude
		 * latitude
		 * frequency
		 * ctcssTone
		 * txPower
		 * antennaHeight
		 * LocationInfo_Comment.getValue()
		 * ModuleEcholink_Location.getValue()
		 * ModuleEcholink_Description - value-array 
		 */
		
		SvxlinkLocationConfig(INIFile svxlinkConfig, INIFile moduleEcholinkConfig)
		{
			final boolean DEBUG=false;
			
			//Global Parameters
			Global_LocationInfo = svxlinkConfig.getOneParam("GLOBAL", "LOCATION_INFO");
			Global_Logics       = svxlinkConfig.getOneParam("GLOBAL", "LOGICS");
			
			//Location Parameters
			LocationInfo_LonPosition   = svxlinkConfig.getOneParam(Global_LocationInfo.getValue(), "LON_POSITION");
			if(DEBUG) System.out.print(LocationInfo_LonPosition.getValue() + " / ");
			String lonString = LocationInfo_LonPosition.getValue();
			String lonDirection = lonString.substring(lonString.length()-1);
			lonString = lonString.substring(0, lonString.length()-1);
			String[] lonArray = lonString.split("\\.",3);
			int lonDeg=Integer.parseInt(lonArray[0]);
			int lonMin=Integer.parseInt(lonArray[1]);
			int lonSec=Integer.parseInt(lonArray[2]);
			double lonDec=lonDeg+(lonMin/60.0)+(lonSec/60.0/60.0);
			longitude = (lonDirection.equalsIgnoreCase("W")?-1:1) * lonDec;
			if(DEBUG) System.out.println(longitude);
			
			LocationInfo_LatPosition   = svxlinkConfig.getOneParam(Global_LocationInfo.getValue(), "LAT_POSITION");
			if(DEBUG) System.out.print(LocationInfo_LatPosition.getValue() + " / ");
			String latString = LocationInfo_LatPosition.getValue();
			String latDirection = latString.substring(latString.length()-1);
			latString = latString.substring(0, latString.length()-1);
			String[] latArray = latString.split("\\.",3);
			int latDeg=Integer.parseInt(latArray[0]);
			int latMin=Integer.parseInt(latArray[1]);
			int latSec=Integer.parseInt(latArray[2]);
			double latDec=lonDeg+(latMin/60.0)+(latSec/60.0/60.0);
			latitude = (latDirection.equalsIgnoreCase("S")?-1:1) * latDec;
			if(DEBUG) System.out.println(latitude);
			
			LocationInfo_Frequency     = svxlinkConfig.getOneParam(Global_LocationInfo.getValue(), "FREQUENCY");
			LocationInfo_Tone          = svxlinkConfig.getOneParam(Global_LocationInfo.getValue(), "TONE");
			LocationInfo_TxPower       = svxlinkConfig.getOneParam(Global_LocationInfo.getValue(), "TX_POWER");
			LocationInfo_AntennaHeight = svxlinkConfig.getOneParam(Global_LocationInfo.getValue(), "ANTENNA_HEIGHT");
			LocationInfo_Comment       = svxlinkConfig.getOneParam(Global_LocationInfo.getValue(), "COMMENT");
			
			//try to parse out some stuff
			try {
				frequency                  = Double.parseDouble(LocationInfo_Frequency.getValue());
			} catch(NumberFormatException e) {}
			try {
				ctcssTone                  = Double.parseDouble(LocationInfo_Tone.getValue());
			} catch(NumberFormatException e) {}
			try {
				txPower                    = Integer.parseInt(LocationInfo_TxPower.getValue());
			} catch(NumberFormatException e) {}
			try {
				String antHeight           = LocationInfo_AntennaHeight.getValue();
				antennaHeight              = Integer.parseInt(antHeight.substring(0,antHeight.length()-1));
			} catch(NumberFormatException e) {}
			
			//Logics CTCSS Parameters
			String[] LogicsEnabled = Global_Logics.getValue().split(",");
			LogicsEnabledReportCtcss.clear();
			for(int x=0; x < LogicsEnabled.length; x++)
			{
				//get REPORT_CTCSS for each
				LogicsEnabled[x]=LogicsEnabled[x].trim();
				INIParam reportCtcss = svxlinkConfig.getOneParam(LogicsEnabled[x], "REPORT_CTCSS");
				if(reportCtcss != null)
					LogicsEnabledReportCtcss.add(reportCtcss);
			}
			//if this exists, it will be a better format (with decimal).
			if(!LogicsEnabledReportCtcss.isEmpty())
			try {
				ctcssTone                  = Double.parseDouble(LogicsEnabledReportCtcss.get(0).getValue());
			} catch(NumberFormatException e) {}
			
			
			//Echolink Parameters
			ModuleEcholink_Location    = moduleEcholinkConfig.getOneParam("ModuleEchoLink", "LOCATION");
			ModuleEcholink_Description = moduleEcholinkConfig.getParam("ModuleEchoLink", "DESCRIPTION");
		}		
		
		public void writeFile(String path) {
			//write out file
			try {

				File file = new File(path);
				System.out.println("File: "+file.getAbsolutePath());

				// if file doesn't exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);

				/*
				 * Fields we need to write out for re-loading (minimum)
				 * longitude
				 * latitude
				 * frequency
				 * ctcssTone
				 * txPower
				 * antennaHeight
				 * LocationInfo_Comment.getValue()
				 * ModuleEcholink_Location.getValue()
				 * ModuleEcholink_Description - value-array 
				 */
				
				bw.write(String.valueOf(getLongitude()));
				bw.newLine();
				bw.write(String.valueOf(getLatitude()));
				bw.newLine();
				bw.write(String.valueOf(getFrequency()));
				bw.newLine();
				bw.write(String.valueOf(getCtcssTone()));
				bw.newLine();
				bw.write(String.valueOf(getTxPower()));
				bw.newLine();
				bw.write(String.valueOf(getAntennaHeight()));
				bw.newLine();
				bw.write(getAprsComment());
				bw.newLine();
				bw.write(getEcholinkLocation());
				bw.newLine();
				
				String str = getEcholinkDescription("QTH:");
				if(str != null)
				{
					bw.write(str);
				}
				bw.newLine();
				str = getEcholinkDescription("Trx:");
				if(str != null)
				{
					bw.write(str);
				}
				bw.newLine();
				str = getEcholinkDescription("Antenna:");
				if(str != null)
				{
					bw.write(str);
				}
				bw.newLine();
				str = getEcholinkDescription("Pwr Src:");
				if(str != null)
				{
					bw.write(str);
				}
				bw.newLine();
				
				bw.close();

				System.out.println("Config saved.");

			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		public void readFile(String path) {
			try
			{
				File file = new File(path);
				BufferedReader br = new BufferedReader(new FileReader(file));
				
				setLongitude(Double.parseDouble(br.readLine()));
				setLatitude(Double.parseDouble(br.readLine()));
				setFrequency(Double.parseDouble(br.readLine()));
				setCtcssTone(Double.parseDouble(br.readLine()));
				setTxPower(Integer.parseInt(br.readLine()));
				setAntennaHeight(Integer.parseInt(br.readLine()));
				setAprsComment(br.readLine());
				setEcholinkLocation(br.readLine());
				
				setEcholinkDescription(br.readLine()); //QTH
				setEcholinkDescription(br.readLine()); //Trx
				setEcholinkDescription(br.readLine()); //Antenna
				setEcholinkDescription(br.readLine()); //Pwr Src
				
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
		}

		public void promptForLocationValues() {
			setLongitude( MM.readDouble("Longitude (decimal)? "));
			setLatitude(  MM.readDouble("Latitude  (decimal)? "));
			setTxPower(      MM.readInt("TX Power (watts)?    "));
			setAntennaHeight(MM.readInt("Ant Height (meters)? "));
			setAprsComment("svxlink on Linux built with http://git.io/vGxwW configuration");
			setEcholinkLocation(MM.readString("Enter location tag ([Svx] City, ST): "));
			
			setEcholinkDescription("QTH:     ", MM.readString("Location Description?     "), "\\n"); //QTH
		}
		
		public void promptForFrequencyValues() {
			setFrequency( MM.readDouble("Frequency?           "));
			setCtcssTone( MM.readDouble("CTCSS Tone (Hz)?     "));
		}
		
		public void promptForTransmitterValues() {
			setEcholinkDescription("Trx:     ", MM.readString("Transmitter make/model?   "), "\\n"); //Trx
			setEcholinkDescription("Antenna: ", MM.readString("Antenna type/description? "), "\\n"); //Antenna
			setEcholinkDescription("Pwr Src: ", MM.readString("Power source/description? "), "\\n"); //Pwr Src
		}

		void printToConsole()
		{
			System.out.println("**** Current Location & Frequency Data ****");
			System.out.println(Global_LocationInfo == null ? "GLOBAL -> LOCATION_INFO is not set." : Global_LocationInfo);
			
			//Location Parameters
			System.out.println(LocationInfo_LonPosition + " (or " + longitude + " deg.)");
			System.out.println(LocationInfo_LatPosition + " (or " + latitude + " deg.)");
			System.out.println(LocationInfo_Frequency == null ? "LOCATION_INFO -> FREQUENCY is not set" : LocationInfo_Frequency);
			System.out.println(LocationInfo_Tone == null ? "LOCATION_INFO -> TONE is not set" : LocationInfo_Tone);
			System.out.println(LocationInfo_TxPower == null ? "LOCATION_INFO -> TONE is not set" : LocationInfo_TxPower);
			System.out.println(LocationInfo_AntennaHeight == null ? "LOCATION_INFO -> ANTENNA_HEIGHT is not set" : LocationInfo_AntennaHeight);
			System.out.println(LocationInfo_Comment == null ? "LOCATION_INFO -> COMMENT is not set" : LocationInfo_Comment);
			
			//Echolink Parameters
			System.out.println(ModuleEcholink_Location == null ? "ModuleEchoLink -> LOCATION is not set" : ModuleEcholink_Location);
			if(ModuleEcholink_Description == null)
			{
				System.out.println("ModuleEchoLink -> DESCRIPTION is not set");
			}
			else
			{
				for(int x=0; x < ModuleEcholink_Description.size(); x++)
					System.out.println(ModuleEcholink_Description.get(x));
			}
			System.out.println("*******************************************");
		}


		double getLongitude()
		{
			return longitude;
		}
		
		double getLatitude()
		{
			return latitude;
		}
		
		double getFrequency()
		{
			return frequency;
		}
		
		double getCtcssTone()
		{
			return ctcssTone;
		}
		
		int getTxPower()
		{
			return txPower;
		}
		
		int getAntennaHeight()
		{
			return antennaHeight;
		}
		
		String getAprsComment()
		{
			if(LocationInfo_Comment != null)
				return LocationInfo_Comment.getValue();
			else
				return "";
		}
		
		String getEcholinkLocation()
		{
			return ModuleEcholink_Location.getValue();
		}
		
		void setLongitude(double longitude)
		{
			this.longitude = longitude;
			int deg = Math.abs((int) longitude);
			double minDec = (Math.abs(longitude)-deg)*60.0;
			int min = (int) minDec;
			double secDec = (minDec-(min))*60.0;
			//round to 2 decimals
			int sec=(int) (Math.round(secDec));
			//find direction
			String dir = "E";
			if(longitude < 0)
			{
				dir="W";
			}
			String degStr=String.valueOf(deg);
			if(degStr.length() < 2)
				degStr="0"+degStr;
			String minStr=String.valueOf(min);
			if(minStr.length() < 2)
				minStr="0"+minStr;
			String secStr=String.valueOf(sec);
			if(secStr.length() < 2)
				secStr="0"+secStr;
			LocationInfo_LonPosition.setValue(degStr+"."+minStr+"."+secStr+dir);
		}
		
		void setLatitude(double latitude)
		{
			this.latitude = latitude;
			int deg = Math.abs((int) latitude);
			double minDec = (Math.abs(latitude)-deg)*60.0;
			int min = (int) minDec;
			double secDec = (minDec-(min))*60.0;
			//round to 2 decimals
			int sec=(int) (Math.round(secDec));
			//find direction
			String dir = "N";
			if(latitude < 0)
			{
				dir="S";
			}
			String degStr=String.valueOf(deg);
			if(degStr.length() < 2)
				degStr="0"+degStr;
			String minStr=String.valueOf(min);
			if(minStr.length() < 2)
				minStr="0"+minStr;
			String secStr=String.valueOf(sec);
			if(secStr.length() < 2)
				secStr="0"+secStr;
			LocationInfo_LatPosition.setValue(degStr+"."+minStr+"."+secStr+dir);
		}

		void setFrequency(double frequency)
		{
			this.frequency = frequency;
			LocationInfo_Frequency.setValue(String.valueOf(frequency));
			setEcholinkDescription("QRG:     ", String.valueOf(frequency), " MHz\\n");
		}
		
		void setCtcssTone(double ctcssTone)
		{
			this.ctcssTone = ctcssTone;
			LocationInfo_Tone.setValue(String.valueOf((int)ctcssTone));
			for(int x=0; x < LogicsEnabledReportCtcss.size(); x++)
			{
				LogicsEnabledReportCtcss.get(x).setValue(String.valueOf(ctcssTone));
			}
			setEcholinkDescription("CTCSS:   ", (ctcssTone == 0 ? "None" : String.valueOf(ctcssTone)+" Hz"), "\\n");
		}
		
		void setLocationDescription(String locationDescription)
		{
			setEcholinkDescription("QTH:     ", locationDescription, "\\n");
		}
		
		void setRadioModel(String model)
		{
			setEcholinkDescription("Trx:     ", model, "\\n");
		}
		
		void setAntennaType(String type)
		{
			setEcholinkDescription("Antenna: ", type, "\\n");
		}
		
		void setPowerSource(String descrip)
		{
			setEcholinkDescription("Pwr Src: ", descrip, "\\n");
		}
		
		void setTxPower(int txPower)
		{
			this.txPower = txPower;
			LocationInfo_TxPower.setValue(String.valueOf(txPower));
			setEcholinkDescription("Output:  ", String.valueOf(txPower), " watts\\n");
		}
		
		void setAntennaHeight(int antennaHeight)
		{
			this.antennaHeight = antennaHeight;
			LocationInfo_AntennaHeight.setValue(antennaHeight+"m");
		}
		
		void setAprsComment(String comment)
		{
			if(LocationInfo_Comment != null)
				LocationInfo_Comment.setValue(comment);
		}

		void setEcholinkLocation(String svxCityState)
		{
			ModuleEcholink_Location.setValue(svxCityState);
			setEcholinkDescription("QTH:     ", String.valueOf(svxCityState), "\\n");
		}
		
		private String getEcholinkDescription(String prefix)
		{
			for(int x=0; x < ModuleEcholink_Description.size(); x++)
			{
				if(ModuleEcholink_Description.get(x).getValue().contains(prefix.trim()))
				{
					return ModuleEcholink_Description.get(x).getValue();
				}
			}
			return null;
		}
		
		private void setEcholinkDescription(String toMatch)
		{
			//ignore blank line
			if(!toMatch.trim().isEmpty())
			{
				String[] parsedMatch = toMatch.split(":");
				setEcholinkDescription(parsedMatch[0], ":"+parsedMatch[1], "");
			}
		}
		
		private void setEcholinkDescription(String prefix, String value, String suffix)
		{
			//ignore blank prefix
			if(!prefix.trim().isEmpty())
			{
				for(int x=0; x < ModuleEcholink_Description.size(); x++)
				{
					if(ModuleEcholink_Description.get(x).getValue().contains(prefix.trim()))
					{
						String str=prefix+value+suffix;
						if(str.charAt(0) != '"')
							str="\""+str;
						if(str.charAt(str.length()-1) != '"')
							str=str+"\"";
						ModuleEcholink_Description.get(x).setValue(str);
					}
				}
			}
		}
	}
}
