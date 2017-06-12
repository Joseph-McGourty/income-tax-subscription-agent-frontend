package controllers.matching

import helpers.ImplicitConversions._
import helpers.servicemocks.{AuthStub, EnrolmentsStub, KeystoreStub}
import helpers.{ComponentSpecBase, IntegrationTestModels}
import models.agent.ClientDetailsModel
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.i18n.Messages
import play.api.libs.json.JsValue
import services.CacheConstants.ClientDetails

class ClientDetailsControllerISpec extends ComponentSpecBase {

  "GET /client-details" should {
    "show the client details page" in {
      Given("I setup the wiremock stubs")
      AuthStub.stubAuthSuccess()
      EnrolmentsStub.stubAgentEnrolment()
      KeystoreStub.stubFullKeystore()

      When("I call GET /client-details")
      val res = IncomeTaxSubscriptionFrontend.showClientDetails()

      Then("The result should have a status of OK")
      res should have(
        httpStatus(OK),
        pageTitle(Messages("client-details.title"))
      )
    }
  }

  "POST /client-details" when {
    "An invalid form is submitted" should {
      "show the client details page with validation errors" in {
        Given("I setup the wiremock stubs")
        AuthStub.stubAuthSuccess()
        EnrolmentsStub.stubAgentEnrolment()
        KeystoreStub.stubEmptyKeystore()

        When("I call POST /client-details")
        val res = IncomeTaxSubscriptionFrontend.submitClientDetails(None)

        Then("The result should have a status of BadRequest")
        res should have(
          httpStatus(BAD_REQUEST),
          pageTitle(Messages("client-details.title"))
        )
        KeystoreStub.verifyKeyStoreSave(ClientDetails, None, Some(0))
        KeystoreStub.verifyKeyStoreDelete(Some(0))
      }
    }

    "A valid form is submitted and there are no previous user details in keystore" should {
      "redirects to confirm client and saves the client details to keystore" in {
        Given("I setup the wiremock stubs")
        AuthStub.stubAuthSuccess()
        EnrolmentsStub.stubAgentEnrolment()
        val clientDetails: ClientDetailsModel = IntegrationTestModels.testClientDetails
        KeystoreStub.stubKeystoreSave(ClientDetails, clientDetails)
        KeystoreStub.stubEmptyKeystore()

        When("I call POST /client-details")
        val res = IncomeTaxSubscriptionFrontend.submitClientDetails(Some(clientDetails))

        Then("The result should have a status of SEE_OTHER")
        res should have(
          httpStatus(SEE_OTHER),
          redirectURI(routes.ConfirmClientController.show().url)
        )

        KeystoreStub.verifyKeyStoreSave(ClientDetails, clientDetails, Some(1))
        KeystoreStub.verifyKeyStoreDelete(Some(0))
      }
    }

    "A valid form is submitted and there there is already a user details in keystore which matches the new submission" should {
      "redirects to confirm client but do not modify the keystore" in {
        Given("I setup the wiremock stubs")
        AuthStub.stubAuthSuccess()
        EnrolmentsStub.stubAgentEnrolment()
        val clientDetails: ClientDetailsModel = IntegrationTestModels.testClientDetails
        val clientDetailsJs: JsValue = clientDetails
        KeystoreStub.stubKeystoreData(Map(ClientDetails -> clientDetailsJs))

        When("I call POST /client-details")
        val res = IncomeTaxSubscriptionFrontend.submitClientDetails(Some(clientDetails))

        Then("The result should have a status of SEE_OTHER")
        res should have(
          httpStatus(SEE_OTHER),
          redirectURI(routes.ConfirmClientController.show().url)
        )

        KeystoreStub.verifyKeyStoreSave(ClientDetails, clientDetails, Some(0))
        KeystoreStub.verifyKeyStoreDelete(Some(0))
      }
    }

    "A valid form is submitted and there there is already a user details in keystore which does not match the new submission" should {
      "redirects to confirm client and wipe all previous keystore entries before saving the new client details" in {
        Given("I setup the wiremock stubs")
        AuthStub.stubAuthSuccess()
        EnrolmentsStub.stubAgentEnrolment()
        val clientDetails = IntegrationTestModels.testClientDetails
        val clientDetailsJs: JsValue = clientDetails
        KeystoreStub.stubKeystoreData(Map(ClientDetails -> clientDetailsJs))
        KeystoreStub.stubKeystoreSave(ClientDetails, clientDetailsJs)
        KeystoreStub.stubKeystoreDelete()

        When("I call POST /client-details")
        val submittedUserDetails = clientDetails.copy(firstName = "NotMatching")
        val res = IncomeTaxSubscriptionFrontend.submitClientDetails(Some(submittedUserDetails))

        Then("The result should have a status of SEE_OTHER")
        res should have(
          httpStatus(SEE_OTHER),
          redirectURI(routes.ConfirmClientController.show().url)
        )

        KeystoreStub.verifyKeyStoreDelete(Some(1))
        KeystoreStub.verifyKeyStoreSave(ClientDetails, submittedUserDetails, Some(1))
      }
    }
  }

}
