package org.little.stream.test;

import java.io.ByteArrayOutputStream;
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
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.little.stream.cfg.commonStream;
import org.little.stream.mngr.ufpsFrame;
import org.little.stream.mngr.ufpsManager;
import org.little.stream.mngr.ufpsUser;
import org.little.stream.ufps.ufpsMsg;
import org.little.stream.ufps.ufpsSAXParser;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Version;
import org.little.web.webRun;
/**
 * @author av
 *  
 */
//@WebServlet("/stream/put")
@MultipartConfig(fileSizeThreshold=1024*1024*1,// 1MB 
                 maxFileSize=1024*1024*10,       // 10MB
               maxRequestSize=1024*1024*10)       // 10MB
public class webMngr2 extends webRun{
       private static final long serialVersionUID = -3761343738105949425L;
       private static final Logger logger = LoggerFactory.getLogger(webMngr2.class);
       private static commonStream cfg;
       private static ufpsManager  mngr;
       private static String xpath="";;

       public webMngr2() {
              logger.trace("LITTLE.STREAM("+getServletInfo()+") is create "+Version.getVer()+"("+Version.getDate()+")");
       }
       
       @Override
       public void init() throws ServletException {

              logger.trace("start"+":"+getServletInfo());
              if(cfg==null){   
                 cfg=new commonStream();
                 xpath=this.getServletContext().getRealPath("");
                 String _xpath=getParametr("config");
                 xpath+=_xpath;
                 if(cfg.loadCFG(xpath)==false){
                    logger.error("error read config file:"+xpath);
                    return;
                 }
                 cfg.init();
              }
              if(mngr==null) {              
                 mngr=new ufpsManager(cfg);
                 mngr.fork();
              }
              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.info("LITTLE.STREAM("+getServletInfo()+") is run "+Version.getVer()+"("+Version.getDate()+")"+" config:"+xpath);
       }

       @Override
       public String getServletInfo() {
              return "stream server";
       }
       @Override
       public void destroy() {
              super.destroy();
              logger.info("LITTLE.STREAM("+getServletInfo()+") is stop "+Version.getVer()+"("+Version.getDate()+")"+" config:"+xpath);
              if(mngr!=null) {
                 mngr.stop(); 
                 mngr=null;  
              }
              
       }
       private int  doGetMsg(OutputStream h_out,String username,String q_name){
               if(username==null) {
                  logger.trace("webMngr.doGetMsg()  no user status:401");
                  return 401;            
               }
               logger.trace("webMngr.doGetMsg() check user:"+username);

               ufpsUser user = mngr.getUser(username);
               if(user==null) {
                  logger.trace("webMngr.doGetMsg()  no user:"+username+" in manager status:401");
                  return 401;            
               }
              
               logger.trace("webMngr.doGetMsg()  user:"+username+ " Ok");
              
               ufpsFrame f_msg=null;
               if(q_name==null)f_msg=user.getQueue();else f_msg=user.getQueue(q_name);
               
               if(f_msg==null) {
                  logger.trace("doGeMsg user:"+username+" no frame message");
                  return 404;            
               }

               ufpsMsg msg=f_msg.getMsg();
               if(msg==null) {
                  logger.trace("doGetMsg user:"+username+" no ufps message");
                  return 404;            
               }
               if(msg.getBuf()==null){
                  logger.trace("doGetMsg user:"+username+" no buffer message");
                  return 404;            
               }
              
               //response.setContentType("application/xml");
               try {
                    h_out.write(msg.getBuf());
               } catch (Exception e1) {
                    logger.error("setCharacterEncoding "+new Except("ex:",e1));
                    return 500;
               }
              
               return 202;            


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
               String username = p_user.getName();
               int ret=doGetMsg(h_out,username,q_name);
               response.setStatus(ret);
              
               logger.trace("end doGetMsg");
       }
       /*
       private void doGetMsg1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               String    path = (String) request.getPathInfo();
               String    q_name = (String) request.getParameter("q");
               
               logger.trace("begin doGetMsg path:"+path);
               try {
                   request.setCharacterEncoding("UTF-8");
                   response.setCharacterEncoding("UTF-8");
               } catch (UnsupportedEncodingException e1) {
                    logger.error("setCharacterEncoding "+new Except("ex:",e1));
                    logger.trace("end doGetMsg");
                    return;
               }

               Principal p_user = request.getUserPrincipal();
               if(p_user==null) {
                  response.setStatus(401);
                  logger.trace("webMngr.doGetMsg() path:"+path+" no principal status:401");
                  logger.trace("end doGetMsg");
                  return;            
               }
               String username = p_user.getName();
               if(username==null) {
                  response.setStatus(401);
                  logger.trace("webMngr.doGetMsg() path:"+path+" no user status:401");
                  logger.trace("end doGetMsg");
                  return;            
               }

               logger.trace("webMngr.doGetMsg() path:"+path+" check user:"+username);

               ufpsUser user = mngr.getUser(username);
               if(user==null) {
                  response.setStatus(401);
                  logger.trace("webMngr.doGetMsg() path:"+path+" no user:"+username+" in manager status:401");
                  logger.trace("end doGetMsg");
                  return;            
               }
              
               logger.trace("webMngr.doGetMsg() path:"+path+" user:"+username);
              
               ufpsFrame f_msg=null;
               if(q_name==null)f_msg=user.getQueue();
               else            f_msg=user.getQueue(q_name);
               
               if(f_msg==null) {
                  logger.trace("doGeMsg user:"+username+" no frame message");
                  response.setStatus(404);
                  logger.trace("end doGetMsg");
                  return;            
               }

               ufpsMsg msg=f_msg.getMsg();
               if(msg==null) {
                  logger.trace("doGetMsg user:"+username+" no ufps message");
                  response.setStatus(404);
                  logger.trace("end doGetMsg");
                  return;            
               }
               if(msg.getBuf()==null){
                  logger.trace("doGetMsg user:"+username+" no buffer message");
                  response.setStatus(404);
                  logger.trace("end doGetMsg");
                  return;            
               }
              
               response.setContentType("application/xml");
               OutputStream h_out=response.getOutputStream();
               h_out.write(msg.getBuf());
              
               logger.trace("end doGetMsg");
       }
       */
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

       public int doPostMsg(String username,String q_name,InputStream in,int size) {
              if(username==null) {
                 logger.trace("webMngr.doPostMsg() no user status:401");
                 return 401;            
              }
              logger.trace("webMngr.doPostMsg() check user:"+username);
              ufpsUser user = mngr.getUser(username);
              if(user==null) {
                 logger.trace("webMngr.doPostMsg() no user:"+username+" in manager status:401");
                 return 401;            
              }

              logger.trace("webMngr.doPostMsg()  user:"+username+ " Ok");
              boolean ret=false;
              /*
               * parse byte buffer and get ufpsMsg 
               */
              ByteArrayOutputStream _out  = new ByteArrayOutputStream();
              byte[] buf = new byte[512];
              try {
                  int howmany;
                  while ((howmany = in.read(buf)) > 0) {_out.write(buf, 0, howmany);}
                  in.close();
                  _out.flush();
              } 
              catch (Exception ex) {
                    logger.error("read post buffer status:500  "+new Except("ex:",ex));
                    return 500;
              }

              buf = _out.toByteArray();
              ufpsMsg msg=new ufpsMsg();  
              ufpsSAXParser sax=new ufpsSAXParser(msg,buf);
              msg=sax.parse();

              /*
               * convert  ufpsMsg to ufpsFrame 
               * route    ufpsFrame
               * send to queue 
               */

              ret=mngr.route(msg);
              logger.trace("upload part size:"+size+" msg:"+msg.print());

              if(ret)return 202;
              else   return 507;
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
              String username = p_user.getName();
              int ret=404;
              if(path.startsWith("/post")){
                 for (Part part : request.getParts()) {
                      //int         ret=0;
                      InputStream in = part.getInputStream();
                      int         size=(int)part.getSize();
                      ret=doPostMsg(username,q_name,in,size);
                 }
              }

              response.setStatus(ret);
              logger.trace("end webMngr.doPost() path:"+path);
              return ;

       }
/*
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
              String username = p_user.getName();
              if(username==null) {
                 response.setStatus(401);
                 logger.trace("webMngr.doPost() path:"+path+" no user status:401");
                 return;            
              }

              logger.trace("webMngr.doPost() path:"+path+" check user:"+username);
              ufpsUser user = mngr.getUser(username);
              if(user==null) {
                 response.setStatus(401);
                 logger.trace("webMngr.doPost() path:"+path+" no user:"+username+" in manager status:401");
                 return;            
              }

              logger.trace("webMngr.doPost() path:"+path+" user:"+username);
              boolean ret=false;
              if(path.startsWith("/post")){
                 for (Part part : request.getParts()) {
                      //int         ret=0;
                      InputStream in = part.getInputStream();
                      int         size=(int)part.getSize();
                       // parse byte buffer and get ufpsMsg 
                       
                      //try {
                      ByteArrayOutputStream _out  = new ByteArrayOutputStream();

                      byte[] buf = new byte[512];
                      int howmany;
                      while ((howmany = in.read(buf)) > 0) {_out.write(buf, 0, howmany);}
                      in.close();
                      _out.flush();

                      buf = _out.toByteArray();
                      //logger.trace("upload msg:"+new String(buf));
                      ufpsMsg msg=new ufpsMsg();  
                      ufpsSAXParser sax=new ufpsSAXParser(msg,buf);
                      msg=sax.parse();
                      
                       //* convert  ufpsMsg to ufpsFrame 
                       //* route    ufpsFrame
                       //* send to queue 
                       
                      ret=mngr.route(msg);
                      logger.trace("upload part size:"+size+" msg:"+msg.print());
                 }
              }

              if(ret)response.setStatus(202);
              else   response.setStatus(507);
              logger.trace("end webMngr.doPost() path:"+path);
              return ;

       }

*/

}

