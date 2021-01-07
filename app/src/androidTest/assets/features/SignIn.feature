Feature: Sign In

@smoke
@acceptance

Scenario Outline: Successful sign in
  Given I am on the Sign In screen
  And I enter user email <email>
  And I enter user password <password>
  And I press the Sign In button
  Then I expect to see the "Main Application" screen

  Examples:

  | email          | password        |
  | test@email.com | TestPassword_01 |