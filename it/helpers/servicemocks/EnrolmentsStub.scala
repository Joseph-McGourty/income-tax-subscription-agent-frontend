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

package helpers.servicemocks

import common.Constants
import connectors.models.{Enrolment, Identifier}
import play.api.http.Status
import helpers.IntegrationTestConstants._


object EnrolmentsStub extends WireMockMethods {

  val agentEnrolment = Enrolment(Constants.agentServiceName, Seq(Identifier(Constants.agentIdentifierKey, testARN)), Enrolment.Activated)

  def stubAgentEnrolment(): Unit = {
    val enrolments: Seq[Enrolment] = Seq(agentEnrolment)
    val uri = AuthStub.successfulAuthResponse.uri

    when(method = GET, uri = s"$uri/enrolments")
        .thenReturn(Status.OK, body = enrolments)
  }
}
