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

import auth._
import controllers.ControllerBaseSpec
import play.api.http.Status
import play.api.mvc.{Action, AnyContent}
import play.api.test.Helpers.{await, _}
import services.mocks.{MockClientMatchingService, MockKeystoreService}
import utils.TestModels

class ConfirmClientControllerSpec extends ControllerBaseSpec
  with MockKeystoreService
  with MockClientMatchingService {

  override val controllerName: String = "ConfirmClientController"
  override val authorisedRoutes: Map[String, Action[AnyContent]] = Map(
    "show" -> TestConfirmClientController.show(),
    "submit" -> TestConfirmClientController.submit()
  )

  object TestConfirmClientController extends ConfirmClientController(
    MockBaseControllerConfig,
    messagesApi,
    MockKeystoreService,
    TestClientMatchingService
  )

  "Calling the show action of the ConfirmClientController with an authorised user" should {

    def call = TestConfirmClientController.show()(authenticatedFakeRequest())

    "when there are no client details store redirect them to client details" in {
      setupMockKeystore(fetchClientDetails = None)

      val result = call

      status(result) must be(Status.SEE_OTHER)

      await(result)
      verifyKeystore(fetchClientDetails = 1, saveClientDetails = 0)

    }

    "if there is are client details return ok (200)" in {
      setupMockKeystore(fetchClientDetails = TestModels.testClientDetails)
      val result = call

      status(result) must be(Status.OK)

      await(result)
      verifyKeystore(fetchClientDetails = 1, saveClientDetails = 0)
    }
  }

  "Calling the submit action of the ConfirmClientController with an authorised user and valid submission" should {

    def callSubmit() = TestConfirmClientController.submit()(authenticatedFakeRequest())

    "When a match has been found" should {
      "return a redirect status (SEE_OTHER)" in {
        setupMatchClient(matchClientMatched)
        setupMockKeystore(fetchClientDetails = TestModels.testClientDetails)

        val goodRequest = callSubmit()

        status(goodRequest) must be(Status.SEE_OTHER)

        await(goodRequest)
        verifyKeystore(fetchClientDetails = 1, saveClientDetails = 0)
      }

      s"redirect to '${controllers.routes.IncomeSourceController.showIncomeSource().url}" in {
        setupMatchClient(matchClientMatched)
        setupMockKeystore(fetchClientDetails = TestModels.testClientDetails)

        val goodRequest = callSubmit()

        redirectLocation(goodRequest) mustBe Some(controllers.routes.IncomeSourceController.showIncomeSource().url)

        await(goodRequest)
        verifyKeystore(fetchClientDetails = 1, saveClientDetails = 0)
      }
    }

    "When no match was been found" should {
      "return a redirect status (SEE_OTHER)" in {
        setupMatchClient(matchClientNoMatch)
        setupMockKeystore(fetchClientDetails = TestModels.testClientDetails)

        val goodRequest = callSubmit()

        status(goodRequest) must be(Status.SEE_OTHER)

        await(goodRequest)
        verifyKeystore(fetchClientDetails = 1, saveClientDetails = 0)
      }

      s"redirect to '${controllers.matching.routes.ClientDetailsErrorController.show().url}" in {
        setupMatchClient(matchClientNoMatch)
        setupMockKeystore(fetchClientDetails = TestModels.testClientDetails)

        val goodRequest = callSubmit()

        redirectLocation(goodRequest) mustBe Some(controllers.matching.routes.ClientDetailsErrorController.show().url)

        await(goodRequest)
        verifyKeystore(fetchClientDetails = 1, saveClientDetails = 0)
      }
    }

  }

  "The back url" should {
    s"point to ${controllers.matching.routes.ClientDetailsController.showClientDetails().url}" in {
      TestConfirmClientController.backUrl mustBe controllers.matching.routes.ClientDetailsController.showClientDetails().url
    }
  }

  authorisationTests()
}
