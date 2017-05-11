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

import services.mocks.MockClientRelationshipService
import play.api.test.Helpers._

class ClientRelationshipServiceSpec extends MockClientRelationshipService {
  "isPreExistingRelationship" should {
    "return true if the connector returns true" in {
      setupAgentServicesConnector(testNino)(isPreExistingRelationship = true)

      val res = TestClientRelationshipService.isPreExistingRelationship(testNino)

      await(res) mustBe true
    }

    "return false if the connector returns false" in {
      setupAgentServicesConnector(testNino)(isPreExistingRelationship = false)

      val res = TestClientRelationshipService.isPreExistingRelationship(testNino)

      await(res) mustBe false
    }
  }
}
