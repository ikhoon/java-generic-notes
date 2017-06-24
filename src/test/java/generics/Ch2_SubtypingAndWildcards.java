package generics;

import org.junit.Test;

import java.util.*;

public class Ch2_SubtypingAndWildcards {


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

    /*
    // Quiz : get, put을 함께 할때?
    public static double sumCount(Collection<? extends Number> nums, int n) {
        count(nums, n);
        return sum(nums);
    }

    public static double sumCount1(Collection<? super Number> nums, int n) {
        count(nums, n);
        return sum(nums);
    }
    */



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

    // variant



    // 2.5 Arrays
    // array 는 covariant이다
    // Inteter[] a = Number[] 가 대입 가능함.

    // 지난번에 이야기 했던 covariant, contravariant, invariant 에 다시 자세히 알아보자
    // covariant?
    // contravariant?
    // invariant?
    public static void sort0(Object[] a) {
    }

    public static <T> void sort1(T[] a) {
    }

    // 2.6 Wildcards Versus Type Parameters
    interface Collection2<Int> {
        public boolean contains(Object o);
        public boolean containsAll(Collection2<? extends Int> c);
    }

    //  Collection<?> 은 Collection<? extends Object> 와 같은 표현이다.

    @Test
    public void objectContains() {
        Object obj = "one";
        List<Object> objs = Arrays.<Object>asList("one", 2, 3.14, 4);
        List<Integer> ints = Arrays.asList(2, 4);
        assert objs.contains(obj);
        assert objs.containsAll(ints);
        // 이거랑
        assert !ints.contains(obj);
        // 이거는 멍청한 코드처럼 보인다
        assert !ints.containsAll(objs);
        // 왜냐면 List<Integer>는 "one" 문자열과 같은 객체를 가지고 있을수 없다.

    }

    @Test
    public void integerContains() {
        Object obj = 1;
        List<Object> objs = Arrays.<Object>asList(1, 3);
        List<Integer> ints = Arrays.asList(1, 2, 3, 4);
        // 그러지만 obj 가 integer라면 상황은 달라진다.
        assert ints.contains(obj);
        // 그러지만 objs 안의 모든 구성요소가 integer라면 이또한 마찬가지다.
        assert ints.containsAll(objs);
    }

    // 다른 디자인을 고려해보자.
    interface MyCollection<E> {
        boolean contains(E o); // 제약이 있다.
        boolean containsAll(MyCollection<? extends E> c); // 여기도 마찬가지이다.
    }

    static class MyList<E> implements MyCollection<E> {


        List<E> xs;

        public MyList() {
            this.xs = new ArrayList<>();
        }
        public MyList(List<E> xs) {
            this.xs = xs;
        }

        @Override
        public boolean contains(E o) {
            return false;
        }

        @Override
        public boolean containsAll(MyCollection<? extends E> c) {
            return false;
        }

        public static <T> MyList asList(T... ts) {
            return new MyList<T>(Arrays.asList(ts));
        }
    }

    @Test
    public void myCollectionContains() {
        Object obj = "one";
        MyList<Object> objs = MyList.<Object>asList("one", 2, 3.14, 4);
        MyList<Integer> ints = MyList.asList(2, 4);
        assert objs.contains(obj);
        assert objs.containsAll(ints);
//        assert !ints.contains(obj); // compile-time error assert
//        assert !ints.containsAll(objs); // compile-time error
    }


    // 2.7 Wildcard Capture
    // 이것은 뭐당가?
    // 별거아니다.
    // generic 함수가 호출될때 wildcard 타입(알수 없는 타입)에 의해 알수 없는
    // type parameter 가 추론되는거다.
    // <?> 에 어떤 타입이 들어가는지 결정되는거? 그런거?


    // 아래 두개의 함수는 같다
    // 그런데 wildcard를 쓴 reverse1은 간결하고 명확하다.
    // 조금이라도 더 적은게 client 입장에서 API 사용하기 편하다.
    // 반면 reverse2는 구현하기 더 간편하다.
    public static void reverse1(List<?> list) {}
    public static <T> void reverse2(List<T> list) {}

    // Type parameter와 함께 reverse 구현하기
    public static <T> void reverse2_(List<T> list) {
        List<T> tmp = new ArrayList<>(list);
        for (int i = 0; i < list.size(); i++) {
            list.set(i, tmp.get(list.size()-i-1));
        }
    }

    // wildcard와 함께 reverse 구현하기
    // 잘안된다.
    // 그이유는 Object를 알수없는 타입에 넣을려고 한다.
    // 하위 타입에 상위 타입을 대입할수는 없다.
    public static void reverse1_(List<?> list) {
        List<Object> tmp = new ArrayList<Object>(list);
        for (int i = 0; i < list.size(); i++) {
//            list.set(i, tmp.get(list.size()-i-1)); // compile-time error
        }
    }

    // 그러면 List<Object>를 List<?> 로 바꾸면 어떨까?
    // 역시나 안된다, 이유는 서로 모르는 타입에 대해서 대입할라 해서 그렇다.
    public static void reverse3_(List<?> list) {
        List<?> tmp = new ArrayList<>(list);
        for (int i = 0; i < list.size(); i++) {
//            list.set(i, tmp.get(list.size()-i-1)); // compile-time error
        }
    }


    // 이럴때 쓰는 방법은 wildcard 표현은 public으로 쓰고
    // type parameter는 private으로 쓰는것이다.
    // 이것은 wildcard를 사용하는 유요한 기법이다!
    public static void reverse(List<?> list) { rev(list); }
    private static <T> void rev(List<T> list) {
        List<T> tmp = new ArrayList<T>(list);
        for (int i = 0; i < list.size(); i++) {
            list.set(i, tmp.get(list.size()-i-1));
        }
    }
    // scala 에서도 type parameter를 사용하는 이와 비슷한 기법들이 있다.

    // 그리고 에러메시지에 익숙해지자
    // capture<?> 이상해 보이긴 한다.
    // wildcard type 으로 부터 기인된거다.
    // wildcard type에 bound 가 있으면
    // capture<? extends Number> 이런 식으로 표현된다.


    // 2.8 Restrictions on Wildcards
    // 이것도 재미있는 주제이다.
    // wildcard는 top level class의 생성 표현에서 사용할수 없다
    // 개체를 생성할때는 타입이 명확해야한다.
    public void newWithWildCard() {
//        List<?> list = new ArrayList<?>(); // compile-time error
//        Map<String, ? extends Number> map = new HashMap<String, ? extends Number>(); // compile-time error
        List<?> list1 = new ArrayList<Integer>(); // 이런건 된다.
    }

    // wildcard는 상위타입이나, type parameter가 명확한 generic 함수에서만 사용이 가능하다.


    // 생성할때는 정확한 타입을 사용하고
    // put할때나 get할때는 wildcard 타입을 활용한 자료구조형을 이용할수 있다.
    public void sourceAndSink() {
        List<Number> nums = new ArrayList<Number>();
        List<? super Number> sink = nums;
        List<? extends Number> source = nums;
        for (int i=0; i<10; i++) sink.get(i);
        for (int i=0; i<10; i++) sink.add(i);
        double sum=0; for (Number num : source) sum+=num.doubleValue();
    }

    // 그리고 또하나 흥미로운점은 wildcard는 top level의 생성자에는 사용이 안되지만
    // nested 타입에는 사용이 가능하다.

    // 아래 코드는 내부 list는 integer의 list이거나 string의 list이다
    // wildcard는 inner list에서 꺼낸값이 오직 object이도록 한정하고 있다.

    public void nestedWildCard() {
        List<List<?>> lists = new ArrayList<List<?>>();
        lists.add(Arrays.asList(1,2,3));
        lists.add(Arrays.asList("four","five"));
        Object o = lists.get(0).get(0);
        assert lists.toString().equals("[[1, 2, 3], [four, five]]");
    }

    // 공식!!!
    // wildcard와 일반적인 타입과의 관계는 interface와 class의 관계와 유사하다.
    // 즉 wildcard는 interface와 가깝고 일반적인(general)한 특성을 가지고 있다.
    // 이에 반해 일반타입과 class는 더욱 구체적이다.
    // 인스턴의 생성은 보다 정확안 정보가 필요로 한다.
    public void restictionRule() {
        List<?> list = new ArrayList<Object>(); // ok
        // interface이다, 명확하지 않다.
//        List<?> list = new List<Object>(); // compile-time error
        // ? wildcard이다 명확하지 않다. 여긴 객체 만드는 곳이다.
//        List<?> list = new ArrayList<?>(); // compile-time error
    }

    static class MyLists {
        public static <T> List<T> factory() { return new ArrayList<T>(); }
    }

    // generic 함수의 호출이 명확한 type paramter을 포함하고 있으면 type parameter는 wildcard가 아니여야한다.
    // Abc.<Type>.call() 이으로 명확히 호출시에
    public void genericMethod() {

        // 아래 두코드는 유효하다.
        List<?> list1 = MyLists.factory();
        List<?> list2 = MyLists.<Object>factory();

        // 타입 파라메터는 함수에 표시하면 wildcard는 안된다.
//        List<?> list3 = MyLists.<?>factory(); // compile-time error

        // 앞에서 언급했듯이 nested wildcard는 괜찮다.
        List<List<?>> list4 = MyLists.<List<?>>factory(); // ok

    }

    // 이번엔 상속관계에서 super type에 어떻게 처리하는지 알아보자
    // 클래스 선언할때 상위 타입이 상위 interface가 type parameter를 가지고 있으면
    // 그 타입은 wildcard가 되어서는 안된다.

    // 허용안됨
//    class AnyList extends ArrayList<?> {}
    // 역시나 허용이 안됨
//    abstract class AnotherList implements List<?> {} // compile-time error
    // 하지만 nested wildcard는 됨
    class NestedList extends ArrayList<List<?>> {}// ok

    // 이걸 동작하게 하려면 type parameter를 바로 정해주거나
    class IntList extends ArrayList<Integer> {}
    // 혹은 이걸 passing 해줘야한다.
    class SomeList<T> extends ArrayList<T> {}
}


