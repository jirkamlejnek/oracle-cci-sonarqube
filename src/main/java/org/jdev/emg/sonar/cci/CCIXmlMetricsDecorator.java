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

import java.io.File;
import java.io.IOException;

import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.SonarException;

/**
 * Class based on the JavaMetricsDecorator. For handling
 * @see org.adf.emg.sonar.ojaudit.XmlMetricsDecorator 
 */
public class CCIXmlMetricsDecorator implements Decorator {

    private static final Logger LOG = LoggerFactory.getLogger(CCIXmlMetricsDecorator.class);

    @Override
    public void decorate(Resource resource, DecoratorContext context) {
        if (!Qualifiers.isFile(resource)) {
            return;
        }
        ProjectFileSystem fileSystem = context.getProject().getFileSystem();
        File file = lookup(resource, fileSystem);

        try {
            if (readFirstByte(file) != '<') {
                return;
            }
        } catch (IOException e) {
            throw new SonarException(e);
        }

        int numCommentLines;
        CCICountCommentParser commentCounter = new CCICountCommentParser();
        try {
            numCommentLines = commentCounter.countLinesOfComment(FileUtils.openInputStream(file));
            if (numCommentLines == -1) {
                return;
            }
        } catch (IOException e) {
            throw new SonarException(e);
        }

        LineIterator iterator = null;
        int numLines = 0;
        int numBlankLines = 0;
        try {
            Charset charset = fileSystem.getSourceCharset();
            iterator = charset == null ? FileUtils.lineIterator(file) : FileUtils.lineIterator(file, charset.name());
            while (iterator.hasNext()) {
                String line = iterator.nextLine();
                numLines++;
                if (line.trim().isEmpty()) {
                    numBlankLines++;
                }
            }
        } catch (IOException e) {
            LOG.warn("error reading " + file + " to collect metrics", e);
        } finally {
            LineIterator.closeQuietly(iterator);
        }

        context.saveMeasure(CoreMetrics.LINES, (double) numLines); // Lines
        context.saveMeasure(CoreMetrics.COMMENT_LINES, (double) numCommentLines); // Non Commenting Lines of Code
        context.saveMeasure(CoreMetrics.NCLOC, (double) numLines - numBlankLines - numCommentLines); // Comment Lines
    }

    private File lookup(Resource resource, ProjectFileSystem filesys) {
        return filesys.resolvePath(resource.getLongName());
    }

    private byte readFirstByte(File file) throws IOException {
        byte[] buffer = new byte[1];
        int len = IOUtils.read(FileUtils.openInputStream(file), buffer);
        return len == 1 ? buffer[0] : null;
    }

    /**
     * Determines if this Sensor should run for a given project.
     * @param project Project
     * @return <code>true</code> if the supplied project uses cci as language, otherwise <code>false</code>
     * @see CCIPlugin#LANGUAGE_KEY
     */
    @Override
    public boolean shouldExecuteOnProject(Project project) {
        boolean retval = CCIPlugin.LANGUAGE_KEY.equals(project.getLanguageKey());
        if (!retval) {
            LOG.debug(this.getClass().getName() + " not executing on project with language " +
                      project.getLanguageKey());
        }
        return retval;
    }

}
