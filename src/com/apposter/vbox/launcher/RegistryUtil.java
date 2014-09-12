package com.apposter.vbox.launcher;

import java.util.ArrayList;
import java.util.Collections;

public class RegistryUtil
{
	private static final String	REGQUERY_UTIL	= "reg query ";
	private static final String	REGSTR_TOKEN	= "REG_SZ";

	public static String getRegValue( String regPath, String regValue )
	{
		try
		{
			Process process = Runtime.getRuntime().exec( "reg query \"" + regPath + "\" /v " + regValue );
			StreamReader reader = new StreamReader( process.getInputStream() );

			reader.start();
			process.waitFor();
			reader.join();

			String result = reader.getResult();
			int p = result.indexOf( "REG_SZ" );
			if( p == -1 ) { return null; }
			return result.substring( p + "REG_SZ".length() ).trim();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return null;
	}

	public static String getLatestVerKey( String regPath )
	{
		try
		{
			Process process = Runtime.getRuntime().exec( "reg query \"" + regPath );
			StreamReader reader = new StreamReader( process.getInputStream() );

			reader.start();
			process.waitFor();
			reader.join();

			String result = reader.getResult();
			String keyword = "Samsung TV SDK";

			ArrayList<String> keyList = new ArrayList<String>();

			int idx = 0;
			while( idx != -1 )
			{
				idx = result.indexOf( keyword );
				if( idx == -1 )
				{
					break;
				}
				String year = result.substring( idx + keyword.length() + 1, idx + keyword.length() + 5 );
				keyList.add( year );
				result = result.replaceFirst( keyword, "" );
			}
			Collections.sort( keyList );
			Collections.reverse( keyList );
			if( keyList.size() == 0 ) { return null; }
			return (String)keyList.get( 0 );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return null;
	}

	public static String getSDKInstallDir()
	{
		String sdkInstallDir = null;
		sdkInstallDir = getRegValue( "HKLM\\Software\\Samsung\\Samsung TV SDK\\" + getLatestVerKey( "HKLM\\Software\\Samsung\\Samsung TV SDK" ), "InstallDir" );

		return sdkInstallDir;
	}
}
