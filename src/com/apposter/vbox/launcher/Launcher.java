package com.apposter.vbox.launcher;

public class Launcher
{
	public static void main( String[] args )
	{
		System.out.println( "Chrome => " + Dependence.getChromeBinaryPath() );
		System.out.println( "App path => " + Dependence.getAppsDirPath() );
		System.out.println( "App name => " + Dependence.getAppName() );
		
		if( !FileUtil.isSamsungTVApp( Dependence.getAppsDirPath() ) )
		{
			System.out.println( "Invalid app path or there is no Samsung TV App." );
			return;
		}
		
//		if( ( SDKInfo.getCurrentProjectName() == null ) || ( !SDKInfo.getCurrentProjectName().equals( project.getName() ) ) )
//		{
			SDKInfo.setCurrentProjectName( "HCN_EPG_H1" );
//		}
		
		WebInspectorLauncher webInspector = new WebInspectorLauncher();
		Thread webInspectorThread = new Thread( webInspector );
		webInspectorThread.start();

		PNaClManager managerPNaCl = new PNaClManager();
		managerPNaCl.startPNaClTranslate();
	}
}