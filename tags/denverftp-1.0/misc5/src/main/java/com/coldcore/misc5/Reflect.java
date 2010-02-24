/**
 * Created by IntelliJ IDEA.
 * User: Power
 * Date: Feb 5, 2003
 * Time: 11:07:15 PM
 * Reflect class.
 */
package com.coldcore.misc5;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflect {

  private Reflect() {}


  /** Invoke specified method of "obj" class with parameters of exact type (unless ignoreParamsTypes is set to TRUE)
   *  @param obj Object on which to invoke the method
   *  @param methodName The name of the method to invoke
   *  @param params The array with the parametes of the method
   *  @param ignoreParamsTypes If TRUE then types of parameters will not be taken into account
   *  @return The output made by the method
   *  @throws NoSuchMethodException if a class does not have the specified method
   *  @throws Exception if a method throws an exception
   */
  public static Object invokeMethod(Object obj, String methodName, Object[] params, boolean ignoreParamsTypes) throws Exception {
    Method[] methods = obj.getClass().getMethods();
    for (Method method : methods) {
      //Get the name of the next method and parameters that it takes
      String meth_name = method.getName();
      Class[] meth_params = method.getParameterTypes();

      //Check the name of the method and amount of the parameters
      if (!meth_name.equals(methodName) || meth_params.length != params.length) continue;

      //Check if this is the method we need - compare names of the parameters
      boolean ok = true;
      for (int i = 0; i < meth_params.length; i++)
        if (!meth_params[i].getName().equals(params[i].getClass().getName()) && !ignoreParamsTypes) ok = false;
      if (!ok) continue; //This is not the method we need.

      //Execute it
      try {
        return method.invoke(obj, params); //Method found, invoke it
      } catch (InvocationTargetException e) {
        //Method has thrown some exception, extract it and forward
        throw (Exception) e.getTargetException();
      }
    }

    throw new NoSuchMethodException("Method '"+methodName+"' not found in "+obj.getClass());
  }


  /** Convenience method
   * @see com.coldcore.misc5.Reflect#invokeMethod(Object, String, Object[], boolean)
   */
  public static Object invokeMethod(Object obj, String methodName, Object[] params) throws Exception {
    return invokeMethod(obj, methodName, params, true);
  }


  /** Convenience method
   * @see com.coldcore.misc5.Reflect#invokeMethod(Object, String, Object[], boolean)
   */
  public static Object invokeMethod(Object obj, String methodName) throws Exception {
    return invokeMethod(obj, methodName, new Object[0], true);
  }
}
