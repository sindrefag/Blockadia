/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.thegaminghuskymc.sgf.fml.common.toposort;

import net.thegaminghuskymc.sgf.fml.common.EnhancedRuntimeException;
import net.thegaminghuskymc.sgf.fml.common.ModContainer;

import java.util.Set;

public class ModSortingException extends EnhancedRuntimeException
{
    private static final long serialVersionUID = 1L;

    public class SortingExceptionData<T>
    {
        public SortingExceptionData(T node, Set<T> visitedNodes)
        {
            this.firstBadNode = node;
            this.visitedNodes = visitedNodes;
        }

        private T firstBadNode;
        private Set<T> visitedNodes;

        public T getFirstBadNode()
        {
            return firstBadNode;
        }
        public Set<T> getVisitedNodes()
        {
            return visitedNodes;
        }
    }

    private SortingExceptionData<?> sortingExceptionData;

    public <T> ModSortingException(String string, T node, Set<T> visitedNodes)
    {
        super(string);
        this.sortingExceptionData = new SortingExceptionData<T>(node, visitedNodes);
    }

    @SuppressWarnings("unchecked")
    public <T> SortingExceptionData<T> getExceptionData()
    {
        return (SortingExceptionData<T>) sortingExceptionData;
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream)
    {
        SortingExceptionData<ModContainer> exceptionData = getExceptionData();
        stream.println("A dependency cycle was detected in the input mod set so an ordering cannot be determined");
        stream.println("The first mod in the cycle is " + exceptionData.getFirstBadNode());
        stream.println("The mod cycle involves:");
        for (ModContainer mc : exceptionData.getVisitedNodes())
        {
            stream.println(String.format("\t%s : before: %s, after: %s", mc.toString(), mc.getDependants(), mc.getDependencies()));
        }
    }

}