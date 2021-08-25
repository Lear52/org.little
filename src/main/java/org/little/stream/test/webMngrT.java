package org.little.stream.test;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.web.webRun;

public class webMngrT extends webRun{
       private static final long serialVersionUID = -3761346874646849425L;
       private static final Logger logger = LoggerFactory.getLogger(webMngr2.class);

       public webMngrT() {
              logger.trace("CREATE OBJECT");
       }
       
       @Override
       public void init() throws ServletException {

              super.init();
              logger.trace("INIT OBJECT thread:"+Thread.currentThread().toString());
       }

       @Override
       public String getServletInfo() {
              return "text server";
       }
       @Override
       public void destroy() {
              super.destroy();
              logger.info("DESTROY thread:"+Thread.currentThread().toString());
       }
       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              String    page="/index1.jsp";

              logger.trace("begin webMngr.doRun() thread:"+Thread.currentThread().toString());

              for(int i=0;i<100;i++){
                  try{
                     
                     logger.trace("WAIT OBJECT thread:"+Thread.currentThread().toString());
                     Thread.sleep(100);
                  }
                  catch(Exception e){}
              }

              logger.trace("webMngr.doRun() page:"+page+" thread:"+Thread.currentThread().toString());
              //-----------------------------------------------------------------------------------------
              if(page!=null)
              try {
                   RequestDispatcher d         = null;
                   ServletContext servlet_cntx = null;
                   ServletConfig servlet_cfg   = null;
              
                   servlet_cfg  = getServletConfig();
              
                   servlet_cntx = servlet_cfg.getServletContext();
              
                   d = servlet_cntx.getRequestDispatcher(page);
              
                   d.forward(request, response);
              } catch (Exception ex) {
                   logger.error("error forward to " + page +" Exception:"+ex);
              }
                
              logger.trace("end webMngr.doRun() thread:"+Thread.currentThread().toString());
       }



}

