package com.digitalroute.output;

/**
 * @author Khor Lee Yong
 */
public class BillingGatewayImpl
    implements BillingGateway
{
    /**
     * beginBatch should be called when starting to read a input file. Before aggregation is started
     *
     * @param callRecords
     */
    @Override
    public void beginBatch()
    {
        System.out.println("beginBatch");
    }

    /**
     * Consume a record from aggregation either by encountering causeForOutput '2' or when file processing is completed,
     * before calling {@link #endBatch(long)}
     *
     * @param callId Character representing the call
     * @param seqNum Highest seqNum in the aggregated call session
     * @param aNum From number
     * @param bNum To number
     * @param causeForOutput Highest cause for output unless it contains incomplete records, then it should contain 0
     * @param duration Accumulated duration for the aggregated call session
     */
    @Override
    public void consume(final String callId, final int seqNum, final String aNum, final String bNum,
        final byte causeForOutput, final int duration)
    {
        System.out.println(callId + ":" + seqNum + "," + aNum + "," + bNum + "," + causeForOutput + "," + duration);
    }

    /**
     * Should be called after all call records in a input file has been processed
     *
     * @param totalDuration Total duration of all the flushed call sessions
     */
    @Override
    public void endBatch(final long totalDuration)
    {
        System.out.println("Total Duration: " + totalDuration);
    }

    @Override
    public void logError(final ErrorCause errorCause, final String callId, final int seqNum, final String aNum,
        final String bNum)
    {
        System.out.println(
            errorCause + " error encountered for call ID: " + callId + " with seqNum: " + seqNum + " " + " aNum: "
                + aNum + " bNum: " + bNum);
    }
}
