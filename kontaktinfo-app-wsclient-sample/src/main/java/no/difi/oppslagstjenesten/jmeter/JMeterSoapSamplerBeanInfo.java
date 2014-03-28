/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package no.difi.oppslagstjenesten.jmeter;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.PasswordEditor;
import org.apache.jmeter.testbeans.gui.TextAreaEditor;

import java.beans.PropertyDescriptor;

public class JMeterSoapSamplerBeanInfo extends BeanInfoSupport {

    // These names must agree case-wise with the variable and property names
    private static final String PROTOCOL = "protocol"; //$NON-NLS-1$
    private static final String HOST     = "host";     //$NON-NLS-1$
    private static final String PORT     = "port";     //$NON-NLS-1$
    private static final String PATH     = "path";     //$NON-NLS-1$
    private static final String DATA     = "data";     //$NON-NLS-1$
    private static final String USER     = "user";     //$NON-NLS-1$
    private static final String PASSWORD = "password"; //$NON-NLS-1$


    public JMeterSoapSamplerBeanInfo() {
        super(KontaktRegisterSampler.class);

        //ResourceBundle rb = (ResourceBundle) getBeanDescriptor().getValue(RESOURCE_BUNDLE);

        // These must match with the resources
        createPropertyGroup("webservice_config",       //$NON-NLS-1$
                new String[] {  PROTOCOL, HOST, PORT, PATH, USER, PASSWORD, DATA });

        PropertyDescriptor p = property(PROTOCOL);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "http");
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        
        p = property(HOST);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");        //$NON-NLS-1$
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);

        p = property(PORT);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");        //$NON-NLS-1$
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);

        p = property(PATH);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");        //$NON-NLS-1$
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);

        p = property(USER);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");        //$NON-NLS-1$
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);

        p = property(PASSWORD);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");        //$NON-NLS-1$
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p.setPropertyEditorClass(PasswordEditor.class);

        p = property(DATA);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");        //$NON-NLS-1$
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p.setPropertyEditorClass(TextAreaEditor.class);

    }
}
