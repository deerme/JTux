import jtux.tmib.TMIB;
import jtux.atmi.ATMI;
import jtux.atmi.TPEMIB;
import jtux.fml32.FML32;
import jtux.fml32.TPFBuilder;
import jtux.holders.ByteBufferHolder;
import jtux.TUX;
import java.nio.ByteBuffer;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Enumeration;

public class SampleConfig
{
    private static String jdbcConnectString = null;

    private static String jmsConnectString = null;

    private static boolean isWindows;
    
    private static String TUXDIR;
    private static String APPDIR;
    
    private static boolean hasWSL;
    
    private static File TUXCONFIG;
    private static File TLOG;
    private static File QSPACE;
    
    public static void main(String[] args)
    {
	init(args);

        config();
    }
    
    static void init(String[] args)
    {
	for (int i = 0; i < args.length; i++) {
	    String arg = args[i];
	    if (arg.startsWith("JDBC:")) {
		jdbcConnectString = arg.substring(5);
	    } else if (arg.startsWith("JMS:")) {
		jmsConnectString = arg.substring(4);
	    } else {
                System.err.println("ERROR: Invalid command line argument: "
		   + arg);
                System.exit(1);
	    }
	}

        isWindows = (File.separatorChar == '\\');
        
        TUXDIR = TUX.tuxgetenv("TUXDIR");
        if (TUXDIR == null) {
            System.err.println("ERROR: Environment variable TUXDIR not set");
            System.exit(1);
        }
        
        APPDIR = System.getProperty("user.dir");
        
        String WSL = isWindows ? "WSL.exe" : "WSL";
        
        hasWSL = new File(new File(TUXDIR, "bin"), WSL).exists();
        
        TUXCONFIG = new File(APPDIR, "TUXCONFIG");
        if (TUXCONFIG.exists()) {
            if (!TUXCONFIG.delete()) {
                System.err.println("ERROR: Failed to delete " + TUXCONFIG);
                System.exit(1);
            }
        }
        
        TLOG = new File(APPDIR, "TLOG");
        if (TLOG.exists()) {
            if (!TLOG.delete()) {
                System.err.println("ERROR: Failed to delete " + TLOG);
                System.exit(1);
            }
        }
        
        QSPACE = new File(APPDIR, "QSPACE");
        if (QSPACE.exists()) {
            if (!QSPACE.delete()) {
                System.err.println("ERROR: Failed to delete " + QSPACE);
                System.exit(1);
            }
        }
        
        TUX.tuxputenv("TUXCONFIG=" + TUXCONFIG);
        
        File tpadm = new File(new File(TUXDIR, "udataobj"), "tpadm");
        FML32.addFieldTable(tpadm);
    }
    
    static void config()
    {
        create("T_DOMAIN", new String[] {
            "TA_IPCKEY=51966",
            "TA_MODEL=SHM",
            "TA_MASTER=master"
        });
        
        update("T_MACHINE", new String[] {
            "TA_LMID=master",
            "TA_APPDIR=" + APPDIR,
            "TA_TLOGDEVICE=" + TLOG,
            "TA_ULOGPFX=" + new File(APPDIR, "ULOG"),
            "TA_MAXWSCLIENTS=10"
        });
        
        create("T_DEVICE", new String[] {
            "TA_CFGDEVICE=" + TLOG,
            "TA_DEVSIZE=200"
        });
        
        create("T_GROUP", new String[] {
            "TA_SRVGRP=JGRP",
            "TA_LMID=master",
            "TA_GRPNO=1"
        });
        
        create("T_SERVER", new String[] {
            "TA_SERVERNAME=JServer",
            "TA_SRVGRP=JGRP",
            "TA_SRVID=1",
            "TA_CLOPT=-- -classpath . -s TOUPPER SimpleServer"
        });
        
        create("T_SERVER", new String[] {
            "TA_SERVERNAME=JServer",
            "TA_SRVGRP=JGRP",
            "TA_SRVID=2",
            "TA_CONV=Y",
            "TA_CLOPT=-- -classpath . -s DOWNLOAD DownloadServer",
            "TA_MINDISPATCHTHREADS=3",
            "TA_MAXDISPATCHTHREADS=9"
        });
        
        create("T_SERVER", new String[] {
            "TA_SERVERNAME=JServer",
            "TA_SRVGRP=JGRP",
            "TA_SRVID=3",
            "TA_CLOPT=-- -classpath . -s NOTIFY_TOUPPER,BRDCST_TOUPPER"
	        + " UnsolServer"
        });
        
        create("T_SERVER", new String[] {
            "TA_SERVERNAME=JServer",
            "TA_SRVGRP=JGRP",
            "TA_SRVID=4",
            "TA_CLOPT=-- -classpath . -s PUBLISH_TIME TimePublisher"
        });
        
        create("T_SERVER", new String[] {
            "TA_SERVERNAME=JServer",
            "TA_SRVGRP=JGRP",
            "TA_SRVID=5",
            "TA_CLOPT=-- -classpath . -s FML32_TOUPPER SimpleFML32Server"
        });
        
        create("T_GROUP", new String[] {
            "TA_SRVGRP=EGRP",
            "TA_LMID=master",
            "TA_GRPNO=2"
        });
        
        create("T_SERVER", new String[] {
            "TA_SERVERNAME=TMUSREVT",
            "TA_SRVGRP=EGRP",
            "TA_SRVID=1",
            "TA_CLOPT=-A --"
        });
        
        create("T_DEVICE", new String[] {
            "TA_CFGDEVICE=" + QSPACE,
            "TA_DEVSIZE=2000"
        });
        
        create("T_APPQSPACE", new String[] {
            "TA_APPQSPACENAME=QSPACE",
            "TA_QMCONFIG=" + QSPACE,
            "TA_LMID=master",
            "TA_IPCKEY=47787",
            "TA_MAXMSG=10000",
            "TA_MAXPAGES=1000",
            "TA_MAXPROC=100",
            "TA_MAXQUEUES=50",
            "TA_MAXTRANS=100"
        });

        create("T_APPQ", new String[] {
            "TA_APPQSPACENAME=QSPACE",
            "TA_QMCONFIG=" + QSPACE,
            "TA_APPQNAME=TOUPPER"
        });
        
        create("T_APPQ", new String[] {
            "TA_APPQSPACENAME=QSPACE",
            "TA_QMCONFIG=" + QSPACE,
            "TA_APPQNAME=TOUPPER_RET"
        });
    
        create("T_GROUP", new String[] {
            "TA_SRVGRP=QGRP",
            "TA_LMID=master",
            "TA_GRPNO=3",
            "TA_TMSNAME=TMS_QM",
            "TA_OPENINFO=TUXEDO/QM:" + QSPACE + File.pathSeparator + "QSPACE"
        });
        
        create("T_SERVER", new String[] {
            "TA_SERVERNAME=TMQUEUE",
            "TA_SRVGRP=QGRP",
            "TA_SRVID=1",
            "TA_CLOPT=-s QSPACE:TMQUEUE -- -t 5"
        });

        create("T_SERVER", new String[] {
            "TA_SERVERNAME=TMQFORWARD",
            "TA_SRVGRP=QGRP",
            "TA_SRVID=2",
            "TA_CLOPT=-- -q TOUPPER -i 1 -n"
        });
        
        if (!hasWSL) {
            System.out.println("INFO: WSL not found, skipping WGRP");
            return;
        }
    
        create("T_GROUP", new String[] {
            "TA_SRVGRP=WGRP",
            "TA_LMID=master",
            "TA_GRPNO=4"
        });

	String localHost;
	
	try {
	    localHost = InetAddress.getLocalHost().getHostName();
	} catch (UnknownHostException eUnknownHost) {
	    throw new RuntimeException(eUnknownHost);
	}
        
        create("T_SERVER", new String[] {
            "TA_SERVERNAME=WSL",
            "TA_SRVGRP=WGRP",
            "TA_SRVID=1",
            "TA_CLOPT=-A -- -n //" + localHost + ":5120 -m 1"
        });

	if (jdbcConnectString != null) {
    
            create("T_GROUP", new String[] {
                "TA_SRVGRP=JDBCGRP",
                "TA_LMID=master",
                "TA_GRPNO=5",
                "TA_TMSNAME=JTMS",
		"TA_ENVFILE=" + new File(APPDIR, "JDBCGRP.env"),
                "TA_OPENINFO=JTux:JDBC:" + jdbcConnectString,
            });

	    create("T_SERVER", new String[] {
		"TA_SERVERNAME=JServer",
		"TA_SRVGRP=JDBCGRP",
		"TA_SRVID=1",
		"TA_CLOPT=-- -s FINDDEPT,MOVEDEPT:tpservice JDBCServer",
		"TA_MINDISPATCHTHREADS=3",
		"TA_MAXDISPATCHTHREADS=9"
	    });

	    create("T_SERVICE", new String[] {
		"TA_SERVICENAME=MOVEDEPT",
		"TA_AUTOTRAN=Y"
	    });

	    create("T_APPQ", new String[] {
		"TA_APPQSPACENAME=QSPACE",
		"TA_QMCONFIG=" + QSPACE,
		"TA_APPQNAME=DEPTMOVES"
	    });
	}

	if (jmsConnectString != null) {
    
            create("T_GROUP", new String[] {
                "TA_SRVGRP=JMSGRP",
                "TA_LMID=master",
                "TA_GRPNO=6",
                "TA_TMSNAME=JTMS",
		"TA_ENVFILE=" + new File(APPDIR, "JMSGRP.env"),
                "TA_OPENINFO=JTux:JMS:" + jmsConnectString,
            });

	    create("T_SERVER", new String[] {
		"TA_SERVERNAME=JServer",
		"TA_SRVGRP=JMSGRP",
		"TA_SRVID=1",
		"TA_CLOPT=-- -s JMSENQ,JMSDEQ:tpservice JMSServer",
		"TA_MINDISPATCHTHREADS=3",
		"TA_MAXDISPATCHTHREADS=9"
	    });

	    create("T_SERVICE", new String[] {
		"TA_SERVICENAME=JMSENQ",
		"TA_AUTOTRAN=Y"
	    });

	    create("T_SERVICE", new String[] {
		"TA_SERVICENAME=JMSDEQ",
		"TA_AUTOTRAN=Y"
	    });
	}

        /*
         * This fails on both Solaris 8 64-bit and HP-UX 11i 64-bit
	 * with TA_ERROR==-1 w/o TA_STATUS or any other info
         * TODO: Investigate why this happens.
         */

        clean("T_APPQSPACE", new String[] {
            "TA_APPQSPACENAME=QSPACE",
            "TA_QMCONFIG=" + QSPACE,
            "TA_LMID=master",
        });
    }

    static void create(String cls, String[] attributes)
    {
        set(cls, "NEW", attributes);
    }
    
    static void update(String cls, String[] attributes)
    {
        set(cls, null, attributes);
    }
    
    static void clean(String cls, String[] attributes)
    {
        set(cls, "CLEANING", attributes);
    }    
    
    static void set(String cls, String state, String[] attributes)
    {
        ByteBufferHolder fbfrRef = new ByteBufferHolder();
        fbfrRef.value = ATMI.tpalloc("FML32", null, 1024);
        try {
            TPFBuilder.I.FaddString(fbfrRef, TMIB.TA_OPERATION, "SET");
            TPFBuilder.I.FaddString(fbfrRef, TMIB.TA_CLASS, cls);
            if (state != null) {
                TPFBuilder.I.FaddString(fbfrRef, TMIB.TA_STATE, state);
                if (state.equals("CLEANING")) {
                    TPFBuilder.I.FaddInt(fbfrRef, TMIB.TA_FLAGS, 
		        TMIB.QMIB_FORCECLOSE);
                }
            }
            for (int i = 0; i < attributes.length; i++) {
                String attribute = attributes[i];
                int eqPos = attribute.indexOf('=');
                String key = attribute.substring(0, eqPos);
                String value = attribute.substring(eqPos + 1);
                int fieldID = FML32.Fldid(key);
                TPFBuilder.I.FaddString(fbfrRef, fieldID, value);
            }
            try {
                System.out.println("INFO: Executing " + cls + ".SET("
		     + state + ", " + toString(attributes) + ")");
                ATMI.tpadmcall(fbfrRef.value, fbfrRef, 0);
            } catch (TPEMIB eMIB) {
                System.err.println("ERROR: " + FML32.toString(fbfrRef.value));
                System.exit(1);
            }
        } finally {
            ATMI.tpfree(fbfrRef.value);
        }
    }
    
    static String toString(String[] strArray)
    {
        StringBuffer strBuffer = new StringBuffer();
        for (int i = 0; i < strArray.length; i++) {
            if (i > 0) {
                strBuffer.append(", ");
            }
            strBuffer.append(strArray[i]);
        }
        return strBuffer.toString();
    }
}
