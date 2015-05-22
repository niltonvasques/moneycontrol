Feature: Manage accounts 

  Scenario: After start the app 
    When I press "OK"
    Then I see "Carteira"
    
  Scenario: Try delete an account 
    When I long press "Carteira"
    Then I see "Atenção!"

  Scenario: Add an account 
    When I press view with id "action_add" 
    Then I see "Adicionar conta"
