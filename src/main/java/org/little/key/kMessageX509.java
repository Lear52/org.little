package org.little.key;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CRLEntryHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;

import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import org.little.util.Logger;
import org.little.util.LoggerFactory;



public class kMessageX509 {
       private static final Logger  logger  = LoggerFactory.getLogger(kMessageX509.class);
       private static boolean       debug   = false; 

       private static String checkPEMType(String type) {
               
           if("CERTIFICATE"            .equals(type)){ logger.trace("PEM ("+type+") Ok!"); return "CERTIFICATE";}
           if("X509 CERTIFICATE"       .equals(type)){ logger.trace("PEM ("+type+") Ok!"); return "CERTIFICATE";}
           if("PRIVATE KEY"            .equals(type)){ logger.trace("PEM ("+type+") Ok!"); return "PRIVATE KEY";}
           if("RSA PRIVATE KEY"        .equals(type)){ logger.trace("PEM ("+type+") Ok!"); return "PRIVATE KEY";}
           if("NEW CERTIFICATE REQUEST".equals(type)){ logger.trace("PEM ("+type+") Ok!"); return "CERTIFICATE REQUEST";}
           if("CERTIFICATE REQUEST"    .equals(type)){ logger.trace("PEM ("+type+") Ok!"); return "CERTIFICATE REQUEST";}
           if("X509 CRL"               .equals(type)){ logger.trace("PEM ("+type+") Ok!"); return "X509 CRL";}
           logger.trace("PEM UNKNOW ("+type+")!");
           return null;   
               
       }
 
       private static kMessage parsePEM2MSG(kMessage msg,byte [] buf){
              InputStream       in    =null;
              InputStreamReader is    =null;
              PEMParser         parser=null;
              PemReader         reader=null;
              String            type =null;
              try {
                   in     = new ByteArrayInputStream(buf);
                   is     = new InputStreamReader(in);
                   reader = new PemReader(is);
                   PemObject pem_obj=reader.readPemObject();
                   if(pem_obj==null)return null;
                   type   =pem_obj.getType();
                   type=checkPEMType(type);
                   if(type==null)return null;
              }
              catch(Exception e){
                    if(debug) { 
                       Except ex=new Except(e);
                       logger.error("DER NO! ex:"+ex);
                    }
                    return null;
              }
              finally {
                  if(reader!=null)try {reader.close();} catch (IOException e) {}
                  if(is    !=null)try {is.close();    } catch (IOException e) {}
                  if(in    !=null)try {in.close();    } catch (IOException e) {}
              }

              in     = new ByteArrayInputStream(buf);
              is     = new InputStreamReader(in);
              parser = new PEMParser(is);

              if("CERTIFICATE".equals(type)){
                  try {
                      Object parsedObj = parser.readObject();
                      if(parsedObj==null)return null;
                      if(parsedObj instanceof X509CertificateHolder) {
                        X509CertificateHolder holder = (X509CertificateHolder) parsedObj;
                        //if(holder == null) return null;
                        BigInteger n=holder.getSerialNumber();
                        X500Name   issuer =holder.getIssuer(); 
                        X500Name   subject=holder.getSubject();
                        Date       start=holder.getNotBefore();
                        Date       end=holder.getNotAfter();   
                 
                        msg.setX509Type     (type);
                        msg.setX509TypeFile ("PEM");
                        msg.setX509BeginDate(start             );
                        msg.setX509EndDate  (end               );
                        msg.setX509Serial   (n.toString      ());
                        msg.setX509Subject  (subject.toString());
                        msg.setX509Issuer   (issuer.toString ());
                      }
                      else return null;
                  } 
                  catch(Exception e){
                        if(debug) { 
                                 Except ex=new Except(e);
                                 logger.error("DER NO! ex:"+ex);
                        }
                        return null;
                  }
                  finally {
                      if(parser!=null)try {parser.close(); } catch (Exception e){}
                      if(is    !=null)try {is.close();     } catch (Exception e){}
                      if(in    !=null)try {in.close();     } catch (Exception e){}
                  }
               }
               else
               if("X509 CRL".equals(type)){
                      try {
                      Object parsedObj = parser.readObject();
                      if(parsedObj==null)return null;
                      if (parsedObj instanceof X509CRLHolder) {
                          X509CRLHolder crl = (X509CRLHolder) parsedObj;
                          String issuer=crl.getIssuer().toString();
                          Iterator<?> crl_number=crl.getRevokedCertificates().iterator();
                              
                          String n_str="";
                          while(crl_number.hasNext()){
                                X509CRLEntryHolder p=(X509CRLEntryHolder)crl_number.next();
                                n_str+=p.getSerialNumber().toString()+";";           
                          }
                            
                          msg.setX509Type     (type);
                          msg.setX509TypeFile ("PEM");
                          msg.setX509BeginDate(new Date());
                          msg.setX509EndDate  (new Date());
                          msg.setX509Serial   (n_str);
                          msg.setX509Subject  (issuer);
                          msg.setX509Issuer   (issuer);
                      }
                      else return null;
                      
                   } 
                   catch(Exception e) {
                         if(debug) { 
                            Except ex=new Except(e);
                            logger.error("DER NO! ex:"+ex);
                         }
                         return null;
                  }
                  finally {
                      if(parser!=null)try {parser.close(); } catch (Exception e){}
                      if(is    !=null)try {is.close();     } catch (Exception e){}
                      if(in    !=null)try {in.close();     } catch (Exception e){}
                  }
               }
               else
               if("CERTIFICATE REQUEST".equals(type)){
                  try {
                       Object parsedObj = parser.readObject();
                       if(parsedObj==null)return null;
                       if(parsedObj instanceof PKCS10CertificationRequest) {
                          PKCS10CertificationRequest csr = (PKCS10CertificationRequest) parsedObj;
                          //System.out.println("PKCS10CertificationRequest:"+csr);
                          String subject =csr.getSubject().toString();
                          
                          Attribute []  attr=csr.getAttributes();
                          for(int i=0;i<attr.length;i++) {
                        	  Attribute att=attr[i];
                        	  ASN1ObjectIdentifier t = att.getAttrType();
                        	  ASN1Set v = att.getAttrValues();
                        	  String txt="type:"+t.getId()+" v:"+v.toString();
                        	  msg.appendX509attrib(txt);
                        	  logger.trace("attr["+i+"]="+txt);
                                  System.out.println("A>>"+txt);
                          }
                          byte [] b_en=csr.getEncoded();
                          byte [] b_si=csr.getSignature();
                          msg.appendX509attrib("Encoded:"+stringTransform.getHex(b_en));
                          msg.appendX509attrib("Signature:"+stringTransform.getHex(b_si));
                          
                          msg.setX509Type     (type);
                          msg.setX509TypeFile ("PEM");
                          msg.setX509BeginDate(new Date());
                          msg.setX509EndDate  (new Date());
                          msg.setX509Serial   ("0");
                          msg.setX509Subject  (subject);
                          msg.setX509Issuer   ("");
                       }
                       else return null;
                  } 
                  catch(Exception e) {
                        if(debug) { 
                           Except ex=new Except(e);
                           logger.error("DER NO! ex:"+ex);
                        }
                   }
                   finally {
                       if(parser!=null)try {parser.close(); } catch (Exception e){}
                       if(is    !=null)try {is.close();     } catch (Exception e){}
                       if(in    !=null)try {in.close();     } catch (Exception e){}
                   }
               }
               else{
                   return null;
               }
               logger.trace(type+" DER Ok!");
               return msg;

       }
       //---------------------------------------------------------------------------------------------------------------
       //  X.509 Certificate
       private static kMessage parseX509CERDER2MSG(kMessage msg,byte [] buf){
               String          type="CERTIFICATE";
               InputStream     in  =null;
               X509Certificate cert=null;
               try{
                   try{
                       CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                       in   = new ByteArrayInputStream(buf);
                       cert = (X509Certificate) certFactory.generateCertificate(in);
                   }
                   catch (Exception e){
                          if(debug) {
                             Except ex=new Except(e);
                             logger.error("DER NO! ex:"+ex);
                          }
                          return null;
                   }
                   try{
                       BigInteger n      =cert.getSerialNumber();
                       String     n_str=n.toString      ();
                       String     issuer;
                       issuer  =  cert.getIssuerX500Principal().toString();
                       issuer  =  cert.getIssuerDN().toString().toString();
                       String     subject;
                       subject =  cert.getSubjectX500Principal().toString();
                       subject =  cert.getSubjectDN().toString();
                       Date       start  =cert.getNotBefore();
                       Date       end    =cert.getNotAfter();   
                        
                       msg.setX509Type     (type   );
                       msg.setX509TypeFile ("DER"  );
                       msg.setX509BeginDate(start  );
                       msg.setX509EndDate  (end    );
                       msg.setX509Serial   (n_str  );
                       msg.setX509Subject  (subject); 
                       msg.setX509Issuer   (issuer );
                   }
                   catch (Exception e){
                           if(debug) {                  
                             Except ex=new Except(e);
                             logger.error("DER NO! ex:"+ex);
                           }
                         return null;
                   }
                      
               }     
               finally {
                  if(in!=null)try {in.close();     } catch (Exception e){}
               }
               logger.trace(type+" DER Ok!");
               return msg;
              
       }
       
       //Certificate Revocation List (CRL)
       private static kMessage parseX509CRLDER2MSG(kMessage msg,byte [] buf){
               String type="X509 CRL";
               InputStream in=null;
               X509CRL crl=null; 
               try{
                  logger.trace("start parseX509CRLDER2MSG");
                  try{
                      CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                      in=new ByteArrayInputStream(buf);
                      crl = (X509CRL)certFactory.generateCRL(in);
                  }
                  catch (Exception e){
                         if(debug) {
                            Except ex=new Except(e);
                            logger.error("DER NO! ex:"+ex);
                         }
                         return null;
                  }
                  logger.trace("certFactory.generateCRL(in)");
                  String n_str="";
                  try{
                      Iterator<? extends X509CRLEntry> crl_number=crl.getRevokedCertificates().iterator();
                      int count=0;
                      while(crl_number.hasNext()){
                            X509CRLEntry p=crl_number.next();
                            n_str+=p.getSerialNumber().toString()+";";           
                            count++;
                      }
                      logger.trace("getSerialNumber:"+count);
                  }
                  catch(Exception e){
                        if(debug) {
                           Except ex=new Except(e);
                           logger.error("DER NO! ex:"+ex);
                        }
                        //return null;
                  }
                  try{
                              
                      String     issuer0;
                      String     issuer1;
                      issuer0  = crl.getIssuerX500Principal().toString();
                      issuer1  = crl.getIssuerDN().toString().toString();
                      String    subject=issuer1;
                                     
                      Date       start  =crl.getThisUpdate();
                      Date       end    =crl.getNextUpdate();   
                       
                      msg.setX509Type     (type      );
                      msg.setX509TypeFile ("DER"     );
                      msg.setX509BeginDate(start     );
                      msg.setX509EndDate  (end       );
                      msg.setX509Serial   (n_str     );
                      msg.setX509Subject  (subject   ); 
                      msg.setX509Issuer   (issuer0   );
                  }
                  catch(Exception e){
                        if(debug) {
                           Except ex=new Except(e);
                           logger.error("DER NO! ex:"+ex);
                        }
                        return null;
                  }

               }     
               finally {
                   if(in!=null)try {in.close();     } catch (Exception e){}
               }
               logger.trace(type+" DER Ok!");
               return msg;
       }

       public static String getX500Field(String asn1ObjectIdentifier, X500Name x500Name) {
    	    RDN[] rdnArray = x500Name.getRDNs(new ASN1ObjectIdentifier(asn1ObjectIdentifier));

    	    String retVal = null;
    	    for (RDN item : rdnArray) {
    	        retVal = item.getFirst().getValue().toString();
    	    }
    	    return retVal;
       }
       /*
       private static final String COUNTRY = "2.5.4.6";
       private static final String STATE = "2.5.4.8";
       private static final String LOCALE = "2.5.4.7";
       private static final String ORGANIZATION = "2.5.4.10";
       private static final String ORGANIZATION_UNIT = "2.5.4.11";
       private static final String COMMON_NAME = "2.5.4.3";
       private static final String EMAIL = "2.5.4.9";
       */       
       //  Certificate Request Message
       private static kMessage parseX509CSRDER2MSG(kMessage msg,byte [] buf){
               String type="CERTIFICATE REQUEST";
               //if(true)
               try {
                      JcaPKCS10CertificationRequest csr = new JcaPKCS10CertificationRequest(buf);
                      String subject=csr.getSubject().toString();

                      csr.getSubjectPublicKeyInfo();

                      X500Name x500Name = csr.getSubject();
                      System.out.println("x500Name is: " + x500Name + "\n");
                      RDN[] _cn = x500Name.getRDNs(BCStyle.EmailAddress);
                      if(_cn==null) {
                    	  System.out.println("getRDNs is null");
                      }
                      else 
                      if(_cn.length>0){
                    	  System.out.println("getRDNs is "+_cn.length);	  
                          RDN c_cn = _cn[0];
                          if(c_cn!=null)System.out.println(c_cn.getFirst().getValue().toString());
                      }
                      
                      //System.out.println("COUNTRY: " + getX500Field(COUNTRY, x500Name));
                      //System.out.println("STATE: " + getX500Field(STATE, x500Name));
                      //System.out.println("LOCALE: " + getX500Field(LOCALE, x500Name));
                      //System.out.println("ORGANIZATION: " + getX500Field(ORGANIZATION, x500Name));
                      //System.out.println("ORGANIZATION_UNIT: " + getX500Field(ORGANIZATION_UNIT, x500Name));
                      //System.out.println("COMMON_NAME: " + getX500Field(COMMON_NAME, x500Name));
                      //System.out.println("EMAIL: " + getX500Field(EMAIL, x500Name));
                      
                      
                      Attribute []  attr=csr.getAttributes();

                      for(int i=0;i<attr.length;i++) {
                      	  Attribute att=attr[i];
                          System.out.println("attr["+i+"]"+att.getClass().getName());

                      	  ASN1ObjectIdentifier t = att.getAttrType();
                      	  ASN1Set v = att.getAttrValues();
                      	  String txt="type:"+t.getId()+" v:"+v.toString();
                          System.out.println(txt);

                          ASN1Encodable[]  arr_v=v.toArray();
                          System.out.println("attr["+i+"].v len:"+arr_v.length);


                      	  //msg.appendX509attrib(txt);
                      	  //logger.trace("attr["+i+"]="+txt);
                          //System.out.println("A>>"+txt);
                          System.out.println("______________________________");
                      	 
                      	  Iterator<ASN1Encodable> list = v.iterator();
                      	  while(list.hasNext()) {
                      		  ASN1Encodable a = list.next();
                      		  System.out.println("a class:"+a.getClass().getName());
                      		  DERSequence aa=(DERSequence)a;
                      		  System.out.println("a class:"+aa.getClass().getName()+" size:"+aa.size());
                                  ASN1Encodable[]  arr_aa=aa.toArray();
                                  for(int ii=0;ii<arr_aa.length;ii++){
                                      ASN1Encodable aaa=arr_aa[ii];
                                      System.out.println("aaa class:"+aaa.getClass().getName()+"  txt:"+aaa.toString());
                      		      DERSequence aaaa=(DERSequence)aaa;
                                      System.out.println("aaaa class:"+aaaa.getClass().getName()+" size:"+aaaa.size());
                                      ASN1Encodable[]  arr_aaaa=aaaa.toArray();
                                      for(int iii=0;iii<aaaa.size();iii++){
                                          ASN1Encodable aaaaa=arr_aaaa[ii];
                                          System.out.println("aaaaa class:"+aaaaa.getClass().getName());
                                          System.out.println("txt:"+aaaaa.toString());
                                          //DEROctetString aaaaaa=(DEROctetString)aaaaa;
                                          //ASN1Primitive a7=aaaaaa.toDERObject();
                                          //System.out.println("a7 class:"+a7.getClass().getName()+"txt:"+a7.toString());

                                      }
                                  }
                      		  
                      	  }
                          System.out.println("______________________________");
                      	  
                          //System.out.println("A>>"+txt);
                      }
                      byte [] b_en=csr.getEncoded();
                      byte [] b_si=csr.getSignature();
                      String en=stringTransform.getHex(b_en);   msg.appendX509attrib("Encoded:"+en);   logger.trace("attr[Encoded]="+en);
                      String si=stringTransform.getHex(b_si);   msg.appendX509attrib("Signature:"+si); logger.trace("attr[Signature]="+si);
                      
                      msg.setX509Type     (type      );
                      msg.setX509TypeFile ("DER"     );
                      msg.setX509BeginDate(new Date());
                      msg.setX509EndDate  (new Date());
                      msg.setX509Serial   ("0");
                      msg.setX509Subject  (subject   ); 
                      msg.setX509Issuer   (" "       );
               }
               catch(Exception e){
                     if(debug) {
                        Except ex=new Except(e);
                        logger.error("DER NO! ex:"+ex);
                     }
             		 System.out.println("ex:"+e);
                     return null;
               }
               logger.trace(type+" DER Ok!");
               return msg;
          
       }
       /*
       private static kMessage parsePEM2MSG(kMessage msg,final InputStream in){
               byte[] buf=null;
               try{buf=_ByteBuilder.toByte(in);}
               catch (Exception e){
                     if(debug) {
                        Except ex=new Except(e);
                        logger.error("DER NO! ex:"+ex);
                     }
                     return null;
               }
               return parsePEM2MSG(msg,buf);
       }
       private static kMessage parseX509CERDER2MSG(kMessage msg,final InputStream in){
               byte[] buf=null;
               try{buf=_ByteBuilder.toByte(in);}
               catch (Exception e){
                     if(debug) {
                        Except ex=new Except(e);
                        logger.error("DER NO! ex:"+ex);
                     }
                     return null;
               }
               return parseX509CERDER2MSG(msg,buf);
       }
       private static kMessage parseX509CRLDER2MSG(kMessage msg,final InputStream in){
               byte[] buf=null;
               try{buf=_ByteBuilder.toByte(in);}
               catch (Exception e){
                     if(debug) {
                        Except ex=new Except(e);
                        logger.error("DER NO! ex:"+ex);
                     }
                     return null;
               }
               return parseX509CRLDER2MSG(msg,buf);
       }
       
       private static kMessage parseX509CSRDER2MSG(kMessage msg,final InputStream in){
               byte[] buf=null;
               try{buf=_ByteBuilder.toByte(in);}
               catch (Exception e){
                     if(debug) {
                        Except ex=new Except(e);
                        logger.error("DER NO! ex:"+ex);
                     }
                     return null;
               }
               return parseX509CSRDER2MSG(msg,buf);  
        }
        */
        public static kMessage parse(kMessage msg) {
               byte[]   bin_buffer=msg.getBodyBin();
               kMessage ret=null;
               ret=parsePEM2MSG(msg,bin_buffer);
               logger.trace("parsePEM2MSG ret:"+ret);
               if(ret==null){
                  ret=parseX509CERDER2MSG(msg,bin_buffer);
                  logger.trace("parseX509CERDER2MSG ret:"+ret);
               }
               if(ret==null){
                  ret=parseX509CRLDER2MSG(msg,bin_buffer);
                  logger.trace("parseX509CRLDER2MSG ret:"+ret);
               }
               if(ret==null){
                  ret=parseX509CSRDER2MSG(msg,bin_buffer);
                  logger.trace("parseX509CSRDER2MSG ret:"+ret);
               }
               return ret;
       }

       public static void main(String[] args) {
              FileInputStream in;
              kMessage msg=new kMessage();

              logger.trace("start print key ");
              byte[] buf=null;
              try{
                  in=new FileInputStream(args[0]);
                  buf=_ByteBuilder.toByte(in);
              }
              catch (Exception e){
                     logger.error("ex:"+e);
                     return ;
              }
              logger.trace("load key length:"+buf.length);
              msg.setBodyBin(buf);
              msg=parse(msg);
              //---------------------------------------------------------------------------------------------------
              if(msg!=null)System.out.println("msg:"+msg.printx509());
              else         System.out.println("msg:null");

       }

}
