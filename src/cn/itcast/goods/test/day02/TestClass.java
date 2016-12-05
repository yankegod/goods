package cn.itcast.goods.test.day02;

import org.junit.Test;

import java.nio.CharBuffer;
import java.util.Scanner;

/**
 * Created by yanke on 2016/12/5.
 */
public class TestClass {

    @Test
    public void  test(){
        Scanner scanner =new Scanner(System.in);

        Apple apple =new Apple();
        String name= apple.getClass().getName();
        String simpleName = apple.getClass().getSimpleName();
        String appleClass = Apple.class.getSimpleName();

        System.out.println(name +"\n"+simpleName);
    }

}

class Apple{
    private int i;

    public void g(){

        System.out.println("I have get a");

    }


}
