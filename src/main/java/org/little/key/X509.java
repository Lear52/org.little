package org.little.key;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
//import java.math.BigInteger;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
//import java.util.Date;

import org.little.util.LogHexDump;


public class X509 {
       //---------------------------------------------------------------------------------------------------------------
       //  X.509 Certificate
       public static void parseX509CERDER2MSG(byte [] buf){
               //String          type="CERTIFICATE";
               InputStream     in  =null;
               X509Certificate cert=null;
               try{
                   try{
                       CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                       in   = new ByteArrayInputStream(buf);
                       cert = (X509Certificate) certFactory.generateCertificate(in);
                   }
                   catch (Exception e){
                             System.out.println("DER NO! e:"+e.getClass().getName());
                             Except ex=new Except(e);
                             System.out.println("DER NO! ex:"+ex);
                          return ;
                   }
                   try{
                       //BigInteger n      =cert.getSerialNumber();
                       //String     n_str=n.toString      ();
                       //String     issuer;
                       //issuer  =  cert.getIssuerX500Principal().toString();
                       //issuer  =  cert.getIssuerDN().toString().toString();
                       String     subject;
                       subject =  cert.getSubjectX500Principal().toString();
                       subject =  cert.getSubjectDN().toString();
                      // Date       start  =cert.getNotBefore();
                      // Date       end    =cert.getNotAfter();   
                        
                   System.out.println("DER Ok!");
                   System.out.println("subject:"+subject);
                   try{LogHexDump.dump(buf);}catch (Exception e111){}
                   }
                   catch (Exception e){
                             System.out.println("DER NO! e:"+e.getClass().getName());
                             Except ex=new Except(e);
                             System.out.println("DER NO! ex:"+ex);
                         return ;
                   }

                      
               }     
               finally {
                  if(in!=null)try {in.close();     } catch (Exception e){}
               }
              
       }
       

       public static void main(String[] args) {
              FileInputStream in;

              byte[] buf=null;
              try{
                  in=new FileInputStream(args[0]);
                  buf=_ByteBuilder.toByte(in);
              }
              catch (Exception e){
                     System.out.println("ex:"+e);
                     return ;
              }

              parseX509CERDER2MSG(buf);

       }

}
