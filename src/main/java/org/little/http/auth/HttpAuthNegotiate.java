package org.little.http.auth;

import java.security.PrivilegedExceptionAction;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;
import org.little.http.commonHTTP;
//import org.little.proxy.commonProxy;
import org.little.proxy.util.statChannel;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
//import org.little.proxy.util.statChannel;

//import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import jcifs.util.Base64;


public class HttpAuthNegotiate extends HttpAuth {
       private static final Logger  logger = LoggerFactory.getLogger(HttpAuthNegotiate.class);
      
       /** Factory for GSS-API mechanism. */
       private static final GSSManager     MANAGER          = GSSManager.getInstance();
       /** GSS-API mechanism "1.3.6.1.5.5.2". */
       private static final Oid            SPNEGO_OID       = getOid();
       private static final Lock           LOCK             = new ReentrantLock();
       //private        AtomicBoolean        authenticated    ;
       private        String               username         ; 
       //private        boolean              is_auth_disable  ; 
       private        String               host_addr_port   ;

       private        LoginContext         loginContext     ;    /** Login Context server uses for pre-authentication.    */
       @SuppressWarnings("unused")
       private        KerberosPrincipal    serverPrincipal  ;    /** Server Principal used for pre-authentication.        */
       private        GSSCredential        serverCredentials;    /** Credentials server uses for authenticating requests. */


       private   HttpAuthNegotiate() {super(HttpAuth.SPNEGO,null);}

       protected HttpAuthNegotiate(int _type_auth,statChannel _stat) {
               super(_type_auth,_stat);
               init();
       }

       @Override                            
       public String getFieldName() {return HttpHeaderNames.AUTHORIZATION.toString();} 


       private void init(){
              logger.debug("begin init HttpAuthNegotiate");
              try{
                 // set auth info 
                  CallbackHandler handler = new CallbackHandler() {
                                  public void handle(final Callback[] callback) {
                                      for (int i=0; i<callback.length; i++) {
                                          if(callback[i] instanceof NameCallback) {
                                             NameCallback nameCallback = (NameCallback) callback[i];
                                             nameCallback.setName(commonHTTP.get().getCfgAuth().getLdapUsername());
                                          } 
                                          else 
                                          if(callback[i] instanceof PasswordCallback) {
                                             PasswordCallback passCallback = (PasswordCallback) callback[i];
                                             passCallback.setPassword(commonHTTP.get().getCfgAuth().getLdapPassword().toCharArray());
                                          } else {
                                                  logger.info("Unsupported Callback i=" + i + "; class=" + callback[i].getClass().getName());
                                          }
                                      }
                                  }
                  };
          
                  loginContext = new LoginContext("spnego-server", handler);
                  loginContext.login();
                  Subject subject=loginContext.getSubject();
                  PrivilegedExceptionAction<GSSCredential> action =  new PrivilegedExceptionAction<GSSCredential>() {
                                                           public GSSCredential run() throws GSSException {
                                                                  return MANAGER.createCredential(null,GSSCredential.INDEFINITE_LIFETIME,SPNEGO_OID,GSSCredential.ACCEPT_ONLY);
                                                           } 
                 };
                 serverCredentials = Subject.doAs(subject, action);
                 serverPrincipal   = new KerberosPrincipal(serverCredentials.getName().toString());
              }
              catch(Exception ex){
                  Except ex1=new Except(ex.toString(),ex);
                  logger.error("HttpAuthNegotiate ex:"+ex1);
              }
              logger.debug("end init HttpAuthNegotiate");


       }
    
       @Override
       public HttpAuthResponse authParse(String authorization,String request_method){
              response=new HttpAuthResponse();
              if(authorization==null){
                 response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                 response.setUser(null);
                 response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                 response.setAuthicationData("Negotiate");        
                 response.setAuthicationHeader("WWW-Authenticate");        

                 logger.trace("authParse("+authorization+","+request_method+") ret:UNAUTHORIZED");
                 return response;
              }
              String  principal="unknown_principal";
              //String  realm=commonHTTP.get().getRealm();//"vip.cbr.ru";/**/
              
              if(authorization.startsWith("Negotiate")) {
                  String sub_auth=authorization.substring(10);
                    //---------------------------------------------------------------------------------------
                    if(channel_info!=null)logger.trace("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" client sent to server negotiate token:"+sub_auth);
                    byte[] _gss = Base64.decode(sub_auth);
                    if(0 == _gss.length) {
                       logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" GSS data was NULL.");
                       //writeAuthenticationRequired("",realm,"",null);
                       response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                       response.setUser(null);
                       response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                       response.setAuthicationData("Negotiate");        
                       response.setAuthicationHeader("WWW-Authenticate");        
                       return response;
                    }
                    //---------------------------------------------------------------------------------------
                    {
                       String hdr="NTLMSSP";
                       byte [] _ntlmspp={(byte)0x4E,(byte)0x54,(byte)0x4C,(byte)0x4D,(byte)0x53,(byte)0x53,(byte)0x50,(byte)0x0};
                       //try{ HexDump.dump(_ntlmspp); }catch(Exception ex113){}
                       boolean is_ntlmspp=true;
                       for(int i=0;(i<_ntlmspp.length && i<_gss.length);i++){
                           if(_ntlmspp[i]!=_gss[i]){
                              is_ntlmspp=false;
                              break;
                          }
                       }
                       if(is_ntlmspp){
                          if(channel_info!=null)logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" GSS data begin with :"+hdr);
                          //writeAuthenticationRequired("",realm,"",null);
                          response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                          response.setUser(null);
                          response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                          response.setAuthicationData("Negotiate");        
                          response.setAuthicationHeader("WWW-Authenticate");        
                          return response;
                       }
                    }
                  
                  //------------------------------------------------------------------------------------------
                  byte[]        token2    = null;
                  GSSContext    context   = null;
                  try {
                      //-----------------------------------------------------------------------------------------------
                      LOCK.lock();
                      try {
                              if(channel_info!=null)logger.trace("client_ip:"+channel_info.getSrc()+" crete context");
                          context = MANAGER.createContext(serverCredentials);
                      }
                      catch(Exception ex){
                          String msg=ex.toString();
                          Except ex1=new Except(msg,ex);
                          if(channel_info!=null)logger.error("client_ip:"+channel_info.getSrc()+" error createContext ex:"+ex1);
                          //writeAuthenticationRequired("",realm,"",null);
                          response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                          response.setUser(null);
                          response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                          response.setAuthicationData("Negotiate");        
                          response.setAuthicationHeader("WWW-Authenticate");        
                          return response;
                      }
                      finally {
                          LOCK.unlock();
                      }
                      //-----------------------------------------------------------------------------------------------
                      LOCK.lock();
                      try {
                          token2 = context.acceptSecContext(_gss, 0, _gss.length);
                      }
                      catch(GSSException ex){
                          String msg=ex.toString()+" | major:"+ex.getMajorString()+" | minor:"+ex.getMinorString();
                          Except ex1=new Except(msg,ex);
                          if(channel_info!=null)logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" error acceptSecContext ex:"+ex1);
                          //writeAuthenticationRequired("",realm,"",null);
                          response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                          response.setUser(null);
                          response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                          response.setAuthicationData("Negotiate");        
                          response.setAuthicationHeader("WWW-Authenticate");        
                          return response;
                      }
                      finally {
                          LOCK.unlock();
                      }
                      //-----------------------------------------------------------------------------------------------
                      try {
                          if (!context.isEstablished()) {
                               if(channel_info!=null)logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" context not established");
                               //writeAuthenticationRequired("",realm,"",null);
                               response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                               response.setUser(null);
                               response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                               response.setAuthicationData("Negotiate");        
                               response.setAuthicationHeader("WWW-Authenticate");        
                               return response;
                          }
                          else{
                               try {
                                   principal = context.getSrcName().toString();
                               }
                               catch(Exception ex){
                                    Except ex1=new Except(ex.toString(),ex);
                                    if(channel_info!=null)logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" error context.getSrcName().toString() ex:"+ex1);
                                    //writeAuthenticationRequired("",realm,"",null);
                                    response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                                    response.setUser(null);
                                    response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                                    response.setAuthicationData("Negotiate");        
                                    response.setAuthicationHeader("WWW-Authenticate");        
                                    return response;
                               }
                               finally {
                                 //SpnegoAuthenticator.LOCK.unlock();
                               }
                          }
          
                          if(channel_info!=null)logger.debug("client_ip:"+channel_info.getSrc()+" server_ip:"+host_addr_port+" principal:" + principal);
                          //------------------------------------------------------------------------------
                          
                          if(token2!=null){
                             String _token2=Base64.encode(token2);
                             if(channel_info!=null)logger.trace("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" context.acceptSecContext(_gss, 0, _gss.length) return:" + _token2);
                          }
                          //------------------------------------------------------------------------------
                      }
                      catch(Exception ex){
                          String msg=ex.toString();
                          Except ex1=new Except(msg,ex);
                          if(channel_info!=null)logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" error get Context ex:"+ex1);
                          //writeAuthenticationRequired("",realm,"",null);
                          response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                          response.setUser(null);
                          response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                          response.setAuthicationData("Negotiate");        
                          response.setAuthicationHeader("WWW-Authenticate");        
                          return response;
                      }
                      finally {
                          //SpnegoAuthenticator.LOCK.unlock();
                      }
                      //------------------------------------------------------------------------------------
                  } 
                  finally {
                      if (null != context) {
                          //SpnegoAuthenticator.LOCK.lock();
                          try {
                              if(channel_info!=null)logger.info("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" context dispose");
                              context.dispose();
                          }
                          catch(Exception ex){
                                Except ex1=new Except(ex.toString(),ex);
                                if(channel_info!=null)logger.error("AuthenticationNegotiateRequired client_ip:"+channel_info.getSrc()+" error context.dispose() ex:"+ex1);
                                //writeAuthenticationRequired("",realm,"",null);
                                response.setStatus(HttpResponseStatus.UNAUTHORIZED);
                                response.setUser(null);
                                response.setBodyMsg(getBody("Negotiate",HttpResponseStatus.UNAUTHORIZED));
                                response.setAuthicationData("Negotiate");        
                                response.setAuthicationHeader("WWW-Authenticate");
                                return response;                                
                          }
                          finally {
                              //SpnegoAuthenticator.LOCK.unlock();
                          }
                      }
                  }
                  //-----------------------------------------------------------------------------------------
                }
                logger.trace("auth username:"+principal+" ret:Ok");

                username=commonHTTP.get().getCfgAuth().getAuthUser().getFullUserName(principal);     

                response.setStatus(HttpResponseStatus.OK);
                response.setUser(username);
                response.setBodyMsg("");
                response.setAuthicationData("");        
                response.setAuthicationHeader(null);
                logger.trace("auth full user name:"+response.getUser()+" ret:"+response.getStatus());
                return response;
       }
       private static Oid getOid() {
           Oid oid = null;
           try {
               oid = new Oid("1.3.6.1.5.5.2");
           } catch (GSSException gsse) {
               logger.error("Unable to create OID 1.3.6.1.5.5.2 !", gsse);
           }
           return oid;
       }

}
