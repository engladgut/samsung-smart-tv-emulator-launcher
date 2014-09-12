package com.apposter.vbox.launcher;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class WebInspectorLauncher implements Runnable
{
	String	connectionURL	= "http://localhost:8888";
	boolean	bInPending		= false;

	public void run()
	{
		File chromeFile = new File( Dependence.getChromeBinaryPath() );
		
		if( !chromeFile.exists() )
		{
			System.out
					.println( "The Chrome browser is not installed.\nChrome browser are necessary to run the Web Inspector.\nPlease install the Chrome browser. \nIf you have already installed Chrome, please set Chrome's path in the Samsung Smart TV SDK Preferences screen" );
			openChromeDownloadSite();

			return;
		}

		System.out.println( "Launch VirtualBox..." );
		
		VirtualBoxEmulatorLauncher vBoxEmulator = new VirtualBoxEmulatorLauncher( Dependence.getAppName(),
				VirtualBoxEmulatorLauncher.LauncherMode.MODE_DEBUG, null );
		vBoxEmulator.setLaunchedCallback( new launchVBox() );
		
		if( !vBoxEmulator.launchThread() )
		{}
	}

	class launchVBox implements VirtualBoxEmulatorLauncher.ICallBack
	{
		launchVBox()
		{}

		public void onSuccess( String msg )
		{
			System.out.println( msg );
			
			File chromeBinary = new File( Dependence.getChromeBinaryPath() );
			
			if( !WebInspectorLauncher.this.connectServer() )
			{
				System.out
						.println( "Automatic connection failed.\nFailed to connect to remote server of Web Inspector.\nPlease run Chrome browser manually and connect the 'http://localhost:8888' " );

				return;
			}
			
			try
			{
				if( chromeBinary.exists() )
				{
					String osName = System.getProperty( "os.name" ).toLowerCase();
					String[] szParam = null;
					
					if( osName.indexOf( "mac" ) >= 0 )
					{
						String chromeCommand = "open -a \"" + Dependence.getChromeBinaryPath() + "\" " + WebInspectorLauncher.this.connectionURL;
						szParam = new String[] { "/bin/bash", "-c", chromeCommand };
					}
					else
					{
						szParam = new String[] { Dependence.getChromeBinaryPath(), WebInspectorLauncher.this.connectionURL };
					}
					
					Runtime.getRuntime().exec( szParam );
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}

		public void onFailure( String msg )
		{
			// WebInspectorLauncher.this.bInPending = true;
			//
			// display.asyncExec( new Runnable()
			// {
			// public void run()
			// {
			// if( WebInspectorLauncher.this.WILoadingDlg != null )
			// {
			// WebInspectorLauncher.this.WILoadingDlg.close();
			// }
			// WebInspectorLauncher.this.bInPending = false;
			// }
			// } );

			System.out.println( msg );
		}
	}

	private int getResponseCodeFromURL( String urlSpec )
	{
		int responseCode = 0;
		
		try
		{
			URL url = new URL( urlSpec );
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection)connection;

			responseCode = httpConn.getResponseCode();
		}
		catch( Exception e )
		{
//			e.printStackTrace();
			System.out.println( "Connect to Remote Inspect Service... Please wait..." );
		}
		
		return responseCode;
	}

	private boolean connectServer()
	{
		int tryNumber = 0;
		int responseCode = 0;
		this.bInPending = true;

		boolean success = false;

		while( ( responseCode != 200 ) && this.bInPending )
		{
			responseCode = getResponseCodeFromURL( this.connectionURL );

			try
			{
				Thread.sleep( 3000L );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}

			tryNumber++;

			if( tryNumber > 40 )
			{
				success = false;
				break;
			}
		}

		if( responseCode == 200 )
		{
			success = true;
		}

		// if( this.WILoadingDlg.isOpen() )
		// {
		// this.bInPending = true;
		//
		// display.asyncExec( new Runnable()
		// {
		// public void run()
		// {
		// WebInspectorLauncher.this.WILoadingDlg.close();
		// WebInspectorLauncher.this.bInPending = false;
		// }
		// } );
		// }

		return success;
	}

	private void openChromeDownloadSite()
	{
		String ChromeDownloadURL = "http://www.google.com/chrome";

		// IWorkbenchBrowserSupport browserSupport =
		// PlatformUI.getWorkbench().getBrowserSupport();
		// try
		// {
		// webUrl = new URL( ChromeDownloadURL );
		// }
		// catch( MalformedURLException e )
		// {
		// URL webUrl;
		// e.printStackTrace();
		// return;
		// }
		// try
		// {
		// URL webUrl;
		// IWebBrowser browser = browserSupport.createBrowser( 32, null, null,
		// null );
		// browser.openURL( webUrl );
		// }
		// catch( PartInitException e )
		// {
		// e.printStackTrace();
		// return;
		// }
		//
		// IWebBrowser browser;

		System.out.println( "Download google Chrome here => " + ChromeDownloadURL );
	}
}