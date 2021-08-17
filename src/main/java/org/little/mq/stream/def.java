package org.little.mq.stream;



public class def{

  public static final java.lang.String CNFGQUEUE                            = "SYSTEM.STREAM.CONFIG";
  public static final java.lang.String CMDQUEUE                             = "SYSTEM.STREAM.COMMAND";
  public static final int              CMDWAIT                              = 15000;
  public static final int              CFGWAIT                              = 1000;
  public static final int              CFGINTERVAL                          = 1000;
  public static final int              CMDINTERVAL                          = 1000;
  public static final int              MSGWAIT                              = 15000;
  public static final int              MSGINTERVAL                          = 1000;
  public static final java.lang.String CMDSTOP                              = "stop routing";
  //public static final int              BFRLNGTH_INIT                        = 1024;
  public static final int              BFRLNGTH_INIT                        = 4194304;
  public static final int              BFRLNGTH_MAX                         = 314572800;


  public static final java.lang.String LOG_FORMAT_CSV                       = "CSV";
  public static final java.lang.String LOG_FORMAT_PLAIN                     = "PLAIN";
  
  public static final java.lang.String ERR_LOG_PATH_KEY                     = "ru.factorts.stream.errlog.path";
  
  public static final int              RET_OK                               = org.little.mq.mqapi.def.RET_OK   ;
  public static final int              RET_WARN                             = org.little.mq.mqapi.def.RET_WARN ;
  public static final int              RET_ERROR                            = org.little.mq.mqapi.def.RET_ERROR;
  public static final int              RET_FATAL                            = org.little.mq.mqapi.def.RET_FATAL;

  public static final int              CMD_INIT                             = 0;
  public static final int              CMD_RUN                              = 1;
  public static final int              CMD_STOP                             = -1;

                                                    
}                                                   
                                                    
                                                    
                                                    
                                                    
                                                    
                                                    
                                                    
                                                    
                                                    
                                                    