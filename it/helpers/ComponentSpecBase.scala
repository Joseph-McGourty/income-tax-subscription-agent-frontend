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

package helpers

import java.util.UUID

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import controllers.ITSASessionKeys
import controllers.ITSASessionKeys.GoHome
import forms.ClientDetailsForm
import helpers.IntegrationTestConstants.baseURI
import helpers.SessionCookieBaker._
import helpers.servicemocks.{AuditStub, WireMockMethods}
import models.agent.ClientDetailsModel
import org.scalatest._
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api._
import play.api.data.Form
import play.api.http.HeaderNames
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsArray, JsValue, Writes}
import play.api.libs.ws.{WSClient, WSResponse}
import uk.gov.hmrc.play.test.UnitSpec

trait ComponentSpecBase extends UnitSpec
  with GivenWhenThen with TestSuite
  with GuiceOneServerPerSuite with ScalaFutures with IntegrationPatience with Matchers
  with BeforeAndAfterEach with BeforeAndAfterAll with Eventually
  with I18nSupport with CustomMatchers with WireMockMethods {

  import WiremockHelper._

  lazy val ws = app.injector.instanceOf[WSClient]

  lazy val wmConfig = wireMockConfig().port(wiremockPort)
  lazy val wireMockServer = new WireMockServer(wmConfig)

  def startWiremock() = {
    wireMockServer.start()
    WireMock.configureFor(wiremockHost, wiremockPort)
  }

  def stopWiremock() = wireMockServer.stop()

  def resetWiremock() = WireMock.reset()

  def buildClient(path: String) = ws.url(s"http://localhost:$port$baseURI$path").withFollowRedirects(false)

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build
  override lazy val messagesApi = app.injector.instanceOf[MessagesApi]
  val mockHost = WiremockHelper.wiremockHost
  val mockPort = WiremockHelper.wiremockPort.toString
  val mockUrl = s"http://$mockHost:$mockPort"
  implicit val nilWrites: Writes[Nil.type] = new Writes[Nil.type] {
    override def writes(o: Nil.type): JsValue = JsArray()
  }

  def config: Map[String, String] = Map(
    "play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck",
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "microservice.services.session-cache.host" -> mockHost,
    "microservice.services.session-cache.port" -> mockPort,
    "microservice.services.subscription-service.host" -> mockHost,
    "microservice.services.subscription-service.port" -> mockPort,
    "microservice.services.feature-switch.show-guidance" -> "true",
    "auditing.consumer.baseUri.host" -> mockHost,
    "auditing.consumer.baseUri.port" -> mockPort
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    resetWiremock()
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
    AuditStub.stubAuditing()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }

  def toFormData[T](form: Form[T], data: T): Map[String, Seq[String]] =
    form.fill(data).data map { case (k, v) => k -> Seq(v) }

  object IncomeTaxSubscriptionFrontend {
    val csrfToken = UUID.randomUUID().toString

    def startPage(): WSResponse = get("/")

    def index(): WSResponse = get("/index")

    def get(uri: String): WSResponse = await(
      buildClient(uri)
        .withHeaders(HeaderNames.COOKIE -> getSessionCookie(Map(GoHome -> "et")))
        .get()
    )

    def showClientDetails(): WSResponse = get("/client-details")

    def submitClientDetails(clientDetails: Option[ClientDetailsModel]): WSResponse =
      post("/client-details")(
        clientDetails.fold(Map.empty: Map[String, Seq[String]])(
          cd => toFormData(ClientDetailsForm.clientDetailsValidationForm, cd)
        )
      )

    def submitCheckYourAnswers: (Map[String, Seq[String]]) => WSResponse = post("/check-your-answers")

    def post(uri: String)(body: Map[String, Seq[String]]): WSResponse = await(
      buildClient(uri)
        .withHeaders(
          HeaderNames.COOKIE -> getSessionCookie(
            Map(
              GoHome -> "et",
              ITSASessionKeys.ArnKey -> IntegrationTestConstants.testARN
            )
          ),
          "Csrf-Token" -> "nocheck")
        .post(body)
    )

  }

}
