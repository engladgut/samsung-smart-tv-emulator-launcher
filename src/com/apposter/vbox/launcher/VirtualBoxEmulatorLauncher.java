package com.apposter.vbox.launcher;

import java.io.File;

import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.ISession;
import org.virtualbox_4_2.MachineState;
import org.virtualbox_4_2.VBoxException;
import org.virtualbox_4_2.VirtualBoxManager;

public class VirtualBoxEmulatorLauncher
{
	public static abstract interface ICallBack
	{
		public abstract void onSuccess( String paramString );

		public abstract void onFailure( String paramString );
	}

	public static enum LauncherMode
	{
		MODE_RUN,
		MODE_DEBUG,
		MODE_PREVIEW;
	}

	private final String				VB_WEB_SERVER_URL_DEFAULT	= "http://localhost:18083";
	private final String				VB_SHARED_DIR_NAME			= "Apps";
	private String						virtual_box_web_server_url;
	private String						linux_emulator_machine_name;
	private String						linux_username;
	private String						linux_password;
	private static Process				processVBoxServer			= null;
	private static boolean				bVBoxOnLaunchLocked			= false;
	private static VirtualBoxManager	vBoxManager					= null;
	private ICallBack					onLaunchedCB				= null;
	private String						appName						= null;
	private LauncherMode				lMode						= LauncherMode.MODE_RUN;
	private String						sceneName					= null;

	public VirtualBoxEmulatorLauncher( String name, LauncherMode mode, String scene )
	{
		this.appName = name;
		this.lMode = mode;
		this.sceneName = scene;
	}

	public VirtualBoxEmulatorLauncher( String appName )
	{
		this( appName, LauncherMode.MODE_RUN, null );
	}

	private boolean connectMachine()
	{
		this.virtual_box_web_server_url = VB_WEB_SERVER_URL_DEFAULT;
		this.linux_emulator_machine_name = Dependence.getProperty( "EMULATOR_MACHINE_NAME" );
		this.linux_username = "smarttv"; // getVBLinuxID();
		this.linux_password = "password"; // getVBLinuxPassword();

		if( vBoxManager == null )
		{
			try
			{
				vBoxManager = VirtualBoxManager.createInstance( null );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}

		if( vBoxManager == null )
		{

			System.out.println( "Sorry, cannot start a VirtualBox \nPlease try again." );
			return false;
		}

		IMachine machine = null;
		boolean bReconnect = true;

		if( vBoxManager.getVBox() != null )
		{
			try
			{
				machine = vBoxManager.getVBox().findMachine( this.linux_emulator_machine_name );
				bReconnect = false;
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}

		try
		{
			if( bReconnect )
			{
				vBoxManager.connect( this.virtual_box_web_server_url, this.linux_username, this.linux_password );
				machine = vBoxManager.getVBox().findMachine( this.linux_emulator_machine_name );
			}
		}
		catch( VBoxException e )
		{
			e.printStackTrace();
		}

		if( !machineCheck( machine ) ) { return false; }

		return true;
	}

	private boolean initializeVirtualBox()
	{
		if( !startVBoxWebSrv() ) { return false; }
		if( !connectMachine() ) { return false; }
		if( !updateSharedDir() ) { return false; }

		return true;
	}

	private boolean machineCheck( IMachine machine )
	{
		if( machine == null )
		{
			if( VirtualBoxEmulatorLauncher.this.linux_emulator_machine_name.length() == 0 )
			{
				System.out.println( "Could not find a registered machine.\nPlease check the Emulator setting from Samsung Smart TV Preference page." );
			}
			else
			{
				System.out.println( "Could not find a registered machine named \"" + VirtualBoxEmulatorLauncher.this.linux_emulator_machine_name
						+ "\".\nPlease check the Emulator machine name of VirtualBox." );
			}

			return false;
		}

		if( machine.getState() == MachineState.Running )
		{
			System.out.println( "Emulator is already running." );

			return false;
		}
		return true;
	}

	private boolean startVM()
	{
		assert ( vBoxManager != null );
		assert ( vBoxManager.getVBox() != null );

		boolean bResult = true;

		try
		{
			IMachine machine = vBoxManager.getVBox().findMachine( this.linux_emulator_machine_name );

			try
			{
				ISession session = vBoxManager.getSessionObject();
				machine.launchVMProcess( session, "gui", "" );
			}
			catch( Exception e )
			{
				bResult = false;
				e.printStackTrace();
			}

			return bResult;
		}
		catch( Exception e )
		{
			bResult = false;
			e.printStackTrace();
		}

		return bResult;
	}

	class VirtualBoxEmulator implements Runnable
	{
		VirtualBoxEmulator()
		{}

		public void run()
		{
			if( !VirtualBoxEmulatorLauncher.this.initializeVirtualBox() )
			{
				if( VirtualBoxEmulatorLauncher.this.onLaunchedCB != null )
				{
					VirtualBoxEmulatorLauncher.this.onLaunchedCB.onFailure( "Initialize VirtualBox Failure" );
				}

				VirtualBoxEmulatorLauncher.bVBoxOnLaunchLocked = false;

				return;
			}

			// if( !EmulatorLogServer.isRunning() )
			// {
			// EmulatorLogServer.startServer();
			// }

			if( VirtualBoxEmulatorLauncher.this.lMode == VirtualBoxEmulatorLauncher.LauncherMode.MODE_PREVIEW )
			{
				createPreviewProject( VirtualBoxEmulatorLauncher.this.appName, VirtualBoxEmulatorLauncher.this.sceneName );
			}

			if( !createAppNameInfoFile( VirtualBoxEmulatorLauncher.this.appName, VirtualBoxEmulatorLauncher.this.lMode ) )
			{
				if( VirtualBoxEmulatorLauncher.this.onLaunchedCB != null )
				{
					VirtualBoxEmulatorLauncher.this.onLaunchedCB.onFailure( "create App Name Info Failed" );
				}

				VirtualBoxEmulatorLauncher.bVBoxOnLaunchLocked = false;

				return;
			}

			if( !VirtualBoxEmulatorLauncher.this.startVM() )
			{
				if( VirtualBoxEmulatorLauncher.this.onLaunchedCB != null )
				{
					VirtualBoxEmulatorLauncher.this.onLaunchedCB.onFailure( "Start VirtualBox Failed... ;-(" );
				}

				VirtualBoxEmulatorLauncher.bVBoxOnLaunchLocked = false;

				return;
			}

			if( VirtualBoxEmulatorLauncher.this.onLaunchedCB != null )
			{
				VirtualBoxEmulatorLauncher.this.onLaunchedCB.onSuccess( "VirtualBox Launch Succeed!" );
			}

			VirtualBoxEmulatorLauncher.bVBoxOnLaunchLocked = false;
		}

		private boolean createPreviewProject( String appName, String sceneName )
		{
			if( ( sceneName == null ) || ( sceneName.length() == 0 ) ) { return false; }
			String previewProjectPath = Dependence.getProperty( "APP_PATH" ) + SDKInfo.getTempFolderName();
			String currentProjectPath = Dependence.getProperty( "APP_PATH" ) + appName;
			File curPrj = new File( currentProjectPath );
			File prevPrj = new File( previewProjectPath );

			try
			{
				if( FileUtil.copyDir( curPrj, prevPrj ) )
				{
					String initFilePath = previewProjectPath + Dependence.getPathChar() + "app" + Dependence.getPathChar() + "init.js";
					File initFile = new File( initFilePath );
					String fileText = FileUtil.readTextFile( initFilePath );
					String replaceShowText = "sf.scene.show('" + sceneName + "' , 0);";
					String replaceFocusText = "sf.scene.focus('" + sceneName + "');";
					fileText = fileText.replaceAll( "sf\\.scene\\.show(.*?);", replaceShowText );
					fileText = fileText.replaceAll( "sf\\.scene\\.focus(.*?);", replaceFocusText );
					FileUtil.writeLineTextFile( initFile, fileText );
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			return true;
		}

		private boolean createAppNameInfoFile( String appName, VirtualBoxEmulatorLauncher.LauncherMode mode )
		{
			File folder = new File( Dependence.getProperty( "APP_PATH" ) ).getParentFile();

			String appNameInfoFilePath = folder.getAbsolutePath() + Dependence.getPathChar() + "_AppName_80292_.info"; 
			String appInfoFilePath = Dependence.getProperty( "APP_PATH" ) + Dependence.getPathChar() + "appinfo.js";

			FileUtil.createFile( appNameInfoFilePath );

			File appNameInfoFile = new File( appNameInfoFilePath );
			File appInfoFile = new File( appInfoFilePath );

			if( appInfoFile.exists() )
			{
				appInfoFile.delete();
			}

			String lineText = "";

			if( appName != null )
			{
				if( mode == VirtualBoxEmulatorLauncher.LauncherMode.MODE_PREVIEW )
				{
					lineText = lineText + "-f " + SDKInfo.getTempFolderName() + "\n";
					lineText = lineText + "-m kEclipsePre\n";
				}
				else if( mode == VirtualBoxEmulatorLauncher.LauncherMode.MODE_DEBUG )
				{
					lineText = lineText + "-f " + appName + "\n";
					lineText = lineText + "-m kEclipseDbg\n";
				}
				else
				{
					FileUtil.createFile( appInfoFilePath );
					String appInfoText = "{\"WidgetID\":\"" + appName + "\"}";
					FileUtil.writeLineTextFile( appInfoFile, appInfoText );
				}
			}

			// String localHost = EmulatorLogServer.getListeningAddress();
			// int port = EmulatorLogServer.getListeningPort();

			String localHost = "127.0.0.1";
			int port = 8888;

			if( ( localHost != null ) && ( port != -1 ) )
			{
				lineText = lineText + "-s " + localHost + "\n";
				lineText = lineText + "-p " + port;
			}

			FileUtil.writeLineTextFile( appNameInfoFile, lineText );

			return appNameInfoFile.exists();
		}
	}

	public void setLaunchedCallback( ICallBack launchVBox )
	{
		this.onLaunchedCB = launchVBox;
	}

	public boolean launchThread()
	{
		if( bVBoxOnLaunchLocked )
		{
			System.out.println( "Emulator is starting up." );

			return false;
		}

		bVBoxOnLaunchLocked = true;

		VirtualBoxEmulator vBoxEmulator = new VirtualBoxEmulator();
		Thread vBoxEmulatorThread = new Thread( vBoxEmulator );
		vBoxEmulatorThread.start();

		return true;
	}

	public void removePreviousSharedDir()
	{
		try
		{
			synchronized( VirtualBoxEmulatorLauncher.class )
			{
				Runtime.getRuntime().exec(
						new String[] { Dependence.getVBManagePathFromOS(), "sharedfolder", "remove", Dependence.getProperty( "EMULATOR_MACHINE_NAME" ),
								"--name", VB_SHARED_DIR_NAME } );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	public boolean addSharedDir()
	{
		boolean bResult = true;

		String title = null;
		String message = null;

		if( !Dependence.getProperty( "EMULATOR_MACHINE_NAME" ).matches( SDKInfo.getAllowedProjectNameChar() ) )
		{
			title = "Emulator Machine Name Error";
			// message = MessagesUtil.NameErrorWarning;
			bResult = false;
		}

		if( !bResult )
		{
			final String ftitle = title;
			final String fmessage = message;

			System.out.println( fmessage );

			return false;
		}

		try
		{
			synchronized( VirtualBoxEmulatorLauncher.class )
			{
				String hostPath = new File( Dependence.getProperty( "APP_PATH" ) ).getParentFile().getAbsolutePath();

				// System.out.println( "sharedfolder" + " add " +
				// Dependence.getProperty( "EMULATOR_MACHINE_NAME" ) +
				// " --name " + VB_SHARED_DIR_NAME + " --hostpath "
				// + hostPath + " --automount" );

				Runtime.getRuntime().exec(
						new String[] { Dependence.getVBManagePathFromOS(), "sharedfolder", "add", Dependence.getProperty( "EMULATOR_MACHINE_NAME" ), "--name",
								VB_SHARED_DIR_NAME, "--hostpath", hostPath, "--automount" } );
			}
		}
		catch( Exception e )
		{
			bResult = false;
			e.printStackTrace();
		}

		return bResult;
	}

	private boolean updateSharedDir()
	{
		boolean bResult = true;

		removePreviousSharedDir();

		try
		{
			Thread.sleep( 2000L );
		}
		catch( Exception e )
		{
			bResult = false;
			e.printStackTrace();
		}

		if( !addSharedDir() ) { return false; }

		try
		{
			Thread.sleep( 2000L );
		}
		catch( Exception e )
		{
			bResult = false;
			e.printStackTrace();
		}

		return bResult;
	}

	private boolean spaceCheck( String spaceCheck )
	{
		for( int i = 0; i < spaceCheck.length(); i++ )
		{
			if( spaceCheck.charAt( i ) == ' ' ) { return true; }
		}
		return false;
	}

	private boolean isNumber( String str )
	{
		boolean check = true;
		for( int i = 0; i < str.length(); i++ )
		{
			if( !Character.isDigit( str.charAt( i ) ) )
			{
				check = false;
				break;
			}
		}
		return check;
	}

	private boolean startVBoxWebSrv()
	{
		boolean bResult = true;
		if( processVBoxServer == null )
		{
			try
			{
				Runtime.getRuntime().exec( new String[] { Dependence.getVBManagePathFromOS(), "setproperty", "websrvauthlibrary", "null" } );

				try
				{
					Thread.sleep( 2000L );
				}
				catch( Exception e )
				{
					bResult = false;
					e.printStackTrace();
				}

				if( bResult )
				{
					int portStartNumber = VB_WEB_SERVER_URL_DEFAULT.lastIndexOf( ":" ) + 1;
					int portEndNumber = VB_WEB_SERVER_URL_DEFAULT.length();

					String portNumber = VB_WEB_SERVER_URL_DEFAULT.substring( portStartNumber, portEndNumber );

					portNumber = portNumber.trim();

					if( ( spaceCheck( portNumber ) ) || ( !isNumber( portNumber ) ) )
					{
						System.out.println( "VirtualBox WebServer Port Number Error" );
					}
					else
					{
						processVBoxServer = Runtime.getRuntime().exec( new String[] { Dependence.getVBWebServerPathFromOS(), "-p", portNumber } );
					}
				}
			}
			catch( Exception e )
			{
				bResult = false;
				e.printStackTrace();
			}
		}

		return bResult;
	}

	public static void cleanupVBox()
	{
		if( processVBoxServer != null )
		{
			processVBoxServer.destroy();
		}
		bVBoxOnLaunchLocked = false;
	}

	// public static String getVBManageFilePath()
	// {
	// String VBManageFilePath = SDKPreferences.getPreferenceStore().getString(
	// "SamsungEmulatorVBoxManage" );
	//
	// if( VBManageFilePath.replaceAll( "\\p{Space}", "" ).trim().length() == 0
	// )
	// {
	// VBManageFilePath = Dependence.getVBManagePathFromOS();
	// }
	//
	// return VBManageFilePath;
	// }

	// public String getVBFilePath()
	// {
	// String VBFilePath = SDKPreferences.getPreferenceStore().getString(
	// "SamsungEmulatorVBox" );
	// if( VBFilePath.replaceAll( "\\p{Space}", "" ).trim().length() == 0 )
	// {
	// VBFilePath = Dependence.getVBFilePathFromOS();
	// }
	// return VBFilePath;
	// }

	// public String getVBWebServerFilePath()
	// {
	// String VBWebServerFilePath =
	// SDKPreferences.getPreferenceStore().getString(
	// "SamsungEmulatorVBoxServer" );
	// if( VBWebServerFilePath.replaceAll( "\\p{Space}", "" ).trim().length() ==
	// 0 )
	// {
	// VBWebServerFilePath = Dependence.getVBWebServerFilePathFromOS();
	// }
	// return VBWebServerFilePath;
	// }

	// public String getVBWebServerURL()
	// {
	// String VBWebServerURL = SDKPreferences.getPreferenceStore().getString(
	// "SamsungEmulatorVBoxURL" );
	// if( VBWebServerURL.replaceAll( "\\p{Space}", "" ).trim().length() == 0 )
	// {
	// VBWebServerURL = "http://localhost:18083";
	// }
	// return VBWebServerURL;
	// }

	// public String getVBEmulMachineName()
	// {
	// String VBEmulMachineName = SDKPreferences.getPreferenceStore().getString(
	// "SamsungEmulatorVBoxMachine" );
	// if( VBEmulMachineName.replaceAll( "\\p{Space}", "" ).trim().length() == 0
	// )
	// {
	// VBEmulMachineName = SDKInfo.getDefaultEmulatorName();
	// }
	// return VBEmulMachineName;
	// }

	// public String getVBLinuxID()
	// {
	// IPreferenceStore store = SDKPreferences.getPreferenceStore();
	// String user = store.getString( "LINUX_USERNAME" );
	// if( user.replaceAll( "\\p{Space}", "" ).trim().length() == 0 )
	// {
	// user = "smarttv";
	// store.setValue( "LINUX_USERNAME", user );
	// }
	// return user;
	// }

	// public String getVBLinuxPassword()
	// {
	// IPreferenceStore store = SDKPreferences.getPreferenceStore();
	// String password = store.getString( "LINUX_PASSWORD" );
	// if( password.replaceAll( "\\p{Space}", "" ).trim().length() == 0 )
	// {
	// password = "password";
	// store.setValue( "LINUX_PASSWORD", password );
	// }
	// return password;
	// }
}