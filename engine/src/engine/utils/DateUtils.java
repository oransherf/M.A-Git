package engine.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils
{
    private static DateFormat m_Format = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");

    public static String FormatToString(Date i_Date) { return m_Format.format(i_Date); }

    public static Date FormatToDate(String i_String)
    {
        try
        {
            return m_Format.parse(i_String);
        }
        catch(ParseException e)
        {
            return null;
        }
    }
}

