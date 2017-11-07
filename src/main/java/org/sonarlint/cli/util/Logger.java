/*
 * SonarLint CLI
 * Copyright (C) 2016-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarlint.cli.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Logger {
  private static volatile Logger instance;
  private boolean skipUnParsableFiles = false;
  private boolean debugEnabled = false;
  private boolean displayStackTrace = false;
  private PrintStream stdOut;
  private PrintStream stdErr;
  private String unParsableFileLocation;

  private Logger() {
    this.stdErr = System.err;
    this.stdOut = System.out;
  }

  public Logger(PrintStream stdOut, PrintStream stdErr) {
    this.stdErr = stdErr;
    this.stdOut = stdOut;
  }

  public static Logger get() {
    if (instance == null) {
      instance = new Logger();
    }
    return instance;
  }

  public static void set(PrintStream stdOut, PrintStream stdErr) {
    get().stdOut = stdOut;
    get().stdErr = stdErr;
  }

  public void setDebugEnabled(boolean debugEnabled) {
    this.debugEnabled = debugEnabled;
  }

  public void setDisplayStackTrace(boolean displayStackTrace) {
    this.displayStackTrace = displayStackTrace;
  }

  public boolean isDebugEnabled() {
    return debugEnabled;
  }

  public void setSkipUnParsableFiles(boolean skipUnParsableFiles) {
    this.skipUnParsableFiles = skipUnParsableFiles;
  }

  public void setUnParsableFileLocation(String unParsableFileLocation) {
    this.unParsableFileLocation = unParsableFileLocation;
  }
  public boolean isSkipUnParsableFilesEnabled() {
    return skipUnParsableFiles;
  }

  public void debug(String message) {
    if (isDebugEnabled()) {
      stdOut.println("DEBUG: " + message);
    }
  }

  public void debug(String message, Throwable t) {
    if (isDebugEnabled()) {
      stdErr.println("DEBUG: " + message);
      if (displayStackTrace) {
        t.printStackTrace(stdErr);
      }
    }
  }

  public void info(String message) {
    stdOut.println("INFO: " + message);
  }

  public void warn(String message) {
    stdOut.println("WARN: " + message);
  }

  public void error(String message) {

    if(isSkipUnParsableFilesEnabled() && (message.startsWith("Unable to parse file:")) ){

      try {
        stdErr.println("ERROR: " + message);
        String splitMessage = message.split("Unable to parse file:")[1].trim();
        FileUtils.writeStringToFile(new File(unParsableFileLocation), splitMessage+"\n", true);
      }catch (IOException ie){
        stdErr.println("ERROR: " + ie.getStackTrace());
        System2.INSTANCE.exit(1);
      }

    }else if(isSkipUnParsableFilesEnabled() && message.startsWith("Parse error")) {
      stdErr.println("ERROR: " + message);
    }else {
      stdErr.println("ERROR: " + message);
      System2.INSTANCE.exit(1);
    }

  }

  public void error(String message, Throwable t) {
    stdErr.println("ERROR: " + message);
    if (displayStackTrace) {
      t.printStackTrace(stdErr);
    }
    System2.INSTANCE.exit(1);

  }
}
