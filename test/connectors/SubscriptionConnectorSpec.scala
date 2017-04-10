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

package connectors

import connectors.mocks.MockSubscriptionConnector
import connectors.models.subscription.{FEResponse, FESuccessResponse}
import org.scalatest.Matchers._
import play.api.test.Helpers._
import utils.TestConstants

class SubscriptionConnectorSpec extends MockSubscriptionConnector {

  val nino: String = TestConstants.testNino
  val id: String = TestConstants.testMTDID

  "SubscriptionConnector.subscribe" should {

    "Post to the correct url" in {
      TestSubscriptionConnector.subscriptionUrl(TestConstants.testNino) should endWith(s"/income-tax-subscription/subscription/${TestConstants.testNino}")
    }

    def call = await(TestSubscriptionConnector.subscribe(request = testRequest))

    "return the succcess response as an object" in {
      setupSubscribe(subScribeSuccess)
      val expected = FESuccessResponse(id)
      val actual = call
      actual shouldBe Some(expected)
    }

    "return None if the middle service indicated a bad request" in {
      val reason = "Your submission contains one or more errors. Failed Parameter(s) - [idType, idNumber, payload]"
      val code = "INVALID_NINO"
      setupSubscribe(subScribeBadRequest)
      val actual = call
      actual shouldBe None
    }

    "return None if the middle service indicated internal server error" in {
      setupSubscribe(subScribeInternalServerError)
      val actual = call
      actual shouldBe None
    }
  }


  "SubscriptionConnector.getSubscription" should {

    "GET to the correct url" in {
      TestSubscriptionConnector.subscriptionUrl(TestConstants.testNino) should endWith(s"/income-tax-subscription/subscription/${TestConstants.testNino}")
    }

    def result: Option[FEResponse] = await(TestSubscriptionConnector.getSubscription(TestConstants.testNino))

    "return the succcess response as an object" in {
      setupGetSubscription(subScribeSuccess)
      result shouldBe Some(FESuccessResponse(id))
    }

    "return None if the middle service indicated a bad request" in {
      setupGetSubscription(subScribeBadRequest)
      result shouldBe None
    }

    "return None if the middle service indicated internal server error" in {
      setupGetSubscription(subScribeInternalServerError)
      result shouldBe None
    }
  }

}
