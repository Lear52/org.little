package org.little.mq.stream.config;

public class cfg_addr {

       private String queueName       ;
       private String qmName          ;
     
       public cfg_addr(){clear();}
       
       public void clear() {
              queueName       = null;
              qmName          = null;
       }
       public void   setQueueName(String q){queueName = q; }
       public void   setQMName   (String q){qmName    = q; }

       public String getID()               {return queueName+"@"+qmName;}

       public String getQueueName()        {return queueName;           }
       public String getQMName()           {return qmName;              }

       public boolean equals(cfg_addr p){
             if(p.getQueueName()==null)                        return false;
             if(getQueueName().equals(p.getQueueName())==false)return false;
             if(p.getQMName()==null && getQMName()!=null)      return false;
             if(p.getQMName()!=null && getQMName()==null)      return false;
             if(getQMName().equals(p.getQMName())==false)      return false;
             return true;
       }
       public String toString(){
              return " queueName:"+queueName+
                     " qmName:"+qmName+"";   
       }

}
