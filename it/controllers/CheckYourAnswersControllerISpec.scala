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

package controllers

import helpers.ComponentSpecBase
import helpers.servicemocks.{AuthStub, EnrolmentsStub, KeystoreStub, SubscriptionStub}
import play.api.http.Status._
import helpers.IntegrationTestConstants._

class CheckYourAnswersControllerISpec extends ComponentSpecBase {
  "POST /check-your-answers" should {
    "call subscription on the back end service" in {
      Given("I setup the wiremock stubs")
      AuthStub.stubAuthSuccess()
      EnrolmentsStub.stubAgentEnrolment()
      KeystoreStub.stubFullKeystore()
      SubscriptionStub.stubSuccessfulSubscription()
      KeystoreStub.stubPutMtditId()

      When("I call POST /check-your-answers")
      val res = IncomeTaxSubscriptionFrontend.submitCheckYourAnswers(Map.empty)

      Then("The result should have a status of SEE_OTHER and redirect to the confirmation page")
      res should have(
        httpStatus(SEE_OTHER),
        redirectURI(confirmationURI)
      )
    }
  }
}
