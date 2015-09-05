//MM.java
//
//Custom methods for frequently used code
//Created 10 SEPT 2009 by Matthew Miller

package configFileEditor;

import java.util.Scanner;

/**
 *
 * @author mmiller7
 */
public class MM
{
    //reads in text
    public static String readString()
    {
        return new Scanner(System.in).nextLine();
    }
    //Prompts for input and reads in text
    public static String readString(String prompt)
    {
        System.out.print(prompt);
        String input=readString();
        return input;
    }

    //Prompts for input and reads in a int, testing that it is an int
    public static int readInt(String prompt)
    {
        boolean valid=false;
        int value=-1;
        while(!valid)
        {
            try
            {
                value=Integer.parseInt(readString(prompt));
                valid=true;
            }
            catch(NumberFormatException e)
            {
                System.out.println("Invalid entry.  " +
                        "Please enter a whole number only.");
            }
        }
        return value;
    }

    //Prompts for input and reads in a int, testing that it is an int
    public static double readDouble(String prompt)
    {
        boolean valid=false;
        double value=-1;
        while(!valid)
        {
            try
            {
                value=Double.parseDouble(readString(prompt));
                valid=true;
            }
            catch(NumberFormatException e)
            {
                System.out.println("Invalid entry.  " +
                        "Please enter a number or decimal only.");
            }
        }
        return value;
    }

    /**
	 * @param str
	 * @param length
	 * @return the string either cut to length or with spaces appended
	 *
	 * If the string is longer than 3 characters it trims and puts "..."
         * indicate it was truncated.  If it's 3 or less characters it just
         * truncates.
	 */
	public static String makeLength(String str,int length)
	{
		//trim to length
		if(str.length()>length)
		{
			if(length>3)
			{
				//neatly trim
				if(str.length()>length)
					str=str.substring(0, length-3) + "...";
			}
			else//just trim
				str=str.substring(0, length);
		}

		//fill with white space
		for(int x=str.length(); x<length; x++)
			str+=" ";

		return str;
	}
}
