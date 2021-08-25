package org.little.stream.mngr;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.little.stream.cfg.commonStream;
import org.little.stream.http.webMngr;
import org.little.stream.ufps.ufpsMsg;
import org.little.stream.ufps.ufpsSAXParser;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Version;

public class ufpsHttpMngr {
       private static final Logger logger = LoggerFactory.getLogger(webMngr.class);
       private static commonStream cfg;
       private static ufpsManager  mngr;

       public ufpsHttpMngr() {
              logger.trace("LITTLE.STREAM("+getServletInfo()+") is create "+Version.getVer()+"("+Version.getDate()+")");
       }
       
       public void init(String xpath){

              logger.trace("start"+":"+getServletInfo());
              if(cfg==null){   
                 cfg=new commonStream();
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
              logger.info("LITTLE.STREAM("+getServletInfo()+") is run "+Version.getVer()+"("+Version.getDate()+")"+" config:"+xpath);
       }

       public String getServletInfo() {
              return "stream server";
       }

       public void destroy() {
              logger.info("LITTLE.STREAM("+getServletInfo()+") is stop "+Version.getVer()+"("+Version.getDate()+")");
              if(mngr!=null) {
                    mngr.stop(); 
                    mngr=null;  
              }
              
       }
       public int  doGetMsg(OutputStream h_out,String username,String q_name){
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
              
               logger.trace("webMngr.doGetMsg() user:"+username+ " Ok");
              
               ufpsFrame f_msg;

               f_msg=user.getQueue(q_name);
               
               if(f_msg==null){
                  mngr.runCommandGet(username,q_name);
                  f_msg=user.getQueue(q_name,cfg.getTimeoutQueue());
                  if(f_msg==null){
                     logger.trace("doGeMsg user:"+username+" no frame message");
                     return 404;            
                  }
               }
               logger.trace("doGetMsg user:"+username+" frame message OK");

               ufpsMsg msg=f_msg.getMsg();
               if(msg==null) {
                  logger.trace("doGetMsg user:"+username+" no ufps message");
                  return 404;            
               }
               logger.trace("doGetMsg user:"+username+" ufps message OK");

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

       public String getDefPage() {return cfg.getDefPage();}

}

