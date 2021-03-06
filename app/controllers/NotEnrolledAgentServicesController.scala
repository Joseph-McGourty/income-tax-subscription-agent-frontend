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

import javax.inject.{Inject, Singleton}

import config.BaseControllerConfig
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Call}

import scala.concurrent.Future

@Singleton
class NotEnrolledAgentServicesController @Inject()(val baseConfig: BaseControllerConfig,
                                                   val messagesApi: MessagesApi
                                        ) extends BaseController {

  val show: Action[AnyContent] = Action.async {
    implicit request =>
      Future.successful(Ok(views.html.not_enrolled_agent_services(getAction = controllers.routes.SignOutController.signOut())))
  }

}
