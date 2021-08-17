package org.little.mq.file;

public interface cmdElement {

	   public void    clear();
       public void    open();
       public void    work();
       public void    close();
       public long    getCount();
       public boolean isRun();
       public void    isRun(boolean r);

}
