package com.digitalroute.model;

/**
 * @author Khor Lee Yong
 */
public abstract class AbstractCallRecord
{
    private String callId;

    private int seqNum;

    private String aNum;

    private String bNum;

    private CauseForOutput causeForOutput;

    private int duration;

    public AbstractCallRecord(final String pCallId, final int pSeqNum, final String pANum, final String pBNum,
        final int pDuration)
    {
        callId = pCallId;
        seqNum = pSeqNum;
        aNum = pANum;
        bNum = pBNum;
        duration = pDuration;
    }

    public String getCallId()
    {
        return callId;
    }

    public void setCallId(final String pCallId)
    {
        callId = pCallId;
    }

    public int getSeqNum()
    {
        return seqNum;
    }

    public void setSeqNum(final int pSeqNum)
    {
        seqNum = pSeqNum;
    }

    public String getaNum()
    {
        return aNum;
    }

    public void setaNum(final String pANum)
    {
        aNum = pANum;
    }

    public String getbNum()
    {
        return bNum;
    }

    public void setbNum(final String pBNum)
    {
        bNum = pBNum;
    }

    public CauseForOutput getCauseForOutput()
    {
        return causeForOutput;
    }

    public void setCauseForOutput(final CauseForOutput pCauseForOutput)
    {
        causeForOutput = pCauseForOutput;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration(final int pDuration)
    {
        duration = pDuration;
    }
}
