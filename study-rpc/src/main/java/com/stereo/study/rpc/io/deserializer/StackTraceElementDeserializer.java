package com.stereo.study.rpc.io.deserializer;


/**
 * Deserializing a JDK 1.4 StackTraceElement
 */
public class StackTraceElementDeserializer extends JavaDeserializer {
  public StackTraceElementDeserializer()
  {
    super(StackTraceElement.class);
  }

  @Override
  protected Object instantiate()
    throws Exception
  {
    return new StackTraceElement("", "", "", 0);
  }
}
