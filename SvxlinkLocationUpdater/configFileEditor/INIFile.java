package configFileEditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class INIFile
{
	private ArrayList<INIParam> iniFile = new ArrayList<INIParam>();
	private String path;
	private boolean ignoreCase=false;
	
	public INIFile(String path)
	{
		this.path = path;
	}
	
	public void loadFromFile()
	{
		try
		{
			File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String section=""; //start global scope
			String line;
		    while ((line = br.readLine()) != null)
		    {
		    	line=line.trim();
		    	
		    	if(!line.isEmpty() &&
		    		line.charAt(0) == '[' &&
		    		line.charAt(line.length()-1) == ']')
		    	{
		    		section=line.substring(1, line.length()-1);
		    	}
		    	else
		    	{
		    		iniFile.add(new INIParam(section, line));
		    	}
		    }
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void writeToConsole()
	{
		String section="";
		for(int x=0; x < iniFile.size(); x++)
		{
			//If it's a new section, write section heading
			if(
				//case-sensitive && it's different section
				(!ignoreCase && !section.equals(iniFile.get(x).getSection())) ||
				//ignore-case && it's different section
				( ignoreCase && !section.equalsIgnoreCase(iniFile.get(x).getSection()))
			   )
			{
				section=iniFile.get(x).getSection();
				System.out.println("["+section+"]");
			}
			
			//Write value
			System.out.println(iniFile.get(x).toString());
		}
	}
	
	public void writeToFile()
	{
		//write out file
		try {

			File file = new File(path);

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			String section="";
			for(int x=0; x < iniFile.size(); x++)
			{
				//If it's a new section, write section heading
				if(
						//case-sensitive && it's different section
						(!ignoreCase && !section.equals(iniFile.get(x).getSection())) ||
						//ignore-case && it's different section
						( ignoreCase && !section.equalsIgnoreCase(iniFile.get(x).getSection()))
					   )
				{
					section=iniFile.get(x).getSection();
					bw.write("["+section+"]");
					bw.newLine();
				}
				
				//Write field
				bw.write(iniFile.get(x).toString());
				bw.newLine();
			}
			
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the ignoreCase
	 */
	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @param ignoreCase the ignoreCase to set
	 */
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	
	public ArrayList<String> getSections()
	{
		ArrayList<String> sections = new ArrayList<String>();
		for(int x=0; x < iniFile.size(); x++)
		{
			String section = iniFile.get(x).getSection();
			if(!sections.contains(section))
			{
				sections.add(section);
			}
		}
		return sections;
	}

	public INIParam getOneParam(String section, String parameter)
	{
		ArrayList<INIParam> result = getParam(section, parameter);
		if(result != null && !result.isEmpty())
		{
			return result.get(0);
		}
		else
		{
			return null;
		}
	}
	
	public ArrayList<INIParam> getParam(String section, String parameter)
	{
		ArrayList<INIParam> theParam = new ArrayList<INIParam>();
		
		for(int x=0; x < iniFile.size(); x++)
		{
			//If we are still looking for the parameter
			if(theParam.isEmpty())
			{
				//and we find it
				if(	(	section.equals(  iniFile.get(x).getSection()  )				&&
						parameter.equals(iniFile.get(x).getParameter())				) ||
					(	ignoreCase &&
						section.equalsIgnoreCase(  iniFile.get(x).getSection()  )	&&
						parameter.equalsIgnoreCase(iniFile.get(x).getParameter())	))
				{
					//get it
					theParam.add(iniFile.get(x));
				}
			}
			//if we already found it, and there are more value lines (multi-line)
			else if((	section.equals(  iniFile.get(x).getSection()  )				&&
						iniFile.get(x).isValueOnly()								) ||
					(	ignoreCase &&
						section.equalsIgnoreCase(  iniFile.get(x).getSection()  )	&&
						iniFile.get(x).isValueOnly()								))
			{
				theParam.add(iniFile.get(x));
			}
			else
				return theParam;

		}
		return theParam;
	}

	public ArrayList<INIParam> getSection(String section)
	{
		ArrayList<INIParam> theParam = new ArrayList<INIParam>();
		
		for(int x=0; x < iniFile.size(); x++)
		{
				//and we find it
				if(	(	section.equals(  iniFile.get(x).getSection()  )				) ||
					(	ignoreCase &&
						section.equalsIgnoreCase(  iniFile.get(x).getSection()  )	))
				{
					//get it
					theParam.add(iniFile.get(x));
				}
				//if we didn't find it, and we already have stuff
				else if(!theParam.isEmpty())
				{
					//that must be it
					return theParam;
				}
		}
		return theParam;
	}

	
	public void addParam(INIParam newParam)
	{
		//try and find the end of the requested section to add item
		for(int x=iniFile.size(); x > 0; x--)
		{
			if(	(	iniFile.get(x-1).getSection().equals(newParam.getSection()) &&
					iniFile.get(x-1).getParameter().equals(newParam.getParameter())		) ||
				(	ignoreCase &&
					iniFile.get(x-1).getSection().equalsIgnoreCase(newParam.getSection()) &&
					iniFile.get(x-1).getParameter().equalsIgnoreCase(newParam.getParameter())		))
			{
				iniFile.add(x, newParam);
				return;
			}
		}
		//this means we didn't find a matching section anywhere, or it was empty
		//so we can just put it at the end
		iniFile.add(newParam);
	}
	
	public void delParam(ArrayList<INIParam> theParam)
	{
		iniFile.removeAll(theParam);
	}
	
	
	public class INIParam
	{
		private boolean comment=false;
		private String section="";
		private String parameter=null;
		private String value=null;
		
		INIParam(INIParam toCopy)
		{
			comment=toCopy.comment;
			section=toCopy.section;
			parameter=toCopy.parameter;
			value=toCopy.value;
		}
		
		INIParam(String data)
		{
			//if it's not an empty line
			if(!data.isEmpty())
			{
				//if it's a comment
				if(data.charAt(0) == '#' || data.charAt(0) == ';')
				{
					//mark comment
					comment=true;
					//store data line only
					value=data;
				}
				else //if it's not a comment
				{
					int equalsPos = data.indexOf("=");
					int quotePos = data.indexOf("\"");
					
					//if it has a quote AND
					//the quote is before the equals OR there is no equals
					//then it's just a value
					if(quotePos != -1 && (quotePos < equalsPos || equalsPos == -1))
					{
						value=data;
					}
					else //if it has no quote, or the quote was after equals
					{
						String[] keyValue=data.split("=");
						parameter=keyValue[0].trim();
						value=keyValue[1].trim();
					}
				}
			}
		}
		
		INIParam(String section,String data)
		{
			this(data);
			this.section=section;
		}
		
		boolean isComment()
		{
			return comment;
		}
		
		boolean isBlankLine()
		{
			return (!comment && parameter == null && value == null);
		}
		
		boolean isValueOnly()
		{
			return (!comment && parameter == null && value != null);
		}
		
		public String toString()
		{
			if(isBlankLine())
				return "";
			if(isComment())
				return value;
			if(isValueOnly())
				return "\t"+value;
			if(parameter != null)
				return parameter+"="+value;
			return "#svxlinkConfParse - unknown field: "+parameter+"="+value;
		}

		/**
		 * @return the section
		 */
		public String getSection() {
			return section;
		}

		/**
		 * @return the parameter
		 */
		public String getParameter() {
			return parameter;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param comment the comment to set
		 */
		public void setComment(boolean comment) {
			if(this.comment && !comment) //we are uncommenting
			{
				//Trim comment char and re-parse
				INIParam tryParse=new INIParam(value.substring(1));
				this.parameter = tryParse.parameter;
				this.value = tryParse.value;
				this.comment = tryParse.comment;
			}
			else if(!this.comment && comment) //we are commenting out
			{
				//Add comment char and reformat
				value="#"+toString();
				parameter=null;
				this.comment=comment;
			}
		}

		/**
		 * @param parameter the parameter to set
		 */
		public void setParameter(String parameter) {
			this.parameter = parameter;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}
	}
}
