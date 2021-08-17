package org.little.auth;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string.stringTransform;


public class authUserStub implements authUser {
       private static final Logger  logger = LoggerFactory.getLogger(authUserStub.class);

        //private void load() {
        //        logger.trace("load ok");            
        //}
        private String        domain;
        
        public authUserStub(commonAUTH cfg_auth){
               if(cfg_auth!=null)this.domain=cfg_auth.getDefaultDomain();
               else this.domain="local";
               logger.info("create authUserStub");
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

        public boolean isUser(String username) {
               if(username==null)return false;
               boolean ret=false;
               if("av".equals(username)) ret=true;
               else
               if("iap".equals(username)) ret=true;
               logger.trace("user:"+username+" is correct:"+ret); 
               return ret;
        }
        public boolean checkUser(String user,String passwd) {
               boolean ret=false;
               if(isUser(user) && "123".equals(passwd))ret=true;
               logger.trace("passwd for user:"+user+" is correct:"+ret); 
               return ret;
        }

        private static final String DIGEST_REALM = "FESBLoginService";

        public String getDigestUser(String user) {
               return getDigestUser(user,DIGEST_REALM);
        }

        public String getDigestUser(String user,String realm) {
               if(!isUser(user)) return null;
               String password="123";

               String ha1 = stringTransform.getMD5Hash(user + ":" + realm + ":" + password);
               return ha1;

        }
        public boolean checkVisible(String user1,String user2) {
               return true;
        }

  
}

