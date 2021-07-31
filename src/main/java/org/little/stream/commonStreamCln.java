package org.little.stream;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;

//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;

public class commonStreamCln  extends commonStream{
       private static final Logger logger = LoggerFactory.getLogger(commonStreamCln.class);

       public commonStreamCln() {
              clear();
              logger.info("create commonStreamCln");
       }

       @Override
       public void clear() {
              super.clear();          
       }


}
