/*
 * Copyright (c) 2013, ENGEES. All rights reserved.
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
 *  - Jean-Rémy Falleri
 *  - Xavier Dolques
 *  
 *  this file contains code covered by the following terms:
 *  Copyright 2009 Jean-Rémy Falleri
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rcaexplore.conceptorder.optimized;

import org.rcaexplore.context.ObjectAttributeContext;

public class OptimizedConceptLattice extends OptimizedConceptOrder {

	protected OptimizedConcept top;
	
	protected OptimizedConcept bottom;
	
	public OptimizedConceptLattice(ObjectAttributeContext context) {
		super(context);
	}

	public OptimizedConcept getTop() {
		return top;
	}

	public void setTop(OptimizedConcept top) {
		this.top = top;
	}

	public OptimizedConcept getBottom() {
		return bottom;
	}

	public void setBottom(OptimizedConcept bottom) {
		this.bottom = bottom;
	}
    
	
}
