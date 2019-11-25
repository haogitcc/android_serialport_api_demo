package com.thingmagic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import android_serialport_api.SerialPort;


public class SerialTransportAndroid
  implements SerialTransport
{
  private SerialPort mSerialPort;
  private InputStream is;
  private OutputStream os;
  private String path;
  private int baudrate = 115200;
  
  public SerialTransportAndroid(String readerUri)
  {
    this.path = readerUri;
  }
  
  public void open()
    throws ReaderException
  {
    try
    {
      this.mSerialPort = new SerialPort(this.path, this.baudrate);
    }
    catch (Exception e)
    {
      return;
    }
    this.is = this.mSerialPort.getInputStream();
    this.os = this.mSerialPort.getOutputStream();
  }
  
  public void sendBytes(int length, byte[] message, int offset, int timeoutMs)
    throws ReaderException
  {
    try
    {
      if (this.os == null) {
        throw new ReaderException("android_serialport Connection lost");
      }
      System.out.println("sendBytes : " + message[2]);
      
      this.os.write(message, offset, length);
    }
    catch (Exception ex)
    {
      throw new ReaderCommException(ex.getMessage());
    }
  }
  
  public byte[] receiveBytes(int length, byte[] messageSpace, int offset, int timeoutMillis)
    throws ReaderException
  {
    try
    {
      if (this.is == null) {
        throw new IOException("android_serialport Connection lost");
      }
      int responseWaitTime = 0;
      while ((this.is.available() < length) && (responseWaitTime < timeoutMillis))
      {
        Thread.sleep(10L);
        
        responseWaitTime += 10;
      }
      if (this.is.available() <= 0) {
        throw new IOException("android_serialport Timeout");
      }
      this.is.read(messageSpace, offset, length);
      System.out.println("receiveBytes : %x" + messageSpace[2]);
    }
    catch (Exception ex)
    {
      throw new ReaderCommException(ex.getMessage());
    }
    return messageSpace;
  }
  
  public int getBaudRate()
    throws ReaderException
  {
    return 0;
  }
  
  public void setBaudRate(int rate)
    throws ReaderException
  {}
  
  public void flush()
    throws ReaderException
  {}
  
  public void shutdown()
    throws ReaderException
  {
    this.mSerialPort.close();
  }
  
  public static class Factory
    implements ReaderFactory
  {
    public SerialReader createReader(String uriString)
      throws ReaderException
    {
      String readerUri = null;
      try
      {
        URI uri = new URI(uriString);
        readerUri = uri.getPath();
      }
      catch (Exception localException) {}
      return new SerialReader(readerUri, new SerialTransportAndroid(readerUri));
    }
  }
}
