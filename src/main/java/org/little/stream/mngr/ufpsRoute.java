package org.little.stream.mngr;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ufpsRoute {
       private static final Logger logger = LoggerFactory.getLogger(ufpsRoute.class);
          
       private String  address;
       private String  route_user;
       private String  route_queue;
       private boolean is_route_local;
       
       
       public ufpsRoute() {
              clear();
       }
       
       public ufpsRoute(String _address,String _route_user,boolean _is_route_local) {
              clear();
              address       =_address;
              route_user    =_route_user;
              is_route_local=_is_route_local;
       }
       
       public void clear() {
              address="*";
              route_user="no_name";
              route_queue="*";
              is_route_local=true;
       }

       public String getAddress() {return address;}
       public String getRouteUser() {return route_user;}
       //public String getRouteStream() {return route_stream;}
       public String getRouteQueue() {return route_queue;}
       public boolean islocal() {return is_route_local;}
          
       public void   init(Node _node_cfg){
              if(_node_cfg!=null){
                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("addr"       .equalsIgnoreCase(n.getNodeName())){address    =n.getTextContent(); logger.info("addr:"+address   );}else
                     if("user"       .equalsIgnoreCase(n.getNodeName())){route_user =n.getTextContent(); logger.info("user:"+route_user);}else
                     if("queue"      .equalsIgnoreCase(n.getNodeName())){route_queue=n.getTextContent(); logger.info("queue:"+route_queue);}else
                     if("local"      .equalsIgnoreCase(n.getNodeName())){String s   =n.getTextContent(); try{is_route_local=Boolean.parseBoolean(s);}catch(Exception e){ is_route_local= true;logger.error("local:"+s);}logger.info("local:"+is_route_local);}else
                     {}
                 }
              }
              else{
                  logger.error("The configuration node:null");
              }             
              if(is_route_local && route_queue.equals("*"))route_queue=ufpsDef.DEF_NAME_IN_QUEUE;
              if(!is_route_local && route_queue.equals("*"))route_queue=ufpsDef.DEF_NAME_OUT_QUEUE;

       }
          
       public String toString() {return " addr:" +address +" user:" +route_user+" queue:"+route_queue+" local:"+is_route_local;

}
          

}
