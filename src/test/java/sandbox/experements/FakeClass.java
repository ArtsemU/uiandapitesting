package sandbox.experements;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FakeClass {
    public static void main(String[] args) {

        List<String> stringList = new ArrayList<>();
        stringList.add("Hello");
        stringList.add("World");
        stringList.get(0);

        for (String s : stringList) {
            System.out.println(s);
        }
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add("Hello");

        List<String> stringLinkedList = new LinkedList<>();
        stringLinkedList.add("Hello");
        stringLinkedList.add("World");

        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("Hello");


    }
}
