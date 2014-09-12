package com.apposter.vbox.launcher;

import java.io.File;

class SDKPreferences
{
	public static final String	SAMSUNG_LICENSE_KEY		= "SamsungLicense";
	public static final String	SAMSUNG_LICENSE_VALUE	= "NO";
	public static final String	SAMSUNG_CHROME_KEY		= "SamsungPNaClChrome";
	public static final String	SAMSUNG_CHROME_VALUE	= "Use Browse to Choose Chrome Path";
}

public class SDKInfo
{
	public static final String	FONT_NAME								= "Verdana";
	private static String		SDKInstallPath							= " ";
	private static String		currentPprojectName						= null;
	private static String		APP_RESOLUTION_DEFAULT_VALUE			= "960x540";
	private static final String	ALLOWED_PROJECT_NAME_CHAR				= "[0-9|a-z|A-Z|_|-|.]*";
	private static final String	ALLOWED_ID_CHAR							= "[0-9|a-z|A-Z|_]*";
	private static boolean		showLog									= false;
	public static final boolean	WIN32_EMUL								= false;

	public static boolean getShowLogValue()
	{
		return showLog;
	}

	public static void setShowLogValue( boolean onOff )
	{
		showLog = onOff;
	}

	private static final String[]	APP_RESOLUTIONS	= { "960x540", "1280x720" };

	public static String[] getAppResolutions()
	{
		return APP_RESOLUTIONS;
	}

	public static String getAppDefaultResolution()
	{
		return APP_RESOLUTION_DEFAULT_VALUE;
	}

	public static String getSDKInstllPath()
	{
		if( SDKInstallPath.replaceAll( "\\p{Space}", "" ).trim().length() != 0 ) { return SDKInstallPath; }

		File sdkInstPathPropFile = new File( Dependence.getRoot() + Dependence.getPathChar() + "sec_sdk.properties" );

		if( sdkInstPathPropFile.exists() )
		{
			String path = Dependence.getPropety( Dependence.getRoot() + Dependence.getPathChar() + "sec_sdk.properties", "PATH" );

			if( path == null || "".equals( path ) )
			{
				if( sdkInstPathPropFile.exists() ) sdkInstPathPropFile.delete();
			}

			setSDKInstallPath( path );
		}
		
		if( ( SDKInstallPath != null ) && ( SDKInstallPath.replaceAll( "\\p{Space}", "" ).trim().length() == 0 ) )
		{
			if( sdkInstPathPropFile.exists() )
			{
				sdkInstPathPropFile.delete();
			}
			String osName = System.getProperty( "os.name" );
			if( osName.matches( "(?i).*Windows.*" ) )
			{
				String path = RegistryUtil.getSDKInstallDir();
				setSDKInstallPath( path );
			}
			else if( ( osName.matches( "(?i).*Linux.*" ) ) || ( osName.matches( "(?i).*Mac.*" ) ) )
			{
				setSDKInstallPath( "" );
			}
		}
		
		return SDKInstallPath;
	}

	public static String getAllowedProjectNameChar()
	{
		return "[0-9|a-z|A-Z|_|-|.]*";
	}

	public static String getAllowedIDChar()
	{
		return "[0-9|a-z|A-Z|_]*";
	}

	public static String getMapleFontPropFileName()
	{
		return "fontlist.info";
	}

	public static String getWebkitFontPropFileName()
	{
		return "fontlist.ini";
	}
	
	public static String getPropertiesDirPath()
	{
		// StringBuffer buf = new StringBuffer();
		// buf.append( PluginUtil.getBundleOSPath( Platform.getBundle(
		// "tv.samsung.sdk.product" ) ) );
		// buf.append( "plugin_data" );
		// buf.append( StringStatics.FILE_SEPARATOR );
		// buf.append( "properties" );
		// buf.append( StringStatics.FILE_SEPARATOR );
		//
		// String propertiesFolderPath = buf.toString();
		//
		// return propertiesFolderPath;

		return null;
	}

	public static String getPNaclInfoFilePath()
	{
		String PNaclInfoFilePath = null;
		try
		{
			String PNaclPropertiesFullPath = getPropertiesDirPath() + "pnacl.properties";
			File PNaclInfoFile = new File( PNaclPropertiesFullPath );
			if( PNaclInfoFile.exists() )
			{
				PNaclInfoFilePath = PNaclPropertiesFullPath;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return PNaclInfoFilePath;
	}

	public static String getCurrentProjectName()
	{
		return currentPprojectName;
	}

	public static String getEPLTextFileName()
	{
		return "epl.txt";
	}

	public static String getSECTVLTextFileName()
	{
		return "samsung_license.txt";
	}

	public static String getASLTextFileName()
	{
		return "asl.txt";
	}

	public static String getCDDLTextFileName()
	{
		return "cddl.txt";
	}

	public static String getGPLTextFileName()
	{
		return "gpl.txt";
	}

	public static String getLGPLTextFileName()
	{
		return "lgpl.txt";
	}

	public static String getMITLTextFileName()
	{
		return "mit.txt";
	}

	public static void setCurrentProjectName( String curPrjName )
	{
		currentPprojectName = curPrjName;
	}

	public static void setSDKInstallPath( String sSDKInstallPath )
	{
		SDKInstallPath = sSDKInstallPath;
	}

	public static String getDeviceAPIListFileName()
	{
		return "deviceAPI_info.xml";
	}

	public static String getTempFolderName()
	{
		return "_temp_20100108_";
	}

	public static String getPluginDataDirName()
	{
		return "plugin_data";
	}

	public static void deleteTempFolder()
	{
		File tempApp = new File( Dependence.getAppsDirPath() + Dependence.getPathChar() + "_temp_20100108_" );
		File tempApp2 = new File( Dependence.getProperty( "APP_PATH" ) + "_temp_20100108_" );
		if( tempApp.exists() )
		{
			FileUtil.deleteFolder( tempApp );
		}
		if( tempApp2.exists() )
		{
			FileUtil.deleteFolder( tempApp2 );
		}
	}

	public static String getEmulatorSharedWorkspaceDirName()
	{
		return "AppsWorkspace";
	}
}