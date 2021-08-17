package org.little.stream.test;
               
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.InputStream;
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

public class SAX {
       private static final Logger log = LoggerFactory.getLogger(SAX.class);


       private static String parse() throws Except{
               return parse(def_msg2.getBytes());
       }
       private static String parse(byte[] buf) throws Except{
               ByteArrayInputStream in          = new ByteArrayInputStream(buf);
               Writer               out         = new StringWriter();
               XMLStreamWriter      xml_writer  = null;
               XMLStreamReader      xml_reader  = null;
               XMLInputFactory      ifactory    = XMLInputFactory.newInstance();
               XMLOutputFactory     ofactory    = XMLOutputFactory.newInstance();
               try {
                    xml_reader = ifactory.createXMLStreamReader(in);
               } 
               catch (XMLStreamException e) {
                      log.error(""+new Except("Error createXMLStreamReader",e));
                      return null;
               }
               try {
                    xml_writer = ofactory.createXMLStreamWriter(out);
               } 
               catch (XMLStreamException e) {
                      log.error(""+new Except("Error createXMLStreamWriter",e));
                      return null;
               }

               try {
                   int     event             = xml_reader.getEventType();
                   String  current_element  =null;
                   String  current_namespace=null;
                   boolean is_run           =true;
 
                   while (is_run) {
                          
                          switch (event) {
                          case XMLStreamConstants.START_DOCUMENT:
                               String encoding = xml_reader.getCharacterEncodingScheme();
                               if(encoding==null)encoding="UTF-8";
                               String ver=xml_reader.getVersion();
                               if(ver==null)ver="1.0";
                               xml_writer.writeStartDocument(encoding,ver);                                 
                               break;
                          case XMLStreamConstants.START_ELEMENT:
                               QName obj=xml_reader.getName();
                               System.out.println("p:"+obj.getPrefix()+" l:"+obj.getLocalPart()+" size_ns:"+xml_reader.getNamespaceCount());
                               xml_writer.writeStartElement(obj.getPrefix(), obj.getLocalPart(), obj.getNamespaceURI());
                               {
                                    for (int i = 1; i < xml_reader.getNamespaceCount(); i++) {
                                      String uri = xml_reader.getAttributeNamespace(i);
                                      if(uri==null)   uri=obj.getNamespaceURI();
                                      xml_writer.writeNamespace(obj.getPrefix(),  uri);
                                      System.out.println("p:"+obj.getPrefix()+" u:"+uri);
                                     }
                               }   
                               {
                                     for (int i = 0; i < xml_reader.getAttributeCount(); i++) {
                                       String uri = xml_reader.getAttributeNamespace(i);
                                       if(uri==null)   uri="";
                                       String localName = xml_reader.getAttributeLocalName(i);
                                       String prefix = xml_reader.getAttributePrefix(i);
                                       String qName;
                                       if(prefix==null || prefix.length()==0)qName = localName;
                                       else  qName = prefix + ':' + localName;
                                       //String type = xml_reader.getAttributeType(i);
                                       
                                       String value = xml_reader.getAttributeValue(i);
                                       xml_writer.writeAttribute(qName, value);
                                      System.out.println("n:"+localName+" v:"+value+" p:"+prefix);
                                     }
                               }
                               break;
                          case XMLStreamConstants.ATTRIBUTE:
                               //System.out.println("ATTRIBUTE");
                               break;
                          case XMLStreamConstants.NAMESPACE:
                               //QName obj2=xml_reader.getName();
                               //xml_writer.writeNamespace(obj2.getPrefix(),  obj2.getNamespaceURI());
                               //System.out.println("NAMESPACE");
                               break;
                          case XMLStreamConstants.CHARACTERS:
                               xml_writer.writeCharacters(xml_reader.getText());
                               break;
                          case XMLStreamConstants.END_ELEMENT:
                               xml_writer.writeEndElement();
                               break;
                          case XMLStreamConstants.END_DOCUMENT:
                               xml_writer.writeEndDocument();
                               break;
                          case XMLStreamConstants.SPACE:
                               //System.out.println("SPACE");
                               break;
                          case XMLStreamConstants.COMMENT:
                               //System.out.println("COMMENT");
                               break;
                          case XMLStreamConstants.CDATA:
                               //System.out.println("CDATA");
                               break;
                          case XMLStreamConstants.DTD:
                               //System.out.println("DTD");
                               break;
                          case XMLStreamConstants.ENTITY_DECLARATION:
                               //System.out.println("ENTITY_DECLARATION");
                               break;
                          case XMLStreamConstants.ENTITY_REFERENCE:
                               //System.out.println("ENTITY_REFERENCE");
                               break;
                          case XMLStreamConstants.NOTATION_DECLARATION:
                               break;
                          default:
                                 System.out.println("Event:"+event);
                              break;
                          }
                          
                          if (xml_reader.hasNext())event = xml_reader.next();
                          else is_run=false;
                   }
               } 
               catch (XMLStreamException e) {
                      log.error(""+new Except("XML parsing",e));
                      return null;
               }
               finally {
                    try{xml_reader.close();} catch (XMLStreamException e) {}
               }
               //System.out.println("-------------------\n"+out.toString());
               return out.toString();
       }
     
       public static void main(String[] args) throws Except {
              long start=System.currentTimeMillis();
              int  size=1;
              byte [] buf=def_msg2.getBytes();
              for(int i=0;i<size;i++){
                 String s=parse(buf);    
                 System.out.println("-------------------\n"+s);
              }
              start=System.currentTimeMillis()-start;
              System.out.println("\ntime:"+start+"\n s:"+(double)size/(double)start*1000);

       }
       public static final String def_msg2=""
               +"<?xml version=\"1.1\" encoding=\"Windows-1251\"?>"
               +"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">\n"
               +"  <env:Header>\n"
               +"    <props:MessageInfo xmlns:props=\"urn:cbr-ru:msg:props:v1.3\" >\n"
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
               +"      <sen:Object>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</sen:Object>\n"
               +"    </sen:SigEnvelope>\n"
               +"  </env:Body>\n"
               +"</env:Envelope>\n"
               +"\n"
               ;

}
