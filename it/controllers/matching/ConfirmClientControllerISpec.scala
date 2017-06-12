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

package controllers.matching

import helpers.ComponentSpecBase
import helpers.IntegrationTestConstants._
import helpers.servicemocks._
import play.api.http.Status._


class ConfirmClientControllerISpec extends ComponentSpecBase {
  import IncomeTaxSubscriptionFrontend._

  "POST /confirm-client" should {
    "check the client details from keystore and audit" in {
      Given("I setup the wiremock stubs")
      AuthStub.stubAuthSuccess()
      EnrolmentsStub.stubAgentEnrolment()
      KeystoreStub.stubFullKeystore()
      AuditStub.stubAuditing()
      AuthenticatorStub.stubMatchFound()
      SubscriptionStub.stubGetNoSubscription()

      When("I call POST /confirm-client")
      val res = IncomeTaxSubscriptionFrontend.submitConfirmClient()

      Then("The result should have a status of SEE_OTHER and redirect to check client relationship")
      res should have(
        httpStatus(SEE_OTHER),
        redirectURI(checkClientRelationshipURI)
      )

      Then("The client matching request should have been audited")
        AuditStub.verifyAudit()
    }
  }
}
