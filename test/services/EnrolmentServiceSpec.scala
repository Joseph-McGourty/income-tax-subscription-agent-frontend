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

package services

import audit.Logging
import auth.{authenticatedFakeRequest, ggUser, mockAuthorisedUserIdCL200, mockNotASEnrolled}
import common.Constants
import connectors.EnrolmentConnector
import connectors.models.Enrolment.{Enrolled, NotEnrolled}
import _root_.connectors.models.{Enrolment, Identifier}
import org.scalatest.Matchers._
import org.scalatest.mockito.MockitoSugar
import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.mocks.MockEnrolmentService
import uk.gov.hmrc.play.frontend.auth.AuthenticationProviderIds
import uk.gov.hmrc.play.frontend.auth.connectors.AuthConnector
import uk.gov.hmrc.play.http.HeaderCarrier
import utils.{TestConstants, UnitTestTrait}
import org.mockito.Mockito._

import scala.concurrent.Future


class EnrolmentServiceSpec extends UnitTestTrait
  with MockEnrolmentService
  with MockitoSugar {

  val isEnrolled = (e: Enrolled) => e match {
    case x =>
      x shouldBe Enrolled
      Future.successful(Results.Ok)
  }

  val isNotEnrolled = (e: Enrolled) => e match {
    case x =>
      x shouldBe NotEnrolled
      Future.successful(Results.Ok)
  }

  implicit def hcUtil(implicit request: FakeRequest[_]): HeaderCarrier = HeaderCarrier.fromHeadersAndSession(request.headers, Some(request.session))

  "checkAgentServiceEnrolment" should {
    "return is enrolled for an user with AS enrolment" in {
      implicit val request = authenticatedFakeRequest(AuthenticationProviderIds.GovernmentGatewayId, mockAuthorisedUserIdCL200)
      await(TestEnrolmentService.checkAgentServiceEnrolment(isEnrolled)(hcUtil(request)))
    }

    "return not enrolled for a user without AS enrolment" in {
      implicit val request = authenticatedFakeRequest(AuthenticationProviderIds.GovernmentGatewayId, mockNotASEnrolled)
      await(TestEnrolmentService.checkAgentServiceEnrolment(isNotEnrolled)(hcUtil(request)))
    }
  }

  val mockAuthConnector = mock[AuthConnector]
  val mockEnrolmentConnector = mock[EnrolmentConnector]
  val mockLogging = mock[Logging]

  val service = new EnrolmentService(mockAuthConnector, mockEnrolmentConnector, mockLogging)

  "getEnrolments" should {
    "return all enrolments for a user" in {
      val authority = ggUser.userCL50
      when(mockAuthConnector.currentAuthority).thenReturn(Future.successful(Some(authority)))

      val enrolment = Enrolment(Constants.agentServiceName, Seq(Identifier(Constants.agentIdentifierKey, TestConstants.testARN)), Enrolment.Activated)
      when(mockEnrolmentConnector.getEnrolments(authority.uri)).thenReturn(Future.successful(Some(Seq(enrolment))))

      val res = await(service.getEnrolments)

      res.get should contain(enrolment)
    }
  }

  "getARN" should {
    "return the ARN for a user" in {
      val authority = ggUser.userCL50
      when(mockAuthConnector.currentAuthority).thenReturn(Future.successful(Some(authority)))

      val enrolment = Enrolment(Constants.agentServiceName, Seq(Identifier(Constants.agentIdentifierKey, TestConstants.testARN)), Enrolment.Activated)
      when(mockEnrolmentConnector.getEnrolments(authority.uri)).thenReturn(Future.successful(Some(Seq(enrolment))))

      val res = await(service.getARN)

      res should contain(TestConstants.testARN)
    }
  }
}
