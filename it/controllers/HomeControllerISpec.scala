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
import play.api.http.Status._
import play.api.i18n.Messages
import helpers.IntegrationTestConstants._
import helpers.servicemocks.{AuthStub, EnrolmentsStub}

class HomeControllerISpec extends ComponentSpecBase {
  "GET /report-quarterly/income-and-expenses/sign-up" when {
    "feature-switch.show-guidance is true" should {
      "return the guidance page" in {
        When("I call GET /")
        val res = IncomeTaxSubscriptionFrontend.startPage()

        Then("the result should have a status of OK and the front page title")
        res should have(
          httpStatus(OK),
          pageTitle(Messages("frontpage.title"))
        )
      }
    }
  }

  "GET /index" when {
    "auth is successful" should {
      "redirect to client details" in {
        Given("I setup the wiremock stubs")
        AuthStub.stubAuthSuccess()
        EnrolmentsStub.stubAgentEnrolment()

        When("I call GET /index")
        val res = IncomeTaxSubscriptionFrontend.index()

        Then("the result should have a status of SEE_OTHER and a redirect location of /client-details")
        res should have(
          httpStatus(SEE_OTHER),
          redirectURI(clientDetailsURI)
        )
      }
    }
  }
}
