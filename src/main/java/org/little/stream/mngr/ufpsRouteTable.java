package org.little.stream.mngr;

import java.util.ArrayList;

import org.little.stream.ufps.ufpsMsg;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string.*;

public class ufpsRouteTable {
       private static final Logger logger = LoggerFactory.getLogger(ufpsRouteTable.class);
       private ArrayList<ufpsRoute> route;
       
       public ufpsRouteTable(){
              route=new ArrayList<ufpsRoute>();
       }

       public void add(ufpsRoute rt){
              route.add(rt);
              logger.trace("add route: "+rt);
       }

       public ufpsRoute get(String _addr){
              for(int i=0;i<route.size();i++){
                  ufpsRoute rt=route.get(i);
                  if(stringWildCard.wildcardMatch(_addr, rt.getAddress(), stringCase.INSENSITIVE))return rt;
              }

              return null;
       }

       public ufpsFrame set(ufpsMsg msg) {
              ufpsRoute rt=get(msg.getTO().get(0));
              if(rt==null){
                 logger.error("no route msg:"+msg);
                 return null;
              }
              ufpsFrame f_msg=new ufpsFrame(rt.getRouteUser(),rt.getRouteQueue(),msg);
              f_msg.isLocal(rt.islocal());

              logger.trace("addr:"+msg.getTO().get(0)+" user:"+rt.getRouteUser()+" queue:"+rt.getRouteQueue()+" local:"+rt.islocal()+" f_msg:"+f_msg);
              logger.trace("f_msg:"+f_msg);
                 
              return f_msg;
       }

}
