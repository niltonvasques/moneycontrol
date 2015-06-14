package br.niltonvasques.moneycontrol.business;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import br.niltonvasques.moneycontrol.database.DatabaseHandler;
import br.niltonvasques.moneycontrol.database.QuerysUtil;
import br.niltonvasques.moneycontrol.database.bean.ContaAPagar;
import br.niltonvasques.moneycontrol.util.DateUtil;
import br.niltonvasques.moneycontrol.util.MessageUtils;

/**
 * Created by niltonvasques on 6/13/15.
 */
public class ContaAPagarBusiness {
    private static final String TAG = "[ContaAPagarBusiness]";

    public static List<ContaAPagar> getContaAPagarsOnMonth(DatabaseHandler db, GregorianCalendar range, GregorianCalendar lastDay) throws Exception{
        GregorianCalendar tmp = lastDay;
        List<ContaAPagar> contasList = db.select(ContaAPagar.class, QuerysUtil.whereContasAPagarOnMonth(tmp.getTime()));
        GregorianCalendar gC = (GregorianCalendar) range.clone();
        List<ContaAPagar> newItems = new ArrayList<ContaAPagar>();
        for(ContaAPagar c : contasList){
            Log.d(TAG, "Conta: " + c.getDescricao() + " repeticao: " + c.getId_Repeticao());
            if(c.getId_Repeticao() == 2){ // SEMANAL
                long diffDays = DateUtil.diffDays(DateUtil.sqlDateFormat().parse(c.getData()), range.getTime());
                long shiftDays = 7 - diffDays % 7;
                if(diffDays < 0){ // Se a diferença for negativa a data está no mesmo mês
                    diffDays = 0;
                    shiftDays = 0;
                    gC.setTime(DateUtil.sqlDateFormat().parse(c.getData()));
                }
                Log.d(TAG, "diffDays " + diffDays + " shiftDays: " + shiftDays);
                diffDays += shiftDays;
                long qtd = diffDays / 7;
                Log.d(TAG, "qtd: " + qtd);
                gC.add(Calendar.DAY_OF_MONTH, (int) shiftDays);
                c.setData(DateUtil.sqlDateFormat().format(gC.getTime()));
                qtd++;
                int restDaysOnMonth = tmp.get(Calendar.DAY_OF_MONTH) - gC.get(Calendar.DAY_OF_MONTH);
                int max = Math.min(c.getQuantidade() - (int)qtd, restDaysOnMonth / 7 );
                Log.d(TAG, "restDaysOnMonth: " + restDaysOnMonth + " max: " + max);
                for(int i = 0; i < max; i++ ){
                    ContaAPagar cp = (ContaAPagar) c.clone();
                    gC.add(Calendar.DAY_OF_MONTH, 7);
                    cp.setData(DateUtil.sqlDateFormat().format(gC.getTime()));
                    Log.d(TAG, "ADD " + cp.getDescricao()+ " - " +cp.getData());
                    newItems.add(cp);
                }
            }
        }
        contasList.addAll(newItems);
        return contasList;
    }
}
