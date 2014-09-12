package com.apposter.vbox.launcher;

import java.io.File;

public class PNaClManager
{
	private String	pexeFileName	= "";

	public String getPexeFileName()
	{
		return this.pexeFileName;
	}

	public boolean isPNaclApp( String appFullPath )
	{
		boolean isPNaclApp = false;

		File appPath = new File( appFullPath );

		String pexeFileName = "";
		String pexeFileExt = "";

		File[] prjFileList = appPath.listFiles();
		if( prjFileList == null ) { return false; }
		for( File file : prjFileList )
		{
			if( file.isFile() )
			{
				String fileName = file.getName();

				int index = fileName.lastIndexOf( "." );
				if( index != -1 )
				{
					pexeFileName = fileName.substring( 0, index );
					pexeFileExt = fileName.substring( index + 1 );
				}
				if( pexeFileExt.equals( "pexe" ) )
				{
					File manifestFile = new File( appFullPath + Dependence.getPathChar() + pexeFileName + ".nmf" );
					if( manifestFile.exists() )
					{
						isPNaclApp = true;
						break;
					}
				}
			}
		}
		this.pexeFileName = pexeFileName;

		return isPNaclApp;
	}

	public void startPNaClTranslate()
	{
		String orgAppPath = Dependence.getProperty( "APP_PATH" ); // SDKPublicData.getCurrentProject().getName();

		if( isPNaclApp( orgAppPath ) )
		{
			File PNaclInfoFile = new File( SDKInfo.getPNaclInfoFilePath() );

			if( !PNaclInfoFile.exists() )
			{
				System.out.println( "PNacl translator path does not exist. \nPlease verify the path is correct." );
				return;
			}
			
			String translatorPath = Dependence.getPropety( SDKInfo.getPNaclInfoFilePath(), "translatorPath" );
			
			if( translatorPath == null || "".equals( translatorPath ) )
			{
				System.out.println( "Failed translate to x86-32 nexe file." );
				return;
			}

//			try
//			{
				PNaClTranslator pNaclTranslator = new PNaClTranslator( orgAppPath, translatorPath,
						getPexeFileName(), 1 );
				pNaclTranslator.start();
//			}
//			catch( Exception localException )
//			{
				
//			}
		}
	}
}
