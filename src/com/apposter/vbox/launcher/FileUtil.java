package com.apposter.vbox.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class FileUtil
{
	public static boolean copyFile( File source, File destination )
	{
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		FileChannel fcin = null;
		FileChannel fcout = null;

		try
		{
			inputStream = new FileInputStream( source );
			outputStream = new FileOutputStream( destination );

			fcin = inputStream.getChannel();
			fcout = outputStream.getChannel();

			long size = fcin.size();

			fcin.transferTo( 0L, size, fcout );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				if( fcout != null )
				{
					fcout.close();
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
			try
			{
				if( fcin != null )
				{
					fcin.close();
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
			try
			{
				if( outputStream != null )
				{
					outputStream.close();
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
			try
			{
				if( inputStream != null )
				{
					inputStream.close();
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}

		return true;
	}

	public static boolean copyDir( File source, File destination )
	{
		InputStream in = null;
		OutputStream out = null;

		label222: try
		{
			if( source.isDirectory() )
			{
				if( !destination.exists() )
				{
					destination.mkdir();
				}
				String[] children = source.list();
				if( children != null )
				{
					for( int i = 0; i < children.length; i++ )
					{
						copyDir( new File( source, children[i] ), new File( destination, children[i] ) );
					}

					break label222;
				}
			}
			else
			{
				in = new FileInputStream( source );
				out = new FileOutputStream( destination );

				byte[] buf = new byte[ 1024 ];
				int len;

				while( ( len = in.read( buf ) ) > 0 )
				{
					out.write( buf, 0, len );
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				if( in != null )
				{
					in.close();
				}
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}

			try
			{
				if( out != null )
				{
					out.close();
				}
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}

		return true;
	}

	public static boolean delfile( String path, String name )
	{
		File file = new File( path + Dependence.getPathChar() + name );
		file.delete();

		return !file.exists();
	}

	public static boolean delfile( String path )
	{
		File file = new File( path );
		file.delete();

		return !file.exists();
	}

	public static boolean createFile( String path )
	{
		File file = new File( path );

		try
		{
			if( file.exists() )
			{
				if( file.delete() )
				{
					file.createNewFile();
				}
			}
			else
			{
				file.createNewFile();
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return file.exists();
	}

	public static boolean deleteFolder( File targetFolder )
	{
		try
		{
			File[] childFile = targetFolder.listFiles();

			if( childFile == null ) { return !targetFolder.exists(); }

			int size = childFile.length;

			if( size > 0 )
			{
				for( int i = 0; i < size; i++ )
				{
					if( childFile[i].isFile() )
					{
						childFile[i].delete();
					}
					else
					{
						deleteFolder( childFile[i] );
					}
				}
			}

			targetFolder.delete();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return !targetFolder.exists();
	}

	public static void writeLineTextFile( File file, String data )
	{
		PrintWriter pw = null;

		try
		{
			pw = new PrintWriter( file );

			pw.print( data );
			pw.flush();
		}
		catch( Exception e )
		{
			e.printStackTrace();
			try
			{
				if( pw != null )
				{
					pw.close();
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
				if( pw != null )
				{
					pw.close();
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

	public static void writeLineTextFile( File file, String[] dataArray )
	{
		PrintWriter pw = null;

		try
		{
			pw = new PrintWriter( file );

			for( String data : dataArray )
			{
				pw.println( data );
			}

			pw.flush();
		}
		catch( Exception e )
		{
			e.printStackTrace();

			try
			{
				if( pw != null )
				{
					pw.close();
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
				if( pw != null )
				{
					pw.close();
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

	public static String readTextFile( String filePath )
	{
		File textFile = new File( filePath );
		BufferedReader in = null;
		String text = null;
		StringBuilder allText = new StringBuilder( "" );

		try
		{
			in = new BufferedReader( new FileReader( textFile ) );
			while( ( text = in.readLine() ) != null )
			{
				allText.append( text ).append( "\n" );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();

			try
			{
				if( in != null )
				{
					in.close();
				}
			}
			catch( Exception ee )
			{
				ee.printStackTrace();
			}
		}
		finally
		{
			try
			{
				if( in != null )
				{
					in.close();
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}

		return allText.toString();
	}

	public static boolean isCorrectSDKInstallFolder( String sdkInstallPath )
	{
		File pluginDataDir = new File( sdkInstallPath + Dependence.getPathChar() + SDKInfo.getPluginDataDirName() );

		if( ( pluginDataDir.exists() ) && ( pluginDataDir.isDirectory() ) ) { return true; }

		return false;
	}


	public static boolean isSamsungTVApp( String appPath )
	{
		File appFolder = new File( appPath );
		File[] appFolderFileList = appFolder.listFiles();
		
		if( appFolderFileList == null ) { return false; }
		
		boolean bSamsungTVApp = false;
		
		for( File file : appFolderFileList )
		{
			if( file.isFile() )
			{
				if( isSamsungTVAppConfigFile( file ) )
				{
					bSamsungTVApp = true;
				}
			}
		}
		
		return bSamsungTVApp;
	}

	public static boolean isSamsungTVAppConfigFile( File file )
	{
		boolean bSamsungTVApp = false;
		
		if( file.isFile() )
		{
			if( file.getName().equals( "config.xml" ) )
			{
				bSamsungTVApp = true;
			}
			else
			{
				try
				{
					String[] fileNameSplit = file.getName().split( Dependence.getPathChar() + "." );
		
					if( fileNameSplit.length > 2 )
					{
						if( ( fileNameSplit[0].equals( "config" ) ) && ( fileNameSplit[1].equals( "xml" ) ) )
						{
							bSamsungTVApp = true;
						}
					}
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}
		}
		
		return bSamsungTVApp;
	}

	public static long folderSize( File directory )
	{
		long length = 0L;
		if( directory != null )
		{
			File[] files = directory.listFiles();
			if( files == null ) { return 0L; }
			File[] arrayOfFile1;
			int j = ( arrayOfFile1 = files ).length;
			for( int i = 0; i < j; )
			{
				File file = arrayOfFile1[i];
				try
				{
					if( file.isFile() )
					{
						length += file.length();
					}
					else if( file.isDirectory() )
					{
						length += folderSize( file );
					}
					if( length < 0L )
					{
						break;
					}
					i++;
				}
				catch( Exception ioe )
				{
					ioe.printStackTrace();
				}
			}
		}
		return length;
	}

	public boolean spaceCheck( String spaceCheck )
	{
		for( int i = 0; i < spaceCheck.length(); i++ )
		{
			if( spaceCheck.charAt( i ) == ' ' ) { return true; }
		}
		return false;
	}
}