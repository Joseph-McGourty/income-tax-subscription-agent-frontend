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

package views

import assets.MessageLookup.{NoClientRelationship => messages, Base => common}
import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest

class NoClientRelationshipViewSpec extends ViewSpecTrait {

  val action = ViewSpecTrait.testCall

  lazy val page = views.html.no_client_relationship(action)(FakeRequest(), applicationMessages, appConfig)

  "The No Client Relationship View" should {
    val testPage = TestView(
      name = "No Client Relationship View",
      title = messages.title,
      heading = messages.heading,
      page = page
    )

    testPage.mustHavePara(
      messages.para1
    )

    val form = testPage.getForm("No Client Relationship form")(actionCall = action)

    form.mustHaveSubmitButton(common.goBack)

  }
}
