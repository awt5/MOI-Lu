/**
 * Copyright (c) 2020 Jalasoft.
 *
 * This software is the confidential and proprietary information of Jalasoft.
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Jalasoft.
 */

package org.jalasoft.moi.model.core;

import org.jalasoft.moi.model.core.parameters.InputParameters;
import org.jalasoft.moi.model.core.parameters.ProcessResult;
import org.jalasoft.moi.model.core.parameters.Result;
import org.jalasoft.moi.model.exceptions.CommandBuildException;
import org.jalasoft.moi.model.exceptions.InputParametersException;
import org.jalasoft.moi.model.exceptions.ProcessIDException;
import org.jalasoft.moi.model.exceptions.ResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Class receives a string, executes it on cmd and returns output on a string.
 *
 * @author Mauricio Oroza
 *         Diego Perez
 *         Lucero Quiroga Perez
 * @version 1.2
 */
public class Executer {

    private static Logger LOGGER = LoggerFactory.getLogger(Handler.class);

    private ICacheProvider cache;
    private Result result;

    public Executer(ICacheProvider cache) {
        this.cache = cache;
        this.result = new ProcessResult();
    }

    /**
     * Executes command in cmd.
     *
     * @return The output of the console in one string in the form: String1 + \n + String1 + \n + ...
     * @throws CommandBuildException
     * @throws ResultException
     * @throws ProcessIDException
     */
    public Result execute(String command) throws CommandBuildException, ResultException, ProcessIDException {
        ProcessBuilder builder = new ProcessBuilder("sh", "-c", command);
        builder.redirectErrorStream(true);
        Process process;
        long pid;
        try {
            LOGGER.info("Running process with the next command: {}", command);
            process = builder.start();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new CommandBuildException(e);
        }
        try {
            pid = getPid(process);
        } catch (StringIndexOutOfBoundsException | NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
            throw new ProcessIDException(e);
        }
        cache.add(pid, process);
        LOGGER.info("Filling result");
        try {
            result.setPid(pid);
            result.setValue(buildResult(process.getInputStream()));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new ResultException(result, e);
        }
        return result;
    }

    /**
     * Processes an input to continue the execution.
     *
     * @param answer user input
     * @return a result value and the process id
     * @throws InputParametersException
     * @throws ResultException
     */
    public Result processAnswer(InputParameters answer) throws InputParametersException, ResultException {
        Process process;
        try {
            LOGGER.info("Process in use: pid={}", answer.getProcessId());
            LOGGER.info("User input={}", answer.getValue());
            process = cache.getProcessById(answer.getProcessId());
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(Objects.requireNonNull(process).getOutputStream()));
            writer.write(answer.getValue() + System.lineSeparator());
            writer.flush();
        } catch (IOException | NullPointerException e) {
            LOGGER.error(e.getMessage());
            throw new InputParametersException(e);
        }

        try {
            LOGGER.info("Filling result");
            result.setPid(answer.getProcessId());
            result.setValue(buildResult(process.getInputStream()));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new ResultException(result, e);
        }
        return result;
    }

    /**
     * Builds the result value.
     *
     * @param inputStream to obtain the result
     * @return result value
     * @throws IOException of system
     */
    private String buildResult(InputStream inputStream) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder builder = new StringBuilder();
        boolean isReady;
        int count = 0;
        int MAXIMUM_WAITING_VALUE = 1000000;
        while ((isReady = reader.ready()) || count < MAXIMUM_WAITING_VALUE) {
            if (isReady) {
                builder.append((char) reader.read());
            } else {
                count++;
            }
        }
        return builder.toString();
    }

    /**
     * Obtains the process id as long.
     *
     * @param process object
     * @return a process id
     */
    private Long getPid(Process process) throws NoSuchFieldException, IllegalAccessException {
        Field f = process.getClass().getDeclaredField("pid");
        f.setAccessible(true);
        Long result = f.getLong(process);
        f.setAccessible(false);
        return result;
    }
}
