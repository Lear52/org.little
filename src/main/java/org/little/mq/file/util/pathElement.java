package org.little.mq.file.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class pathElement{
       private static final Logger log = LoggerFactory.getLogger(pathElement.class);
       //public  static String getClassName(){return pathElement.class.getName();}

       private Path in_file;
       private long start;

       private static Hashtable<String,FileSystem> fs = new Hashtable<String,FileSystem>();


       public pathElement() {
              clear();
       }
       public pathElement(Path p) {
              clear();
              in_file=p;
       }
       public void clear() {
              in_file=null;
              start   =0;
       }
       public long getStart() {
              return start;
       }
       public String getName() {
              return in_file.getFileName().toString();
       }
       public String getFullName() {
              return in_file.toAbsolutePath().toString();
       }
       public Path get() {
              return in_file;
       }

       public boolean remove(Path out_file) {
              boolean ret=false;
                 if(out_file!=null){
                 try {
                      ret=Files.move(in_file, out_file,StandardCopyOption.REPLACE_EXISTING)!=null;
                 }
                 catch (IOException e2) {
                        log.error("Files.move("+in_file+","+out_file+") ex:"+e2);
                        ret=false;
                 }
              }
                 return ret;
       }
       /*
       public boolean remove(File out_file) {
              if(out_file!=null)return in_file.toFile().renameTo(out_file);               
              return false;
       }
       */
       private static void list(ArrayList<pathElement> list,Path path) throws Except {
               log.info("list:"+path);

               if(Files.isDirectory(path)){
              
                   DirectoryStream<Path> ds = null;
                   try {
                        ds = Files.newDirectoryStream(path);
                   } 
                   catch (IOException e1) {
                          throw new Except("error open directory:"+path.toString(),e1);
                   }

                   //if(false){
                   //   for(Path child : ds)list(list,child);
                   //}
                   //else{
                      Path           child=null;
                      Iterator<Path> i=ds.iterator();
                      while(i.hasNext()){

                         child=i.next();
                         log.info("child:"+child);
                         list(list,child);
                      }
                   //}

                   try {
                        ds.close();
                   } 
                   catch (IOException e2) {
                          throw new Except("error close directory:"+path.toString(),e2);
                   }

               }
               else{ 
                   log.info("add:"+path);
                   list.add(new pathElement(path));
               }

       }
       public static ArrayList<pathElement> getDir(String _global_dirname,String _local_dirname){
              return getDir(_global_dirname,_local_dirname,0);
       }
       public static ArrayList<pathElement> getDir(String _global_dirname,String _local_dirname,long range_ms){
              ArrayList<pathElement> list=new ArrayList<pathElement>(100000);
              Path                   local_dir=null;
              Path                   pfile    =null;
              try {
                   pfile       =Paths.get(_global_dirname);
              } 
              catch (Exception e2) {    
                     log.error("file:"+_global_dirname+" ex:"+e2);
                     return null;
              }

              try {
                   if(Files.isDirectory(pfile)){
                      //FileSystem pFileSystem =FileSystems.getDefault();
                      local_dir = Paths.get(_global_dirname+"/"+_local_dirname);
                      log.info("filesystem:"+_global_dirname+"/"+_local_dirname+" is dir");
                   }
                   else{
                      FileSystem pFileSystem;

                      synchronized (fs){
                        log.trace("search filesystem:"+_global_dirname);
                        pFileSystem=fs.get(_global_dirname);

                        if(pFileSystem==null){
                           Map <String,String> map=new HashMap<String,String>();
                           URI uri=pfile.toUri();
                           log.trace("create 1 uri:"+uri);
                           URI new_uri= new URI("jar:"+uri.getScheme(),uri.getPath(),null);
                           log.trace("create 2 uri:"+new_uri);

                           pFileSystem=FileSystems.newFileSystem(new_uri,map);
                           //pFileSystem=FileSystems.newFileSystem(pfile,null);
                           log.trace("create new filesystem:"+_global_dirname);
                       
                           fs.put(_global_dirname,pFileSystem);
                        }
                        else{
                           log.trace("get filesystem:"+_global_dirname);
                        }
                      }

                      local_dir = pFileSystem.getPath(_local_dirname);
                   }
              } 
              catch (IOException e3) {
                     log.error("file:"+_global_dirname+_local_dirname+" IOException ex:"+e3);
                     return null;
              }
              catch (Exception e4) {
                     log.error("file:"+_global_dirname+_local_dirname+" Exception ex:"+e4);
                     return null;
              }

              try {
                  list(list,local_dir);
              } 
              catch (Except e5) {
                     log.error("error get list file:"+_global_dirname+_local_dirname+" ex:"+e5);
                     return null;
              }

              if(list.size()==0)return null;

              Collections.sort(list,new Comparator<pathElement>() 
                                   {
                                     @Override
                                     public int compare(pathElement n1,pathElement n2){
                                           //return n1.getName().compareToIgnoreCase(n2.getName());
                                           return n1.get().toString().compareToIgnoreCase(n2.get().toString());
                                     }
                                   }
              );
              if(range_ms!=0){
                 double d_range_ms=((double)range_ms)/((double)list.size());
                 double tps1=1.0/d_range_ms*1000.0;
                 double tps2=((double)list.size())/(((double)range_ms)/1000);

                 log.trace("list_size:"+list.size()+" range:"+range_ms+"ms delta_range:"+d_range_ms+"ms tps:"+tps1+"="+tps2+" msg/sec");

                
                 for(int i=0;i<list.size();i++){
                     double t=d_range_ms*((double)i);
                     long tt=(long)t;
                     list.get(i).start=tt;
                 }
              }
              return list;
       }

       private static void run(Path _file) {

              InputStream     in;
              int             f_size=10000;
              byte []         buf;
              try{
                  f_size=(int)Files.size(_file);
                  in = Files.newInputStream(_file);
              }
              catch (Exception ex1){
                  log.error("open file:"+_file +" ex:"+Except.printException(ex1));
                  return;
              }
              buf=new byte[f_size];
              try{
                  f_size=in.read(buf, 0, buf.length);
              }
              catch (Exception ex1){
                  log.error("read file:"+_file +" ex:"+Except.printException(ex1));
                  return;
              }

              try{
                  in.close();
              }
              catch (Exception ex1){
                  log.error("close file:"+_file+" ex:"+Except.printException(ex1));
              }
              //-------------------------------------------------------------
              _file=null;

       }
       private static void work(String global_dirname,String local_dirname,long range_ms ){

              System.out.println("start:"+new java.util.Date());

              ArrayList<pathElement> list=pathElement.getDir(global_dirname,local_dirname,range_ms);

              if(list==null){
                 System.out.println("dirname:"+global_dirname+" is_empty");
                 log.trace("dirname:"+global_dirname+" is_empty");
                 return;
              }
              System.out.println("get:"+new java.util.Date());

              System.out.println("dirname:"+global_dirname+" size:"+list.size());
              //------------------------------------------------
              long st=System.currentTimeMillis();

              while(list.size()>0)
              for(int i=0;i<list.size();i++){
                  pathElement o=list.get(i);
                  long t=System.currentTimeMillis()-(o.start+st);
                  if(t>=0){
                     run(o.get());
                     o.clear();
                     list.remove(i);
                     break;
                  }
                  else{
                       try{Thread.sleep(t);}catch(Exception e){}
                  }
                  
              }
              System.out.println("quit:"+new java.util.Date());
       }

       public static void help(String[] args) {
              System.out.println("run java "+pathElement.class+" global_dirname range_msek [local_dir_name]");
       }
       public static void main(String[] args) {

              if(args.length<2){
                 help(args);
                 return;
              }
              if(args.length==2){
                 System.out.println("arg 2");
                 work(args[0],"/",0);
                 return;
              }
              if(args.length==3){
                 System.out.println("arg 3");
                 work(args[0],args[2],0);
                 return;
              }


       }

}
