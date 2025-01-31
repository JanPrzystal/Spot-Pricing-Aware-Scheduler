/*
 * Copyright (c) 2024 AtLarge Research
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.opendc.compute.simulator.host;

import org.opendc.compute.api.TaskState;
import org.opendc.compute.simulator.service.ServiceTask;

/**
 * Listener interface for events originating from a {@link SimHost}.
 */
public interface HostListener {
    /**
     * This method is invoked when the state of <code>task</code> on <code>host</code> changes.
     */
    default void onStateChanged(SimHost host, ServiceTask task, TaskState newState) {}

    /**
     * This method is invoked when the state of a {@link SimHost} has changed.
     */
    default void onStateChanged(SimHost host, HostState newState) {}


    /**
     * This method is invoked when the price of a {@link SimHost} has changed.
     */
    default void onPriceChanged(SimHost host) {}
}
