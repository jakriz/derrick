package com.test;

import java.util.*;

public class TestInterfaceDerrickImpl implements TestInterface {

    public List<String> theMethod(List<String> list) {
        list.add("a");
        list.add("b");
        // do something with the list
        return list;
    }
}
