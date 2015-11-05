/*
 * Sonar CCI Plugin
 * Copyright (C) 2015 Whitehorses
 * dev@sonar.codehaus.org
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.jdev.emg.sonar.cci;

import org.sonar.api.utils.SonarException;


/**
 * Simple RuntimeException used in the cci sonar plugin.
 * @author Wilfred van der Deijl
 */
public class CCIException extends SonarException {

    @SuppressWarnings("compatibility:1251977755862706243")
    private static final long serialVersionUID = 2L;

    /**
     * Creates an exception with a message and wrapping another exception.
     * @param s Message for this exception
     * @param throwable cause of this exception
     */
    public CCIException(String s, Throwable throwable) {
        super(s, throwable);
    }

}
