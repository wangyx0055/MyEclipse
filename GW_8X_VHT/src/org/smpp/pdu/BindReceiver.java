/*
 * Copyright (c) 1996-2001
 * Logica Mobile Networks Limited
 * All rights reserved.
 *
 * This software is distributed under Logica Open Source License Version 1.0
 * ("Licence Agreement"). You shall use it and distribute only in accordance
 * with the terms of the License Agreement.
 *
 */
package org.smpp.pdu;

import org.smpp.Data;
import org.smpp.util.*;


/**
 * @author Logica Mobile Networks SMPP Open Source Team
 * @version 1.0, 11 Jun 2001
 */

public class BindReceiver extends BindRequest
{
    public BindReceiver()
    {
        super(Data.BIND_RECEIVER);
    }
    
    protected Response createResponse()
    {
        return new BindReceiverResp();
    }

    public boolean isTransmitter()
    {
        return false;
    }

    public boolean isReceiver()
    {
        return true;
    }
}