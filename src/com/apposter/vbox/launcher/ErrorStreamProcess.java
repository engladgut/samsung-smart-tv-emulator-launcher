package com.apposter.vbox.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ErrorStreamProcess
{
	private InputStream		is;
	private String			type;
	private StringBuilder	content	= new StringBuilder();

	public ErrorStreamProcess( InputStream is, String type )
	{
		this.is = is;
		this.type = type;
	}

	public String getErrorConent()
	{
		return this.content.toString();
	}

	public void run()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader( this.is );
			BufferedReader br = new BufferedReader( isr );
			String line;
			while( ( line = br.readLine() ) != null )
			{
				if( this.type.equals( "Error" ) )
				{
					this.content.append( "Error   :" + line + "\n" );
				}
				else
				{
					System.out.println( "Debug:" + line );
				}
			}
		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
		}
	}
}