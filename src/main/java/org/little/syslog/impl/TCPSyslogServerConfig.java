package org.little.syslog.impl;

import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServerConfig;

public class TCPSyslogServerConfig extends TCPNetSyslogServerConfig {
	private static final long serialVersionUID = 419756314866169408L;
	private printEvent log;

       public TCPSyslogServerConfig(printEvent _log){
              log=_log;
       }

       @Override
       public Class getSyslogServerClass() { return TCPSyslogServer.class; }
       public printEvent getLog() {return log;};
       
}
