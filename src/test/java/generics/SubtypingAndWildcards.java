package generics;

import org.junit.Test;

import java.util.*;

public class SubtypingAndWildcards {

    // 2.1 Subtyping and the Substitution Principle
    // 우리는 얼마나 알고 있나?
    // * Integer는 Number의 하위 타입이다.
    // * List<E>는 Collection<E>의 하위 타입이다.
    // * List<Integer> 는 List<Number>의 하위 타입이다.





    // 팩트 체크
    @Test
    public void listIntIsSubTypeOfListNumber() {
        List<Integer> listOfInt = new ArrayList<>();
        listOfInt.add(10);
        listOfInt.add(20);
//        List<Number> listOfNumber = listOfInt; // Quiz : 될까 안될까?



        // 이게 된다면
//        listOfNumber.add(3.12);   // number에 double을 넣는건 자연스럽다.

    }

    // 그렇다면 배열은 어떨까?
    @Test
    public void arrayIntIsSubTypeOfArrayNumber() {
        Integer[] arrayOfInt = new Integer[] {10, 20};
        Number[] arrayOfNumber = arrayOfInt;
        arrayOfNumber[1] = 3.12;
    }


    // 2.2 Wildcards with extends

    // collection API가 아래와 같을때
    interface Collection1<E> {
        boolean add(E e);
        boolean addAll(Collection1<? extends E> c);
    }

    // Quiz : ? extends E는 무슨 의미를 갖을까?
    // Quiz : 그럼 addAll(Collection<? extends E> c) 은 무슨 의미를 가질까?


    public void collectionAddAll() {
        List<Number> nums = new ArrayList<Number>();
        List<Integer> ints = Arrays.asList(1, 2);
        List<Double> dbls = Arrays.asList(2.78, 3.14);
        // 여기 addAll
        nums.addAll(ints);
        nums.addAll(dbls);
    }

    public void wildcardWhenDeclarValiable() {
        List<Integer> ints = new ArrayList<Integer>();
        ints.add(1);
        ints.add(2);
        // 변수 선언할때도 wildcard 표현식을 쓸수 있다.
        // List<Number> nums = ints;  // Quiz : 된다 안된다?
         List<? extends Number> nums = ints;  // Quiz : 된다 안된다?
        // nums.add(3.14); // Quiz : 된다 안된다?
        // nums.add(3); // Quiz : 된다 안된다?

//        Number a = nums.get(1); // Quiz : 된다 안된다?
//        Integer b = nums.get(1); // Quiz : 된다 안된다?

    }


    // 공식 알아두면 무덤까지 유효하다
    // ? extends E 이런 형태를 가지 자료 구조는
    // 값을 꺼낼수(get) 있다.
    // 하지만 값을 넣을수(put) 할수 없다.
    // 그렇기 때문에 List<? extends Number> nums



    // 2.3 Wildcards with super

    // collection copy하는 함수가 있다 해보자.
    // 이함수를 한번 이해해보자.
    // 하나는 왜 super이고 하나는 extends 인가?

    public static <T> void copy(List<? super T> dst, List<? extends T> src) {
        for (int i = 0; i < src.size(); i++) {
            // Quiz : 위에서는 넣을수 없다 했는데?
            dst.set(i, src.get(i));
        }
    }

    @Test
    public void collectionCopy() {
        List<Object> objs = Arrays.<Object>asList(2, 3.14, "four");
        List<Integer> ints = Arrays.asList(5, 6);
        this.copy(objs, ints);
        this.copy(objs, ints);
        // object의 하위타입은 다 받을수 있다.
        // object에 넣는거니까 말이 된다.
        this.<Object>copy(objs, ints);
        // number의 하위타입은 다 받을수 있다.
        // object에 넣는거니까 말이 된다.
        this.<Number>copy(objs, ints);
        // integer의 하위타입은 다 받을수 있다.
        // object에 넣는거니까 말이 된다.
        this.<Integer>copy(objs, ints);
    }


    // 그런데 왜 copy함수 하나 만드는데 이렇게 복잡하게 할까?
    // 아래처럼 하면 안되나?
    public static <T> void copy1(List<T> dst, List<T> src) {
        for (int i = 0; i < src.size(); i++) {
            dst.set(i, src.get(i));
        }
    }
    // Quiz : why?

    // 이건?
    public static <T> void copy2(List<T> dst, List<? extends T> src) {
        for (int i = 0; i < src.size(); i++) {
            dst.set(i, src.get(i));
        }
    }
    // 또 이건?
    public static <T> void copy3(List<? super T> dst, List<T> src) {
        for (int i = 0; i < src.size(); i++) {
            dst.set(i, src.get(i));
        }
    }

    // 이건 어떤가?
    public static <T> void copy4(List<? super T> dst, List<? extends T> src) {
        for (int i = 0; i < src.size(); i++) {
            dst.set(i, src.get(i));
        }
    }

    @Test
    public void collectionCopy1() {
        List<Object> objs = Arrays.<Object>asList(2, 3.14, "four");
        List<Integer> ints = Arrays.asList(5, 6);
        List<Integer> ints1 = Arrays.asList(5, 6);
        // 같은 타입이 아니면
        this.copy1(ints, ints1);

        // 아무것도 복사 되지 않는다.
        // object 타입에 integer가 대입이 안된다고? 헐랭
//        this.copy1(objs, ints);
//        this.<Object>copy1(objs, ints);
//        this.<Number>copy1(objs, ints);
//        this.<Integer>copy1(objs, ints);
    }

    @Test
    public void collectionCopy2() {
        List<Object> objs = Arrays.<Object>asList(2, 3.14, "four");
        List<Integer> ints = Arrays.asList(5, 6);
        List<Integer> ints1 = Arrays.asList(5, 6);
        this.copy2(ints, ints1);

        this.copy2(objs, ints);
        // object에 integer에 되입이 된다.
        this.<Object>copy2(objs, ints);
        // 되야 할것 같지만 안된다.
        // 이건 objects의 타입을 List<Number>로 강제하고 있다
//        this.<Number>copy2(objs, ints);
        // 이건 objects의 타입을 List<Integer>로 강제하고 있다
//        this.<Integer>copy2(objs, ints);
    }

    @Test
    public void collectionCopy3() {
        List<Object> objs = Arrays.<Object>asList(2, 3.14, "four");
        List<Integer> ints = Arrays.asList(5, 6);
        List<Integer> ints1 = Arrays.asList(5, 6);
        this.copy3(ints, ints1);

        this.copy3(objs, ints);
        // 이건 ints의 타입을 List<Object>로 강제하고 있다
        // 하지만 List<Integer>는 List<Object>의 하위 타입이 아니다.
//        this.<Object>copy3(objs, ints);
//        this.<Number>copy3(objs, ints);
        this.<Integer>copy3(objs, ints);
    }



    // 2.4 The Get and Put Principle
    // 역시나 공식 만한게 없다.
    // 공식을 외우자.


    // get은 extends
    public static double sum(Collection<? extends Number> nums) {
        double s = 0.0;
        for (Number num : nums) s += num.doubleValue();
        return s;
    }

    // put은 super
    public static void count(Collection<? super Integer> ints, int n) {
        for (int i = 0; i < n; i++) ints.add(i);
    }

    // Quiz : get, put을 함께 할때?
    public static double sumCount(Collection<? extends Number> nums, int n) {
        count(nums, n);
        return sum(nums);
    }

    public static double sumCount1(Collection<? super Number> nums, int n) {
        count(nums, n);
        return sum(nums);
    }



    // get 과 put을 함께 할때는 invariant
    public static double sumCount1(Collection<Number> nums, int n) {
        count(nums, n);
        return sum(nums);
    }



    public void collectionAdd() {
        // Substitution Principle 에 따라
        {
            List<Number> nums = new ArrayList<>();
            nums.add(2);
            nums.add(3.14); // 하위 타입은 대입이 된다.
            assert nums.toString().equals("[2, 3.14]");
        }

        {
            List<Number> nums = new ArrayList<>();
            nums.add(2.78);
            nums.add(3.14);
//            List<Integer> ints = nums; // compile-time error, 하위 타입으로 casting이 안된다.
//            assert ints.toString().equals("[2.78, 3.14]"); // uh oh!
        }


    }

}
