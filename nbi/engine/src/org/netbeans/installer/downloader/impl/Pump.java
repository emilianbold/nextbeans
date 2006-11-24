package org.netbeans.installer.downloader.impl;

import org.netbeans.installer.downloader.Pumping;
import org.netbeans.installer.downloader.connector.URLConnector;
import org.netbeans.installer.downloader.dispatcher.Process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.StreamUtils;

/**
 * @author Danila_Dugurov
 */
public class Pump implements Process {

  private static final int ATTEMPT_TIME_DELAY = 5 * 1000;
  private static final int MAX_ATTEMPT_COUNT = 15;

  final PumpingImpl pummping;
  URLConnector connector = URLConnector.getConnector();

  InputStream in;
  OutputStream out;

  public Pump(Pumping pumping) {
    this.pummping = (PumpingImpl) pumping;
  }

  public PumpingImpl pumping() {
    return pummping;
  }

  public void init() {
  }

  public void run() {
    if (!initPumping()) return;
    pummping.fireChanges("pumpingUpdate");
    processPumping();
  }

  private boolean initPumping() {
    int attemptCount = 0;
    while (attemptCount < MAX_ATTEMPT_COUNT) {
      pummping.changeState(Pumping.State.CONNECTING);
      try {
        final URL url = pummping.declaredURL();
        URLConnection connection = connector.establishConnection(url);
        in = connection.getInputStream();
        if (exitOnInterrupt()) return false;
        initPumping(connection);
        pummping.changeState(Pumping.State.WAITING);
        return true;
      } catch (IOException ex) {
        LogManager.log(ex);
        attemptCount++;
        try {
          pummping.changeState(Pumping.State.WAITING);
          Thread.sleep(ATTEMPT_TIME_DELAY);
        } catch (InterruptedException exit) {
          pummping.changeState(Pumping.State.INTERRUPTED);
          return false;
        }
      } finally {
        try {
          if (in != null) in.close();
        } catch (IOException ignored) {
            LogManager.log(ignored);
        }
      }
    }
    pummping.changeState(Pumping.State.FAILED);
    return false;
  }

  private void initPumping(URLConnection connection) throws IOException {
    final Date lastModif = new Date(connection.getLastModified());
    final URL realUrl = connection.getURL();
    final String accept = connection.getHeaderField("Accept-Ranges");
    final boolean acceptBytes = accept != null ? accept.contains("bytes"): false;
    final long length = connection.getContentLength();
    pummping.init(realUrl, length, lastModif, acceptBytes);
  }

  private boolean processPumping() {
    int attemptCount = 0;
    while (attemptCount < MAX_ATTEMPT_COUNT) {
      pummping.changeState(Pumping.State.CONNECTING);
      try {
        final SectionImpl section = pummping.getSection();
        final URL connectingUrl = pummping.realURL();
        URLConnection connection = connector.establishConnection(connectingUrl, section.headers());
        in = connection.getInputStream();
        if (exitOnInterrupt()) return false;
        out = ChannelUtil.channelFragmentAsStream(pummping.outputFile(), section);
        pummping.changeState(Pumping.State.PUMPING);
        StreamUtils.transferData(in, out);
        out.flush();
        if (section.length() > 0)
          if (section.offset() != section.start() + section.length())
            continue;
        pummping.changeState(Pumping.State.FINISHED);
        return true;
      } catch (IOException ex) {
        LogManager.log(ex);
        if (exitOnInterrupt()) return false;
        attemptCount++;
        try {
          pummping.changeState(Pumping.State.WAITING);
          Thread.sleep(ATTEMPT_TIME_DELAY);
        } catch (InterruptedException exit) {
          pummping.changeState(Pumping.State.INTERRUPTED);
          return false;
        }
      } finally {
        if (in != null) try {
          in.close();
        } catch (IOException ignored) {
            LogManager.log(ignored);
        }
        if (out != null) try {
          out.close();
        } catch (IOException ignored) {
            LogManager.log(ignored);
        }
      }
    }
    pummping.changeState(Pumping.State.FAILED);
    return false;
  }

  private boolean exitOnInterrupt() {
    if (!Thread.interrupted()) return false;
    pummping.changeState(Pumping.State.INTERRUPTED);
    return true;
  }

  public void terminate() {
    if (in != null) try {
      in.close();
    } catch (IOException ignored) {
        LogManager.log(ignored);
    }
    if (out != null) try {
      out.close();
    } catch (IOException ignored) {
        LogManager.log(ignored);
    }
  }
}