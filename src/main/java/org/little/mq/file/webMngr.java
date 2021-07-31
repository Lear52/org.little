package org.little.mq.file;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.json.JSONArray;
//import org.json.JSONObject;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Version;
import org.little.web.webRun;
/**
 * @author av
 *  
 */
public class webMngr extends webRun{
       private static final long serialVersionUID = -8820420158454598488L;
       private static final Logger logger = LoggerFactory.getLogger(webMngr.class);

       public webMngr(){
       }

       @Override
       public void init() throws ServletException {

              logger.trace("start"+":"+getServletInfo());
              
              
              String xpath=this.getServletContext().getRealPath("");

              String _xpath=getParametr("config");
              xpath+=_xpath;

                 logger.info("START LITTLE.SYSLOG config:"+xpath+" "+Version.getVer()+"("+Version.getDate()+")");
              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
       }

       @Override
       public String getServletInfo() {
              return "syslog server";
       }

       @Override
       public void destroy() {
              //if(server!=null){
              //   server.stop();
              //}
              //server=null;
              super.destroy();
              logger.info("STOP LITTLE.SYSLOG "+Version.getVer()+"("+Version.getDate()+")");
       }


       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              String    path = (String) request.getPathInfo();
              String    page=null;

              logger.trace("webMngr.doRun() path:"+path);

  
              //if(page==null)page = commonSyslog.get().getDefPage();
              logger.trace("webMngr.doRun() page:"+page);
              //-----------------------------------------------------------------------------------------
              //if(page!=null)
              try {
                   ServletConfig servlet_cfg  = getServletConfig();
                   ServletContext servlet_cntx = servlet_cfg.getServletContext();
                   RequestDispatcher d = servlet_cntx.getRequestDispatcher(page);
              
                   d.forward(request, response);
              } catch (Exception ex) {
                      logger.error("error forward to " + page +" Exception:"+ex);
              }
                
       }

}

