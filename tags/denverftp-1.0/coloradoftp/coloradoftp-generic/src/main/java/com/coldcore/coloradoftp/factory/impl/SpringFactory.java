package com.coldcore.coloradoftp.factory.impl;

import com.coldcore.coloradoftp.factory.InternalFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * Object factory based on the Spring framework.
 * @see com.coldcore.coloradoftp.factory.ObjectFactory
 */
public class SpringFactory implements InternalFactory {

  protected BeanFactory beanFactory;


  public SpringFactory(Resource resource) {
    beanFactory = new XmlBeanFactory(resource);
  }


  public SpringFactory(BeanFactory beanFactory) {
    if (beanFactory == null) throw new IllegalArgumentException("Bean factory is not set");
    this.beanFactory = beanFactory;
  }


  public Object getBean(String name) {
    return beanFactory.getBean(name);
  }
}
