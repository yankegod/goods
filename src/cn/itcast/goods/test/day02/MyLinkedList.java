package cn.itcast.goods.test.day02;

/**
 * Created by yanke on 2016/12/2.
 */
public class MyLinkedList {
    private Node first;
    private Node last;

    private int size;

    public void add(Object obj){
        Node n = new Node();
        if(first == null){
            n.setPrevious(null);
            n.setObj(obj);
            n.setNext(null);
            first = n;
            last = n;
        }else{
            n.setPrevious(last);
            n.setObj(obj);
            n.setNext(null);

            last.setNext(n);
            last = n;
        }
        size++;
    }

//    public void remove(Objects obj){
//
//    }

    public Object get(int index){
        Node temp = null;
        if(first != null){
            for (int i = 0; i <index ; i++) {
                temp = temp.getNext();
            }
        }
        return temp.getObj();
    }
    public int getsize(){
        return size;
    }
    public static void main(String[] str){
        MyLinkedList list = new MyLinkedList();
        list.add("aaa");
        list.add("bbb");

        System.out.println(list.getsize());
    }
}

class Node{
    private Node previous;
    private Object obj;
    private Node next;

    public Node getPrevious() {
        return previous;
    }

    public Object getObj() {
        return obj;
    }

    public Node getNext() {
        return next;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}

