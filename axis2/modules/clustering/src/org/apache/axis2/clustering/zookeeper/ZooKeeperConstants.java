/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.axis2.clustering.zookeeper;

/**
 * Contains constants that are needed
 *
 * @author pulasthi
 */
public class ZooKeeperConstants {

    public static final String COMMAND_BASE_NAME = "/command/command";
    public static final String COMMANDS_BASE_NAME = "/command";
    public static final String LAST_COMMAND_BASE_NAME = "/lastcommand";
    public static final String MEMBER_BASE_NAME = "/members";

    public static final String LOCAL_MEMBER_HOST = "localMemberHost";
    public static final String LOCAL_MEMBER_PORT = "localMemberPort";
}
