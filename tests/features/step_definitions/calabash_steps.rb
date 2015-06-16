require 'calabash-android/calabash_steps'

When(/^I hide the soft keyboard$/) do
  hide_soft_keyboard
end

When(/^I hide the keyboard$/) do
  hide_soft_keyboard
end

When(/^I create an account$/) do
  steps %Q{
    When I press view with id "action_add" 
    Then I see "Adicionar conta"
    When I enter text "Banco do Brasil" into field with id "addContaDialogEditTxtNome"
    And I enter text "250" into field with id "addContaDialogEditTxtSaldo"
    And I hide the soft keyboard 
    And I press "Ok"
  }
end

When(/^I create a credit card$/) do
  steps %Q{
    When I press view with id "action_add" 
    Then I see "Adicionar conta"
    When I enter text "VISA" into field with id "addContaDialogEditTxtNome"
    And I select "Cartão de Crédito" from "addContaDialogSpinnerTipo" 
    And I enter text "0" into field with id "addContaDialogEditTxtSaldo"
    And I hide the soft keyboard 
    And I enter text "2500" into field with id "addContaDialogEditTxtLimite"
    And I hide the soft keyboard 
    And I select "3" from "addContaDialogSpinnerVencimento" 
    And I select "1" from "addContaDialogSpinnerFechamento" 
    And I press "Ok"
  }
end

When(/^I create a credit card with name "([^\"]*)"$/) do |name|
  steps %Q{
    When I press view with id "action_add" 
    Then I see "Adicionar conta"
    When I enter text "#{name}" into field with id "addContaDialogEditTxtNome"
    And I select "Cartão de Crédito" from "addContaDialogSpinnerTipo" 
    And I enter text "0" into field with id "addContaDialogEditTxtSaldo"
    And I hide the soft keyboard 
    And I enter text "2500" into field with id "addContaDialogEditTxtLimite"
    And I hide the soft keyboard 
    And I select "3" from "addContaDialogSpinnerVencimento" 
    And I select "1" from "addContaDialogSpinnerFechamento" 
    And I press "Ok"
  }
end

When(/^I create a credit card with name "([^\"]*)", vencimento "(\d+)" and fechamento "(\d+)"$/) do |name, vencimento, fechamento|
  current_vencimento = 1
  current_fechamento = 1
  fechamento = fechamento.to_i
  vencimento = vencimento.to_i
  steps %Q{
    When I press view with id "action_add" 
    Then I see "Adicionar conta"
    When I enter text "#{name}" into field with id "addContaDialogEditTxtNome"
    And I select "Cartão de Crédito" from "addContaDialogSpinnerTipo" 
    And I enter text "0" into field with id "addContaDialogEditTxtSaldo"
    And I hide the soft keyboard 
    And I enter text "2500" into field with id "addContaDialogEditTxtLimite"
    And I hide the soft keyboard 
  }
  while current_vencimento < vencimento do
    steps %Q{
      And I select "#{current_vencimento}" from "addContaDialogSpinnerVencimento" 
    }
    current_vencimento += 5
    current_vencimento = [current_vencimento, vencimento].min
  end
  steps %Q{
    And I select "#{current_vencimento}" from "addContaDialogSpinnerVencimento" 
  }
  while current_fechamento < fechamento do
    steps %Q{
      And I select "#{current_fechamento}" from "addContaDialogSpinnerFechamento" 
    }
    current_fechamento += 5
    current_fechamento = [current_fechamento, fechamento].min
  end
  steps %Q{
    And I select "#{current_fechamento}" from "addContaDialogSpinnerFechamento" 
  }
  steps %Q{
    And I press "Ok"
  }
end

When(/^I fill add transaction dialog$/) do
  steps %Q{
    When I enter text "25" into field with id "addTransacaoDialogEditTxtValor"
    And I hide the keyboard
    And I enter text "Compras" into field with id "addTransacaoDialogEditTxtDescrição"
    And I hide the keyboard
  }
end
When(/^I fill add transaction dialog with value "([^\"]*)" and text "([^\"]*)"$/) do |value, text|

  steps %Q{
    When I enter text "#{value}" into field with id "addTransacaoDialogEditTxtValor"
    And I hide the keyboard
    And I enter text "#{text}" into field with id "addTransacaoDialogEditTxtDescrição"
    And I hide the keyboard
  }
end

When(/^I add a revenue with value "([^\"]*)" and text "([^\"]*)"$/) do |value, text|
  steps %Q{
    When I press view with id "action_add" 
    Then I should see "Adicionar Transação"
    And I fill add transaction dialog with value "#{value}" and text "#{text}"
    And I select "Receita" from "addTransacaoDialogSpinnerTipo"
    And I press "Ok"
    Then I should see "#{text}"
  }
end

When(/^I add a expense with value "([^\"]*)" and text "([^\"]*)" at day "(\d\d)"$/ ) do |value, text, day|
  date = "#{day}-#{Time.now.month}-#{Time.now.year}"
  steps %Q{
    When I press view with id "action_add" 
    Then I should see "Adicionar Transação"
    And I hide the keyboard
    And I press view with id "addTransacaoDialogBtnData"
  }
  set_date("android.widget.DatePicker #{"datePicker"}", date)
  steps %Q{
    And I press "Definir"
    And I fill add transaction dialog with value "#{value}" and text "#{text}"
    And I press "Ok"
  }
end

When(/^I open menu$/) do
  perform_action('swipe', 'left')
end
