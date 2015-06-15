Feature: Manage accounts 

  Scenario: After start the app 
    When I press "OK"
    Then I see "Carteira"
    
  Scenario: Try delete an account 
    When I long press "Carteira"
    Then I see "Atenção!"
    Then I see "Deseja excluir esta conta?"
    When I press "Sim"
    Then I should not see "Carteira"

  Scenario: Add an account 
    When I press view with id "action_add" 
    Then I see "Adicionar conta"
    When I enter text "Banco do Brasil" into field with id "addContaDialogEditTxtNome"
    And I enter text "250" into field with id "addContaDialogEditTxtSaldo"
    And I hide the soft keyboard 
    And I press "Ok"
    Then I should see "Banco do Brasil"
    And I should see "$ 250,00"

  Scenario: Edit an account
    When I press view with id "contaListItemBtnEditConta" 
    Then I see "Editar conta"
    When I clear input field with id "editContaDialogEditTxtNome" 
    And I clear input field with id "editContaDialogEditTxtSaldo"
    And I hide the soft keyboard 
    And I enter text "Bradesco" into field with id "editContaDialogEditTxtNome"
    And I enter text "269" into field with id "editContaDialogEditTxtSaldo"
    And I hide the soft keyboard 
    And I press "Ok"
    Then I should see "Bradesco"
    Then I should see "$ 269,00"
