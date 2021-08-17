package org.little.stream.ufps;

public class ufpsBody {

       private String body;
     

       public ufpsBody() {
              clear();
               
       }
       public void clear() {
              body        ="";
       }

       public void   set(String arg){body   =arg;}  
       public String get(){return body;}     


       public String toString(){
                     return 
                           " body:"+get()        
                     ;
       }

       public static void main(String[] args) throws Exception {

       }
}



/*
<?xml version="1.0" encoding="WINDOWS-1251"?>
<env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope">
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
   <env:Body>
   <Object xmlns="urn:cbr-ru:dc:v1.0">ezE6DQo6VFlQRTo4NjUNCjpUTzo5OTk5OTk5OTkwMDANCjpGUk9NOjA0MDQ5OTk5OTAxMw0KOklEOjA0MDQ5OTk5OTAxMzIwMTUxMjE2NDUwMjk1NDEzLTAwMDAwMDQwOTkwMTMvMDAwMDAwMDAwMQ0KOlJFRklEOjAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwLzAwMDAwMDAwMDB9DQp7NDoNCjozMDA6OTYwMDAxDQo6MzQ0OjIxj66r4+eo4uwgpKCtreulfXs1OntGQUM6MzA4MjAxNjQwNjA5MkE4NjQ4ODZGNzBEMDEwNzAyQTA4MjAxNTUzMDgyMDE1MTAyMDEwMTMxMEYzMDBEMDYwOTJCMDYwMTA0MDE5QzU2MDEwMTA1MDAzMDBCMDYwOTJBODY0ODg2RjcwRDAxMDcwMTMxODIwMTJDMzA4MjAxMjgwMjAxMDEzMDU4MzA0NDMxMEIzMDA5MDYwMzU1MDQwNjEzMDI1MjU1MzEwQjMwMDkwNjAzNTUwNDA4MTMwMjMwMzQzMTBDMzAwQTA2MDM1NTA0MEExMzAzNDM0MjUyMzEwRDMwMEIwNjAzNTUwNDBCMTMwNDU1NDI1QTQ5MzEwQjMwMDkwNjAzNTUwNDAzMTMwMjQzNDEwMjEwNDAzNjEwQjc3RDBCRDVBRUI3NDI3N0QxNTUyMUYxRTAzMDBEMDYwOTJCMDYwMTA0MDE5QzU2MDEwMTA1MDBBMDY5MzAxODA2MDkyQTg2NDg4NkY3MEQwMTA5MDMzMTBCMDYwOTJBODY0ODg2RjcwRDAxMDcwMTMwMUMwNjA5MkE4NjQ4ODZGNzBEMDEwOTA1MzEwRjE3MEQzMTM1MzEzMjMxMzYzMTM5MzUzMDMxMzM1QTMwMkYwNjA5MkE4NjQ4ODZGNzBEMDEwOTA0MzEyMjA0MjAzREFBNzk0OUM2MzE0NkNGRThCQkI3MEM2NUQ5RDQ5NkUxODlFNTgyNTk1Q0ZEMzUyNjkxRDAzQjBCQUQzQTQwMzAwRDA2MDkyQjA2MDEwNDAxOUM1NjAxMDIwNTAwMDQ0MDkyOEUwRDlEOTUyOEE2Nzc2RUVGRkU5NjcwNzlFNEE2ODI2OTQ0MDcyOTFGNkMwMzRDMTU5MjVGQjAwRTUwNzM3MDQyQjVBRkE0NDYxRDJENEZDQzg0NkQzQjRBM0E4NkVGQUE0ODhDODgxRjdFOEJEODVCMEJGMEY1QjlCQUU0fX0=</Object>
   </env:Body>

</env:Envelope>

*/
