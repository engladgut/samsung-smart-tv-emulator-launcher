package com.apposter.vbox.launcher;

import java.io.IOException;

public class PNaClTranslator
{
  private String currentProjectFullPath = null;
  private String pexeFileName = null;
  private int mode = -1;
  private static String translatorCmd = "pnacl-translate";
  
  public PNaClTranslator(String currentProjectFullPath, String translatorFilePath, String pexeFileName, int mode)
  {
    this.currentProjectFullPath = currentProjectFullPath;
    this.pexeFileName = pexeFileName;
    this.mode = mode;
  }
  
  private void processTranslateError(Process translate)
  {
    ErrorStreamProcess errorProcess = new ErrorStreamProcess(translate.getErrorStream(), "Error");
    errorProcess.run();
    if (!errorProcess.getErrorConent().isEmpty()) {
      System.out.println(errorProcess.getErrorConent());
    }
  }
  
  private boolean translate(Object command)
  {
    try
    {
      Process translate;
      if ((command instanceof String))
      {
        translate = Runtime.getRuntime().exec((String)command);
      }
      else
      {
        if ((command instanceof String[])) {
          translate = Runtime.getRuntime().exec((String[])command);
        } else {
          return true;
        }
      }
      processTranslateError(translate);
      try
      {
        translate.waitFor();
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
      return true;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return false;
    }
  }
  
  private boolean doTranslate(String arch, String source, String target)
  {
    if ((System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0) || 
      (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0))
    {
      String[] commandArray = { translatorCmd, "-arch", arch, "-o", target, source };
      return translate(commandArray);
    }
    String commandString = String.format("%s -arch %s -o %s %s", new Object[] { translatorCmd, arch, target, source });
    commandString = String.format("cmd /c %s", new Object[] { commandString });
    return translate(commandString);
  }
  
  public void start()
  {
    String pexeFileFullPath = this.currentProjectFullPath + Dependence.getPathChar() + this.pexeFileName + ".pexe";
    String arch;
    String postfixFileName = null;
    
    switch (this.mode)
    {
    case 0: 
      postfixFileName = "_arm.nexe";
      arch = "armv7";
      break;
    case 1: 
      postfixFileName = "_x86-32.nexe";
      arch = "i686";
      break;
    default: 
      return;
    }
    
    String nexeFileFullPath = this.currentProjectFullPath + Dependence.getPathChar() + this.pexeFileName + postfixFileName;
    doTranslate(arch, pexeFileFullPath, nexeFileFullPath);
  }
}