package org.little.db.kir;

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
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Version;
import org.little.web.webRun;
/**
 * @author av
 *  
 */
public class webMngr extends webRun{
      private static final long serialVersionUID = -3163620482303427340L;
      private static final Logger logger = LoggerFactory.getLogger(webMngr.class);
        private commonKIR    cfg;
        private listKIR      list;
        private arhKIR       arh;
        private loadKIR      load;

       public webMngr(){
              cfg = new commonKIR();
              list=null;
              arh =null;
              load=null;
              logger.info("create webMngr");
       } 

       @Override
       public void init() throws ServletException {

              if(cfg==null)return;

              logger.trace("start"+":"+getServletInfo());
              String xpath =this.getServletContext().getRealPath("");
              String _xpath=getParametr("config");
              _xpath=xpath+_xpath;

              boolean ret=cfg.loadCFG(_xpath);
              cfg.init();
              if(ret==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              arh=new arhKIR(xpath+cfg.getWorkPath());
              list=new listKIR(xpath+cfg.getWorkPath());
              load=new loadKIR(cfg,list);
              load.open();

              logger.info("START LITTLE.KIR.WEB(VIEW) config:"+xpath+" "+Version.getVer()+"("+Version.getDate()+")");
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
               logger.info("STOP LITTLE.KIR.WEB(VIEW) "+Version.getVer()+"("+Version.getDate()+")");
        }

       @Override
       public String getServletInfo() {
              return "Show KIR data";
       }
       private void doGetList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               logger.trace("begin doGetList:");
              
              
               logger.trace("end doGetList");
       }
       private void doGetStat(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               logger.trace("begin doGetStat:");
              
              
               logger.trace("end doGetStat");
       }
       private void doGetArh(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               logger.trace("begin doGetZip:");
              
               int       buf_size=-1;

               String filename="kir_data.zip";

               response.setContentType("application/octet-stream");
               response.addHeader("Accept-Ranges","bytes");
               response.setHeader("Content-Type","application/octet-stream");
               response.setHeader("Content-Transfer-Encoding", "Binary");
               response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
               response.setContentLength(buf_size);
               logger.trace("set header");
               arh.zipFlush(response.getOutputStream(),filename);

               response.getOutputStream().flush();
              
               logger.trace("end doGetZip filename="+filename);
       }
       private void doGetZip(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               logger.trace("begin doGetZip:");
              
               int       buf_size=-1;

               String filename="kir_data.zip";

               response.setContentType("application/octet-stream");
               response.addHeader("Accept-Ranges","bytes");
               response.setHeader("Content-Type","application/octet-stream");
               response.setHeader("Content-Transfer-Encoding", "Binary");
               response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
               response.setContentLength(buf_size);
               logger.trace("set header");
               list.zipFlush(response.getOutputStream());

               response.getOutputStream().flush();;
              
               logger.trace("end doGetZip filename="+filename);
       }

       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              if(cfg==null)return;

              String    path = (String) request.getPathInfo();
              String    page=null;

              logger.trace("webAddr.doRun() path:"+path);

              if(path.startsWith("/stat")){
                 doGetStat(request,response);
                 return;
              }
              else
              if(path.startsWith("/list")){
                 doGetStat(request,response);
                 return;
              }
              else
              if(path.startsWith("/arh")){
                 doGetArh(request,response);
                 return;
              }
              else
              if(path.startsWith("/load")){
                 doGetZip(request,response);
                 return;
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

