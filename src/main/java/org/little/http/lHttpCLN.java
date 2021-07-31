package org.little.http;
       
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class lHttpCLN {
       private static final Logger logger = LoggerFactory.getLogger(lHttpCLN.class);
       private String      username;
       private String      password;
       private URI         uri;
       CloseableHttpClient httpclient;
       HttpClientContext   context;

       public lHttpCLN() throws URISyntaxException{
              //debug     =true;
              String url ="http://localhost:8080/";
              username   =null;
              password   =null;
              httpclient =null;
              context    =null;
              uri        =new URI(url);
       }
       public lHttpCLN(String _url) throws URISyntaxException{
              username   =null;
              password   =null;
              httpclient =null;
              context    =null;
              uri        =new URI(_url);
       }
       public lHttpCLN(String _url,String u,String p) throws URISyntaxException{
              httpclient =null;
              context    =null;
              uri        =new URI(_url);

              setUser(u);
              setPassword(p);
       }
       public void setURL(String _url) throws URISyntaxException  {uri=new URI(_url); }
       public void setUser(String u)    {this.username = u;}
       public void setPassword(String p){this.password = p;}

       private void _open(){
               httpclient =null;
               context   =null;
               if(username!=null && password!=null) {
                       CredentialsProvider         provider    = new BasicCredentialsProvider();
                       UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
                       provider.setCredentials(AuthScope.ANY, credentials);
                       httpclient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
                       HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
                       AuthCache authCache = new BasicAuthCache();
                       authCache.put(targetHost, new BasicScheme());
                       context = HttpClientContext.create();
                       context.setCredentialsProvider(provider);
                       context.setAuthCache(authCache);                      
               }else { 
                     httpclient = HttpClientBuilder.create().build();
               }
       }
       private void _close() {
               if(httpclient!=null)try {httpclient.close();} catch (IOException e) {}
       }

       public String get(ByteArrayOutputStream os) throws Exception{
              //CloseableHttpClient httpclient =null;
              try {
            	  /*
                   if(username!=null && password!=null) {
                      CredentialsProvider provider = new BasicCredentialsProvider();
                      UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
                      //new AuthScope("www.verisign.com", 443, "realm")
                      provider.setCredentials(AuthScope.ANY, credentials);
                      httpclient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
                   }else { 
                      httpclient = HttpClientBuilder.create().build();
                   }
                   */
                   _open();
                   HttpGet http_get = new HttpGet(uri);
                   //HttpGet http_get = new HttpGet(url);
                   //http_get.setDoAuthentication( true );
                   
                   CloseableHttpResponse response = null;
                   InputStream is=null;
                   try {
                        response = httpclient.execute(http_get);
                        HttpEntity ent = response.getEntity();
                        if(ent==null) {
                            return null;       
                        }
                        is = response.getEntity().getContent();
                        while(true) {
                                byte [] buf=new byte [1024];
                                int ret=is.read(buf);
                                if(ret<0) {
                                        break;
                                }
                                os.write(buf, 0, ret);
                        }
                        http_get.abort();
                   } 
                   finally {
                       if(is!=null)is.close();
                       response.close();
                   }
      
              } finally {
                 //httpclient.close();
                 _close();
                 os.close();
              }
              return null;

       }
       public JSONObject getJSON() throws Exception{
              ByteArrayOutputStream os        =new ByteArrayOutputStream();
              JSONObject            json_root =null;
              //CloseableHttpClient   httpclient=null;
              //HttpClientContext     context   =null;
              logger.trace("begin get json");
              //HttpGet               http_get = new HttpGet(url);
              HttpGet               http_get = new HttpGet(uri);

              try {
            	  _open();
            	  /*
                   if(username!=null && password!=null) {
                      CredentialsProvider         provider    = new BasicCredentialsProvider();
                      UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);

                      //AuthScope a=new AuthScope("www.verisign.com", 443, "realm");
                      provider.setCredentials(AuthScope.ANY, credentials);
                      httpclient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
                      URI uri = http_get.getURI();
                      HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
                      AuthCache authCache = new BasicAuthCache();
                      authCache.put(targetHost, new BasicScheme());
                      context = HttpClientContext.create();
                      context.setCredentialsProvider(provider);
                      context.setAuthCache(authCache);                      
                      
                   }else { 
                      httpclient = HttpClientBuilder.create().build();
                   }
                   */
                   CloseableHttpResponse response = null;
                   InputStream           is=null;

                   //logger.trace("get httpclient");

                   try {
                        if(context!=null) response = httpclient.execute(http_get,context);
                        else              response = httpclient.execute(http_get);
                        if(response==null){
                           return null;
                        }
                        int status = response.getStatusLine().getStatusCode();
                        if (status < 200 || status > 300) {
                            logger.error("httpclient execute code:"+status);
                            return null;
                        }
                        logger.trace("httpclient execute code:"+status);
                        HttpEntity ent = response.getEntity();
                        if(ent==null) {
                           return null;       
                        }
                        is = ent.getContent();
                        while(true) {
                                byte [] buf=new byte [10240];
                                int ret=is.read(buf);
                                if(ret<0) {break;}
                                os.write(buf, 0, ret);
                        }
                   } 
                   finally {
                       http_get.abort();
                       if(is!=null)is.close();
                       try {
                           if(response!=null)response.close();
                       }
                       catch(Exception e1){
                             logger.error("ex: "+new Except("httpclient.close",e1));
                             return null;
                       }
                   }
      
              }
              catch(Exception e){
                 logger.error("ex: "+new Except("get json httpclient",e));
                 return null;
              } 
              finally {
            	  _close();
                 //try {
                 //    if(httpclient!=null)httpclient.close();
                 //}
                 //catch(Exception e1){
                 //   logger.trace("ex: "+new Except("httpclient.close",e1));
                 //   return null;
                 //}
              }

              logger.trace("httpclient close");
              byte [] b_buf=os.toByteArray();
              if(b_buf==null){
                  logger.error("httpclient get is null");
                  return null;
              }
              if(b_buf.length<3){
                  logger.error("httpclient get is null");
                  return null;
              }
              
              String s_buf=new String(os.toByteArray(),Charset.forName("UTF-8")); 

              //logger.trace("httpclient get:"+s_buf);

              //if(s_buf==null){
              //   logger.error("httpclient get is null");
              //   return null;
              //}
              //byte[] out = os.toByteArray();
              try {
                  JSONTokener tokener=new JSONTokener(s_buf);

                  json_root=new JSONObject(tokener);
              }
              catch(Exception e){
                 logger.error("ex:"+new Except("JSONTokener",e));
                 return null;
              } 
              //logger.trace("get json object");
              os.close();
              

              logger.trace("end get json:"+json_root);
              
              return json_root;
       }
       public void sent(ByteArrayOutputStream os,String filename) throws Exception{
              ByteArrayInputStream is=new ByteArrayInputStream(os.toByteArray());
              //CloseableHttpClient httpclient =null;
              //if(debug)System.out.println("ok!");
              
              try {
                   _open();
                   /*
                   if(username!=null && password!=null) {
                      CredentialsProvider provider = new BasicCredentialsProvider();
                      UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
                      //new AuthScope("www.verisign.com", 443, "realm")
                      provider.setCredentials(AuthScope.ANY, credentials);
                      httpclient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
                   }else { 
                      httpclient = HttpClientBuilder.create().build();
                   }
                   //CloseableHttpClient httpclient = HttpClientBuilder.create().build();
                   //httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                  
                   HttpPost http_post = new HttpPost(url);
                  */
                   HttpPost http_post = new HttpPost(uri);
                   
                   MultipartEntityBuilder builder = MultipartEntityBuilder.create();        

                   builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                   builder.addBinaryBody(filename,is);              

                   //File file = new File(filename);           /**/
                   //FileBody fileBody = new FileBody(file);       /**/
                   //builder.addPart("file_"+filename, fileBody);         /**/
                   
                   HttpEntity entity = builder.build();
                   http_post.setEntity(entity);
                   
                   //if(debug)System.out.println("executing request " + http_post.getRequestLine());
                  
                   HttpResponse response        = httpclient.execute(http_post);
                   HttpEntity   response_entity = response.getEntity();
                  
                   //if(debug)System.out.println(response.getStatusLine());
                   
                   if (response_entity != null) {
                      //if(debug)System.out.println(EntityUtils.toString(response_entity));
                   }
                   if (response_entity != null) {
                       EntityUtils.consume(response_entity);
                   }              
              }catch(Exception e){
                 logger.trace("ex: "+new Except("get json httpclient",e));
                 return ;
              } 
              finally {
                 try {
                     if(httpclient!=null)httpclient.close();
                 }
                 catch(Exception e1){
                    logger.trace("ex: "+new Except("httpclient.close",e1));
                    return ;
                 }
              }
              
              //httpclient.getConnectionManager().shutdown();
              
       }
       public static void main1(String[] args) throws Exception {
              System.setProperty("java.net.preferIPv4Stack","true");
              lHttpCLN cln=new lHttpCLN();
              String  f_name;
              String  url;

              if(args.length>0)url=args[0];
              else             url="http://sa5lear1.vip.cbr.ru:8080/main/doc/law_cb.pdf"; 

              System.out.println("executing request :" + url);

              cln.setURL(url);

              ByteArrayOutputStream os=new ByteArrayOutputStream();
              f_name=cln.get(os);
              byte[] out = os.toByteArray();
              System.out.println("file:"+f_name);
              System.out.write(out);
              
       }
       public static void main(String[] args) throws Exception {
              System.setProperty("java.net.preferIPv4Stack","true");
              String url_state="http://127.0.0.1:8080/control/cmd/2list";
              lHttpCLN cln=new lHttpCLN(url_state);

              logger.trace("new lHttpCLN("+url_state+")");

              JSONObject root=null;
              try{
                  root=cln.getJSON();
              } 
              catch (Exception ex) {
                    logger.error("ex:"+new Except("cln.getJSON("+url_state+")",ex));
                    return;
              }

              logger.trace("getJSON("+url_state+") json:"+root);

       }
}
