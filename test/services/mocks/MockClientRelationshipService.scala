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
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import services.ClientRelationshipService
import uk.gov.hmrc.play.http.HeaderCarrier
import utils.MockTrait

import scala.concurrent.Future

trait MockClientRelationshipService extends MockTrait {
  object TestClientRelationshipService extends ClientRelationshipService(mockAgentServicesConnector)

  val mockAgentServicesConnector = mock[AgentServicesConnector]

  val mockClientRelationshipService = mock[ClientRelationshipService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAgentServicesConnector)
    reset(mockClientRelationshipService)
  }

  def setupCreateClientRelationship(arn: String, mtdid: String): Unit =
    when(mockClientRelationshipService
      .createClientRelationship(ArgumentMatchers.eq(arn), ArgumentMatchers.eq(mtdid))(ArgumentMatchers.any[HeaderCarrier])
    ).thenReturn(Future.successful(()))

  def setupCreateClientRelationshipFailure(arn: String, mtdid: String)(failure: Throwable): Unit =
    when(mockClientRelationshipService
      .createClientRelationship(ArgumentMatchers.eq(arn), ArgumentMatchers.eq(mtdid))(ArgumentMatchers.any[HeaderCarrier])
    ).thenReturn(Future.failed(failure))

  object MockConnectorSetup {
    def preExistingRelationship(nino: String)(isPreExistingRelationship: Boolean): Unit =
      when(mockAgentServicesConnector.isPreExistingRelationship(nino)).thenReturn(Future.successful(isPreExistingRelationship))

    def preExistingRelationshipFailure(nino: String)(failure: Throwable): Unit =
      when(mockAgentServicesConnector.isPreExistingRelationship(nino)).thenReturn(Future.failed(failure))

    def createClientRelationship(arn: String, mtdid: String): Unit =
      when(mockAgentServicesConnector.createClientRelationship(arn, mtdid)).thenReturn(Future.successful(()))

    def createClientRelationshipFailure(arn: String, mtdid: String)(failure: Throwable): Unit =
      when(mockAgentServicesConnector.createClientRelationship(arn, mtdid)).thenReturn(Future.failed(failure))
  }
}
