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

import assets.MessageLookup.{ClientDetails => messages}
import auth._
import controllers.ControllerBaseSpec
import forms.ClientDetailsForm
import models.DateModel
import models.agent.ClientDetailsModel
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.{Action, AnyContent}
import play.api.test.Helpers.{await, contentAsString, contentType, _}
import services.mocks.MockKeystoreService
import utils.TestConstants


class ClientDetailsControllerSpec extends ControllerBaseSpec
  with MockKeystoreService {

  override val controllerName: String = "ClientDetailsController"
  override val authorisedRoutes: Map[String, Action[AnyContent]] = Map(
    "show" -> TestClientDetailsController.show(isEditMode = false),
    "submit" -> TestClientDetailsController.submit(isEditMode = false)
  )

  object TestClientDetailsController extends ClientDetailsController(
    MockBaseControllerConfig,
    messagesApi,
    MockKeystoreService
  )

  "Calling the show action of the ClientDetailsController with an authorised user" should {

    lazy val result = TestClientDetailsController.show(isEditMode = false)(authenticatedFakeRequest())

    "return ok (200)" in {
      setupMockKeystore(fetchClientDetails = None)

      status(result) must be(Status.OK)

      await(result)
      verifyKeystore(fetchClientDetails = 1, saveClientDetails = 0)
    }

    "return HTML" in {
      contentType(result) must be(Some("text/html"))
      charset(result) must be(Some("utf-8"))
    }

    "render the 'Not subscribed to Agent Services page'" in {
      val document = Jsoup.parse(contentAsString(result))
      document.title mustBe messages.title
    }
  }

  val testNino = TestConstants.testNino

  for (editMode <- Seq(true, false)) {

    s"editMode=$editMode" when {

      "Calling the submit action of the ClientDetailsController with an authorised user and valid submission" should {

        def callSubmit(isEditMode: Boolean) =
          TestClientDetailsController.submit(isEditMode = isEditMode)(
            authenticatedFakeRequest()
              .post(ClientDetailsForm.clientDetailsForm.form, ClientDetailsModel(
                firstName = "Abc",
                lastName = "Abc",
                nino = testNino,
                dateOfBirth = DateModel("01", "01", "1980")))
          )

        "return a redirect status (SEE_OTHER - 303)" in {
          setupMockKeystoreSaveFunctions()

          val goodResult = callSubmit(isEditMode = editMode)

          status(goodResult) must be(Status.SEE_OTHER)

          await(goodResult)
          verifyKeystore(fetchClientDetails = 0, saveClientDetails = 1)
        }

        s"redirect to '${controllers.matching.routes.ConfirmClientController.show().url}'" in {
          setupMockKeystoreSaveFunctions()

          val goodResult = callSubmit(isEditMode = editMode)

          redirectLocation(goodResult) mustBe Some(controllers.matching.routes.ConfirmClientController.show().url)

          await(goodResult)
          verifyKeystore(fetchClientDetails = 0, saveClientDetails = 1)
        }
      }

      "Calling the submit action of the ClientDetailsController with an authorised user and invalid submission" should {

        def callSubmit(isEditMode: Boolean) =
          TestClientDetailsController.submit(isEditMode = isEditMode)(
            authenticatedFakeRequest()
              .post(ClientDetailsForm.clientDetailsForm.form, ClientDetailsModel(
                firstName = "Abc",
                lastName = "Abc",
                nino = testNino,
                dateOfBirth = DateModel("00", "01", "1980")))
          )

        "return a redirect status (BAD_REQUEST - 400)" in {
          setupMockKeystoreSaveFunctions()

          val badResult = callSubmit(isEditMode = editMode)

          status(badResult) must be(Status.BAD_REQUEST)

          await(badResult)
          verifyKeystore(fetchClientDetails = 0, saveClientDetails = 0)
        }

        "return HTML" in {
          val badResult = callSubmit(isEditMode = editMode)

          contentType(badResult) must be(Some("text/html"))
          charset(badResult) must be(Some("utf-8"))
        }

        "render the 'Not subscribed to Agent Services page'" in {
          val badResult = callSubmit(isEditMode = editMode)
          val document = Jsoup.parse(contentAsString(badResult))
          document.title mustBe messages.title
        }

      }
    }

  }

  authorisationTests()
}
