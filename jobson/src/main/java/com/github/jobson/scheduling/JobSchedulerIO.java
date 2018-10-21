/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.jobson.scheduling;

import com.github.jobson.api.persistence.JobId;
import com.github.jobson.internal.PersistedJob;

import java.util.Map;
import java.util.Set;

public interface JobSchedulerIO {
    Map<JobId, PersistedJob> getSubmittedJobs();
    Set<JobId> getExecutingJobs();  // scheduler is in position to resubmit dangling jobs after a crash
    void setJobStateAsSubmitted(JobId jobId);  // as above
}