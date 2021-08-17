package org.little.stream.http;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.little.stream.cfg.commonStream;
import org.little.stream.mngr.ufpsFrame;
import org.little.stream.mngr.ufpsManager;
import org.little.stream.mngr.ufpsUser;
import org.little.stream.test.ufpsReader;
import org.little.stream.ufps.ufpsMsg;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Version;
import org.little.web.webRun;
/**
 * @author av
 *  
 */
@WebServlet("/stream/put")
@MultipartConfig(fileSizeThreshold=1024*1024*1,// 1MB 
                 maxFileSize=1024*1024*10,       // 10MB
               maxRequestSize=1024*1024*10)       // 10MB
public class webMngr extends webRun{
       private static final long serialVersionUID = -3761343738105949425L;
       private static final Logger logger = LoggerFactory.getLogger(webMngr.class);
       private static commonStream cfg;
       private static ufpsManager  mngr;

       @Override
       public void init() throws ServletException {

              logger.trace("start"+":"+getServletInfo());
              cfg=new commonStream();
              
              String xpath=this.getServletContext().getRealPath("");

              String _xpath=getParametr("config");
              xpath+=_xpath;

              if(cfg.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              
              mngr=new ufpsManager(cfg);
              logger.info("START LITTLE.STREAM config:"+xpath+" "+Version.getVer()+"("+Version.getDate()+")");
              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
       }

       @Override
       public String getServletInfo() {
              return "stream server";
       }
       private void doGetMsg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               String username = request.getUserPrincipal().getName();
               logger.trace("begin doGetList");
               ufpsUser user = mngr.getUser(username);
               if(user==null) {
                  response.setStatus(401);
                  return;            
               }
              
               ufpsMsg msg=user.getStream().get().getMsg();
              
               response.setContentType("application/xml");
               
               response.getWriter().write(msg.print());
              
               logger.trace("end doGetList");
       }

       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              String    path = (String) request.getPathInfo();
              String    page=null;

              logger.trace("webMngr.doRun() path:"+path);

              if(path.startsWith("/get")){
                 doGetMsg(request,response);
                 return;
              }
  
              if(page==null)page = cfg.getDefPage();
              logger.trace("webMngr.doRun() page:"+page);
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
                
       }

       /**
        * handles msg upload
        */
        @Override
       public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
               String username = request.getUserPrincipal().getName();
               ufpsUser user = mngr.getUser(username);
               if(user==null) {
                  response.setStatus(401);
                  return;            
               }
        
               for (Part part : request.getParts()) {
                    int ret=0;
                    InputStream in = part.getInputStream();
                    int         size=(int)part.getSize();
                    try {
                           ufpsMsg msg=new ufpsMsg();  
                           ret = ufpsReader.parse(msg,in);
                           ufpsFrame frame=new ufpsFrame(username,"in", msg);  
                           user.getStream().put(frame);
                    } catch (Except | InterruptedException e) {
                           logger.error("upload part ex:"+e);
                    }
                    logger.trace("upload part size:"+size+" ret:"+ret);
               }

               logger.info("webUp cmd:upload ");
               return ;

       }


}

