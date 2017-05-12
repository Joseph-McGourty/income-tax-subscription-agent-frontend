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

package services.mocks

import connectors.AgentServicesConnector
import org.mockito.Mockito._
import services.ClientRelationshipService
import utils.MockTrait

import scala.concurrent.Future

trait MockClientRelationshipService extends MockTrait {
  object TestClientRelationshipService extends ClientRelationshipService(mockAgentServicesConnector)

  val mockAgentServicesConnector = mock[AgentServicesConnector]

  def setupAgentServicesConnector(nino: String)(isPreExistingRelationship: Boolean): Unit =
    when(mockAgentServicesConnector.isPreExistingRelationship(nino)).thenReturn(Future.successful(isPreExistingRelationship))

  def setupAgentServicesConnectorFailure(nino: String)(failure: Throwable): Unit =
    when(mockAgentServicesConnector.isPreExistingRelationship(nino)).thenReturn(Future.failed(failure))

  val testNino = "AA123456A"
}
