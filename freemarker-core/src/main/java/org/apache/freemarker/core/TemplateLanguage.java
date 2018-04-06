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

package org.apache.freemarker.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.freemarker.core.outputformat.OutputFormat;
import org.apache.freemarker.core.util._NullArgumentException;
import org.apache.freemarker.core.util._StringUtils;

/**
 * Represents a template language. Currently this class is not mature, so it can't be implemented outside FreeMarker,
 * also its methods shouldn't be called from outside FreeMarker.
 */
// [TODO][FM3] Make this mature, or hide its API somehow, or at least prevent subclassing it by user.
public abstract class TemplateLanguage {
    
    private final String fileExtension;
    private final OutputFormat outputFormat;
    private final AutoEscapingPolicy autoEscapingPolicy;
    
    // Package visibility to prevent user implementations until this API is mature.

    /**
     * Returns if the template can specify its own charset inside the template. If so, the parser can throw
     * {@link WrongTemplateCharsetException}, and it might gets a non-{@code null} for the {@link InputStream}
     * parameter.
     */
    public abstract boolean getCanSpecifyEncodingInContent();

    /**
     * @param fileExtension
     *            See {@link #getFileExtension()}
     * @param outputFormat
     *            See {@link #getOutputFormat(Configuration)}
     * @param autoEscapingPolicy
     *            See {@link #getAutoEscapingPolicy()}
     * 
     * @throws IllegalArgumentException
     *             If the {@code #fileExtension} argument contains upper case letter or dot, or if it starts with "f"
     *             and the language isn't defined by the FreeMarker project.
     */
    public TemplateLanguage(String fileExtension, OutputFormat outputFormat, AutoEscapingPolicy autoEscapingPolicy) {
        this(fileExtension, false, outputFormat, autoEscapingPolicy);
    }
    
    /**
     * Non-public constructor used for languages defiend by the FreeMarker project.
     */
    TemplateLanguage(String fileExtension, boolean allowExtensionStartingWithF,
            OutputFormat outputFormat, AutoEscapingPolicy autoEscapingPolicy) {
        _NullArgumentException.check("fileExtension", fileExtension);
        for (int i = 0; i < fileExtension.length(); i++) {
            char c = fileExtension.charAt(i);
            if (Character.isUpperCase(c)) {
                throw new IllegalArgumentException("The \"fileExtension\" argument can't contain upper case letter.");
            }
            if (c == '.') {
                throw new IllegalArgumentException("The \"fileExtension\" argument can't contain dot.");
            }
        }
        if (!allowExtensionStartingWithF && fileExtension.length() > 0 && fileExtension.charAt(0) == 'f') {
            throw new IllegalArgumentException(
                    "The \"fileExtension\" argument can't start with 'f' for an user-defined language.");
        }
        this.fileExtension = fileExtension;
        this.outputFormat = outputFormat;
        this.autoEscapingPolicy = autoEscapingPolicy;
    }

    /**
     * This is what
     * {@link Template#Template(String, String, InputStream, Charset, Reader, Configuration, TemplateConfiguration,
     * OutputFormat, AutoEscapingPolicy)} calls internally to do the actual parsing.
     */
    // TODO [FM3] This is not the final API, or else it can't be public (because ASTElement isn't public for example).
    public abstract ASTElement parse(Template template, Reader reader,
            ParsingConfiguration pCfg, OutputFormat contextOutputFormat, AutoEscapingPolicy contextAutoEscapingPolicy,
            InputStream streamToUnmarkWhenEncEstabd)
            throws IOException, ParseException;

    /**
     * The file extension that should be used for this language. Not {@code null}. It can't contain upper-case letters,
     * nor dot. It can't start with "f", unless it's defined by the FreeMarker project itself. 
     */
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public final String toString() {
        return "TemplateLanguage(" + _StringUtils.jQuote(fileExtension) + ")";
    }
    
    /**
     * The {@link OutputFormat} that this language enforces, or else {@code null} (and it comes from the
     * {@link ParsingConfiguration}).
     * 
     * @param cfg
     *            The {@link Configuration} used; this is needed for {@link Configuration}-level {@link OutputFormat}
     *            overrides to work (such as when someone provides an alternative implementation for "HTML" escaping).
     *            If this is {@code null}, you get back the original {@link OutputFormat} object of this
     *            {@link TemplateLanguage}, but you hardly ever should do that. 
     */
    public OutputFormat getOutputFormat(Configuration cfg) {
        return cfg != null && outputFormat != null ? cfg.getCustomOrArgumentOutputFormat(outputFormat) : outputFormat;
    }

    /**
     * The {@link OutputFormat} that this language enforces, or else {@code null} (and it comes from the
     * {@link ParsingConfiguration})
     */
    public AutoEscapingPolicy getAutoEscapingPolicy() {
        return autoEscapingPolicy;
    }

}