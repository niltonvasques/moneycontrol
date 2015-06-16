Feature: Invoices 

  Scenario: After start the app 
    When I press "OK"
    Then I see "Carteira"

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
    

    
