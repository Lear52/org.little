package org.little.auth;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;


/**
 * 
 */
public class commonAUTH{

       private static final Logger logger = LoggerFactory.getLogger(commonAUTH.class);

       private int           type_authenticateUser          ;
       private String        ldap_url                       ;
       private String        ldap_ad_username               ;
       private String        ldap_ad_password               ;
       private String        java_security_krb5_conf        ;
       private String        java_security_auth_login_config;
       private String        realm                          ;
       private String        default_domain                 ;
       private boolean       auth_requared                  ;
       private String        listuser_filename              ;
       private authUser      auth_user                      ;
       
       public  static final  int NOAUTH   =0;
       public  static final  int STUB     =1;
       public  static final  int XML      =2;
       public  static final  int LDAP     =3;
       public  static final  int SPNEGO   =4;

       public commonAUTH(){clear();}

       public void clear(){
              type_authenticateUser          =0;
              ldap_url                       ="ldap://rdc22-vip01.vip.cbr.ru:3268";
              ldap_ad_username               ="k1svcfarmadmin";       
              ldap_ad_password               ="3edcVFR$";             
              realm                          ="vip.cbr.ru";
              default_domain                 ="vip.cbr.ru";
              java_security_krb5_conf        ="krb5.conf";
              java_security_auth_login_config="login.conf";
              auth_requared                  =true;
              //listuser_filename              ="user_h.xml";
              listuser_filename              ="";
              auth_user                      =new authUserEmpty(this);
       }
       public void init(NodeList glist){
              if(glist==null) return;
              logger.info("The configuration auth");
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("ldap_ad_username"               .equals(n.getNodeName())){ldap_ad_username=n.getTextContent();               logger.info("ldap_ad_username:"+ldap_ad_username);                              }
                  else                                                                                                            
                  if("ldap_ad_password"               .equals(n.getNodeName())){ldap_ad_password=n.getTextContent();               logger.info("ldap_ad_password:**********");                                     }
                  else                                                                                                            
                  if("ldap_url"                       .equals(n.getNodeName())){ldap_url=n.getTextContent();                       logger.info("ldap_url:"+ldap_url);                                              }
                  else
                  if("java_security_krb5_conf"        .equals(n.getNodeName())){java_security_krb5_conf=n.getTextContent();        logger.info("java.security.krb5.conf:"+java_security_krb5_conf);                }
                  else
                  if("java_security_auth_login_config".equals(n.getNodeName())){java_security_auth_login_config=n.getTextContent();logger.info("java.security.auth.login.config:"+java_security_auth_login_config);}
                  else
                  if("default_realm"                  .equals(n.getNodeName())){realm=n.getTextContent();                          logger.info("Default realm:"+realm);                                            }
                  else
                  if("default_domain"                 .equals(n.getNodeName())){default_domain   =n.getTextContent(); logger.info("default_domain:"+default_domain);        }
                  else
                  if("auth_requared"                  .equals(n.getNodeName())){String s=n.getTextContent(); try{auth_requared=Boolean.parseBoolean(s);}catch(Exception e){auth_requared=true;logger.error("auth_requared:"+s);} logger.info("auth_requared:"+auth_requared);}
                  else
                  if("authenticateUser"               .equals(n.getNodeName())){String s=n.getTextContent(); try{type_authenticateUser=Integer.parseInt(s, 10);}catch(Exception e){ type_authenticateUser=0; logger.error("authenticateUser:"+s);} logger.info("authenticateUser:"+type_authenticateUser);}
                  else
                  if("listuser_filename"              .equals(n.getNodeName())){listuser_filename=n.getTextContent();             logger.info("listuser_filename:"+listuser_filename);}
                  //else
              }
              System.setProperty("java.security.krb5.conf",        getPathKrb5() );
              System.setProperty("java.security.auth.login.config",getPathLogin());

              logger.info("create authenticateUser:"+type_authenticateUser);
              switch(type_authenticateUser) {
              case NOAUTH: auth_user=new authUserEmpty     (this);logger.info("create authUserEmpty     code type:"+type_authenticateUser);break;
              case STUB  : auth_user=new authUserStub      (this);logger.info("create authUserStub      code type:"+type_authenticateUser);break;             
              case LDAP  : auth_user=new authUserLDAP      (this);logger.info("create authUserLDAP      code type:"+type_authenticateUser);break;        
              case XML   : auth_user=new authUserXML       (this);logger.info("create authUserXML       code type:"+type_authenticateUser);break;                  
              case SPNEGO: auth_user=new authUserNegotiate (this);logger.info("create authUserNegotiate code type:"+type_authenticateUser);break;             
                                                                                                       
              default:     auth_user=new authUserEmpty     (this);logger.info("create authUserEmpty    ");break;
              }
              
       }

       public int           getTypeAuthenticateUser(){return type_authenticateUser;             }
       public String        getLdapUrl                (){return ldap_url;                       }
       public String        getRealm                  (){return realm;                          }
       public String        getLdapUsername           (){return ldap_ad_username;               }
       public String        getLdapPassword           (){return ldap_ad_password;               }
       public String        getPathKrb5               (){return java_security_krb5_conf;        }
       public String        getPathLogin              (){return java_security_auth_login_config;}
       public String        getDefaultDomain          (){return default_domain;                 }
       public boolean       getAuthRequared           (){return auth_requared;}
       public authUser      getAuthUser               (){return auth_user    ;}
       public String        getListUserFilename       (){return listuser_filename;}
}

