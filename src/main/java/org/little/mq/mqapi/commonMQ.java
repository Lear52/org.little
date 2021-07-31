package org.little.mq.mqapi;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonMQ{
       private static final Logger logger = LoggerFactory.getLogger(commonMQ.class);

       private String    mq_mngr;
       private String    mq_queue;
       private String    mq_host;
       private int       mq_port;
       private String    mq_user;
       private String    mq_passwd;
       private String    mq_channel;

       public commonMQ() {
              clear();
              //logger.info("create commonMQ");
       }

       public void clear() {
              mq_mngr   ="noname_mngr";
              mq_queue  ="noname_queue";
              mq_host   =null;
              mq_port   =1414;
              mq_user   =null;
              mq_passwd =null;
              mq_channel="SYSTEM.ADMIN.SVRCONN";
       }
       public String  getNameQ   () {return mq_queue;}
       public String  getNameMngr() {return mq_mngr;}
       public String  getHost    () {return mq_host;}
       public int     getPort    () {return mq_port;}
       public String  getUser    () {return mq_user;}
       public String  getPasswd  () {return mq_passwd;}
       public String  getChannel () {return mq_channel;}

       public void   setNameQ   (String q) {mq_queue=q;}
       public void   setNameMngr(String m) {mq_mngr=m;}


       public void  init(Node _node_cfg) {

              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());

                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("mngr"    .equalsIgnoreCase(n.getNodeName())){mq_mngr          =n.getTextContent();logger.info("mngr:"    +mq_mngr   );                     }else
                     if("queue"   .equalsIgnoreCase(n.getNodeName())){mq_queue         =n.getTextContent();logger.info("queue:"   +mq_queue  );                     }else
                     if("host"    .equalsIgnoreCase(n.getNodeName())){mq_host          =n.getTextContent();logger.info("host:"    +mq_host   );                     }else
                     if("port"    .equalsIgnoreCase(n.getNodeName())){String          s=n.getTextContent();try{mq_port=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set port:"+s);mq_port=1414;}logger.info("port:"+mq_port        );}else
                     if("user"    .equalsIgnoreCase(n.getNodeName())){mq_user          =n.getTextContent();logger.info("user:"    +mq_user   );                     }else
                     if("password".equalsIgnoreCase(n.getNodeName())){mq_passwd        =n.getTextContent();logger.info("password:"+mq_passwd );                     }else
                     if("channel" .equalsIgnoreCase(n.getNodeName())){mq_channel       =n.getTextContent();logger.info("channel:" +mq_channel);                     }
                 }
              }
              else{
                  logger.error("The configuration node:null");
              }                 


       }



}

