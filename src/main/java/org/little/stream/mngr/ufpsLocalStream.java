package org.little.stream.mngr;

import java.sql.Timestamp;

import org.little.stream.ufps.ufpsMsg;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class ufpsLocalStream implements ufpsStream {
       private static final Logger logger = LoggerFactory.getLogger(ufpsLocalStream.class);

       private String       name;
       private ufpsQueue    in;   // user queue
       private ufpsQueue    out;  // transfer queue
       private long         last_out;
       private long         last_in;
       private long         last;
 
       public ufpsLocalStream(String name,int max_size){
              this.name=name;
              this.in  =new ufpsQueue("in",max_size);
              this.out =new ufpsQueue("out",max_size);
              last=System.currentTimeMillis();
              last_in=System.currentTimeMillis();
              last_out=System.currentTimeMillis();
       }

       public ufpsLocalStream(String name,int max_size,ufpsQueue tranfer){
              this.name=name;
              this.in  =new ufpsQueue("in",max_size);
              this.out =tranfer;
              last=System.currentTimeMillis();
              last_in=System.currentTimeMillis();
              last_out=System.currentTimeMillis();
       }
       @Override
       public String  getName() {return name;}
       @Override
       public boolean put(ufpsFrame msg) throws InterruptedException {boolean ret=out.put(msg);last=System.currentTimeMillis();return ret;}
       @Override
       public ufpsFrame get() { ufpsFrame f=in.get(); last=System.currentTimeMillis();return f;}

       public boolean put_transfer(ufpsFrame msg) throws InterruptedException {return in.put(msg);}
       public ufpsFrame get_transfer() { return out.get(); }

       //public ufpsQueue getOut(){return out;}

       public boolean open(){return false;}
       public void    close(){}
       public long    getLastOut() {return last_out;}
       public long getLastIn() {return last_in;}
       public long getLast() {return last;}




}
