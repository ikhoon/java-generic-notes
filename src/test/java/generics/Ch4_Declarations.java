package generics;

import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

/**
 * Created by ikhoon on 01/07/2017.
 */
public class Ch4_Declarations {

    // 4장 Declarations?
    // 이번장에서는 제너릭 클래스를 어떻게 선언하는지 알아본다.
    // 생성자, static 함수, nested class, 그리고 erasure 작동에 대해서 설명한다.

    // 4.1 Constructors
    // 타입 파라메터는 클래스의 시작부분에 선언이 된다.
    // 생성자가 아니다.
    class Pair<T, U> {
        private final T first;
        private final U second;
        public Pair(T first, U second) { this.first=first; this.second=second; }
        public T getFirst() { return first; }
        public U getSecond() { return second; }
    }



    @Test
    public void typeConstructor() {
        // 하지만 생성자를 부를때, (new 를 호출할때) 타입 파라메터를 전달해줘야 한다.
        // (자바 7에서 바뀌었음)
        Pair<String, Integer> pair = new Pair<String, Integer>("one",2);
        assert pair.getFirst().equals("one") && pair.getSecond() == 2;

        // 이런건 안된다. 나쁘다.
        Pair<String, Integer> pair1 = new Pair("one",2);

    }

    // 4.2 Static Members
    // 제너릭은 컴파일 타임에 다 사라진다.

    // 복습
    // List<String>은 컴파일 되면?
    // List<List<String>>은 컴파일 되면?


    @Test
    public void getClassName() {
        List<Integer> ints = Arrays.asList(1,2,3);
        List<String> strings = Arrays.asList("one","two");
        System.out.println("List<Integer> = " + ints.getClass());
        System.out.println("List<String> = " + strings.getClass());
        // 런타임에는 같은 클래스 정보이다.
        assert ints.getClass() == strings.getClass();
    }

    // 제너릭 클래스의 static 멤버(static 함수가 말고)
    // * static 멤버는 모든 인스턴스에 공유된다. 다른 타입 파라메터일지라도
    // * static 멤버는 클래스의 타입 파라메터를 참조할수 없다.

    static class Cell<T> {
        private final int id;
        private final T value;
        private static int count = 0;
        private static synchronized int nextId() { return count++; }

        // 생성자를 호출할때마다 count를 증가 시킨다
        public Cell(T value) { this.value=value; id=nextId(); }

        public T getValue() { return value; }
        public int getId() { return id; }
        public static synchronized int getCount() { return count; }
    }

    @Test
    public void getCount() {
        Cell<String> a = new Cell<String>("one");
        Cell<Integer> b = new Cell<Integer>(2);
        assert a.getId() == 0 && b.getId() == 1 && Cell.getCount() == 2;

    }

    // 위결과가 동작하는 이유는 static 멤버는 타입 파라메터에 독립적이다.
    // 그렇기 때문에 타입 파라메터를 이용한 접근은 안된다.

    @Test
    public void getCountWithType() {
        Cell.getCount(); // ok
//        Cell<Integer>.getCount(); // compile-time error
//        Cell<?>.getCount();  // error
    }


    /**
    static class Cell2<T> {
        private final T value;
        private static List<T> values = new ArrayList<T>(); // illegal

        public Cell2(T value) { this.value=value; values.add(value); }

        public T getValue() { return value; }
        public static List<T> getValues() { return values; } // illegal
    }
    **/

    static class Cell3<T> {
        private final T value;
        private static List<Object> values = new ArrayList<Object>(); // ok
        public Cell3(T value) { this.value=value; values.add(value); }
        public T getValue() { return value; }
        public static List<Object> getValues() { return values; } // ok
    }

    @Test
    public void getValues() {
        Cell3<String> a = new Cell3<String>("one");
        Cell3<Integer> b = new Cell3<Integer>(2);
        // 돌아 가긴한다.
        assert Cell3.getValues().toString().equals("[one, 2]");
    }


    // 4.3 Nested Classes
    // 자바는 클래스 안에 클래스를 선언할수있음
    // inner class가 static이 아니면 outer의 type parameter가 inner class에서 사용가능
    // static member 개념과 비슷한 맥락인거 같다.

    static class MyLinkedCollection<E> extends AbstractCollection<E> {
        private class Node {
            // inner class에서 outer class의 타입 파라메터를 접근한다.
            private E element;
            private Node next = null;
            private Node(E elt) { element = elt; }
        }

        private Node first = new Node(null);
        private Node last = first;
        private int size = 0;

        public MyLinkedCollection() { }
        public MyLinkedCollection(Collection<? extends E> c) { addAll(c); }

        public int size() { return size; }

        public boolean add(E elt) {
            last.next = new Node(elt);
            last = last.next;
            size++;
            return true;
        }

        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private Node current = first;
                public boolean hasNext() { return current.next != null; }
                public E next() {
                    if (current.next != null) {
                        current = current.next;
                        return current.element;
                    } else throw new NoSuchElementException();
                }
                public void remove() { throw new UnsupportedOperationException(); }
            };
        }
    }


    // 위의 Node가 LinkedCollection<E>.Node 였다면
    // 아래는 LinkedCollection.Node<E> 이다.
    static class MyLinkedCollection2<E> extends AbstractCollection<E> {
        // static으로 선언하고 별도의 type paramter를 가지고 있다
        private static class Node<T> {
            private T element;
            private Node<T> next = null;
            private Node(T elt) { element = elt; }
        }

        private Node<E> first = new Node<E>(null);
        private Node<E> last = first;
        private int size = 0;

        public MyLinkedCollection2() { }
        public MyLinkedCollection2(Collection<? extends E> c) { addAll(c); }

        public int size() { return size; }
        public boolean add(E elt) {
            last.next = new Node<E>(elt);
            last = last.next;
            size++;
            return true;
        }

        private static class LinkedIterator<T> implements Iterator<T> {
            private Node<T> current;
            public LinkedIterator(Node<T> first) { current = first; }
            public boolean hasNext() { return current.next != null; }
            public T next() {
                if (current.next != null) {
                    current = current.next;
                    return current.element;
                } else throw new NoSuchElementException();
            }

            public void remove() { throw new UnsupportedOperationException(); }
        }

        public Iterator<E> iterator() { return new LinkedIterator<E>(first); }
    }

    // 2가지의 구현 예를 보여 주었다.
    // 많은 java 책에서 inner class에 대해서 static으로 선언하는게 좋다고 한다.
    // 이책도 그와 비슷하게
    // 2번째 구현을 더 선호한다. 더 간단하고 더 효율적이라 한다.




    // 4.4 How Erasure Works
    // 이부분이 이장에서는 가장 흥미로운 부분인것 같다.
    // 1. 타입 파라메터가 있는 곳에서 모든 타입들을 잘라버린다. List<T> -> List
    // 2. 타입 변수가 있는곳에 바운드가 있으면 그 타입 아니면 Object로 치환한다. max(T a) -> max(Object a)

    // 예)
    // List<Integer>, List<List<String>> -> List

    // List<Integer>[] -> List[]

    // List는 그자체로 raw 타입이다.

    // int도 당연히 int, 모든 primitive type이 이에 해당한다.

    // Integer도 Integer, 모든 타입 파라메터가 없는 타입이 이에 해당한다.

    // asList(T... t)의 T -> Object, T는 바운드가 없다.

    // T max(Collection) 의 T -> Comparable, T의 바운드는 Comparable<? super T>

    // QUIZ : 아래 함수에서 S와 T의 타입 바운드는?
    public static <S extends Readable & Closeable, T extends Appendable & Closeable>
    void copy(S src, T trg, int size) throws IOException { }







    // 3.6절에 나오는 max의 정의를 이용하면 T -> Object이다. T의 바운드는 Object & Comparable<T>,
    // Comparable이 하위 타입같은데 헷갈림,  we take the erasure of the leftmost bound.
    // 비슷한 질문을 한사람이 역시나 stackoverflow에 있음
    // 결론은 그냥 왼쪽거다?
    // 여러개 타입이 바운드일때
    // class & interface1 & interface2 & ...
    // interface1 & interface2 & ...
    // 또다른 추측은 유저에서 하위 코드와 호환성유지를 할때 컨트롤 할수 있게 했다는 말이 있음
    // https://stackoverflow.com/questions/15296193/what-is-meant-by-left-most-bound-for-generic-type-or-a-method-and-why-was-this-p

    // inner class: LinkedCollection<E>.Node or LinkedCollection.Node<E> -> LinkedCollection.Node


    // 이번엔 또다른 흥미로운 주제, overloaded
    // 하나의 클래스에 이름이 같고 타입 인자가 다른 함수의 예를 보자.


    // QUIZ: 아래 코드는 컴파일이 될까 안될까?
    /**
    static class Overloaded {
        public int sum(List<Integer> ints) {
            int sum = 0;
            for (int i : ints) sum += i; return sum;
        }
        public String sum(List<String> strings) {
            StringBuffer sum = new StringBuffer(); for (String s : strings) sum.append(s); return sum.toString();
        }
    }
     */





    // 안타깝게도 되지 않는다.
    // 책에는 된다고 되어 있느디, 뭔가 중간에 바뀌었나 봉가?
    // erasure 되고 나면 아래와 같이 타입 달라서 된다고 되어 있는데 여기서는 안되네
    //
    // int sum(List)
    // String sum(List)

    // 테스트를 해보니 generic이 아닌 형태에서도
    // 인자가 같고 return 타입이 다른건 허용이 안된다.
    /**
    static class Overloaded2 {
        public Long one(Long a) {
            return a;
        }
        public Integer one(Long a) {
            return a.intValue();
        }
    }
    */



    // QUIZ: 아래코드는 될까 안될까

    /**
    class MyInteger implements Comparable<Integer>, Comparable<Long> {
        // compile-time error, cannot implement two interfaces with same erasure private final int value;
        public MyInteger(int value) { this.value = value; }
        public int compareTo(Integer i) {
            return (value < i.value) ? -1 : (value == i.value) ? 0 : 1; }
        public int compareTo(Long l) {
            return (value < l.value) ? -1 : (value == l.value) ? 0 : 1; }
    }
    */




    // 위의 코드도 되지 않는다.
    // Comparable로 바뀌어서 같은 interface를 중복으로 상속 받는 형국이다.
    // 실제 이게 컴파일 되면 앞에서 나왔던 bridge 함수가 겁내 복잡해질수 있다한다.
    // 역시나 겁나 하위호완성 챙김
    



}






