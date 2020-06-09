package com.digitalroute.input;

import java.io.InputStream;

import com.digitalroute.output.BillingGateway;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for {@link CallRecordsProcessorImpl}
 *
 * @author Khor Lee Yong, 01/12/2017
 */
@RunWith(MockitoJUnitRunner.class)
public class CallRecordsProcessorImplTest
{
    @InjectMocks
    private CallRecordsProcessorImpl processor;

    @Mock
    private BillingGateway billingGateway;

    @Test
    public void processBatch_oneShortOneLongCall_callsAggregatedInOrder()
        throws Exception
    {
        //Arrange
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("one_short_one_long_call");

        //Act
        processor.processBatch(inputStream);

        //Assert
        InOrder order = Mockito.inOrder(billingGateway);
        order.verify(billingGateway).beginBatch();
        order.verify(billingGateway).consume("1B", 1, "111111", "222222", (byte) 2, 5);
        order.verify(billingGateway).consume("1K", 4, "555555", "666666", (byte) 2, 32);
        order.verify(billingGateway).endBatch(37L);
        Mockito.verifyNoMoreInteractions(billingGateway);
    }

    @Test
    public void processBatch_callWithNoEndOfCall_callsAggregatedInOrder()
        throws Exception
    {

        //Arrange
        InputStream input = getClass().getClassLoader().getResourceAsStream("call_with_no_end_of_call");

        //Act
        processor.processBatch(input);

        //Assert
        InOrder order = Mockito.inOrder(billingGateway);
        order.verify(billingGateway).beginBatch();
        order.verify(billingGateway).consume("111K", 4, "555555", "666666", (byte) 1, 45);
        order.verify(billingGateway).endBatch(45L);
        Mockito.verifyNoMoreInteractions(billingGateway);
    }

    @Test
    public void processBatch_callWithDuplicateSequenceNumber_callsAggregatedAndErrorLoggedInOrder()
        throws Exception
    {
        //Arrange
        InputStream input = getClass().getClassLoader().getResourceAsStream("call_with_duplicate_sequence_number");

        //Act
        processor.processBatch(input);

        //Assert
        InOrder order = Mockito.inOrder(billingGateway);
        order.verify(billingGateway).beginBatch();
        order.verify(billingGateway).logError(BillingGateway.ErrorCause.DUPLICATE_SEQ_NO, "K", 21, "555555", "666666");
        order.verify(billingGateway).consume("K", 21, "555555", "666666", (byte) 1, 30);
        order.verify(billingGateway).endBatch(30L);
        Mockito.verifyNoMoreInteractions(billingGateway);
    }

    @Test
    public void processBatch_callWithMatchingAndNonMatchingPartialRecord__callsAggregatedInOrder()
        throws Exception
    {
        //Arrange
        InputStream input =
            getClass().getClassLoader().getResourceAsStream("calls_with_matching_and_non-matching_partial_record");

        //Act
        processor.processBatch(input);

        //Assert
        InOrder order = Mockito.inOrder(billingGateway);
        order.verify(billingGateway).beginBatch();
        order.verify(billingGateway).logError(BillingGateway.ErrorCause.NO_MATCH, "_", 13, "222233", "445566");
        order.verify(billingGateway).consume("7D", 12, "111111", "222222", (byte) 0, 40);
        order.verify(billingGateway).consume("1E", 11, "333333", "444444", (byte) 1, 55);
        order.verify(billingGateway).endBatch(95L);
        Mockito.verifyNoMoreInteractions(billingGateway);
    }
}
