package com.shtibel.truckies.jsonParser;

/**
 * Created by shomron-ssd on 9/8/2015.
 */
public class ResultEntity {



    private String result;
    private boolean isOk;

    public ResultEntity(){

    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean getIsOk() {
        return isOk;
    }

    public void setIsOk(boolean isOk) {
        this.isOk = isOk;
    }
}
