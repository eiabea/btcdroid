package com.eiabea.btcdroid.model;

import com.eiabea.btcdroid.model.btce.BTCeTicker;

public class PricesCoinfinity {

    // Attributes
    private BTCeTicker ticker;
    private String base;
    private String atm;
    private String bitcoinbon;

//    {"base":"254.33","atm":"263.85","bitcoinbon":"275.06","pair":"XBTEUR","timestamp":1436534288}


    // Standardconstructor
    public PricesCoinfinity() {
    }

    public float getBase() {
        try {
            return Float.parseFloat(this.base);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public float getAtm() {
        try {
            return Float.parseFloat(this.atm);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public float getBitcoinbon() {
        try {
            return Float.parseFloat(this.bitcoinbon);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

}
