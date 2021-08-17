package org.little.stream.test;
        
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class ufpsWriter {
       private static final Logger log = LoggerFactory.getLogger(ufpsWriter.class);

       public XMLStreamWriter xml_writer = null;
       public ufpsWriter(){
              Writer out = new StringWriter();
              XMLOutputFactory     ofactory    = XMLOutputFactory.newInstance();
              try {
                   xml_writer = ofactory.createXMLStreamWriter(out);
              } 
              catch (XMLStreamException e) {
                     log.error(""+new Except("Error createXMLStreamWriter",e));
              }
       }



}
