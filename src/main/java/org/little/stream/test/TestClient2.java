package org.little.stream.test;
       
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

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
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class TestClient2 {
       private static final Logger logger = LoggerFactory.getLogger(TestClient2.class);
       private String      username;
       private String      password;
       private URI         uri;
       CloseableHttpClient httpclient;
       HttpClientContext   context;

       public TestClient2() throws URISyntaxException{
              //debug     =true;
              String url ="http://localhost:8080/";
              username   =null;
              password   =null;
              httpclient =null;
              context    =null;
              uri        =new URI(url);
       }
       public TestClient2(String _url) throws URISyntaxException{
              username   =null;
              password   =null;
              httpclient =null;
              context    =null;
              uri        =new URI(_url);
       }
       public TestClient2(String _url,String u,String p) throws URISyntaxException{
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
                  //System.out.println("begin CredentialsProvider");

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

                  //System.out.println("end CredentialsProvider");
               }else { 
                     httpclient = HttpClientBuilder.create().build();
               }
       }
       private void _close() {
               if(httpclient!=null)try {httpclient.close();} catch (IOException e) {}
       }

       public String get(ByteArrayOutputStream os) throws Exception{
              try {
                   _open();
                   _get(os);
      
              } finally {
                 _close();
                 os.close();
              }
              return null;

       }
       public String _get(ByteArrayOutputStream os) throws Exception{
              HttpGet http_get = new HttpGet(uri);
              
              CloseableHttpResponse response = null;
              InputStream is=null;
              try {
                   if(context!=null) response = httpclient.execute(http_get,context);
                   else              response = httpclient.execute(http_get);
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
                  if(response!=null)response.close();
              }
      
              return null;

       }
       public void sent(byte[] out,String filename) throws Exception{
              try {
                   _open();
                   _sent(out,filename);
              }catch(Exception e){
                 logger.trace("ex: "+new Except("post file  httpclient",e));
                 return ;
              } 
              finally {
                 _close();
              }
       }
       public void _sent(byte[] out,String filename) throws Exception{
              ByteArrayInputStream is=new ByteArrayInputStream(out);
              try {
                   HttpPost http_post = new HttpPost(uri);
                   
                   MultipartEntityBuilder builder = MultipartEntityBuilder.create();        

                   builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                   builder.addBinaryBody(filename,is);              

                   HttpEntity entity = builder.build();
                   http_post.setEntity(entity);
                   
                   HttpResponse response        = null;
                   if(context!=null) response = httpclient.execute(http_post,context);
                   else              response = httpclient.execute(http_post);

                   HttpEntity   response_entity = response.getEntity();
                  
                   if (response_entity != null) {
                      //if(debug)System.out.println(EntityUtils.toString(response_entity));
                   }
                   if (response_entity != null) {
                       EntityUtils.consume(response_entity);
                   }              
              }catch(Exception e){
                 logger.trace("ex: "+new Except("post file  httpclient",e));
                 return ;
              } 
       }
       public static void main(String[] args) throws Exception {
              System.setProperty("java.net.preferIPv4Stack","true");
              long start=System.currentTimeMillis();

              TestClient2 cln=new TestClient2();
              int count=1;
              String  url;
              if(args.length<3){
                  System.out.println(" [get|post] [count] [url]");
                return;
              }
              cln.setUser("av");
              cln.setPassword("123");
              //System.out.println("set user name");
              try{count=Integer.parseInt(args[1],10);}catch(Exception e){count=1;System.out.println("error count==1");}

              if(args[0].equalsIgnoreCase("get")){url=args[2]+"/stream/"+"get";}
              else                               {url=args[2]+"/stream/"+"post";}
              //url="http://127.0.0.1:8080/stream/get"; 

              cln.setURL(url);
              System.out.println("executing request :" + url);

              if(args[0].equalsIgnoreCase("get")){
                 System.out.println("GET" );
                 for(int i=0;i<count;i++){
                     ByteArrayOutputStream os=new ByteArrayOutputStream();
                     cln.get(os);
                     byte[] out = os.toByteArray();
                     //System.out.println("file:"+f_name);
                     System.out.write(out);
                 }
             }
             else{
                 System.out.println("POST" );
                 for(int i=0;i<count;i++)cln.sent(def_msg2.getBytes(),"test.xml");

             }
             start=System.currentTimeMillis()-start;
             System.out.println("\ntime:"+start+"\n s:"+(double)count/(double)start*1000);
       }
       public static final String def_msg1=""
               +"<?xml version=\"1.0\" encoding=\"WINDOWS-1251\"?>\n"
               +"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">\n"
               +"  <env:Header>\n"
               +"      <props:MessageInfo xmlns:props=\"urn:cbr-ru:msg:props:v1.3\">\n"
               +"           <props:To>uic:040700199900</props:To>\n"
               +"           <props:From>uic:049999900000</props:From>\n"
               +"           <props:MessageID>450295413-0000004099013</props:MessageID>\n"
               +"           <props:MessageType>1</props:MessageType>\n"
               +"           <props:Priority>5</props:Priority>\n"
               +"           <props:CreateTime>2015-12-16T19:50:08Z</props:CreateTime>\n"
               +"      </props:MessageInfo>\n"
               +"      <props:DocInfo xmlns:props=\"urn:cbr-ru:msg:props:v1.3\">\n"
               +"           <props:DocFormat>2</props:DocFormat>\n"
               +"           <props:DocType>MT865</props:DocType>\n"
               +"           <props:DocID>450295413-0000004099013</props:DocID>\n"
               +"      </props:DocInfo>\n"
               +"   </env:Header>\n"
               +"   <env:Body>\n"
               +"   <Object xmlns=\"urn:cbr-ru:dc:v1.0\">ezE6DQo6VFlQRTo4NjUNCjpUTzo5OTk5OTk5OTkwMDANCjpGUk9NOjA0MDQ5OTk5OTAxMw0KOklEOjA0MDQ5OTk5OTAxMzIwMTUxMjE2NDUwMjk1NDEzLTAwMDAwMDQwOTkwMTMvMDAwMDAwMDAwMQ0KOlJFRklEOjAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwLzAwMDAwMDAwMDB9DQp7NDoNCjozMDA6OTYwMDAxDQo6MzQ0OjIxj66r4+eo4uwgpKCtreulfXs1OntGQUM6MzA4MjAxNjQwNjA5MkE4NjQ4ODZGNzBEMDEwNzAyQTA4MjAxNTUzMDgyMDE1MTAyMDEwMTMxMEYzMDBEMDYwOTJCMDYwMTA0MDE5QzU2MDEwMTA1MDAzMDBCMDYwOTJBODY0ODg2RjcwRDAxMDcwMTMxODIwMTJDMzA4MjAxMjgwMjAxMDEzMDU4MzA0NDMxMEIzMDA5MDYwMzU1MDQwNjEzMDI1MjU1MzEwQjMwMDkwNjAzNTUwNDA4MTMwMjMwMzQzMTBDMzAwQTA2MDM1NTA0MEExMzAzNDM0MjUyMzEwRDMwMEIwNjAzNTUwNDBCMTMwNDU1NDI1QTQ5MzEwQjMwMDkwNjAzNTUwNDAzMTMwMjQzNDEwMjEwNDAzNjEwQjc3RDBCRDVBRUI3NDI3N0QxNTUyMUYxRTAzMDBEMDYwOTJCMDYwMTA0MDE5QzU2MDEwMTA1MDBBMDY5MzAxODA2MDkyQTg2NDg4NkY3MEQwMTA5MDMzMTBCMDYwOTJBODY0ODg2RjcwRDAxMDcwMTMwMUMwNjA5MkE4NjQ4ODZGNzBEMDEwOTA1MzEwRjE3MEQzMTM1MzEzMjMxMzYzMTM5MzUzMDMxMzM1QTMwMkYwNjA5MkE4NjQ4ODZGNzBEMDEwOTA0MzEyMjA0MjAzREFBNzk0OUM2MzE0NkNGRThCQkI3MEM2NUQ5RDQ5NkUxODlFNTgyNTk1Q0ZEMzUyNjkxRDAzQjBCQUQzQTQwMzAwRDA2MDkyQjA2MDEwNDAxOUM1NjAxMDIwNTAwMDQ0MDkyOEUwRDlEOTUyOEE2Nzc2RUVGRkU5NjcwNzlFNEE2ODI2OTQ0MDcyOTFGNkMwMzRDMTU5MjVGQjAwRTUwNzM3MDQyQjVBRkE0NDYxRDJENEZDQzg0NkQzQjRBM0E4NkVGQUE0ODhDODgxRjdFOEJEODVCMEJGMEY1QjlCQUU0fX0=</Object>\n"
               +"   </env:Body>\n"
               +"</env:Envelope>\n"
               +"\n"
               ;
       public static final String def_msg2=""
               +"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">\n"
               +"  <env:Header>\n"
               +"    <props:MessageInfo xmlns:props=\"urn:cbr-ru:msg:props:v1.3\">\n"
               +"      <props:To>uic:451111111100</props:To>\n"
               +"      <props:From>uic:452222222211</props:From>\n"
               +"      <props:AppMessageID>guid:sat-00000094.22765.6577674.2202603.1.SYS.FAST.PAYMENT.xml-unicend</props:AppMessageID>\n"
               +"      <props:MessageID>guid:sat-00000094.22765.6577674.2202603.1.SYS.FAST.PAYMENT.xml-unicend</props:MessageID>\n"
               +"      <props:MessageType>1</props:MessageType>\n"
               +"      <props:Priority>5</props:Priority>\n"
               +"      <props:CreateTime>2018-08-10T05:07:01Z</props:CreateTime>\n"
               +"      <props:AckRequest>true</props:AckRequest>\n"
               +"    </props:MessageInfo>\n"
               +"    <props:DocInfo xmlns:props=\"urn:cbr-ru:msg:props:v1.3\">\n"
               +"      <props:DocFormat>1</props:DocFormat>\n"
               +"      <props:DocType>ED701</props:DocType>\n"
               +"      <props:EDRefID EDNo=\"302146116\" EDDate=\"2018-08-10\" EDAuthor=\"4522222222\"/>\n"
               +"    </props:DocInfo>\n"
               +"  </env:Header>\n"
               +"  <env:Body>\n"
               +"    <sen:SigEnvelope xmlns:sen=\"urn:cbr-ru:dsig:env:v1.1\">\n"
               +"      <sen:SigContainer>\n"
               +"        <dsig:MACValue xmlns:dsig=\"urn:cbr-ru:dsig:v1.1\">MIIBYAYJKoZIhvcNAQcCoIIBUTCCAU0CAQExDjAMBggqhQMHAQECAgUAMAsGCSqGSIb3DQEHATGCASkwggElAgEBMFcwQzELMAkGA1UEBhMCUlUxCzAJBgNVBAgTAjQ1MQwwCgYDVQQKEwNDQlIxDDAKBgNVBAsTA01DSTELMAkGA1UEAxMCQ0ECEEBQFMBruXkZhaYgSluWSYwwDAYIKoUDBwEBAgIFAKBpMBgGCSqGSIb3DQEJAzELBgkqhkiG9w0BBwEwHAYJKoZIhvcNAQkFMQ8XDTE4MTAyOTIwMzMxNFowLwYJKoZIhvcNAQkEMSIEIAaN22pzpScqBhna1r0zNf5AsTqQTnVRbIV+ngkArGrOMAwGCCqFAwcBAQEBBQAEQD25HZ3y1oLv6we0Lal8C81WnGgOCDFVxx0GOhcnOJl+5uS/duZbmi8o8wlLrXDyMObnMwblLqUK0HvbNM1XCRw=</dsig:MACValue>\n"
               +"      </sen:SigContainer>\n"
               +"      <sen:Object>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</sen:Object>\n"
               +"    </sen:SigEnvelope>\n"
               +"  </env:Body>\n"
               +"</env:Envelope>\n"
               +"\n"
               ;

}
