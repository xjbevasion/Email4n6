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

package com.github.email4n6.view.tabs.tree;

import com.github.email4n6.message.MessageRow;
import com.github.email4n6.message.factory.MessageFactory;
import com.github.email4n6.view.messagepane.MessagePane;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper around Runnable which allows me to "monitor" tasks because there's
 * no way to interact with the running tasks in an ExecutorService.
 *
 * @author Marten4n6
 */
@Slf4j
public final class TreeRunnable implements Runnable {

    private static final @Getter List<Task> activeTasks = Collections.synchronizedList(new ArrayList<>());

    private TreeRunnable() {
        throw new AssertionError("Don't.");
    }

    static TreeRunnable getMessageTask(MessagePane messagePane, MessageFactory messageFactory, TreeItem<TreeObject> item, boolean remove) {
        Task<List<MessageRow>> task = new Task<List<MessageRow>>() {
            @Override
            protected List<MessageRow> call() {
                // TODO - Only return a list of IDs instead?
                // Possibly faster to loop instead of removeAll
                // (constructing a MessageRow is expensive)

                return messageFactory.getMessagesFromTreeItem(item);
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (!isCancelled()) { // Don't do anything if this task was cancelled.
                        if (remove) {
                            List<MessageRow> previousSelection = getValue();

                            if (previousSelection != null) {
                                messagePane.getTable().getItems().removeAll(previousSelection);
                                log.debug("Removed {} messages from the message pane.", previousSelection.size());
                            }
                        } else {
                            List<MessageRow> folderMessages = getValue();

                            if (folderMessages == null) { // May return null if the tree item has no messages.
                                messagePane.getTable().getItems().clear();
                                log.debug("This folder has no messages.");
                            } else {
                                messagePane.getTable().getItems().addAll(folderMessages);
                                log.debug("Added {} messages to the message pane.", folderMessages.size());
                            }
                            messagePane.setLoading(false);
                        }
                    }
                });
            }

            @Override
            public String toString() {
                // Used to identify this thread (so we can stop the thread later).
                return item.getValue().getFolderID();
            }
        };
        return new TreeRunnable(task);
    }

    private final Task runnable;

    private TreeRunnable(Task runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        activeTasks.add(runnable);
        runnable.run();
        activeTasks.remove(runnable);
    }
}
