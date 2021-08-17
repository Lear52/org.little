package org.little.stream.cfg;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonChannel  {
       private static final Logger logger = LoggerFactory.getLogger(commonChannel.class);

       private String name;
       private String type;
       private String subtype;
       private String id;
       private int    port;
       private String local_bind_server;
       private String local_bind_client;
       private String client_host;      
       private int    client_port;      
       private int    max_size_queue;
       private int    timeout;


       public commonChannel(){clear();}
       /*
       public commonChannel(String _type,String _subtype,String _id){
              this.type=_type;
              this.subtype=_subtype;
              this.id=_id;
       }
       */
       private void clear(){
              this.name="";
              this.type="";
              this.subtype="";
              this.id="";
              this.port=9000;             
              this.local_bind_server=null;
              this.local_bind_client=null;
              this.client_host="127.0.0.1";      
              this.client_port=8000;
              this.max_size_queue=1000;
              this.timeout=10000;
       }

       public String getID(){return id;}
       public String getName(){return name;}
       public String getType(){return type;}
       public String getSubType(){return subtype;}
       public int    getPort() {return port;}
       public String getLocalBindServer() {return local_bind_server;}
       public String getLocalBindClient() {return local_bind_client;}
       public String getClientHost() {return client_host;}
       public int    getClientPort() {return client_port;}
       public int    getMaxSizeQueue() {return max_size_queue;}
       public int    getTimeout() {return timeout;}




       public void    init(Node _node_cfg,int _max_size_queue){
               if(_node_cfg!=null){
            	   max_size_queue=_max_size_queue;
                  logger.info("The configuration node:"+_node_cfg.getNodeName());
                  if(_node_cfg.getAttributes().getNamedItem("name")   !=null) { name=_node_cfg.getAttributes().getNamedItem("name").getNodeValue();      logger.info("name:"   +name);   }
                  if(_node_cfg.getAttributes().getNamedItem("type")   !=null) { type=_node_cfg.getAttributes().getNamedItem("type").getNodeValue();      logger.info("type:"   +type);   }
                  if(_node_cfg.getAttributes().getNamedItem("subtype")!=null) { subtype=_node_cfg.getAttributes().getNamedItem("subtype").getNodeValue();logger.info("subtype:"+subtype);}
                  if(_node_cfg.getAttributes().getNamedItem("id")     !=null) { type=_node_cfg.getAttributes().getNamedItem("id").getNodeValue();        logger.info("id:"     +id);     }
      
                  NodeList glist=_node_cfg.getChildNodes();     
                  for(int i=0;i<glist.getLength();i++){
                      Node n=glist.item(i);
                      if("port"              .equalsIgnoreCase(n.getNodeName())){String s         =n.getTextContent(); try{port       =Integer.parseInt(s, 10);}catch(Exception e){ port=9000;       logger.error("port:"+s);}       logger.info("port:"+port);              }else
                      if("timeout"           .equalsIgnoreCase(n.getNodeName())){String s         =n.getTextContent(); try{timeout    =Integer.parseInt(s, 10);}catch(Exception e){ timeout= 10000;logger.error("timeout:"+s);}logger.info("timeout:"+client_port);}else
                      if("client_port"       .equalsIgnoreCase(n.getNodeName())){String s         =n.getTextContent(); try{client_port=Integer.parseInt(s, 10);}catch(Exception e){ client_port=8000;logger.error("client_port:"+s);}logger.info("client_port:"+client_port);}else
                      if("local_bind_server" .equalsIgnoreCase(n.getNodeName())){local_bind_server=n.getTextContent(); logger.info("local_bind_server:"+local_bind_server);}else
                      if("local_bind_client" .equalsIgnoreCase(n.getNodeName())){local_bind_client=n.getTextContent(); logger.info("local_bind_client:"+local_bind_client);}else
                      if("client_host"       .equalsIgnoreCase(n.getNodeName())){client_host      =n.getTextContent(); logger.info("client_host:"      +client_host      );}else
                      {}
                  }
      
                  if(type.equalsIgnoreCase("server")){}else
                  if(type.equalsIgnoreCase("client")){
                     if(subtype.equalsIgnoreCase("trans")){
      
                     }else
                     if(subtype.equalsIgnoreCase("local")){
      
                     }else
                     {logger.error("channel_type:"+type+" channel_subtype:"+subtype +"trans|local");return;}
      
                  }else{logger.error("channel_type:"+type+"servar|client");return;}
      
               }
               else{
                   logger.error("The configuration node:null");
               }                 





       }

}
