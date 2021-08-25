package org.little.stream.cfg;
             
import java.util.ArrayList;

import org.little.auth.authUser;
import org.little.auth.authUserXML;
import org.little.auth.commonAUTH;
import org.little.stream.mngr.ufpsRoute;
import org.little.stream.mngr.ufpsRouteTable;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonStream  extends common{
       private static final Logger logger = LoggerFactory.getLogger(commonStream.class);
       private static commonStream   cfg = new commonStream();

       private String                   def_page;
       private String                   error_page;
       private int                      max_size_local_queue;
       private int                      timeout_get_local_queue;
       private boolean                  is_auto_creat_user;
       private String                   manager_name;
       private String                   frame_node_name;
       private ArrayList<commonChannel> channels;
       private commonAUTH               cfg_auth;
       private ufpsRouteTable           route;

       public commonStream() {
              clear();
              setNodeName("littlestream");
              logger.info("create commonStream");
       }

       @Override
       public void clear() {
              super.clear();          
              def_page            ="index.jsp";
              error_page          ="error.jsp";
              max_size_local_queue=1000;
              timeout_get_local_queue=5000;
              is_auto_creat_user  =true;
              manager_name        ="no_name";
              frame_node_name     ="no_name";
              channels            =new ArrayList<commonChannel>();
              cfg_auth            =new commonAUTH();
              route               =new ufpsRouteTable();

       }
       public  static commonStream  get(){ if(cfg==null)cfg=new commonStream();return cfg;};
       
       public String                   getDefPage             () {return def_page;            }  
       public String                   getErrorPage           () {return error_page;          }
       public int                      getMaxSizeQueue        () {return max_size_local_queue;}
       public int                      getTimeoutQueue        () {return timeout_get_local_queue;}
       public authUser                 getAuth                () {return cfg_auth.getAuthUser();}
       public int                      getTypeAuthenticateUser(){return cfg_auth.getTypeAuthenticateUser();}
       public ArrayList<commonChannel> getChannels            (){return channels;}
       public boolean                  isAutoCreatUser        (){return is_auto_creat_user;}
       public ufpsRouteTable           getRoute               (){return route;}

       public String                   getManagerName         () {return manager_name;     }  
       public String                   getFrameNodeName       () {return frame_node_name;        }  

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
                     if("global_option".equalsIgnoreCase(n.getNodeName())){initGlobal(n);}else
                     if("channels"     .equalsIgnoreCase(n.getNodeName())){initChannels(n);}else
                     if("user"         .equalsIgnoreCase(n.getNodeName())){initUser(n);}else
                     if("route"         .equalsIgnoreCase(n.getNodeName())){initRoute(n);}else
                     {}
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
                     if("def_page"      .equalsIgnoreCase(n.getNodeName())){def_page    =n.getTextContent(); logger.info("def_page:"+def_page);    }else
                     if("error_page"    .equalsIgnoreCase(n.getNodeName())){error_page  =n.getTextContent(); logger.info("error_page:"+error_page);}else
                     if("max_size_queue".equalsIgnoreCase(n.getNodeName())){String s    =n.getTextContent(); try{max_size_local_queue=Integer.parseInt(s, 10);}catch(Exception e){max_size_local_queue=1000;logger.error("max_size_queue:"+s);}logger.info("max_size_queue:"+max_size_local_queue);}else
                     if("timeout_queue".equalsIgnoreCase(n.getNodeName())){String s    =n.getTextContent();  try{timeout_get_local_queue=Integer.parseInt(s, 10);}catch(Exception e){timeout_get_local_queue=5000;logger.error("timeout_queue:"+s);}logger.info("timeout_queue:"+timeout_get_local_queue);}else
                     if("manager"       .equalsIgnoreCase(n.getNodeName())){manager_name=n.getTextContent(); logger.info("manager:"+manager_name); }else
                     if("node"          .equalsIgnoreCase(n.getNodeName())){frame_node_name=n.getTextContent(); logger.info("node:"+frame_node_name);       }else
                     {}
                 }
                 cfg_auth.init(glist);
              }
              else{
                  logger.error("The configuration node:null");
              }                 

       }
       private void initChannels(Node _node_cfg) {
               int max_size_queue=getMaxSizeQueue();
               if(_node_cfg!=null){
                  logger.info("The configuration node:"+_node_cfg.getNodeName());
                  if(_node_cfg.getAttributes().getNamedItem("max_size_queue")!=null) {
                     String _max_size_queue=_node_cfg.getAttributes().getNamedItem("max_size_queue").getNodeValue();
                     try{max_size_queue=Integer.parseInt(_max_size_queue, 10);}catch(Exception e){ max_size_queue=getMaxSizeQueue   ();logger.error("max_size_queue:"+_max_size_queue); }
                     logger.info("max_size_queue:"+max_size_queue);
                  }
                  NodeList glist=_node_cfg.getChildNodes();     
                  for(int i=0;i<glist.getLength();i++){
                      Node n=glist.item(i);
                      if("channel".equalsIgnoreCase(n.getNodeName())){commonChannel ch=new commonChannel(); ch.init(n,max_size_queue);channels.add(ch);}else
                      {}
                  }
               }
               else{
                   logger.error("The configuration node:null");
               }                 
               logger.trace("all channels:"+channels.size());
       }
       private void initUser(Node _node_cfg) {
               if(getAuth() instanceof authUserXML ) {
                  ((authUserXML)getAuth()).init(_node_cfg); 
               }
       }
       private void initRoute(Node _node_cfg) {
               //int max_size_queue=getMaxSizeQueue();
               if(_node_cfg!=null){
                  logger.info("The configuration node:"+_node_cfg.getNodeName());
                  NodeList glist=_node_cfg.getChildNodes();     
                  for(int i=0;i<glist.getLength();i++){
                      Node n=glist.item(i);
                      if("p".equalsIgnoreCase(n.getNodeName())){ufpsRoute r=new ufpsRoute(); r.init(n);route.add(r);}else
                      {}
                  }
               }
               else{
                   logger.error("The configuration node:null");
               }                 
      
       }


       public static void main(String args[]){
              commonStream cfg=new commonStream();
              String xpath  =args[0];

              if(commonStream.get().loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.STREAM "+ver());
              cfg.init();
              logger.info("RUN LITTLE.STREAM "+ver());

       }

}
