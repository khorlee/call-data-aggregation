package com.digitalroute.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.digitalroute.Application;
import com.digitalroute.model.CallRecord;
import com.digitalroute.model.CauseForOutput;
import com.digitalroute.output.BillingGateway;
import org.apache.commons.io.IOUtils;


/**
 * Implementation class for {@link CallRecordsProcessorBase}
 *
 * @author Khor Lee Yong
 */
public class CallRecordsProcessorImpl
    extends CallRecordsProcessorBase
{
    private final BillingGateway billingGateway;

    public CallRecordsProcessorImpl(final BillingGateway pBillingGateway)
    {
        super();
        billingGateway = pBillingGateway;
    }

    /**
     * Should be used to process CDR(Call Data Records) batches from an {@link InputStream}. Look at {@link
     * Application#main(String[])} for an example
     *
     * @param in InputStream that should contain Call Data Records
     */
    @Override
    public void processBatch(final InputStream inputStream)
        throws IOException
    {
        try {
            billingGateway.beginBatch();

            List<CallRecord> callRecords = parseCallRecords(inputStream);

            long totalDuration = aggregateCallRecords(callRecords);

            billingGateway.endBatch(totalDuration);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private List<CallRecord> parseCallRecords(final InputStream inputStream)
        throws IOException
    {
        String stringFromStream = IOUtils.toString(inputStream, "UTF-8");
        String[] aCall = stringFromStream.split("\n");

        Function<String, CallRecord> mapStringToCallRecords = aCallEntry -> {

            String[] callElements = aCallEntry.split("[:,]");
            String callId = callElements[0];
            String seqNum = callElements[1];
            String aNum = callElements[2];
            String bNum = callElements[3];
            String causeForOutput = callElements[4];
            String duration = callElements[5];

            CallRecord callRecord =
                parseCallRecords(callId, Integer.parseInt(seqNum), aNum, bNum, Byte.parseByte(causeForOutput),
                    Integer.parseInt(duration));

            return callRecord;
        };

        List<CallRecord> callRecords = Arrays.stream(aCall).map(mapStringToCallRecords).collect(Collectors.toList());

        return callRecords;
    }

    private long aggregateCallRecords(final List<CallRecord> pCallRecords)
    {
        long totalDuration = 0L;
        Map<String, CallRecord> aggregatedOngoingCalls = new HashMap<>();
        Set<Integer> processedSeqNumbers = new HashSet<Integer>();

        for (CallRecord callRecord : pCallRecords) {
            String key = groupBy(callRecord.getaNum(), callRecord.getbNum());
            int duration = callRecord.getDuration();

            // Duplicated Sequence Number
            if (processedSeqNumbers.contains(callRecord.getSeqNum())) {
                billingGateway.logError(BillingGateway.ErrorCause.DUPLICATE_SEQ_NO, callRecord.getCallId(),
                    callRecord.getSeqNum(), callRecord.getaNum(), callRecord.getbNum());
                continue;
            }

            processedSeqNumbers.add(callRecord.getSeqNum());

            if (aggregatedOngoingCalls.containsKey(key)) {
                CallRecord existingRecord = aggregatedOngoingCalls.get(key);

                String callId = callRecord.getCallId();

                if (callId.equalsIgnoreCase(existingRecord.getCallId()) || isIncompleteCall(callRecord)) {
                    duration += aggregatedOngoingCalls.get(key).getDuration();
                    callRecord.setCallId(existingRecord.getCallId());
                }
            }

            // Incomplete call with no match
            if (isIncompleteCall(callRecord)) {
                billingGateway.logError(BillingGateway.ErrorCause.NO_MATCH, callRecord.getCallId(),
                    callRecord.getSeqNum(), callRecord.getaNum(), callRecord.getbNum());
                continue;
            }

            // End of call
            if (callRecord.getCauseForOutput() == CauseForOutput.END_CALL) {
                callRecord.setDuration(duration);
                billingGateway.consume(callRecord.getCallId(), callRecord.getSeqNum(), callRecord.getaNum(),
                    callRecord.getbNum(), callRecord.getCauseForOutput().getId(), callRecord.getDuration());
                totalDuration += duration;
                aggregatedOngoingCalls.remove(key);
                continue;
            }

            callRecord.setDuration(duration);
            aggregatedOngoingCalls.put(key, callRecord);
            processedSeqNumbers.add(callRecord.getSeqNum());
        }
        totalDuration += processOngoingCalls(aggregatedOngoingCalls);

        return totalDuration;
    }

    private String groupBy(final String firstKey, String secondKey)
    {
        return firstKey.concat("-").concat(secondKey);
    }

    private int processOngoingCalls(final Map<String, CallRecord> pAggregatedOngoingCalls)
    {
        int totalDuration = 0;

        for (Map.Entry<String, CallRecord> entry : pAggregatedOngoingCalls.entrySet()) {
            CallRecord callRecord = entry.getValue();
            billingGateway.consume(callRecord.getCallId(), callRecord.getSeqNum(), callRecord.getaNum(),
                callRecord.getbNum(), callRecord.getCauseForOutput().getId(), callRecord.getDuration());

            totalDuration += callRecord.getDuration();
        }

        return totalDuration;
    }

    private CallRecord parseCallRecords(String callId, int seqNum, String aNum, String bNum, byte causeForOutput,
        int duration)
    {
        CallRecord callRecord = new CallRecord(callId, seqNum, aNum, bNum, duration);
        callRecord.setCauseForOutput(CauseForOutput.findCauseForOutput(causeForOutput));

        return callRecord;
    }

    private boolean isIncompleteCall(final CallRecord callRecord)
    {
        return callRecord.getCallId().equals(CallRecord.INCOMPLETE_RECORD);
    }
}
