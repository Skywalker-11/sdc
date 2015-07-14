/*
 * Copyright (C) 2015 Michael Pietsch (Skywalker-11)
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
package de.sdc;

import de.sdc.commands.ResultCommand;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Queue for storing the tasks to run and their results
 *
 * @author Michael Pietsch (Skywalker-11)
 * @param <R> class of the result commands
 */
class TaskQueue< T extends Task, R extends ResultCommand> {

    private static final Logger log = LogManager.getLogger(TaskQueue.class);
    private static final transient ReentrantLock tasksLock = new ReentrantLock(true);

    private ArrayList<R> results = new ArrayList<>();
    private Queue<T> availableTasks = new ArrayDeque<>();
    private ArrayList<T> runningTasks = new ArrayList<>();
    private ArrayList<T> finishedTasks = new ArrayList<>();
    private final String logString = "{}: Available:{} Running:{} Finished:{} Results:{}";

    /**
     * @return an available task or null if no tasks are available
     */
    protected Task pollTask() {
        tasksLock.lock();
        if (availableTasks.isEmpty()) {
            tasksLock.unlock();
            return null;
        }

        T task;
        try {
            task = availableTasks.poll();
            runningTasks.add(task);
            log.debug(logString, "poll task", availableTasks.size(), runningTasks.size(), finishedTasks.size(), results.size());
        } finally {
            tasksLock.unlock();
        }
        return task;
    }

    /**
     * finishes a task by moving it from the running to the finished list and
     * stores the result from this
     *
     * @param task that should be finished
     * @param command the result to that command
     */
    protected void finishTask(T task, R command) {
        tasksLock.lock();
        try {
            runningTasks.remove(task);
            finishedTasks.add(task);
            results.add(command);
            log.debug(logString, "finish task", availableTasks.size(), runningTasks.size(), finishedTasks.size(), results.size());
        } finally {
            tasksLock.unlock();
        }
    }

    /**
     * resets the queue by removing the stored finished, available and running
     * tasks and results
     */
    protected void resetQueue() {
        tasksLock.lock();
        try {
            availableTasks = new ArrayDeque<>();
            runningTasks = new ArrayList<>();
            finishedTasks = new ArrayList<>();
            results = new ArrayList<>();
            log.debug(logString, "reset queues", availableTasks.size(), runningTasks.size(), finishedTasks.size(), results.size());
        } finally {
            tasksLock.unlock();
        }
    }

    /**
     * adds a task to the queue
     *
     * @param task the task that should be added
     */
    protected void addTask(T task) {
        tasksLock.lock();
        try {
            availableTasks.add(task);
            log.debug(logString, "add task", availableTasks.size(), runningTasks.size(), finishedTasks.size(), results.size());
        } finally {
            tasksLock.unlock();
        }
    }

    /**
     * if a client disconnects but doesn't finished his task add it to available
     * tasks again
     *
     * @param task task that hasn't been finished
     */
    protected void setTaskAvailable(T task) {
        tasksLock.lock();
        try {
            runningTasks.remove(task);
            availableTasks.add(task);
            log.debug(logString, "reset task", availableTasks.size(), runningTasks.size(), finishedTasks.size(), results.size());
        } finally {
            tasksLock.unlock();
        }
    }

    /**
     * @return true, if all queued task are finished
     */
    protected boolean allTasksFinished() {
        tasksLock.lock();
        boolean isfinished;
        try {
            isfinished = (availableTasks.size() + runningTasks.size()) == 0;
            log.trace(logString, "check for finished tasks", availableTasks.size(), runningTasks.size(), finishedTasks.size(), results.size());
        } finally {
            tasksLock.unlock();
        }
        return isfinished;
    }

    /**
     *
     * @return returns the results received by the clients
     */
    protected List<R> getResults() {
        List<R> resultList;
        tasksLock.lock();
        try {
            resultList = results;
            log.trace(logString, "get results", availableTasks.size(), runningTasks.size(), finishedTasks.size(), results.size());
        } finally {
            tasksLock.unlock();
        }
        return resultList;

    }

    /**
     *
     * @return returns the percentage of finished tasks or -1 if 0 tasks are
     * addedS
     */
    protected double getProgress() {
        double taskCount = (availableTasks.size() + runningTasks.size() + finishedTasks.size());
        double percentage = (taskCount == 0d ? -1d : finishedTasks.size() / taskCount);
        return percentage;
    }
}
