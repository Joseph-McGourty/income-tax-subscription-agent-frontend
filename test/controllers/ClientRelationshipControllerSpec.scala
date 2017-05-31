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

import assets.MessageLookup.{NoClientRelationship => messages}
import auth.authenticatedFakeRequest
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import play.api.mvc.{Action, AnyContent}
import play.api.test.Helpers._
import services.ClientRelationshipService
import services.mocks.{MockEnrolmentService, MockKeystoreService}
import uk.gov.hmrc.play.http.HeaderCarrier
import utils.TestConstants._
import utils.TestModels

import scala.concurrent.Future

class ClientRelationshipControllerSpec
  extends ControllerBaseSpec
    with MockKeystoreService
    with MockEnrolmentService {
  override def beforeEach(): Unit = {
    super.beforeEach()
    setupMockEnrolmentCheckAgentService()
  }

  override lazy val authorisedRoutes: Map[String, Action[AnyContent]] = Map(
    "checkClientRelationship" -> TestClientRelationshipController.checkClientRelationship
  )
  override val controllerName: String = "ClientRelationshipController"
  val mockClientRelationshipService = mock[ClientRelationshipService]

  object TestClientRelationshipController extends ClientRelationshipController(
    MockBaseControllerConfig,
    messagesApi,
    mockClientRelationshipService,
    MockKeystoreService
  ) {
    override lazy val enrolmentService = mockEnrolmentService
  }

  "checkClientRelationship" should {
    "redirect to 'Capture Client's Subscription Details' if there is a pre-existing relationship" in {
      setupMockKeystore(fetchClientDetails = TestModels.testClientDetails)

      setupMockEnrolmentGetARN(testARN)

      when(mockClientRelationshipService.isPreExistingRelationship(ArgumentMatchers.eq(testARN), ArgumentMatchers.eq(testNino))(ArgumentMatchers.any()))
        .thenReturn(Future.successful(true))


      val res = TestClientRelationshipController.checkClientRelationship(authenticatedFakeRequest())

      status(res) mustBe SEE_OTHER
      redirectLocation(res) must contain(controllers.routes.IncomeSourceController.showIncomeSource().url)
    }

    "show the 'Unable to subscribe' page if there is no pre-existing relationship" in {
      setupMockKeystore(fetchClientDetails = TestModels.testClientDetails)

      setupMockEnrolmentGetARN(testARN)

      when(mockClientRelationshipService.isPreExistingRelationship(ArgumentMatchers.eq(testARN), ArgumentMatchers.eq(testNino))(ArgumentMatchers.any()))
        .thenReturn(Future.successful(false))

      val res = TestClientRelationshipController.checkClientRelationship(authenticatedFakeRequest())

      status(res) mustBe SEE_OTHER
      redirectLocation(res) must contain(controllers.routes.ClientRelationshipController.noClientErrorRedirect().url)
    }

    "return an INTERNAL_SERVER_ERROR if there are no client details in keystore" in {
      setupMockKeystore(fetchClientDetails = None)

      val res = TestClientRelationshipController.checkClientRelationship(authenticatedFakeRequest())

      intercept[Exception](await(res)) mustBe a[NoSuchElementException]
    }

    "return an INTERNAL_SERVER_ERROR if the ARN cannot be collected" in {
      setupMockKeystore(fetchClientDetails = TestModels.testClientDetails)

      val exception = new Exception()

      setupMockEnrolmentGetARNFailure(exception)

      val res = TestClientRelationshipController.checkClientRelationship(authenticatedFakeRequest())

      intercept[Exception](await(res)) mustBe exception
    }

    "return an INTERNAL_SERVER_ERROR if the call to agent services fails" in {
      setupMockKeystore(fetchClientDetails = TestModels.testClientDetails)

      setupMockEnrolmentGetARN(testARN)

      val exception = new Exception()

      when(mockClientRelationshipService.isPreExistingRelationship(ArgumentMatchers.eq(testARN), ArgumentMatchers.eq(testNino))(ArgumentMatchers.any()))
        .thenReturn(Future.failed(exception))

      val res = TestClientRelationshipController.checkClientRelationship(authenticatedFakeRequest())

      intercept[Exception](await(res)) mustBe exception
    }
  }

  "noClientError" should {
    "return an OK with the client error page" in {
      val res = TestClientRelationshipController.noClientError(authenticatedFakeRequest())

      status(res) mustBe OK

      lazy val document = Jsoup.parse(contentAsString(res))

      document.title mustBe messages.heading

      document.select("form").attr("action") mustBe controllers.routes.ClientRelationshipController.noClientErrorRedirect().url
    }
  }

  "noClientErrorRedirect" should {
    s"redirect to '${controllers.matching.routes.ClientDetailsController.show().url}'" in {
      val res = TestClientRelationshipController.noClientErrorRedirect(authenticatedFakeRequest())

      status(res) mustBe SEE_OTHER
      redirectLocation(res) must contain(controllers.matching.routes.ClientDetailsController.show().url)
    }
  }

  authorisationTests()
}
