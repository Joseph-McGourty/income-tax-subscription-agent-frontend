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
import forms.ClientDetailsForm
import models.{ClientDetailsModel, DateModel}
import play.api.http.Status
import play.api.mvc.{Action, AnyContent}
import play.api.test.Helpers.{await, _}
import services.mocks.{MockClientMatchingService, MockKeystoreService}

class ClientDetailsControllerSpec extends ControllerBaseSpec
  with MockKeystoreService
  with MockClientMatchingService {

  override val controllerName: String = "ClientDetailsController"
  override val authorisedRoutes: Map[String, Action[AnyContent]] = Map(
    "showClientDetails" -> TestClientDetailsController.showClientDetails(isEditMode = false),
    "submitClientDetails" -> TestClientDetailsController.submitClientDetails(isEditMode = false)
  )

  object TestClientDetailsController extends ClientDetailsController(
    MockBaseControllerConfig,
    messagesApi,
    MockKeystoreService,
    MockClientMatchingService
  )

  "Calling the showClientDetails action of the ClientDetailsController with an authorised user" should {

    lazy val result = TestClientDetailsController.showClientDetails(isEditMode = false)(authenticatedFakeRequest())

    "return ok (200)" in {
      setupMockKeystore(fetchClientDetails = None)

      status(result) must be(Status.OK)

      await(result)
      verifyKeystore(fetchClientDetails = 1, saveClientDetails = 0)

    }
  }

  "Calling the submitClientDetails action of the ClientDetailsController with an authorised user and valid submission" should {

    def callSubmit(isEditMode: Boolean) =
      TestClientDetailsController.submitClientDetails(isEditMode = isEditMode)(
        authenticatedFakeRequest()
          .post(ClientDetailsForm.clientDetailsForm.form, ClientDetailsModel(
            firstName = "Abc",
            lastName = "Abc",
            nino = "AB123456C",
            dateOfBirth = DateModel("01", "01", "1980")))
      )

    "When a match has been found and it is not in edit mode" should {
      "return a redirect status (SEE_OTHER)" in {
        setupMatchClient(matchClientMatched)
        setupMockKeystoreSaveFunctions()

        val goodRequest = callSubmit(isEditMode = false)

        status(goodRequest) must be(Status.SEE_OTHER)

        await(goodRequest)
        verifyKeystore(fetchClientDetails = 0, saveClientDetails = 1)
      }

      s"redirect to '${controllers.routes.IncomeSourceController.showIncomeSource().url}" in {
        setupMatchClient(matchClientMatched)
        setupMockKeystoreSaveFunctions()

        val goodRequest = callSubmit(isEditMode = false)

        redirectLocation(goodRequest) mustBe Some(controllers.routes.IncomeSourceController.showIncomeSource().url)

        await(goodRequest)
        verifyKeystore(fetchClientDetails = 0, saveClientDetails = 1)
      }
    }

    "When no match was been found and it is not in edit mode" should {
      "return a redirect status (SEE_OTHER)" in {
        setupMatchClient(matchClientNoMatch)
        setupMockKeystoreSaveFunctions()

        val goodRequest = callSubmit(isEditMode = false)

        status(goodRequest) must be(Status.SEE_OTHER)

        await(goodRequest)
        verifyKeystore(fetchClientDetails = 0, saveClientDetails = 1)
      }

      s"redirect to '${controllers.matching.routes.ClientDetailsErrorController.show().url}" in {
        setupMatchClient(matchClientNoMatch)
        setupMockKeystoreSaveFunctions()

        val goodRequest = callSubmit(isEditMode = false)

        redirectLocation(goodRequest) mustBe Some(controllers.matching.routes.ClientDetailsErrorController.show().url)

        await(goodRequest)
        verifyKeystore(fetchClientDetails = 0, saveClientDetails = 1)
      }
    }

    "When a match has been found and it is in edit mode" should {
      "return a redirect status (SEE_OTHER)" in {
        setupMatchClient(matchClientMatched)
        setupMockKeystoreSaveFunctions()

        val goodRequest = callSubmit(isEditMode = true)

        status(goodRequest) must be(Status.SEE_OTHER)

        await(goodRequest)
        verifyKeystore(fetchClientDetails = 0, saveClientDetails = 1)
      }

      s"redirect to '${controllers.routes.IncomeSourceController.showIncomeSource().url}" in {
        setupMatchClient(matchClientMatched)
        setupMockKeystoreSaveFunctions()

        val goodRequest = callSubmit(isEditMode = true)

        redirectLocation(goodRequest) mustBe Some(controllers.routes.IncomeSourceController.showIncomeSource().url)

        await(goodRequest)
        verifyKeystore(fetchClientDetails = 0, saveClientDetails = 1)
      }
    }

    "When no match was been found and it is in edit mode" should {
      "return a redirect status (SEE_OTHER)" in {
        setupMatchClient(matchClientNoMatch)
        setupMockKeystoreSaveFunctions()

        val goodRequest = callSubmit(isEditMode = true)

        status(goodRequest) must be(Status.SEE_OTHER)

        await(goodRequest)
        verifyKeystore(fetchClientDetails = 0, saveClientDetails = 1)
      }

      s"redirect to '${controllers.matching.routes.ClientDetailsErrorController.show().url}" in {
        setupMatchClient(matchClientNoMatch)
        setupMockKeystoreSaveFunctions()

        val goodRequest = callSubmit(isEditMode = true)

        redirectLocation(goodRequest) mustBe Some(controllers.matching.routes.ClientDetailsErrorController.show().url)

        await(goodRequest)
        verifyKeystore(fetchClientDetails = 0, saveClientDetails = 1)
      }
    }



  }

  "The back url" should {
    s"point to ${controllers.routes.HomeController.index().url}" in {
      TestClientDetailsController.backUrl mustBe controllers.routes.HomeController.index().url
    }
  }

  authorisationTests()
}
