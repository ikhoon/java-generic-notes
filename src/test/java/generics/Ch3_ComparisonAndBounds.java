package generics;

import org.junit.Test;
import sun.awt.im.CompositionArea;

import java.io.Closeable;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.*;

/**
 * Created by ikhoon on 24/06/2017.
 */
public class Ch3_ComparisonAndBounds {
    // 벌서 베이직은 다 배웠다 한다.
    // 이론은 끝났다는 이야기 인듯
    // 조금 실망이긴 하지만
    // 이제부터는 generic의 보다 고급진 generic의 사용법에 대해서 알아보자.


    // 이번장에서는 Comparable<T>, Comparator<T> 에 대해서 알아본다.
    // sort, max을 값을 찾을때 유용하다.
    // 그리고 type variable의 bound, 제약에 대해서 소개한다.


    // 3.1 Comparable

    // 이것이 뭐신가 하면 하나의 객체와 다른 객체를 비교하는것이다.

    interface MyComparable<T> {
        int compareTo(T o);
    }

    public void compareInt() {
        Integer int0 = 0;
        Integer int1 = 1;
        assert int0.compareTo(int1) < 0;
    }

    public void compareString() {
        String str0 = "zero";
        String str1 = "one";
        assert str0.compareTo(str1) > 0;
    }

    // 별거없다.
    // string과 int는 Comparable<T>를 구현하고 있다.
    // 그러면 string과 int의 비교는?

    // 컴파일이 되지 않는다.
    // 좋다!
    public void compareIntAndString() {
        Integer i = 0;
        String s = "one";
//        assert i.compareTo(s) < 0; // compile-time error
    }


    // Quiz : Integer 와 Double의 비교는? 될까 안될까?



    public void compareIntAndDouble() {

        // 다른 타입이라서 안된다
        Integer m = new Integer(2);
        Double n = new Double(3.14);
//        assert m.compareTo(n) < 0; // compile-time e


        // 그련 같은 타입으로 만들면?
        Number m1 = new Integer(2);
        Number n2 = new Double(3.14);
//        assert m1.compareTo(n2) < 0; // compile-time e
        // 안된다. Number는 Comparable<T>를 구현하지 않았다.


    }

    class Foo implements Comparable<Foo> {

        int foo;

        public Foo(int foo) {
            this.foo = foo;
        }

        @Override
        public int compareTo(Foo o) {
            return foo < o.foo ? -1 :
                    foo == o.foo ? 0 : 1;
        }

        @Override
        public int hashCode() {
            return foo;
        }

//        @Override
//        public boolean equals(Object obj) {
//            return foo == ((Foo) obj).foo;
//        }

        @Override
        public String toString() {
            return "Foo(" + foo + ")" + "@" + hashCode();
        }
    }

    // Quiz : 이것의 출력값은?
    @Test
    public void sortedSet() {
        // SortedSet
        Set<Foo> is = new TreeSet<>();
        Foo a = new Foo(1);
        Foo b = new Foo(2);
        Foo c = new Foo(1);
        is.add(a);
        is.add(b);
        is.add(c);
        for (Foo i : is) {
            // 1, 1, 2
            System.out.println(i);
        }
    }


    @Test
    public void hashSet() {
        // hashCode
        Set<Foo> is = new HashSet<>();
        Foo a = new Foo(1);
        Foo b = new Foo(2);
        Foo c = new Foo(1);
        is.add(a);
        is.add(b);
        is.add(c);
        for (Foo i : is) {
            System.out.println(i);
        }
    }

    @Test
    public void hashSet2() {
        // hashCode
        Set<Foo> is = new HashSet<>();
        Foo a = new Foo(1);
        is.add(a);
        a.foo = 2;
        Foo b = a;
        is.add(b);
        for (Foo i : is) {
            System.out.println(i);
        }
    }

    class Bar {
        int bar;

        public Bar(int bar) {
            this.bar = bar;
        }
    }

    @Test
    public void sortedSetWithBar() {
        Set<Bar> is = new TreeSet<>();
        Bar a = new Bar(1);
        Bar b = new Bar(2);
        Bar c = new Bar(1);
        is.add(a);
        is.add(b);
        is.add(c);
        for (Bar i : is) {
            System.out.println(i);
        }
    }

    // if and only if https://en.wikipedia.org/wiki/If_and_only_if

    // equals와 comapreTo
    // two objects are equal if and only if they compare as the same

    // 두 객체의 equals가 참이면 compareTo가 같다.
    // 두 객체의 compareTo가 같으면 equals가 참이다.

    // SortedSet이나 SortedMap은 이 equals를 이용하여 비교한다. object의 hashcode가 아니다.





    // compareTo를 구현할때는 아래와 같이 구현하면 안된다.

    class MyInteger implements Comparable<MyInteger> {
        int value;
        public int compareTo(MyInteger that) {
            return this.value - that.value;
        }
    }
    class MyInteger1 implements Comparable<MyInteger1> {
        int value;
        public int compareTo(MyInteger1 that) {
            return this.value < that.value ? -1 :
                    this.value == that.value ? 0 : 1 ;
        }
    }

    // 3.2 Maximum of a Collection
    // Comparable<T> interface를 이용해서 collection의 maximum을 어떻게 찾는지 알아보자.
    // 우선 간단한 버전부터
    // 여기서 T는 Comparable<T>의 하위 타입이다.
    public static <T extends Comparable<? super T>> T max1(Collection<? extends T> coll) {
        T candidate = coll.iterator().next();
        // GET
        for (T elt : coll) {
            // PUT
            if (candidate.compareTo(elt) < 0) candidate = elt; }
        return candidate;
    }


    // 중요! : super 키워드는 wildcard가 아니면 bound되지 않는다.!!

    interface C<A, B> {}
    interface D<A, B> {}

    // 그리고 mutually recursive bound
    <T extends C<T,U>, U extends D<T,U>>
    void recursiveBound(T t, U u) {

    }
    // 이거의 대한 예제는 9.5에서 나온다고 한다.

    // max method의 시그니쳐를 최대한 활용할수 있도록 generalize 해야한다.
    // type parameter와 wildcard를 이용해서 바꿀수 있다.

    <T extends Comparable<? super T>> T max2(Collection<? extends T> coll) {
        T candidate = coll.iterator().next();
        for (T elt : coll) {
            if (candidate.compareTo(elt) < 0) candidate = elt; }
        return candidate;
    }

    /**
     * @see Collections#max(Collection)
     * @param coll
     * @param <T>
     * @return
     */
    // 실제 자바 API는 앞의 것보다 좀더 구리다.
    // 그이유는 하위호환성 때문이라 함. 3.6장에 나온다함
    <T extends Object & Comparable<? super T>> T max3(Collection<? extends T> coll) {
        T candidate = coll.iterator().next();
        for (T elt : coll) {
            if (candidate.compareTo(elt) < 0) candidate = elt; }
        return candidate;
    }




    // 3.3 A Fruity Example
    // Comparable<T>은 비교하는것에 대한 컨트롤을 할수 있게 한다.
    // 비교를 금지할수도 허락할수도 있게 한다.

    // 이코드는 Apple과 Orange를 비교하는것을 금지한다.
    class Fruit {}
    class Apple extends Fruit implements Comparable<Apple> {
        @Override public int compareTo(Apple o) { return 0; }
    }
    class Orange extends Fruit implements Comparable<Orange> {
        @Override public int compareTo(Orange o) { return 0; }
    }

    public void compareAppleOrange() {
        Apple apple = new Apple();
        Orange orange = new Orange();
//        apple.compareTo(orange); // 컴파일 안됨
    }


    class Fruit1 implements Comparable<Fruit1>{
        @Override public int compareTo(Fruit1 o) { return 0; }
    }
    class Apple1 extends Fruit1 {}
    class Orange1 extends Fruit1 {}

    public void comparedApple1Orange1() {
        Apple1 apple1 = new Apple1();
        Orange1 orange1 = new Orange1();
        apple1.compareTo(orange1);  // 비교가 가능함

    }

    @Test
    public void maxFruit() {
        List<Apple> apples = Arrays.asList(new Apple(), new Apple());
        List<Apple1> apple1s = Arrays.asList(new Apple1(), new Apple1());
        this.<Apple1>max1(apple1s);
        max1(apple1s);
        this.<Fruit1>max1(apple1s);
//        max2(apples);
//        max3(apples);
//
//        // 안됨
////        max1(apple1s);
//        // 됨
//        max2(apple1s);
//        max3(apple1s);


        // Apple1은 Comparable<T>를 바로 구현하고 있지 않아서 max1의 함수를 사용할수 없다.
        // 즉 max1 함수가 덜 generic하다.

    }


    // 3.4 Comparator
    // * Comparable interface를 구현하고 싶지 않을때
    // * 기존에 상속에 의해 구현된것과 다른 compare를 사용하고 싶을때

    interface MyComparator<T> {
        // compare의 구현은 compareTo와 비슷하게 하면 된다.
        // o1이 o2보다 더작으면 음수, 같으면 0, 더 크면 양수
        int compare(T o1, T o2);
        boolean equals(Object obj);
    }

    // 자바의 라이브러리들은 Comparable과 Comparator 둘가지 버전의 API를 제공한다
    // comparable 버전
    <T extends Comparable<? super T>> T max4(Collection<? extends T> coll) {
        T candidate = coll.iterator().next();
        for (T elt : coll)
            if (candidate.compareTo(elt) < 0) candidate = elt;
        return candidate;
    }

    // comparator 버전
    <T> T max5(Collection<? extends T> coll, Comparator<? super T> cmp)
    {
        T candidate = coll.iterator().next();
        for (T elt : coll)
            // candidate.compareTo(elt) -> cmp.compare(candidate,elt)
            if (cmp.compare(candidate, elt) < 0) candidate = elt;
        return candidate;
    }

    // 그리고 comparable을 활용한 comparator 구현을 활용한면 두가지 버전을 중복 코드 없이 제공할수 있다
    <T extends Comparable<? super T>>
    Comparator<T> naturalOrder() {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        };
    }

    <T extends Comparable<? super T>>
    T max6(Collection<? extends T> coll) {
       return max5(coll, this.<T>naturalOrder());
    }

    // comparator가 있으면 min함수를 구현하기도 쉽다.
    // 역순의 comparator를 넘겨주면 된다.

    // comparator가 있는 경우
    <T> Comparator<T> reverseOrder(final Comparator<T> cmp) {
        return new Comparator<T>() {

            @Override
            public int compare(T o1, T o2) {
                // 반대로 비교
                return cmp.compare(o2, o1);
            }
        };
    }

    // comparable이 있는 경우
    <T extends Comparable<? super T>>
    Comparator<T> reverseOrder() {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o2.compareTo(o1);
            }
        };
    }

    // comparator가 있는 경우
    <T>
    T min(Collection<? extends T> coll, Comparator<? super T> cmp) {
        return max5(coll, reverseOrder(cmp));
    }

    // comparable이 있는경우
    <T extends Comparable<? super T>>
    T min(Collection<? extends T> coll) {
        return max5(coll, this.<T>reverseOrder());
    }

    // 중복코드가 없으면 관리하기 편한다.
    // 그러나 좀더 빠르다고 한다.
    // 30% 정도 성능의 차이가 있어서 일부러 구리게 만들고 있다고함.
    // 하지만 우리는 이럴 필요가 없다고 한다.
    // 쓸모없는 행동이라 한다.




    // 3.5 Enumerated Types
    // 자바 5부터 추가된 기능
    enum Season { WINTER, SPRING, SUMMER, FALL }

    // 각각의 enum type은 java.lang.Enum의 하위 클래스이다.

    public static abstract class MyEnum<E extends MyEnum<E>> implements Comparable<E> {
        private final String name;
        final int ordinal;
        protected MyEnum(String name, int ordinal) {
            this.name = name; this.ordinal = ordinal;
        }
        public final String name() { return name; }
        public final int ordinal() { return ordinal; }
        public String toString() { return name; }
        public final int compareTo(E o) { return ordinal - o.ordinal; }
    }


    // 아래코드는
    // enum Season { WINTER, SPRING, SUMMER, FALL }
    // 과 같은 구현이다.
    static final class Season1 extends MyEnum<Season1> {
        private Season1(String name, int ordinal) {
            super(name, ordinal);
        }

        public static final Season1 WINTER = new Season1("WINTER", 0);
        public static final Season1 SPRING = new Season1("SPRING", 1);
        public static final Season1 SUMMER = new Season1("SUMMER", 2);
        public static final Season1 FALL = new Season1("FALL", 3);
        private static final Season1[] VALUES = {WINTER, SPRING, SUMMER, FALL};

        public static Season1[] values() {
            return VALUES.clone();
        }

        public static Season1 valueOf(String name) {
            for (Season1 e : VALUES)
                if (e.name().equals(name)) return e;
            throw new IllegalArgumentException();
        }
    }

    // E extends Enum<E>
    // Season extends Enum<Season>
    // Enum<E> implements Comparable<E>
    // Enum<Season> implements Comparable<Season>


    // 3.6 Multiple Bounds
    // 이때까지는 바운드가 1개만있었다.
    // 여러개 걸리는 경우에 대해서 알아보자.

    // 다시 찾아온 copy함수, 이번에 파일 복사다.
    // 역시나 java api는 넘나 깔끔하지 못한것!
    // file하나 copy하는데 넘나 많은 코드들이 필요로 하.



    public static <S extends Readable & Closeable, T extends Appendable & Closeable>
    void copy(S src, T trg, int size) throws IOException
    {
        try {
            CharBuffer buf = CharBuffer.allocate(size); int i = src.read(buf);
            while (i >= 0) {
                buf.flip(); // prepare buffer for writing
                // Readarble
                trg.append(buf);
                buf.clear(); // prepare buffer for reading
                i = src.read(buf); }
        } finally {
            // Closeable
            src.close();
            trg.close();
        } }


}
