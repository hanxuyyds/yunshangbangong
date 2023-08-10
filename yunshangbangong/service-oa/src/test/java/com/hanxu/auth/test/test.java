package com.hanxu.auth.test;

import org.junit.jupiter.api.Test;

public class test {
    @Test
    public void test01(){
        Animal animal = new Animal();
        Animal b=animal;
        b.setName("李四");
        System.out.println(animal);
    }
}
