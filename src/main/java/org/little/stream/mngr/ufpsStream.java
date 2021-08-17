package org.little.stream.mngr;

public interface ufpsStream {

       public String getName();

       public boolean put(ufpsFrame msg)  throws InterruptedException;
       public ufpsFrame get();
       //public ufpsQueue getOut();

       public boolean open();
       public void    close();

}
