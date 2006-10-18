/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.websvc.wsitmodelext.rm;

import org.netbeans.modules.xml.wsdl.model.ExtensibilityElement;

/**
 *
 * @author Martin Grebac
 */
public interface RMAssertion extends ExtensibilityElement {
    public static final String RMASSERTION_PROPERTY = "RMRMASSERTION";     //NOI18N
    public static final String INACTIVITY_TIMEOUT_PROPERTY = "INACTIVITY_TIMEOUT";     //NOI18N
    public static final String BASE_RETRANSMISSION_INTERVAL_PROPERTY = "BASE_TRANSMISSION_INTERVAL";     //NOI18N
    public static final String EXPONENTIAL_BACKOFF_PROPERTY = "EXPONENTIAL_BACKOFF";     //NOI18N
    public static final String ACKNOWLEDGEMENT_INTERVAL_PROPERTY = "ACKNOWLEDGEMENT_INTERVAL";     //NOI18N
    
    InactivityTimeout getInactivityTimeout();
    void setInactivityTimeout(InactivityTimeout inactivityTimeout);
    void removeInactivityTimeout(InactivityTimeout inactivityTimeout);

    BaseRetransmissionInterval getBaseRetransmissionInterval();
    void setBaseRetransmissionInterval(BaseRetransmissionInterval baseRetransmissionInterval);
    void removeBaseRetransmissionInterval(BaseRetransmissionInterval baseRetransmissionInterval);
    
    ExponentialBackoff getExponentialBackoff();
    void setExponentialBackoff(ExponentialBackoff exponentialBackoff);
    void removeExponentialBackoff(ExponentialBackoff exponentialBackoff);

    AcknowledgementInterval getAcknowledgementInterval();
    void setAcknowledgementInterval(AcknowledgementInterval acknowledgementInterval);
    void removeAcknowledgementInterval(AcknowledgementInterval acknowledgementInterval);
}
