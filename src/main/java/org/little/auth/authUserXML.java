package org.little.auth;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string.stringTransform;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;

public class authUserXML implements authUser {
       private static final Logger  logger = LoggerFactory.getLogger(authUserXML.class);

       private Document      doc       ;
       private NodeList      ulist     ;

       private String        cfg_filename;
       private String        domain;

       public authUserXML(commonAUTH cfg_auth){
              this.cfg_filename=cfg_auth.getListUserFilename();
              this.domain      =cfg_auth.getDefaultDomain();
              load();
              logger.info("create authUserXML:"+cfg_filename);
       }
       public authUserXML(String  _domain,Node node_cfg){
              this.cfg_filename="";
              this.domain=_domain;
              init(node_cfg);
              logger.info("create authUserXML");
       }
       /*
       public authUserXML(){
              this.cfg_filename="user_h.xml";
              this.domain="local";
              load();
              logger.info("create authUserXML");
       }
       */
       private synchronized void load() {
               if(doc==null){
                  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                  try {
                       DocumentBuilder builder;
                       builder  = factory.newDocumentBuilder();
                       doc      = builder.parse(cfg_filename);
                       logger.trace("open doc:"+cfg_filename);
                       Node node_cfg = doc.getFirstChild();                
                       init(node_cfg);
                       doc=null;
                       ulist=null;
                  }catch(Exception e) {
                     doc=null;
                     ulist=null;
                     logger.error("load file:"+cfg_filename+" error ex:"+e);   
                  }
               }

       }
       public void init(Node node_cfg) {
              if(node_cfg==null){
                 logger.error("node:null");
                 return;
              }
              logger.trace("node:"+node_cfg.getNodeName());
              if("user".equals(node_cfg.getNodeName())){
                 ulist=node_cfg.getChildNodes();     
                 logger.trace("load ok");
                 return;
              }
              logger.trace("node user unknow");
              ulist=null;
       }

       public String  getFullUserName(String username){
               return getFullUserName(username,domain);
       }
       public String  getFullUserName(String username,String domain){
              return username+"@"+domain;
       }
       public String  getShortUserName(String username){
              return username;
       }

       public boolean isUser(String user) {
              boolean ret=false;
              if(user==null)return false;
              if(ulist==null)return false;
              for(int i=0;i<ulist.getLength();i++){
                  //-----------------------------------------
                  Node n=ulist.item(i);
                  if("p".equals(n.getNodeName())){
                     NodeList nl=n.getChildNodes(); 
                     for(int j=0;j<nl.getLength();j++){
                         Node nn=nl.item(j);
                         if("name".equals(nn.getNodeName())){
                            if(user.equals(nn.getTextContent())){
                               return true;
                            }
                         }
                     }
                  }
                  //-----------------------------------------
             }
             return ret;
       }
       public boolean checkUser(String user,String passwd) {
              boolean ret=false;
              if(user==null || passwd==null)return false;
              if(ulist==null)return false;
              for(int i=0;i<ulist.getLength();i++){
                  //-----------------------------------------
                  Node n=ulist.item(i);
                  if("p".equals(n.getNodeName())){
                     NodeList nl=n.getChildNodes(); 
                     String u=null;
                     String p=null;
                     for(int j=0;j<nl.getLength();j++){
                         Node nn=nl.item(j);
                         if("name"    .equals(nn.getNodeName())){u=nn.getTextContent();}
                         if("password".equals(nn.getNodeName())){p=nn.getTextContent();}
                     }
                     if(u==null||p==null)break;
                     if(user.equals(u)&&passwd.equals(p))return true;
                  }
                  //-----------------------------------------
             }
              return ret;
       }
       private String getPswd(String user) {
              //boolean ret=false;
              if(user==null )return null;

              for(int i=0;i<ulist.getLength();i++){
                  //-----------------------------------------
                  Node n=ulist.item(i);
                  if("p".equals(n.getNodeName())){
                     NodeList nl=n.getChildNodes(); 
                     String u="";
                     //---------------------------------------------------------------------------
                     for(int j=0;j<nl.getLength();j++){
                         Node nn=nl.item(j);
                         if("name".equals(nn.getNodeName())){
                             u=nn.getTextContent();
                         }
                         if(user.equals(u)){
                            if("password".equals(nn.getNodeName())){return nn.getTextContent();}
                         }
                     }
                     //---------------------------------------------------------------------------
                  }
                  //-----------------------------------------
             }
             return null;
       }

       private static final String DIGEST_REALM = "FESBLoginService";

       public String getDigestUser(String user) {
              return getDigestUser(user,DIGEST_REALM);
       }

       public String getDigestUser(String user,String realm) {
              if(!isUser(user)) return null;
              String password=getPswd(user);
              String ha1 = stringTransform.getMD5Hash(user + ":" + realm + ":" + password);
              return ha1;
       }
       public boolean checkVisible(String user1,String user2) {
              return true;
       }

  
}

