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

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationTestConstants
import org.joda.time.{DateTime, DateTimeZone}
import uk.gov.hmrc.play.frontend.auth.connectors.domain
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{Authority, ConfidenceLevel, CredentialStrength}
//import models.auth.UserIds
import helpers.IntegrationTestConstants._
import play.api.http.Status._

object AuthStub extends WireMockMethods {
  val authIDs = "/uri/to/ids"
  val authority = "/auth/authority"

  val gatewayID = "12345"
  val internalID = "internal"
  val externalID = "external"

  def stubAuthSuccess(): StubMapping = {
    stubAuthoritySuccess()
  }

  def stubAuthoritySuccess(): StubMapping =
    when(method = GET, uri = authority)
      .thenReturn(status = OK, body = successfulAuthResponse)

  val loggedInAt: Some[DateTime] = Some(new DateTime(2015, 11, 22, 11, 33, 15, 234, DateTimeZone.UTC))
  val previouslyLoggedInAt: Some[DateTime] = Some(new DateTime(2014, 8, 3, 9, 25, 44, 342, DateTimeZone.UTC))

  def successfulAuthResponse: Authority = Authority(
    uri = userId,
    accounts = domain.Accounts(),
    loggedInAt = loggedInAt,
    previouslyLoggedInAt = previouslyLoggedInAt,
    credentialStrength = CredentialStrength.Strong,
    confidenceLevel = ConfidenceLevel.L500,
    userDetailsLink = None,
    enrolments = None,
    ids = None,
    legacyOid = ""
  )
}
