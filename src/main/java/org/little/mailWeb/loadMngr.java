package org.little.mailWeb;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.little.mailWeb.imap.ImapListBox;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Version;
import org.little.util.run.scheduler;
import org.little.web.webThread;
/**
 * @author av
 *  
 */
public class loadMngr extends webThread{
       private static final long   serialVersionUID = -8857423924701221546L;
       private static final Logger logger           = LoggerFactory.getLogger(loadMngr.class);
       private static ImapListBox  client           = new ImapListBox();

       public loadMngr(){
              logger.info("create object loadMngr runner:"+size_runner);
       } 
       @Override
       protected void preinit(){
           size_runner=2;
           runner = new scheduler(size_runner);
       }
       @Override
       public void init() throws ServletException {

              logger.trace("start"+":"+getServletInfo());
              
              String xpath =this.getServletContext().getRealPath("");
              String _xpath=getParametr("config");
              xpath+=_xpath;

              if(client.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.IMAPWEB(LOAD) config:"+xpath+" "+Version.getVer()+"("+Version.getDate()+")");
              client.start(runner);
              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
       }
       @Override
       public void destroy() {

              client.stop(runner);
              runner.stop();
              runner=null;
              client=null;
              super.destroy();
              logger.info("STOP LITTLE.IMAPWEB(LOAD) "+Version.getVer()+"("+Version.getDate()+")");
       }


       @Override
       public String getServletInfo() {
              return "Load key data";
       }
       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{}

}

