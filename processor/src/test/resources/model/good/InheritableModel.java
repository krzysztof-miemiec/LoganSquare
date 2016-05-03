package com.bluelinelabs.logansquare.processor;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonGetByKey;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonInherit;

@JsonObject
public class InheritableModel {

    @JsonField(isKey = true)
    public String string;

    @JsonField(name = "test_int")
    public int testInt;

    @JsonField(name = "test_long")
    public long testLong;

    @JsonField(name = "test_float")
    public float testFloat;

    @JsonField(name = "test_double")
    public double testDouble;

    @OnJsonInherit
    public void inherit(UpdatableModel model) {
        testInt = model.testInt;
    }

    @JsonGetByKey
    public InheritableModel jsonGetByKey(String string) {
        InheritableModel model = new InheritableModel();
        model.testInt = 60;
        model.string = "abc";
        return model;
    }
}
