/*
 * Copyright 2011 http://pvoutput.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.guarascio.evopoller.pvoutput;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.Channel;
import java.nio.channels.Selector;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipFile;

public class Util 
{
	public static final int MAX_BYTE_BUFFER = 8192; // 8kb
	public static final byte[] BYTE_ARRAY_BUFFER = new byte[MAX_BYTE_BUFFER];
	public static final char[] CHAR_ARRAY_BUFFER = new char[MAX_BYTE_BUFFER];
	
    public static Date getRoundedTime(Date time, int n)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        
        int dayFrom = c.get(Calendar.DAY_OF_YEAR);
        
        int minutes = c.get(Calendar.MINUTE);
        int rounded = n * (int)((minutes / (n+0.0)) + 0.5);

        if(minutes == 60)
        {
            c.set(Calendar.MINUTE, 0);
            c.add(Calendar.HOUR_OF_DAY, 1);
        }
        else
        {
            c.set(Calendar.MINUTE, rounded);
        }
        
        int dayTo = c.get(Calendar.DAY_OF_YEAR);

        // rounded to next day
        if(c.get(Calendar.MINUTE) == 0 && c.get(Calendar.HOUR_OF_DAY) == 0 && dayFrom != dayTo)
        {
        	c.add(Calendar.MINUTE, -1 * n);
        }
        
        return c.getTime();
    }
    
    public static boolean validTemperature(double d)
    {
    	return d >= -100 && d <= 100;
    }
    
    public static boolean validVoltage(double d)
    {
    	return d >= -1;
    }
    
	public static boolean validDateTime(Date date)
	{
		int diff = (int)Math.ceil((System.currentTimeMillis() - date.getTime())/1000/3600/24.0);
		
		if(diff > 14)
		{
			return false;
		}
		
		return true;
	}
	
	public static double getCelsius(double f)
	{
		return (f - 32) * (5.0/9.0);
	}
    
    public static long getLong(String s)
    {
    	try
		{
			return Long.parseLong(s);
		}
		catch(Exception e)
		{
			
		}
		
		return -1;
    }
    
	public static int getNumber(String s, int _default)
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch(Exception e)
		{
			
		}
		
		return _default;
	}
	
	public static double getDouble(String s, double _default)
	{
		try
		{
			return Double.parseDouble(s);
		}
		catch(Exception e)
		{
			
		}
		
		return _default;
	}
	
	public static int getNumber(String s)
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch(Exception e)
		{
			
		}
		
		return -1;
	}
	
	public static String getElement(String s, String elem)
	{
		int start = s.indexOf("<" + elem + ">");
		int end = s.indexOf("</" + elem + ">");
		
		if(start > -1 && end > -1 && end > start)
		{
			return s.substring(start+elem.length()+2, end);
		}
		
		return null;
	}
	
	public static String getAttribute(String s, String elem, String attr)
	{
		try
		{
			int start = s.indexOf("<" + elem + " ");
			
			int end = -1;
			int end1 = s.indexOf("/>", start);
			int end2 = s.indexOf("</" + elem + ">");
			
			if(end1 == -1)
			{
				end = end2;
			}
			else if(end2 == -1)
			{
				end = end1;
			}
			
			if(start > -1 && end > -1)
			{
				s = s.substring(start+elem.length()+2, end);
				
				start = s.indexOf(attr + "=\"");
				end = s.indexOf("\"", start+attr.length()+2);
				
				return s.substring(start+attr.length()+2, end);
			}
		}
		catch(Exception e)
		{
			
		}
		
		return null;
	}

	public static String getState(int pc) 
	{
		if(pc >= 3000 && pc <= 3999)
		{
			return "VIC";
		}
		else if(pc >= 4000 && pc <= 4999)
		{
			return "QLD";
		}
		else if(pc >= 5000 && pc <= 5999)
		{
			return "SA";
		}
		else if(pc >= 6000 && pc <= 6999)
		{
			return "WA";
		}
		else if(pc >= 7000 && pc <= 7999)
		{
			return "TAS";
		}
		else if(pc >= 800 && pc <= 899)
		{
			return "NT";
		}
		else if(pc == 2540 || pc == 2620 
				|| (pc >= 2600 && pc <= 2618)
				|| (pc >= 2900 && pc <= 2914))
		{
			return "ACT";
		}
		
		return "NSW";
	}
	
	public static float round(float val, int places) 
	{
		return (float)round((double)val, places);
	}

	public static double round(double val, int places) 
	{
		long factor = (long)Math.pow(10,places);

		val *= factor;

		return (double)Math.round(val) / factor;
	}

	public static boolean getBoolean(String s)
	{
		if(s == null)
		{
			return false;
		}			
		else if(s.equalsIgnoreCase("YES") || s.equalsIgnoreCase("TRUE") || s.equalsIgnoreCase("Y") || s.equals("1"))
		{
			return true;
		}

		return false;
	}

	public static boolean getBoolean(int i)
	{
		return i > 0;		
	}

	public static int getBooleanNumber(boolean b)
	{
		return b ? 1 : 0;
	}

	public static String getFilename(String file)
	{
		int index = file.lastIndexOf("/")+1;

		if(index > 0)
		{
			return file.substring(index, file.length());
		}

		return file;
	}
	
	public static String[] toStringArray(StringBuffer sb)
	{
		if(sb == null || sb.length() == 0)
			return null;

		if(sb.charAt(0) == ',')
			sb.deleteCharAt(0);		

		return sb.toString().split(",");
	}    

	public static String getString(String input, String _default)
	{
		if(input == null || input.length() == 0)
			return _default;

		return input;
	}

	public static void sleep(long l)
	{
		try
		{
			Thread.sleep(l);
		}
		catch(Exception e)
		{
		}
	}
	
	public static int getInt(String input, int _default) 
	{
		if(input == null || input.length() == 0)
			return _default;

		try
		{
			return Integer.parseInt(input);
		}
		catch(Exception e)
		{			
		}

		return _default;
	}

	/*
	public static double getDouble(String input, double _default) 
	{
		if(input == null || input.length() == 0)
			return _default;

		try
		{
			return Double.parseDouble(input);
		}
		catch(Exception e)
		{			
		}

		return _default;
	}
	*/

	public static int getInt(String input) 
	{
		return getInt(input, -1);		
	}

	public static Date getDate(String input, SimpleDateFormat df, Date _default)
	{
		try
		{
			return df.parse(input);
		}
		catch (ParseException e)
		{ 			
		}
		
		return _default;
	}
	
	public static double getDouble(String input)
	{
		return getDouble(input, -1);		
	}

	public static void copy(InputStream in, File f, long startAt) 
	throws IOException
	{
		copy(in, new FileOutputStream(f), startAt, true, true);
	}

	public static void copy(InputStream in, File f) 
	throws IOException
	{
		copy(in, f, 0);
	}

	public static boolean copy(InputStream in, OutputStream out, long startAt, boolean closeIn, boolean closeOut)
	{
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;

		boolean success = true;

		try
		{
			bos = new BufferedOutputStream(out, MAX_BYTE_BUFFER);
			bis = new BufferedInputStream(in, MAX_BYTE_BUFFER);

			if (startAt > 0)
			{
				bis.skip(startAt);
			}

			int read;

			synchronized (BYTE_ARRAY_BUFFER)
			{
				while ((read = bis.read(BYTE_ARRAY_BUFFER)) > -1)
				{
					bos.write(BYTE_ARRAY_BUFFER, 0, read);
					bos.flush();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			success = false;
		}
		finally
		{
			if (closeOut)
			{
				close(bos);
			}

			if (closeIn)
			{
				close(bis);
			}
		}

		return success;
	}

	public static boolean copy(InputStream in, Writer writer, int startAt, boolean close)
	{
		return copy(in, writer, startAt, close, close);		
	}

	public static boolean copy(InputStream in, Writer writer, int startAt, boolean closeIn, boolean closeOut)
	{
		BufferedWriter bos = null;
		BufferedReader bis = null;

		boolean success = true;

		try
		{			
			bos = new BufferedWriter(writer, MAX_BYTE_BUFFER);
			bis = new BufferedReader(new InputStreamReader(in), MAX_BYTE_BUFFER);

			if (startAt > 0)
			{
				bis.skip(startAt);
			}

			int read;

			synchronized (CHAR_ARRAY_BUFFER)
			{
				while ((read = bis.read(CHAR_ARRAY_BUFFER)) > -1)
				{										
					bos.write(CHAR_ARRAY_BUFFER, 0, read);
					bos.flush();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			success = false;
		}
		finally
		{
			if (closeOut)
			{
				close(bos);
			}

			if (closeIn)
			{
				close(bis);
			}
		}

		return success;
	}

	public static boolean copy(InputStream in, OutputStream out)
	{
		return copy(in, out, 0, true, true);
	}

	public static void ensureExists(File f)
	{
		if (f != null && !f.exists())
		{
			f.mkdirs();
		}
	}

	public static void deleteTree(File f)
	{
		if (f.exists())
		{
			// avoid stale caches, remove local document caching on startup
			File[] files = f.listFiles();

			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					deleteTree(files[i]);
				}

				files[i].delete();
			}
		}
	}

	public static void copyFile(File src, File target)
	{		
		try
		{
			copy(new FileInputStream(src), new FileOutputStream(target), 0, true, true);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public static void copyDirectory(File src, File target)
	{		
		if(src.isDirectory() && target.isDirectory())
		{
			File[] files = src.listFiles();

			for(int i = 0; i < files.length; i++)
			{
				File targetFile = new File(target, files[i].getName());

				copyFile(files[i], targetFile);
			}
		}
	}

	public static String getMimeType(String format)
	{
		if(format.equalsIgnoreCase("xml"))
		{
			return "text/xml";
		}
		else if(format.equalsIgnoreCase("html"))
		{
			return "text/html";
		}
		else if(format.equalsIgnoreCase("image_gif"))
		{
			return "image/gif";
		}
		else if(format.equalsIgnoreCase("image_jpeg"))
		{
			return "image/jpeg";
		}
		else if(format.equalsIgnoreCase("image_bmp"))
		{
			return "image/bmp";
		}
		else if(format.equalsIgnoreCase("image_png"))
		{
			return "image/png";
		}		
		else if(format.equalsIgnoreCase("image_x-png"))
		{
			return "image/x-png";
		}		
		else if(format.equalsIgnoreCase("pdf"))
		{
			return "application/pdf";
		}
		else if(format.equalsIgnoreCase("binary"))
		{
			return "application/octet-stream";
		}
		else if(format.equalsIgnoreCase("msdownload"))
		{
			return "application/x-msdownload";
		}
		else if(format.equalsIgnoreCase("video_avi"))
		{
			return "video/avi";
		}
		else if(format.equalsIgnoreCase("video_mpeg"))
		{
			return "video/mpeg";
		}
		else if(format.equalsIgnoreCase("audio_basic"))
		{
			return "audio/basic";
		}
		else if(format.equalsIgnoreCase("audio_wav"))
		{
			return "audio/wav";
		}          

		return null;
	}

	public static int inArray(String value, String[] s)
	{
		if(s != null && value != null)
		{
			for(int i = 0; i < s.length; i++)
			{
				if(value.equals(s[i]))
				{
					return i;
				}				
			}
		}

		return -1;
	}

	public static boolean equals(String a, String b)
	{
		if(a == null && b == null)
		{
			return true;
		}
		else if(a != null && b == null)
		{
			return false;
		}
		else if(a == null && b != null)
		{
			return false;
		}

		return a.equals(b);
	}
	
	public static String toString(Throwable e)
	{
		StringWriter sw = null;
		PrintWriter pw = null;

		try
		{
			sw = new StringWriter();
			pw = new PrintWriter(sw);

			e.printStackTrace(pw);			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			close(pw);			
			close(sw);
		}

		if(sw != null)
		{
			return sw.toString();
		}

		return "";
	}

	public static String toString(InputStream in)
	throws Exception
	{
		ByteArrayOutputStream out = null;

		try
		{
			out = new ByteArrayOutputStream();

			copy(in, out);

			return new String(out.toByteArray());
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			close(out);
			close(in);
		}		
	}

	public static String toString(File f)
	throws IOException
	{
		ByteArrayOutputStream out = null;
		InputStream in = null;

		try
		{
			out = new ByteArrayOutputStream();
			in = new FileInputStream(f);

			copy(in, out);
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			close(out);
			close(in);
		}

		return new String(out.toByteArray());
	}

	public static int[] toIntArray(String s)
	{
		if(s == null)
			return null;

		String[] a = s.split(",");

		int[] newArray = new int[a.length];

		for(int i = 0; i < a.length; i++)
		{
			newArray[i] = Integer.parseInt(a[i]);
		}

		return newArray;		
	}

	public static void close(InputStream o)
	{
		if (o != null)
		{
			try
			{
				o.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				o = null;
			}
		}
	}

	public static void close(Writer o)
	{
		if (o != null)
		{
			try
			{
				o.close();
			}
			catch (Exception e)
			{
				System.err.print("Could not close object");
			}
			finally
			{
				o = null;
			}
		}
	}

	public static void close(Reader o)
	{
		if (o != null)
		{
			try
			{
				o.close();
			}
			catch (Exception e)
			{
				System.err.print("Could not close object");
			}
			finally
			{
				o = null;
			}
		}
	}

	public static void close(ZipFile o)
	{
		if (o != null)
		{
			try
			{
				o.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				o = null;
			}
		}
	}

	public static void close(Selector o)
	{
		if (o != null)
		{
			try
			{
				o.close();
			}
			catch (Exception e)
			{
				System.err.print("Could not close object");
			}
			finally
			{
				o = null;
			}
		}
	}

	public static void close(Channel o)
	{
		if (o != null)
		{
			try
			{
				o.close();
			}
			catch (Exception e)
			{
				System.err.print("Could not close object");
			}
			finally
			{
				o = null;
			}
		}
	}

	public static void close(OutputStream o)
	{
		if (o != null)
		{
			try
			{
				o.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				o = null;
			}
		}
	}

	public static void flush(Writer writer)
	{
		if(writer != null)
		{
			try
			{
				writer.flush();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}		
	}

	public static void flush(OutputStream out)
	{
		if(out != null)
		{
			try
			{
				out.flush();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}		
	}

	public static boolean isSameDay(Date d1, Date d2) 
	{
		if(d1 != null && d2 != null)
		{
			Calendar c1 = Calendar.getInstance();
			c1.setTime(d1);
			int n1 = c1.get(Calendar.DAY_OF_YEAR) + c1.get(Calendar.YEAR);
			
			c1.setTime(d2);
			int n2 = c1.get(Calendar.DAY_OF_YEAR) + c1.get(Calendar.YEAR);
			
			return n1 == n2;
		}
		
		return false;
	}
		
}
