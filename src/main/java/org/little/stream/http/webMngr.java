package org.little.stream.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.little.stream.mngr.ufpsHttpMngr;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.web.webRun;
/**
 * @author av
 *  
 */
@WebServlet("/stream/")
@MultipartConfig(fileSizeThreshold=1024*1024*1,// 1MB 
                 maxFileSize=1024*1024*10,       // 10MB
               maxRequestSize=1024*1024*10)       // 10MB
public class webMngr extends webRun{
       private static final long serialVersionUID = -3761343738105949425L;
       private static final Logger logger = LoggerFactory.getLogger(webMngr.class);
       private static ufpsHttpMngr  mngr;
       private static String xpath="";;

       public webMngr() {}
       
       @Override
       public void init() throws ServletException {

              logger.trace("start"+":"+getServletInfo());
              if(mngr==null){   
                 mngr=new ufpsHttpMngr();
                 xpath=this.getServletContext().getRealPath("");
                 String _xpath=getParametr("config");
                 xpath+=_xpath;
                 mngr.init(xpath);
              }
              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
       }

       @Override
       public String getServletInfo() {return "stream web server";}

       @Override
       public void destroy() {
              mngr.destroy();              
              super.destroy();
       }
       private void doGetMsg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               String       path   = (String) request.getPathInfo();
               String       q_name = (String) request.getParameter("q");
               OutputStream h_out  = null;

               logger.trace("begin doGetMsg path:"+path);
               try {
                   request.setCharacterEncoding("UTF-8");
                   response.setCharacterEncoding("UTF-8");
                   response.setContentType("application/xml");
                   h_out=response.getOutputStream();
               } catch (UnsupportedEncodingException e1) {
                    logger.error("setCharacterEncoding "+new Except("ex:",e1));
                    response.setStatus(500);
                    logger.trace("end doGetMsg");
                    return;
               }

               Principal p_user = request.getUserPrincipal();
               if(p_user==null) {
                  logger.trace("webMngr.doGetMsg() path:"+path+" no principal status:401");
                  response.setStatus(401);
                  logger.trace("end doGetMsg");
                  return;            
               }
               int ret=mngr.doGetMsg(h_out,p_user.getName(),q_name);
               response.setStatus(ret);
              
               logger.trace("end doGetMsg");
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
  
              if(page==null)page = mngr.getDefPage();
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
              String path   = (String) request.getPathInfo();
              String q_name = (String) request.getParameter("q");
              logger.trace("begin webMngr.doPost() path:"+path);

              try {
                  request.setCharacterEncoding("UTF-8");
                  response.setCharacterEncoding("UTF-8");
              } catch (UnsupportedEncodingException e1) {
                   logger.error("setCharacterEncoding "+new Except("ex:",e1));
                   return;
              }

              Principal p_user = request.getUserPrincipal();
              if(p_user==null) {
                 response.setStatus(401);
                 logger.trace("webMngr.doPost() path:"+path+" no principal status:401");
                 return;            
              }

              int ret=404;
              String username = p_user.getName();

              if(path.startsWith("/post")){
                 for (Part part : request.getParts()) {
                      //int         ret=0;
                      InputStream in = part.getInputStream();
                      int         size=(int)part.getSize();
                      ret=mngr.doPostMsg(username,q_name,in,size);
                 }
              }

              response.setStatus(ret);
              logger.trace("end webMngr.doPost() path:"+path);
              return ;

       }
}

