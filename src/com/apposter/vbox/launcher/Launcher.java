package com.apposter.vbox.launcher;

public class Launcher
{
	public static void main( String[] args )
	{
		if( !FileUtil.isSamsungTVApp( "C:\\projects\\workspace\\hcn\\HCN_EPG_H1" ) ) { return; }
		
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