/*
 * JMSService.java
 *
 * The JMSService class shown below provides the FML field definitions for
 * the JMSENQ and JMSDEQ services.
 */

import jtux.fml32.FML32;

public class JMSService
{
    public final static int QNAME = FML32.Fmkfldid(FML32.FLD_STRING, 20011);

    public final static int TEXT = FML32.Fmkfldid(FML32.FLD_STRING, 20012);
}
