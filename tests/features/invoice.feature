Feature: Invoices 

  Scenario: After start the app 
    When I press "OK"
    Then I see "Carteira"

  Scenario: Invoice for credit card
    When I create a credit card with name "VISA", vencimento "22" and fechamento "7"
    And I press "VISA"
    Then I should see "VISA"
    When I add a expense with value "2500" and text "Geladeira" at day "07"
    Then I should see "2500"
    And I should see "Geladeira"
    Given I open menu
    And I press "Contas a pagar"
    Then I should see "VISA"
    And I should see "$ 2500,00"

  Scenario: Add a invoice  
    When I open menu 
    And I press "Contas a pagar"
    Then I see "Contas a pagar"
    When I press view with id "action_add" 
    Then I should see "Adicionar conta a pagar"
    When I enter text "Energia" into field with id "addContaAPagarDialogEditTxtDescrição"
    And I enter text "100" into field with id "addContaAPagarDialogEditTxtValor"
    And I hide the keyboard
    And I press "Ok"
    Then I should see "Energia"
    And I should see "$ 100,00"

  Scenario: Delete a invoice 
    When I open menu 
    And I press "Contas a pagar"
    Then I see "Contas a pagar"
    When I long press "Energia"
    Then I should see "Deseja excluir"
    And I press "Sim"
    Then I should not see "Energia"

    

    
