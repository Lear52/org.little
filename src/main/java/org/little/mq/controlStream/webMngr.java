package org.little.mq.controlStream;

import java.io.IOException;
import java.util.ArrayList;

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
import org.little.util.run.task;
import org.little.web.webThread;
/**
 * @author av
 *  
 */
public class webMngr extends webThread{
       //final   private static int    CLASS_ID  =605;
       //public  static int    getClassId(){return CLASS_ID;}
       //private static final long serialVersionUID = 19690401L+CLASS_ID;
       private static final long serialVersionUID = 2151685431200835242L;
       private static final Logger logger = LoggerFactory.getLogger(webMngr.class);

       final public  static String url_cmd_base  ="/cmd";  
       final public  static String url_i_get_stat="/internal/state";  
       final public  static String url_i_set_flag="/internal/cntrl";  
       final public  static String url_i_set_flag_all="/internal/allcntrl";  
       final public  static String url_i_exec_clr="/internal/clr";    
       final public  static String url_i_get_list="/internal/list";     
       final public  static String url_i_exec_chl="/internal/chl";      
       final public  static String url_i_exec_chl_all="/internal/allchl";      
                       
       //final public  static String url_p_get_stat="/public/state";  
       final public  static String url_p_set_flag="/public/cntrl";    
       final public  static String url_p_set_flag_all="/public/allcntrl";    
       final public  static String url_p_exec_clr="/public/clr";      
       final public  static String url_p_get_list="/public/list";     
       final public  static String url_p_exec_chl="/public/chl";      
       final public  static String url_p_exec_chl_all="/public/allchl";      

       private fc_mngr mngr;

       @Override
       public void init() throws ServletException {

              logger.trace("start servlet:"+getServletInfo());

              mngr=new fc_mngr();

              String xpath=this.getServletContext().getRealPath("")+getParametr("config");
              
              if(mngr.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.CONTROLSTREAM "+Version.getVer()+"("+Version.getDate()+")");

              mngr.init();
              mngr.setDelay(1);

              ArrayList<task> _task=mngr.getListTask();
              runner.add(mngr);
              for(int i=0;i<_task.size();i++){
                  task t=_task.get(i);
                  t.setDelay(mngr.getTimeout());
                  runner.add(t);
              }

              logger.info("RUN LITTLE.CONTROLSTREAM "+Version.getVer()+"("+Version.getDate()+")");

              //-------------------------------------------------------------------------------------------------------
              super.init();
              //-------------------------------------------------------------------------------------------------------
              logger.trace("run:"+getServletInfo());
       }
       @Override
       public void destroy() {
              mngr.KILL();
              ArrayList<task> _task=mngr.getListTask();
              runner.add(mngr);
              for(int i=0;i<_task.size();i++){
                  task t=_task.get(i);
                  t.KILL();
                  runner.del(t);
              }
              _task.clear();

              runner.stop();
              runner=null;
              mngr.close();
              mngr=null;
              super.destroy();
              logger.info("STOP LITTLE.CONTROLSTREAM "+Version.getVer()+"("+Version.getDate()+")");
       }

       @Override
       public String getServletInfo() {
              return "Show state queue";
       }
       /**
          processing JSON request: get all state group 
        */
       private void doGetList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");

               JSONObject  root=mngr.getStatAll();
               logger.trace("mngr.getStatAll() :"+root);
               root.write(response.getWriter());

               logger.trace("webMngr.doGetList()");
       }
       /**
       processing JSON request: get state local group 
       */
       private void doGetState(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");

               JSONObject  root=mngr.getLocalStat();
               logger.trace("mngr.getGetStat() :"+root);
               root.write(response.getWriter());

               logger.trace("webMngr.doGetState()");
       }
       /**
         processing JSON request: set control mq channel
       */
       private void doSetChannel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");

               String node_id  =(String) request.getParameter("node");
               String group_id =(String) request.getParameter("group");
               String flow_id  =(String) request.getParameter("flow");
               String _is_run =(String) request.getParameter("state");
               boolean is_run =false;
               try{ is_run=Boolean.parseBoolean(_is_run);}catch(Exception e){is_run=false;}

               logger.trace("mngr.doSetChannel(node:"+node_id+",group:"+group_id+",flow:"+flow_id+",flag:"+is_run+")");

               JSONObject  root=mngr.setChannel(node_id,group_id,flow_id,is_run);

               logger.trace("mngr.SetChannel(group:"+group_id+",flow:"+flow_id+",flag:"+is_run+") return:"+root);

               root.write(response.getWriter());

               logger.trace("webMngr.doSetChannel()");
       }
       private void doSetChannelAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");

               String node_id  =(String) request.getParameter("node");
               String _is_run =(String) request.getParameter("state");
               boolean is_run =false;
               try{ is_run=Boolean.parseBoolean(_is_run);}catch(Exception e){is_run=false;}

               logger.trace("mngr.doSetChannelAll(node:"+node_id+",flag:"+is_run+")");

               JSONObject  root=mngr.setChannelAll(node_id,is_run);

               logger.trace("mngr.doSetChannelAll(node:"+node_id+",flag:"+is_run+") return:"+root);

               root.write(response.getWriter());

               logger.trace("webMngr.doSetChannelAll()");
       }

       /**
         processing JSON request: set control flag  
       */
       private void doSetFlag(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");

               String node_id  =(String) request.getParameter("node");
               String group_id =(String) request.getParameter("group");
               String flow_id  =(String) request.getParameter("flow");
               String _is_flag =(String) request.getParameter("state");
               boolean is_flag =false;
               try{ is_flag=Boolean.parseBoolean(_is_flag);}catch(Exception e){is_flag=false;}

               logger.trace("mngr.SetFlag(node:"+node_id+",group:"+group_id+",flow:"+flow_id+",flag:"+is_flag+")");

               JSONObject  root=mngr.setFlag(node_id,group_id,flow_id,is_flag);

               logger.trace("mngr.SetFlag(node:"+node_id+",group:"+group_id+",flow:"+flow_id+",flag:"+is_flag+") return:"+root);

               root.write(response.getWriter());

               logger.trace("webMngr.doSetFlag()");
       }
       private void doSetFlagAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");

               String node_id  =(String) request.getParameter("node");
               String _is_flag =(String) request.getParameter("state");
               boolean is_flag =false;
               try{ is_flag=Boolean.parseBoolean(_is_flag);}catch(Exception e){is_flag=false;}

               logger.trace("mngr.SetFlagAll(node:"+node_id+",flag:"+is_flag+")");

               JSONObject  root=mngr.setFlagAll(node_id,is_flag);

               logger.trace("mngr.SetFlag(node:"+node_id+",flag:"+is_flag+") return:"+root);

               root.write(response.getWriter());

               logger.trace("webMngr.doSetFlagAll()");
       }
       private void doClearQ(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
               response.setContentType("application/json");
               response.setContentType("text/html; charset=UTF-8");

               String node_id =(String) request.getParameter("node");
               String group_id=(String) request.getParameter("group");
               String flow_id =(String) request.getParameter("flow");
               String mngr_id =(String) request.getParameter("mngr");
               String q_id    =(String) request.getParameter("q");

               logger.trace("mngr.ClearQ(node:"+node_id+",group:"+group_id+",flow:"+flow_id+",mngr:"+mngr_id+",q:"+q_id+")");

               JSONObject  root=mngr.ClearQ(node_id,group_id,flow_id,mngr_id,q_id);

               logger.trace("mngr.ClearQ(node:"+node_id+",group:"+group_id+",flow:"+flow_id+",mngr:"+mngr_id+",q:"+q_id+") return:"+root);

               mngr.work();/**/

               root.write(response.getWriter());

               logger.trace("mngr.ClearQ()");
       }

       @Override
       public void doRun(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

              String    path = (String) request.getPathInfo();
              String    page=null;
              
              logger.trace("webAddr.doRun() path:"+path);
                
              /**
                 JSON request get all group 
               */
              if(path.startsWith(url_i_get_list)||path.startsWith(url_p_get_list)){
                 doGetList(request,response);
                 return;
              }
              /**
                 JSON request get local group from remote mngr
               */
              else 
              if(path.startsWith(url_i_get_stat)){
                 doGetState(request,response);
                 return;
              }  
              /**
                 JSON request set control flag
               */
              else 
              if(path.startsWith(url_i_set_flag)||path.startsWith(url_p_set_flag)){
                 doSetFlag(request,response);
                 return;
              }  
              else 
              if(path.startsWith(url_i_set_flag_all)||path.startsWith(url_p_set_flag_all)){
                 doSetFlagAll(request,response);
                 return;
              }  
              /**
                 JSON request set control channel
               */
              else 
              if(path.startsWith(url_i_exec_chl)||path.startsWith(url_p_exec_chl)){
                 doSetChannel(request,response);
                 return;
              }  
              else 
              if(path.startsWith(url_i_exec_chl_all)||path.startsWith(url_p_exec_chl_all)){
                 doSetChannelAll(request,response);
                 return;
              }  
              /**
                 JSON request set control flag
               */
              else 
              if(path.startsWith(url_i_exec_clr)||path.startsWith(url_p_exec_clr)){
                 doClearQ(request,response);
                 return;
              }  
              else{
                 page =mngr.getDefaulPage();
              }

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

