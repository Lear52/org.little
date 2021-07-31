/*
 * Created on 18.09.2012
 * Modification 17/10/2014
 *
 */
package org.little.util.run;

import org.little.util.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

//------------------------------------------------
/**
 * @author av
 * класс создания потока потоков
 * 
 */
public class tfork implements Runnable{
       final private static String CLASS_NAME="org.little.util.fork";
       final private static int    CLASS_ID  =118;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);


       // Системный потокдля запуска задач в паралель
       protected Thread             process_tread;
       // Общий флаг работы пула потоков
       protected boolean            is_run;

       protected AtomicBoolean      is_Run; 

       public void init() {}

       public void clear(){
              is_run=false;
              process_tread=null;
              is_Run = new AtomicBoolean(false); 
              
       }

       public tfork(){ clear();}
                                          

       @Override
       public void run(){

              for(int i=0;i<10;i++){
                  delay(1);
                  log.trace("empty RUN!!!! Override !!!!");
              }
              stop();

              clear();

      };

      public void fork(){
             is_run=true;
             process_tread=new Thread(this);
             process_tread.start();

      }

      public boolean isRun(){return is_run;}

      public void start(){
             is_run=true;
      }
      public void stop(){
             is_run=false;
      }
      public void shutdown(){
             is_run=false;
      }
      static public void delay(int d){
             delayMs(d*1000);
      }
      static public void delayMs(int d){
            try{
                Thread.sleep(d);
            }
            catch(Exception e){}

      }

}

