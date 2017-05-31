/*
 * Copyright 2017 HM Revenue & Customs
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

package connectors.mocks

import audit.Logging
import connectors.AgentServicesConnector
import play.api.libs.json.JsValue

trait MockAgentServicesConnector extends MockHttp {
  object TestAgentServicesConnector extends AgentServicesConnector(appConfig, mockHttpGet, mockHttpPut, app.injector.instanceOf[Logging])

  def mockIsPreExistingRelationship(arn: String, nino: String)(status: Int, response: Option[JsValue] = None): Unit =
    setupMockHttpGet(Some(TestAgentServicesConnector.agentClientURL(arn, nino)))(status, response)

  def mockCreateClientRelationship(arn: String, mtdid: String)(status: Int, response: Option[JsValue]): Unit =
    setupMockHttpPut(Some(TestAgentServicesConnector.createClientRelationshipURL(arn, mtdid)))(status, response)
}
