package org.little.db.kir;
                    
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class arhKIR{

       private static final   Logger logger = LoggerFactory.getLogger(listKIR.class);
       private static String  preData="<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n<kir_data>\n";
       private static String  postData="</kir_data>\n";
       private String         work_path;
       private FileReader     fin;   
       private BufferedReader in;   

       public arhKIR(String _work_path) {
              work_path=_work_path;
              clear();
              logger.trace("create arhKIR:"+work_path);
       }

       protected void clear() {
              fin=null;      
              in=null;      
       }
       //------------------------------------------------------------------------------------------------------------------------
       public void printFlush(OutputStream out,String data_filename){
              openIN(work_path+"/"+data_filename);
              try {
                  String str;
                  while ((str = readLine()) != null) {
                        out.write(str.getBytes());
                  }
                  out.flush();
                  out.close();
              } catch (IOException e) {
                  logger.error("ex:"+new Except("print flush data",e));
              }
              finally{
                   if(out!=null) try {out.close();} catch (IOException e) {}
                   closeIN();
              }

       }
       public void   zipFlush(OutputStream out,String data_filename){
              ZipOutputStream zos = null;
              ZipEntry        ze  = null;
              Date            cur_date=new Date();
              try {
                   zos = new ZipOutputStream(out);
                   ze = new ZipEntry(data_filename);
                   ze.setTime(cur_date.getTime());
                   ze.setMethod(ZipEntry.DEFLATED);
                   ze.setComment(data_filename);
                   ze.setExtra(new byte[]{(byte)'X'});
                   zos.putNextEntry(ze);
                   printFlush(zos,data_filename);
                   zos.closeEntry();
              } catch (IOException e) {
                  logger.error("ex:"+new Except("print flush data",e));
              }
              finally{
                   if(zos!=null) try {zos.close();} catch (IOException e) {}
                   closeIN();
              }

       }
       public void printFlush(Writer out,String data_filename){
              openIN(work_path+"/"+data_filename);
              try {
                    out.write(preData);
                    int howmany;
                    char[] buf = new char[512];
                    while ((howmany = read(buf)) >= 0) {
                           out.write(buf, 0, howmany);
                    }
                    out.write(postData);
                    out.flush();
              } catch (IOException e) {
                  logger.error("ex:"+new Except("print flush data",e));
              }
              finally{
            	  if(out!=null)try {out.close();} catch (IOException e) {}
                 closeIN();
              }
       }
       //------------------------------------------------------------------------------------------------------------------------

       protected String readLine()      {if(in==null)return null;try{return in.readLine();}catch (IOException e) { return null;}}
       //protected int    read()          {if(in==null)return -1;  try{return in.read();    }catch (IOException e) { return -1;}}
       protected int    read(char[] buf){if(in==null)return -1;  try{return in.read(buf); }catch (IOException e) { return -1;}}

       //------------------------------------------------------------------------------------------------------------------------
       private void openIN(String data_filename) {
              try{
                  fin=new FileReader(data_filename);
                  in =new BufferedReader(fin);
              }
              catch (IOException e){
                   logger.error("ex:"+new Except("open int filename:"+data_filename,e));
                   closeIN();
              }
              logger.info("open IN: "+data_filename);
       }
       private void closeIN() {
              if(in !=null)try{ in.close();}catch (IOException e){}in=null;
              if(fin!=null)try{fin.close();}catch (IOException e){}fin=null;
              logger.info("close IN");
       }



       public static void main(String args[]){
              arhKIR list=new arhKIR("./var/kir");
              try {
                  OutputStream os = new FileOutputStream(args[0]);
                  list.zipFlush(os,"kir_2021-08-01_15-53-18.dat");
                  os.close();
              } catch (IOException e) {
                  System.err.println(e);
              }

       }
}
