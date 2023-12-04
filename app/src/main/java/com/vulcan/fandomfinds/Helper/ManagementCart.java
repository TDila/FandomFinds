package com.vulcan.fandomfinds.Helper;

import android.content.Context;
import android.widget.Toast;

import com.vulcan.fandomfinds.Domain.ProductsDomain;

import java.util.ArrayList;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;

    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }
    public void insertProduct(ProductsDomain item){
        ArrayList<ProductsDomain> productList = getListCart();
        boolean existAlready = false;
        int n = 0;
        for (int position = 0; position < productList.size();position++){
            if(productList.get(position).getId().equals(item.getId())){
                existAlready = true;
                n = position;
                break;
            }
        }
        if(existAlready){
            productList.get(n).setNumberInCart(item.getNumberInCart());
        }else{
            productList.add(item);
        }
        tinyDB.putListObject("CartList",productList);
        Toast.makeText(context,"Added to your Cart",Toast.LENGTH_LONG).show();
    }
    public ArrayList<ProductsDomain> getListCart(){
        return tinyDB.getListObject("CartList");
    }

    public Double getTotal(){
        ArrayList<ProductsDomain> listItem = getListCart();
        double fee = 0;
        for (int i = 0;i < listItem.size();i++){
            if(listItem.get(i).getDiscount() != 0){
                double oldPrice = listItem.get(i).getPrice();
                fee = fee + ((oldPrice - (oldPrice * listItem.get(i).getDiscount()/100))  * listItem.get(i).getNumberInCart());
            }else{
                fee = fee + (listItem.get(i).getPrice() * listItem.get(i).getNumberInCart());
            }
        }
        return fee;
    }

    public void minusNumberItem(ArrayList<ProductsDomain> listItem,int position,ChangeNumberitemsListener changeNumberitemsListener){
        if(listItem.get(position).getNumberInCart() == 1){
            listItem.remove(position);
        }else{
            listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() - 1);
        }
        tinyDB.putListObject("CartList",listItem);
        changeNumberitemsListener.change();
    }

    public void plusNumberItem(ArrayList<ProductsDomain> listItem,int position,ChangeNumberitemsListener changeNumberitemsListener){
        listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CartList",listItem);
        changeNumberitemsListener.change();
    }
}
