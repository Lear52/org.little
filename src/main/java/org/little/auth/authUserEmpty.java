package org.little.auth;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string.stringTransform;


public class authUserEmpty implements authUser {
       private static final Logger  logger = LoggerFactory.getLogger(authUserEmpty.class);

       private String        domain;
       public authUserEmpty(commonAUTH cfg_auth){
               if(cfg_auth!=null)this.domain=cfg_auth.getDefaultDomain();
               else this.domain="local";
              logger.info("create authUserEmpty");
       }

        public String  getFullUserName(String username){
               return getFullUserName(username,domain);
        }
        public String  getFullUserName(String username,String domain){
               if(username==null)return null;
               if(username.indexOf('@')>= 0)return username;
               return username+"@"+domain;
        }
        public String  getShortUserName(String username){
               if(username==null)return null;
               int p=username.indexOf('@');
               if(p>= 0){
                 return username.substring(0,p);
               }
               return username;
        }

       public boolean isUser(String user) {
              boolean ret=true;
              logger.trace("user:"+user+" is correct:"+ret); 
              return ret;
       }
       public boolean checkUser(String user,String passwd) {
              boolean ret=true;
              logger.trace("passwd for user:"+user+" is correct:"+ret); 
              return ret;
       }

       public String getDigestUser(String user) {
              return getDigestUser(user,"local");
       }

       public String getDigestUser(String user,String realm) {
              String password="";
              String ha1 = stringTransform.getMD5Hash(user + ":" + realm + ":" + password);
              return ha1;

       }
       public boolean checkVisible(String user1,String user2) {
              return true;
       }

  
}

