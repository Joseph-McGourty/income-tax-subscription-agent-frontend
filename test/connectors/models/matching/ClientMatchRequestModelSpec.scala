package connectors.models.matching

import utils.{TestModels, UnitTestTrait}


class ClientMatchRequestModelSpec extends UnitTestTrait {

  "ClientMatchRequestModel" should {

    "implicitly convert from ClientDetailsModel" in {
      val input = TestModels.testClientDetails
      val converted: ClientMatchRequestModel = input
      input.firstName mustBe converted.firstName
      input.lastName mustBe converted.lastName
      input.nino mustBe converted.nino.toString
      input.dateOfBirth.toLocalDate mustBe converted.dateOfBirth
    }

  }

}
