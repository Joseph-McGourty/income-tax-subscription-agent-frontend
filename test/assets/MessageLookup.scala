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

package assets

import forms.validation.Constraints.{invalidFormat, maxLength, nonEmpty}
import play.api.data.validation.Constraint

object MessageLookup {

  object Base {
    val continue = "Continue"
    val submit = "Submit"
    val update = "Update"
    val signOut = "Sign out"
    val startDate = "Start date"
    val endDate = "End date"
    val day = "Day"
    val month = "Month"
    val year = "Year"
    val errorHeading = "There's a problem"
    val change = "Change"
    val where_can_i_get_this_information = "Where can I get this information"
    val signUp = "Sign up"
    val dateOfBirth = "Date of birth"
    val goBack = "Go back"
  }

  object FrontPage {
    val title = "Sign up to report your client's income and expenses quarterly"
    val heading = title
    val subHeading_1 = "Sign up using Government Gateway"
    val subHeading_2 = "Before you start"
    val bullet_1 = "using your accounting software to record your clients income and expenses"
    val bullet_2 = "sending details to us regularly from July 2017"
    val bullet_3 = "a sole trader with income from one business"
    val bullet_4 = "someone who rents out a UK property"
    val bullet_5 = "a sole trader with income from one business and they rent out a UK property"
    val bullet_6 = "full name"
    val bullet_7 = "date of birth"
    val bullet_8 = "National Insurance Number"
    val para_1 = "By signing up to HM Revenue and Customs secure service, you're helping to test a new way of working out your tax. You'll do this by:"
    val para_2 = "You can sign up your client if their current accounting period starts after 5 April 2017 and they're one of the following:"
    val para_3 = "If they have income other than those listed above, you won't be able to report it using this service yet."
    val para_4 = "You'll need to enter the user ID and password you got when you created your Agent Services account."
    val para_5 = "You'll need your client's:"
    val para_6 = "To see your client's tax details, they must authorise you as their agent. You can still sign up and send reports, but you won't be able to access their information until you've been authorised."
  }

  object PreferencesCallBack {
    val title = "Do you want to continue?"
    val heading: String = "To sign up, you must allow HMRC to send you electronic communications"
    val legend: String = "To sign up for quarterly reporting, you must allow HMRC to send you electronic communications"
    val yes: String = "Continue to sign up"
    val no: String = "Sign out"
  }

  object IncomeSource {
    val title = "Select your client's income type"
    val heading: String = title
    val business = "Sole trader business"
    val property = "Renting out a UK property"
    val both = "Their sole trader business and renting out a UK property"
    val other = "Other"
  }

  object Property {

    object Income {
      val title = "How much was your income from property this year?"
      val heading: String = title
      val lt10k = "Less than £10,000"
      val ge10k = "£10,000 or more"
    }

  }

  object Business {

    object SoleTrader {
      val title = "Are you a sole trader?"
      val heading: String = title
      val line_1 = "As a sole trader, you run your own business as an individual. You can keep all your business's profits after you've paid tax on them. 'Sole trader' means you're responsible for the business, not that you have to work alone."
      val yes = "Yes - I'm a sole trader"
      val no = "No - I am a different type of business"
    }

    object AccountingPeriodPrior {
      val title = "Business accounting period"
      val heading: String = "Did your client's current accounting period start before 6 April 2017?"
      val yes = "Yes"
      val no = "No"
    }

    object RegisterNextAccountingPeriod {
      val title = "You can't send quarterly reports yet"
      val heading = title
      val line_1 = "You can sign up now, but you won't be able to send any reports for your client until their next accounting period begins."
      val button = "Continue to sign up"
    }

  }

  object AccountingPeriod {
    val title = "Business accounting period"
    val heading_current = "Enter your client's current accounting period dates"
    val heading_next = "Enter your client's next accounting period dates"
    val heading_editMode = "What are the dates of your accounting period?"
    val exampleStartDate_current = "For example, 6 4 2017"
    val exampleEndDate_current = "For example, 5 4 2018"
    val exampleStartDate_next = "For example, 1 4 2018"
    val exampleEndDate_next = "For example, 31 3 2019"
  }

  object ContactEmail {
    val title = "Enter your email address"
    val heading: String = title
    val line_1 = "We'll use this to get in touch with updates about your estimated Income Tax calculations."
  }

  object BusinessName {
    val title = "Business name"
    val heading: String = "What's the name of your client's business?"
    val line_1 = "This is the business name they used to register for Self Assessment. If their business doesn't have a name, enter your client's name."
  }

  object AccountingMethod {
    val title = "Accounting method"
    val heading: String = "What accounting method does your client use?"
    val cash = "Cash basis"
    val accruals = "Traditional accounting"
  }

  object Terms {
    val title = "Terms of participation"
    val heading: String = title
    val line_1 = "By accepting the terms of participation, you confirm that you've read and understood the terms of the trial. Your accounting software provider will have sent you these terms separately."
    val checkbox = "I accept the terms of participation"
  }

  object Summary {
    val title = "Check your answers"
    val heading: String = title
    val heading_hidden = "before signing up"
    val h2 = "You've told us"
    val income_source = "Your client's income type"

    object IncomeSource {
      val business = "Sole trader business"
      val property = "Property"
      val both = "Sole trader business and property"
    }

    val accounting_period_prior = "Did your current accounting period start before 1 April 2017?"
    val accounting_period = s"Your client's accounting period dates"
    val accounting_period_month: Int => String = (month: Int) => s"$month month period"
    val business_name = "What's the name of your client's business?"
    val income_type = "What accounting method does your client use?"
    val other_income = "Does your client have any other sources of income?"

    object AccountingMethod {
      val cash = "Cash basis"
      val accruals = "Accruals basis"
    }

    val contact_email = "Do you want to receive electronic communications from HMRC?"
    val terms = "Terms of participation"
    val terms_agreed = "I agree"
    val confirm_and_sign_up = "Confirm and sign up"
  }

  object Timeout {
    val title = "Your session has timed out"
    val heading = "Your session has timed out"
    val returnToHome = """To sign up for quarterly reporting, you'll have to sign in using your Government Gateway ID."""
  }

  object ClientAlreadySubscribed {
    val title = "Your client has already signed up"
    val heading = "Your client has already signed up"
    val para1 = "The details you've entered are already in use."
  }

  object Confirmation {
    val title = "Confirmation page"
    val heading = "Success"
    val banner_line1_1 = "Sign up complete."
    val banner_line1_2 = "Your reference number is:"
    val signOut = "Finish"

    object whatHappensNext {
      val para1 = "Make a note of your reference number. If you forget your Government Gateway login details, we can use it to help you retrieve them."
      val heading = "What happens next"
      val para2 = "You must report to HMRC once every quarter from the start of your accounting period."
      val para3 = "HMRC will calculate an estimate of your Income Tax after you send a report. You can see these estimates from your accounting software after your first report, or through your business tax account from August 2017."
      val linkText = "business tax account"
      val para4 = s"Activate your $linkText, you can use this to see a summary of your business taxes."
    }

  }

  object Error {

    object BackToPreferences {
      val empty = "You must select an option to continue"
    }

    object Business {

      object SoleTrader {
        val empty = "You must select an option to continue"
        val invalid = "You must select an option to continue"
      }

      object AccountingPeriodPrior {
        val empty = "You must select an option to continue"
        val invalid = "You must select an option to continue"
      }

      object RegisterNextAccountingPeriod {
        val empty = "You must select an option to continue"
        val invalid = "You must select an option to continue"
      }

    }

    object Property {

      object Income {
        val empty = "You must select an option to continue"
        val invalid = "You must select an option to continue"
      }

    }

    object Date {
      val empty = "You must enter a date"
      val invalid = "You must enter a valid date"
      val end_violation = "You must enter a date greater than the start date"
    }

    object BusinessAccountingPeriod {
      val minStartDate = "You can't enter a start date before 6 April 2017"
      val maxEndDate = "You must provide an end date that is not more than 24 months after your start date"
    }

    object BusinessName {
      val empty = "You must enter your Business name"
      val maxLength = "You can't enter more than 105 characters for your Business name"
      val invalid = "The business name contains invalid characters"
    }

    object ContactEmail {
      val empty = "Please enter a contact email"
      val maxLength = "The email is too long"
      val invalid = "The email is invalid"
    }

    object AccountingMethod {
      val empty = "You must select an option to continue"
      val invalid = "You must select an option to continue"
    }

    object NotEligible {
      val empty = "You must select an option to continue"
      val invalid = "You must select an option to continue"
    }

    object OtherIncome {
      val empty = "You must select an option to continue"
      val invalid = "You must select an option to continue"
    }

    object IncomeSource {
      val empty = "You must select an option to continue"
      val invalid = "You must select an option to continue"
    }

    object Terms {
      val empty = "You must confirm T&C's to continue"
    }

    object ClientDetails {
      val firstNameEmpty = "You must enter your first name"
      val lastNameEmpty = "You must enter your last name"
      val firstNameInvalid = "Your first name contains invalid characters"
      val lastNameInvalid = "Your last name contains invalid characters"
      val firstNameMaxLength = "The first name is too long"
      val lastNameMaxLength = "The last name is too long"
    }

    object Nino {
      val empty = "You must enter a National Insurance number"
      val invalid = "You must enter a valid National Insurance number"
    }

  }

  object Eligible {
    val title = "You can send digital updates"
    val heading: String = title
    val line_1 = "Your answers mean you should be able to start sending HMRC digital updates after you sign up."
    val line_2 = "You just need to enter a few more details."
  }

  object Not_Eligible {
    val title = "You can't send digital updates yet"
    val heading: String = title
    val line_1 = "Your answers mean you're not eligible to send digital updates to HMRC right now."
    val line_2 = "You can still sign up for the service. HMRC are working to make digital updates available for all sole traders in the coming months. If you sign up now, you will be able to send digital updates to HMRC when you become eligible."
    val line_3 = "If you choose to sign up, we'll ask you a few questions about your income and contact details."
    val signUp = "I want to sign up"
    val signOut = "I don't want to sign up - sign me out"
    val question = "Choose what you would like to do next"
  }

  object NoNino {
    val title = "You can't sign up for quarterly reporting yet"
    val heading = title
    val line1 = "You can sign up later in the year when this service is available to you."
  }

  object ThrottleLimit {
    val title = "Service is unavailable due to maintenance"
    val heading = title
    val line1 = "Please try again later."
  }

  object MainIncomeError {
    val title = "You can't sign up your client yet"
    val heading = "You can't sign up your client yet"
    val para1 = "At the moment, you can only sign up if your client is one of the following:"
    val para2 = "You'll be able to send quarterly reports for other income later in the year."
    val bullet1 = "a sole trader with income from one business"
    val bullet2 = "someone who rents out a UK property"
    val bullet3 = "a sole trader with income from one business and you rent out a UK property"
  }

  object OtherIncomeError {
    val title = "You can only send quarterly reports on part of your client's income"
    val heading: String = title
    val para1 = "As this service is currently a trial, you can only send quarterly reports on income from your client's sole trader business, renting out a UK property or both."
  }

  object OtherIncome {
    val title = "Does your client have any other sources of income?"
    val heading = "Does your client have any other sources of income?"
    val para1 = "This could include:"
    val bullet1 = "employment that isn't their sole trader business"
    val bullet2 = "UK pensions or annuities"
    val bullet3 = "taxable state benefits"
    val bullet4 = "employment or investments from outside the UK"
    val bullet5 = "capital gains"
    val yes = "Yes"
    val no = "No"
  }


  object NotEnrolledAgentServices {
    val title = "You can't sign up your clients yet"
    val heading = title
    val linkText = "create an Agent Services account"
    val para1 = s"To sign up for quarterly reporting, you must $linkText"
    val button = "Sign out"
  }

  object ClientDetailsError {
    val title = "Client match error"
    val heading = "We couldn't confirm your client"
    val line1 = "The details you've entered are not on our system."
  }

  object ClientDetails {
    val title = "Enter your client's details"
    val heading = "Enter your client's details"
    val line1 = "We will attempt to match these details against information we currently hold."
    val field1 = "First name"
    val field2 = "Last name"
    val field3 = "National Insurance number"
    val field4 = "Date of birth"
    val formhint1_line1 = "This is on your client's National Insurance card, payslip or P60."
    val formhint1_line2 = "For example, 'QQ 12 34 56 C'."
    val formhint2 = "For example, 10 12 1990"
  }

}
