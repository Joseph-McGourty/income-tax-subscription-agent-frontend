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

import services.mocks.MockClientMatchingService
import uk.gov.hmrc.play.http.InternalServerException
import utils.TestModels.testClientDetails
import play.api.test.Helpers._

class ClientMatchingServiceSpec extends MockClientMatchingService {

  "ClientMatchingService" should {

    "return true if authenticator response with ok" in {
      setupMatchClient(matchClientMatched)
      val result = TestClientMatchingService.matchClient(testClientDetails)
      await(result) mustBe true
    }

    "return false if authenticator response with Unauthorized but with a matching error message" in {
      setupMatchClient(matchClientNoMatch)
      val result = TestClientMatchingService.matchClient(testClientDetails)
      await(result) mustBe false
    }

    "throw InternalServerException if authenticator response with Unauthorized but with a server error message" in {
      setupMatchClient(matchClientUnexpectedFailure)
      val result = TestClientMatchingService.matchClient(testClientDetails)

      val e = intercept[InternalServerException] {
        await(result)
      }
      e.message must include (s"AuthenticatorConnector.matchClient unexpected response from authenticator: status=$UNAUTHORIZED")
    }

    "throw InternalServerException if authenticator response with an unexpected status" in {
      setupMatchClient(matchClientUnexpectedFailure)
      val result = TestClientMatchingService.matchClient(testClientDetails)

      val e = intercept[InternalServerException] {
        await(result)
      }
      e.message must include (s"AuthenticatorConnector.matchClient unexpected response from authenticator: status=")
      e.message must not include s"UNAUTHORIZED"
    }
  }

}
