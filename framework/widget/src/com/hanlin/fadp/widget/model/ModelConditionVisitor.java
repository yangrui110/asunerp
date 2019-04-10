/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") throws Exception ; you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.hanlin.fadp.widget.model;

import com.hanlin.fadp.widget.model.AbstractModelCondition.And;
import com.hanlin.fadp.widget.model.AbstractModelCondition.IfCompare;
import com.hanlin.fadp.widget.model.AbstractModelCondition.IfCompareField;
import com.hanlin.fadp.widget.model.AbstractModelCondition.IfEmpty;
import com.hanlin.fadp.widget.model.AbstractModelCondition.IfEntityPermission;
import com.hanlin.fadp.widget.model.AbstractModelCondition.IfHasPermission;
import com.hanlin.fadp.widget.model.AbstractModelCondition.IfRegexp;
import com.hanlin.fadp.widget.model.AbstractModelCondition.IfServicePermission;
import com.hanlin.fadp.widget.model.AbstractModelCondition.IfValidateMethod;
import com.hanlin.fadp.widget.model.AbstractModelCondition.Not;
import com.hanlin.fadp.widget.model.AbstractModelCondition.Or;
import com.hanlin.fadp.widget.model.AbstractModelCondition.Xor;
import com.hanlin.fadp.widget.model.ModelScreenCondition.IfEmptySection;

/**
 *  A <code>ModelCondition</code> visitor.
 */
public interface ModelConditionVisitor {

    void visit(And and) throws Exception;

    void visit(IfCompare ifCompare) throws Exception;

    void visit(IfCompareField ifCompareField) throws Exception;

    void visit(IfEmpty ifEmpty) throws Exception;

    void visit(IfEntityPermission ifEntityPermission) throws Exception;

    void visit(IfHasPermission ifHasPermission) throws Exception;

    void visit(IfRegexp ifRegexp) throws Exception;

    void visit(IfServicePermission ifServicePermission) throws Exception;

    void visit(IfValidateMethod ifValidateMethod) throws Exception;

    void visit(Not not) throws Exception;

    void visit(Or or) throws Exception;

    void visit(Xor xor) throws Exception;

    void visit(ModelMenuCondition modelMenuCondition) throws Exception;

    void visit(ModelTreeCondition modelTreeCondition) throws Exception;

    void visit(IfEmptySection ifEmptySection) throws Exception;
}
