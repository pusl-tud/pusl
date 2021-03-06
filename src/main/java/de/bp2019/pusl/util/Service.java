package de.bp2019.pusl.util;

import com.vaadin.flow.server.VaadinServlet;

import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Simple class to access Beans in a static way. Needed in views
 * 
 * @author Leon Chemnitz
 */
public final class Service {

  private Service(){}

  public static <T> T get(Class<T> serviceType) {
    return WebApplicationContextUtils.getWebApplicationContext(VaadinServlet.getCurrent().getServletContext())
        .getBean(serviceType);
  }
}