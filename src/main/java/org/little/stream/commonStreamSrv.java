package org.little.stream;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;

public class commonStreamSrv  extends commonStream{
       private static final Logger logger = LoggerFactory.getLogger(commonStreamSrv.class);

       
       public commonStreamSrv() {
              clear();
              logger.info("create commonStreamSrv");
       }

       @Override
       public void clear() {
              super.clear();          
       }


}
