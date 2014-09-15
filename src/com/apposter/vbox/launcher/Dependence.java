package com.apposter.vbox.launcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dependence
{
	private static final String	winVBRegPath		= "HKLM\\Software\\Oracle\\VirtualBox";
	private static final String	linuxVBInstallPath	= "/usr/lib/virtualbox/";
	private static final String	macOSVBInstallPath	= "/Applications/VirtualBox.app/Contents/MacOS/";

	public static String getPathChar()
	{
		String pathChar = "/";
		String osName = System.getProperty( "os.name" );
		if( osName.matches( "(?i).*Windows.*" ) )
		{
			pathChar = "\\";
		}
		else if( osName.matches( "(?i).*Linux.*" ) )
		{
			pathChar = "/";
		}
		else if( osName.matches( "(?i).*Mac.*" ) )
		{
			pathChar = "/";
		}
		return pathChar;
	}

	public static boolean getWindowsArch()
	{
		return System.getProperty( "os.arch" ).indexOf( "64" ) != -1;
	}

	public static String getRoot()
	{
		String rootDir = "";

		String osName = System.getProperty( "os.name" );
		if( osName.matches( "(?i).*Windows.*" ) )
		{
			rootDir = "C:";
		}
		else if( osName.matches( "(?i).*Linux.*" ) )
		{
			rootDir = System.getProperty( "user.home" );
		}
		else if( osName.matches( "(?i).*Mac.*" ) )
		{
			rootDir = System.getProperty( "user.home" );
		}
		return rootDir;
	}

	public static String getEmulatorProcessName()
	{
		String emulatorProcessName = "";
		String osName = System.getProperty( "os.name" );
		if( osName.matches( "(?i).*Windows.*" ) )
		{
			emulatorProcessName = "Emulator2.exe";
		}
		else if( osName.matches( "(?i).*Linux.*" ) )
		{
			emulatorProcessName = "Emulator";
		}
		else if( osName.matches( "(?i).*Mac.*" ) )
		{
			emulatorProcessName = "Emulator";
		}
		return emulatorProcessName;
	}

	public static String getEmulatorBinaryDirName()
	{
		String emulatorBinaryDirName = "";
		String osName = System.getProperty( "os.name" );
		if( osName.matches( "(?i).*Windows.*" ) )
		{
			emulatorBinaryDirName = "bin";
		}
		else if( osName.matches( "(?i).*Linux.*" ) )
		{
			emulatorBinaryDirName = "BIN";
		}
		else if( osName.matches( "(?i).*Mac.*" ) )
		{
			emulatorBinaryDirName = "BIN";
		}
		return emulatorBinaryDirName;
	}

	public static String getAppsDirName()
	{
		String appsDirName = "Apps";
		String osName = System.getProperty( "os.name" );

		if( osName.matches( "(?i).*Windows.*" ) )
		{
			appsDirName = "apps";
		}
		else if( osName.matches( "(?i).*Linux.*" ) )
		{
			appsDirName = "Apps";
		}
		else if( osName.matches( "(?i).*Mac.*" ) )
		{
			appsDirName = "Apps";
		}

		return appsDirName;
	}

	public static String getAppsDirPath()
	{
		String appsDirPathName = getProperty( "APP_PATH" );
		// System.getProperty( "user.home" ) + getPathChar() + getAppsDirName();

		if( appsDirPathName == null || "".equals( appsDirPathName ) )
			appsDirPathName = System.getProperty( "user.dir" );

		return appsDirPathName;
	}

	public static String getVBFilePathFromOS()
	{
		String VBFileSystemPath = "";
		String osName = System.getProperty( "os.name" );
		if( osName.matches( "(?i).*Windows.*" ) )
		{
			String virtualBoxInstallPath = RegistryUtil.getRegValue( "HKLM\\Software\\Oracle\\VirtualBox", "InstallDir" );
			VBFileSystemPath = virtualBoxInstallPath + "VirtualBox.exe";
		}
		else if( osName.matches( "(?i).*Linux.*" ) )
		{
			VBFileSystemPath = "/usr/lib/virtualbox/VirtualBox";
		}
		else if( osName.matches( "(?i).*Mac.*" ) )
		{
			VBFileSystemPath = "/Applications/VirtualBox.app/Contents/MacOS/VirtualBox";
		}
		return VBFileSystemPath;
	}

	public static String getVBWebServerPathFromOS()
	{
		String VBWebServerFileSystemPath = "";
		String osName = System.getProperty( "os.name" );

		if( osName.matches( "(?i).*Windows.*" ) )
		{
			String virtualBoxInstallPath = RegistryUtil.getRegValue( "HKLM\\Software\\Oracle\\VirtualBox", "InstallDir" );
			VBWebServerFileSystemPath = virtualBoxInstallPath + "VBoxWebSrv.exe";
		}
		else if( osName.matches( "(?i).*Linux.*" ) )
		{
			VBWebServerFileSystemPath = "/usr/lib/virtualbox/vboxwebsrv";
		}
		else if( osName.matches( "(?i).*Mac.*" ) )
		{
			VBWebServerFileSystemPath = "/Applications/VirtualBox.app/Contents/MacOS/vboxwebsrv";
		}

		return VBWebServerFileSystemPath;
	}

	public static String getVBManagePathFromOS()
	{
		String VBManageFileSystemPath = "";
		String osName = System.getProperty( "os.name" );
		if( osName.matches( "(?i).*Windows.*" ) )
		{
			String virtualBoxInstallPath = RegistryUtil.getRegValue( "HKLM\\Software\\Oracle\\VirtualBox", "InstallDir" );
			VBManageFileSystemPath = virtualBoxInstallPath + "VBoxManage.exe";
		}
		else if( osName.matches( "(?i).*Linux.*" ) )
		{
			VBManageFileSystemPath = "/usr/lib/virtualbox/VBoxManage";
		}
		else if( osName.matches( "(?i).*Mac.*" ) )
		{
			VBManageFileSystemPath = "/Applications/VirtualBox.app/Contents/MacOS/VBoxManage";
		}
		return VBManageFileSystemPath;
	}

	public static String getVBWebsrvPathFromOS()
	{
		String VBWebsrvFileSystemPath = "";
		String osName = System.getProperty( "os.name" );
		if( osName.matches( "(?i).*Windows.*" ) )
		{
			String virtualBoxInstallPath = RegistryUtil.getRegValue( "HKLM\\Software\\Oracle\\VirtualBox", "InstallDir" );
			VBWebsrvFileSystemPath = virtualBoxInstallPath + "vboxwebsrv.exe";
		}
		else if( osName.matches( "(?i).*Linux.*" ) )
		{
			VBWebsrvFileSystemPath = "/usr/lib/virtualbox/vboxwebsrv";
		}
		else if( osName.matches( "(?i).*Mac.*" ) )
		{
			VBWebsrvFileSystemPath = "/Applications/VirtualBox.app/Contents/MacOS/vboxwebsrv";
		}
		return VBWebsrvFileSystemPath;
	}

	public static String getChromeBinaryPath()
	{
		String chromefilePath = getChromePath();

		if( ( chromefilePath != null ) && ( chromefilePath.replaceAll( "\\p{Space}", "" ).trim().length() != 0 ) ) { return chromefilePath; }

		String osName = System.getProperty( "os.name" );

		if( osName.matches( "(?i).*Windows.*" ) )
		{
			String chromefilePath1 = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
			String chromefilePath2 = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
			String chromefilePath3 = System.getProperty( "user.home" ) + "\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe";

			File chromefile1 = new File( chromefilePath1 );
			File chromefile2 = new File( chromefilePath2 );
			File chromefile3 = new File( chromefilePath3 );

			if( chromefile1.exists() )
			{
				chromefilePath = chromefilePath1;
			}
			else if( chromefile2.exists() )
			{
				chromefilePath = chromefilePath2;
			}
			else if( chromefile3.exists() )
			{
				chromefilePath = chromefilePath3;
			}
		}
		else if( osName.matches( "(?i).*Linux.*" ) )
		{
			String chromefilePath1 = "/opt/google/chrome/chrome";
			String chromefilePath2 = "/usr/bin/chromium-browser";

			File chromefile1 = new File( chromefilePath1 );
			File chromefile2 = new File( chromefilePath2 );

			if( chromefile1.exists() )
			{
				chromefilePath = chromefilePath1;
			}
			else if( chromefile2.exists() )
			{
				chromefilePath = chromefilePath2;
			}
		}
		else if( osName.matches( "(?i).*Mac.*" ) )
		{
			String chromefilePath1 = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
			File chromefile1 = new File( chromefilePath1 );

			if( chromefile1.exists() )
			{
				chromefilePath = chromefilePath1;
			}
		}

		return chromefilePath;
	}

	public static String getChromePath()
	{
		String chromePath = getProperty( "CHROMEPATH" );
		
		return chromePath;
	}

	public static String getProperty( String keyName )
	{
		return getPropety( "vbox_launch.properties", keyName );
	}
	
	public static String getAppName()
	{
		String appName = getProperty( "APP_NAME" );
		
		if( appName == null )
			appName = new File( System.getProperty( "user.dir" ) ).getName();
		
		return appName;
	}

	public static String getPropety( String fileName, String keyName )
	{
		Properties prop = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		File propertiesFile = null;
		String value = null;

		try
		{
			propertiesFile = new File( fileName );

			if( !propertiesFile.exists() ) { return ""; }

			prop = new Properties();
			fis = new FileInputStream( propertiesFile );
			bis = new BufferedInputStream( fis );

			prop.load( bis );

			value = prop.getProperty( keyName );
			value = value == null ? value : value.trim();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( bis != null )
				{
					bis.close();
				}
				if( fis != null )
				{
					fis.close();
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}

		return value;
	}

	public static void listVboxMachine()
	{
		try
		{
			Process vBoxMan = Runtime.getRuntime().exec( new String[] { Dependence.getVBManagePathFromOS(), "list", "vms" } );

			Scanner s = new Scanner( vBoxMan.getInputStream() );
			Pattern p = Pattern.compile( "\"([^\"]*)\"" );

			while( s.hasNext() )
			{
				String line = s.nextLine();
				Matcher m = p.matcher( line );

				if( m.find() ) System.out.println( m.group( 1 ) );

			}

			s.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	public static String[] getPropertiesDataArray( String fileName, String keyword, boolean sort )
	{
		ArrayList<String> dataArray = new ArrayList<String>();
		File propertiesFile = null;
		Properties prop = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;

		try
		{
			propertiesFile = new File( fileName );
			if( !propertiesFile.exists() ) { return null; }

			prop = new Properties();
			fis = new FileInputStream( propertiesFile );
			bis = new BufferedInputStream( fis );
			prop.load( bis );

			Enumeration<?> ee = prop.propertyNames();
			while( ee.hasMoreElements() )
			{
				String key = (String)ee.nextElement();
				String[] splitKeyName = key.split( "-" );
				for( String keyName : splitKeyName )
				{
					if( keyName.equals( keyword ) )
					{
						dataArray.add( prop.getProperty( key ) );
						break;
					}
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			try
			{
				if( bis != null )
				{
					bis.close();
				}
				if( fis != null )
				{
					fis.close();
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
		finally
		{
			try
			{
				if( bis != null )
				{
					bis.close();
				}
				if( fis != null )
				{
					fis.close();
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
		
		String[] stringArray = (String[])dataArray.toArray( new String[ dataArray.size() ] );
		
		if( sort )
		{
			String[] tempArray = new String[ stringArray.length ];
			int k = 0;
			for( int i = stringArray.length - 1; i >= 0; i-- )
			{
				tempArray[k] = stringArray[i];
				k++;
			}
			stringArray = (String[])tempArray.clone();
		}
		
		return stringArray;
	}
}