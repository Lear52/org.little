package org.little.db.kir;
                    
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string.stringDate;


public class listKIR{

       private static final   Logger logger = LoggerFactory.getLogger(listKIR.class);
       private static String  preData="<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n<kir_data>\n";
       private static String  postData="</kir_data>\n";
       private String         work_path;
       private String         current_filename;
       private String         flash_filename;
       private FileReader     fin;   
       private BufferedReader in;   
       private FileWriter     fout;   
       private BufferedWriter out;   

       public listKIR(String _work_path) {
              work_path=_work_path;
              clear();
              logger.trace("create listKIR:"+work_path);
       }

       protected void clear() {
              current_filename=work_path+"/out.dat";
              flash_filename=".dat";
              fin=null;      
              in=null;      
              fout=null;      
              out=null;      
       }


       public void    open() {openOUT();}

       public boolean write(objKIR obj) {if(out==null)return false;try{out.write(obj.printXML());out.flush();}catch (IOException e) { closeOUT();return false;}return true;}

       public void    close(){closeOUT();closeIN();}
       
       //------------------------------------------------------------------------------------------------------------------------
       public void printFlush(OutputStream out){
              printFlush(out,new Date());
       }
       private void printFlush(OutputStream out,Date cur_date){
              createFlush();
              try {
                  String str;
                  while ((str = readLine()) != null) {
                        out.write(str.getBytes());
                  }
                  out.flush();
                  out.close();
                  commitFlush(cur_date);
              } catch (IOException e) {
                  revertFlush();
                  logger.error("ex:"+new Except("print flush data",e));
              }

       }
       public void   zipFlush(OutputStream out){
              ZipOutputStream zos = null;
              ZipEntry        ze  = null;
              Date            cur_date=new Date();
              try {
                   zos = new ZipOutputStream(out);
                   ze = new ZipEntry("kir_"+stringDate.date2filename(cur_date)+".xml");
                   ze.setTime(cur_date.getTime());
                   ze.setMethod(ZipEntry.DEFLATED);
                   ze.setComment("kir_"+stringDate.date2filename(cur_date)+".xml");
                   ze.setExtra(new byte[]{(byte)'X'});
                   zos.putNextEntry(ze);
                   printFlush(zos,cur_date);
                   zos.closeEntry();
              } catch (IOException e) {
                  revertFlush();
                  logger.error("ex:"+new Except("print flush data",e));
              }
              finally{
                   if(zos!=null)
					try {zos.close();} catch (IOException e) {}

              }

       }
       public void printFlush(Writer out){
              Date cur_date=new Date();
              int metod=1;
              createFlush();
              try {
                    out.write(preData);
                    if(metod==0){
                       String str;
                       while ((str = readLine()) != null) {
                             out.write(str);
                       }
                    }
                    else
                    if(metod==1){
                         int howmany;
                         char[] buf = new char[512];
                         while ((howmany = read(buf)) >= 0) {
                                out.write(buf, 0, howmany);
                         }
                    }
                    else
                    if(metod==2){
                         int howmany;
                         while ((howmany = read()) >= 0) {
                                out.write(howmany);
                         }
                    }

                    out.write(postData);
                    out.flush();
                    out.close();
                    commitFlush(cur_date);
              } catch (IOException e) {
                  revertFlush();
                  logger.error("ex:"+new Except("print flush data",e));
              }

       }
       //------------------------------------------------------------------------------------------------------------------------
       protected void createFlush() {
                   flash_filename=work_path+"/flash.dat";
                   closeOUT();
                   File f1 = new File(current_filename);
                   File f2 = new File(flash_filename);
                   logger.info(f1.getPath() + (f1.renameTo(f2) ? " renamed to " : " could not be renamed to ") + f2.getPath());
                   openOUT();
                   openIN();
       }


       protected String readLine()      {if(in==null)return null;try{return in.readLine();}catch (IOException e) { return null;}}
       protected int    read()          {if(in==null)return -1;  try{return in.read();    }catch (IOException e) { return -1;}}
       protected int    read(char[] buf){if(in==null)return -1;  try{return in.read(buf); }catch (IOException e) { return -1;}}

       protected void revertFlush() {
                   closeIN();
                   closeOUT();
                   String tmp_filename=work_path+"tmp.dat";
                   {
                    File f1 = new File(current_filename);
                    File f2 = new File(tmp_filename);
                    logger.info("revert: "+f1.getPath() + (f1.renameTo(f2) ? " renamed to " : " could not be renamed to ") + f2.getPath());
                   }
                   {
                    File f1 = new File(flash_filename);
                    File f2 = new File(current_filename);
                    logger.info("revert: "+f1.getPath() + (f1.renameTo(f2) ? " renamed to " : " could not be renamed to ") + f2.getPath());
                   }
                   openOUT();

                   tmp_filename=work_path+"flash.dat";
                   {
                    File f1 = new File(tmp_filename);
                    File f2 = new File(flash_filename);
                    logger.info("revert: "+f1.getPath() + (f1.renameTo(f2) ? " renamed to " : " could not be renamed to ") + f2.getPath());
                   }
                   openIN();
                   int howmany;
                   try{
                      while ((howmany = in.read()) >= 0) {out.write(howmany);}
                      out.flush();
                   }
                   catch (IOException e) { 
                         logger.error("revert: i/o ex:"+e);
                   }
                   closeIN();

                   {
                    File f1 = new File(flash_filename);
                    logger.info("revert: "+(f1.delete() ? "Deleted " :"Could not delete ") + f1.getPath());
                   }

       }
       protected void commitFlush(Date cur_date) {
                   closeIN();
                   String new_filename=work_path+"/kir_"+stringDate.date2filename(cur_date)+".dat";
                   {
                     File f1 = new File(flash_filename);
                     File f2 = new File(new_filename);
                     logger.info("commit and "+f1.getPath() + (f1.renameTo(f2) ? " renamed to " : " could not be renamed to ") + f2.getPath());
                   }
       }
       //------------------------------------------------------------------------------------------------------------------------
       private void openOUT() {
              try{
                  fout=new FileWriter(current_filename);
                  out =new BufferedWriter(fout);
              }
              catch (IOException e){
                   logger.error("ex:"+new Except("open out filename:"+current_filename,e));
                   closeOUT();
              }
              logger.info("open OUT: "+current_filename);
       }
       private void openIN() {
              try{
                  fin=new FileReader(flash_filename);
                  in =new BufferedReader(fin);
              }
              catch (IOException e){
                   logger.error("ex:"+new Except("open int filename:"+flash_filename,e));
                   closeIN();
              }
              logger.info("open IN: "+flash_filename);
       }
       private void closeOUT() {
              if(out !=null)try{ out.flush(); out.close();}catch (IOException e){}out=null;
              if(fout!=null)try{fout.close();}catch (IOException e){}fout=null;
              logger.info("close OUT: "+current_filename);
       }
       private void closeIN() {
              if(in !=null)try{ in.close();}catch (IOException e){}in=null;
              if(fin!=null)try{fin.close();}catch (IOException e){}fin=null;
              logger.info("close IN: "+flash_filename);
       }



       public static void main(String args[]){
              listKIR list=new listKIR("./var/kir");
              try {
                  OutputStream os = new FileOutputStream(args[0]);
                  list.zipFlush(os);
                  os.close();
              } catch (IOException e) {
                  list.revertFlush();
                  System.err.println(e);
              }

       }
       public static void main1(String args[]){
              listKIR list=new listKIR("./var/kir");
              try {
                    Writer out = new BufferedWriter(new FileWriter(args[0]));
                    list.printFlush(out);
                    out.close();
              } catch (IOException e) {
                  list.revertFlush();
                  System.err.println(e);
              }

       }
}
