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

package controllers.business

import auth._
import controllers.ControllerBaseSpec
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.{Action, AnyContent, Result}
import play.api.test.Helpers._
import services.mocks.MockKeystoreService

import scala.concurrent.Future

class RegisterNextAccountingPeriodControllerSpec extends ControllerBaseSpec with MockKeystoreService {

  override val controllerName: String = "RegisterNextAccountingPeriodController"
  override val authorisedRoutes: Map[String, Action[AnyContent]] = Map(
    "show" -> TestRegisterNextAccountingPeriodController.show,
    "submit" -> TestRegisterNextAccountingPeriodController.submit
  )

  object TestRegisterNextAccountingPeriodController extends RegisterNextAccountingPeriodController(
    MockBaseControllerConfig,
    messagesApi,
    MockKeystoreService
  )

  "Calling the show action of the RegisterNextAccountingPeriod with an authorised user" should {

    def result: Future[Result] = {
      TestRegisterNextAccountingPeriodController.show(authenticatedFakeRequest())
    }

    "return ok (200)" in {
      status(result) must be(Status.OK)
    }

    s"The back url should point to '${controllers.business.routes.BusinessAccountingPeriodPriorController.show().url}'" in {
      val document = Jsoup.parse(contentAsString(result))
      document.select("#back").attr("href") mustBe controllers.business.routes.BusinessAccountingPeriodPriorController.show().url
    }

    s"the page must have a link to ${controllers.routes.SignOutController.signOut().url}" in {
      val document = Jsoup.parse(contentAsString(result))
      document.select("#sign-out").attr("href") mustBe controllers.routes.SignOutController.signOut().url
    }
  }

  "Calling the submit action of the RegisterNextAccountingPeriod with an authorised user and valid submission" when {

    def callShow(): Future[Result] = TestRegisterNextAccountingPeriodController.submit(authenticatedFakeRequest())

    "Continue to sign up is clicked" should {

      def goodRequest: Future[Result] = {
        setupMockKeystoreSaveFunctions()
        callShow()
      }

      "return status SEE_OTHER (303)" in {
        status(goodRequest) mustBe Status.SEE_OTHER
      }

      s"redirect to ${controllers.business.routes.BusinessAccountingPeriodDateController.showAccountingPeriod().url}" in {
        redirectLocation(goodRequest).get mustBe controllers.business.routes.BusinessAccountingPeriodDateController.showAccountingPeriod().url
      }

    }

    authorisationTests()

  }
}
