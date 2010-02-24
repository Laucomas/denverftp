package com.coldcore.coloradoftp.factory;

/**
 * Object factory.
 *
 * Provides objects referred by name. Written as a static class this factory is available from all
 * other objects in the same JVM. Configuration dictates what objects must be provided as singletons
 * and wich must be not (every time a new object).
 *
 * This implementation delegates all calls to its internal factory which is responsible for
 * finding requested objects.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public class ObjectFactory {

  protected static InternalFactory internalFactory;


  private ObjectFactory() {}


  /** Get internal factory
   * @return Internal factory
   */
  public static InternalFactory getInternalFactory() {
    return internalFactory;
  }


  /** Set new internal factory
   * @param internalFactory Internal factory
   */
  public static void setInternalFactory(InternalFactory internalFactory) {
    ObjectFactory.internalFactory = internalFactory;
  }


  /** Get object by name
   * @param name Object name
   * @return Requested object (never returns NULL)
   */
  public static Object getObject(String name) {
    if (internalFactory == null) throw new IllegalStateException("Internal factory is not set");
    Object o = internalFactory.getBean(name);
    if (o == null) throw new IllegalArgumentException("Object "+name+" cannot be loaded");
    return o;
  }
}
