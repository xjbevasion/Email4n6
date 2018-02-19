/*
 * This file is part of Email4n6.
 * Copyright (C) 2018  Marten4n6
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.email4n6.parser;

import com.github.email4n6.model.H2Database;
import com.github.email4n6.model.casedao.Case;
import com.github.email4n6.model.Indexer;
import com.github.email4n6.parser.spi.ParserConfiguration;
import com.github.email4n6.parser.spi.ParserFactory;
import com.github.email4n6.parser.view.LoadingStage;
import com.github.email4n6.view.tabs.tree.TreeObject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The class handles parsing the case's source files.
 *
 * @author Marten4n6
 */
@Slf4j
public class FileParser {

    private ParserFactory parserFactory;
    private Case currentCase;
    private LoadingStage loadingStage;

    private @Setter FinishedListener onParsingFinished;

    public FileParser(ParserFactory parserFactory, Case currentCase, LoadingStage loadingStage) {
        this.parserFactory = parserFactory;
        this.currentCase = currentCase;
        this.loadingStage = loadingStage;
    }

    /**
     * Calls the appropriate parsers on the files.
     *
     * @param sources The paths of the files to parse.
     */
    public void parseFiles(Set<String> sources) {
        Indexer indexer = new Indexer(currentCase.getName());

        sources.forEach(source -> {
            File file = new File(source);

            CountDownLatch countDownLatch = new CountDownLatch(1);
            AtomicInteger runningParsersForFile = new AtomicInteger(0);

            // Call all supported parsers on the file.
            parserFactory.getParsers(getFileExtension(file)).forEach(parser -> {
                runningParsersForFile.incrementAndGet();

                EventHandler<ActionEvent> finishedListener = (event) -> {
                    runningParsersForFile.decrementAndGet();

                    if (runningParsersForFile.get() == 0) {
                        countDownLatch.countDown(); // Continue to the next file.
                    }
                };

                ParserConfiguration configuration = ParserConfiguration.builder()
                        .currentCase(currentCase)
                        .indexer(indexer)
                        .loadingStage(loadingStage)
                        .finishedListener(finishedListener).build();

                int expectedFiles = 0;

                for (String filePath : sources) {
                    if (parser.getSupportedFileExtensions().contains(getFileExtension(new File(filePath)))) {
                        expectedFiles++;
                    }
                }

                log.debug("The \"{}\" parser is expecting {} file(s).", parser.getName(), expectedFiles);

                parser.parseFile(file, configuration, expectedFiles);
            });

            try {
                log.debug("Waiting for parsers to finish with \"{}\"...", file.getName());
                countDownLatch.await(); // Wait for parsers to finish before continuing.
            } catch (InterruptedException ex) {
                log.error(ex.getMessage(), ex);
            }
        });

        onParsingFinished.finished(currentCase, indexer, loadingStage);
    }

    private String getFileExtension(File file) {
        String extension = "";
        int i = file.getName().lastIndexOf('.');

        if (i >= 0) {
            extension = file.getName().substring(i + 1);
        }
        return extension;
    }

    /**
     * Listener which gets called when parsing finishes.
     */
    public interface FinishedListener {

        void finished(Case currentCase, Indexer indexer, LoadingStage loadingStage);
    }
}
