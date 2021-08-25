package org.little.stream.ufps;


//import prj0.util.Logger;

public class ufpsMsgField{
  public static final String H_ENVELOPE                           =   "Envelope"; 
  public static final String H_BODY                               = "Body";
  public static final String H_HEADER                             = "Header";
  public static final String H_NAME_TO                            = "To";
  public static final String H_NAME_FROM                          = "From";
  public static final String H_MESSAGE_INFO_TAG                   = "MessageInfo";
  public static final String H_MESSAGE_TYPE                       = "MessageType";
  public static final String H_PRIORITY                           = "Priority";
  public static final String H_MESSAGE_ID                         = "MessageID";
  public static final String H_LEGACY_TRANSPORT_FILE_NAME         = "LegacyTransportFileName";
  public static final String H_CREATE_TIME                        = "CreateTime";
  public static final String H_APPLICATION_MESSAGE_ID             = "AppMessageID"        ;
  public static final String H_CORRELATION_MESSAGE_ID             = "CorrelationMessageID";
  public static final String H_SEND_TIME                          = "SendTime"            ;
  public static final String H_RECEIVE_TIME                       = "ReceiveTime"         ;
  public static final String H_ACCEPT_TIME                        = "AcceptTime"          ;
  public static final String H_ACKNOLEDGE_REQUEST                 = "AckRequest"          ;
                                                                                         
  public static final String H_DOC_INFO                           = "DocInfo"           ;
  public static final String H_DOC_FORMAT                         = "DocFormat"           ;
  public static final String H_DOC_TYPE                           = "DocType"             ;
  public static final String H_DOC_ID                             = "DocID"               ;
  public static final String H_DOC_REFID                          = "EDRefID"             ;
  public static final String H_DOC_EDNO                           = "EDNo"                ;
  public static final String H_DOC_EDDATE                         = "EDDate"              ;
  public static final String H_DOC_EDAUTHOR                       = "EDAuthor"            ;

  
  public static final int    RET_OK                               = 1;
  public static final int    RET_WARN                             = 0 ;
  public static final int    RET_ERROR                            = -1;
  public static final int    RET_FATAL                            = -2;

                                                    
}                                                   
                                                    
                                                    
/*
  <env:Header>
      <props:MessageInfo xmlns:props="urn:cbr-ru:msg:props:v1.3">
           <props:To>uic:040700199900</props:To>
           <props:From>uic:049999900000</props:From>
           <props:MessageID>450295413-0000004099013</props:MessageID>
           <props:MessageType>1</props:MessageType>
           <props:Priority>5</props:Priority>
           <props:CreateTime>2015-12-16T19:50:08Z</props:CreateTime>
      </props:MessageInfo>
      <props:DocInfo xmlns:props="urn:cbr-ru:msg:props:v1.3">
           <props:DocFormat>2</props:DocFormat>
           <props:DocType>MT865</props:DocType>
           <props:DocID>450295413-0000004099013</props:DocID>
      </props:DocInfo>
   </env:Header>

*/                                                    
                                                    
                                                    
                                                    
                                                    
                                                    
                                                    
                                                    