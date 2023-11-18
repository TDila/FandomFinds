package com.vulcan.fandomfinds.Helper;

import android.content.Context;
import android.widget.Toast;

import com.vulcan.fandomfinds.Domain.NewArrivalDomain;

import java.util.ArrayList;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;

    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }
    public void insertFood(NewArrivalDomain item){
        ArrayList<NewArrivalDomain> listpop = getListCart();
        boolean existAlready = false;
        int n = 0;
        for (int i = 0; i < listpop.size();i++){
            if(listpop.get(i).getTitle().equals(item.getTitle())){
                existAlready = true;
                n = i;
                break;
            }
        }
        if(existAlready){
            listpop.get(n).setNumberInCart(item.getNumberInCart());
        }else{
            listpop.add(item);
        }
        tinyDB.putListObject("CartList",listpop);
        Toast.makeText(context,"Added to your Cart",Toast.LENGTH_LONG).show();
    }
    public ArrayList<NewArrivalDomain> getListCart(){
        return tinyDB.getListObject("CartList");
    }

    public Double getTotal(){
        ArrayList<NewArrivalDomain> listItem = getListCart();
        double fee = 0;
        for (int i = 0;i < listItem.size();i++){
            fee = fee + (listItem.get(i).getPrice() * listItem.get(i).getNumberInCart());
        }
        return fee;
    }

    public void minusNumberItem(ArrayList<NewArrivalDomain> listItem,int position,ChangeNumberitemsListener changeNumberitemsListener){
        if(listItem.get(position).getNumberInCart() == 1){
            listItem.remove(position);
        }else{
            listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() - 1);
        }
        tinyDB.putListObject("CartList",listItem);
        changeNumberitemsListener.change();
    }

    public void plusNumberItem(ArrayList<NewArrivalDomain> listItem,int position,ChangeNumberitemsListener changeNumberitemsListener){
        listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CartList",listItem);
        changeNumberitemsListener.change();
    }
}
