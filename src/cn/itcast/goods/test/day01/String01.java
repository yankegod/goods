package cn.itcast.goods.test.day01;

import org.junit.Test;

/**
 * Created by yanke on 2016/12/1.
 */
/*
equals:比较的是两个字符串中的每个字符是否相同  。重写了Object类的equals方法
==:比较的是两个字符串是否同时引用的一个地址。注意相同字符串常量的存储位置相同。
compareTo(): 这个函数的作用就是对两个字符串按字典排序的方式进行比较，返回两个字符串中第一个不同的字符的ascII码差值。
 */
public class String01 {

    public static String a ="ab"+"c";
    public static String b = "abc";
    //  + 和 ==的优先级问题，先执行+
    @Test
    public void f() {
        System.out.println(a==b +":"+a.equals(b)); //输出为false
    }
    @Test
    public  void g(){
        System.out.println("："+a==b);    //输出为false
    }
    @Test
    public void k(){
        System.out.println("a=b:"+a.equals(b));//   a=b:true
    }
    @Test
    public void m(){
        System.out.println("a=b"+a==b);   //false
    }
    @Test
    public void l(){
        System.out.println("a=b:"+(a==b));   //a=b:true
    }

}
