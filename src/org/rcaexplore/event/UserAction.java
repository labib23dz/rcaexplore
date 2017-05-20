/*
 * Copyright (c) 2014, ENGEES. All rights reserved.
 * This file is part of RCAExplore.
 * 
 *  RCAExplore is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  RCAExplore is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with RCAExplore.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  Authors : 
 *  - Xavier Dolques
 */
package org.rcaexplore.event;

public enum UserAction {
SET_AUTO_MODE,
SET_MANUAL_MODE, 
ADD_OA_CONTEXT,
REMOVE_OA_CONTEXT,
SET_OA_CONSTRUCTION_ALGO,
ADD_SCALING_OPERATOR, 
ADD_OO_CONTEXT, 
REMOVE_OO_CONTEXT, 
REMOVE_SCALING_OPERATOR,
STOP_PROCESS, 
SET_OA_CONSTRUCTION_ALGO_WITH_PARAM;
}
