package com.aidijing.deploy;

import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author : 披荆斩棘
 * @date : 2016/12/03
 */
@WebListener
public class AppContextListener implements ServletContextListener {


    @Override
    public void contextInitialized ( ServletContextEvent servletContextEvent ) {
        WebApplicationContextUtils.getRequiredWebApplicationContext( servletContextEvent.getServletContext() ).getAutowireCapableBeanFactory().autowireBean( this );
        ServletContext context = servletContextEvent.getServletContext();
    }

   

    @Override
    public void contextDestroyed ( ServletContextEvent servletContextEvent ) {

    }


}







