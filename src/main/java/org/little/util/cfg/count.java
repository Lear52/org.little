package org.little.util.cfg;
        
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
 

/**
 * @author av
 *
 */
public class count {
       private static final Logger logger = LoggerFactory.getLogger(count.class);

       private static ReentrantReadWriteLock locallock = new ReentrantReadWriteLock();
       private static Lock                   readLock = locallock.readLock();
       //private static Lock                   writeLock = locallock.writeLock();
       private String                        filename;
       private RandomAccessFile              file;
       private FileChannel                   channel;
       private FileLock                      lock;

       public count(String filename){
              this.filename=filename;
              file=null;
              channel=null;
              lock=null;
       }

       public void open(){
              try {
                  file=new RandomAccessFile(filename, "rws");
              } catch (FileNotFoundException e) {
                  logger.error("ex:"+new Except("open count file",e));
                  file=null;
                  channel=null;
                  lock=null;
                  return;        
              }
              channel = file.getChannel();
       }
       public long inc0(long delta){
              long c=0;
              try {
                   channel.position(0);
                   lock=channel.lock();
                   ByteBuffer buf=ByteBuffer.allocate(64);
                   channel.read(buf);
                   buf.flip();
                   byte [] _buf=new byte[buf.limit()];
                   buf.get(_buf);
                   try {c=Long.parseLong((new String(_buf)).trim(), 10);}catch(Exception e) {c=0;}
                   c+=delta;
                   _buf=Long.toString(c).getBytes();
                   file.setLength(0);
                   buf=ByteBuffer.allocate(64);
                   buf.put(_buf);                     
                   buf.flip();
                   channel.position(0);
                   channel.write(buf);
              } catch (IOException ex) {
                   logger.error("error i/o count ex:"+ex);
              }
              finally {
                      unlock();
              }
              return c;
       }
       public long inc(long delta){
              long c=0;
              try {
                   c=_get();
                   c+=delta;
                   _set(c);
              } 
              finally {
                      unlock();
              }
              return c;
       }
       public long _get(){
              long c=0;
              try {
                   channel.position(0);
                   lock();
                   ByteBuffer buf=ByteBuffer.allocate(64);
                   channel.read(buf);
                   buf.flip();
                   byte [] _buf=new byte[buf.limit()];
                   buf.get(_buf);
                   try {c=Long.parseLong((new String(_buf)).trim(), 10);}catch(Exception e) {c=0;}
              } catch (IOException ex) {
                   logger.error("error i/o count ex:"+ex);
              }
              return c;
       }
       public void _set(long c){
              try {
                   byte [] _buf=Long.toString(c).getBytes();
                   lock();
                   file.setLength(0);
                   ByteBuffer buf=ByteBuffer.allocate(_buf.length);
                   buf.put(_buf);                     
                   buf.flip();
                   channel.position(0);
                   channel.write(buf);
              } catch (IOException ex) {
                   logger.error("error i/o count ex:"+ex);
              }
              finally {
                      unlock();
              }
       }
       public void lock(){
              readLock.lock();
               //writeLock.lock();
              if(lock==null) try {lock=channel.lock();} catch (IOException e) {}
           
       }
       public void unlock(){
              if(lock!=null)try {lock.release();} catch (IOException e) {}
              lock=null;
              readLock.unlock();
               //writeLock.unlock();
       }
       public void close(){
              unlock();
              if(channel!=null) {              
                 try {channel.close();} catch (IOException e) {}
                  channel=null;
              }
              if(file!=null) {
                 try {file.close();} catch (IOException e) {}
                 file=null;
              }

       }

       public static void main(String[] args) throws Exception{
              count l=new count("test.cnt");
              l.open();
              for(int i=0;i<300000;i++) {
                  long c=l.inc(1);
                  System.out.println(" is ok ="+c);
              }
              l.close();
       }
}

