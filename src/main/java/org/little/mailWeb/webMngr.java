package org.little.mailWeb;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.little.lmsg.lMessage;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Version;
import org.little.web.webRun;
/**
 * @author av
 *  
 */
public class webMngr extends webRun{
       private static final Logger logger = LoggerFactory.getLogger(webMngr.class);
       private static final long serialVersionUID = -3616757490430537836L;
       private commonArh    cfg;
       //private ImapView client;

       public webMngr(){
              cfg = new commonArh();
              logger.info("create webMngr");
       } 

       @Override
       public void init() throws ServletException {

              if(cfg==null)return;

              logger.trace("start"+":"+getServletInfo());
              String xpath =this.getServletContext().getRealPath("");
              String _xpath=getParametr("config");
              xpath+=_xpath;

              boolean ret=cfg.loadCFG(xpath);
              cfg.init();
              if(ret==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }

              logger.info("START LITTLE.IMAPWEB(VIEW) config:"+xpath+" "+Version.getVer()+"("+Version.getDate()+")");
              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
        }
        @Override
        public void destroy() {
               if(cfg!=null)cfg.clear();
               cfg=null;
               super.destroy();
               //client=null;
               logger.info("STOP LITTLE.IMAPWEB(VIEW) "+Version.getVer()+"("+Version.getDate()+")");
        }

       @Override
       public String getServletInfo() {
              return "Show key data";
       }
       private void doGetFileID(HttpServletRequest request, HttpServletResponse response,int _uid) throws ServletException, IOException{
               logger.trace("begin doGetFileID:"+_uid);
              
               lMessage  msg     =cfg.getFolder().loadArray(_uid);
               byte []   buf     =null;
               int       buf_size=0;
               if(msg!=null){
                  buf=msg.getBodyBin();
                  buf_size=buf.length;
                  logger.trace("load buf:"+buf.length);
               }

               String filename=msg.getFilename();
               if(filename==null)filename="cert_"+_uid+".cer";
               if(filename.equals(""))filename="cert_"+_uid+".cer";

               response.setContentType("application/octet-stream");
               response.addHeader("Accept-Ranges","bytes");
               response.setHeader("Content-Type","application/octet-stream");
               response.setHeader("Content-Transfer-Encoding", "Binary");
               response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
               response.setContentLength(buf_size);
               logger.trace("set header");
              
               response.getOutputStream().write(buf,0,buf_size);
               logger.trace("write buf");
               response.getOutputStream().flush();;
              
               logger.trace("end doGetFileID:"+_uid+" filename="+filename);
       }
       private void doGetSRL(HttpServletRequest request, HttpServletResponse response,int _uid) throws ServletException, IOException{
               logger.trace("begin doGetSRLID:"+_uid);
              
               lMessage  msg     =cfg.getFolder().loadSRL(_uid);
               byte []   buf     =null;
               int       buf_size=0;
               if(msg!=null){
                  buf=msg.getBodyBin();
                  buf_size=buf.length;
                  logger.trace("load buf:"+buf.length);
               }

               String filename=msg.getFilename();
               if(filename==null)filename="cert_"+_uid+".cer";
               if(filename.equals(""))filename="cert_"+_uid+".cer";

               response.setContentType("application/octet-stream");
               response.addHeader("Accept-Ranges","bytes");
               response.setHeader("Content-Type","application/octet-stream");
               response.setHeader("Content-Transfer-Encoding", "Binary");
               response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
               response.setContentLength(buf_size);
               logger.trace("set header");
              
               response.getOutputStream().write(buf,0,buf_size);
               logger.trace("write buf");
               response.getOutputStream().flush();;
              
               logger.trace("end doGetSRLID:"+_uid+" filename="+filename);
       }
       private void doGetX509ID(HttpServletRequest request, HttpServletResponse response,int _x509_id) throws ServletException, IOException{
               logger.trace("begin doGetX509ID:"+_x509_id);
              
               lMessage  msg     =cfg.getFolder().loadArrayX509(_x509_id);
               byte []   buf     =null;
               int       buf_size=0;
               if(msg!=null){
                  buf=msg.getBodyBin();
                  buf_size=buf.length;
                  logger.trace("load buf:"+buf.length);
               }
               String filename=msg.getFilename();
               if(filename==null)filename="cert_"+_x509_id+".cer";
               if(filename.equals(""))filename="cert_"+_x509_id+".cer";

               response.setContentType("application/octet-stream");
               response.addHeader("Accept-Ranges","bytes");
               response.setHeader("Content-Type","application/octet-stream");
               response.setHeader("Content-Transfer-Encoding", "Binary");
               response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
               response.setContentLength(buf_size);
               logger.trace("set header");
              
               response.getOutputStream().write(buf,0,buf_size);
               logger.trace("write buf");
               response.getOutputStream().flush();;
              
               logger.trace("end doGetX509ID:"+_x509_id+" filename="+filename);
       }
       private void doGetList(HttpServletRequest request, HttpServletResponse response,String _type) throws ServletException, IOException{
               String prn_type;
               if(_type==null)prn_type="is null";else prn_type=_type;

               logger.trace("begin doGetList type:"+prn_type);
               
               JSONObject  root=cfg.getFolder().loadJSON(_type);
               
               logger.trace("getStatAll() :"+root);
               
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");
               
               root.write(response.getWriter());
               
               logger.trace("end doGetList type:"+prn_type);
       }
       private void doGetX509(HttpServletRequest request, HttpServletResponse response,String _type) throws ServletException, IOException{
               String prn_type;
               if(_type==null)prn_type="is null";else prn_type=_type;

               logger.trace("begin doGetX509 type:"+prn_type);
              
               JSONObject  root=cfg.getFolder().loadJSONX509(_type);
              
               logger.trace("getStatAll() :"+root);
              
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");
              
               root.write(response.getWriter());
              
               logger.trace("end doGetX509 type:"+prn_type);
       }
       private void doGetAlarm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

           logger.trace("begin doGetAlarm");
           
           JSONObject  root=cfg.getFolder().loadJSONAlarm(cfg.getAlarm().getTimeAlarm(),new Timestamp(new Date().getTime()));
          
           logger.trace("getStatAll() :"+root);
          
           response.setContentType("application/json");
           response.setContentType("text/html; charset=UTF-8");
          
           root.write(response.getWriter());
          
           logger.trace("end doGetAlarm");
       }

       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              if(cfg==null)return;

              String    path = (String) request.getPathInfo();
              String    page=null;

              logger.trace("webAddr.doRun() path:"+path);

              if(path.startsWith("/list")){
                  String    _type = (String) request.getParameter("type");
                  doGetList(request,response,_type);
                  return;
              }
              else
              if(path.startsWith("/x509")){
                  String    _type = (String) request.getParameter("type");
                  doGetX509(request,response,_type);
                  return;
              }
              else
              if(path.startsWith("/alarm")){
                  doGetAlarm(request,response);
                  return;
              }
              else
              if(path.startsWith("/srl")){
                  String    _id = (String) request.getParameter("uid");
                  int id    =-1;
                  if(_id    !=null)try {id    =Integer.parseInt(_id, 10);    } catch(Exception e) {id=-1;}
                  doGetSRL(request,response,id);
                  return;
              }
              else
              if(path.startsWith("/get")){
                  String    _uid     = (String) request.getParameter("uid");
                  String    _x509_id = (String) request.getParameter("x509_id");
                  int uid    =-1;
                  int x509_id=-1;
                  if(_uid    !=null)try {uid    =Integer.parseInt(_uid, 10);    } catch(Exception e) {uid=-1;}
                  if(_x509_id!=null)try {x509_id=Integer.parseInt(_x509_id, 10);} catch(Exception e) {x509_id=-1;}

                  if(uid>0 || x509_id>0) {
                     if(x509_id>0)doGetX509ID(request,response,x509_id);
                     else         doGetFileID(request,response,uid);
                     return;
                  }
                  logger.trace("error request uid:"+_uid);
                  page =cfg.getErrorPage();;
              }
  
              if(page==null)page = cfg.getDefPage();

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

}

