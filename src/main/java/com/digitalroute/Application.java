// Copyright 2000-2017 Digital Route AB. All rights reserved.
// DIGITAL ROUTE AB PROPRIETARY/CONFIDENTIAL.
// Use is subject to license terms.
//

package com.digitalroute;

import java.io.IOException;
import java.io.InputStream;

import com.digitalroute.input.CallRecordsProcessor;
import com.digitalroute.input.CallRecordsProcessorImpl;
import com.digitalroute.output.BillingGatewayImpl;


public class Application
{
    public static final String IN_FILE = "INFILE_ascii_big";

    public static void main(String[] args)
        throws IOException
    {
        //Create an CallRecordsProcessor an feed it with an anonymous class, to debug its activity
        CallRecordsProcessor processor = new CallRecordsProcessorImpl(new BillingGatewayImpl());

        InputStream inputStream;
        try {
            inputStream = Application.class.getClassLoader().getResourceAsStream(IN_FILE);

            //perform processing
            processor.processBatch(inputStream);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
