Feature: Add transaction 

  Scenario: After start the app 
    When I press "OK"
    Then I see "Carteira"

  Scenario: Add a expense transaction
    When I press "Carteira"
    Then I should see "Carteira"
    When I press view with id "action_add" 
    Then I should see "Adicionar Transação"
    And I fill add transaction dialog with value "25" and text "Compras"
    And I press "Ok"
    Then I should see "Compras"
    And I should see "- Mercado"
    And I should see "$ 25,00"

  Scenario: Add a revenue transaction
    When I press "Carteira"
    Then I should see "Carteira"
    When I press view with id "action_add" 
    Then I should see "Adicionar Transação"
    And I fill add transaction dialog with value "500" and text "Empresa"
    And I select "Receita" from "addTransacaoDialogSpinnerTipo"
    And I press "Ok"
    Then I should see "Empresa"
    And I should see "- Salário"
    And I should see "$ 475,00"

  Scenario: Delete a transaction
    When I press "Carteira"
    And I long press "Compras"
    Then I should see "Deseja excluir"
    And I press "Sim"
    Then I should not see "Compras"

    
