package com.vulcan.fandomfinds.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.vulcan.fandomfinds.Activity.CartActivity;
import com.vulcan.fandomfinds.Domain.ProductsDomain;

import java.util.ArrayList;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;
    CoordinatorLayout coordinatorLayout;

    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public ManagementCart(Context context, CoordinatorLayout singleProductCoordinator) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
        this.coordinatorLayout = singleProductCoordinator;
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
        if(coordinatorLayout != null){
            Snackbar snackbar = Snackbar.make(context,coordinatorLayout,"Added to the Cart",Snackbar.LENGTH_LONG);
            snackbar.setAction("Check Cart", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            });
            snackbar.show();
        }
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
