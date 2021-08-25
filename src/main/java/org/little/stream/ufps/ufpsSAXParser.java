package org.little.stream.ufps;
               
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class ufpsSAXParser {
       private static final Logger     logger    = LoggerFactory.getLogger(ufpsSAXParser.class);
       private static XMLInputFactory  ifactory  = XMLInputFactory.newInstance();
       private static XMLOutputFactory ofactory  = XMLOutputFactory.newInstance();

       private Writer                out              ;
       private XMLStreamWriter       xml_writer       ;
       private XMLStreamReader       xml_reader       ;
       private ufpsMsg               msg              ;            
       private boolean               is_parse         ;
       private boolean               is_del           ;
       //private String                current_element  ;
       //private String                current_namespace;
       //private String                current_uri      ;

       private String                after_element    ;
       private String                add_element      ;
       private String                add_element_data ;
       
       public ufpsSAXParser(ufpsMsg msg,byte[] buf,String after_element,String element,String element_data){ // add element after  element
              init(msg,buf,1,after_element,element,element_data);
       }
       public ufpsSAXParser(ufpsMsg msg,byte[] buf,String element){ //del element
              init(msg,buf,-1,null,element,null);
       }
       public ufpsSAXParser(ufpsMsg msg,byte[] buf){ //no change
              init(msg,buf,0,null,null,null);
       }

       private void init(ufpsMsg msg,byte[] buf,int element_add,String _after_element,String element,String element_data){
                  ByteArrayInputStream in  = new ByteArrayInputStream(buf);
               out = null;
               this.msg         = msg;
               msg.setBuf(buf);
               is_del           = false;
               is_parse         = true;
               xml_reader       = null;
               xml_writer       = null;
               //current_element  = null;
               //current_namespace= null;
               //current_uri      = null;      
               try {
                    xml_reader = ifactory.createXMLStreamReader(in);
               } 
               catch (XMLStreamException e) {
                      in          = null;
                      xml_reader  = null;
                      logger.error(""+new Except("Error createXMLStreamReader",e));
                      return;
               }
              
               if(element_add!=0)add_element      =element;      // add or del
               else              add_element      =null;
               if(element_add>0 )add_element_data =element_data;// add
               else              add_element_data =null;        // del
               after_element     =_after_element;
              
               if(element_add!=0) {
                  out  = new StringWriter();
                  try {
                      xml_writer = ofactory.createXMLStreamWriter(out);
                  } 
                  catch (XMLStreamException e) {
                         in          = null;
                         xml_reader  = null;
                         xml_writer  = null;
                         out         = null;
                         logger.error(""+new Except("Error createXMLStreamWriter",e));
                         return;
                  }
               } 

       }

       private void setElement(String element,String data){//ufpsMsg msg,,, XMLStreamWriter xml_writer
               if(is_parse==false)return;
               if(element==null)return;
               if(data==null)return;

               //logger.trace("elenment:"+element+" data:"+data);

               //if(element.equalsIgnoreCase(ufpsDef.H_HEADER                  )){}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_BODY                      )){is_parse=false;         return;}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_NAME_TO                   )){msg.addTO(data)        ;return;}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_NAME_FROM                 )){msg.setFROM(data)      ;return;}else
               //if(element.equalsIgnoreCase(def.H_MESSAGE_INFO_TAG          )){}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_MESSAGE_TYPE              )){msg.setType(data)      ;return;}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_PRIORITY                  )){msg.setPriority(data)  ;return;}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_MESSAGE_ID                )){msg.setID(data)        ;return;}else
               //if(element.equalsIgnoreCase(ufpsDef.H_LEGACY_TRANSPORT_FILE_NAME)){}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_APPLICATION_MESSAGE_ID    )){msg.setAppID(data)     ;return;}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_CORRELATION_MESSAGE_ID    )){msg.setCorID(data)     ;return;}else
              
               if(element.equalsIgnoreCase(ufpsMsgField.H_CREATE_TIME               )){msg.setCreateTime(data);return;}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_SEND_TIME                 )){msg.setSendTime(data)  ;return;}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_RECEIVE_TIME              )){msg.setReceveTime(data);return;}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_ACCEPT_TIME               )){msg.setAcceptTime(data);return;}else
              
               if(element.equalsIgnoreCase(ufpsMsgField.H_ACKNOLEDGE_REQUEST        )){msg.setAckRequest(data);return;}else

               if(element.equalsIgnoreCase(ufpsMsgField.H_DOC_FORMAT                )){msg.setDocFormat (data);return;}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_DOC_TYPE                  )){msg.setDocType   (data);return;}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_DOC_ID                    )){msg.setDocID     (data);return;}else
               //if(element.equalsIgnoreCase(ufpsMsgField.H_DOC_REFID                 )){}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_DOC_EDNO                  )){msg.setDocEDNO       (data);}else  
               if(element.equalsIgnoreCase(ufpsMsgField.H_DOC_EDDATE                )){msg.setDocEDDate     (data);}else
               if(element.equalsIgnoreCase(ufpsMsgField.H_DOC_EDAUTHOR              )){msg.setDocEDAutor    (data);}else
               {}
               //logger.trace("msg:"+msg.toString());
         
       }
       private void endElement(String element){
               if(element==null)return;
               if(element.equalsIgnoreCase(ufpsMsgField.H_HEADER                    )){is_parse=false;}else
               /*
               if(element.equalsIgnoreCase(ufpsDef.H_NAME_TO                   )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_NAME_FROM                 )){}else
               //if(element.equalsIgnoreCase(def.H_MESSAGE_INFO_TAG          )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_MESSAGE_TYPE              )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_PRIORITY                  )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_MESSAGE_ID                )){}else
               //if(element.equalsIgnoreCase(def.H_LEGACY_TRANSPORT_FILE_NAME)){}else
               if(element.equalsIgnoreCase(ufpsDef.H_CREATE_TIME               )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_APPLICATION_MESSAGE_ID    )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_CORRELATION_MESSAGE_ID    )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_SEND_TIME                 )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_RECEIVE_TIME              )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_ACCEPT_TIME               )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_ACKNOLEDGE_REQUEST        )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_DOC_FORMAT                )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_DOC_TYPE                  )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_DOC_ID                    )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_DOC_REFID                 )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_DOC_EDNO                  )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_DOC_EDDATE                )){}else
               if(element.equalsIgnoreCase(ufpsDef.H_DOC_EDAUTHOR              )){}else
               */
               {}
         
       }
       private void addElement(String _current_element,String current_namespace,String current_uri) {
               if(xml_writer==null)return;
               if(_current_element==null)return;
               if(after_element==null)return;
               if(_current_element.equalsIgnoreCase(after_element)){
                   try {
                        xml_writer.writeStartElement(current_namespace, add_element, current_uri);
                        xml_writer.writeCharacters(add_element_data);
                        xml_writer.writeEndElement();
                   }  catch (XMLStreamException e) {
                      logger.error(""+new Except("XML parsing",e));
                   }
               }
       }
       private boolean delElement(String _current_element){
               if(xml_writer==null)return false;
               if(_current_element==null)return false;
               if(add_element_data==null || add_element==null)return false;
               if(_current_element.equalsIgnoreCase(add_element))return true;
               return false;
       }

       public ufpsMsg parse() {
              String current_element=null;
              String current_namespace=null;
              String current_uri=null;
              if(xml_reader  ==null)return null;
              try {
                  int     event            = xml_reader.getEventType();
                  boolean is_run           =true;
 
                  while (is_run) {
                         QName obj=null;
                         switch (event) {
                         case XMLStreamConstants.START_DOCUMENT:
                              if(xml_writer!=null && !is_del){
                                 String encoding = xml_reader.getCharacterEncodingScheme();
                                 if(encoding==null)encoding="UTF-8";
                                 String ver=xml_reader.getVersion();
                                 if(ver==null)ver="1.0";
                                 xml_writer.writeStartDocument(encoding,ver);
                              }
                              break;
                         case XMLStreamConstants.START_ELEMENT:
                              {//---------------------------------------------------------------------------------------------------------
                              obj              = xml_reader.getName();
                              current_namespace= obj.getPrefix();
                              current_element  = obj.getLocalPart();
                              current_uri      = obj.getNamespaceURI();
                              is_del           = delElement(current_element);

                              //---------------------------------------------------------------------------------------------------------
                              if(xml_writer!=null && !is_del)xml_writer.writeStartElement(current_namespace, current_element, current_uri);
                              //---------------------------------------------------------------------------------------------------------
                              if(xml_writer!=null && !is_del){
                                for (int i = 1; i < xml_reader.getNamespaceCount(); i++) {
                                  String uri = xml_reader.getAttributeNamespace(i);
                                  if(uri==null)   uri=obj.getNamespaceURI();
                                  xml_writer.writeNamespace(current_namespace,  uri);
                                }
                              }   
                              //---------------------------------------------------------------------------------------------------------
                              {
                                for (int i = 0; i < xml_reader.getAttributeCount(); i++) {
                                  String uri = xml_reader.getAttributeNamespace(i);
                                  if(uri==null)   uri="";
                                  String localName = xml_reader.getAttributeLocalName(i);
                                  String prefix = xml_reader.getAttributePrefix(i);
                                  String qName;
                                  if(prefix==null || prefix.length()==0)qName = localName;
                                  else  qName = prefix + ':' + localName;
                                  String value = xml_reader.getAttributeValue(i);
                                  if(xml_writer!=null && !is_del)xml_writer.writeAttribute(qName, value);

                                  setElement(localName,value);

                                }
                              }
                              
                              //---------------------------------------------------------------------------------------------------------
                              }
                              break;
                         case XMLStreamConstants.CHARACTERS:
                              if(!is_del){
                                 setElement(current_element,xml_reader.getText());//msg,,xml_writer
                                 current_element=null;
                              }
                              if(xml_writer!=null && !is_del){
                                 xml_writer.writeCharacters(xml_reader.getText());
                              }
                              break;
                         case XMLStreamConstants.END_ELEMENT:
                             {
                              obj      =xml_reader.getName();
                              String el=obj.getLocalPart();
                              endElement(el);
                              if(xml_writer!=null && !is_del)xml_writer.writeEndElement();
                              if(delElement(el)==true)is_del=false;
                              addElement(el,current_namespace,current_uri);
                             }
                             current_element=null;
                              break;
                         case XMLStreamConstants.END_DOCUMENT:
                              if(xml_writer!=null)xml_writer.writeEndDocument();
                              break;
                         default:
                              logger.error("unknow sax event:"+event);
                              break;
                         }
                         
                         if (xml_reader.hasNext())event = xml_reader.next();
                         else is_run=false;
                  }
                  logger.trace("msg:"+msg.toString());
                  if(xml_writer!=null) {
                     String s_out=out.toString();
                     if(s_out!=null){
                        byte [] b_out=s_out.getBytes();
                        msg.setBuf(b_out);
                     }
                  }

              } 
              catch (XMLStreamException e) {
                     logger.error(""+new Except("XML parsing",e));
                     return null;
              }
              finally {
                   try{xml_reader.close();} catch (XMLStreamException e) {}
                   if(xml_writer!=null)
                   try{xml_writer.close();} catch (XMLStreamException e) {}
                   xml_writer  = null;
                   out         = null;
                   xml_reader  = null;
              }
              return msg;
       }
     
       public static void main(String[] args) throws Except {
              long start=System.currentTimeMillis();
              int  size=1;
              byte [] buf=def_msg2.getBytes();
              ufpsMsg msg=new ufpsMsg(); 
              //System.out.println("-------------------\n");
              //System.out.println(new String(buf));
              //System.out.println("-------------------\n");

              for(int i=0;i<size;i++){
                  ufpsSAXParser sax=new ufpsSAXParser(msg,buf);
                  ufpsMsg msg2=sax.parse();    
                  String s=msg2.toString();    
                  System.out.println("-------------------\n"+s);
              }
              start=System.currentTimeMillis()-start;
              System.out.println("\ntime:"+start+"\n s:"+(double)size/(double)start*1000);

       }
       public static final String def_msg2=""
               +"<?xml version=\"1.1\" encoding=\"Windows-1251\"?>"
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
               +"    <prl:DocInfo xmlns:prl=\"urn:cbr-ru:doc:prl:v1.3\">\n"
               +"      <prl:DocFormat>1</prl:DocFormat>\n"
               +"      <prl:DocType>ED701</prl:DocType>\n"
               +"      <prl:EDRefID EDNo=\"302146116\" EDDate=\"2018-08-10\" EDAuthor=\"4522222222\"/>\n"
               +"    </prl:DocInfo>\n"
               +"  </env:Header>\n"
               +"  <env:Body>\n"
               +"    <sen:SigEnvelope xmlns:sen=\"urn:cbr-ru:dsig:env:v1.1\">\n"
               +"      <sen:SigContainer>\n"
               +"        <dsig:MACValue xmlns:dsig=\"urn:cbr-ru:dsig:v1.1\">MIIBYAYJKoZIhvcNAQcCoIIBUTCCAU0CAQExDjAMBggqhQMHAQECAgUAMAsGCSqGSIb3DQEHATGCASkwggElAgEBMFcwQzELMAkGA1UEBhMCUlUxCzAJBgNVBAgTAjQ1MQwwCgYDVQQKEwNDQlIxDDAKBgNVBAsTA01DSTELMAkGA1UEAxMCQ0ECEEBQFMBruXkZhaYgSluWSYwwDAYIKoUDBwEBAgIFAKBpMBgGCSqGSIb3DQEJAzELBgkqhkiG9w0BBwEwHAYJKoZIhvcNAQkFMQ8XDTE4MTAyOTIwMzMxNFowLwYJKoZIhvcNAQkEMSIEIAaN22pzpScqBhna1r0zNf5AsTqQTnVRbIV+ngkArGrOMAwGCCqFAwcBAQEBBQAEQD25HZ3y1oLv6we0Lal8C81WnGgOCDFVxx0GOhcnOJl+5uS/duZbmi8o8wlLrXDyMObnMwblLqUK0HvbNM1XCRw=</dsig:MACValue>\n"
               +"      </sen:SigContainer>\n"
               +"      <sen:Object>MIIF6gYJKoZIhvcNAQcDoIIF2zCCBdcCAQAxggI/MIIBFwIBADBXMEMxCzAJBgNVBAYTAlJVMQswCQYDVQQIEwI0NTEMMAoGA1UEChMDQ0JSMQwwCgYDVQQLEwNNQ0kxCzAJBgNVBAMTAkNBAhBAUBTAa7l5GYWmIEpblkmMMAwGCCqFAwcBAQEBBQAEgaowgacwKAQgC3in9XmzrdWNpw7WuGGzKR6/XMV7H88skpbgSv0rR4wEBFUsBVugewYHKoUDAgIfAaBmMB8GCCqFAwcBAQEBMBMGByqFAwICJAAGCCqFAwcBAQICA0MABEDbknbstcnOgVGIT94yRrcHRIV0NcXU/h4TStdCum0/nwyY/YVtM9ibo9RoBtxaeqlUU7mBFOAD6O6jDqHov+iZBAhknF3FqpMRRjCCASACAQAwYDBMMQswCQYDVQQGEwJSVTELMAkGA1UECBMCMDAxDzANBgNVBAcTBlJUR1NCUjENMAsGA1UEChMER0NLSTEQMA4GA1UEAxMHQURNSU5DQQIQQFAUwM0SAiCL+H7/W5kZNTAMBggqhQMHAQEBAQUABIGqMIGnMCgEIA5pMFfCBQZ9dKMSDAH3W9+NK9GHhn++0Dp9fTNd7DQ0BATZg9uvoHsGByqFAwICHwGgZjAfBggqhQMHAQEBATATBgcqhQMCAiQABggqhQMHAQECAgNDAARA8wzjHY+TFrZqmU3OLh/T40TWieRVIzQ+SdBF3sxfKc7RDvLjxd957ZZoHLkFshSJmaMHKDZoCXqJbPU5o4q+9AQIZLc2o1vikjMwggONBgkqhkiG9w0BBwEwHQYGKoUDAgIVMBMECE/s9dw+cfIeBgcqhQMCAh8BgIIDXyCBDLV3o9whu0ouhXmFlh+MXUG6CA2nqs+u1CU0L/DfrvbVZB9scu1i07a4zGCHJfMBalmhKgndq7jWQ/tTx5Xt+ny8SV+R9HPuGSQy6Q+McHGRLSYNQBQoL0IA2LCPoQ/rD+x7OUxW+r+hPqL8CAW0hv18J05QeFAPm8x1jCf9XF5p+QsAEf/V+3ugZI3f7S/soSGxLdvm/rHvCRgYv95Tl8LaLHrr9fA95KUGtW6nFs0WKPl2k470YiWLy+rzpK8uZCmAxIIAP9Ja3evg4ecaqOXmE4LwSv68S89q6ybaydEsV6MKa1IiUzZRk/bd3vJoNHNQCgVP4rMjhlPqt/bIG4crsDY7/iVbYi7GSH7sQ9Dmkk6gj8Rgqn9pmCkqZWG6W1kdmDx5Ejt5TvP1nO3aHp8sMxpPcQ2p8yBydflZCODg+KDbt8xISXZAEPoL0B69tADaPjaJyiTXpSwRnbsPx+L2SBg7HWyQ5cWd42hQPwRjPDKuAZR2FesQ9h5+8QfkS71Tu5Zy2QmHKwLYDE1Xwk43NwTDT4SLMojlssij/+s8j7pmYFB75UXxlJ/y/XsRIUYJ5c8YxnbtNgXV4Z5QQrupHbcYQ+MpK/GUkPKOXgLJOo6/rWQO0EhBizQXk3HC3rD219FjiGsMWhV51Zcp8+buWGZSmaz/1SkDoKluyyxxeCO3+XusedQr9cIEjUCaror3nrFPoHu5VWQkCVgv6wUVwJzntv9TlXRgtypTsCjDzVKBEQOW1EmaEea1kvp7/x1vvLMwwvSOIlGHSsLFJYjTnzCTh4pjdpEvSVpDPcMUkyOUH/bXbXFqU9176ZY1QIEpOBwRgR9LkiE1uCDODCIhCf3xgcakt2DiFE1bu0CqXT93aQ6MH3tTXiDyrUo0AEy0fQ9T7z4mMX9VS848sqimZKdUwM21E/7KZ8+3BCuLtAzVAURvF1t5EPAmcTDUH/qAdOIAzg4ZpTMij18SeIcd7VKG0O5v9+Efpm/EFcIPln3Bfg24NgE4URhsWSbKAo7zfdmk2xV3Su4qPO8gf3VV7TtvIsdzZNThnMpBoLJJCbAutxcsbaKGl7SnvWwb18dozMrnoQCJsY1SjPCvu7yqY0oyg8aOuj50FQrp7qkTt/8THeOoIOvPo1BZ</sen:Object>\n"
               +"    </sen:SigEnvelope>\n"
               +"  </env:Body>\n"
               +"</env:Envelope>\n"
               +"\n"
               ;

}
