package org.little.db.kir;
             
import java.util.Date;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.little.util.cfg.count;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonKIR  extends common{
       private static final Logger logger = LoggerFactory.getLogger(commonKIR.class);

       private commonKIRDB   db_cfg;
       private String        def_page;
       private String        error_page;
       private String        work_path;
       private long          timeout;
       private long          start;
       private count         cnt;
       
       public commonKIR() {
              clear();
              setNodeName("little-kir");
              logger.info("create commonKIR");
       }

       @Override
       public void clear() {
              super.clear();          
              db_cfg      =new commonKIRDB();
              def_page    ="index.jsp";
              error_page  ="error.jsp";
              cnt         =new count("runtime.cnt");
              work_path   =".";
              timeout     =1000;
              start       =(new Date()).getTime();
       }
       
       public    String       getDefPage        ()       {return def_page;          }
       public    String       getErrorPage      ()       {return error_page;        }
       protected commonKIRDB  getDB             ()       {return db_cfg;            }
       public    String       getWorkPath       ()       {return work_path;         }
       public    long         getTimeout        ()       {return timeout;           }
       public    long         getStart          ()       {return cnt._get();        }
       public    void         setStart          (long c) {cnt._set(c);  }
       public    void         clearStart        ()       {cnt.unlock();      }
       

       @Override
       public void init() {
            init(this.getNode());
       }
       @Override
       public void  init(Node _node_cfg) {
              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());
                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("global_option".equalsIgnoreCase(n.getNodeName())){initGlobal(n);}
                     else
                     if("tshkbr_db"    .equalsIgnoreCase(n.getNodeName())){db_cfg.init(n);}
                 }
              }
              else{
                  logger.error("The configuration node:null");
              }                 

       }

       private void initGlobal(Node _node_cfg) {
              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());
                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("def_page"          .equalsIgnoreCase(n.getNodeName())){def_page   =n.getTextContent(); logger.info("def_page:"+def_page);}    else
                     if("error_page"        .equalsIgnoreCase(n.getNodeName())){error_page =n.getTextContent(); logger.info("error_page:"+error_page);}else
                     if("work_path"         .equalsIgnoreCase(n.getNodeName())){work_path  =n.getTextContent(); logger.info("work_dir:"+work_path);}else
                     if("timeout"           .equalsIgnoreCase(n.getNodeName())){String t   =n.getTextContent(); try {timeout=Long.getLong(t,10);}catch(Exception e) {timeout=1000;logger.error("timeout:"+t);} logger.info("timeout:"+timeout);}else
                     if("work_count"        .equalsIgnoreCase(n.getNodeName())){String t   =n.getTextContent(); logger.info("work_count:"+t);cnt.close();cnt=new count(t);cnt.open();}else
                     {}
                 }
              }
              else{
                  logger.error("The configuration node:null");
              }                 

      }


       public static void main(String args[]){
              commonKIR cfg=new commonKIR();
              String xpath  =args[0];

              if(cfg.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.KIR.WEB(cfg) "+ver());
              cfg.init();
              logger.info("RUN LITTLE.KIR.WEB(cfg) "+ver());

       }

}
