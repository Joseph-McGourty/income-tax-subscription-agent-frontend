package connectors.models.matching

import utils.{TestModels, UnitTestTrait}


class ClientMatchRequestModelSpec extends UnitTestTrait {

  "ClientMatchRequestModel" should {

    "implicitly convert from ClientDetailsModel" in {
      // nino is updated to add spaces, this is to test this conversion also removes all the spaces
      val input = TestModels.testClientDetails.copy(nino = " " + TestModels.testClientDetails.nino.toCharArray.mkString(" ") + " ")
      val converted: ClientMatchRequestModel = input
      input.firstName mustBe converted.firstName
      input.lastName mustBe converted.lastName
      input.nino must not be converted.nino.toString
      input.nino.replace(" ", "") mustBe converted.nino.toString
      input.dateOfBirth.toLocalDate mustBe converted.dateOfBirth
    }

  }

}
