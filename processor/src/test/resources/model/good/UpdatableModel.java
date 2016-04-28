package com.bluelinelabs.logansquare.processor;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonGetByKey;
import com.bluelinelabs.logansquare.annotation.JsonKey;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class UpdatableModel {

    @JsonKey
    @JsonField
    public String string;

    @JsonField(name = "test_int")
    public int testInt;

    @JsonField(name = "test_long")
    public long testLong;

    @JsonField(name = "test_float")
    public float testFloat;

    @JsonField(name = "test_double")
    public double testDouble;


    @JsonGetByKey
    public UpdatableModel jsonGetByKey(String string) {
        UpdatableModel model = new UpdatableModel();
        model.testInt = 60;
        model.string = "abc";
        return model;
    }
}
