/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
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
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.service.RunningService;
import com.hanlin.fadp.service.engine.GenericEngine;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilProperties;

uiLabelMap = UtilProperties.getResourceBundleMap("WebtoolsUiLabels", locale);
uiLabelMap.addBottomResourceBundle("CommonUiLabels");

threads = [];
poolState = dispatcher.getJobManager().getPoolState();
context.poolState = poolState;
context.threads = poolState.taskList;

// Some stuff for general threads on the server
currentThread = Thread.currentThread();
currentThreadGroup = currentThread.getThreadGroup();
topThreadGroup = currentThreadGroup;
while (topThreadGroup.getParent()) {
    topThreadGroup = topThreadGroup.getParent();
}

Thread[] allThreadArray = new Thread[1000];
topThreadGroup.enumerate(allThreadArray);
allThreadList = Arrays.asList(allThreadArray);

context.allThreadList = allThreadList;
