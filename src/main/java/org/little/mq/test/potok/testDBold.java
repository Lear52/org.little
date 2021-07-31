package org.little.mq.test.potok;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.cfg.commonPRJ;
import org.little.util.db.connection_pool;
import org.little.util.db.dbExcept;
import org.little.util.db.query;
import org.little.util.run.tfork;

public class testDBold  extends tfork{
       final private static String CLASS_NAME="prj0.potok.testDBold";
       final private static int    CLASS_ID  =2404;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

             static private Object lock = new Object();

             private static count cnt=new count();

             private   boolean         isdebug;
             private   connection_pool b;
             private   query           q_r;
             private   query           q_h;
             private   reportMsg       report;
             private   headerMsg       header;
             protected int             id;
             protected int             flag_state;
             protected static int      flag_global_state=def.CMD_RUN;
             protected static int      count=10;
             protected static int      batch=1;
             protected static long     tmp_cnt=1;

       public testDBold() {
              clear();
       }
       public void clear() {
              b        =null;      
              q_r      =null;      
              q_h      =null;
              report   =null;
              header   =null;
              id       =0;
              isdebug  =false;

       }

       public void set(int i){id=i;}

       public boolean initDB(){

              try {
                  b=commonPRJ.getInstance().getDB();
              }
              catch (Except e) {
                  log.error("Error open DB ex:"+e);
                  cmd_close();
                  return false;
              }
              if(b==null){
                  log.error("Error open DB");
                  cmd_close();
                  return false;
              }

              try {
                  q_r=b.open(); q_r.commit(false);       
                  q_h=b.open(); q_h.commit(false);              
              }
              catch (dbExcept e) {
                  log.error("Error open DB ex:"+e);
                  cmd_close();
                  return false;
              }
              report =new reportMsg();
              header =new headerMsg();
              report.initMsg();
              header.initMsg();

              try {
                   report.initQ(q_r);
                   header.initQ(q_h);
              }
              catch (dbExcept e) {
                  log.error("Error open DB ex:"+e);
                  cmd_close();
                  return false;
              }
              cmd_start();
              log.trace("open DB");
              return true;
      }
      public void close(){
              cmd_close();
              try {
                  report.closeQ(q_r);
                  header.closeQ(q_h);
                  b.close(q_r);
                  b.close(q_h);
              }
              catch (Except e) {
                  log.error("Error close DB ex:"+e);
                  return;
              }
              log.trace("close DB");

      }
      public void run(){
              log.trace("Run potok:"+id);

              for(int i=0;i<count;i++){
                  if(!run0())break;
                  log.trace("Run potok:"+id+"/"+i);
              }
              close();
              log.trace("Stop potok:"+id);
              cmd_close();
      }
      public boolean run0(){
              long _c=0;


              for(int i=0;i<batch;i++){
                  synchronized (lock) {
                       _c=tmp_cnt++;
                  }
                  StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("");
				stringBuilder.append(( new java.util.Date().getTime()));
				stringBuilder.append(java.lang.System.currentTimeMillis());
				stringBuilder.append("");
				stringBuilder.append(java.lang.System.nanoTime());
				stringBuilder.append("");
				stringBuilder.append(_c);
				String t_id=stringBuilder.toString();
                
                  try {
              //System.out.print("1");
                      report.initMsg(t_id,"test0");
                      report.setQ(q_r);
                      q_r.executeUpdate();
                      if(isdebug)log.trace("Potok:"+id+" "+report.toString());
              //System.out.print("2");
                      report.initMsg(t_id,"test1");
                      report.setQ(q_r);
                      q_r.executeUpdate();
                      if(isdebug)log.trace("Potok:"+id+" "+report.toString());
              //System.out.print("3");
                      report.initMsg(t_id,"test2");
                      report.setQ(q_r);
                      q_r.executeUpdate();
                      if(isdebug)log.trace("Potok:"+id+" "+report.toString());
                  }
                  catch (Except e0) {
                      log.error("Error set vol DB:"+id+" ex:"+e0);
                      log.error("Potok:"+id+" "+report.toString());
                      cmd_close();
                      return false;
                  }

                  try {
                      header.initMsg(t_id);
                      header.setQ(q_h);
                      q_h.executeUpdate();
                      if(isdebug)log.trace("Potok:"+id+" "+header.toString());
                  }
                  catch (Except e1) {
                      log.error("Error set vol DB:"+id+" ex:"+e1);
                      log.error("Potok:"+id+" "+header.toString());
                      cmd_close();
                      return false;
                  }
                  cnt.add();

              }
              try {
                  q_r.commit();       
                  q_h.commit();       
              }
              catch (Except e3) {
                  log.error("Error set vol DB:"+id+" ex:"+e3);
                  log.error("Potok:"+id+" "+report.toString());
                  log.error("Potok:"+id+" "+header.toString());
                  cmd_close();
                  return false;
              }


              return true;
       }
       public void  cmd_start(){
              flag_state=def.CMD_RUN;
              start();
       }
       public void  cmd_close(){
              flag_state=def.CMD_STOP;
              stop();
       }
       public boolean isStop() {
              return (flag_state<def.CMD_RUN)||(flag_global_state<def.CMD_RUN);
       }

       static public testDBold StartPotok(int i){
              testDBold t=new testDBold();
              t.set(i);
              t.initDB();
              t.fork();
              return t;
       }
       public static void main(String[] args) {
              int r_count=10;
              cnt.init();
              if(args.length>=1)if(args[0]!=null) try { r_count=Integer.parseInt(args[0], 10);} catch (Exception e) {r_count=10;} 
              if(args.length>=2)if(args[1]!=null) try { count=Integer.parseInt(args[1], 10);} catch (Exception e) {count=10;}
              if(args.length>=3)if(args[2]!=null) try { batch=Integer.parseInt(args[2], 10);} catch (Exception e) {batch=1;}
              System.out.println("Init parametr potok:"+ r_count+" count:"+count+" batch:"+batch);

              testDBold t[]=new testDBold[r_count+1];
              for(int i=0;i<r_count;i++){
                  System.out.print(" "+i);
                  t[i]=StartPotok(i);
              }
              System.out.println("");
              System.out.println("start potok:"+r_count);
              boolean is_run=true;
              while(is_run){
                   is_run=false;
                   for(int i=0;i<r_count;i++){
                       if(t[i]!=null){
                          if(t[i].isStop()){t[i]=null;}
                          else {is_run=true;break;}
                       }
                   }
                   tfork.delay(1);
                   log.trace("COUNT:"+cnt);
                   System.out.println("run:"+is_run);
                   System.out.println(cnt.toString());
              }
              System.out.println("Stop all");


       }



}

