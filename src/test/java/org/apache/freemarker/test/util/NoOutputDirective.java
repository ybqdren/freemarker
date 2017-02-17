/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.freemarker.test.util;

import java.io.IOException;
import java.util.Map;

import org.apache.freemarker.core.TemplateException;
import org.apache.freemarker.core.ast.Environment;
import org.apache.freemarker.core.model.TemplateDirectiveBody;
import org.apache.freemarker.core.model.TemplateDirectiveModel;
import org.apache.freemarker.core.model.TemplateModel;
import org.apache.freemarker.core.model.TemplateModelException;
import org.apache.freemarker.core.util._NullWriter;

public class NoOutputDirective implements TemplateDirectiveModel {

    public static final NoOutputDirective INSTANCE = new NoOutputDirective(); 
    
    private NoOutputDirective() {
        //
    }

    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        if (!params.isEmpty()) {
            throw new TemplateModelException("This directivey doesn't support any parameters.");
        }
        body.render(_NullWriter.INSTANCE);
    }

}
