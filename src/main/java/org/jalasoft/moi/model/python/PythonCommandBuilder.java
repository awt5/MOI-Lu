/**
 * Copyright (c) 2020 Jalasoft.
 * <p>
 * This software is the confidential and proprietary information of Jalasoft.
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Jalasoft.
 */

package org.jalasoft.moi.model.python;

import org.jalasoft.moi.model.core.ICommandBuilder;

import java.nio.file.Path;

/**
 * Builds commands to execute a python file.
 *
 * @author Lucero Quiroga Perez
 * @version 1.2
 */
public class PythonCommandBuilder implements ICommandBuilder {

    private final Path pythonPath;

    /**
     * Builds python commands.
     *
     * @param pythonPath the location for the python file
     */
    public PythonCommandBuilder(Path pythonPath) {
        this.pythonPath = pythonPath;
    }

    /**
     * Builds compilation and execution commands.
     *
     * @param path location of the directory or file
     * @return compilation and execution commands
     */
    @Override
    public String buildCommand(Path path) {
        return commandToCompile(path) + " && " + commandToRun(path);
    }

    /**
     * Builds compilation command.
     *
     * @param path the location of the directory or file
     * @return compilation command
     */
    private String commandToCompile(Path path) {
        return "python3 -m compileall " + path;
    }

    /**
     * Builds execution command.
     *
     * @param path location of the directory or file
     * @return execution command
     */
    private String commandToRun(Path path) {
        String fileMain = "/MainClass.py";
        return "python3 " + path + fileMain;
    }

    @Override
    public String toString() {
        return "PythonCommandBuilder{ " +
                pythonPath +
                " }";
    }
}
