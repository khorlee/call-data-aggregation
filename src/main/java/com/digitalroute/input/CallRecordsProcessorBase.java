package com.digitalroute.input;

import java.io.IOException;
import java.io.InputStream;

import com.digitalroute.Application;
import org.apache.commons.io.IOUtils;


/**
 * Abstract class that implements {@link CallRecordsProcessor}
 *
 * @author Khor Lee Yong
 */
public abstract class CallRecordsProcessorBase
    implements CallRecordsProcessor
{
    /**
     * Should be used to process CDR(Call Data Records) batches from an {@link InputStream}. Look at {@link
     * Application#main(String[])} for an example
     *
     * @param in InputStream that should contain Call Data Records
     */
    public void processBatch(final InputStream inputStream)
        throws IOException
    {
        try {
            System.out.println(IOUtils.toString(inputStream));
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
