package com.apposter.vbox.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class StreamReader extends Thread
{
	private InputStream		is;
	private StringWriter	sw;

	public StreamReader( InputStream is )
	{
		this.is = is;
		this.sw = new StringWriter();
	}

	public void run()
	{
		try
		{
			int c;
			while( ( c = this.is.read() ) != -1 )
			{
				this.sw.write( c );
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	public String getResult()
	{
		return this.sw.toString();
	}
}
