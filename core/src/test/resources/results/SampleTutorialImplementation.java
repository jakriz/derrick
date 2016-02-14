package com.test;

import java.util.*;

public class TestInterfaceDerrickImpl implements TestInterface {

    public List<String> theMethod() {
        List<String> list = new ArrayList<>();
        list.add("a");
        return list;
    }
}
