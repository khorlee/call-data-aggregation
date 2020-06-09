package com.digitalroute.model;

/**
 * @author Khor Lee Yong
 */
public class CallRecord extends AbstractCallRecord
{
    public static final String INCOMPLETE_RECORD = "_";

    public CallRecord(final String pCallId, final int pSeqNum, final String pANum, final String pBNum,
        final int pDuration)
    {
        super(pCallId, pSeqNum, pANum, pBNum, pDuration);
    }
}
