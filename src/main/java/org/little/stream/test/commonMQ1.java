package org.little.stream.test;
        
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

//import org.little.mq.mqapi.*;
import org.w3c.dom.Document;
//import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;

public class commonMQ1   {
       private static Logger logger = LoggerFactory.getLogger(commonMQ1.class);
       private String   mq_mngr     ;
       private String   mq_host     ;
       private int      mq_port     ;
       private String   mq_user     ;
       private String   mq_passwd   ;
       private String   mq_channel  ;
       //private mq_mngr  queueManager;
       private String        def_page;
       private String        error_page;

       private static commonMQ1 cfg = new commonMQ1();
       public  static commonMQ1 get(){ if(cfg==null)cfg=new commonMQ1();return cfg;};
       private static String getDefNodeName(){ return "little";};
       private static String getNodeName()   { return "littlesyslog";}

       public commonMQ1(){
              mq_mngr     ="QM";
              mq_host     ="localhost";
              mq_port     =1414;
              mq_user     ="no user";
              mq_passwd   ="";
              mq_channel  ="SYSTEM.DEF.SVRCONN";
              //queueManager=null;
       }
       
       public void   setMngr     (String mq_mngr   ) {this.mq_mngr = mq_mngr;       }
       public void   setHost     (String mq_host   ) {this.mq_host = mq_host;       }
       public void   setPort     (int    mq_port   ) {this.mq_port = mq_port;}
       public void   setUser     (String mq_user   ) {this.mq_user = mq_user;}
       public void   setPasswd   (String mq_passwd ) {this.mq_passwd = mq_passwd;       }
       public void   setChannel  (String mq_channel) {this.mq_channel = mq_channel;}
       public String getMngr     () {return this.mq_mngr;   }
       public String getHost     () {return this.mq_host;   }
       public int    getPort     () {return this.mq_port;   }
       public String getUser     () {return this.mq_user;   }
       public String getPasswd   () {return this.mq_passwd; }
       public String getChannel  () {return this.mq_channel;}


       public String getDefPage  () {return def_page;          }
       public String getErrorPage() {return error_page;        }
       public boolean init(Node _node_cfg){
              if(_node_cfg==null)return false;
              //node_cfg=_node_cfg;
              logger.info("The configuration node:"+_node_cfg.getNodeName()+" for commonSyslog");

              NodeList glist=_node_cfg.getChildNodes();
              if(glist==null) return false;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("global_option".equalsIgnoreCase(n.getNodeName())){initGlobal(n);}
                  if("proxymq".equalsIgnoreCase(n.getNodeName())){initMQ(n);}
              }

              logger.info("The configuration node:"+_node_cfg.getNodeName()+" for commonMQ ");
              return true;
       }

       public boolean initMQ(Node _node_cfg){
              if(_node_cfg==null)return false;
              logger.info("The configuration node:"+_node_cfg.getNodeName()+" for commonMQ");

              NodeList glist=_node_cfg.getChildNodes();
              if(glist==null) return false;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("mngr"    .equals(n.getNodeName())){String _mq_mngr  =n.getTextContent();logger.info("mngr:"    +_mq_mngr   );setMngr(_mq_mngr);  }else
                  if("host"    .equals(n.getNodeName())){mq_host          =n.getTextContent();logger.info("host:"    +mq_host   );                     }else
                  if("port"    .equals(n.getNodeName())){String          s=n.getTextContent();try{mq_port=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set port:"+s);mq_port=1414;}logger.info("port:"+mq_port        );}else
                  if("user"    .equals(n.getNodeName())){mq_user          =n.getTextContent();logger.info("user:"    +mq_user   );                     }else
                  if("password".equals(n.getNodeName())){mq_passwd        =n.getTextContent();logger.info("password:"+mq_passwd );                     }else
                  if("channel" .equals(n.getNodeName())){String _mq_channel=n.getTextContent();logger.info("channel:" +_mq_channel  );setChannel(_mq_channel);}
              }

              logger.info("The configuration node:"+_node_cfg.getNodeName()+" for commonMQ ");
              return true;
       }
       public static boolean loadCFG(String cfg_filename) {
              Node _node_cfg = findCFG(cfg_filename);
              if(_node_cfg==null)return false;
              return commonMQ1.get().init(_node_cfg);
       }
       private static Node findCFG(String cfg_filename) {
               DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
               try {
                     DocumentBuilder builder;
                     builder  = factory.newDocumentBuilder();
                     Document doc = builder.parse(cfg_filename);
                     logger.trace("open doc:"+cfg_filename);
                     //-----------------------------------------------------------------------
                     Node node_cfg = doc.getFirstChild();
                     //-----------------------------------------------------------------------
                     if(getNodeName().equals(node_cfg.getNodeName())){
                        logger.trace("config structure name:"+getNodeName());
                        return node_cfg;
                     }
                     //-----------------------------------------------------------------------
                     if(getDefNodeName().equals(node_cfg.getNodeName())){
                         logger.trace("default config structure name:"+getDefNodeName());
                         logger.trace("seach topic name:"+getNodeName());
                         NodeList glist=node_cfg.getChildNodes();     
                         for(int i=0;i<glist.getLength();i++){
                             Node n=glist.item(i);
                             //logger.trace("topic["+i+"] name:"+n.getNodeName());
                             if(getNodeName().equals(n.getNodeName())) {
                                      //logger.trace("topic["+i+"] is name:"+getNodeName());
                                      return n;
                             }
                         }
                     } 
                     //-----------------------------------------------------------------------
               }
               catch(Exception e) {
                     logger.error("Could not load xml config file:"+cfg_filename, e);
                     return null;
               }
               return null;
       }
       private void initGlobal(Node _node_cfg) {
              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());
                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("def_page"   .equalsIgnoreCase(n.getNodeName())){def_page          =n.getTextContent(); logger.info("def_page:"+def_page);}
                     else
                     if("error_page" .equalsIgnoreCase(n.getNodeName())){error_page        =n.getTextContent(); logger.info("error_page:"+error_page);}
                 }
              }
              else{
                  logger.error("The configuration node:null");
              }                 

       }

          
}
